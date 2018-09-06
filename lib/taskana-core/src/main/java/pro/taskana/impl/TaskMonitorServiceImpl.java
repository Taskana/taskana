package pro.taskana.impl;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.CategoryReport;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.CustomFieldValueReport;
import pro.taskana.report.TaskStatusReport;
import pro.taskana.report.WorkbasketReport;

/**
 * This is the implementation of TaskMonitorService.
 */
public class TaskMonitorServiceImpl implements TaskMonitorService {

    private TaskanaEngineImpl taskanaEngineImpl;
    private TaskMonitorMapper taskMonitorMapper;

    TaskMonitorServiceImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
    }

    @Override
    public WorkbasketReport.Builder createWorkbasketReportBuilder() {
        return new WorkbasketReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

    @Override
    public CategoryReport.Builder createCategoryReportBuilder() {
        return new CategoryReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

    @Override
    public ClassificationReport.Builder createClassificationReportBuilder() {
        return new ClassificationReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

    @Override
    public CustomFieldValueReport.Builder createCustomFieldValueReportBuilder(CustomField customField) {
        return new CustomFieldValueReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper, customField);
    }

    @Override
    public TaskStatusReport.Builder createTaskStatusReportBuilder() {
        return new TaskStatusReportBuilderImpl(taskanaEngineImpl, taskMonitorMapper);
    }

}
