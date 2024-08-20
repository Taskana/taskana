package io.kadai.task.api;

/**
 * This enum contains all status of synchronization between a kadai task and a task in a remote
 * system.
 */
public enum CallbackState {
  NONE,
  CALLBACK_PROCESSING_REQUIRED,
  CLAIMED,
  CALLBACK_PROCESSING_COMPLETED
}
