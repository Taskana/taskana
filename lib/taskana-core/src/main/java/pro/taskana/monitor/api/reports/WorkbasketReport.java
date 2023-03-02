package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * A WorkbasketReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
 */
public class WorkbasketReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public WorkbasketReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"WORKBASKET"});
  }

  /** Builder for {@linkplain WorkbasketReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    WorkbasketReport buildReport() throws InvalidArgumentException, NotAuthorizedException;

    @Override
    WorkbasketReport buildReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;

    /**
     * Adds a list of {@linkplain CombinedClassificationFilter} to the builder. The created report
     * contains only tasks with a pair of a classificationId for a task and a classificationId for
     * the corresponding attachment in this list.
     *
     * @param combinedClassificationFilter a list of combinedClassificationFilter
     * @return the WorkbasketReportBuilder
     */
    WorkbasketReport.Builder combinedClassificationFilterIn(
        List<CombinedClassificationFilter> combinedClassificationFilter);
  }
}
