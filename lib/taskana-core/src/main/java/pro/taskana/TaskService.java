package pro.taskana;

import java.util.List;
import java.util.Map;

import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.BulkOperationResults;
import pro.taskana.impl.ObjectReference;

/**
 * The Task Service manages all operations on tasks.
 */
public interface TaskService {

    /**
     * Claim an existing task for the current user.
     *
     * @param taskId
     *            the id of the task to be claimed
     * @return claimed Task
     * @throws TaskNotFoundException
     *             if the task with taskId was not found
     * @throws InvalidStateException
     *             if the state of the task with taskId is not READY
     * @throws InvalidOwnerException
     *             if the task with taskId is claimed by some else
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task claim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException;

    /**
     * Claim an existing task for the current user. Enable forced claim.
     *
     * @param taskId
     *            the id of the task to be claimed
     * @param forceClaim
     *            if true, claim is performed even if the task is already claimed by someone else
     * @return claimed Task
     * @throws TaskNotFoundException
     *             if the task with taskId was not found
     * @throws InvalidStateException
     *             if the state of the task with taskId is not READY
     * @throws InvalidOwnerException
     *             if the task with taskId is claimed by someone else
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task claim(String taskId, boolean forceClaim)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException;

    /**
     * Unclaim a existing Task which was claimed and owned by you before.
     *
     * @param taskId
     *            id of the task which should be unclaimed.
     * @return updated unclaimed task
     * @throws TaskNotFoundException
     *             if the task can´t be found or does not exist
     * @throws InvalidStateException
     *             when the task is already completed.
     * @throws InvalidOwnerException
     *             when the task is claimed by another user.
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task cancelClaim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException;

    /**
     * Unclaim a existing Task which was claimed and owned by you before. Also there can be enabled a force flag for
     * admins.
     *
     * @param taskId
     *            id of the task which should be unclaimed.
     * @param forceCancel
     *            force the cancellation of claim. If true, the task is unclaimed even if it was claimed by another
     *            user.
     * @return updated unclaimed task
     * @throws TaskNotFoundException
     *             if the task can´t be found or does not exist
     * @throws InvalidStateException
     *             when the task is already completed.
     * @throws InvalidOwnerException
     *             when forceCancel is false and the task is claimed by another user.
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task cancelClaim(String taskId, boolean forceCancel)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException;

    /**
     * Complete a claimed Task as owner/admin and update State and Timestamps.
     *
     * @param taskId
     *            - Id of the Task which should be completed.
     * @return Task - updated task after completion.
     * @throws InvalidStateException
     *             when Task wasn´t claimed before.
     * @throws TaskNotFoundException
     *             if the given Task can´t be found in DB.
     * @throws InvalidOwnerException
     *             if current user is not the task-owner or administrator.
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task completeTask(String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException;

    /**
     * Complete a claimed Task and update State and Timestamps.
     *
     * @param taskId
     *            - Id of the Task which should be completed.
     * @param isForced
     *            - Flag which can complete a Task in every case if Task does exist.
     * @return Task - updated task after completion.
     * @throws InvalidStateException
     *             when Task wasn´t claimed before.
     * @throws TaskNotFoundException
     *             if the given Task can´t be found in DB.
     * @throws InvalidOwnerException
     *             if current user is not the task-owner or administrator.
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task completeTask(String taskId, boolean isForced)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException;

    /**
     * Persists a not persisted Task which does not exist already.
     *
     * @param taskToCreate
     *            the transient task object to be persisted
     * @return the created and persisted task
     * @throws TaskAlreadyExistException
     *             when the Task does already exist.
     * @throws NotAuthorizedException
     *             thrown if the current user is not authorized to create that task
     * @throws WorkbasketNotFoundException
     *             thrown if the work basket referenced by the task is not found
     * @throws ClassificationNotFoundException
     *             thrown if the {@link Classification} referenced by the task is not found
     * @throws InvalidArgumentException
     *             thrown if the primary ObjectReference is invalid
     */
    Task createTask(Task taskToCreate)
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, InvalidArgumentException;

