package pro.taskana.report.internal.item;

import pro.taskana.report.api.structure.QueryItem;
import pro.taskana.task.api.TaskState;

/**
 * The TaskQueryItem entity contains the number of tasks for a domain which have a specific state.
 */
public class TaskQueryItem implements QueryItem {

  private String domain;
  private TaskState state;
  private int count;

  public void setDomain(String domain) {
    this.domain = domain;
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
    return domain;
  }

  @Override
  public int getValue() {
    return count;
  }

  @Override
  public String toString() {
    return "TaskQueryItem ["
        + "domain= "
        + this.domain
        + ", state= "
        + this.state.name()
        + ", count= "
        + this.count
        + "]";
  }
}
