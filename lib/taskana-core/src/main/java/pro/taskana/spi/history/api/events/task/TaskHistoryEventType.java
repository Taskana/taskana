package pro.taskana.spi.history.api.events.task;

public enum TaskHistoryEventType {
  CREATED("CREATED"),
  UPDATED("UPDATED"),
  CLAIMED("CLAIMED"),
  CLAIM_CANCELLED("CLAIM_CANCELLED"),
  REQUESTED_REVIEW("REQUESTED_REVIEW"),
  CHANGES_REQUESTED("CHANGES_REQUESTED"),
  COMPLETED("COMPLETED"),
  CANCELLED("CANCELLED"),
  TERMINATED("TERMINATED"),
  TRANSFERRED("TRANSFERRED");

  private String name;

  TaskHistoryEventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
