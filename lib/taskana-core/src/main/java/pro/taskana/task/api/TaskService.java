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
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
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

/** The TaskService manages all operations on {@linkplain Task Tasks}. */
public interface TaskService {

  // region Task

  // region CREATE

  /**
   * Instantiates a non-persistent/non-inserted {@linkplain Task}.
   *
   * <p>Since a {@linkplain Task} doesn't allow setting a {@linkplain Task#getWorkbasketSummary()
   * workbasketSummary}, please either provide an implementation of the {@linkplain
   * TaskRoutingProvider} or use the referenced methods to create a {@linkplain Task} within a
   * specific {@linkplain Workbasket}.
   *
   * @return the instantiated {@linkplain Task}
   * @see #newTask(String)
   * @see #newTask(String, String)
   */
  Task newTask();

  /**
   * Instantiates a non-persistent/non-inserted {@linkplain Task}.
   *
   * @param workbasketId the {@linkplain Workbasket#getId() id} of the {@linkplain Workbasket} to
   *     which the {@linkplain Task} belongs
   * @return the instantiated {@linkplain Task}
   * @see #newTask()
   * @see #newTask(String, String)
   */
  Task newTask(String workbasketId);

  /**
   * Instantiates a non-persistent/non-inserted {@linkplain Task}.
   *
   * @param workbasketKey the {@linkplain Workbasket#getKey() key} of the {@linkplain Workbasket} to
   *     which the {@linkplain Task} belongs
   * @param domain the {@linkplain Workbasket#getDomain() domain} of the {@linkplain Workbasket} to
   *     which the {@linkplain Task} belongs
   * @return the instantiated {@linkplain Task}
   * @see #newTask()
   * @see #newTask(String)
   */
  Task newTask(String workbasketKey, String domain);

  /**
   * Inserts a {@linkplain Task} that doesn't exist in the database yet.
   *
   * <p>If the {@linkplain Task#getWorkbasketSummary() workbasketSummary} of the given {@linkplain
   * Task} is NULL, TaskService will call the {@linkplain TaskRoutingProvider} to determine a
   * {@linkplain Workbasket} for the {@linkplain Task}. If the {@linkplain TaskRoutingProvider} is
   * not active, e.g. because no {@linkplain TaskRoutingProvider} is registered, or the {@linkplain
   * TaskRoutingProvider} doesn't find a {@linkplain Workbasket}, the {@linkplain Task} will not be
   * inserted.
   *
   * <p>The default values of the created {@linkplain Task} are:
   *
   * <ul>
   *   <li><b>{@linkplain Task#getId() id}</b> - generated automatically
   *   <li><b>{@linkplain Task#getExternalId() externalId}</b> - if NULL, then generated
   *       automatically, else unchanged
   *   <li><b>{@linkplain Task#getBusinessProcessId() businessProcessId}</b> - generated
   *       automatically
   *   <li><b>{@linkplain Task#getName() name}</b> - if NULL then the name of its {@linkplain
   *       Classification}, else unchanged
   *   <li><b>{@linkplain Task#getDescription() description}</b> - if NULL then description of its
   *       {@linkplain Classification}, else unchanged
   *   <li><b>{@linkplain Task#getCreator() creator}</b> - id of current user
   *   <li><b>{@linkplain Task#getState() state}</b> - {@linkplain TaskState#READY}
   *   <li><b>{@linkplain Task#isRead() isRead}</b> - false
   *   <li><b>{@linkplain Task#isTransferred() isTransferred}</b> - false
   * </ul>
   *
   * @param taskToCreate the transient {@linkplain Task} to be inserted
   * @return the created and inserted {@linkplain Task}
   * @throws TaskAlreadyExistException if the {@linkplain Task} already exists
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#APPEND} for the {@linkplain Workbasket} the {@linkplain Task} is in
   * @throws WorkbasketNotFoundException if the {@linkplain Workbasket} referenced by the
   *     {@linkplain Task#getWorkbasketSummary() workbasketSummary} of the {@linkplain Task} isn't
   *     found
   * @throws ClassificationNotFoundException if the {@linkplain Classification} referenced by
   *     {@linkplain Task#getClassificationSummary() classificationSummary} of the {@linkplain Task}
   *     isn't found
   * @throws InvalidArgumentException if the {@linkplain Task#getPrimaryObjRef() primaryObjRef} is
   *     invalid
   * @throws AttachmentPersistenceException if an {@linkplain Attachment} with the same {@linkplain
   *     Attachment#getId() id} was added to the {@linkplain Task} multiple times without using
   *     {@linkplain Task#addAttachment(Attachment)}
   * @throws ObjectReferencePersistenceException if an {@linkplain ObjectReference} with the same
   *     {@linkplain ObjectReference#getId() id} was added to the {@linkplain Task} multiple times
   *     without using {@linkplain Task#addSecondaryObjectReference(ObjectReference)}
   */
  Task createTask(Task taskToCreate)
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException,
          ObjectReferencePersistenceException;

