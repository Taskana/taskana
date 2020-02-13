package pro.taskana.task.api;

import pro.taskana.task.api.models.Task;

/** This enum contains all timestamps saved in the database table for a {@link Task}. */
public enum Timestamp {
  CREATED,
  CLAIMED,
  COMPLETED,
  MODIFIED,
  PLANNED,
  DUE
}
