package pro.taskana;

import java.util.List;

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
import pro.taskana.impl.TaskState;

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
     *             if the state of the task with taskId is not {@link TaskState#READY}
     * @throws InvalidOwnerException
     *             if the task with taskId is claimed by some else
     */
    Task claim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

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
     *             if the state of the task with taskId is not {@link TaskState#READY}
     * @throws InvalidOwnerException
     *             if the task with taskId is claimed by someone else
     */
    Task claim(String taskId, boolean forceClaim)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

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
     */
    Task cancelClaim(String taskId) throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

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
     */
    Task cancelClaim(String taskId, boolean forceCancel)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

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
     */
    Task completeTask(String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException;

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
     */
    Task completeTask(String taskId, boolean isForced)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException;

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
     * @throws InvalidWorkbasketException
     *             thrown if the referenced Workbasket has missing required properties
     * @throws InvalidArgumentException
     *             thrown if the primary ObjectReference is invalid
     */
    Task createTask(Task taskToCreate)
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException;

    /**
     * Get the details of a task by Id without checking permissions.
     *
     * @param taskId
     *            the id of the task
     * @return the Task
     * @throws TaskNotFoundException
     *             thrown of the {@link Task} with taskId is not found
     */
    Task getTask(String taskId) throws TaskNotFoundException;

    /**
     * Transfer a task to another work basket. The transfer sets the transferred flag and resets the read flag.
     *
     * @param taskId
     *            The id of the {@link Task} to be transferred
     * @param workbasketKey
     *            The key of the target work basket
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
    Task transfer(String taskId, String workbasketKey)
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
     */
    Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException;

    /**
     * This method provides a query builder for quering the database.
     *
     * @return a {@link TaskQuery}
     */
    TaskQuery createTaskQuery();

    /**
     * Getting a list of all Task summaries which got matching workbasketIds and states.
     *
     * @param workbasketKey
     *            the key of the workbasket where the tasks need to be in.
     * @param taskState
     *            which is required for the request,
     * @return a filled/empty list of tasks with attributes which are matching given params.
     * @throws WorkbasketNotFoundException
     *             if the workbasketId can´t be resolved to a existing work basket.
     * @throws NotAuthorizedException
     *             if the current user got no rights for reading on this work basket.
     * @throws ClassificationNotFoundException
     *             if a single Classification can not be found for a task which is returned
     */
    List<TaskSummary> getTasksByWorkbasketKeyAndState(String workbasketKey, TaskState taskState)
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException;

    /**
     * Getting a short summary of all tasks in a specific work basket.
     *
     * @param workbasketKey
     *            Key of work basket where tasks are located.
     * @return TaskSummaryList with all TaskSummaries of a work basket
     * @throws WorkbasketNotFoundException
     *             if a Work basket can´t be located.
     * @throws NotAuthorizedException
     *             if the current user got no rights for reading on this work basket.
     */
    List<TaskSummary> getTaskSummariesByWorkbasketKey(String workbasketKey)
        throws WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Returns a not persisted instance of {@link Task}.
     *
     * @param workbasketKey
     *            the key of the workbasket to which the task belongs
     * @return an empty new Task
     */
    Task newTask(String workbasketKey);

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
     * @param destinationWorkbasketKey
     *            target workbasket key
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
    BulkOperationResults<String, TaskanaException> transferTasks(String destinationWorkbasketKey, List<String> taskIds)
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
     */
    void deleteTask(String taskId) throws TaskNotFoundException, InvalidStateException;

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
     */
    void deleteTask(String taskId, boolean forceDelete) throws TaskNotFoundException, InvalidStateException;

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
     * @throws NotAuthorizedException
     */
    BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
        throws InvalidArgumentException, NotAuthorizedException;
}
