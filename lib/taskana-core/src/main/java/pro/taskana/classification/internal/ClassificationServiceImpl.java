package pro.taskana.classification.internal;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.ClassificationQuery;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.exceptions.MalformedServiceLevelException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.common.internal.util.ObjectAttributeChangeDetector;
import pro.taskana.spi.history.api.events.classification.ClassificationCreatedEvent;
import pro.taskana.spi.history.api.events.classification.ClassificationDeletedEvent;
import pro.taskana.spi.history.api.events.classification.ClassificationUpdatedEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.TaskMapper;

/** This is the implementation of ClassificationService. */
public class ClassificationServiceImpl implements ClassificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);
  private final HistoryEventManager historyEventManager;
  private final ClassificationMapper classificationMapper;
  private final TaskMapper taskMapper;
  private final InternalTaskanaEngine taskanaEngine;

  public ClassificationServiceImpl(
      InternalTaskanaEngine taskanaEngine,
      ClassificationMapper classificationMapper,
      TaskMapper taskMapper) {
    this.taskanaEngine = taskanaEngine;
    this.classificationMapper = classificationMapper;
    this.taskMapper = taskMapper;
    this.historyEventManager = taskanaEngine.getHistoryEventManager();
  }

  @Override
  public Classification getClassification(String key, String domain)
      throws ClassificationNotFoundException {
    if (key == null) {
      throw new ClassificationNotFoundException(null, domain);
    }

    Classification result;
    try {
      taskanaEngine.openConnection();
      result = classificationMapper.findByKeyAndDomain(key, domain);
      if (result == null) {
        result = classificationMapper.findByKeyAndDomain(key, "");
        if (result == null) {
          throw new ClassificationNotFoundException(key, domain);
        }
      }
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Classification getClassification(String id) throws ClassificationNotFoundException {
    if (id == null) {
      throw new ClassificationNotFoundException(null);
    }
    Classification result;
    try {
      taskanaEngine.openConnection();
      result = classificationMapper.findById(id);
      if (result == null) {
        throw new ClassificationNotFoundException(id);
      }
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void deleteClassification(String classificationId)
      throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      Classification classification = this.classificationMapper.findById(classificationId);
      if (classification == null) {
        throw new ClassificationNotFoundException(classificationId);
      }

      if (classification.getDomain().equals("")) {
        // master mode - delete all associated classifications in every domain.
        List<ClassificationSummary> classificationsInDomain =
            createClassificationQuery().keyIn(classification.getKey()).list();
        for (ClassificationSummary classificationInDomain : classificationsInDomain) {
          if (!"".equals(classificationInDomain.getDomain())) {
            deleteClassification(classificationInDomain.getId());
          }
        }
      }

      List<ClassificationSummary> childClassifications =
          createClassificationQuery().parentIdIn(classificationId).list();
      for (ClassificationSummary child : childClassifications) {
        this.deleteClassification(child.getId());
      }

      try {
        this.classificationMapper.deleteClassification(classificationId);

        if (HistoryEventManager.isHistoryEnabled()) {
          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(
                  classification, newClassification("", "", ""));

          historyEventManager.createEvent(
              new ClassificationDeletedEvent(
                  IdGenerator.generateWithPrefix(
                      IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT),
                  classification,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }

      } catch (PersistenceException e) {
        if (isReferentialIntegrityConstraintViolation(e)) {
          throw new ClassificationInUseException(classification, e);
        }
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void deleteClassification(String classificationKey, String domain)
      throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      Classification classification =
          this.classificationMapper.findByKeyAndDomain(classificationKey, domain);
      if (classification == null) {
        throw new ClassificationNotFoundException(classificationKey, domain);
      }
      deleteClassification(classification.getId());
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Classification createClassification(Classification classification)
      throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
          InvalidArgumentException, MalformedServiceLevelException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    if (!taskanaEngine.domainExists(classification.getDomain())
        && !"".equals(classification.getDomain())) {
      throw new DomainNotFoundException(classification.getDomain());
    }
    ClassificationImpl classificationImpl;
    final boolean isClassificationExisting;
    try {
      taskanaEngine.openConnection();
      isClassificationExisting =
          doesClassificationExist(classification.getKey(), classification.getDomain());
      if (isClassificationExisting) {
        throw new ClassificationAlreadyExistException(classification);
      }
      classificationImpl = (ClassificationImpl) classification;
      this.checkClassificationId(classificationImpl);
      classificationImpl.setCreated(Instant.now());
      classificationImpl.setModified(classificationImpl.getCreated());
      this.initDefaultClassificationValues(classificationImpl);

      validateAndPopulateParentInformation(classificationImpl);

      classificationMapper.insert(classificationImpl);

      if (HistoryEventManager.isHistoryEnabled()) {
        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                newClassification("", "", ""), classificationImpl);

        historyEventManager.createEvent(
            new ClassificationCreatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT),
                classificationImpl,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Method createClassification created classification {}.", classificationImpl);
      }

      if (!classification.getDomain().isEmpty()) {
        addClassificationToMasterDomain(classificationImpl);
      }
    } finally {
      taskanaEngine.returnConnection();
    }
    return classificationImpl;
  }

  @Override
  public Classification updateClassification(Classification classification)
      throws NotAuthorizedException, ConcurrencyException, ClassificationNotFoundException,
          InvalidArgumentException, MalformedServiceLevelException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    ClassificationImpl classificationImpl;
    try {
      taskanaEngine.openConnection();
      if (classification.getKey().equals(classification.getParentKey())) {
        throw new InvalidArgumentException(
            String.format(
                "The Classification '%s' has the same key and parent key",
                classification.getName()));
      }

      classificationImpl = (ClassificationImpl) classification;
      Classification oldClassification =
          this.getExistingClassificationAndVerifyTimestampHasNotChanged(classificationImpl);
      classificationImpl.setModified(Instant.now());
      this.initDefaultClassificationValues(classificationImpl);

      if (!Objects.equals(oldClassification.getCategory(), classificationImpl.getCategory())) {
        this.updateCategoryOnAssociatedTasks(classificationImpl, oldClassification);
      }

      this.checkExistenceOfParentClassification(oldClassification, classificationImpl);
      classificationMapper.update(classificationImpl);
      this.createJobIfPriorityOrServiceLevelHasChanged(oldClassification, classificationImpl);

      if (HistoryEventManager.isHistoryEnabled()) {
        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                oldClassification, classificationImpl);

        historyEventManager.createEvent(
            new ClassificationUpdatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT),
                classificationImpl,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Method updateClassification() updated the classification {}.", classificationImpl);
      }
      return classification;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public ClassificationQuery createClassificationQuery() {
    return new ClassificationQueryImpl(taskanaEngine);
  }

  @Override
  public Classification newClassification(String key, String domain, String type) {
    ClassificationImpl classification = new ClassificationImpl();
    classification.setKey(key);
    classification.setDomain(domain);
    classification.setType(type);
    return classification;
  }

  private static void validateServiceLevel(Classification classification)
      throws MalformedServiceLevelException {
    String serviceLevel = classification.getServiceLevel();
    Duration duration;

    try {
      duration = Duration.parse(serviceLevel);
    } catch (Exception e) {
      throw new MalformedServiceLevelException(
          serviceLevel, classification.getKey(), classification.getDomain());
    }

    if (duration.isNegative()) {
      throw new MalformedServiceLevelException(
          serviceLevel, classification.getKey(), classification.getDomain());
    }
    // check that the duration is based on format PnD, i.e. it must start with a P, end with a D
    if (!serviceLevel.toLowerCase().startsWith("p") || !serviceLevel.toLowerCase().endsWith("d")) {
      throw new MalformedServiceLevelException(
          serviceLevel, classification.getKey(), classification.getDomain());
    }
  }

  private void validateAndPopulateParentInformation(ClassificationImpl classificationImpl)
      throws InvalidArgumentException {
    try {

      if (classificationImpl.getParentId() != null && !classificationImpl.getParentId().isEmpty()) {
        Classification parentClassification =
            this.getClassification(classificationImpl.getParentId());
        if (classificationImpl.getParentKey() != null
            && !classificationImpl.getParentKey().isEmpty()) {
          if (!classificationImpl.getParentKey().equals(parentClassification.getKey())) {
            throw new InvalidArgumentException(
                "Given parent key of classification does not match key of parent id.");
          }
          classificationImpl.setParentKey(parentClassification.getKey());
        }
      }

      if (classificationImpl.getParentKey() != null
          && !classificationImpl.getParentKey().isEmpty()) {
        Classification parentClassification =
            this.getClassification(
                classificationImpl.getParentKey(), classificationImpl.getDomain());
        classificationImpl.setParentId(parentClassification.getId());
      }

    } catch (ClassificationNotFoundException e) {
      throw new InvalidArgumentException("Parent classification could not be found.", e);
    }
  }

  private void checkClassificationId(ClassificationImpl classificationImpl)
      throws InvalidArgumentException {
    if (classificationImpl.getId() != null && !"".equals(classificationImpl.getId())) {
      throw new InvalidArgumentException("ClassificationId should be null on creation");
    }
  }

  private void addClassificationToMasterDomain(ClassificationImpl classification) {
    if (!Objects.equals(classification.getDomain(), "")) {
      boolean doesExist = true;
      ClassificationImpl masterClassification = classification.copy(classification.getKey());
      masterClassification.setId(
          IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION));
      masterClassification.setParentKey(classification.getParentKey());
      masterClassification.setDomain("");
      masterClassification.setIsValidInDomain(false);
      try {
        if (classification.getParentKey() != null && !"".equals(classification.getParentKey())) {
          masterClassification.setParentId(
              getClassification(classification.getParentKey(), "").getId());
        }
        this.getClassification(masterClassification.getKey(), masterClassification.getDomain());
        throw new ClassificationAlreadyExistException(masterClassification);
      } catch (ClassificationNotFoundException e) {
        doesExist = false;
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "Method createClassification: Classification does not "
                  + "exist in master domain. Classification {}.",
              masterClassification);
        }
      } catch (ClassificationAlreadyExistException ex) {
        LOGGER.warn(
            "Method createClassification: Classification does already exist "
                + "in master domain. Classification {}.",
            LogSanitizer.stripLineBreakingChars(masterClassification));
      } finally {
        if (!doesExist) {
          classificationMapper.insert(masterClassification);
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "Method createClassification: Classification created in "
                    + "master-domain, too. Classification {}.",
                masterClassification);
          }
        }
      }
    }
  }

  /**
   * Fill missing values and validate classification before saving the classification.
   *
   * @param classification the classification which will be verified.
   * @throws InvalidArgumentException if the given classification has no key, the type is not valid
   *     or the category for the provided type is invalid.
   * @throws MalformedServiceLevelException if the given service level of the {@linkplain
   *     Classification} is invalid
   */
  private void initDefaultClassificationValues(ClassificationImpl classification)
      throws InvalidArgumentException, MalformedServiceLevelException {
    Instant now = Instant.now();
    if (classification.getId() == null || "".equals(classification.getId())) {
      classification.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION));
    }

    if (classification.getCreated() == null) {
      classification.setCreated(now);
    }

    if (classification.getModified() == null) {
      classification.setModified(now);
    }

    if (classification.getIsValidInDomain() == null) {
      classification.setIsValidInDomain(true);
    }

    if (classification.getServiceLevel() == null || classification.getServiceLevel().isEmpty()) {
      classification.setServiceLevel("P0D");
    } else {
      validateServiceLevel(classification);
    }

    if (classification.getKey() == null) {
      throw new InvalidArgumentException("Classification must contain a key");
    }

    if (classification.getParentId() == null) {
      classification.setParentId("");
    }

    if (classification.getParentKey() == null) {
      classification.setParentKey("");
    }

    if (classification.getType() != null
        && !taskanaEngine
            .getEngine()
            .getConfiguration()
            .getClassificationTypes()
            .contains(classification.getType())) {
      throw new InvalidArgumentException(
          "Given classification type "
              + classification.getType()
              + " is not valid according to the configuration.");
    }

    if (classification.getCategory() != null
        && !taskanaEngine
            .getEngine()
            .getConfiguration()
            .getClassificationCategoriesByType(classification.getType())
            .contains(classification.getCategory())) {
      throw new InvalidArgumentException(
          "Given classification category "
              + classification.getCategory()
              + " with type "
              + classification.getType()
              + " is not valid according to the configuration.");
    }

    if (classification.getDomain().isEmpty()) {
      classification.setIsValidInDomain(false);
    }
  }

  private boolean doesClassificationExist(String key, String domain) {
    boolean isExisting = false;
    try {
      if (classificationMapper.findByKeyAndDomain(key, domain) != null) {
        isExisting = true;
      }
    } catch (Exception ex) {
      LOGGER.warn(
          "Classification-Service threw Exception while calling "
              + "mapper and searching for classification. EX={}",
          ex,
          ex);
    }
    return isExisting;
  }

  private boolean isReferentialIntegrityConstraintViolation(PersistenceException e) {
    return isH2OrPostgresIntegrityConstraintViolation(e) || isDb2IntegrityConstraintViolation(e);
  }

  private boolean isDb2IntegrityConstraintViolation(PersistenceException e) {
    return e.getCause() instanceof SQLIntegrityConstraintViolationException
        && e.getMessage().contains("-532");
  }

  private boolean isH2OrPostgresIntegrityConstraintViolation(PersistenceException e) {
    return e.getCause() instanceof SQLException
        && ((SQLException) e.getCause()).getSQLState().equals("23503");
  }

  /**
   * Check if current object is based on the newest (by modified).
   *
   * @param classificationImpl the classification
   * @return the old classification
   * @throws ConcurrencyException if the classification has been modified by some other process.
   * @throws ClassificationNotFoundException if the given classification does not exist.
   */
  private Classification getExistingClassificationAndVerifyTimestampHasNotChanged(
      ClassificationImpl classificationImpl)
      throws ConcurrencyException, ClassificationNotFoundException {
    Classification oldClassification =
        this.getClassification(classificationImpl.getKey(), classificationImpl.getDomain());
    if (!oldClassification.getModified().equals(classificationImpl.getModified())) {
      throw new ConcurrencyException();
    }
    return oldClassification;
  }

  /**
   * Update classification fields used by tasks.
   *
   * @param classificationImpl the new classification
   * @param oldClassification the old classification
   */
  private void updateCategoryOnAssociatedTasks(
      ClassificationImpl classificationImpl, Classification oldClassification) {
    List<TaskSummary> taskSummaries =
        taskanaEngine
            .getEngine()
            .getTaskService()
            .createTaskQuery()
            .classificationIdIn(oldClassification.getId())
            .list();

    if (!taskSummaries.isEmpty()) {
      List<String> taskIds = new ArrayList<>();
      taskSummaries.forEach(ts -> taskIds.add(ts.getId()));
      taskMapper.updateClassificationCategoryOnChange(taskIds, classificationImpl.getCategory());
    }
  }

  /**
   * Check if parentId or parentKey were changed and if the classification exist.
   *
   * @param classificationImpl the new classification
   * @param oldClassification the old classification
   * @throws ClassificationNotFoundException if the given classification does not exist.
   */
  private void checkExistenceOfParentClassification(
      Classification oldClassification, ClassificationImpl classificationImpl)
      throws ClassificationNotFoundException {
    if (!oldClassification.getParentId().equals(classificationImpl.getParentId())
        && classificationImpl.getParentId() != null
        && !classificationImpl.getParentId().isEmpty()) {
      this.getClassification(classificationImpl.getParentId());
    }

    if (!oldClassification.getParentKey().equals(classificationImpl.getParentKey())
        && classificationImpl.getParentKey() != null
        && !classificationImpl.getParentKey().isEmpty()) {
      this.getClassification(classificationImpl.getParentKey(), classificationImpl.getDomain());
    }
  }

  /**
   * Check if priority or service level were changed and create the new job.
   *
   * @param classificationImpl the new classification
   * @param oldClassification the old classification
   */
  private void createJobIfPriorityOrServiceLevelHasChanged(
      Classification oldClassification, ClassificationImpl classificationImpl) {
    boolean priorityChanged = oldClassification.getPriority() != classificationImpl.getPriority();
    boolean serviceLevelChanged =
        !Objects.equals(oldClassification.getServiceLevel(), classificationImpl.getServiceLevel());

    if (priorityChanged || serviceLevelChanged) {
      Map<String, String> args = new HashMap<>();
      args.put(ClassificationChangedJob.CLASSIFICATION_ID, classificationImpl.getId());
      args.put(ClassificationChangedJob.PRIORITY_CHANGED, String.valueOf(priorityChanged));
      args.put(ClassificationChangedJob.SERVICE_LEVEL_CHANGED, String.valueOf(serviceLevelChanged));
      ScheduledJob job = new ScheduledJob();
      job.setArguments(args);
      job.setType(ScheduledJob.Type.CLASSIFICATIONCHANGEDJOB);
      taskanaEngine.getEngine().getJobService().createJob(job);
    }
  }
}
