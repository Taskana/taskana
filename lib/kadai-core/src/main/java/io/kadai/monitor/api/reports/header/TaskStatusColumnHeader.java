package io.kadai.monitor.api.reports.header;

import io.kadai.monitor.api.reports.item.TaskQueryItem;
import io.kadai.task.api.TaskState;

/** The TaskStatusColumnHeader represents a column for each {@linkplain TaskState}. */
public class TaskStatusColumnHeader implements ColumnHeader<TaskQueryItem> {

  private final TaskState state;

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
