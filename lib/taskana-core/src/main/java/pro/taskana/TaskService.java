package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;

/**
 * The Task Service manages all operations on tasks.
 */
public interface TaskService {

    /**
     * Claim an existing task.
     * @param id
     *            task id
     * @param userName
     *            user who claims the task
     * @throws TaskNotFoundException
     */
    void claim(String id, String userName) throws TaskNotFoundException;

    /**
     * Set task to completed.
     * @param taskId
     *            the task id
     * @throws TaskNotFoundException
     */
    void complete(String taskId) throws TaskNotFoundException;

    /**
     * Create a task by a task object.
     * @param task
     * @return the created task
     * @throws NotAuthorizedException
     */
    Task create(Task task) throws NotAuthorizedException;

    /**
     * Get the details of a task.
     * @param taskId
     *            the id of the task
     * @return the Task
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
     * @param workbasketId
     * @param daysInPast
     * @param states
     * @return
     */
    long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast, List<TaskState> states);

    List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast, List<TaskState> states);

    /**
     * Transfer task to another workbasket. The transfer set the transferred flag
     * and resets the read flag.
     * @param workbasketId
     * @return the updated task
     * @throws NotAuthorizedException
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
}
