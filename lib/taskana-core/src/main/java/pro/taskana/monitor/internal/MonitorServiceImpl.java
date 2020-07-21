package pro.taskana.monitor.internal;

import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.CustomFieldValueReport;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.internal.reports.ClassificationCategoryReportBuilderImpl;
import pro.taskana.monitor.internal.reports.ClassificationReportBuilderImpl;
import pro.taskana.monitor.internal.reports.CustomFieldValueReportBuilderImpl;
import pro.taskana.monitor.internal.reports.TaskStatusReportBuilderImpl;
import pro.taskana.monitor.internal.reports.TimestampReportBuilderImpl;
import pro.taskana.monitor.internal.reports.WorkbasketReportBuilderImpl;
import pro.taskana.task.api.CustomField;

/** This is the implementation of MonitorService. */
public class MonitorServiceImpl implements MonitorService {

  private InternalTaskanaEngine taskanaEngine;
  private MonitorMapper monitorMapper;

  public MonitorServiceImpl(InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super();
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
  }

  @Override
  public WorkbasketReport.Builder createWorkbasketReportBuilder() {
    return new WorkbasketReportBuilderImpl(taskanaEngine, monitorMapper);
  }

  @Override
  public ClassificationCategoryReport.Builder createCategoryReportBuilder() {
    return new ClassificationCategoryReportBuilderImpl(taskanaEngine, monitorMapper);
  }

  @Override
  public ClassificationReport.Builder createClassificationReportBuilder() {
    return new ClassificationReportBuilderImpl(taskanaEngine, monitorMapper);
  }

  @Override
  public CustomFieldValueReport.Builder createCustomFieldValueReportBuilder(
      CustomField customField) {
    return new CustomFieldValueReportBuilderImpl(taskanaEngine, monitorMapper, customField);
  }

  @Override
  public TaskStatusReport.Builder createTaskStatusReportBuilder() {
    return new TaskStatusReportBuilderImpl(taskanaEngine, monitorMapper);
  }

  @Override
  public TimestampReport.Builder createTimestampReportBuilder() {
    return new TimestampReportBuilderImpl(taskanaEngine, monitorMapper);
  }
}
