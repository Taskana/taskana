package pro.taskana.report.api.header;

import pro.taskana.report.api.item.TaskQueryItem;
import pro.taskana.report.api.structure.ColumnHeader;
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
