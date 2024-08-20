package io.kadai.monitor.internal;

import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.MonitorService;
import io.kadai.monitor.api.reports.ClassificationCategoryReport;
import io.kadai.monitor.api.reports.ClassificationReport;
import io.kadai.monitor.api.reports.TaskCustomFieldValueReport;
import io.kadai.monitor.api.reports.TaskStatusReport;
import io.kadai.monitor.api.reports.TimestampReport;
import io.kadai.monitor.api.reports.WorkbasketPriorityReport;
import io.kadai.monitor.api.reports.WorkbasketReport;
import io.kadai.monitor.internal.reports.ClassificationCategoryReportBuilderImpl;
import io.kadai.monitor.internal.reports.ClassificationReportBuilderImpl;
import io.kadai.monitor.internal.reports.TaskCustomFieldValueReportBuilderImpl;
import io.kadai.monitor.internal.reports.TaskStatusReportBuilderImpl;
import io.kadai.monitor.internal.reports.TimestampReportBuilderImpl;
import io.kadai.monitor.internal.reports.WorkbasketPriorityReportBuilderImpl;
import io.kadai.monitor.internal.reports.WorkbasketReportBuilderImpl;
import io.kadai.task.api.TaskCustomField;

/** This is the implementation of MonitorService. */
public class MonitorServiceImpl implements MonitorService {

  private final InternalKadaiEngine kadaiEngine;
  private final MonitorMapper monitorMapper;

  public MonitorServiceImpl(InternalKadaiEngine kadaiEngine, MonitorMapper monitorMapper) {
    super();
    this.kadaiEngine = kadaiEngine;
    this.monitorMapper = monitorMapper;
  }

  @Override
  public WorkbasketReport.Builder createWorkbasketReportBuilder() {
    return new WorkbasketReportBuilderImpl(kadaiEngine, monitorMapper);
  }

  @Override
  public WorkbasketPriorityReport.Builder createWorkbasketPriorityReportBuilder() {
    return new WorkbasketPriorityReportBuilderImpl(kadaiEngine, monitorMapper);
  }

  @Override
  public ClassificationCategoryReport.Builder createClassificationCategoryReportBuilder() {
    return new ClassificationCategoryReportBuilderImpl(kadaiEngine, monitorMapper);
  }

  @Override
  public ClassificationReport.Builder createClassificationReportBuilder() {
    return new ClassificationReportBuilderImpl(kadaiEngine, monitorMapper);
  }

  @Override
  public TaskCustomFieldValueReport.Builder createTaskCustomFieldValueReportBuilder(
      TaskCustomField taskCustomField) {
    return new TaskCustomFieldValueReportBuilderImpl(kadaiEngine, monitorMapper, taskCustomField);
  }

  @Override
  public TaskStatusReport.Builder createTaskStatusReportBuilder() {
    return new TaskStatusReportBuilderImpl(kadaiEngine, monitorMapper);
  }

  @Override
  public TimestampReport.Builder createTimestampReportBuilder() {
    return new TimestampReportBuilderImpl(kadaiEngine, monitorMapper);
  }
}