    /**
     * Get the details of a task by Id without checking permissions.
     *
     * @param taskId
     *            the id of the task
     * @return the Task
     * @throws TaskNotFoundException
     *             thrown of the {@link Task} with taskId is not found
     * @throws NotAuthorizedException
     *             if the current user has no READ permission for the workbasket the task is in.
     */
    Task getTask(String taskId) throws TaskNotFoundException, NotAuthorizedException;

    /**
     * Transfer a task to another work basket. The transfer sets the transferred flag and resets the read flag.
     *
     * @param taskId
     *            The id of the {@link Task} to be transferred
     * @param destinationWorkbasketId
     *            The Id of the target work basket
     * @return the transferred task
     * @throws TaskNotFoundException
     *             Thrown if the {@link Task} with taskId was not found.
     * @throws WorkbasketNotFoundException
     *             Thrown if the target work basket was not found.
     * @throws NotAuthorizedException
     *             Thrown if the current user is not authorized to transfer this {@link Task} to the target work basket
     * @throws InvalidWorkbasketException
     *             Thrown if either the source or the target workbasket has a missing required property
     */
    Task transfer(String taskId, String destinationWorkbasketId)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException;

    /**
     * Transfer a task to another work basket. The transfer sets the transferred flag and resets the read flag.
     *
     * @param taskId
     *            The id of the {@link Task} to be transferred
     * @param workbasketKey
     *            The key of the target work basket
     * @param domain
     *            The domain of the target work basket
     * @return the transferred task
     * @throws TaskNotFoundException
     *             Thrown if the {@link Task} with taskId was not found.
     * @throws WorkbasketNotFoundException
     *             Thrown if the target work basket was not found.
     * @throws NotAuthorizedException
     *             Thrown if the current user is not authorized to transfer this {@link Task} to the target work basket
     * @throws InvalidWorkbasketException
     *             Thrown if either the source or the target workbasket has a missing required property
     */
    Task transfer(String taskId, String workbasketKey, String domain)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException;

    /**
     * Marks a task as read.
     *
     * @param taskId
     *            the id of the task to be updated
     * @param isRead
     *            the new status of the read flag.
     * @return the updated Task
     * @throws TaskNotFoundException
     *             Thrown if the {@link Task} with taskId was not found
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the workbasket the task is in
     */
    Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException, NotAuthorizedException;

    /**
     * This method provides a query builder for quering the database.
     *
     * @return a {@link TaskQuery}
     */
    TaskQuery createTaskQuery();

    /**
     * Returns a not persisted instance of {@link Task}.
     *
     * @param workbasketId
     *            the id of the workbasket to which the task belongs
     * @return an empty new Task
     */
    Task newTask(String workbasketId);

    /**
     * Returns a not persisted instance of {@link Task}.
     *
     * @param workbasketKey
     *            the key of the workbasket to which the task belongs
     * @param domain
     *            the domain of the workbasket to which the task belongs
     * @return an empty new Task
     */
    Task newTask(String workbasketKey, String domain);

    /**
     * Returns a not persisted instance of {@link Attachment}.
     *
     * @return an empty new Attachment
     */
    Attachment newAttachment();

    /**
     * Update a task.
     *
     * @param task
     *            the task to be updated in the database
     * @return the updated task
     * @throws InvalidArgumentException
     *             if the task to be updated contains invalid properties like e.g. invalid object references
     * @throws TaskNotFoundException
     *             if the id of the task is not found in the database
     * @throws ConcurrencyException
     *             if the task has already been updated by another user
     * @throws InvalidWorkbasketException
     *             if the updated task refers to a workbasket that has missing required properties
     * @throws ClassificationNotFoundException
     *             if the updated task refers to a classification that cannot be found
     * @throws WorkbasketNotFoundException
     *             if the updated task refers to a work basket that cannot be found
     * @throws NotAuthorizedException
     *             if the current user is not authorized to update the task
     * @throws AttachmentPersistenceException
     *             if an Attachment with ID will be added multiple times without using the task-methods.
     */
    Task updateTask(Task task) throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
        WorkbasketNotFoundException, ClassificationNotFoundException, InvalidWorkbasketException,
        NotAuthorizedException, AttachmentPersistenceException;

