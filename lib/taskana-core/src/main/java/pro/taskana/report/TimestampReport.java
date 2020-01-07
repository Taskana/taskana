package pro.taskana.report;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.TimestampQueryItem;
import pro.taskana.impl.report.row.TimestampRow;
import pro.taskana.impl.report.structure.Report;

/** A {@link TimestampReport} displays created and competed tasks for a specific dates. */
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
  protected TimestampRow createRow(int columnSize) {
    return new TimestampRow(columnSize);
  }

  /** Builder for {@link TimestampReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<
          TimestampReport.Builder, TimestampQueryItem, TimeIntervalColumnHeader> {

    @Override
    TimestampReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    Builder withTimestamps(List<Timestamp> statuses);
  }
}
