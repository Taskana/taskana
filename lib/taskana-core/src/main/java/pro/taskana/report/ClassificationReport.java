package pro.taskana.report;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.DetailedMonitorQueryItem;
import pro.taskana.impl.report.DetailedReportRow;
import pro.taskana.impl.report.MonitorQueryItem;
import pro.taskana.impl.report.TimeIntervalColumnHeader;

/**
 * The ClassificationReport extends the Report. The {@link ReportRow}s of the ClassificationReport are grouped by
 * classifications.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "CLASSIFICATION KEYS");
    }

    /**
     * Builder for {@link ClassificationReport}.
     */
    public interface Builder extends TimeIntervalReportBuilder<Builder, TimeIntervalColumnHeader> {

        @Override
        ClassificationReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

        /**
         * Returns a {@link DetailedClassificationReport} containing all tasks after applying the filters. If the column
         * headers are set the report is subdivided into clusters. Its
         * {@link pro.taskana.impl.report.DetailedReportRow}s contain an additional list of
         * {@link ReportRow}s for the classifications of the attachments of the tasks.
         *
         * @throws InvalidArgumentException
         *             if the column headers are not initialized
         * @throws NotAuthorizedException
         *             if the user has no rights to access the monitor
         * @return the DetailedClassificationReport
         */
        DetailedClassificationReport buildDetailedReport() throws InvalidArgumentException, NotAuthorizedException;
    }

    /**
     * The DetailedClassificationReport is a functional extension of the {@link ClassificationReport}.
     * Its {@link DetailedReportRow}s contain an additional list of {@link ReportRow}s
     * for the classifications of the attachments of the tasks.
     */
    public static class DetailedClassificationReport extends Report<DetailedMonitorQueryItem, TimeIntervalColumnHeader> {

        public DetailedClassificationReport(List<TimeIntervalColumnHeader> workbasketLevelReportColumnHeaders) {
            super(workbasketLevelReportColumnHeaders, "TASK CLASSIFICATION KEYS");
        }

        @Override
        protected DetailedReportRow createReportRow(int columnSize) {
            return new DetailedReportRow(columnSize);
        }

        @Override
        public DetailedReportRow getRow(String key) {
            return (DetailedReportRow) super.getRow(key);
        }

    }
}
