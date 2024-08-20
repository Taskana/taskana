package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

/** Event fired if a task is claimed. */
public class TaskClaimedEvent extends TaskHistoryEvent {

  public TaskClaimedEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = (TaskHistoryEventType.CLAIMED.getName());
    created = task.getClaimed();
  }
}
