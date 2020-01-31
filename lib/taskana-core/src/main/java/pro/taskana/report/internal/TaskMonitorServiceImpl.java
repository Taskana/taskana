package pro.taskana.report.internal;

import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.report.api.CategoryReport;
import pro.taskana.report.api.ClassificationReport;
import pro.taskana.report.api.CustomFieldValueReport;
import pro.taskana.report.api.TaskMonitorService;
import pro.taskana.report.api.TaskStatusReport;
import pro.taskana.report.api.TimestampReport;
import pro.taskana.report.api.WorkbasketReport;
import pro.taskana.task.api.CustomField;

/** This is the implementation of TaskMonitorService. */
public class TaskMonitorServiceImpl implements TaskMonitorService {

  private InternalTaskanaEngine taskanaEngine;
  private TaskMonitorMapper taskMonitorMapper;

  public TaskMonitorServiceImpl(
      InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
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
  public CustomFieldValueReport.Builder createCustomFieldValueReportBuilder(
      CustomField customField) {
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
