package io.kadai.monitor.internal.reports;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.TaskCustomFieldValueReport;
import io.kadai.monitor.api.reports.TaskCustomFieldValueReport.Builder;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import io.kadai.task.api.TaskCustomField;
import java.time.Instant;
import java.util.List;

/** The implementation of CustomFieldValueReportBuilder. */
public class TaskCustomFieldValueReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements TaskCustomFieldValueReport.Builder {

  private final TaskCustomField taskCustomField;

  public TaskCustomFieldValueReportBuilderImpl(
      InternalKadaiEngine kadaiEngine,
      MonitorMapper monitorMapper,
      TaskCustomField taskCustomField) {
    super(kadaiEngine, monitorMapper);
    this.taskCustomField = taskCustomField;
  }

  @Override
  public TaskCustomFieldValueReport buildReport()
      throws InvalidArgumentException, NotAuthorizedException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public TaskCustomFieldValueReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      TaskCustomFieldValueReport report = new TaskCustomFieldValueReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfTaskCustomFieldValues(Instant.now(), timestamp, this);

      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
    }
  }

  @Override
  protected TaskCustomFieldValueReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return taskCustomField.name();
  }
}
