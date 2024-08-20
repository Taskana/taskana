package io.kadai.monitor.api.reports;

import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.header.ColumnHeader;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.TimestampQueryItem;
import io.kadai.monitor.api.reports.row.FoldableRow;
import io.kadai.monitor.api.reports.row.TimestampRow;
import io.kadai.task.api.models.Task;
import io.kadai.workbasket.api.models.Workbasket;
import java.util.List;

/**
 * A TimestampReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain FoldableRow} represents a {@linkplain TaskTimestamp} and can be expanded to
 * display the four organization levels of the corresponding {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
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
    TimestampReport buildReport() throws InvalidArgumentException, NotAuthorizedException;

    Builder withTimestamps(List<TaskTimestamp> taskTimestamps);
  }
}
