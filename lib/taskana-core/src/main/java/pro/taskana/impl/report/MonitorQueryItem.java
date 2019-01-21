package pro.taskana.impl.report;

import pro.taskana.report.QueryItem;

/**
 * The MonitorQueryItem entity contains the number of tasks for a key (e.g. workbasketKey) and age in days.
 */
public class MonitorQueryItem implements QueryItem {

    private String key;
    private int ageInDays;
    private int numberOfTasks;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getAgeInDays() {
        return ageInDays;
    }

    public void setAgeInDays(int ageInDays) {
        this.ageInDays = ageInDays;
    }

    public int getValue() {
        return numberOfTasks;
    }

    public void setNumberOfTasks(int numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }

    @Override
    public String toString() {
        return "MonitorQueryItem [" +
            "key= " + this.key +
            ", ageInDays= " + this.ageInDays +
            ", numberOfTasks= " + this.numberOfTasks +
            "]";
    }

}
