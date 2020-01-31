package pro.taskana.report.internal.header;

import pro.taskana.report.internal.item.TaskQueryItem;
import pro.taskana.report.internal.structure.ColumnHeader;
import pro.taskana.task.api.TaskState;

/** The TaskStatusColumnHeader represents a column for each {@link TaskState}. */
public class TaskStatusColumnHeader implements ColumnHeader<TaskQueryItem> {

  private TaskState state;

  public TaskStatusColumnHeader(TaskState state) {
    this.state = state;
  }

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