    /**
     * Transfers a list of tasks to an other workbasket. Exceptions will be thrown if the caller got no permissions on
     * the target or it doesn´t exist. Other Exceptions will be stored and returned in the end.
     *
     * @param destinationWorkbasketId
     *            target workbasket id
     * @param taskIds
     *            source task which will be moved
     * @return Bulkresult with ID and Error in it for failed transactions.
     * @throws NotAuthorizedException
     *             if the caller hasn´t permissions on tarket WB.
     * @throws InvalidArgumentException
     *             if the method paramesters are EMPTY or NULL.
     * @throws WorkbasketNotFoundException
     *             if the target WB can´t be found.
     */
    BulkOperationResults<String, TaskanaException> transferTasks(String destinationWorkbasketId, List<String> taskIds)
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

    /**
     * Transfers a list of tasks to an other workbasket. Exceptions will be thrown if the caller got no permissions on
     * the target or it doesn´t exist. Other Exceptions will be stored and returned in the end.
     *
     * @param destinationWorkbasketKey
     *            target workbasket key
     * @param destinationWorkbasketDomain
     *            target workbasket domain
     * @param taskIds
     *            source task which will be moved
     * @return Bulkresult with ID and Error in it for failed transactions.
     * @throws NotAuthorizedException
     *             if the caller hasn´t permissions on tarket WB.
     * @throws InvalidArgumentException
     *             if the method paramesters are EMPTY or NULL.
     * @throws WorkbasketNotFoundException
     *             if the target WB can´t be found.
     */
    BulkOperationResults<String, TaskanaException> transferTasks(String destinationWorkbasketKey,
        String destinationWorkbasketDomain, List<String> taskIds)
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException;

    /**
     * Deletes the task with the given Id.
     *
     * @param taskId
     *            The Id of the task to delete.
     * @throws TaskNotFoundException
     *             If the given Id does not refer to an existing task.
     * @throws InvalidStateException
     *             If the state of the referenced task is not Completed.
     * @throws NotAuthorizedException
     *             if the current user is not member of role ADMIN
     */
    void deleteTask(String taskId) throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

    /**
     * Deletes the task with the given Id.
     *
     * @param taskId
     *            The Id of the task to delete.
     * @param forceDelete
     *            force the deletion. If true, a task is deleted even if it is not in state completed.
     * @throws TaskNotFoundException
     *             If the given Id does not refer to an existing task.
     * @throws InvalidStateException
     *             If the state of the referenced task is not Completed and forceDelet is false.
     * @throws NotAuthorizedException
     *             if the current user is not member of role ADMIN
     */
    void deleteTask(String taskId, boolean forceDelete)
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException;

    /**
     * Deletes a list of tasks.
     *
     * @param tasks
     *            the ids of the tasks to delete.
     * @return the result of the operations with Id and Exception for each failed task deletion.
     * @throws InvalidArgumentException
     *             if the TaskIds parameter is NULL
     */
    BulkOperationResults<String, TaskanaException> deleteTasks(List<String> tasks) throws InvalidArgumentException;

    /**
     * Completes a list of tasks.
     *
     * @param taskIds
     *            of the tasks which should be completed.
     * @return the result of the operations with Id and Exception for each failed task completion.
     * @throws InvalidArgumentException
     *             If the taskId parameter is NULL.
     */
    BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
        throws InvalidArgumentException;

    /**
     * Completes tasks with a matching {@link ObjectReference}.
     *
     * @param selectionCriteria
     *            the {@link ObjectReference} that is used to select the tasks.
     * @param customFieldsToUpdate
     *            a {@link Map} that contains as key the identification of the custom field and as value the
     *            corresponding new value of that custom field. The key for identification of the custom field must be a
     *            String with value "1", "2" ... "16" as in the setCustomAttribute or getCustomAttribute method of
     *            {@link Task}
     * @return a list of the Ids of all modified tasks
     * @throws InvalidArgumentException
     *             If the customFieldsToUpdate map contains an invalid key or if the selectionCriteria is invalid
     */
    List<String> updateTasks(ObjectReference selectionCriteria,
        Map<String, String> customFieldsToUpdate) throws InvalidArgumentException;
}
