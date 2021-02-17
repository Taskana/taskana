package pro.taskana.monitor.internal.reports;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
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

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskCustomFieldValueReportBuilderImpl.class);

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
      throws NotAuthorizedException, InvalidArgumentException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public TaskCustomFieldValueReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(taskCustomField = {}), this = {}", taskCustomField, this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      TaskCustomFieldValueReport report = new TaskCustomFieldValueReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfTaskCustomFieldValues(
              Instant.now(),
              this.taskCustomField,
              this.workbasketIds,
              this.states,
              this.classificationCategory,
              this.domains,
              timestamp,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);

      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, converter, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
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
