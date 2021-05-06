package pro.taskana.monitor.api;

import pro.taskana.task.api.models.Task;

/** This enum contains all Timestamps saved in the database table for a {@linkplain Task}. */
public enum TaskTimestamp {
  CREATED,
  CLAIMED,
  COMPLETED,
  MODIFIED,
  PLANNED,
  DUE
}
