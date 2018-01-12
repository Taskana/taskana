package pro.taskana.impl;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.Task;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.mappings.ClassificationMapper;

/**
 * This is the implementation of ClassificationService.
 */
public class ClassificationServiceImpl implements ClassificationService {

    public static final Date CURRENT_CLASSIFICATIONS_VALID_UNTIL = Date.valueOf("9999-12-31");
    private static final String ID_PREFIX_CLASSIFICATION = "CLI";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);
    private ClassificationMapper classificationMapper;
    private TaskanaEngineImpl taskanaEngineImpl;

    public ClassificationServiceImpl(TaskanaEngine taskanaEngine, ClassificationMapper classificationMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.classificationMapper = classificationMapper;
    }

    @Override
    public List<ClassificationSummary> getClassificationTree() {
        LOGGER.debug("entry to getClassificationTree()");
        List<ClassificationSummary> rootClassificationSumamries = null;
        try {
            taskanaEngineImpl.openConnection();
            rootClassificationSumamries = this.createClassificationQuery()
                .parentClassificationKey("")
                .validUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL)
                .list();
            rootClassificationSumamries = this.populateChildClassifications(rootClassificationSumamries);
            return rootClassificationSumamries;
        } catch (NotAuthorizedException ex) {
            LOGGER.debug("getClassificationTree() caught NotAuthorizedException. Throwing SystemException");
            throw new SystemException(
                "ClassificationService.getClassificationTree caught unexpected NotAuthorizedException");
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = rootClassificationSumamries == null ? 0
                    : rootClassificationSumamries.size();
                LOGGER.debug("exit from getClassificationTree(). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(rootClassificationSumamries));
            }
        }
    }

    private List<ClassificationSummary> populateChildClassifications(
        List<ClassificationSummary> classificationSumamries)
        throws NotAuthorizedException {
        try {
            taskanaEngineImpl.openConnection();
            List<ClassificationSummary> children = new ArrayList<>();
            for (ClassificationSummary classification : classificationSumamries) {
                List<ClassificationSummary> childClassifications = this.createClassificationQuery()
                    .parentClassificationKey(classification.getKey())
                    .validUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL)
                    .list();
                children.addAll(populateChildClassifications(childClassifications));
            }
            classificationSumamries.addAll(children);
            return classificationSumamries;
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Classification createClassification(Classification classification)
        throws ClassificationAlreadyExistException {
        LOGGER.debug("entry to createClassification(classification = {})", classification);
        ClassificationImpl classificationImpl;
        final boolean isClassificationExisting;
        try {
            taskanaEngineImpl.openConnection();
            isClassificationExisting = doesClassificationExist(classification.getKey(), classification.getDomain());

            if (isClassificationExisting) {
                throw new ClassificationAlreadyExistException(classification);
            }
            classificationImpl = (ClassificationImpl) classification;
            this.initDefaultClassificationValues(classificationImpl);

            classificationMapper.insert(classificationImpl);
            LOGGER.debug("Method createClassification created classification {}.", classification);

            addClassificationToRootDomain(classificationImpl);
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from createClassification()");
        }
        return classificationImpl;
    }

    private void addClassificationToRootDomain(ClassificationImpl classificationImpl) {
        if (classificationImpl.getDomain() != "") {
            boolean doesExist = true;
            String idBackup = classificationImpl.getId();
            String domainBackup = classificationImpl.getDomain();
            classificationImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
            classificationImpl.setDomain("");
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
            }
        }
    }

    @Override
    public Classification updateClassification(Classification classification) {
        LOGGER.debug("entry to updateClassification(Classification = {})", classification);
        ClassificationImpl classificationImpl = null;
        try {
            taskanaEngineImpl.openConnection();
            classificationImpl = (ClassificationImpl) classification;
            this.initDefaultClassificationValues(classificationImpl);

            ClassificationImpl oldClassification = null;
            try {
                oldClassification = (ClassificationImpl) this.getClassification(classificationImpl.getKey(),
                    classificationImpl.getDomain());
                LOGGER.debug("Method updateClassification() inserted classification {}.", classificationImpl);
                // ! If you update an classification twice the same day,
                // the older version is valid from today until yesterday.
                if (!oldClassification.getDomain().equals(classificationImpl.getDomain())) {
                    addClassificationToDomain(classificationImpl);
                } else {
                    updateExistingClassification(oldClassification, classificationImpl);
                }
            } catch (ClassificationNotFoundException e) {
                classificationImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION)); // TODO
                classificationImpl.setCreated(Date.valueOf(LocalDate.now()));
                classificationMapper.insert(classificationImpl);
                LOGGER.debug("Method updateClassification() inserted classification {}.", classificationImpl);
            }
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
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from updateClassification().");
        }
    }

    /**
     * Fill missing values and validate classification before saving the classification.
     *
     * @param classification
     */
    private void initDefaultClassificationValues(ClassificationImpl classification) throws IllegalStateException {
        classification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));

        classification.setValidFrom(Date.valueOf(LocalDate.now()));
        classification.setValidUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        if (classification.getCreated() == null) {
            classification.setCreated(Date.valueOf(LocalDate.now()));
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

        if (classification.getParentClassificationKey() == null) {
            classification.setParentClassificationKey("");
        }

        if (classification.getDomain() == null) {
            classification.setDomain("");
        }
    }

    /**
     * Add a new Classification if this Classification Key is not yet specified for this domain.
     *
     * @param classification
     */
    private void addClassificationToDomain(ClassificationImpl classification) {
        classification.setCreated(Date.valueOf(LocalDate.now()));
        classificationMapper.insert(classification);
        LOGGER.debug("Method updateClassification() inserted classification {}.", classification);
    }

    /**
     * Set the validUntil-Date of the oldClassification to yesterday and inserts the new Classification.
     *
     * @param oldClassification
     * @param newClassification
     */
    private Classification updateExistingClassification(ClassificationImpl oldClassification,
        ClassificationImpl newClassification) {
        oldClassification.setValidUntil(Date.valueOf(LocalDate.now().minusDays(1)));
        classificationMapper.update(oldClassification);
        classificationMapper.insert(newClassification);
        LOGGER.debug("Method updateClassification() updated old classification {} and inserted new {}.",
            oldClassification, newClassification);
        return newClassification;
    }

    @Override
    public List<ClassificationSummary> getAllClassifications(String key, String domain) {
        LOGGER.debug("entry to getAllClassificationsWithKey(key = {}, domain = {})", key, domain);
        List<ClassificationSummary> results = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            List<ClassificationSummaryImpl> classificationSummaries = classificationMapper
                .getAllClassificationsWithKey(key, domain);
            classificationSummaries.stream().forEach(c -> results.add(c));
            return results;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = results == null ? 0 : results.size();
                LOGGER.debug("exit from getAllClassificationsWithKey(). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(results));
            }
        }
    }

    @Override
    public Classification getClassification(String key, String domain) throws ClassificationNotFoundException {
        if (key == null) {
            throw new ClassificationNotFoundException(
                "Classification for key " + key + " and domain " + domain + " was not found.");
        }
        LOGGER.debug("entry to getClassification(key = {}, domain = {})", key, domain);
        Classification result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = classificationMapper.findByKeyAndDomain(key, domain, CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            if (result == null) {
                result = classificationMapper.findByKeyAndDomain(key, "", CURRENT_CLASSIFICATIONS_VALID_UNTIL);
                if (result == null) {
                    throw new ClassificationNotFoundException("Classification for key " + key + " was not found");
                }
            }
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassification(). Returning result {} ", result);
        }
    }

    @Override
    public ClassificationQuery createClassificationQuery() {
        return new ClassificationQueryImpl(taskanaEngineImpl);
    }

    @Override
    public Classification newClassification(String domain, String key, String type) {
        ClassificationImpl classification = new ClassificationImpl();
        classification.setKey(key);
        classification.setDomain(domain);
        classification.setType(type);
        return classification;
    }

    private boolean doesClassificationExist(String key, String domain) {
        boolean isExisting = false;
        try {
            if (classificationMapper.findByKeyAndDomain(key, domain, CURRENT_CLASSIFICATIONS_VALID_UNTIL) != null) {
                isExisting = true;
            }
        } catch (Exception ex) {
            LOGGER.warn(
                "Classification-Service throwed Exception while calling mapper and searching for classification. EX={}",
                ex);
        }
        return isExisting;
    }

    public Classification getClassificationByTask(Task task) throws ClassificationNotFoundException {
        if (task.getId() == null) {
            throw new ClassificationNotFoundException("Classification for task with id null was not found.");
        }
        LOGGER.debug("entry to getClassificationByTask(taskId = {})", task.getId());
        TaskImpl taskImpl = (TaskImpl) task;
        String classificationKey = taskImpl.getClassificationKey();
        Classification result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = classificationMapper.findByTask(classificationKey, task.getWorkbasketKey(),
                CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            if (result == null) {
                throw new ClassificationNotFoundException(
                    "Classification for task with id " + task.getId() + " was not found.");
            }
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassification(). Returning result {} ", result);
        }
    }

}
