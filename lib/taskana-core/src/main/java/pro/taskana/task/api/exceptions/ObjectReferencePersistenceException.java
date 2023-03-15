package pro.taskana.task.api.exceptions;

import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;

/**
 * This exception is thrown when an {@linkplain ObjectReference} should be inserted to the DB, but
 * it does already exist. <br>
 * This may happen when a not inserted {@linkplain ObjectReference} with the same {@linkplain
 * ObjectReference#getId() id} will be added twice on a {@linkplain Task}. This can't happen if the
 * correct {@linkplain Task}-Methods will be used instead of the List ones.
 */
public class ObjectReferencePersistenceException extends TaskanaException {
  public static final String ERROR_KEY = "OBJECT_REFERENCE_ALREADY_EXISTS";
  private final String objectReferenceId;
  private final String taskId;

  public ObjectReferencePersistenceException(
      String objectReferenceId, String taskId, Throwable cause) {
    super(
        String.format(
            "Cannot insert ObjectReference with id '%s' for Task with id '%s' "
                + "because it already exists.",
            objectReferenceId, taskId),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("objectReferenceId", ensureNullIsHandled(objectReferenceId)),
                Map.entry("taskId", ensureNullIsHandled(taskId)))),
        cause);
    this.objectReferenceId = objectReferenceId;
    this.taskId = taskId;
  }

  public String getObjectReferenceId() {
    return objectReferenceId;
  }

  public String getTaskId() {
    return taskId;
  }
}
