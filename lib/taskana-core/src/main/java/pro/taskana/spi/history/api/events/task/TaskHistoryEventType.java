package pro.taskana.spi.history.api.events.task;

public enum TaskHistoryEventType {
  TASK_CREATED("TASK_CREATED"),
  TASK_UPDATED("TASK_UPDATED"),
  TASK_CLAIMED("TASK_CLAIMED"),
  TASK_CLAIM_CANCELLED("TASK_CLAIM_CANCELLED"),
  TASK_COMPLETED("TASK_COMPLETED"),
  TASK_CANCELLED("TASK_CANCELLED"),
  TASK_TERMINATED("TASK_TERMINATED"),
  TASK_TRANSFERRED("TASK_TRANSFERRED");

  private String name;

  TaskHistoryEventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
