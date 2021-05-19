package pro.taskana.monitor.api.reports.item;

import pro.taskana.task.api.TaskState;

/**
 * The TaskQueryItem entity contains the number of {@linkplain pro.taskana.task.api.models.Task
 * Tasks} for a domain which have a specific state.
 */
public class TaskQueryItem implements QueryItem {

  private String workbasketKey;
  private TaskState state;
  private int count;

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  @Override
  public String getKey() {
    return workbasketKey;
  }

  @Override
  public int getValue() {
    return count;
  }

  @Override
  public String toString() {
    return "TaskQueryItem ["
        + "domain= "
        + this.workbasketKey
        + ", state= "
        + this.state.name()
        + ", count= "
        + this.count
        + "]";
  }
}
