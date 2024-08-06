package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.TaskSummary;

public class TaskReroutedEvent extends TaskHistoryEvent {

  public TaskReroutedEvent(
      String id,
      TaskSummary task,
      String oldWorkbasketId,
      String newWorkbasketId,
      String userId,
      String details) {
    super(id, task, userId, details);
    eventType = TaskHistoryEventType.REROUTED.getName();
    this.oldValue = oldWorkbasketId;
    this.newValue = newWorkbasketId;
  }
}