  // endregion

  // region READ

  /**
   * Fetches a {@linkplain Task} from the database by the specified {@linkplain Task#getId() id}.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task}
   * @return the {@linkplain Task} with the specified taskId
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task getTask(String taskId) throws TaskNotFoundException, NotAuthorizedException;

  // endregion

  // region UPDATE

  /**
   * Claim an existing {@linkplain Task} for the current user.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to be claimed
   * @return the claimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId isn't {@linkplain TaskState#READY}
   * @throws InvalidOwnerException if the {@linkplain Task} with taskId is claimed by some else
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task claim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Claim an existing {@linkplain Task} for the current user even if it is already claimed by
   * someone else.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to be claimed
   * @return the claimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the state of Task with taskId is in {@linkplain
   *     TaskState#END_STATES}
   * @throws InvalidOwnerException cannot be thrown
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Selects and claims the first {@linkplain Task} which is returned by the {@linkplain TaskQuery}.
   *
   * @param taskQuery the {@linkplain TaskQuery}
   * @return the {@linkplain Task} that got selected and claimed
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by someone else
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task selectAndClaim(TaskQuery taskQuery) throws NotAuthorizedException, InvalidOwnerException;

  /**
   * Cancel the claim of an existing {@linkplain Task} if it was claimed by the current user before.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     unclaimed
   * @return the unclaimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the {@linkplain Task} is already in one of the {@linkplain
   *     TaskState#END_STATES}
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by another user
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task cancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Cancel the claim of an existing {@linkplain Task} even if it was claimed by another user.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     unclaimed
   * @return the unclaimed {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws InvalidStateException if the {@linkplain Task} is already in one of the {@linkplain
   *     TaskState#END_STATES}
   * @throws InvalidOwnerException cannot be thrown
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceCancelClaim(String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException;

  /**
   * Request review for an existing {@linkplain Task} that is in {@linkplain TaskState#CLAIMED}.
   *
   * @param taskId the {@linkplain Task#getId() id} of the specified {@linkplain Task}
   * @return the {@linkplain Task} after a review has been requested
   * @throws InvalidTaskStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is not in {@linkplain TaskState#CLAIMED}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by another user
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task requestReview(String taskId)
      throws InvalidTaskStateException, TaskNotFoundException, NotAuthorizedException,
          InvalidOwnerException;

  /**
   * Request review for an existing {@linkplain Task} even if the current user is not the
   * {@linkplain Task#getOwner() owner} or the Task is not in {@linkplain TaskState#CLAIMED} yet.
   *
   * @param taskId the {@linkplain Task#getId() id} of the specified {@linkplain Task}
   * @return the {@linkplain Task} after a review has been requested
   * @throws InvalidTaskStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is one of the {@linkplain TaskState#END_STATES}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidOwnerException cannot be thrown
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceRequestReview(String taskId)
      throws InvalidTaskStateException, TaskNotFoundException, NotAuthorizedException,
          InvalidOwnerException;

  /**
   * Request changes for an existing {@linkplain Task} that is in {@linkplain TaskState#IN_REVIEW}.
   * The {@linkplain TaskState} is changed to {@linkplain TaskState#READY} after changes have been
   * requested.
   *
   * @param taskId the {@linkplain Task#getId() id} of the specified {@linkplain Task}
   * @return the {@linkplain Task} after changes have been requested
   * @throws InvalidTaskStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is not in {@linkplain TaskState#IN_REVIEW}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidOwnerException if the {@linkplain Task} is claimed by another user
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task requestChanges(String taskId)
      throws InvalidTaskStateException, TaskNotFoundException, NotAuthorizedException,
          InvalidOwnerException;

  /**
   * Request changes for an existing {@linkplain Task} even if the current user is not the
   * {@linkplain Task#getOwner() owner} or the Task is not in {@linkplain TaskState#IN_REVIEW} yet.
   * The {@linkplain TaskState} is changed to {@linkplain TaskState#READY} after changes have been
   * requested.
   *
   * @param taskId the {@linkplain Task#getId() id} of the specified {@linkplain Task}
   * @return the {@linkplain Task} after changes have been requested
   * @throws InvalidTaskStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is one of the {@linkplain TaskState#END_STATES}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidOwnerException cannot be thrown
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceRequestChanges(String taskId)
      throws InvalidTaskStateException, TaskNotFoundException, NotAuthorizedException,
          InvalidOwnerException;

  /**
   * Complete a claimed {@linkplain Task} as {@linkplain Task#getOwner() owner} or {@linkplain
   * TaskanaRole#ADMIN} and update {@linkplain Task#getState() state} and timestamps.
   *
   * <p>If the {@linkplain Task} is already completed, the {@linkplain Task} is returned unchanged.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     completed
   * @return the completed {@linkplain Task}
   * @throws InvalidStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is neither {@linkplain TaskState#CLAIMED} nor {@linkplain
   *     TaskState#COMPLETED}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidOwnerException if current user isn't the {@linkplain Task#getOwner() owner} of
   *     the {@linkplain Task} or {@linkplain TaskanaRole#ADMIN}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task completeTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Completes a {@linkplain Task} and updates {@linkplain Task#getState() state} and timestamps in
   * every case if the {@linkplain Task} exists.
   *
   * <p>If the {@linkplain Task} is already completed, the {@linkplain Task} is returned unchanged.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     completed
   * @return the updated {@linkplain Task} after completion
   * @throws InvalidStateException if the {@linkplain Task#getState() state} of the {@linkplain
   *     Task} with taskId is with taskId is {@linkplain TaskState#TERMINATED} or {@linkplain
   *     TaskState#CANCELLED}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidOwnerException if current user isn't the {@linkplain Task#getOwner() owner} of
   *     the {@linkplain Task} or {@linkplain TaskanaRole#ADMIN}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task forceCompleteTask(String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException;

  /**
   * Completes a List of {@linkplain Task Tasks}.
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
   * Task}.
   *
   * <p>If the {@linkplain Task} is already {@linkplain TaskState#COMPLETED completed}, the
   * {@linkplain Task} stays unchanged.
   *
   * @param taskIds {@linkplain Task#getId() id} of the {@linkplain Task Tasks} which should be
   *     completed
   * @return the result of the operations with {@linkplain Task#getId() ids} and Exception for each
   *     failed completion
   * @throws InvalidArgumentException If the taskIds parameter is NULL
   */
  BulkOperationResults<String, TaskanaException> forceCompleteTasks(List<String> taskIds)
      throws InvalidArgumentException;

