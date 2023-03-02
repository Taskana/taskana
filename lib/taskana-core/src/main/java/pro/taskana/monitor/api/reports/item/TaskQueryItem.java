package pro.taskana.monitor.api.reports.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.task.api.TaskState;

/**
 * The TaskQueryItem entity contains the number of tasks for a domain which have a specific state.
 */
@ToString
public class TaskQueryItem implements QueryItem {

  @Setter private String workbasketKey;
  @Getter @Setter private TaskState state;
  @Setter private int count;

  @Override
  public String getKey() {
    return workbasketKey;
  }

  @Override
  public int getValue() {
    return count;
  }
}
