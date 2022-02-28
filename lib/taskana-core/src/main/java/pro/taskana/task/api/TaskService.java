package pro.taskana.task.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.IdGenerator;
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
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/** The Task Service manages all operations on tasks. */
public interface TaskService {

  /**
   * Claim an existing {@linkplain Task} for the current user.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to be claimed
   * @return claimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is not {@linkplain TaskState#READY READY}
   * @throws InvalidOwnerException if the {@linkplain Task} with taskId is claimed by some else
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task claim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Claim an existing {@linkplain Task} for the current user even if it is already claimed by
   * someone else.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to be claimed
   * @return claimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is not READY
   * @throws InvalidOwnerException if the {@linkplain Task} with taskId is claimed by someone else
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancel the claim of an existing {@linkplain Task} if it was claimed by the current user before.
   *
   * @param taskId {@linkplain Task#getId() id} of the task which should be unclaimed.
   * @return updated unclaimed task
   * @throws TaskNotFoundException if the {@linkplain Task} can't be found or does not exist
   * @throws InvalidStateException if the {@linkplain Task} is already in an end {@linkplain
   *     Task#getState() state}.
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by another user.
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the task is in
   */
  Task cancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancel the claim of an existing {@linkplain Task} even if it was claimed by another user.
   *
   * @param taskId id of the {@linkplain Task} which should be unclaimed.
   * @return updated unclaimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} can't be found or does not exist
   * @throws InvalidStateException if the {@linkplain Task} is already in an end {@linkplain
   *     Task#getState() state}
   * @throws InvalidOwnerException if forceCancel is false and the {@linkplain Task} is claimed by
   *     another user.
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceCancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Complete a claimed {@linkplain Task} as owner/admin and update {@linkplain Task#getState()
   * state} and Timestamps. If {@linkplain Task} is already completed, the {@linkplain Task} is
   * returned as itself.
   *
   * @param taskId - {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     completed.
   * @return Task - updated {@linkplain Task} after completion.
   * @throws InvalidStateException if {@linkplain Task} wasn't claimed before.
   * @throws TaskNotFoundException if the given {@linkplain Task} can't be found in DB.
   * @throws InvalidOwnerException if current user is not the task-owner or administrator.
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task completeTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Completes a {@linkplain Task} and updates {@linkplain Task#getState() state} and Timestamps in
   * every case if the {@linkplain Task} exists. If {@linkplain Task} is already completed, the
   * {@linkplain Task} is returned as itself.
   *
   * @param taskId - {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     completed.
   * @return Task - updated {@linkplain Task} after completion.
   * @throws InvalidStateException if {@linkplain Task} wasn't claimed before.
   * @throws TaskNotFoundException if the given {@linkplain Task} can't be found in DB.
   * @throws InvalidOwnerException if current user is not the task-owner or administrator.
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceCompleteTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Inserts a not existing {@linkplain Task}. <br>
   * The default values of the created {@linkplain Task} are:
   *
   * <ul>
   *   <li><b>{@linkplain Task#getId() id}</b> - generated by {@linkplain IdGenerator}
   *   <li><b>{@linkplain Task#getExternalId() externalId}</b> - generated by IdGenerator
   *   <li><b>{@linkplain Task#getBusinessProcessId() businessProcessId}</b> - generated by
   *       IdGenerator
   *   <li><b>{@linkplain Task#getName() name}</b> - name of its Classification
   *   <li><b>{@linkplain Task#getDescription() description}</b> - description of its Classification
   *   <li><b>{@linkplain Task#getCreator() creator}</b> - id of current user
   *   <li><b>{@linkplain Task#getState() state}</b> - 'READY'
   *   <li><b>{@linkplain Task#isRead() isRead}</b> - {@code false}
   *   <li><b>{@linkplain Task#isTransferred() isTransferred}</b> - {@code false}
   * </ul>
   *
   * @param taskToCreate the transient {@linkplain Task} object to be inserted
   * @return the created and inserted {@linkplain Task}
   * @throws TaskAlreadyExistException if the {@linkplain Task} does already exist.
   * @throws NotAuthorizedException thrown if the current user is not authorized to create that
   *     {@linkplain Task}
   * @throws WorkbasketNotFoundException thrown if the workbasket referenced by the {@linkplain
   *     Task} is not found
   * @throws ClassificationNotFoundException thrown if the Classification referenced by the
   *     {@linkplain Task} is not found
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
   * Gets the details of a {@linkplain Task} by {@linkplain Task#getId() id} without checking
   * permissions.
   *
   * @param taskId the {@linkplain Task#getId()} of the {@linkplain Task}
   * @return the {@linkplain Task}
   * @throws TaskNotFoundException thrown of the {@linkplain Task} with taskId is not found
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission#READ
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in.
   */
  Task getTask(String taskId) throws TaskNotFoundException, NotAuthorizedException;

  /**
   * Transfers a {@linkplain Task} to another {@linkplain Workbasket} while always setting the
   * {@linkplain Task#isTransferred transfer} flag.
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
   * Transfers a {@linkplain Task} to another {@linkplain Workbasket}.
   *
   * <p>The transfer resets the {@linkplain Task#isRead() read} flag and sets the {@linkplain
   * Task#isTransferred() transfer} flag if {@code setTransferFlag} is {@code true}.
   *
   * @param taskId the {@linkplain Task#getId()} of the {@linkplain Task} which should be
   *     transferred
   * @param destinationWorkbasketId the {@linkplain Workbasket#getId() id} of the target {@linkplain
   *     Workbasket}
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return the transferred {@linkplain Task}
   * @throws TaskNotFoundException Thrown if the {@linkplain Task} with taskId was not found.
   * @throws WorkbasketNotFoundException Thrown if the target {@linkplain Workbasket} was not found.
   * @throws NotAuthorizedException Thrown if the current user is not authorized to transfer this
   *     {@linkplain Task} to the target {@linkplain Workbasket}
   * @throws InvalidStateException Thrown if the {@linkplain Task} is in a {@linkplain
   *     Task#getState() state} which does not allow transferring
   */
  Task transfer(String taskId, String destinationWorkbasketId, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Transfers a {@linkplain Task} to another {@linkplain Workbasket} while always setting the
   * {@linkplain Task#isTransferred transfer} flag.
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
   * Transfers a {@linkplain Task} to another {@linkplain Workbasket}.
   *
   * <p>The transfer resets the {@linkplain Task#isRead() read} flag and sets the transfer flag if
   * {@code setTransferFlag} is {@code true}.
   *
   * @param taskId the id of the {@linkplain Task} which should be transferred
   * @param workbasketKey the key of the target {@linkplain Workbasket}
   * @param domain the domain of the target {@linkplain Workbasket}
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return the transferred {@linkplain Task}
   * @throws TaskNotFoundException Thrown if the {@linkplain Task} with taskId was not found.
   * @throws WorkbasketNotFoundException Thrown if the target {@linkplain Workbasket} was not found.
   * @throws NotAuthorizedException Thrown if the current user is not authorized to transfer this
   *     {@linkplain Task} to the target {@linkplain Workbasket}
   * @throws InvalidStateException Thrown if the {@linkplain Task} is in a {@linkplain
   *     Task#getState()} which does not allow transferring
   */
  Task transfer(String taskId, String workbasketKey, String domain, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Marks a {@linkplain Task} as read.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to be updated
   * @param isRead the new status of the {@linkplain Task#isRead() read} flag
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException Thrown if the {@linkplain Task} with taskId was not found
   * @throws NotAuthorizedException if the current user has no {@linkplain WorkbasketPermission
   *     READ} permission for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task setTaskRead(String taskId, boolean isRead)
      throws TaskNotFoundException, NotAuthorizedException;

  /**
   * Provides a query builder for querying the database.
   *
   * @return a {@linkplain TaskQuery}
   */
  TaskQuery createTaskQuery();

  /**
   * This method provides a query builder for querying the database.
   *
   * @return a {@linkplain TaskCommentQuery}
   */
  TaskCommentQuery createTaskCommentQuery();

  /**
   * Returns a not inserted instance of {@linkplain Task}. The returned {@linkplain Task} has no
   * {@linkplain Task#getWorkbasketSummary() w} set. When createTask() is invoked for this task,
   * TaskService will call the TaskRouting SPI to determine a workbasket for the task. If the
   * TaskRouting API is not active, e.g. because no TaskRouter is registered, or the TaskRouter(s)
   * don't find a workbasket, the task will not be inserted.
   *
   * @return an empty new Task
   */
  Task newTask();

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * @param workbasketId the {@linkplain Workbasket#getId() id} of the {@linkplain Workbasket} to
   *     which the task belongs
   * @return an empty new {@linkplain Task}
   */
  Task newTask(String workbasketId);

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * @param workbasketKey the {@linkplain Workbasket#getKey() key} of the {@linkplain Workbasket} to
   *     which the {@linkplain Task} belongs
   * @param domain the {@linkplain Workbasket#getDomain() domain} of the {@linkplain Workbasket} to
   *     which the {@linkplain Task} belongs
   * @return an empty new {@linkplain Task}
   */
  Task newTask(String workbasketKey, String domain);

  /**
   * Returns a not inserted instance of {@linkplain TaskComment}.
   *
   * @param taskId The {@linkplain Task#getId() id} of the {@linkplain Task} to which the task
   *     comment belongs
   * @return an empty new {@linkplain TaskComment}
   */
  TaskComment newTaskComment(String taskId);

  /**
   * Returns a not inserted instance of {@linkplain Attachment}.
   *
   * @return an empty new {@linkplain Attachment}
   */
  Attachment newAttachment();

  /**
   * Returns a not inserted instance of {@linkplain ObjectReference}.
   *
   * @return an empty new {@linkplain ObjectReference}
   */
  ObjectReference newObjectReference();

  ObjectReference newObjectReference(
      String company, String system, String systemInstance, String type, String value);

  /**
   * Update a {@linkplain Task}.
   *
   * @param task the {@linkplain Task} to be updated in the database
   * @return the updated {@linkplain Task}
   * @throws InvalidArgumentException if the {@linkplain Task} to be updated contains invalid
   *     properties like e.g. invalid {@linkplain ObjectReference}s
   * @throws TaskNotFoundException if the id of the {@linkplain Task} is not found in the database
   * @throws ConcurrencyException if the {@linkplain Task} has been updated by another user in the
   *     meantime; that's the case if the given modified timestamp differs from the one in the
   *     database
   * @throws ClassificationNotFoundException if the updated {@linkplain Task} refers to a {@link
   *     Classification} that cannot be found
   * @throws NotAuthorizedException if the current user is not authorized to update the {@linkplain
   *     Task}
   * @throws AttachmentPersistenceException if an {@linkplain Attachment} with ID will be added
   *     multiple times without using the Task-methods
   * @throws ObjectReferencePersistenceException if an {@linkplain ObjectReference} with ID will be
   *     added multiple times without using the Task-methods
   * @throws InvalidStateException if an attempt is made to change the owner of the {@linkplain
   *     Task} that state isn't READY.
   */
  Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          ObjectReferencePersistenceException, InvalidStateException;

  /**
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain Workbasket} while always
   * setting the {@linkplain Task#isTransferred transfer} flag.
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
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain Workbasket}.
   *
   * <p>The transfer resets the {@linkplain Task#isRead() read flag} and sets the {@linkplain
   * Task#isTransferred() transfer flag} if {@code setTransferFlag} is {@code true}. Exceptions will
   * be thrown if the caller got no {@linkplain WorkbasketPermission} on the target or if the target
   * {@linkplain Workbasket} does not exist. Other Exceptions will be stored and returned in the
   * end.
   *
   * @param destinationWorkbasketId {@linkplain Workbasket#getId() id} of the target {@linkplain
   *     Workbasket}
   * @param taskIds list of source {@linkplain Task Tasks} which will be moved
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return Bulkresult with {@linkplain Task#getId() ids} and Error in it for failed transactions
   * @throws NotAuthorizedException if the caller has no permissions on target {@linkplain
   *     Workbasket}
   * @throws InvalidArgumentException if the method parameters are EMPTY or NULL
   * @throws WorkbasketNotFoundException if the target {@linkplain Workbasket} can not be found
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds, boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain Workbasket} while always
   * setting the {@linkplain Task#isTransferred} flag.
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
   * Transfers a list of {@linkplain Task Tasks} to another {@linkplain Workbasket}.
   *
   * <p>The transfer resets the {@linkplain Task#isRead() read flag} and sets the {@linkplain
   * Task#isTransferred() transfer flag} if {@code setTransferFlag} is {@code true}. Exceptions will
   * be thrown if the caller got no {@linkplain WorkbasketPermission Permission} on the target
   * {@linkplain Workbasket} or if it does not exist. Other Exceptions will be stored and returned
   * in the end.
   *
   * @param destinationWorkbasketKey target {@linkplain Workbasket#getKey()} Workbasket}
   * @param destinationWorkbasketDomain target {@linkplain Workbasket#getDomain() domain}
   * @param taskIds List of source {@linkplain Task Tasks} which will be moved
   * @param setTransferFlag the control about whether to set the {@linkplain Task#isTransferred()}
   *     flag or not
   * @return BulkResult with {@linkplain Task#getId() id} and Error in it for failed transactions.
   * @throws NotAuthorizedException if the caller has no {@linkplain WorkbasketPermission} on target
   *     {@linkplain Workbasket}.
   * @throws InvalidArgumentException if the method parameters are EMPTY or NULL.
   * @throws WorkbasketNotFoundException if the target {@linkplain Workbasket} can not be found.
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey,
      String destinationWorkbasketDomain,
      List<String> taskIds,
      boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Deletes the {@linkplain Task} with the given {@linkplain Task#getId() id}.
   *
   * @param taskId The {@linkplain Task#getId() id} of the {@linkplain Task} to delete.
   * @throws TaskNotFoundException If the given {@linkplain Task#getId() id} does not refer to an
   *     existing {@linkplain Task}.
   * @throws InvalidStateException If the {@linkplain Task#getState() state} of the referenced
   *     {@linkplain Task} is not an end state.
   * @throws NotAuthorizedException if the current user is not member of role {@linkplain
   *     TaskanaRole#ADMIN}
   */
  void deleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes the {@linkplain Task} with the given {@linkplain Task#getId() id} even if it is not
   * completed.
   *
   * @param taskId The {@linkplain Task#getId() id} of the {@linkplain Task} to delete.
   * @throws TaskNotFoundException If the given {@linkplain Task#getId() id} does not refer to an
   *     existing {@linkplain Task}.
   * @throws InvalidStateException If the state of the referenced {@linkplain Task} is not an end
   *     state and forceDelete is false.
   * @throws NotAuthorizedException if the current user is not member of role {@linkplain
   *     TaskanaRole#ADMIN}
   */
  void forceDeleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Selects and claims the first {@linkplain Task} which is returned by the {@linkplain TaskQuery}.
   *
   * @param taskQuery the {@linkplain TaskQuery}.
   * @return the {@linkplain Task} that got selected and claimed
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by someone else
   * @throws NotAuthorizedException if the current user has no read permission for the Workbasket
   *     the {@linkplain Task} is in
   */
  Task selectAndClaim(TaskQuery taskQuery) throws NotAuthorizedException, InvalidOwnerException;

  /**
   * Deletes a list of {@linkplain Task Tasks}.
   *
   * @param tasks the {@linkplain Task#getId() ids} of the tasks to delete.
   * @return the result of the operations with each {@linkplain Task#getId() id} and Exception for
   *     each failed task deletion.
   * @throws InvalidArgumentException if the TaskIds parameter is NULL
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  BulkOperationResults<String, TaskanaException> deleteTasks(List<String> tasks)
      throws InvalidArgumentException, NotAuthorizedException;

  /**
   * Completes a list of {@linkplain Task Tasks}.
   *
   * @param taskIds {@linkplain Task#getId() ids} of the {@linkplain Task Tasks} which should be
   *     completed
   * @return the result of the operations with each {@linkplain Task#getId() id} and Exception for
   *     each failed completion
   * @throws InvalidArgumentException If the taskIds parameter is NULL
   */
  BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Completes each existing {@linkplain Task} in the given List in every case, independent of the
   * {@linkplain Task#getOwner() owner} or {@linkplain Task#getState() state} of the {@linkplain
   * Task}. If the {@linkplain Task} is already {@linkplain TaskState#COMPLETED completed}, the
   * {@linkplain Task} stays unchanged.
   *
   * @see TaskService#forceCompleteTask
   * @param taskIds {@linkplain Task#getId() id} of the {@linkplain Task Tasks} which should be
   *     completed
   * @return the result of the operations with {@linkplain Task#getId() id} and Exception for each
   *     failed completion
   * @throws InvalidArgumentException If the taskIds parameter is NULL
   */
  BulkOperationResults<String, TaskanaException> forceCompleteTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Updates specified {@linkplain TaskCustomField TaskCustomFields} of {@linkplain Task Tasks}
   * associated with the given {@linkplain ObjectReference}.
   *
   * @param selectionCriteria the {@linkplain ObjectReference} that is used to select the tasks
   * @param customFieldsToUpdate a Map that contains as key the identification of the {@linkplain
   *     TaskCustomField} and as value the corresponding new value of that field
   * @return a list of the {@linkplain Task#getId() ids} of all modified {@linkplain Task Tasks}
   * @throws InvalidArgumentException if the given selectionCriteria is invalid or the given
   *     customFieldsToUpdate are NULL or empty
   */
  List<String> updateTasks(
      ObjectReference selectionCriteria, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Updates specified {@linkplain TaskCustomField TaskCustomFields} for all given {@linkplain Task
   * Tasks}.
   *
   * @param taskIds the {@linkplain Task#getId() taskIds} that are used to select the {@linkplain
   *     Task Tasks}.
   * @param customFieldsToUpdate a Map that contains as key the identification of the {@linkplain
   *     TaskCustomField} and as value the corresponding new value of that {@linkplain
   *     TaskCustomField}.
   * @return a list of the {@linkplain Task#getId() ids} of all modified {@linkplain Task Tasks}
   * @throws InvalidArgumentException if the given customFieldsToUpdate are NULL or empty.
   */
  List<String> updateTasks(List<String> taskIds, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Inserts the specified {@linkplain TaskComment} into the database.
   *
   * @param taskComment the {@linkplain TaskComment} to be created
   * @return the created {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no authorization to create a {@link
   *     TaskComment} for the given taskId or is not authorized to access the {@linkplain Task}
   * @throws TaskNotFoundException if the given {@linkplain TaskComment#getTaskId() taskId} does not
   *     refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the {@linkplain TaskComment#getId() id} of the provided
   *     {@link TaskComment} is not NULL or empty
   */
  TaskComment createTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException;

  /**
   * Updates the specified {@linkplain TaskComment}.
   *
   * @param taskComment the {@linkplain TaskComment} to be updated in the database
   * @return the updated {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no authorization to update the
   *     {@linkplain TaskComment} or is not authorized to access the {@linkplain Task}
   * @throws ConcurrencyException if an attempt is made to update the {@linkplain TaskComment} and
   *     another user updated it already; that's the case if the given {} timestamp differs from the
   *     one in the database
   * @throws TaskCommentNotFoundException if the {@linkplain TaskComment#getId() is} of the
   *     specified {@linkplain TaskComment}does not refer to an existing {@linkplain TaskComment}
   * @throws TaskNotFoundException if the {@linkplain TaskComment#getTaskId() taskId} does not refer
   *     to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the given {@linkplain TaskComment#getId() id} is NULL or
   *     empty
   */
  TaskComment updateTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException, InvalidArgumentException;

  /**
   * Deletes the {@linkplain TaskComment} with the given {@linkplain TaskComment#getId() id}.
   *
   * @param taskCommentId the {@linkplain TaskComment#getId() id} of the {@linkplain TaskComment} to
   *     delete
   * @throws NotAuthorizedException if the current user has no authorization to delete a task
   *     comment or is not authorized to access the task.
   * @throws InvalidArgumentException if the taskCommentId is NULL or empty
   * @throws TaskCommentNotFoundException if the given taskCommentId in the TaskComment does not
   *     refer to an existing taskComment.
   * @throws TaskNotFoundException if the {@linkplain TaskComment#getTaskId() taskId} of the
   *     TaskComment does not refer to an existing {@linkplain Task}.
   * @throws InvalidArgumentException if the given taskCommentId is NULL or empty
   */
  void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves the {@linkplain TaskComment} with the given {@linkplain TaskComment#getId() id}.
   *
   * @param taskCommentId the {@linkplain TaskComment#getId() id} of the {@linkplain TaskComment}
   *     which should be retrieved
   * @return the {@linkplain TaskComment} identified by taskCommentId
   * @throws TaskCommentNotFoundException if the given taskCommentId does not refer to an existing
   *     {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no authorization to retrieve a
   *     {@linkplain TaskComment} from a certain {@linkplain Task} or is not authorized to access
   *     the {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain TaskComment#getTaskId() taskId} of the
   *     TaskComment does not refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the given taskCommentId is NULL or empty
   */
  TaskComment getTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves the List of {@linkplain TaskComment TaskComments} for the Task with given {@linkplain
   * Task#getId() id}.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} for which all task
   *     comments should be retrieved
   * @return the List of task comments attached to the specified {@linkplain Task}
   * @throws NotAuthorizedException if the current user has no authorization to retrieve a
   *     taskComment from the {@linkplain Task} or is not authorized to access the {@linkplain Task}
   * @throws TaskNotFoundException if the given taskId does not refer to an existing {@linkplain
   *     Task}
   */
  List<TaskComment> getTaskComments(String taskId)
      throws NotAuthorizedException, TaskNotFoundException;

  /**
   * Sets the specified {@linkplain CallbackState} on a list of {@linkplain Task Tasks}. Note: this
   * method is primarily intended to be used by the TaskanaAdapter
   *
   * @param externalIds the {@linkplain Task#getExternalId() externalIds} of the {@linkplain Task
   *     Tasks} on which the {@linkplain CallbackState} is set
   * @param state the {@linkplain CallbackState} that is to be set on the {@linkplain Task Tasks}
   * @return the result of the operations with {@linkplain Task#getId() id} and Exception for each
   *     failed operation
   */
  BulkOperationResults<String, TaskanaException> setCallbackStateForTasks(
      List<String> externalIds, CallbackState state);

  /**
   * Sets the {@linkplain Task#getOwner() owner} on a List of {@linkplain Task Tasks}. The
   * {@linkplain Task#getOwner() owner} will only be set on {@linkplain Task Tasks} that are in
   * state {@linkplain TaskState#READY}.
   *
   * @param owner the new {@linkplain Task#getOwner() owner} of the {@linkplain Task Tasks}
   * @param taskIds the {@linkplain Task#getId() ids} of the {@linkplain Task Tasks} on which the
   *     {@linkplain Task#getOwner() owner} is to be set
   * @return the result of the operations with {@linkplain Task#getId() id} and Exception for each
   *     failed {@linkplain Task}-update
   */
  BulkOperationResults<String, TaskanaException> setOwnerOfTasks(
      String owner, List<String> taskIds);

  /**
   * Sets the {@linkplain Task#getPlanned() planned} Instant on a List of {@linkplain Task Tasks}.
   * Only {@linkplain Task Tasks} in state {@linkplain TaskState#READY} and {@linkplain
   * TaskState#CLAIMED} will be affected by this method. On each {@linkplain Task}, the
   * corresponding {@linkplain Task#getDue() due date} is set according to the shortest serviceLevel
   * in the {@linkplain Task#getClassificationSummary() Classification} of the {@linkplain Task} and
   * its {@linkplain Task#getAttachments() Attachments}.
   *
   * @param planned the new {@linkplain Task#getPlanned() planned} Instant of the {@linkplain Task
   *     Tasks}
   * @param taskIds the {@linkplain Task#getId() ids} of the {@linkplain Task Tasks} on which the
   *     new {@linkplain Task#getPlanned() planned} Instant is to be set
   * @return the result of the operations with {@linkplain Task#getId() id} and Exception for each
   *     failed {@linkplain Task Task} update.
   */
  BulkOperationResults<String, TaskanaException> setPlannedPropertyOfTasks(
      Instant planned, List<String> taskIds);

  /**
   * Cancels the {@linkplain Task} with the given {@linkplain Task#getId() id}. Cancellation means a
   * {@linkplain Task} is obsolete from a business perspective and does not need to be completed
   * anymore.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to cancel
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId is not found
   * @throws InvalidStateException if the {@linkplain Task} is not in state {@linkplain
   *     TaskState#READY} or {@linkplain TaskState#CLAIMED}
   * @throws NotAuthorizedException if the current user is not authorized to see the {@linkplain
   *     Task}
   */
  Task cancelTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Terminates a {@linkplain Task}. Termination is an administrative action to complete a
   * {@linkplain Task}. This is typically done by administration to correct any technical issue.
   *
   * @param taskId the id of the {@linkplain Task} to cancel
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId is not found
   * @throws InvalidStateException if the {@linkplain Task} is not in state {@linkplain
   *     TaskState#READY} or {@linkplain TaskState#CLAIMED}
   * @throws NotAuthorizedException if the current user is not authorized to see the {@linkplain
   *     Task}
   */
  Task terminateTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;
}
