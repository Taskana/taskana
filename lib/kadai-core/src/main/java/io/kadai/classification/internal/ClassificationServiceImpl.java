package io.kadai.classification.internal;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;

import io.kadai.classification.api.ClassificationQuery;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.exceptions.ClassificationAlreadyExistException;
import io.kadai.classification.api.exceptions.ClassificationInUseException;
import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.classification.api.exceptions.MalformedServiceLevelException;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.internal.jobs.ClassificationChangedJob;
import io.kadai.classification.internal.models.ClassificationImpl;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.common.internal.util.LogSanitizer;
import io.kadai.common.internal.util.ObjectAttributeChangeDetector;
import io.kadai.spi.history.api.events.classification.ClassificationCreatedEvent;
import io.kadai.spi.history.api.events.classification.ClassificationDeletedEvent;
import io.kadai.spi.history.api.events.classification.ClassificationUpdatedEvent;
import io.kadai.spi.history.internal.HistoryEventManager;
import io.kadai.spi.priority.internal.PriorityServiceManager;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.TaskMapper;
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

/** This is the implementation of ClassificationService. */
public class ClassificationServiceImpl implements ClassificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);
  private final HistoryEventManager historyEventManager;
  private final PriorityServiceManager priorityServiceManager;
  private final ClassificationMapper classificationMapper;
  private final TaskMapper taskMapper;
  private final InternalKadaiEngine kadaiEngine;

  public ClassificationServiceImpl(
      InternalKadaiEngine kadaiEngine,
      PriorityServiceManager priorityServiceManager,
      ClassificationMapper classificationMapper,
      TaskMapper taskMapper) {
    this.kadaiEngine = kadaiEngine;
    this.priorityServiceManager = priorityServiceManager;
    this.classificationMapper = classificationMapper;
    this.taskMapper = taskMapper;
    this.historyEventManager = kadaiEngine.getHistoryEventManager();
  }

  @Override
  public Classification getClassification(String key, String domain)
      throws ClassificationNotFoundException {
    if (key == null) {
      throw new ClassificationNotFoundException(null, domain);
    }

    Classification result;
    try {
      kadaiEngine.openConnection();
      result = classificationMapper.findByKeyAndDomain(key, domain);
      if (result == null) {
        result = classificationMapper.findByKeyAndDomain(key, MASTER_DOMAIN);
        if (result == null) {
          throw new ClassificationNotFoundException(key, domain);
        }
      }
      return result;
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public Classification getClassification(String id) throws ClassificationNotFoundException {
    if (id == null) {
      throw new ClassificationNotFoundException(null);
    }
    Classification result;
    try {
      kadaiEngine.openConnection();
      result = classificationMapper.findById(id);
      if (result == null) {
        throw new ClassificationNotFoundException(id);
      }
      return result;
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public void deleteClassification(String classificationId)
      throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException {
    kadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    try {
      kadaiEngine.openConnection();
      Classification classification = this.classificationMapper.findById(classificationId);
      if (classification == null) {
        throw new ClassificationNotFoundException(classificationId);
      }

      if (classification.getDomain().equals(MASTER_DOMAIN)) {
        // master mode - delete all associated classifications in every domain.
        List<ClassificationSummary> classificationsInDomain =
            createClassificationQuery().keyIn(classification.getKey()).list();
        for (ClassificationSummary classificationInDomain : classificationsInDomain) {
          if (!MASTER_DOMAIN.equals(classificationInDomain.getDomain())) {
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

        if (historyEventManager.isEnabled()) {
          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(
                  classification, newClassification("", MASTER_DOMAIN, ""));

          historyEventManager.createEvent(
              new ClassificationDeletedEvent(
                  IdGenerator.generateWithPrefix(
                      IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT),
                  classification,
                  kadaiEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }

      } catch (PersistenceException e) {
        if (isReferentialIntegrityConstraintViolation(e)) {
          throw new ClassificationInUseException(classification, e);
        }
        throw e;
      }
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public void deleteClassification(String classificationKey, String domain)
      throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException {
    kadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    try {
      kadaiEngine.openConnection();
      Classification classification =
          this.classificationMapper.findByKeyAndDomain(classificationKey, domain);
      if (classification == null) {
        throw new ClassificationNotFoundException(classificationKey, domain);
      }
      deleteClassification(classification.getId());
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public Classification createClassification(Classification classification)
      throws ClassificationAlreadyExistException,
          DomainNotFoundException,
          InvalidArgumentException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    kadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    if (!kadaiEngine.domainExists(classification.getDomain())
        && !MASTER_DOMAIN.equals(classification.getDomain())) {
      throw new DomainNotFoundException(classification.getDomain());
    }
    ClassificationImpl classificationImpl;
    final boolean isClassificationExisting;
    try {
      kadaiEngine.openConnection();
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

      if (historyEventManager.isEnabled()) {
        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                newClassification("", MASTER_DOMAIN, ""), classificationImpl);

        historyEventManager.createEvent(
            new ClassificationCreatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT),
                classificationImpl,
                kadaiEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Method createClassification created classification {}.",
            LogSanitizer.stripLineBreakingChars(classificationImpl));
      }

      if (!classification.getDomain().isEmpty()) {
        addClassificationToMasterDomain(classificationImpl);
      }
    } finally {
      kadaiEngine.returnConnection();
    }
    return classificationImpl;
  }

  @Override
  public Classification updateClassification(Classification classification)
      throws ConcurrencyException,
          ClassificationNotFoundException,
          InvalidArgumentException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    kadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    ClassificationImpl classificationImpl;
    try {
      kadaiEngine.openConnection();
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

      if (!priorityServiceManager.isEnabled()) {
        this.createJobIfPriorityOrServiceLevelHasChanged(oldClassification, classificationImpl);
      }

      if (historyEventManager.isEnabled()) {
        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                oldClassification, classificationImpl);

        historyEventManager.createEvent(
            new ClassificationUpdatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT),
                classificationImpl,
                kadaiEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Method updateClassification() updated the classification {}.",
            LogSanitizer.stripLineBreakingChars(classificationImpl));
      }
      return classification;
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public ClassificationQuery createClassificationQuery() {
    return new ClassificationQueryImpl(kadaiEngine);
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
    if (classificationImpl.getId() != null && !classificationImpl.getId().isEmpty()) {
      throw new InvalidArgumentException("ClassificationId should be null on creation");
    }
  }

  private void addClassificationToMasterDomain(ClassificationImpl classification) {
    if (!Objects.equals(classification.getDomain(), MASTER_DOMAIN)) {
      ClassificationImpl masterClassification = classification.copy(classification.getKey());
      masterClassification.setId(
          IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION));
      masterClassification.setParentKey(classification.getParentKey());
      masterClassification.setDomain(MASTER_DOMAIN);
      masterClassification.setIsValidInDomain(false);
      try {
        if (classification.getParentKey() != null && !classification.getParentKey().isEmpty()) {
          masterClassification.setParentId(
              getClassification(classification.getParentKey(), MASTER_DOMAIN).getId());
        }
        this.getClassification(masterClassification.getKey(), masterClassification.getDomain());
        throw new ClassificationAlreadyExistException(masterClassification);
      } catch (ClassificationNotFoundException e) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "Method createClassification: Classification does not "
                  + "exist in master domain. Classification {}.",
              masterClassification);
        }
        classificationMapper.insert(masterClassification);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "Method createClassification: Classification created in "
                  + "master-domain, too. Classification {}.",
              masterClassification);
        }
      } catch (ClassificationAlreadyExistException ex) {
        LOGGER.warn(
            "Method createClassification: Classification does already exist "
                + "in master domain. Classification {}.",
            LogSanitizer.stripLineBreakingChars(masterClassification));
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
    if (classification.getId() == null || classification.getId().isEmpty()) {
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
        && !kadaiEngine
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
        && !kadaiEngine
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
    return isH2OrPostgresIntegrityConstraintViolation(e)
        || isDb2IntegrityConstraintViolation(e)
        || isOracleIntegrityConstraintViolation(e);
  }

  private boolean isDb2IntegrityConstraintViolation(PersistenceException e) {
    return e.getCause() instanceof SQLIntegrityConstraintViolationException
        && e.getMessage().contains("-532");
  }

  private boolean isOracleIntegrityConstraintViolation(PersistenceException e) {
    return e.getCause() instanceof SQLIntegrityConstraintViolationException;
  }

  private boolean isH2OrPostgresIntegrityConstraintViolation(PersistenceException e) {
    return e.getCause() instanceof SQLException sqlException
        && sqlException.getSQLState().equals("23503");
  }

  /**
   * Check if current object is based on the newest (by modified).
   *
   * @param classificationImpl the classification
   * @return the old classification
   * @throws ConcurrencyException if the classification has been modified by some other process;
   *     that's the case if the given modified timestamp differs from the one in the database
   * @throws ClassificationNotFoundException if the given classification does not exist
   */
  private Classification getExistingClassificationAndVerifyTimestampHasNotChanged(
      ClassificationImpl classificationImpl)
      throws ConcurrencyException, ClassificationNotFoundException {
    Classification oldClassification =
        this.getClassification(classificationImpl.getKey(), classificationImpl.getDomain());
    if (!oldClassification.getModified().equals(classificationImpl.getModified())) {
      throw new ConcurrencyException(classificationImpl.getId());
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
        kadaiEngine
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
      job.setType(ClassificationChangedJob.class.getName());
      kadaiEngine.getEngine().getJobService().createJob(job);
    }
  }
}
