package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;

/**
 * A CategoryReport contains the total numbers of tasks of the respective category as well as the
 * total number of all tasks. The tasks of the report can be filtered by workbaskets, states,
 * categories, domains, classifications and values of a custom field. Classifications can also be
 * excluded from the report. If the {@link TimeIntervalColumnHeader}s are set, the report contains
 * also the number of tasks of the respective cluster. The age of the tasks can be counted in days
 * or in working days. Tasks with Timestamp DUE = null are not considered.
 */
public class ClassificationCategoryReport
    extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationCategoryReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION CATEGORIES"});
  }

  /** Builder for {@link ClassificationCategoryReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationCategoryReport buildReport()
        throws NotAuthorizedException, InvalidArgumentException;

    @Override
    ClassificationCategoryReport buildReport(TaskTimestamp timestamp)
        throws NotAuthorizedException, InvalidArgumentException;
  }
}
