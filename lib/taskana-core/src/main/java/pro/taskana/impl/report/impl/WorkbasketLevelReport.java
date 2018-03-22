package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.impl.report.Report;

/**
 * TODO.
 */
public class WorkbasketLevelReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public WorkbasketLevelReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "WORKBASKET KEYS");
    }

}
