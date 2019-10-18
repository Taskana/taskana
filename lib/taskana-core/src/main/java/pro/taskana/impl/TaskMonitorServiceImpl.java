package pro.taskana.impl;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.CategoryReport;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.CustomFieldValueReport;
import pro.taskana.report.TimestampReport;
import pro.taskana.report.TaskStatusReport;
import pro.taskana.report.WorkbasketReport;

/**
 * This is the implementation of TaskMonitorService.
 */
public class TaskMonitorServiceImpl implements TaskMonitorService {

    private InternalTaskanaEngine taskanaEngine;
    private TaskMonitorMapper taskMonitorMapper;

    TaskMonitorServiceImpl(InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
    }

    @Override
    public WorkbasketReport.Builder createWorkbasketReportBuilder() {
        return new WorkbasketReportBuilderImpl(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public CategoryReport.Builder createCategoryReportBuilder() {
        return new CategoryReportBuilderImpl(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public ClassificationReport.Builder createClassificationReportBuilder() {
        return new ClassificationReportBuilderImpl(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public CustomFieldValueReport.Builder createCustomFieldValueReportBuilder(CustomField customField) {
        return new CustomFieldValueReportBuilderImpl(taskanaEngine, taskMonitorMapper, customField);
    }

    @Override
    public TaskStatusReport.Builder createTaskStatusReportBuilder() {
        return new TaskStatusReportBuilderImpl(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public TimestampReport.Builder createTimestampReportBuilder() {
        return new TimestampReportBuilderImpl(taskanaEngine, taskMonitorMapper);
    }

}
