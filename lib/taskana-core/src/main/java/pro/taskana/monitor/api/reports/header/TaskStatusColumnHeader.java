package pro.taskana.monitor.api.reports.header;

import lombok.AllArgsConstructor;
import lombok.Getter;

import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.task.api.TaskState;

/** The TaskStatusColumnHeader represents a column for each {@linkplain TaskState}. */
@Getter
@AllArgsConstructor
public class TaskStatusColumnHeader implements ColumnHeader<TaskQueryItem> {

  private final TaskState state;

  @Override
  public String getDisplayName() {
    return this.state.name();
  }

  @Override
  public boolean fits(TaskQueryItem item) {
    return item.getState() == this.state;
  }

  @Override
  public String toString() {
    return getDisplayName();
  }
}
