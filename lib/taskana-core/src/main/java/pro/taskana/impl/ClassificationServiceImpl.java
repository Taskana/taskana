package pro.taskana.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.JobMapper;
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
        throws ClassificationAlreadyExistException, NotAuthorizedException, ClassificationNotFoundException,
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
            classificationImpl.setCreated(Instant.now());
            classificationImpl.setModified(classificationImpl.getCreated());
            this.initDefaultClassificationValues(classificationImpl);

            if (classificationImpl.getParentId() != null && !classificationImpl.getParentId().isEmpty()) {
                this.getClassification(classificationImpl.getParentId());
            }
            classificationMapper.insert(classificationImpl);
            LOGGER.debug("Method createClassification created classification {}.", classificationImpl);

            if (!classification.getDomain().isEmpty()) {
                addClassificationToRootDomain(classificationImpl);
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createClassification()");
        }
        return classificationImpl;
    }

    private void addClassificationToRootDomain(ClassificationImpl classificationImpl) {
        if (classificationImpl.getDomain() != "") {
            boolean doesExist = true;
            String idBackup = classificationImpl.getId();
            String domainBackup = classificationImpl.getDomain();
            boolean isValidInDomainBackup = classificationImpl.getIsValidInDomain();
            classificationImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
            classificationImpl.setDomain("");
            classificationImpl.setIsValidInDomain(false);
            try {
                this.getClassification(classificationImpl.getKey(), classificationImpl.getDomain());
                throw new ClassificationAlreadyExistException(classificationImpl);
            } catch (ClassificationNotFoundException e) {
                doesExist = false;
                LOGGER.debug(
                    "Method createClassification: Classification does not exist in root domain. Classification {}.",
                    classificationImpl);
            } catch (ClassificationAlreadyExistException ex) {
                LOGGER.warn(
                    "Method createClassification: Classification does already exist in root domain. Classification {}.",
                    classificationImpl);
            } finally {
                if (!doesExist) {
                    classificationMapper.insert(classificationImpl);
                    LOGGER.debug(
                        "Method createClassification: Classification created in root-domain, too. Classification {}.",
                        classificationImpl);
                }
                classificationImpl.setId(idBackup);
                classificationImpl.setDomain(domainBackup);
                classificationImpl.setIsValidInDomain(isValidInDomainBackup);
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
                    "The current Classification has been modified while editing. The values can not be updated. Classification="
                        + classificationImpl.toString());
            }
            classificationImpl.setModified(Instant.now());
            this.initDefaultClassificationValues(classificationImpl);

            // Update classification fields used by tasks
            if (oldClassification.getCategory() != classificationImpl.getCategory()) {
                List<TaskSummary> taskSumamries = taskanaEngine.getTaskService()
                    .createTaskQuery()
                    .classificationIdIn(oldClassification.getId())
                    .list();

                boolean categoryChanged = !(oldClassification.getCategory() == null
                    ? classification.getCategory() == null
                    : oldClassification.getCategory().equals(classification.getCategory()));
                if (!taskSumamries.isEmpty() && categoryChanged) {
                    List<String> taskIds = new ArrayList<>();
                    taskSumamries.stream().forEach(ts -> taskIds.add(ts.getTaskId()));
                    taskMapper.updateClassificationCategoryOnChange(taskIds, classificationImpl.getCategory());
                }
            }

            // Check if parentId changed and object does exist
            if (!oldClassification.getParentId().equals(classificationImpl.getParentId())) {
                if (classificationImpl.getParentId() != null && !classificationImpl.getParentId().isEmpty()) {
                    this.getClassification(classificationImpl.getParentId());
                }
            }
            classificationMapper.update(classificationImpl);
            boolean priorityChanged = oldClassification.getPriority() != classification.getPriority();
            boolean serviceLevelChanged = oldClassification.getServiceLevel() != classification.getServiceLevel();

            if (priorityChanged || serviceLevelChanged) {
                Map<String, String> args = new HashMap<>();
                args.put(TaskUpdateOnClassificationChangeExecutor.CLASSIFICATION_ID, classificationImpl.getId());
                args.put(TaskUpdateOnClassificationChangeExecutor.PRIORITY_CHANGED, String.valueOf(priorityChanged));
                args.put(TaskUpdateOnClassificationChangeExecutor.SERVICE_LEVEL_CHANGED,
                    String.valueOf(serviceLevelChanged));
                Job job = new Job();
                job.setCreated(Instant.now());
                job.setState(Job.State.READY);
                job.setExecutor(TaskUpdateOnClassificationChangeExecutor.class.getName());
                job.setArguments(args);
                taskanaEngine.getSqlSession().getMapper(JobMapper.class).insertJob(job);
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
        if (classification.getId() == null) {
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

        if (classification.getServiceLevel() != null) {
            try {
                Duration.parse(classification.getServiceLevel());
            } catch (Exception e) {
                throw new InvalidArgumentException("Invalid service level. Please use the format defined by ISO 8601");
            }
        }

        if (classification.getKey() == null) {
            throw new InvalidArgumentException("Classification must contain a key");
        }

        if (classification.getParentId() == null) {
            classification.setParentId("");
        }

        if (classification.getType() != null
            && !taskanaEngine.getConfiguration().getClassificationTypes().contains(classification.getType())) {
            throw new InvalidArgumentException("Given classification type " + classification.getType()
                + " is not valid according to the configuration.");
        }

        if (classification.getCategory() != null
            && !taskanaEngine.getConfiguration().getClassificationCategories().contains(classification.getCategory())) {
            throw new InvalidArgumentException("Given classification category " + classification.getCategory()
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
                LOGGER.error("Classification for id {} was not found. Throwing ClassificationNotFoundException", id);
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
                    LOGGER.error(
                        "Classification for key {} and domain {} was not found. Throwing ClassificationNotFoundException",
                        key, domain);
                    throw new ClassificationNotFoundException(key, domain,
                        "Classification for key " + key + " was not found");
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
                    "The classification " + classificationKey + "wasn't found in the domain " + domain);
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
                    "The classification " + classificationId + "wasn't found");
            }

            if (classification.getDomain().equals("")) {
                // master mode - delete all associated classifications in every domain.
                List<ClassificationSummary> classificationsInDomain = createClassificationQuery()
                    .keyIn(classification.getKey()).list();
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
                    throw new ClassificationInUseException("The classification " + classificationId
                        + " is in use and cannot be deleted. There are either tasks or attachments associated with the classification.");
                }
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteClassification()");
        }
    }

    private boolean isReferentialIntegrityConstraintViolation(PersistenceException e) {
        // DB2 check missing
        return (e.getCause().getClass().getName().equals("org.h2.jdbc.JdbcSQLException")
            && e.getMessage().contains("23503"));
    }

}
