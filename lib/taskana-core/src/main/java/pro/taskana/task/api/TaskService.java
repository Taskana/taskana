package pro.taskana.task.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
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
   * @throws TaskNotFoundException if the task can't be found or does not exist
   * @throws InvalidStateException if the task is already in an end state.
   * @throws InvalidOwnerException if the task is claimed by another user.
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
   * @throws TaskNotFoundException if the task can't be found or does not exist
   * @throws InvalidStateException if the task is already in an end state.
   * @throws InvalidOwnerException if forceCancel is false and the task is claimed by another user.
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
   * @throws InvalidStateException if Task wasn't claimed before.
   * @throws TaskNotFoundException if the given Task can't be found in DB.
   * @throws InvalidOwnerException if current user is not the task-owner or administrator.
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task completeTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Completes a Task and updates State and Timestamps in every case if the Task exists. If task is
   * already completed, the task is returned as itself.
   *
   * @param taskId - Id of the Task which should be completed.
   * @return Task - updated task after completion.
   * @throws InvalidStateException if Task wasn't claimed before.
   * @throws TaskNotFoundException if the given Task can't be found in DB.
   * @throws InvalidOwnerException if current user is not the task-owner or administrator.
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task forceCompleteTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Inserts a not existing Task. <br>
   * The default values of the created Task are:
   *
   * <ul>
   *   <li><b>id</b> - generated by {@linkplain pro.taskana.common.internal.util.IdGenerator
   *       IdGenerator}
   *   <li><b>externalId</b> - generated by IdGenerator
   *   <li><b>businessProcessId</b> - generated by IdGenerator
   *   <li><b>name</b> - name of its Classification
   *   <li><b>description</b> - description of its Classification
   *   <li><b>creator</b> - id of current user
   *   <li><b>state</b> - 'READY'
   *   <li><b>isRead</b> - {@code false}
   *   <li><b>isTransferred</b> - {@code false}
   * </ul>
   *
   * @param taskToCreate the transient task object to be inserted
   * @return the created and inserted task
   * @throws TaskAlreadyExistException if the Task does already exist.
   * @throws NotAuthorizedException thrown if the current user is not authorized to create that task
   * @throws WorkbasketNotFoundException thrown if the workbasket referenced by the task is not
   *     found
   * @throws ClassificationNotFoundException thrown if the Classification referenced by the task is
   *     not found
   * @throws InvalidArgumentException thrown if the primary ObjectReference is invalid
   * @throws AttachmentPersistenceException if an Attachment with ID will be added multiple times
   *     without using the task-methods
   * @throws ObjectReferencePersistenceException if an ObjectReference with ID will be added
   *     multiple times without using the task-methods
   */
  Task createTask(Task taskToCreate)
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException,
          ObjectReferencePersistenceException;

  /**
   * Gets the details of a task by Id without checking permissions.
   *
   * @param taskId the id of the task
   * @return the Task
   * @throws TaskNotFoundException thrown of the {@link Task} with taskId is not found
   * @throws NotAuthorizedException if the current user has no READ permission for the workbasket
   *     the task is in.
   */
  Task getTask(String taskId) throws TaskNotFoundException, NotAuthorizedException;

  /**
   * Transfers a {@linkplain Task} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} while always setting the {@linkplain
   * Task#isTransferred transfer} flag.
   *
   * @see #transfer(String, String, boolean)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default Task transfer(String taskId, String destinationWorkbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    return transfer(taskId, destinationWorkbasketId, true);
  }

  /**
   * Transfers a {@linkplain Task} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket}.
   *
   * <p>The transfer resets the read flag and sets the transfer flag if {@code setTransferFlag} is
   * {@code true}.
   *
   * @param taskId the id of the {@linkplain Task} which should be transferred
   * @param destinationWorkbasketId the id of the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket}
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return the transferred {@linkplain Task}
   * @throws TaskNotFoundException Thrown if the {@linkplain Task} with taskId was not found.
   * @throws WorkbasketNotFoundException Thrown if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} was not found.
   * @throws NotAuthorizedException Thrown if the current user is not authorized to transfer this
   *     {@linkplain Task} to the target {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket}
   * @throws InvalidStateException Thrown if the {@linkplain Task} is in a state which does not
   *     allow transferring
   */
  Task transfer(String taskId, String destinationWorkbasketId, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Transfers a {@linkplain Task} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} while always setting the {@linkplain
   * Task#isTransferred transfer} flag.
   *
   * @see #transfer(String, String, String, boolean)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default Task transfer(String taskId, String workbasketKey, String domain)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    return transfer(taskId, workbasketKey, domain, true);
  }

  /**
   * Transfers a {@linkplain Task} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket}.
   *
   * <p>The transfer resets the read flag and sets the transfer flag if {@code setTransferFlag} is
   * {@code true}.
   *
   * @param taskId the id of the {@linkplain Task} which should be transferred
   * @param workbasketKey the key of the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket}
   * @param domain the domain of the target {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket}
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return the transferred {@linkplain Task}
   * @throws TaskNotFoundException Thrown if the {@link Task} with taskId was not found.
   * @throws WorkbasketNotFoundException Thrown if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} was not found.
   * @throws NotAuthorizedException Thrown if the current user is not authorized to transfer this
   *     {@linkplain Task} to the target Workbasket
   * @throws InvalidStateException Thrown if the {@linkplain Task} is in a state which does not
   *     allow transferring
   */
  Task transfer(String taskId, String workbasketKey, String domain, boolean setTransferFlag)
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
   * This method provides a query builder for querying the database.
   *
   * @return a {@link TaskQuery}
   */
  TaskQuery createTaskQuery();

  /**
   * This method provides a query builder for querying the database.
   *
   * @return a {@link TaskCommentQuery}
   */
  TaskCommentQuery createTaskCommentQuery();

  /**
   * Returns a not inserted instance of {@linkplain Task}. The Task will be created in the default
   * domain as it is configured in the properties.
   *
   * <p>See {@link #newTaskInDomain(String)}
   *
   * @return an empty new {@linkplain Task}
   */
  Task newTask();

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * @param workbasketId the id of the workbasket to which the task belongs
   * @return an empty new {@linkplain Task}
   */
  Task newTask(String workbasketId);

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * @param workbasketKey the key of the workbasket to which the task belongs
   * @param domain the domain of the workbasket to which the task belongs
   * @return an empty new {@linkplain Task}
   */
  Task newTask(String workbasketKey, String domain);

  /**
   * Returns a not inserted instance of {@linkplain Task}. The returned task has no Workbasket set.
   * When createTask() is invoked for this Task, TaskService will call the TaskRouting SPI to
   * determine a Workbasket for the Task.
   *
   * <p>If no TaskRoutingProvider is active, e.g. because no TaskRouter is registered, or the
   * TaskRouter(s) don't find a Workbasket, the {@linkplain Task} will not be inserted.
   *
   * <p>It is the responsibility of the TaskRouting provider to make sure that the corresponding
   * routing rules will be applied for the given domain.
   *
   * @param domain the domain in which the Workbasket will be created
   * @return an empty new {@linkplain Task}
   */
  Task newTaskInDomain(String domain);

  /**
   * Returns a not inserted instance of {@link TaskComment}.
   *
   * @param taskId The id of the task to which the task comment belongs
   * @return an empty new TaskComment
   */
  TaskComment newTaskComment(String taskId);

  /**
   * Returns a not inserted instance of {@link Attachment}.
   *
   * @return an empty new Attachment
   */
  Attachment newAttachment();

  /**
   * Returns a not inserted instance of {@link ObjectReference}.
   *
   * @return an empty new ObjectReference
   */
  ObjectReference newObjectReference();

  ObjectReference newObjectReference(
      String company, String system, String systemInstance, String type, String value);

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
   * @throws ObjectReferencePersistenceException if an ObjectReference with ID will be added
   *     multiple times without using the task-methods
   * @throws InvalidStateException if an attempt is made to change the owner of the task and the
   *     task is not in state READY .
   */
  Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          ObjectReferencePersistenceException, InvalidStateException;

  /**
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} while always setting the {@linkplain
   * Task#isTransferred transfer} flag.
   *
   * @see #transferTasks(String, List, boolean)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    return transferTasks(destinationWorkbasketId, taskIds, true);
  }

  /**
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket}.
   *
   * <p>The transfer resets the read flag and sets the transfer flag if {@code setTransferFlag} is
   * {@code true}. Exceptions will be thrown if the caller got no permissions on the target or it
   * does not exist. Other Exceptions will be stored and returned in the end.
   *
   * @param destinationWorkbasketId target {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket} id
   * @param taskIds list of source {@linkplain Task Tasks} which will be moved
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return Bulkresult with ID and Error in it for failed transactions.
   * @throws NotAuthorizedException if the caller has no permissions on target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket}.
   * @throws InvalidArgumentException if the method parameters are EMPTY or NULL.
   * @throws WorkbasketNotFoundException if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} can not be found.
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds, boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} while always setting the {@linkplain
   * Task#isTransferred} flag.
   *
   * @see #transferTasks(String, String, List, boolean)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey, String destinationWorkbasketDomain, List<String> taskIds)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    return transferTasks(destinationWorkbasketKey, destinationWorkbasketDomain, taskIds, true);
  }

  /**
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket}.
   *
   * <p>The transfer resets the read flag and sets the transfer flag if {@code setTransferFlag} is
   * {@code true}. Exceptions will be thrown if the caller got no permissions on the target or it
   * does not exist. Other Exceptions will be stored and returned in the end.
   *
   * @param destinationWorkbasketKey target {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket} key
   * @param destinationWorkbasketDomain target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} domain
   * @param taskIds list of source {@linkplain Task Tasks} which will be moved
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return BulkResult with ID and Error in it for failed transactions.
   * @throws NotAuthorizedException if the caller has no permissions on target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket}.
   * @throws InvalidArgumentException if the method parameters are EMPTY or NULL.
   * @throws WorkbasketNotFoundException if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} can not be found.
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey,
      String destinationWorkbasketDomain,
      List<String> taskIds,
      boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Deletes the task with the given Id.
   *
   * @param taskId The Id of the task to delete.
   * @throws TaskNotFoundException If the given Id does not refer to an existing task.
   * @throws InvalidStateException If the state of the referenced task is not an end state.
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  void deleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes the task with the given Id even if it is not completed.
   *
   * @param taskId The Id of the task to delete.
   * @throws TaskNotFoundException If the given Id does not refer to an existing task.
   * @throws InvalidStateException If the state of the referenced task is not an end state and
   *     forceDelete is false.
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  void forceDeleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Selects and claims the first task which is returned by the task query.
   *
   * @param taskQuery the task query.
   * @return the task that got selected and claimed
   * @throws InvalidOwnerException if the task is claimed by someone else
   * @throws NotAuthorizedException if the current user has no read permission for the workbasket
   *     the task is in
   */
  Task selectAndClaim(TaskQuery taskQuery) throws NotAuthorizedException, InvalidOwnerException;

  /**
   * Deletes a list of tasks.
   *
   * @param tasks the ids of the tasks to delete.
   * @return the result of the operations with Id and Exception for each failed task deletion.
   * @throws InvalidArgumentException if the TaskIds parameter is NULL
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  BulkOperationResults<String, TaskanaException> deleteTasks(List<String> tasks)
      throws InvalidArgumentException, NotAuthorizedException;

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
   * Completes a list of tasks.
   *
   * @see TaskService#forceCompleteTask
   * @param taskIds of the tasks which should be completed.
   * @return the result of the operations with Id and Exception for each failed task completion.
   * @throws InvalidArgumentException If the taskId parameter is NULL.
   */
  BulkOperationResults<String, TaskanaException> forceCompleteTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Updates tasks with a matching {@link ObjectReference}.
   *
   * @param selectionCriteria the {@link ObjectReference} that is used to select the tasks.
   * @param customFieldsToUpdate a {@link Map} that contains as key the identification of the custom
   *     field and as value the corresponding new value of that custom field.
   * @return a list of the Ids of all modified tasks
   * @throws InvalidArgumentException if the given selectionCriteria is invalid or the given
   *     customFieldsToUpdate are null or empty.
   */
  List<String> updateTasks(
      ObjectReference selectionCriteria, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Updates tasks with matching taskIds.
   *
   * @param taskIds the taskIds that are used to select the tasks.
   * @param customFieldsToUpdate a {@link Map} that contains as key the identification of the custom
   *     field and as value the corresponding new value of that custom field.
   * @return a list of the Ids of all modified tasks
   * @throws InvalidArgumentException if the given customFieldsToUpdate are null or empty.
   */
  List<String> updateTasks(List<String> taskIds, Map<TaskCustomField, String> customFieldsToUpdate)
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

  /**
   * Cancels a task. Cancellation means a task is obsolete from a business perspective an does not
   * need to be completed anymore.
   *
   * @param taskId the id of the task to cancel.
   * @return the updated task.
   * @throws TaskNotFoundException if the Task with Id TaskId is not found
   * @throws InvalidStateException if the task is not in state READY or CLAIMED
   * @throws NotAuthorizedException if the current user is not authorized to see the task
   */
  Task cancelTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Terminates a task. Termination is a administrative action to complete a task. This is typically
   * done by an administration to correct any technical issue.
   *
   * @param taskId the id of the task to cancel.
   * @return the updated task.
   * @throws TaskNotFoundException if the Task with Id TaskId is not found
   * @throws InvalidStateException if the task is not in state READY or CLAIMED
   * @throws NotAuthorizedException if the current user is not authorized to see the task
   */
  Task terminateTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;
}
