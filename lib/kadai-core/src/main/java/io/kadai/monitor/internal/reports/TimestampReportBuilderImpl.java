package io.kadai.monitor.internal.reports;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.SelectedItem;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.Report;
import io.kadai.monitor.api.reports.TimestampReport;
import io.kadai.monitor.api.reports.TimestampReport.Builder;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.TimestampQueryItem;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import io.kadai.task.api.TaskState;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** The implementation of {@linkplain TimestampReport.Builder}. */
public class TimestampReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, TimestampQueryItem, TimeIntervalColumnHeader>
    implements TimestampReport.Builder {

  private List<TaskTimestamp> status =
      Arrays.asList(TaskTimestamp.CREATED, TaskTimestamp.COMPLETED);

  public TimestampReportBuilderImpl(InternalKadaiEngine kadaiEngine, MonitorMapper monitorMapper) {
    super(kadaiEngine, monitorMapper);
  }

  @Override
  public TimestampReport.Builder stateIn(List<TaskState> states) {
    throw new UnsupportedOperationException(
        "The states have no influence regarding this report. Use withTimestamps instead");
  }

  @Override
  public List<String> listTaskIdsForSelectedItems(
      List<SelectedItem> selectedItems, TaskTimestamp timestamp) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TimestampReport.Builder withTimestamps(List<TaskTimestamp> taskTimestamps) {
    this.status = new ArrayList<>(taskTimestamps);
    return _this();
  }

  @Override
  public Report<TimestampQueryItem, TimeIntervalColumnHeader> buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    return buildReport();
  }

  @Override
  public TimestampReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      TimestampReport report = new TimestampReport(columnHeaders);
      List<TimestampQueryItem> items =
          status.stream()
              // This can also be implemented into a single sql query which combines all statuses
              // with the union operator. That would reduce the readability of the sql template.
              // That's why "the loop" is done outside mybatis.
              .map(this::getTasksCountForStatusGroupedByOrgLevel)
              .flatMap(Collection::stream)
              .toList();

      report.addItems(
          items,
          new DaysToWorkingDaysReportPreProcessor<>(
              columnHeaders, workingTimeCalculator, inWorkingDays));
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
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
    return monitorMapper.getTasksCountForStatusGroupedByOrgLevel(Instant.now(), s, this);
  }
}
