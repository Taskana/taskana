package pro.taskana.monitor.api.reports.item;

public class PriorityQueryItem implements QueryItem {

  private String workbasketKey;
  private int count;
  private int priority;

  public int getPriority() {
    return priority;
  }

  @Override
  public String getKey() {
    return workbasketKey;
  }

  @Override
  public int getValue() {
    return count;
  }
}
