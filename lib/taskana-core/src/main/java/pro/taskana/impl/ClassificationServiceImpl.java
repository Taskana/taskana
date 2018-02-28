package pro.taskana.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.util.IdGenerator;
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
        throws ClassificationAlreadyExistException, NotAuthorizedException {
        LOGGER.debug("entry to createClassification(classification = {})", classification);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
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

            classificationMapper.insert(classificationImpl);
            LOGGER.debug("Method createClassification created classification {}.", classification);

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
        throws NotAuthorizedException, ConcurrencyException {
        LOGGER.debug("entry to updateClassification(Classification = {})", classification);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        ClassificationImpl classificationImpl = null;
        try {
            taskanaEngine.openConnection();
            classificationImpl = (ClassificationImpl) classification;
            // UPDATE/INSERT classification
            try {
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
                        .classificationKeyIn(oldClassification.getKey())
                        .classificationCategoryIn(oldClassification.getCategory())
                        .list();
                    if (!taskSumamries.isEmpty()) {
                        List<String> taskIds = new ArrayList<>();
                        taskSumamries.stream().forEach(ts -> taskIds.add(ts.getTaskId()));
                        taskMapper.updateClassificationCategoryOnChange(taskIds, classificationImpl.getCategory());
                    }
                }
                classificationMapper.update(classificationImpl);
                LOGGER.debug("Method updateClassification() updated the classification {}.",
                    classificationImpl);
            } catch (ClassificationNotFoundException e) {
                classificationImpl.setCreated(classification.getModified());
                classificationMapper.insert(classificationImpl);
                LOGGER.debug(
                    "Method updateClassification() inserted a unpersisted classification which was wanted to be updated {}.",
                    classificationImpl);
            }

            // CHECK if classification does exist now
            try {
                Classification updatedClassification = this.getClassification(classificationImpl.getKey(),
                    classificationImpl.getDomain());
                return updatedClassification;
            } catch (ClassificationNotFoundException e) {
                LOGGER.debug(
                    "Throwing SystemException because updateClassification didn't find new classification {} after update",
                    classification);
                throw new SystemException("updateClassification didn't find new classification after update");
            }
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
    private void initDefaultClassificationValues(ClassificationImpl classification) throws IllegalStateException {
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
                throw new IllegalArgumentException("Invalid duration. Please use the format defined by ISO 8601");
            }
        }

        if (classification.getKey() == null) {
            throw new IllegalStateException("Classification must contain a key");
        }

        if (classification.getParentId() == null) {
            classification.setParentId("");
        }

        if (classification.getDomain() == null) {
            classification.setDomain("");
        }

        if (classification.getDomain().isEmpty()) {
            classification.setIsValidInDomain(false);
        }
    }

    @Override
    public Classification getClassification(String id) throws ClassificationNotFoundException {
        if (id == null) {
            throw new ClassificationNotFoundException(
                "Classification for id " + id + " was not found.");
        }
        LOGGER.debug("entry to getClassification(id = {})", id);
        Classification result = null;
        try {
            taskanaEngine.openConnection();
            result = classificationMapper.findById(id);
            if (result == null) {
                LOGGER.error("Classification for id {} was not found. Throwing ClassificationNotFoundException", id);
                throw new ClassificationNotFoundException("Classification for id " + id + " was not found");
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
            throw new ClassificationNotFoundException(
                "Classification for key " + key + " and domain " + domain + " was not found.");
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
                    throw new ClassificationNotFoundException("Classification for key " + key + " was not found");
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
                throw new ClassificationNotFoundException(
                    "The classification " + classificationKey + "wasn't found in the domain " + domain);
            }

            if (domain.equals("")) {
                // master mode - delete all associated classifications in every domain.
                List<String> domains = this.classificationMapper.getDomainsForClassification(classificationKey);
                domains.remove("");
                for (String classificationDomain : domains) {
                    deleteClassification(classificationKey, classificationDomain);
                }
            }

            TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngine.getTaskService();
            try {
                List<TaskSummary> classificationTasks = taskService.createTaskQuery()
                    .classificationKeyIn(classificationKey)
                    .list();
                if (classificationTasks.stream()
                    .anyMatch(t -> domain.equals(t.getClassificationSummary().getDomain()))) {
                    throw new ClassificationInUseException(
                        "There are Tasks that belong to this classification or a child classification. Please complete them and try again.");
                }
            } catch (NotAuthorizedToQueryWorkbasketException e) {
                LOGGER.error(
                    "ClassificationQuery unexpectedly returned NotauthorizedException. Throwing SystemException ");
                throw new SystemException("ClassificationQuery unexpectedly returned NotauthorizedException.");
            }

            List<String> childKeys = this.classificationMapper
                .getChildrenOfClassification(classification.getId());
            for (String key : childKeys) {
                this.deleteClassification(key, domain);
            }

            this.classificationMapper.deleteClassificationInDomain(classificationKey, domain);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteClassification()");
        }
    }
}
