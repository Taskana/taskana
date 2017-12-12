package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;
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
     * @param taskId - Id of the Task which should be completed.
     *
     * @return Task - updated task after completion.
     *
     * @throws InvalidStateException when Task wasn´t claimed before.
     * @throws TaskNotFoundException if the given Task can´t be found in DB.
     * @throws InvalidOwnerException if current user is not the task-owner or administrator.
     */
    Task completeTask(String taskId) throws TaskNotFoundException, InvalidOwnerException, InvalidStateException;

    /**
     * Complete a claimed Task and update State and Timestamps.
     *
     * @param taskId - Id of the Task which should be completed.
     * @param isForced - Flag which can complete a Task in every case if Task does exist.
     *
     * @return Task - updated task after completion.
     *
     * @throws InvalidStateException when Task wasn´t claimed before.
     * @throws TaskNotFoundException if the given Task can´t be found in DB.
     * @throws InvalidOwnerException if current user is not the task-owner or administrator.
     */
    Task completeTask(String taskId, boolean isForced) throws TaskNotFoundException, InvalidOwnerException, InvalidStateException;

    /**
     * Create and persist a task.
     *
     * @param task
     *            the transient task object to be persisted
     * @return the created and persisted task
     * @throws NotAuthorizedException
     *             thrown if the current user is not authorized to create that task
     * @throws WorkbasketNotFoundException
     *             thrown if the work basket referenced by the task is not found
     * @throws ClassificationNotFoundException
     *             thrown if the {@link Classification} referenced by the task is not found
     */
    Task createTask(Task task)
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException;

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
     * This method counts all tasks with a given state.
     *
     * @param states
     *            the countable states
     * @return a List of {@link TaskStateCounter} objects that specifies how many tasks in the specified states exist in
     *         the available work baskets
     */
    List<TaskStateCounter> getTaskCountForState(List<TaskState> states);

    /**
     * Count all Tasks in a given work basket where the due date is after "daysInPast" days from today in the past and
     * the tasks are in specified states.
     *
     * @param workbasketId
     *            the id of the work basket
     * @param daysInPast
     *            identifies the days in the past from today
     * @param states
     *            {@link List} of {@link TaskState} that identifies the states of the tasks to be searched for
     * @return the number of {@link Task} objects in the given work basket that match the query parameters
     */
    long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast, List<TaskState> states);

    /**
     * Count all Tasks for all work basket objects where the due date is after "daysInPast" days from today in the past
     * and the tasks are in specified states.
     *
     * @param daysInPast
     *            identifies the days in the past from today
     * @param states
     *            {@link List} of {@link TaskState} objects that identifies the states of the tasks searched
     * @return a list of of {@link DueWorkbasketCounter} objects that specifies how many tasks in the requested states
     *         with appropriate due date exist in the various work baskets
     */
    List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast, List<TaskState> states);

    /**
     * Transfer a task to another work basket. The transfer sets the transferred flag and resets the read flag.
     *
     * @param taskId
     *            The id of the {@link Task} to be transferred
     * @param workbasketId
     *            The id of the target work basket
     * @return the transferred task
     * @throws TaskNotFoundException
     *             Thrown if the {@link Task} with taskId was not found.
     * @throws WorkbasketNotFoundException
     *             Thrown if the target work basket was not found.
     * @throws NotAuthorizedException
     *             Thrown if the current user is not authorized to transfer this {@link Task} to the target work basket
     */
    Task transfer(String taskId, String workbasketId)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException;

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
     * @param workbasketId
     *            where the tasks need to be in.
     * @param taskState
     *            which is required for the request,
     * @return a filled/empty list of tasks with attributes which are matching given params.
     * @throws WorkbasketNotFoundException
     *             if the workbasketId can´t be resolved to a existing work basket.
     * @throws NotAuthorizedException
     *             if the current user got no rights for reading on this work basket.
     */
    List<Task> getTasksByWorkbasketIdAndState(String workbasketId, TaskState taskState)
        throws WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Getting a short summary of all tasks in a specific work basket.
     *
     * @param workbasketId
     *            ID of work basket where tasks are located.
     * @return TaskSummaryList with all TaskSummaries of a work basket
     * @throws WorkbasketNotFoundException
     *             if a Work basket can´t be located.
     */
    List<TaskSummary> getTaskSummariesByWorkbasketId(String workbasketId) throws WorkbasketNotFoundException;
}
