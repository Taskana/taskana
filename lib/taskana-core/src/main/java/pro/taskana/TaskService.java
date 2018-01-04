package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskSummary;

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
    Task claim(String taskId) throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

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
    Task completeTask(String taskId) throws TaskNotFoundException, InvalidOwnerException, InvalidStateException;

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
     * Get the details of a task by Id.
     *
     * @param taskId
     *            the id of the task
     * @return the Task
     * @throws TaskNotFoundException
     *             thrown of the {@link Task} with taskId is not found
     */
    Task getTaskById(String taskId) throws TaskNotFoundException;

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
     * Getting a list of all Tasks which got matching workbasketIds and states.
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
     */
    List<Task> getTasksByWorkbasketKeyAndState(String workbasketKey, TaskState taskState)
        throws WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Getting a short summary of all tasks in a specific work basket.
     *
     * @param workbasketKey
     *            Key of work basket where tasks are located.
     * @return TaskSummaryList with all TaskSummaries of a work basket
     * @throws WorkbasketNotFoundException
     *             if a Work basket can´t be located.
     * @throws InvalidWorkbasketException
     *             thrown if the Workbasket specified with workbasketId has a missing required property
     */
    List<TaskSummary> getTaskSummariesByWorkbasketKey(String workbasketKey)
        throws WorkbasketNotFoundException, InvalidWorkbasketException;

    /**
     * Returns a not persisted instance of {@link Task}.
     *
     * @return an empty new Task
     */
    Task newTask();

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
     */
    Task updateTask(Task task) throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException;
}
