package pro.taskana.task.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** The Task Service manages all operations on tasks. */
public interface TaskService {

  /**
   * Claim an existing task for the current user.
   *
   * @param taskId the id of the task to be claimed
   * @return claimed Task
   * @throws TaskNotFoundException if the task with taskId was not found
   * @throws InvalidStateException if the state of the task with taskId is not READY
   * @throws InvalidOwnerException if the task with taskId is claimed by some else
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task claim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Claim an existing task for the current user even if it is already claimed by someone else.
   *
   * @param taskId the id of the task to be claimed
   * @return claimed Task
   * @throws TaskNotFoundException if the task with taskId was not found
   * @throws InvalidStateException if the state of the task with taskId is not READY
   * @throws InvalidOwnerException if the task with taskId is claimed by someone else
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task forceClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancel the claim of an existing task if it was claimed by the current user before.
   *
   * @param taskId id of the task which should be unclaimed.
   * @return updated unclaimed task
   * @throws TaskNotFoundException if the task can´t be found or does not exist
   * @throws InvalidStateException when the task is already completed.
   * @throws InvalidOwnerException when the task is claimed by another user.
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task cancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancel the claim of an existing task even if it was claimed by another user.
   *
   * @param taskId id of the task which should be unclaimed.
   * @return updated unclaimed task
   * @throws TaskNotFoundException if the task can´t be found or does not exist
   * @throws InvalidStateException when the task is already completed.
   * @throws InvalidOwnerException when forceCancel is false and the task is claimed by another
   *     user.
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task forceCancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Complete a claimed Task as owner/admin and update State and Timestamps. If task is already
   * completed, the task is returned as itself.
   *
   * @param taskId - Id of the Task which should be completed.
   * @return Task - updated task after completion.
   * @throws InvalidStateException when Task wasn´t claimed before.
   * @throws TaskNotFoundException if the given Task can´t be found in DB.
   * @throws InvalidOwnerException if current user is not the task-owner or administrator.
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task completeTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Complete a Task and update State and Timestamps in every case if the Task exists. If task is
   * already completed, the task is returned as itself.
   *
   * @param taskId - Id of the Task which should be completed.
   * @return Task - updated task after completion.
   * @throws InvalidStateException when Task wasn´t claimed before.
   * @throws TaskNotFoundException if the given Task can´t be found in DB.
   * @throws InvalidOwnerException if current user is not the task-owner or administrator.
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task forceCompleteTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Persists a not persisted Task which does not exist already.
   *
   * @param taskToCreate the transient task object to be persisted
   * @return the created and persisted task
   * @throws TaskAlreadyExistException when the Task does already exist.
   * @throws NotAuthorizedException thrown if the current user is not authorized to create that task
   * @throws WorkbasketNotFoundException thrown if the work basket referenced by the task is not
   *     found
   * @throws ClassificationNotFoundException thrown if the {@link Classification} referenced by the
   *     task is not found
   * @throws InvalidArgumentException thrown if the primary ObjectReference is invalid
   */
  Task createTask(Task taskToCreate)
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException;

  /**
   * Get the details of a task by Id without checking permissions.
   *
   * @param taskId the id of the task
   * @return the Task
   * @throws TaskNotFoundException thrown of the {@link Task} with taskId is not found
   * @throws NotAuthorizedException if the current user has no READ permission for the workbasket
   *     the task is in.
   */
  Task getTask(String taskId) throws TaskNotFoundException, NotAuthorizedException;

