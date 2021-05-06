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
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** The TaskService manages all operations on {@linkplain Task Tasks}. */
public interface TaskService {

  /**
   * Claims an existing {@linkplain Task} for the current user.
   *
   * @param taskId the id of the {@linkplain Task} to be claimed
   * @return claimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the state of the {@linkplain Task} with taskId is not READY
   * @throws InvalidOwnerException if the {@linkplain Task} with taskId is claimed by some else
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task claim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Claims an existing {@linkplain Task} for the current user even if it is already claimed by
   * someone else.
   *
   * @param taskId the id of the {@linkplain Task} to be claimed
   * @return claimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the state of the {@linkplain Task} with taskId is not READY
   * @throws InvalidOwnerException if the {@linkplain Task} with taskId is claimed by someone else
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task forceClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancels the claim of an existing {@linkplain Task} if it was claimed by the current user
   * before.
   *
   * @param taskId id of the {@linkplain Task} which should be unclaimed
   * @return updated unclaimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} can´t be found or does not exist
   * @throws InvalidStateException if the {@linkplain Task} is already in an end state
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by another user
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task cancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancels the claim of an existing {@linkplain Task} even if it was claimed by another user.
   *
   * @param taskId id of the {@linkplain Task} which should be unclaimed
   * @return updated unclaimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} can´t be found or does not exist
   * @throws InvalidStateException if the {@linkplain Task} is already in an end state
   * @throws InvalidOwnerException if forceCancel is false and the {@linkplain Task} is claimed by
   *     another user
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task forceCancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Completes a claimed {@linkplain Task} as owner/admin and updates state and timestamps. If the
   * {@linkplain Task} is already completed, the {@linkplain Task} is returned as itself.
   *
   * @param taskId - Id of the {@linkplain Task} which should be completed
   * @return Task - updated {@linkplain Task} after completion
   * @throws InvalidStateException if {@linkplain Task} wasn´t claimed before
   * @throws TaskNotFoundException if the given {@linkplain Task} can´t be found in DB
   * @throws InvalidOwnerException if current user is not the task-owner or administrator
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task completeTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Completes a {@linkplain Task} and updates state and timestamps in every case if the {@linkplain
   * Task} exists. If the {@linkplain Task} is already completed, the {@linkplain Task} is returned
   * as itself.
   *
   * @param taskId - Id of the {@linkplain Task} which should be completed
   * @return Task - updated {@linkplain Task} after completion
   * @throws InvalidStateException if {@linkplain Task} wasn´t claimed before
   * @throws TaskNotFoundException if the given {@linkplain Task} can´t be found in DB
   * @throws InvalidOwnerException if current user is not the task-owner or administrator
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task forceCompleteTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Inserts a not existing {@linkplain Task}. <br>
   * The default values of the created {@linkplain Task} are:
   *
   * <ul>
   *   <li><b>id</b> - generated by {@linkplain pro.taskana.common.internal.util.IdGenerator
   *       IdGenerator}
   *   <li><b>externalId</b> - generated by {@linkplain pro.taskana.common.internal.util.IdGenerator
   *       * IdGenerator}
   *   <li><b>businessProcessId</b> - generated by {{@linkplain
   *       pro.taskana.common.internal.util.IdGenerator * IdGenerator}
   *   <li><b>name</b> - name of its {@linkplain
   *       pro.taskana.classification.api.models.Classification Classification}
   *   <li><b>description</b> - description of its {@linkplain
   *       pro.taskana.classification.api.models.Classification Classification}
   *   <li><b>creator</b> - id of current user
   *   <li><b>state</b> - 'READY'
   *   <li><b>isRead</b> - {@code false}
   *   <li><b>isTransferred</b> - {@code false}
   * </ul>
   *
   * @param taskToCreate the transient {@linkplain Task} object to be inserted
   * @return the created and inserted {@linkplain Task}
   * @throws TaskAlreadyExistException if the {@linkplain Task} does already exist
   * @throws NotAuthorizedException if the current user is not authorized to create that {@linkplain
   *     Task}
   * @throws WorkbasketNotFoundException if the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} referenced by the {@linkplain
   *     Task} is not found
   * @throws ClassificationNotFoundException if the {@linkplain
   *     pro.taskana.classification.api.models.Classification Classification} referenced by the
   *     {@linkplain Task} is not found
   * @throws InvalidArgumentException if the primary {@linkplain ObjectReference} is invalid
   */
  Task createTask(Task taskToCreate)
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException;

