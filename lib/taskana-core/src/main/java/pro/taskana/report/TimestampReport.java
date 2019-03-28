package pro.taskana.report;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.TimestampQueryItem;
import pro.taskana.impl.report.row.TimestampRow;
import pro.taskana.impl.report.structure.Report;

/**
 * A {@link TimestampReport} displays created and competed tasks for a specific dates.
 */
public class TimestampReport extends Report<TimestampQueryItem, TimeIntervalColumnHeader.Date> {

    public TimestampReport(List<TimeIntervalColumnHeader.Date> dates) {
        super(dates, "STATES");
    }

    @Override
    protected TimestampRow createRow(int columnSize) {
        return new TimestampRow(columnSize);
    }

    @Override
    public TimestampRow getRow(String key) {
        return (TimestampRow) super.getRow(key);
    }

    /**
     * Builder for {@link TimestampReport}.
     */
    public interface Builder extends
        TimeIntervalReportBuilder<TimestampReport.Builder, TimestampQueryItem, TimeIntervalColumnHeader.Date> {

        @Override
        TimestampReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

        Builder withTimestamps(List<Timestamp> statuses);
    }
}
