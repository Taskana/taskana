package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskState;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.TimestampQueryItem;
import pro.taskana.impl.report.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.Timestamp;
import pro.taskana.report.TimestampReport;

/** The implementation of {@link TimestampReport.Builder}. */
public class TimestampReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<
        TimestampReport.Builder, TimestampQueryItem, TimeIntervalColumnHeader>
    implements TimestampReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimestampReport.Builder.class);
  private List<Timestamp> status = Arrays.asList(Timestamp.CREATED, Timestamp.COMPLETED);

  TimestampReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
    super(taskanaEngine, taskMonitorMapper);
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
  public TimestampReport.Builder withTimestamps(List<Timestamp> statuses) {
    this.status = new ArrayList<>(statuses);
    return _this();
  }

  @Override
  public TimestampReport buildReport() throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("entry to buildDetailedReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      TimestampReport report = new TimestampReport(this.columnHeaders);
      List<TimestampQueryItem> items =
          status.stream()
              // This can also be implemented into a single sql query which combines all statuses
              // with the union
              // operator. That would reduce the readability of the sql template. That's why "the
              // loop" is done
              // outside of mybatis.
              .map(this::getTasksCountForStatusGroupedByOrgLevel)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());

      report.addItems(
          items, new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
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

  private List<TimestampQueryItem> getTasksCountForStatusGroupedByOrgLevel(Timestamp s) {
    return taskMonitorMapper.getTasksCountForStatusGroupedByOrgLevel(
        s,
        categories,
        classificationIds,
        excludedClassificationIds,
        domains,
        customAttributeFilter);
  }
}
