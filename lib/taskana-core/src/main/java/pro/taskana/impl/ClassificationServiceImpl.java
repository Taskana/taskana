package pro.taskana.impl;

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

import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.jobs.ClassificationChangedJob;
import pro.taskana.jobs.ScheduledJob;
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.TaskMapper;

/**
 * This is the implementation of ClassificationService.
 */
public class ClassificationServiceImpl implements ClassificationService {

    private static final String ID_PREFIX_CLASSIFICATION = "CLI";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);
    private ClassificationMapper classificationMapper;
    private TaskMapper taskMapper;
    private TaskanaEngineImpl taskanaEngine;

    ClassificationServiceImpl(TaskanaEngine taskanaEngine, ClassificationMapper classificationMapper,
        TaskMapper taskMapper) {
        super();
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.classificationMapper = classificationMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    public Classification createClassification(Classification classification)
        throws ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        LOGGER.debug("entry to createClassification(classification = {})", classification);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        if (!taskanaEngine.domainExists(classification.getDomain()) && !"".equals(classification.getDomain())) {
            throw new DomainNotFoundException(classification.getDomain(),
                "Domain " + classification.getDomain() + " does not exist in the configuration.");
        }
        ClassificationImpl classificationImpl;
        final boolean isClassificationExisting;
        try {
            taskanaEngine.openConnection();
            isClassificationExisting = doesClassificationExist(classification.getKey(), classification.getDomain());
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
            LOGGER.debug("Method createClassification created classification {}.", classificationImpl);

            if (!classification.getDomain().isEmpty()) {
                addClassificationToMasterDomain(classificationImpl);
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createClassification()");
        }
        return classificationImpl;
    }

    private void validateAndPopulateParentInformation(ClassificationImpl classificationImpl)
        throws InvalidArgumentException {
        try {

            if (classificationImpl.getParentId() != null && !classificationImpl.getParentId().isEmpty()) {
                Classification parentClassification = this.getClassification(classificationImpl.getParentId());
                if (classificationImpl.getParentKey() != null && !classificationImpl.getParentKey().isEmpty()) {
                    if (!classificationImpl.getParentKey().equals(parentClassification.getKey())) {
                        throw new InvalidArgumentException(
                            "Given parent key of classification does not match key of parent id.");
                    }
                    classificationImpl.setParentKey(parentClassification.getKey());
                }
            }

            if (classificationImpl.getParentKey() != null && !classificationImpl.getParentKey().isEmpty()) {
                Classification parentClassification = this.getClassification(classificationImpl.getParentKey(),
                    classificationImpl.getDomain());
                classificationImpl.setParentId(parentClassification.getId());
            }

        } catch (ClassificationNotFoundException e) {
            throw new InvalidArgumentException("Parent classification could not be found.", e);
        }
    }

    private void checkClassificationId(ClassificationImpl classificationImpl) throws InvalidArgumentException {
        if (classificationImpl.getId() != null && !"".equals(classificationImpl.getId())) {
            throw new InvalidArgumentException("ClassificationId should be null on creation");
        }
    }

    private void addClassificationToMasterDomain(ClassificationImpl classificationImpl) {
        if (!Objects.equals(classificationImpl.getDomain(), "")) {
            boolean doesExist = true;
            ClassificationImpl masterClassification = new ClassificationImpl(classificationImpl);
            masterClassification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
            masterClassification.setParentKey(classificationImpl.getParentKey());
            masterClassification.setDomain("");
            masterClassification.setIsValidInDomain(false);
            try {
                if (classificationImpl.getParentKey() != null && !"".equals(classificationImpl.getParentKey())) {
                    masterClassification.setParentId(getClassification(classificationImpl.getParentKey(), "").getId());
                }
                this.getClassification(masterClassification.getKey(), masterClassification.getDomain());
                throw new ClassificationAlreadyExistException(masterClassification);
            } catch (ClassificationNotFoundException e) {
                doesExist = false;
                LOGGER.debug(
                    "Method createClassification: Classification does not exist in master domain. Classification {}.",
                    masterClassification);
            } catch (ClassificationAlreadyExistException ex) {
                LOGGER.warn(
                    "Method createClassification: Classification does already exist in master domain. Classification {}.",
                    masterClassification);
            } finally {
                if (!doesExist) {
                    classificationMapper.insert(masterClassification);
                    LOGGER.debug(
                        "Method createClassification: Classification created in master-domain, too. Classification {}.",
                        masterClassification);
                }
            }
        }
    }

    @Override
    public Classification updateClassification(Classification classification)
        throws NotAuthorizedException, ConcurrencyException, ClassificationNotFoundException, InvalidArgumentException {
        LOGGER.debug("entry to updateClassification(Classification = {})", classification);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        ClassificationImpl classificationImpl = null;
        try {
            taskanaEngine.openConnection();
            classificationImpl = (ClassificationImpl) classification;

            // Check if current object is based on the newest (by modified)
            Classification oldClassification = this.getClassification(classificationImpl.getKey(),
                classificationImpl.getDomain());
            if (!oldClassification.getModified().equals(classificationImpl.getModified())) {
                throw new ConcurrencyException(
                    "The current Classification has been modified while editing. The values can not be updated. Classification "
                        + classificationImpl.toString());
            }
            classificationImpl.setModified(Instant.now());
            this.initDefaultClassificationValues(classificationImpl);

            // Update classification fields used by tasks
            if (oldClassification.getCategory() != classificationImpl.getCategory()) {
                List<TaskSummary> taskSummaries = taskanaEngine.getTaskService()
                    .createTaskQuery()
                    .classificationIdIn(oldClassification.getId())
                    .list();

                boolean categoryChanged = !(oldClassification.getCategory() == null
                    ? classification.getCategory() == null
                    : oldClassification.getCategory().equals(classification.getCategory()));
                if (!taskSummaries.isEmpty() && categoryChanged) {
                    List<String> taskIds = new ArrayList<>();
                    taskSummaries.stream().forEach(ts -> taskIds.add(ts.getTaskId()));
                    taskMapper.updateClassificationCategoryOnChange(taskIds, classificationImpl.getCategory());
                }
            }

            // Check if parentId changed and object does exist
            if (!oldClassification.getParentId().equals(classificationImpl.getParentId())) {
                if (classificationImpl.getParentId() != null && !classificationImpl.getParentId().isEmpty()) {
                    this.getClassification(classificationImpl.getParentId());
                }
            }

            // Check if parentKey changed and object does exist
            if (!oldClassification.getParentKey().equals(classificationImpl.getParentKey())) {
                if (classificationImpl.getParentKey() != null && !classificationImpl.getParentKey().isEmpty()) {
                    this.getClassification(classificationImpl.getParentKey(), classificationImpl.getDomain());
                }
            }

            classificationMapper.update(classificationImpl);
            boolean priorityChanged = oldClassification.getPriority() != classification.getPriority();

            boolean serviceLevelChanged = !Objects.equals(oldClassification.getServiceLevel(),
                classification.getServiceLevel());

            if (priorityChanged || serviceLevelChanged) {
                Map<String, String> args = new HashMap<>();
                args.put(ClassificationChangedJob.CLASSIFICATION_ID, classificationImpl.getId());
                args.put(ClassificationChangedJob.PRIORITY_CHANGED, String.valueOf(priorityChanged));
                args.put(ClassificationChangedJob.SERVICE_LEVEL_CHANGED,
                    String.valueOf(serviceLevelChanged));
                ScheduledJob job = new ScheduledJob();
                job.setArguments(args);
                job.setType(ScheduledJob.Type.CLASSIFICATIONCHANGEDJOB);
                taskanaEngine.getJobService().createJob(job);
            }

            LOGGER.debug("Method updateClassification() updated the classification {}.",
                classificationImpl);
            return classification;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from updateClassification().");
        }
    }

    /**
     * Fill missing values and validate classification before saving the classification.
     *
     * @param classification
     */
    private void initDefaultClassificationValues(ClassificationImpl classification) throws InvalidArgumentException {
        Instant now = Instant.now();
        if (classification.getId() == null || "".equals(classification.getId())) {
            classification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
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

        if (classification.getServiceLevel() != null && !"".equals(classification.getServiceLevel())) {
            try {
                Duration.parse(classification.getServiceLevel());
            } catch (Exception e) {
                throw new InvalidArgumentException("Invalid service level. Please use the format defined by ISO 8601. "
                    + "For example: \"P2D\" represents a period of \"two days.\" "
                    + "Or \"P2DT6H\" represents a period of \"two days and six hours.\"",
                    e.getCause());
            }
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
            && !taskanaEngine.getConfiguration().getClassificationTypes().contains(classification.getType())) {
            throw new InvalidArgumentException("Given classification type " + classification.getType()
                + " is not valid according to the configuration.");
        }

        if (classification.getCategory() != null
                && !taskanaEngine.getConfiguration().getClassificationCategoriesByType(classification.getType()).contains(classification.getCategory())) {
            throw new InvalidArgumentException("Given classification category " + classification.getCategory() + " with type " + classification.getType()
                + " is not valid according to the configuration.");
        }

        if (classification.getDomain().isEmpty()) {
            classification.setIsValidInDomain(false);
        }
    }

    @Override
    public Classification getClassification(String id) throws ClassificationNotFoundException {
        if (id == null) {
            throw new ClassificationNotFoundException(id,
                "Classification for id " + id + " was not found.");
        }
        LOGGER.debug("entry to getClassification(id = {})", id);
        Classification result = null;
        try {
            taskanaEngine.openConnection();
            result = classificationMapper.findById(id);
            if (result == null) {
                throw new ClassificationNotFoundException(id, "Classification for id " + id + " was not found");
            }
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from getClassification(). Returning result {} ", result);
        }
    }

    @Override
    public Classification getClassification(String key, String domain) throws ClassificationNotFoundException {
        LOGGER.debug("entry to getClassification(key = {}, domain = {})", key, domain);
        if (key == null) {
            throw new ClassificationNotFoundException(key, domain,
                "Classification for null key and domain " + domain + " was not found.");
        }
        LOGGER.debug("entry to getClassification(key = {}, domain = {})", key, domain);
        Classification result = null;
        try {
            taskanaEngine.openConnection();
            result = classificationMapper.findByKeyAndDomain(key, domain);
            if (result == null) {
                result = classificationMapper.findByKeyAndDomain(key, "");
                if (result == null) {
                    throw new ClassificationNotFoundException(key, domain,
                        "Classification for key = " + key + " and master domain was not found");
                }
            }
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from getClassification(). Returning result {} ", result);
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

    private boolean doesClassificationExist(String key, String domain) {
        boolean isExisting = false;
        try {
            if (classificationMapper.findByKeyAndDomain(key, domain) != null) {
                isExisting = true;
            }
        } catch (Exception ex) {
            LOGGER.warn(
                "Classification-Service throwed Exception while calling mapper and searching for classification. EX={}",
                ex);
        }
        return isExisting;
    }

    @Override
    public void deleteClassification(String classificationKey, String domain)
        throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to deleteClassification(key = {}, domain = {})", classificationKey, domain);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            Classification classification = this.classificationMapper.findByKeyAndDomain(classificationKey, domain);
            if (classification == null) {
                throw new ClassificationNotFoundException(classificationKey, domain,
                    "The classification \"" + classificationKey + "\" wasn't found in the domain " + domain);
            }
            deleteClassification(classification.getId());
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteClassification(key,domain)");
        }
    }

    @Override
    public void deleteClassification(String classificationId)
        throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to deleteClassification(id = {})", classificationId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            Classification classification = this.classificationMapper.findById(classificationId);
            if (classification == null) {
                throw new ClassificationNotFoundException(classificationId,
                    "The classification \"" + classificationId + "\" wasn't found");
            }

            if (classification.getDomain().equals("")) {
                // master mode - delete all associated classifications in every domain.
                List<ClassificationSummary> classificationsInDomain = createClassificationQuery()
                    .keyIn(classification.getKey())
                    .list();
                for (ClassificationSummary classificationInDomain : classificationsInDomain) {
                    if (!"".equals(classificationInDomain.getDomain())) {
                        deleteClassification(classificationInDomain.getId());
                    }
                }
            }

            List<ClassificationSummary> childClassifications = createClassificationQuery().parentIdIn(classificationId)
                .list();
            for (ClassificationSummary child : childClassifications) {
                this.deleteClassification(child.getId());
            }

            try {
                this.classificationMapper.deleteClassification(classificationId);
            } catch (PersistenceException e) {
                if (isReferentialIntegrityConstraintViolation(e)) {
                    throw new ClassificationInUseException(
                        "The classification id = \"" + classificationId + "\" and key = \"" + classification.getKey()
                            + "\" in domain = \"" + classification.getDomain()
                            + "\" is in use and cannot be deleted. There are either tasks or attachments associated with the classification.",
                        e.getCause());
                }
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteClassification()");
        }
    }

    private boolean isReferentialIntegrityConstraintViolation(PersistenceException e) {
        return isH2OrPostgresIntegrityConstraintViolation(e) || isDb2IntegrityConstraintViolation(e);
    }

    private boolean isDb2IntegrityConstraintViolation(PersistenceException e) {
        return e.getCause() instanceof SQLIntegrityConstraintViolationException && e.getMessage().contains("-532");
    }

    private boolean isH2OrPostgresIntegrityConstraintViolation(PersistenceException e) {
        return e.getCause() instanceof SQLException && ((SQLException) e.getCause()).getSQLState().equals("23503");
    }

}
