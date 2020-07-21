package pro.taskana.monitor.internal.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.TimestampReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import pro.taskana.task.api.TaskState;

/** The implementation of {@link TimestampReport.Builder}. */
public class TimestampReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, TimestampQueryItem, TimeIntervalColumnHeader>
    implements TimestampReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimestampReportBuilderImpl.class);
  private List<TaskTimestamp> status =
      Arrays.asList(TaskTimestamp.CREATED, TaskTimestamp.COMPLETED);

  public TimestampReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
  }

  @Override
  public TimestampReport.Builder stateIn(List<TaskState> states) {
    throw new UnsupportedOperationException(
        "The states have no influence regarding this report. Use withTimestamps instead");
  }

  @Override
  public List<String> listTaskIdsForSelectedItems(List<SelectedItem> selectedItems) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TimestampReport.Builder withTimestamps(List<TaskTimestamp> statuses) {
    this.status = new ArrayList<>(statuses);
    return _this();
  }

  @Override
  public Report<TimestampQueryItem, TimeIntervalColumnHeader> buildReport(TaskTimestamp timestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    return buildReport();
  }

  @Override
  public TimestampReport buildReport() throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("entry to buildDetailedReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      TimestampReport report = new TimestampReport(columnHeaders);
      List<TimestampQueryItem> items =
          status.stream()
              // This can also be implemented into a single sql query which combines all statuses
              // with the union operator. That would reduce the readability of the sql template.
              // That's why "the loop" is done outside of mybatis.
              .map(this::getTasksCountForStatusGroupedByOrgLevel)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());

      report.addItems(
          items,
          new DaysToWorkingDaysReportPreProcessor<>(columnHeaders, converter, inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildDetailedReport().");
    }
  }

  @Override
  protected TimestampReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    throw new UnsupportedOperationException();
  }

  private List<TimestampQueryItem> getTasksCountForStatusGroupedByOrgLevel(TaskTimestamp s) {
    return monitorMapper.getTasksCountForStatusGroupedByOrgLevel(
        s,
        classificationCategory,
        classificationIds,
        excludedClassificationIds,
        domains,
        customAttributeFilter);
  }
}
