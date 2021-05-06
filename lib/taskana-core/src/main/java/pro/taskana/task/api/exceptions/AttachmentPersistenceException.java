package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if an {@linkplain pro.taskana.task.api.models.Attachment Attachment} should be inserted to
 * the DB, but it does already exists.
 *
 * <p>This may happen when a not inserted {@linkplain pro.taskana.task.api.models.Attachment
 * Attachment} with ID will be added twice on a {@linkplain pro.taskana.task.api.models.Task Task}.
 * This can´t be happen it the correct Task-Methods will be used instead the List ones.
 */
public class AttachmentPersistenceException extends TaskanaException {

  public AttachmentPersistenceException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
