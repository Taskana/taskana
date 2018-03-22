package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.impl.report.Report;

/**
 * TODO.
 */
public class CustomFieldValueReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public CustomFieldValueReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "CUSTOM FIELDS");
    }
}
