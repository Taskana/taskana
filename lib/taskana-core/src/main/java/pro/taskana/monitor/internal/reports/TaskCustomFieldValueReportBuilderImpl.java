package pro.taskana.monitor.internal.reports;

import java.time.Instant;
import java.util.List;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.TaskCustomFieldValueReport;
import pro.taskana.monitor.api.reports.TaskCustomFieldValueReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import pro.taskana.task.api.TaskCustomField;

/** The implementation of CustomFieldValueReportBuilder. */
public class TaskCustomFieldValueReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements TaskCustomFieldValueReport.Builder {

  private final TaskCustomField taskCustomField;

  public TaskCustomFieldValueReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine,
      MonitorMapper monitorMapper,
      TaskCustomField taskCustomField) {
    super(taskanaEngine, monitorMapper);
    this.taskCustomField = taskCustomField;
  }

  @Override
  public TaskCustomFieldValueReport buildReport()
      throws InvalidArgumentException, MismatchedRoleException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public TaskCustomFieldValueReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, MismatchedRoleException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      TaskCustomFieldValueReport report = new TaskCustomFieldValueReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfTaskCustomFieldValues(Instant.now(), timestamp, this);

      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, converter, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
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
