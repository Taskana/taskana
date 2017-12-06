package pro.taskana.impl;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.Classification;
import pro.taskana.model.mappings.ClassificationMapper;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the implementation of ClassificationService.
 */
public class ClassificationServiceImpl implements ClassificationService {

    private static final String ID_PREFIX_CLASSIFICATION = "CLI";
    public static final Date CURRENT_CLASSIFICATIONS_VALID_UNTIL = Date.valueOf("9999-12-31");
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);
    private ClassificationMapper classificationMapper;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;

    public ClassificationServiceImpl(TaskanaEngine taskanaEngine, ClassificationMapper classificationMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
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
            rootClassifications = this.createClassificationQuery().parentClassification("").validUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL).list();
            result = this.populateChildClassifications(rootClassifications);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getClassificationTree(). Returning {} resulting Objects: {} ", numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    private List<Classification> populateChildClassifications(List<Classification> classifications) throws NotAuthorizedException {
        try {
            taskanaEngineImpl.openConnection();
            List<Classification> children = new ArrayList<>();
            for (Classification classification : classifications) {
                List<Classification> childClassifications = this.createClassificationQuery().parentClassification(classification.getId()).validUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL).list();
                children.addAll(populateChildClassifications(childClassifications));
            }
            classifications.addAll(children);
            return classifications;
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public void addClassification(Classification classification) {
        LOGGER.debug("entry to addClassification(classification = {})", classification);
       try {
            taskanaEngineImpl.openConnection();
            classification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
            classification.setCreated(Date.valueOf(LocalDate.now()));

            this.setDefaultValues(classification);

            classificationMapper.insert(classification);
            LOGGER.debug("Method addClassification added classification {}.", classification);
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from addClassification()");
        }
    }

    @Override
    public void updateClassification(Classification classification) {
        LOGGER.debug("entry to updateClassification(Classification = {})", classification);
        try {
            taskanaEngineImpl.openConnection();
            this.setDefaultValues(classification);

            Classification oldClassification = null;
            try {
                oldClassification = this.getClassification(classification.getId(), classification.getDomain());
                LOGGER.debug("Method updateClassification() inserted classification {}.", classification);

                // ! If you update an classification twice the same day,
                // the older version is valid from today until yesterday.
                if (!oldClassification.getDomain().equals(classification.getDomain())) {
                    classification.setCreated(Date.valueOf(LocalDate.now()));
                    classificationMapper.insert(classification);
                    LOGGER.debug("Method updateClassification() inserted classification {}.", classification);
                } else {
                    oldClassification.setValidUntil(Date.valueOf(LocalDate.now().minusDays(1)));
                    classificationMapper.update(oldClassification);
                    classificationMapper.insert(classification);
                    LOGGER.debug("Method updateClassification() updated old classification {} and inserted new {}.", oldClassification, classification);
                }
            } catch (ClassificationNotFoundException e) {
                classification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
                classification.setCreated(Date.valueOf(LocalDate.now()));
                classificationMapper.insert(classification);
                LOGGER.debug("Method updateClassification() inserted classification {}.", classification);
       }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from updateClassification().");
        }
    }

    private void setDefaultValues(Classification classification) {
        classification.setValidFrom(Date.valueOf(LocalDate.now()));
        classification.setValidUntil(CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        classification.setValidInDomain(true);
        if (classification.getServiceLevel() != null) {
            try {
                Duration.parse(classification.getServiceLevel());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid duration. Please use the format defined by ISO 8601");
            }
        }

        if (classification.getParentClassificationId() == classification.getId()) {
            throw new IllegalArgumentException("A classification can't be a parent to itself");
        }

        if (classification.getParentClassificationId() == null) {
            classification.setParentClassificationId("");
        }
        if (classification.getDomain() == null) {
            classification.setDomain("");
        }

    }

    @Override
    public List<Classification> getAllClassificationsWithId(String id, String domain) {
        LOGGER.debug("entry to getAllClassificationsWithId(id = {}, domain = {})", id, domain);
        List<Classification> result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = classificationMapper.getAllClassificationsWithId(id, domain);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getAllClassificationsWithId(). Returning {} resulting Objects: {} ", numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public Classification getClassification(String id, String domain) throws ClassificationNotFoundException {
        LOGGER.debug("entry to getClassification(id = {}, domain = {})", id, domain);
        Classification result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = classificationMapper.findByIdAndDomain(id, domain, CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            if (result == null) {
                result = classificationMapper.findByIdAndDomain(id, "", CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            }
            if (result == null) {
                throw new ClassificationNotFoundException(id);
            }
            return result;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassification(). Returning result {} ", result);
        }
    }

    @Override
    public ClassificationQuery createClassificationQuery() {
        return new ClassificationQueryImpl(taskanaEngine);
    }
}
