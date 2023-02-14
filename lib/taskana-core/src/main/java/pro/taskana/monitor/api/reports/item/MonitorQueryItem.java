package pro.taskana.monitor.api.reports.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The MonitorQueryItem entity contains the number of tasks for a key (e.g. workbasketKey) and age
 * in days.
 */
@Getter
@Setter
@ToString
public class MonitorQueryItem implements AgeQueryItem {

  private String key;
  private int ageInDays;
  private int numberOfTasks;

  @Override
  public int getValue() {
    return numberOfTasks;
  }
}
