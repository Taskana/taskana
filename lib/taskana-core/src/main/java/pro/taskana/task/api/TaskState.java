package pro.taskana.task.api;

/** This enum contains all status of the tasks. */
public enum TaskState {
  READY,
  CLAIMED,
  COMPLETED,
  CANCELLED,
  TERMINATED;

  public boolean isInStates(TaskState... states) {
    for (TaskState currState : states) {
      if (this.equals(currState)) {
        return true;
      }
    }
    return false;
  }

  public boolean isEndState() {
    return this.equals(COMPLETED) || this.equals(CANCELLED) || this.equals(TERMINATED);
  }
}
