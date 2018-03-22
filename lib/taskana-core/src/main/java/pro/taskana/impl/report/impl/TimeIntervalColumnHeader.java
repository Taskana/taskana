package pro.taskana.impl.report.impl;

import pro.taskana.impl.report.ReportColumnHeader;

/**
 * A TimeIntervalColumnHeader has a lower and an upper age limit which subdivide the count of tasks into different
 * sections. Days in past are represented as negative values and days in the future are represented as positive values.
 * To avoid tasks are counted multiple times or not be listed in the report, these TimeIntervalColumnHeaders should not
 * overlap and should not have gaps. If the TimeIntervalColumnHeader should represent a single day, lowerAgeLimit and
 * upperAgeLimit have to be equal. The outer cluster of a report should have open ends. These open ends are represented
 * with Integer.MIN_VALUE and Integer.MAX_VALUE.
 */
public class TimeIntervalColumnHeader implements ReportColumnHeader<MonitorQueryItem> {

    private final int lowerAgeLimit;
    private final int upperAgeLimit;

    public TimeIntervalColumnHeader(int ageInDays) {
        this.lowerAgeLimit = ageInDays;
        this.upperAgeLimit = ageInDays;
    }

    public TimeIntervalColumnHeader(int lowerAgeLimit, int upperAgeLimit) {
        this.lowerAgeLimit = lowerAgeLimit;
        this.upperAgeLimit = upperAgeLimit;
    }

    public int getLowerAgeLimit() {
        return lowerAgeLimit;
    }

    public int getUpperAgeLimit() {
        return upperAgeLimit;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public String getDisplayName() {
        return "(" + this.lowerAgeLimit + "," + this.upperAgeLimit + ")";
    }

    @Override
    public boolean fits(MonitorQueryItem item) {
        return lowerAgeLimit <= item.getAgeInDays() && upperAgeLimit >= item.getAgeInDays();
    }
}
