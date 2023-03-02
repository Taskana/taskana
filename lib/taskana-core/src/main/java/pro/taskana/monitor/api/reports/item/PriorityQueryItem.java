package pro.taskana.monitor.api.reports.item;

import lombok.Getter;
import lombok.Setter;

public class PriorityQueryItem implements QueryItem {

  @Setter private String workbasketKey;
  @Setter private int count;
  @Getter @Setter private int priority;

  @Override
  public String getKey() {
    return workbasketKey;
  }

  @Override
  public int getValue() {
    return count;
  }
}
