package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.impl.report.Report;

/**
 * TODO.
 */
public class WorkbasketReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public WorkbasketReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "WORKBASKET KEYS");
    }

}
