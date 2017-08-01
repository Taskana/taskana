package org.taskana.impl;

import org.taskana.ClassificationService;
import org.taskana.TaskanaEngine;
import org.taskana.impl.persistence.ClassificationQueryImpl;
import org.taskana.impl.util.IdGenerator;
import org.taskana.model.Classification;
import org.taskana.model.mappings.ClassificationMapper;
import org.taskana.persistence.ClassificationQuery;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * This is the implementation of ClassificationService.
 */
public class ClassificationServiceImpl implements ClassificationService {

    private static final String ID_PREFIX_CLASSIFICATION = "CLI";

    private ClassificationMapper classificationMapper;
    private TaskanaEngine taskanaEngine;

    public ClassificationServiceImpl(TaskanaEngine taskanaEngine, ClassificationMapper classificationMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
        this.classificationMapper = classificationMapper;
    }

    @Override
    public List<Classification> selectClassifications() {
        final List<Classification> rootClassifications = classificationMapper.findByParentId("");
        populateChildClassifications(rootClassifications);
        return rootClassifications;
    }

    private void populateChildClassifications(final List<Classification> classifications) {
        for (Classification classification : classifications) {
            List<Classification> childClassifications = classificationMapper.findByParentId(classification.getId());
            classification.setChildren(childClassifications);
            populateChildClassifications(childClassifications);
        }
    }

    @Override
    public List<Classification> selectClassificationsByParentId(String parentId) {
        return classificationMapper.findByParentId(parentId);
    }

    @Override
    public void insertClassification(Classification classification) {
        classification.setId(IdGenerator.generateWithPrefix(ID_PREFIX_CLASSIFICATION));
        classification.setCreated(Date.valueOf(LocalDate.now()));
        classification.setValidFrom(Date.valueOf(LocalDate.now()));
        classification.setValidUntil(Date.valueOf("9999-12-31"));
        this.checkServiceLevel(classification);
        if (classification.getDomain() == null) {
            classification.setDomain("");
        }

        classificationMapper.insert(classification);
    }

    @Override
    public void updateClassification(Classification classification) {
        this.checkServiceLevel(classification);
        Date today = Date.valueOf(LocalDate.now());

        Classification oldClassification = classificationMapper.findByIdAndDomain(classification.getId(), classification.getDomain());

        if (oldClassification != null) {
            if (oldClassification.getValidFrom().equals(today)) {
                // if we insert a new Classification, oldClassification gets a negative Duration
                classificationMapper.update(classification);
            } else {
                oldClassification.setValidUntil(Date.valueOf(LocalDate.now().minusDays(1)));
                classificationMapper.update(oldClassification);

                classification.setValidFrom(today);
                classification.setValidUntil(Date.valueOf("9999-12-31"));
                classificationMapper.insert(classification);
            }
        } else {
            if (classificationMapper.findByIdAndDomain(classification.getId(), "").equals(null)) {
                throw new IllegalArgumentException("There is no Default-Classification with this ID!");
            } else {
                classification.setValidFrom(today);
                classification.setValidUntil(Date.valueOf("9999-12-31"));
                classificationMapper.insert(classification);
            }
        }
    }

    @Override
    public Classification selectClassificationById(String id) {
        return classificationMapper.findByIdAndDomain(id, "");
    }

    @Override
    public Classification selectClassificationByIdAndDomain(String id, String domain) {
        Classification classification = classificationMapper.findByIdAndDomain(id, domain);
        if (classification.equals(null)) {
            return classificationMapper.findByIdAndDomain(id, "");
        } else {
            return classification;
        }
    }

    @Override
    public List<Classification> selectClassificationByDomain(String domain) {
        return classificationMapper.findByDomain(domain);
    }

    @Override
    public List<Classification> selectClassificationByDomainAndType(String domain, String type) {
        return classificationMapper.getClassificationByDomainAndType(domain, type);
    }

    @Override
    public List<Classification> selectClassificationByDomainAndCategory(String domain, String category) {
        return classificationMapper.getClassificationByDomainAndCategory(domain, category);
    }

    @Override
    public List<Classification> selectClassificationByCategoryAndType(String category, String type) {
        return classificationMapper.getClassificationByCategoryAndType(category, type);
    }

    private void checkServiceLevel(Classification classification) {
        if (classification.getServiceLevel() != null) {
            try {
                Duration.parse(classification.getServiceLevel());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid timestamp. Please use the format 'PddDThhHmmM'");
            }
        }
    }

    @Override
    public ClassificationQuery createClassificationQuery() {
        return new ClassificationQueryImpl(taskanaEngine);
    }

}
