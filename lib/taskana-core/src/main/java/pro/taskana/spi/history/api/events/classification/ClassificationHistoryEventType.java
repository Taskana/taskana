package pro.taskana.spi.history.api.events.classification;

public enum ClassificationHistoryEventType {
  CREATED("CREATED"),
  UPDATED("UPDATED"),
  DELETED("DELETED");

  private String name;

  ClassificationHistoryEventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
