package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

/** The TaskRequestChangesEvent is fired if changes on a {@linkplain Task} are requested. */
public class TaskRequestChangesEvent extends TaskHistoryEvent {

  public TaskRequestChangesEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = (TaskHistoryEventType.CHANGES_REQUESTED.getName());
    created = task.getModified();
  }
}
