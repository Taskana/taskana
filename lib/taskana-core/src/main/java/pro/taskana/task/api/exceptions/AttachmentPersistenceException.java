package pro.taskana.task.api.exceptions;

import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.Task;

/**
 * This exception is thrown when an {@linkplain Attachment} should be inserted to the DB, but it
 * does already exist. <br>
 * This may happen when a not inserted {@linkplain Attachment} with the same {@linkplain
 * Attachment#getId() id} will be added twice on a {@linkplain Task}. This can't happen if the
 * correct {@linkplain Task}-Methods will be used instead of the List ones.
 */
public class AttachmentPersistenceException extends TaskanaException {
  public static final String ERROR_KEY = "ATTACHMENT_ALREADY_EXISTS";
  private final String attachmentId;
  private final String taskId;

  public AttachmentPersistenceException(String attachmentId, String taskId, Throwable cause) {
    super(
        String.format(
            "Cannot insert Attachment with id '%s' for Task with id '%s' "
                + "because it already exists.",
            attachmentId, taskId),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("attachmentId", ensureNullIsHandled(attachmentId)),
                Map.entry("taskId", ensureNullIsHandled(taskId)))),
        cause);
    this.attachmentId = attachmentId;
    this.taskId = taskId;
  }

  public String getAttachmentId() {
    return attachmentId;
  }

  public String getTaskId() {
    return taskId;
  }
}
