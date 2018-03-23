package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.impl.report.Report;

/**
 * TODO.
 */
public class CategoryReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public CategoryReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "CLASSIFICATION CATEGORIES");
    }

}
