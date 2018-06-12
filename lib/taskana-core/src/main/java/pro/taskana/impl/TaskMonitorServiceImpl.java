package pro.taskana.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CategoryReportBuilder;
import pro.taskana.ClassificationReportBuilder;
import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * This is the implementation of TaskMonitorService.
 */
public class TaskMonitorServiceImpl implements TaskMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMonitorServiceImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private TaskMonitorMapper taskMonitorMapper;

    TaskMonitorServiceImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
    }

    @Override
    public WorkbasketReportBuilderImpl createWorkbasketReportBuilder() {
        return new WorkbasketReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

    @Override
    public CategoryReportBuilder createCategoryReportBuilder() {
        return new CategoryReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

    @Override
    public ClassificationReportBuilder createClassificationReportBuilder() {
        return new ClassificationReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

    @Override
    public CustomFieldValueReportBuilderImpl createCustomFieldValueReportBuilder(CustomField customField) {
        return new CustomFieldValueReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper, customField);
    }

    @Override
    public TaskStatusReportBuilderImpl createTaskStatusReportBuilder() {
        return new TaskStatusReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

}