  /**
   * Transfer a task to another work basket. The transfer sets the transferred flag and resets the
   * read flag.
   *
   * @param taskId The id of the {@link Task} to be transferred
   * @param destinationWorkbasketId The Id of the target work basket
   * @return the transferred task
   * @throws TaskNotFoundException Thrown if the {@link Task} with taskId was not found.
   * @throws WorkbasketNotFoundException Thrown if the target work basket was not found.
   * @throws NotAuthorizedException Thrown if the current user is not authorized to transfer this
   *     {@link Task} to the target work basket
   * @throws InvalidStateException Thrown if the task is in a state which does not allow
   *     transferring
   */
  Task transfer(String taskId, String destinationWorkbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Transfer a task to another work basket. The transfer sets the transferred flag and resets the
   * read flag.
   *
   * @param taskId The id of the {@link Task} to be transferred
   * @param workbasketKey The key of the target work basket
   * @param domain The domain of the target work basket
   * @return the transferred task
   * @throws TaskNotFoundException Thrown if the {@link Task} with taskId was not found.
   * @throws WorkbasketNotFoundException Thrown if the target work basket was not found.
   * @throws NotAuthorizedException Thrown if the current user is not authorized to transfer this
   *     {@link Task} to the target work basket
   * @throws InvalidStateException Thrown if the task is in a state which does not allow
   *     transferring
   */
  Task transfer(String taskId, String workbasketKey, String domain)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Marks a task as read.
   *
   * @param taskId the id of the task to be updated
   * @param isRead the new status of the read flag.
   * @return the updated Task
   * @throws TaskNotFoundException Thrown if the {@link Task} with taskId was not found
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task setTaskRead(String taskId, boolean isRead)
      throws TaskNotFoundException, NotAuthorizedException;

  /**
   * This method provides a query builder for quering the database.
   *
   * @return a {@link TaskQuery}
   */
  TaskQuery createTaskQuery();

  /**
   * Returns a not persisted instance of {@link Task}. The returned task has no workbasket Id set.
   * When createTask() is invoked for this task, TaskService will call the TaskRouting SPI to
   * determine a workbasket for the task. If the TaskRouting API is not active, e.g. because no
   * TaskRouter is registered, or the TaskRouter(s) don't find a workbasket, the task will not be
   * persisted.
   *
   * @return an empty new Task
   */
  Task newTask();

  /**
   * Returns a not persisted instance of {@link Task}.
   *
   * @param workbasketId the id of the workbasket to which the task belongs
   * @return an empty new Task
   */
  Task newTask(String workbasketId);

  /**
   * Returns a not persisted instance of {@link Task}.
   *
   * @param workbasketKey the key of the workbasket to which the task belongs
   * @param domain the domain of the workbasket to which the task belongs
   * @return an empty new Task
   */
  Task newTask(String workbasketKey, String domain);

  /**
   * Returns a not persisted instance of {@link TaskComment}.
   *
   * @param taskId The id of the task to which the task comment belongs
   * @return an empty new TaskComment
   */
  TaskComment newTaskComment(String taskId);

  /**
   * Returns a not persisted instance of {@link Attachment}.
   *
   * @return an empty new Attachment
   */
  Attachment newAttachment();

  /**
   * Update a task.
   *
   * @param task the task to be updated in the database
   * @return the updated task
   * @throws InvalidArgumentException if the task to be updated contains invalid properties like
   *     e.g. invalid object references
   * @throws TaskNotFoundException if the id of the task is not found in the database
   * @throws ConcurrencyException if the task has already been updated by another user
   * @throws ClassificationNotFoundException if the updated task refers to a classification that
   *     cannot be found
   * @throws NotAuthorizedException if the current user is not authorized to update the task
   * @throws AttachmentPersistenceException if an Attachment with ID will be added multiple times
   *     without using the task-methods
   * @throws InvalidStateException if an attempt is made to change the owner of the task and the
   *     task is not in state READY .
   */
  Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException;

  /**
   * Transfers a list of tasks to an other workbasket. Exceptions will be thrown if the caller got
   * no permissions on the target or it doesn´t exist. Other Exceptions will be stored and returned
   * in the end.
   *
   * @param destinationWorkbasketId target workbasket id
   * @param taskIds source task which will be moved
   * @return Bulkresult with ID and Error in it for failed transactions.
   * @throws NotAuthorizedException if the caller hasn´t permissions on tarket WB.
   * @throws InvalidArgumentException if the method paramesters are EMPTY or NULL.
   * @throws WorkbasketNotFoundException if the target WB can´t be found.
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Transfers a list of tasks to an other workbasket. Exceptions will be thrown if the caller got
   * no permissions on the target or it doesn´t exist. Other Exceptions will be stored and returned
   * in the end.
   *
   * @param destinationWorkbasketKey target workbasket key
   * @param destinationWorkbasketDomain target workbasket domain
   * @param taskIds source task which will be moved
   * @return Bulkresult with ID and Error in it for failed transactions.
   * @throws NotAuthorizedException if the caller hasn´t permissions on tarket WB.
   * @throws InvalidArgumentException if the method paramesters are EMPTY or NULL.
   * @throws WorkbasketNotFoundException if the target WB can´t be found.
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey, String destinationWorkbasketDomain, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Deletes the task with the given Id.
   *
   * @param taskId The Id of the task to delete.
   * @throws TaskNotFoundException If the given Id does not refer to an existing task.
   * @throws InvalidStateException If the state of the referenced task is not Completed.
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  void deleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes the task with the given Id even if it is not completed.
   *
   * @param taskId The Id of the task to delete.
   * @throws TaskNotFoundException If the given Id does not refer to an existing task.
   * @throws InvalidStateException If the state of the referenced task is not Completed and
   *     forceDelet is false.
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  void forceDeleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes a list of tasks.
   *
   * @param tasks the ids of the tasks to delete.
   * @return the result of the operations with Id and Exception for each failed task deletion.
   * @throws InvalidArgumentException if the TaskIds parameter is NULL
   */
  BulkOperationResults<String, TaskanaException> deleteTasks(List<String> tasks)
      throws InvalidArgumentException;

  /**
   * Completes a list of tasks.
   *
   * @param taskIds of the tasks which should be completed.
   * @return the result of the operations with Id and Exception for each failed task completion.
   * @throws InvalidArgumentException If the taskId parameter is NULL.
   */
  BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Updates tasks with a matching {@link ObjectReference}.
   *
   * @param selectionCriteria the {@link ObjectReference} that is used to select the tasks.
   * @param customFieldsToUpdate a {@link Map} that contains as key the identification of the custom
   *     field and as value the corresponding new value of that custom field. The key for
   *     identification of the custom field must be a String with value "1", "2" ... "16" as in the
   *     setCustomAttribute or getCustomAttribute method of {@link Task}
   * @return a list of the Ids of all modified tasks
   * @throws InvalidArgumentException If the customFieldsToUpdate map contains an invalid key or if
   *     the selectionCriteria is invalid
   */
  List<String> updateTasks(
      ObjectReference selectionCriteria, Map<String, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Updates tasks with matching taskIds.
   *
   * @param taskIds the taskIds that are used to select the tasks.
   * @param customFieldsToUpdate a {@link Map} that contains as key the identification of the custom
   *     field and as value the corresponding new value of that custom field. The key for
   *     identification of the custom field must be a String with value "1", "2" ... "16" as in the
   *     setCustomAttribute or getCustomAttribute method of {@link Task}
   * @return a list of the Ids of all modified tasks
   * @throws InvalidArgumentException If the customFieldsToUpdate map contains an invalid key or if
   *     the selectionCriteria is invalid
   */
  List<String> updateTasks(List<String> taskIds, Map<String, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Create a task comment.
   *
   * @param taskComment the task comment to be created.
   * @return the created task comment.
   * @throws NotAuthorizedException If the current user has no authorization to create a task
   *     comment for the given taskId in the TaskComment or is not authorized to access the task.
   * @throws TaskNotFoundException If the given taskId in the TaskComment does not refer to an
   *     existing task.
   * @throws InvalidArgumentException If the given taskCommentId from the provided task comment is
   *     not null or empty
   */
  TaskComment createTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException;

  /**
   * Update a task comment.
   *
   * @param taskComment the task comment to be updated in the database.
   * @return the updated task comment.
   * @throws NotAuthorizedException If the current user has no authorization to update a task
   *     comment or is not authorized to access the task.
   * @throws ConcurrencyException if an attempt is made to update the task comment and another user.
   *     updated it already.
   * @throws TaskCommentNotFoundException If the given taskCommentId in the TaskComment does not
   *     refer to an existing taskComment.
   * @throws TaskNotFoundException If the given taskId in the TaskComment does not refer to an
   *     existing task.
   * @throws InvalidArgumentException If the given taskCommentId from the provided task comment is
   *     null or empty
   */
  TaskComment updateTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException, InvalidArgumentException;

  /**
   * Deletes the task comment with the given Id.
   *
   * @param taskCommentId The id of the task comment to delete.
   * @throws NotAuthorizedException If the current user has no authorization to delete a task
   *     comment or is not authorized to access the task.
   * @throws InvalidArgumentException If the taskCommentId is null/empty
   * @throws TaskCommentNotFoundException If the given taskCommentId in the TaskComment does not
   *     refer to an existing taskComment.
   * @throws TaskNotFoundException If the given taskId in the TaskComment does not refer to an
   *     existing task.
   * @throws InvalidArgumentException If the given taskCommentId is null or empty
   */
  void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves a task comment for a given taskCommentId.
   *
   * @param taskCommentId The id of the task comment which should be retrieved
   * @return the task comment identified by taskCommentId
   * @throws TaskCommentNotFoundException If the given taskCommentId in the TaskComment does not
   *     refer to an existing taskComment.
   * @throws NotAuthorizedException If the current user has no authorization to retrieve a
   *     taskComment from a certain task or is not authorized to access the task.
   * @throws TaskNotFoundException If the given taskId in the TaskComment does not refer to an
   *     existing task.
   * @throws InvalidArgumentException If the given taskCommentId is null or empty
   */
  TaskComment getTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves a list of task comments for a given taskId.
   *
   * @param taskId The id of the task for which all task comments should be retrieved
   * @return the list of task comments attached to task with id taskId
   * @throws NotAuthorizedException If the current user has no authorization to retrieve a
   *     taskComment from a certain task or is not authorized to access the task.
   * @throws TaskNotFoundException If the given taskId in the TaskComment does not refer to an
   *     existing task.
   */
  List<TaskComment> getTaskComments(String taskId)
      throws NotAuthorizedException, TaskNotFoundException;

  /**
   * Sets the callback state on a list of tasks. Note: this method is primarily intended to be used
   * by the TaskanaAdapter
   *
   * @param externalIds the EXTERNAL_IDs of the tasks on which the callback state is set.
   * @param state the callback state that is to be set on the tasks
   * @return the result of the operations with Id and Exception for each failed task deletion.
   */
  BulkOperationResults<String, TaskanaException> setCallbackStateForTasks(
      List<String> externalIds, CallbackState state);

  /**
   * Sets the owner on a list of tasks. The owner will only be set on tasks that are in state READY.
   *
   * @param owner the new owner of the tasks
   * @param taskIds the IDs of the tasks on which the owner is to be set.
   * @return the result of the operations with Id and Exception for each failed task update.
   */
  BulkOperationResults<String, TaskanaException> setOwnerOfTasks(
      String owner, List<String> taskIds);

  /**
   * Sets the planned property on a list of tasks. Only tasks in state READY and CLAIMED will be
   * affected by this method. On each task, the corresponding due date is set according to the
   * shortest service level in the classifications of the task and the task's attachments.
   *
   * @param planned the new 'PLANNED" property of the tasks
   * @param taskIds the IDs of the tasks on which the new planned property is to be set.
   * @return the result of the operations with Id and Exception for each failed task update.
   */
  BulkOperationResults<String, TaskanaException> setPlannedPropertyOfTasks(
      Instant planned, List<String> taskIds);
}
