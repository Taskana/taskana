package pro.taskana.report;

import java.util.List;

import pro.taskana.TaskStatus;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DailyEntryExitQueryItem;
import pro.taskana.impl.report.row.DailyEntryExitRow;
import pro.taskana.report.structure.Report;

/**
 * A {@link DailyEntryExitReport} displays created and competed tasks for a specific dates.
 */
public class DailyEntryExitReport extends Report<DailyEntryExitQueryItem, TimeIntervalColumnHeader.Date> {

    public DailyEntryExitReport(List<TimeIntervalColumnHeader.Date> dates) {
        super(dates, "STATES");
    }

    @Override
    protected DailyEntryExitRow createRow(int columnSize) {
        return new DailyEntryExitRow(columnSize);
    }

    @Override
    public DailyEntryExitRow getRow(String key) {
        return (DailyEntryExitRow) super.getRow(key);
    }

    /**
     * Builder for {@link DailyEntryExitReport}.
     */
    public interface Builder extends
        TimeIntervalReportBuilder<DailyEntryExitReport.Builder, DailyEntryExitQueryItem, TimeIntervalColumnHeader.Date> {

        @Override
        DailyEntryExitReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

        Builder statusIn(List<TaskStatus> statuses);
    }
}
