package pro.taskana.impl;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.mappings.ClassificationMapper;

/**
 * This is the implementation of ClassificationService.
 */
public class ClassificationServiceImpl implements ClassificationService {

    private static final String ID_PREFIX_CLASSIFICATION = "CLI";
    public static final Date CURRENT_CLASSIFICATIONS_VALID_UNTIL = Date.valueOf("9999-12-31");
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);
    private ClassificationMapper classificationMapper;
    private TaskanaEngineImpl taskanaEngineImpl;

    public ClassificationServiceImpl(TaskanaEngine taskanaEngine, ClassificationMapper classificationMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.classificationMapper = classificationMapper;
    }

    @Override
    public List<Classification> getClassificationTree() throws NotAuthorizedException {
        LOGGER.debug("entry to getClassificationTree()");
        List<Classification> result = null;
        try {
            taskanaEngineImpl.openConnection();
            List<Classification> rootClassifications;
            rootClassifications = this.createClassificationQuery()
                .parentClassificationKey("")
                .validUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL)
                .list();
            rootClassifications = this.populateChildClassifications(rootClassifications);
            return rootClassifications;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getClassificationTree(). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    private List<Classification> populateChildClassifications(List<Classification> classifications)
        throws NotAuthorizedException {
        try {
            taskanaEngineImpl.openConnection();
            List<Classification> children = new ArrayList<>();
            for (Classification classification : classifications) {
                List<Classification> childClassifications = this.createClassificationQuery()
                    .parentClassificationKey(classification.getKey())
                    .validUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL)
                    .list();
                children.addAll(populateChildClassifications(childClassifications));
            }
            classifications.addAll(children);
            return classifications;
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Classification createClassification(Classification classification) throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        LOGGER.debug("entry to createClassification(classification = {})", classification);
        ClassificationImpl classificationImpl;
        final String originalClassificationDomain;
        final String originalClassificationId;
        try {
            taskanaEngineImpl.openConnection();
            try {
                // Fail if classification does already exist.
                this.getClassification(classification.getKey(), classification.getDomain());
                throw new ClassificationAlreadyExistException(classification.getKey());
            } catch (ClassificationNotFoundException e) {
                // add classification into domain
                classificationImpl = (ClassificationImpl) classification;
                this.initDefaultClassificationValues(classificationImpl);
                classificationImpl.setCreated(classificationImpl.getValidFrom());
                classificationMapper.insert(classificationImpl);
                originalClassificationDomain = classificationImpl.getDomain();
                originalClassificationId = classificationImpl.getId();
                LOGGER.debug("Method createClassification created classification {}.", classification);

                // Check if already existing at main domain and add it there too.
                // New instance because returned object should be main-subject.
                if (classificationImpl.getDomain() != "") {
                    classificationImpl.setId(UUID.randomUUID().toString());
                    classificationImpl.setDomain("");
                    try {
                        this.getClassification(classificationImpl.getKey(), classificationImpl.getDomain());
                        LOGGER.debug("Method createClassification: Classification does already exits in the root-domain. Classification {}.", classificationImpl);
                    } catch (ClassificationNotFoundException e2) {
                        classificationImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
                        classificationMapper.insert(classificationImpl);
                        LOGGER.debug("Method createClassification: Classification created in root-domain, too. Classification {}.", classificationImpl);
                    }
                }
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from createClassification()");
        }
        classificationImpl.setId(originalClassificationId);
        classificationImpl.setDomain(originalClassificationDomain);
        return classificationImpl;
    }

    @Override
    public void updateClassification(Classification classification) {
        LOGGER.debug("entry to updateClassification(Classification = {})", classification);
        try {
            taskanaEngineImpl.openConnection();
            ClassificationImpl classificationImpl = (ClassificationImpl) classification;
            this.initDefaultClassificationValues(classificationImpl);

            ClassificationImpl oldClassification = null;
            try {
                oldClassification = (ClassificationImpl) this.getClassification(classificationImpl.getKey(), classificationImpl.getDomain());
                LOGGER.debug("Method updateClassification() inserted classification {}.", classificationImpl);
                // ! If you update an classification twice the same day,
                // the older version is valid from today until yesterday.
                if (!oldClassification.getDomain().equals(classificationImpl.getDomain())) {
                    addClassificationToDomain(classificationImpl);
                } else {
                    updateExistingClassification(oldClassification, classificationImpl);
                }
            } catch (ClassificationNotFoundException e) {
                classificationImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION)); //TODO
                classificationImpl.setCreated(Date.valueOf(LocalDate.now()));
                classificationMapper.insert(classificationImpl);
                LOGGER.debug("Method updateClassification() inserted classification {}.", classificationImpl);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from updateClassification().");
        }
    }

    /**
     * Fill missing values and validate classification before saving the classification.
     * @param classification
     */
    private void initDefaultClassificationValues(ClassificationImpl classification) throws IllegalStateException {
        classification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));

        classification.setValidFrom(Date.valueOf(LocalDate.now()));
        classification.setValidUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL);

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
     * @param classification
     */
    private void addClassificationToDomain(ClassificationImpl classification) {
        classification.setCreated(Date.valueOf(LocalDate.now()));
        classificationMapper.insert(classification);
        LOGGER.debug("Method updateClassification() inserted classification {}.", classification);
    }

    /**
     * Set the validUntil-Date of the oldClassification to yesterday and inserts the new Classification.
     * @param oldClassification
     * @param newClassification
     */
    private void updateExistingClassification(ClassificationImpl oldClassification, ClassificationImpl newClassification) {
        oldClassification.setValidUntil(Date.valueOf(LocalDate.now().minusDays(1)));
        classificationMapper.update(oldClassification);
        classificationMapper.insert(newClassification);
        LOGGER.debug("Method updateClassification() updated old classification {} and inserted new {}.",
                oldClassification, newClassification);
    }

    @Override
    public List<Classification> getAllClassificationsWithKey(String key, String domain) {
        LOGGER.debug("entry to getAllClassificationsWithKey(key = {}, domain = {})", key, domain);
        List<Classification> result = null;
        try {
            taskanaEngineImpl.openConnection();
            List<ClassificationImpl> classifications = classificationMapper.getAllClassificationsWithKey(key, domain);
            List<Classification> results = new ArrayList<>();
            classifications.stream().forEach(c -> results.add((Classification) c));
            return results;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getAllClassificationsWithKey(). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public Classification getClassification(String key, String domain) throws ClassificationNotFoundException {
        if (key == null) {
            throw new ClassificationNotFoundException(null);
        }
        LOGGER.debug("entry to getClassification(key = {}, domain = {})", key, domain);
        Classification result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = classificationMapper.findByKeyAndDomain(key, domain, CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            if (result == null) {
                throw new ClassificationNotFoundException(key);
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
    public Classification newClassification() {
        ClassificationImpl classification = new ClassificationImpl();
        return classification;
    }
}
