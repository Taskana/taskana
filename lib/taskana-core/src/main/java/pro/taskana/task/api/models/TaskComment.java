package pro.taskana.task.api.models;

import java.time.Instant;

/** TaskComment-Interface to specify TaskComment Attributes. */
public interface TaskComment {

  /**
   * Returns the ID of this TaskComment.
   *
   * @return taskId
   */
  String getId();

  /**
   * Returns the ID of the associated {@linkplain Task}.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Returns the name of the creator of this TaskComment.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Returns the text field of this TaskComment.
   *
   * @return textField
   */
  String getTextField();

  /**
   * Sets the text field of this TaskComment.
   *
   * @param textField the text field
   */
  void setTextField(String textField);

  /**
   * Returns the time when this TaskComment was created.
   *
   * @return the created {@linkplain Instant}
   */
  Instant getCreated();

  /**
   * Returns the time when this TaskComment was last modified.
   *
   * @return the last modified {@linkplain Instant}
   */
  Instant getModified();

  /**
   * Duplicates this TaskComment without the ID.
   *
   * @return a copy of this TaskComment
   */
  TaskComment copy();
}