  /**
   * Cancels the {@linkplain Task} with the given {@linkplain Task#getId() id}.
   *
   * <p>Cancellation means a {@linkplain Task} is obsolete from a business perspective and doesn't
   * need to be completed anymore.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to cancel
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidStateException if the {@linkplain Task} isn't in {@linkplain TaskState#READY} or
   *     {@linkplain TaskState#CLAIMED}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task cancelTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Terminates a {@linkplain Task}. Termination is an administrative action to complete a
   * {@linkplain Task}.
   *
   * <p>This is typically done by administration to correct any technical issue.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to terminate
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws InvalidStateException if the {@linkplain Task} isn't in {@linkplain TaskState#READY} or
   *     {@linkplain TaskState#CLAIMED}
   * @throws NotAuthorizedException if the current user isn't member of {@linkplain
   *     TaskanaRole#ADMIN} or {@linkplain TaskanaRole#TASK_ADMIN}
   */
  Task terminateTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Transfers a {@linkplain Task} to another {@linkplain Workbasket} while always setting
   * {@linkplain Task#isTransferred() isTransferred} to true.
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
   * <p>The transfer resets {@linkplain Task#isRead() isRead} and sets {@linkplain
   * Task#isTransferred() isTransferred} if setTransferFlag is true.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     transferred
   * @param destinationWorkbasketId the {@linkplain Workbasket#getId() id} of the target {@linkplain
   *     Workbasket}
   * @param setTransferFlag controls whether to set {@linkplain Task#isTransferred() isTransferred}
   *     to true or not
   * @return the transferred {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws WorkbasketNotFoundException if the target {@linkplain Workbasket} was not found
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the source {@linkplain Workbasket} or no {@linkplain
   *     WorkbasketPermission#TRANSFER} for the target {@linkplain Workbasket}
   * @throws InvalidStateException if the {@linkplain Task} is in one of the {@linkplain
   *     TaskState#END_STATES}
   */
  Task transfer(String taskId, String destinationWorkbasketId, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Transfers a {@linkplain Task} to another {@linkplain Workbasket} while always setting
   * {@linkplain Task#isTransferred isTransferred} .
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
   * <p>The transfer resets {@linkplain Task#isRead() isRead} and sets {@linkplain
   * Task#isTransferred() isTransferred} if setTransferFlag is true.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} which should be
   *     transferred
   * @param workbasketKey the {@linkplain Workbasket#getKey() key} of the target {@linkplain
   *     Workbasket}
   * @param domain the {@linkplain Workbasket#getDomain() domain} of the target {@linkplain
   *     Workbasket}
   * @param setTransferFlag controls whether to set {@linkplain Task#isTransferred() isTransferred}
   *     or not
   * @return the transferred {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId was not found
   * @throws WorkbasketNotFoundException if the target {@linkplain Workbasket} was not found
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the source {@linkplain Workbasket} or no {@linkplain
   *     WorkbasketPermission#TRANSFER} for the target {@linkplain Workbasket}
   * @throws InvalidStateException if the {@linkplain Task} is in one of the {@linkplain
   *     TaskState#END_STATES}
   */
  Task transfer(String taskId, String workbasketKey, String domain, boolean setTransferFlag)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException;

  /**
   * Transfers a List of {@linkplain Task Tasks} to another {@linkplain Workbasket} while always
   * setting {@linkplain Task#isTransferred isTransferred} to true.
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
   * Transfers a List of {@linkplain Task Tasks} to another {@linkplain Workbasket}.
   *
   * <p>The transfer resets {@linkplain Task#isRead() isRead} and sets {@linkplain
   * Task#isTransferred() isTransferred} if setTransferFlag is true. Exceptions will be thrown if
   * the caller got no {@linkplain WorkbasketPermission} on the target or if the target {@linkplain
   * Workbasket} doesn't exist. Other Exceptions will be stored and returned in the end.
   *
   * @param destinationWorkbasketId {@linkplain Workbasket#getId() id} of the target {@linkplain
   *     Workbasket}
   * @param taskIds List of source {@linkplain Task Tasks} which will be moved
   * @param setTransferFlag controls whether to set {@linkplain Task#isTransferred() isTransferred}
   *     or not
   * @return Bulkresult with {@linkplain Task#getId() ids} and Error for each failed transactions
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the source {@linkplain Workbasket} or no {@linkplain
   *     WorkbasketPermission#TRANSFER} for the target {@linkplain Workbasket}
   * @throws InvalidArgumentException if the method parameters are empty or NULL
   * @throws WorkbasketNotFoundException if the target {@linkplain Workbasket} can't be found
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketId, List<String> taskIds, boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Transfers a List of {@linkplain Task Tasks} to another {@linkplain Workbasket} while always
   * setting {@linkplain Task#isTransferred() isTransferred} to true.
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
   * Transfers a List of {@linkplain Task Tasks} to another {@linkplain Workbasket}.
   *
   * <p>The transfer resets {@linkplain Task#isRead() isRead} and sets {@linkplain
   * Task#isTransferred() isTransferred} if setTransferFlag is true. Exceptions will be thrown if
   * the caller got no {@linkplain WorkbasketPermission} on the target {@linkplain Workbasket} or if
   * it doesn't exist. Other Exceptions will be stored and returned in the end.
   *
   * @param destinationWorkbasketKey target {@linkplain Workbasket#getKey() key}
   * @param destinationWorkbasketDomain target {@linkplain Workbasket#getDomain() domain}
   * @param taskIds List of {@linkplain Task#getId() ids} of source {@linkplain Task Tasks} which
   *     will be moved
   * @param setTransferFlag controls whether to set {@linkplain Task#isTransferred() isTransferred}
   *     or not
   * @return BulkResult with {@linkplain Task#getId() ids} and Error for each failed transactions
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the source {@linkplain Workbasket} or no {@linkplain
   *     WorkbasketPermission#TRANSFER} for the target {@linkplain Workbasket}
   * @throws InvalidArgumentException if the method parameters are empty or NULL
   * @throws WorkbasketNotFoundException if the target {@linkplain Workbasket} can't be found
   */
  BulkOperationResults<String, TaskanaException> transferTasks(
      String destinationWorkbasketKey,
      String destinationWorkbasketDomain,
      List<String> taskIds,
      boolean setTransferFlag)
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

  /**
   * Update a {@linkplain Task}.
   *
   * @param task the {@linkplain Task} to be updated
   * @return the updated {@linkplain Task}
   * @throws InvalidArgumentException if the {@linkplain Task} to be updated contains invalid
   *     properties like e.g. invalid {@linkplain Task#getSecondaryObjectReferences()
   *     secondaryObjectReferences}
   * @throws TaskNotFoundException if the {@linkplain Task} isn't found in the database by its
   *     {@linkplain Task#getId() id}
   * @throws ConcurrencyException if the {@linkplain Task} has been updated by another user in the
   *     meantime; that's the case if the given {@linkplain Task#getModified() modified} timestamp
   *     differs from the one in the database
   * @throws ClassificationNotFoundException if the {@linkplain Task#getClassificationSummary()
   *     classificationSummary} of the updated {@linkplain Task} refers to a {@link Classification}
   *     that can't be found
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   * @throws AttachmentPersistenceException if an {@linkplain Attachment} with the same {@linkplain
   *     Attachment#getId() id} was added to the {@linkplain Task} multiple times without using
   *     {@linkplain Task#addAttachment(Attachment)}
   * @throws ObjectReferencePersistenceException if an {@linkplain ObjectReference} with the same
   *     {@linkplain ObjectReference#getId() id} was added to the {@linkplain Task} multiple times
   *     without using {@linkplain Task#addSecondaryObjectReference(ObjectReference)}
   * @throws InvalidStateException if an attempt is made to change the {@linkplain Task#getOwner()
   *     owner} of the {@linkplain Task} that {@linkplain Task#getState() state} isn't {@linkplain
   *     TaskState#READY}
   */
  Task updateTask(Task task)
      throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          ObjectReferencePersistenceException, InvalidStateException;

  /**
   * Updates specified {@linkplain TaskCustomField TaskCustomFields} of {@linkplain Task Tasks}
   * associated with the given {@linkplain Task#getPrimaryObjRef() primaryObjRef}.
   *
   * @param selectionCriteria the {@linkplain Task#getPrimaryObjRef() primaryObjRef} of the
   *     searched-for {@linkplain Task Tasks}.
   * @param customFieldsToUpdate a Map that contains as key the identification of the {@linkplain
   *     TaskCustomField} and as value the corresponding new value of that {@linkplain
   *     TaskCustomField}
   * @return a List of the {@linkplain Task#getId() ids} of all modified {@linkplain Task Tasks}
   * @throws InvalidArgumentException if the given selectionCriteria is invalid or the given
   *     customFieldsToUpdate are NULL or empty
   * @see #updateTasks(List, Map)
   */
  List<String> updateTasks(
      ObjectReference selectionCriteria, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Updates specified {@linkplain TaskCustomField TaskCustomFields} for all given {@linkplain Task
   * Tasks}.
   *
   * @param taskIds the {@linkplain Task#getId() taskIds} that are used to select the {@linkplain
   *     Task Tasks}
   * @param customFieldsToUpdate a Map that contains as key the identification of the {@linkplain
   *     TaskCustomField} and as value the corresponding new value of that {@linkplain
   *     TaskCustomField}
   * @return a list of the {@linkplain Task#getId() ids} of all modified {@linkplain Task Tasks}
   * @throws InvalidArgumentException if the given customFieldsToUpdate are NULL or empty
   * @see #updateTasks(ObjectReference, Map)
   */
  List<String> updateTasks(List<String> taskIds, Map<TaskCustomField, String> customFieldsToUpdate)
      throws InvalidArgumentException;

  /**
   * Sets the value of {@linkplain Task#isRead() isRead} of the specified {@linkplain Task}.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to be updated
   * @param isRead the new status of {@linkplain Task#isRead() isRead}
   * @return the updated {@linkplain Task}
   * @throws TaskNotFoundException if the {@linkplain Task} with taskId wasn't found
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} the {@linkplain Task} is in
   */
  Task setTaskRead(String taskId, boolean isRead)
      throws TaskNotFoundException, NotAuthorizedException;

  /**
   * Sets the specified {@linkplain CallbackState} on a List of {@linkplain Task Tasks}.
   *
   * <p>Note: this method is primarily intended to be used by the TaskanaAdapter
   *
   * @param externalIds the {@linkplain Task#getExternalId() externalIds} of the {@linkplain Task
   *     Tasks} on which the {@linkplain CallbackState} is set
   * @param state the {@linkplain CallbackState} that is to be set on the {@linkplain Task Tasks}
   * @return the result of the operations with {@linkplain Task#getId() ids} and Exception for each
   *     failed operation
   */
  BulkOperationResults<String, TaskanaException> setCallbackStateForTasks(
      List<String> externalIds, CallbackState state);

  /**
   * Sets the {@linkplain Task#getOwner() owner} on a List of {@linkplain Task Tasks}.
   *
   * <p>The {@linkplain Task#getOwner() owner} will only be set on {@linkplain Task Tasks} that are
   * in {@linkplain TaskState#READY}.
   *
   * @param owner the new {@linkplain Task#getOwner() owner} of the {@linkplain Task Tasks}
   * @param taskIds the {@linkplain Task#getId() ids} of the {@linkplain Task Tasks} on which the
   *     {@linkplain Task#getOwner() owner} is to be set
   * @return the result of the operations with {@linkplain Task#getId() ids} and Exception for each
   *     failed {@linkplain Task}-update
   */
  BulkOperationResults<String, TaskanaException> setOwnerOfTasks(
      String owner, List<String> taskIds);

  /**
   * Sets the {@linkplain Task#getPlanned() planned} Instant on a List of {@linkplain Task Tasks}.
   *
   * <p>Only {@linkplain Task Tasks} in state {@linkplain TaskState#READY} and {@linkplain
   * TaskState#CLAIMED} will be affected by this method. On each {@linkplain Task}, the
   * corresponding {@linkplain Task#getDue() due} Instant is set according to the shortest
   * serviceLevel in the {@linkplain Task#getClassificationSummary() Classification} of the
   * {@linkplain Task} and its {@linkplain Task#getAttachments() Attachments}.
   *
   * @param planned the new {@linkplain Task#getPlanned() planned} Instant of the {@linkplain Task
   *     Tasks}
   * @param taskIds the {@linkplain Task#getId() ids} of the {@linkplain Task Tasks} on which the
   *     new {@linkplain Task#getPlanned() planned} Instant is to be set
   * @return the result of the operations with {@linkplain Task#getId() ids} and Exception for each
   *     failed {@linkplain Task} update
   */
  BulkOperationResults<String, TaskanaException> setPlannedPropertyOfTasks(
      Instant planned, List<String> taskIds);

  // endregion

  // region DELETE

  /**
   * Deletes the {@linkplain Task} with the given {@linkplain Task#getId() id}.
   *
   * @param taskId The {@linkplain Task#getId() id} of the {@linkplain Task} to delete
   * @throws TaskNotFoundException If the given {@linkplain Task#getId() id} doesn't refer to an
   *     existing {@linkplain Task}
   * @throws InvalidStateException If the {@linkplain Task#getState() state} of the referenced
   *     {@linkplain Task} isn't one of the {@linkplain TaskState#END_STATES}
   * @throws NotAuthorizedException if the current user isn't member of {@linkplain
   *     TaskanaRole#ADMIN}
   */
  void deleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes the {@linkplain Task} with the given {@linkplain Task#getId() id} even if it isn't
   * completed.
   *
   * @param taskId The {@linkplain Task#getId() id} of the {@linkplain Task} to delete
   * @throws TaskNotFoundException if the given {@linkplain Task#getId() id} doesn't refer to an
   *     existing {@linkplain Task}
   * @throws InvalidStateException if the {@linkplain Task#getState() state} of the referenced
   *     {@linkplain Task} isn't {@linkplain TaskState#TERMINATED} or {@linkplain
   *     TaskState#CANCELLED} and the Callback State of the Task is {@linkplain
   *     CallbackState#CALLBACK_PROCESSING_REQUIRED}
   * @throws NotAuthorizedException if the current user isn't member of {@linkplain
   *     TaskanaRole#ADMIN}
   */
  void forceDeleteTask(String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

  /**
   * Deletes a List of {@linkplain Task Tasks}.
   *
   * @param tasks the {@linkplain Task#getId() ids} of the {@linkplain Task Tasks} to delete
   * @return the result of the operations with each {@linkplain Task#getId() id} and Exception for
   *     each failed deletion
   * @throws InvalidArgumentException if the tasks parameter contains NULL values
   * @throws NotAuthorizedException if the current user isn't member of {@linkplain
   *     TaskanaRole#ADMIN}
   */
  BulkOperationResults<String, TaskanaException> deleteTasks(List<String> tasks)
      throws InvalidArgumentException, NotAuthorizedException;

  // endregion

  // endregion

  // region TaskComment

  // region CREATE
  /**
   * Instantiates a non-persistent/non-inserted {@linkplain TaskComment}.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} to which the
   *     {@linkplain TaskComment} belongs
   * @return the instantiated {@linkplain TaskComment}
   */
  TaskComment newTaskComment(String taskId);

  /**
   * Inserts the specified {@linkplain TaskComment} into the database.
   *
   * @param taskComment the {@linkplain TaskComment} to be created
   * @return the created {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} of the commented {@linkplain
   *     Task}.
   * @throws TaskNotFoundException if the given {@linkplain TaskComment#getTaskId() taskId} doesn't
   *     refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the {@linkplain TaskComment#getId() id} of the provided
   *     {@link TaskComment} is neither NULL nor empty
   */
  TaskComment createTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException;

  // endregion

  // region READ

  /**
   * Retrieves the {@linkplain TaskComment} with the given {@linkplain TaskComment#getId() id}.
   *
   * @param taskCommentId the {@linkplain TaskComment#getId() id} of the {@linkplain TaskComment}
   *     which should be retrieved
   * @return the {@linkplain TaskComment} identified by taskCommentId
   * @throws TaskCommentNotFoundException if the given taskCommentId doesn't refer to an existing
   *     {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} of the commented {@linkplain
   *     Task}
   * @throws TaskNotFoundException if the {@linkplain TaskComment#getTaskId() taskId} of the
   *     TaskComment doesn't refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the given taskCommentId is NULL or empty
   */
  TaskComment getTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException;

  /**
   * Retrieves the List of {@linkplain TaskComment TaskComments} for the {@linkplain Task} with
   * given {@linkplain Task#getId() id}.
   *
   * @param taskId the {@linkplain Task#getId() id} of the {@linkplain Task} for which all
   *     {@linkplain TaskComment TaskComments} should be retrieved
   * @return the List of {@linkplain TaskComment TaskComments} attached to the specified {@linkplain
   *     Task}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} of the commented {@linkplain
   *     Task}
   * @throws TaskNotFoundException if the given taskId doesn't refer to an existing {@linkplain
   *     Task}
   */
  List<TaskComment> getTaskComments(String taskId)
      throws NotAuthorizedException, TaskNotFoundException;

  // endregion

  // region UPDATE

  /**
   * Updates the specified {@linkplain TaskComment}.
   *
   * @param taskComment the {@linkplain TaskComment} to be updated in the database
   * @return the updated {@linkplain TaskComment}
   * @throws NotAuthorizedException if the current user has no {@linkplain
   *     WorkbasketPermission#READ} for the {@linkplain Workbasket} of the commented {@linkplain
   *     Task}.
   * @throws ConcurrencyException if an attempt is made to update the {@linkplain TaskComment} and
   *     another user updated it already; that's the case if the given {@linkplain
   *     Task#getModified() modified} timestamp differs from the one in the database
   * @throws TaskCommentNotFoundException if the {@linkplain TaskComment#getId() id} of the
   *     specified {@linkplain TaskComment} doesn't refer to an existing {@linkplain TaskComment}
   * @throws TaskNotFoundException if the {@linkplain TaskComment#getTaskId() taskId} doesn't refer
   *     to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the {@linkplain TaskComment#getId() id} of the specified
   *     {@linkplain TaskComment} is NULL or empty
   */
  TaskComment updateTaskComment(TaskComment taskComment)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException, InvalidArgumentException;

  // endregion

  // region DELETE

  /**
   * Deletes the {@linkplain TaskComment} with the given {@linkplain TaskComment#getId() id}.
   *
   * @param taskCommentId the {@linkplain TaskComment#getId() id} of the {@linkplain TaskComment} to
   *     delete
   * @throws NotAuthorizedException if the current user isn't {@linkplain TaskanaRole#ADMIN},
   *     {@linkplain TaskanaRole#TASK_ADMIN} or the {@linkplain TaskComment#getCreator() creator} of
   *     the {@linkplain TaskComment}; the user also needs {@linkplain WorkbasketPermission#READ}
   *     for the {@linkplain Workbasket} of the commented {@linkplain Task}
   * @throws InvalidArgumentException if the taskCommentId is NULL or empty
   * @throws TaskCommentNotFoundException if the given taskCommentId doesn't refer to an existing
   *     {@linkplain TaskComment}
   * @throws TaskNotFoundException if the {@linkplain TaskComment#getTaskId() taskId} of the
   *     TaskComment doesn't refer to an existing {@linkplain Task}
   * @throws InvalidArgumentException if the given taskCommentId is NULL or empty
   */
  void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException;

  // endregion

  // endregion

  /**
   * Instantiates a non-persistent/non-inserted {@linkplain Attachment}.
   *
   * @return the instantiated {@linkplain Attachment}
   */
  Attachment newAttachment();

  /**
   * Instantiates a non-persistent/non-inserted {@linkplain ObjectReference}.
   *
   * @return the instantiated {@linkplain ObjectReference}
   * @see #newObjectReference(String, String, String, String, String)
   */
  ObjectReference newObjectReference();

  /**
   * Instantiates a non-persistent/non-inserted {@linkplain ObjectReference}.
   *
   * @param company the {@linkplain ObjectReference#getCompany() company} of the new {@linkplain
   *     ObjectReference}
   * @param system the {@linkplain ObjectReference#getSystem() system} of the new {@linkplain
   *     ObjectReference}
   * @param systemInstance the {@linkplain ObjectReference#getSystemInstance() systemInstance} of
   *     the new {@linkplain ObjectReference}
   * @param type the {@linkplain ObjectReference#getType() type} of the new {@linkplain
   *     ObjectReference}
   * @param value the {@linkplain ObjectReference#getValue() value} of the new {@linkplain
   *     ObjectReference}
   * @return the instantiated {@linkplain ObjectReference}
   * @see #newObjectReference()
   */
  ObjectReference newObjectReference(
      String company, String system, String systemInstance, String type, String value);

  /**
   * Creates an empty {@linkplain TaskQuery}.
   *
   * @return a {@linkplain TaskQuery}
   */
  TaskQuery createTaskQuery();

  /**
   * Creates an empty {@linkplain TaskCommentQuery}.
   *
   * @return a {@linkplain TaskCommentQuery}
   */
  TaskCommentQuery createTaskCommentQuery();
}
