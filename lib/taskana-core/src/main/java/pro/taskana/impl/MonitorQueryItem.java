package pro.taskana.impl;

/**
 * The MonitorQueryItem entity contains the number of tasks for a key (e.g. workbasketKey) and age in days.
 */
public class MonitorQueryItem {

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

    public int getNumberOfTasks() {
        return numberOfTasks;
    }

    public void setNumberOfTasks(int numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }

}
