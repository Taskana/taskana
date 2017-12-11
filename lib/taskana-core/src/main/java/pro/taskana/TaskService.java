package pro.taskana;

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

import java.util.List;

/**
 * The Task Service manages all operations on tasks.
 */
public interface TaskService {

    /**
     * Claim an existing task for the current user.
     * @param id
     *            task id
     * @return modified claimed Task
     * @throws TaskNotFoundException if the task with id was not found
     * @throws InvalidStateException if the task state is not ready
     * @throws InvalidOwnerException if the task is claimed by another user
     */
    Task claim(String id) throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

    /**
     * Claim an existing task for the current user.
     * @param id
     *            task id
     * @param forceClaim
     *            if true, claim is performed even if the task is already claimed by someone else
     * @return modified claimed Task
     * @throws TaskNotFoundException if the task with id was not found
     * @throws InvalidStateException if the task state is not ready
     * @throws InvalidOwnerException if the task is claimed by another user
     */
    Task claim(String id, boolean forceClaim) throws TaskNotFoundException, InvalidStateException, InvalidOwnerException;

    /**
     * Set task to completed.
     * @param taskId
     *            the task id
     * @return changed Task after update.
     * @throws TaskNotFoundException TODO
     */
    Task complete(String taskId) throws TaskNotFoundException;

    /**
     * Create a task by a task object.
     * @param task TODO
     * @return the created task
     * @throws NotAuthorizedException TODO
     * @throws WorkbasketNotFoundException TODO
     * @throws ClassificationNotFoundException TODO
     */
    Task createTask(Task task) throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException;

    /**
     * Get the details of a task.
     * @param taskId
     *            the id of the task
     * @return the Task
     * @throws TaskNotFoundException TODO
     */
    Task getTaskById(String taskId) throws TaskNotFoundException;

    /**
     * This method counts all tasks with a given state.
     * @param states
     *            the countable states
     * @return a List of {@link TaskStateCounter}
     */
    List<TaskStateCounter> getTaskCountForState(List<TaskState> states);

    /**
     * Count all Tasks in a given workbasket with daysInPast as Days from today in
     * the past and a specific state.
     * @param workbasketId TODO
     * @param daysInPast TODO
     * @param states TODO
     * @return TODO
     */
    long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast, List<TaskState> states);

    List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast, List<TaskState> states);

    /**
     * Transfer task to another workbasket. The transfer set the transferred flag
     * and resets the read flag.
     * @param taskId TODO
     * @param workbasketId TODO
     * @return the updated task
     * @throws TaskNotFoundException TODO
     * @throws WorkbasketNotFoundException TODO
     * @throws NotAuthorizedException TODO
     */
    Task transfer(String taskId, String workbasketId)
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Marks a task as read.
     * @param taskId
     *            the id of the task to be updated
     * @param isRead
     *            the new status of the read flag.
     * @return Task the updated Task
     * @throws TaskNotFoundException TODO
     */
    Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException;

    /**
     * This method provides a query builder for quering the database.
     * @return a {@link TaskQuery}
     */
    TaskQuery createTaskQuery();

    /**
     * Getting a list of all Tasks which got matching workbasketIds and states.
     *
     * @param workbasketId where the tasks need to be in.
     * @param taskState which is required for the request,
     * @return a filled/empty list of tasks with attributes which are matching given params.
     *
     * @throws WorkbasketNotFoundException if the workbasketId can´t be resolved to a existing workbasket.
     * @throws NotAuthorizedException if the current user got no rights for reading on this workbasket.
     * @throws Exception if no result can be found by @{link TaskMapper}.
     */
    List<Task> getTasksByWorkbasketIdAndState(String workbasketId, TaskState taskState) throws  WorkbasketNotFoundException, NotAuthorizedException, Exception;

    /**
     * Getting a short summary of all tasks in a specific workbasket.
     * @param workbasketId ID of workbasket where tasks are located.
     * @return TaskSummaryList with all TaskSummaries of a workbasket
     * @throws WorkbasketNotFoundException if a Workbasket can´t be located.
     */
    List<TaskSummary> getTaskSummariesByWorkbasketId(String workbasketId) throws WorkbasketNotFoundException;
}
