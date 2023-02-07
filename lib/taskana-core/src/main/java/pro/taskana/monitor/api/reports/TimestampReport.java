package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.Interval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.monitor.api.reports.row.TimestampRow;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * A TimestampReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain FoldableRow} represents a {@linkplain TaskTimestamp} and can be expanded to
 * display the four organization levels of the corresponding {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain Interval}.
 */
public class TimestampReport extends Report<TimestampQueryItem, TimeIntervalColumnHeader> {

  public TimestampReport(List<TimeIntervalColumnHeader> dates) {
    super(
        dates, new String[] {"STATES", "ORG LEVEL 1", "ORG LEVEL 2", "ORG LEVEL 3", "ORG LEVEL 4"});
  }

  @Override
  public TimestampRow getRow(String key) {
    return (TimestampRow) super.getRow(key);
  }

  @Override
  protected TimestampRow createRow(String key, int columnSize) {
    return new TimestampRow(key, columnSize);
  }

  /** Builder for {@linkplain TimestampReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<
          TimestampReport.Builder, TimestampQueryItem, TimeIntervalColumnHeader> {

    @Override
    TimestampReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    Builder withTimestamps(List<TaskTimestamp> taskTimestamps);
  }
}