  /**
   * Returns the details of a {@linkplain Task} by Id without checking permissions.
   *
   * @param taskId the id of the {@linkplain Task}
   * @return the {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId is not found
   * @throws NotAuthorizedException if the current user has no READ permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
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
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws WorkbasketNotFoundException if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} was not found
   * @throws NotAuthorizedException if the current user is not authorized to transfer this
   *     {@linkplain Task} to the target {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket}
   * @throws InvalidStateException if the {@linkplain Task} is in a state which does not allow
   *     transferring
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
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws WorkbasketNotFoundException if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} was not found
   * @throws NotAuthorizedException if the current user is not authorized to transfer this
   *     {@linkplain Task} to the target Workbasket
   * @throws InvalidStateException if the {@linkplain Task} is in a state which does not allow
   *     transferring
   */
  Task transfer(String taskId, String workbasketKey, String domain, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Marks a {@linkplain Task} as read.
   *
   * @param taskId the id of the {@linkplain Task} to be updated
   * @param isRead the new status of the read flag
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task setTaskRead(String taskId, boolean isRead)
      throws TaskNotFoundException, NotAuthorizedException;

  /**
   * Provides a query builder for quering the database.
   *
   * @return a {@link TaskQuery}
   */
  TaskQuery createTaskQuery();

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * <p>The returned {@linkplain Task} has no {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} Id set. When {@linkplain
   * TaskService#createTask createTask()} is invoked for this {@linkplain Task}, {@linkplain
   * TaskService} will call the {@linkplain pro.taskana.spi.routing.api.TaskRoutingProvider
   * TaskRouting API} to determine a {@linkplain pro.taskana.workbasket.api.models.Workbasket
   * Workbasket} for the {@linkplain Task}. If the {@linkplain
   * pro.taskana.spi.routing.api.TaskRoutingProvider TaskRouting API} is not active, e.g. because no
   * TaskRouter is registered, or the TaskRouter(s) don't find a {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket}, the {@linkplain Task} will not be
   * inserted.
   *
   * @return an empty new {@linkplain Task}
   */
  Task newTask();

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * @param workbasketId the id of the {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket} to which the {@linkplain Task} belongs
   * @return an empty new {@linkplain Task}
   */
  Task newTask(String workbasketId);

  /**
   * Returns a not inserted instance of {@linkplain Task}.
   *
   * @param workbasketKey the key of the {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket} to which the {@linkplain Task} belongs
   * @param domain the domain of the {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbasket} to which the {@linkplain Task} belongs
   * @return an empty new {@linkplain Task}
   */
  Task newTask(String workbasketKey, String domain);

  /**
   * Returns a not inserted instance of {@linkplain TaskComment}.
   *
   * @param taskId The id of the {@linkplain Task} to which the task comment belongs
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
   * Updates a {@linkplain Task}.
   *
   * @param task the {@linkplain Task} to be updated in the database
   * @return the updated {@linkplain Task}
   * @throws InvalidArgumentException if the {@linkplain Task} to be updated contains invalid
   *     properties like e.g. invalid object references
   * @throws TaskNotFoundException if the id of the {@linkplain Task} is not found in the database
   * @throws ConcurrencyException if the {@linkplain Task} has already been updated by another user
   * @throws ClassificationNotFoundException if the updated {@linkplain Task} refers to a
   *     {@linkplain pro.taskana.classification.api.models.Classification Classification} that
   *     cannot be found
   * @throws NotAuthorizedException if the current user is not authorized to update the {@linkplain
   *     Task}
   * @throws AttachmentPersistenceException if an {@linkplain Attachment} with ID will be added
   *     multiple times without using the task-methods
   * @throws InvalidStateException if an attempt is made to change the owner of the {@linkplain
   *     Task} and the {@linkplain Task} is not in state READY
   */
  Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException;

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
   * @return Bulkresult with ID and Error in it for failed transactions
   * @throws NotAuthorizedException if the caller has no permissions on target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket}
   * @throws InvalidArgumentException if the method parameters are EMPTY or NULL
   * @throws WorkbasketNotFoundException if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} can not be found
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
   * @return Bulkresult with ID and Error in it for failed transactions
   * @throws NotAuthorizedException if the caller has no permissions on target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket}
   * @throws InvalidArgumentException if the method parameters are EMPTY or NULL
   * @throws WorkbasketNotFoundException if the target {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} can not be found
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey,
      String destinationWorkbasketDomain,
      List<String> taskIds,
      boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Deletes the {@linkplain Task} with the given Id.
   *
   * @param taskId the Id of the {@linkplain Task} to delete
   * @throws TaskNotFoundException if the given Id does not refer to an existing {@linkplain Task}
   * @throws InvalidStateException if the state of the referenced {@linkplain Task} is not an end
   *     state
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  void deleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes the {@linkplain Task} with the given ID even if it is not completed.
   *
   * @param taskId the Id of the {@linkplain Task} to delete
   * @throws TaskNotFoundException if the given Id does not refer to an existing {@linkplain Task}
   * @throws InvalidStateException if the state of the referenced {@linkplain Task} is not an end
   *     state and forceDelete is false
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  void forceDeleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Selects and claims the first {@linkplain Task} which is returned by the {@linkplain TaskQuery}.
   *
   * @param taskQuery the {@linkplain TaskQuery}
   * @return the {@linkplain Task} that got selected and claimed
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by someone else
   * @throws NotAuthorizedException if the current user has no read permission for the {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} the {@linkplain Task} is in
   */
  Task selectAndClaim(TaskQuery taskQuery) throws NotAuthorizedException, InvalidOwnerException;

  /**
   * Deletes a list of {@linkplain Task Tasks}.
   *
   * @param tasks the ids of the {@linkplain Task Tasks} to delete
   * @return the result of the operations with Id and Exception for each failed {@linkplain Task}
   *     deletion
   * @throws InvalidArgumentException if the TaskIds parameter is NULL
   * @throws NotAuthorizedException if the current user is not member of role ADMIN
   */
  BulkOperationResults<String, TaskanaException> deleteTasks(List<String> tasks)
      throws InvalidArgumentException, NotAuthorizedException;

  /**
   * Completes a list of {@linkplain Task Tasks}.
   *
   * @param taskIds of the {@linkplain Task Tasks} which should be completed
   * @return the result of the operations with Id and Exception for each failed {@linkplain Task}
   *     completion
   * @throws InvalidArgumentException if the taskId parameter is NULL
   */
  BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Completes a list of {@linkplain Task Tasks}.
   *
   * @see TaskService#forceCompleteTask
   * @param taskIds of the {@linkplain Task Tasks} which should be completed
   * @return the result of the operations with Id and Exception for each failed {@linkplain Task}
   *     completion
   * @throws InvalidArgumentException if the taskId parameter is NULL
   */
  BulkOperationResults<String, TaskanaException> forceCompleteTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Updates {@linkplain Task Tasks} with a matching {@linkplain ObjectReference}.
   *
   * @param selectionCriteria the {@linkplain ObjectReference} that is used to select the
   *     {@linkplain Task Tasks}
   * @param customFieldsToUpdate a Map that contains as key the identification of the custom field
   *     and as value the corresponding new value of that custom field
   * @return a list of the Ids of all modified {@linkplain Task Tasks}
   * @throws InvalidArgumentException if the given selectionCriteria is invalid or the given
   *     customFieldsToUpdate are null or empty
   */
  List<String> updateTasks(
      ObjectReference selectionCriteria, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Updates {@linkplain Task Tasks} with matching taskIds.
   *
   * @param taskIds the taskIds that are used to select the {@linkplain Task Tasks}
   * @param customFieldsToUpdate a Map that contains as key the identification of the custom field
   *     and as value the corresponding new value of that custom field
   * @return a list of the Ids of all modified {@linkplain Task Tasks}
   * @throws InvalidArgumentException if the given customFieldsToUpdate are null or empty
   */
  List<String> updateTasks(List<String> taskIds, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Creates a {@linkplain TaskComment}.
   *
   * @param taskComment the {@linkplain TaskComment} to be created.
   * @return the created {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no authorization to create a {@linkplain
   *     TaskComment} for the given taskId in the {@linkplain TaskComment} or is not authorized to
   *     access the {@linkplain Task}
   * @throws TaskNotFoundException if the given taskId in the {linkplain TaskComment} does not refer
   *     to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the given taskCommentId from the provided t{@linkplain
   *     TaskComment} is null or empty
   */
  TaskComment createTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException;

  /**
   * Updates a {@linkplain TaskComment}.
   *
   * @param taskComment the {@linkplain TaskComment} to be updated in the database
   * @return the updated {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no authorization to update a {@linkplain
   *     TaskComment} or is not authorized to access the {@linkplain Task}
   * @throws ConcurrencyException if an attempt is made to update the {@linkplain TaskComment} and
   *     another user updated it already
   * @throws TaskCommentNotFoundException if the given taskCommentId in the TaskComment does not
   *     refer to an existing taskComment
   * @throws TaskNotFoundException if the given taskId in the TaskComment does not refer to an
   *     existing task
   * @throws InvalidArgumentException if the given taskCommentId from the provided task comment is
   *     null or empty
   */
  TaskComment updateTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException, InvalidArgumentException;

  /**
   * Deletes the {@linkplain TaskComment} with the given Id.
   *
   * @param taskCommentId the id of the {@linkplain TaskComment} to delete
   * @throws NotAuthorizedException if the current user has no authorization to delete a {@linkplain
   *     TaskComment} or is not authorized to access the {@linkplain Task}
   * @throws TaskCommentNotFoundException if the given taskCommentId in the {@linkplain TaskComment}
   *     does not refer to an existing {@linkplain TaskComment}
   * @throws TaskNotFoundException if the given taskId in the {@linkplain TaskComment} does not
   *     refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException f the given taskCommentId is null or empty
   */
  void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves a {@linkplain TaskComment} for a given taskCommentId.
   *
   * @param taskCommentId the id of the {@linkplain TaskComment} which should be retrieved
   * @return the {@linkplain TaskComment} identified by taskCommentId
   * @throws TaskCommentNotFoundException if the given taskCommentId in the {@linkplain TaskComment}
   *     does not refer to an existing {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no authorization to retrieve a
   *     {@linkplain TaskComment} from a certain {@linkplain Task} or is not authorized to access
   *     the {@linkplain Task}
   * @throws TaskNotFoundException if the given taskId in the {@linkplain TaskComment} does not
   *     refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the given taskCommentId is null or empty
   */
  TaskComment getTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves a list of {@linkplain TaskComment TaskComments} for a given taskId.
   *
   * @param taskId the id of the {@linkplain Task} for which all {@linkplain TaskComment
   *     TaskComments} should be retrieved
   * @return the list of {@linkplain TaskComment TaskComments} attached to task with id taskId
   * @throws NotAuthorizedException if the current user has no authorization to retrieve a
   *     {@linkplain TaskComment} from a certain {@linkplain Task} or is not authorized to access
   *     the {@linkplain Task}
   * @throws TaskNotFoundException if the given taskId in the {@linkplain TaskComment} does not
   *     refer to an existing {@linkplain Task}
   */
  List<TaskComment> getTaskComments(String taskId)
      throws NotAuthorizedException, TaskNotFoundException;

  /**
   * Sets the callback state on a list of {@linkplain Task Tasks}.
   *
   * <p>Note: this method is primarily intended to be used by the TaskanaAdapter.
   *
   * @param externalIds the EXTERNAL_IDs of the {@linkplain Task Tasks} on which the callback state
   *     is set
   * @param state the callback state that is to be set on the {@linkplain Task Tasks}
   * @return the result of the operations with Id and Exception for each failed {@linkplain Task}
   *     deletion
   */
  BulkOperationResults<String, TaskanaException> setCallbackStateForTasks(
      List<String> externalIds, CallbackState state);

  /**
   * Sets the owner on a list of {@linkplain Task Tasks}. The owner will only be set on {@linkplain
   * Task Tasks} that are in state READY.
   *
   * @param owner the new owner of the {@linkplain Task Tasks}
   * @param taskIds the IDs of the {@linkplain Task Tasks} on which the owner is to be set.
   * @return the result of the operations with Id and Exception for each failed {@linkplain Task}
   *     update.
   */
  BulkOperationResults<String, TaskanaException> setOwnerOfTasks(
      String owner, List<String> taskIds);

  /**
   * Sets the planned property on a list of {@linkplain Task Tasks}.
   *
   * <p>Only {@linkplain Task Tasks} in state READY and CLAIMED will be affected by this method. On
   * each {@linkplain Task}, the corresponding due date is set according to the shortest service
   * level in the {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * of the {@linkplain Task} and the {@linkplain Task}'s {@linkplain Attachment Attachments}.
   *
   * @param planned the new 'PLANNED' property of the tasks
   * @param taskIds the IDs of the {@linkplain Task Tasks} on which the new planned property is to
   *     be set
   * @return the result of the operations with Id and Exception for each failed task update
   */
  BulkOperationResults<String, TaskanaException> setPlannedPropertyOfTasks(
      Instant planned, List<String> taskIds);

  /**
   * Cancels a {@linkplain Task}.
   *
   * <p>Cancellation means a {@linkplain Task} is obsolete from a business perspective an does not
   * need to be completed anymore.
   *
   * @param taskId the id of the {@linkplain Task} to cancel
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with Id TaskId is not found
   * @throws InvalidStateException if the {@linkplain Task} is not in state READY or CLAIMED
   * @throws NotAuthorizedException if the current user is not authorized to see the {@linkplain
   *     Task}
   */
  Task cancelTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Terminates a {@linkplain Task}.
   *
   * <p>Termination is a administrative action to complete a {@linkplain Task}. This is typically
   * done by an administration to correct any technical issue.
   *
   * @param taskId the id of the {@linkplain Task} to cancel.
   * @return the updated {@linkplain Task}.
   * @throws TaskNotFoundException if the {@linkplain Task} with Id TaskId is not found
   * @throws InvalidStateException if the {@linkplain Task} is not in state READY or CLAIMED
   * @throws NotAuthorizedException if the current user is not authorized to see the {@linkplain
   *     Task}
   */
  Task terminateTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;
}
