package io.kadai.monitor.api;

import io.kadai.task.api.models.Task;

/** This enum contains all timestamps saved in the database table for a {@linkplain Task}. */
public enum TaskTimestamp {
  CREATED,
  CLAIMED,
  COMPLETED,
  MODIFIED,
  PLANNED,
  DUE
}
