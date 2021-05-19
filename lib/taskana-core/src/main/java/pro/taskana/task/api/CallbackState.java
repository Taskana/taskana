package pro.taskana.task.api;

/**
 * This Enum contains all states of synchronization between a Taskana task and a task in a remote
 * system.
 */
public enum CallbackState {
  NONE,
  CALLBACK_PROCESSING_REQUIRED,
  CLAIMED,
  CALLBACK_PROCESSING_COMPLETED
}
