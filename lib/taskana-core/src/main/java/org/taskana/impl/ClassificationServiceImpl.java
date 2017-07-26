package org.taskana.impl;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.taskana.ClassificationService;
import org.taskana.TaskanaEngine;
import org.taskana.impl.persistence.ClassificationQueryImpl;
import org.taskana.impl.util.IdGenerator;
import org.taskana.model.Classification;
import org.taskana.model.mappings.ClassificationMapper;
import org.taskana.persistence.ClassificationQuery;

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
        classification.setModified(Date.valueOf(LocalDate.now()));
        this.checkServiceLevel(classification);

        classificationMapper.insert(classification);
    }

    @Override
    public void updateClassification(Classification classification) {
        classification.setModified(Date.valueOf(LocalDate.now()));
        this.checkServiceLevel(classification);

        classificationMapper.update(classification);
    }

    @Override
    public Classification selectClassificationById(String id) {
        return classificationMapper.findById(id);
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
