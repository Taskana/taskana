package pro.taskana.task.api;

import java.util.Arrays;

/** The TaskState contains all status of a {@linkplain pro.taskana.task.api.models.Task Task}. */
public enum TaskState {
  READY,
  CLAIMED,
  READY_FOR_REVIEW,
  IN_REVIEW,
  COMPLETED,
  CANCELLED,
  TERMINATED;

  public static final TaskState[] END_STATES = {COMPLETED, CANCELLED, TERMINATED};

  public boolean in(TaskState... states) {
    return Arrays.asList(states).contains(this);
  }

  public boolean isEndState() {
    return in(END_STATES);
  }
}
