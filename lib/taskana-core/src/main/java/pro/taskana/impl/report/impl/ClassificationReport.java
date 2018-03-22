package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.impl.report.Report;

/**
 * The ClassificationReport extends the Report. The {@link pro.taskana.impl.report.ReportRow}s of the ClassificationReport are grouped by
 * classifications.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "CLASSIFICATION KEYS");
    }

}
