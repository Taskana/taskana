package pro.taskana.task.api;

import java.util.Arrays;

/** This enum contains all status of the tasks. */
public enum TaskState {
  READY,
  CLAIMED,
  COMPLETED,
  CANCELLED,
  TERMINATED;

  public static final TaskState[] END_STATES = {COMPLETED, CANCELLED, TERMINATED};

  public boolean in(TaskState... states) {
    return Arrays.stream(states).anyMatch(state -> state == this);
  }

  public boolean isEndState() {
    return in(END_STATES);
  }
}
