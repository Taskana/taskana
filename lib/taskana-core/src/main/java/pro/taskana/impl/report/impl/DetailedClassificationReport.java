package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.impl.report.Report;

/**
 * The DetailedClassificationReport is a functional extension of the {@link ClassificationReport}.
 * Its {@link DetailedReportRow}s contain an additional list of {@link pro.taskana.impl.report.ReportRow}s
 * for the classifications of the attachments of the tasks.
 */
public class DetailedClassificationReport extends Report<DetailedMonitorQueryItem, TimeIntervalColumnHeader> {

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
