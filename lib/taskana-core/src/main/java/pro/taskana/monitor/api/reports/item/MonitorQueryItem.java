package pro.taskana.monitor.api.reports.item;

/**
 * The MonitorQueryItem entity contains the number of {@linkplain pro.taskana.task.api.models.Task
 * Tasks} for a key (e.g. workbasketKey) and age in days.
 */
public class MonitorQueryItem implements AgeQueryItem {

  private String key;
  private int ageInDays;
  private int numberOfTasks;

  @Override
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public int getValue() {
    return numberOfTasks;
  }

  @Override
  public int getAgeInDays() {
    return ageInDays;
  }

  @Override
  public void setAgeInDays(int ageInDays) {
    this.ageInDays = ageInDays;
  }

  public void setNumberOfTasks(int numberOfTasks) {
    this.numberOfTasks = numberOfTasks;
  }

  @Override
  public String toString() {
    return String.format(
        "MonitorQueryItem [key= %s, ageInDays= %d, numberOfTasks= %d]",
        key, ageInDays, numberOfTasks);
  }
}
