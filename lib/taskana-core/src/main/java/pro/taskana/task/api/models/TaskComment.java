package pro.taskana.task.api.models;

import java.time.Instant;

/** TaskComment-Interface to specify TaskComment Attributes. */
public interface TaskComment {

  /**
   * Gets the id of the task comment.
   *
   * @return taskId
   */
  String getId();

  /**
   * Gets the id of the associated task.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Gets the name of the task comment-creator.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Gets the long name of the task comment creator.
   *
   * @return the long Name of the creator
   */
  String getCreatorLongName();

  /**
   * Gets the text field of the task comment.
   *
   * @return textField
   */
  String getTextField();

  /**
   * Sets the text field of the task comment.
   *
   * @param textField the text field
   */
  void setTextField(String textField);

  /**
   * Gets the time when the task comment was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Gets the time when the task comment was last modified.
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
