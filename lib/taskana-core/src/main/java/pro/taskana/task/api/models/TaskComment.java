package pro.taskana.task.api.models;

import java.time.Instant;

/** TaskComment-Interface to specify attributes of the TaskComment. */
public interface TaskComment {

  /**
   * Returns the id of the TaskComment.
   *
   * @return taskId
   */
  String getId();

  /**
   * Returns the id of the associated Task.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Returns the id of the creator of the TaskComment.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Returns the long name of the creator of the TaskComment.
   *
   * @return the long name of the creator
   */
  String getCreatorFullName();

  /**
   * Returns the content of the TaskComment.
   *
   * @return textField
   */
  String getTextField();

  /**
   * Sets the content of the TaskComment.
   *
   * @param textField the textField
   */
  void setTextField(String textField);

  /**
   * Returns the time when the TaskComment was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the TaskComment was last modified.
   *
   * @return the last modified Instant
   */
  Instant getModified();

  /**
   * Duplicates this TaskComment without the id.
   *
   * @return a copy of this TaskComment
   */
  TaskComment copy();
}
