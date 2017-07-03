package org.taskana;

import java.util.List;

import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.TaskNotFoundException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.model.DueWorkbasketCounter;
import org.taskana.model.Task;
import org.taskana.model.TaskStateCounter;
import org.taskana.model.TaskState;

public interface TaskService {

	/**
	 * Claim an existing task
	 * 
	 * @param id
	 *            task id
	 * @param userName
	 *            user who claims the task
	 * @throws TaskNotFoundException
	 */
	public void claim(String id, String userName) throws TaskNotFoundException;

	/**
	 * Set task to completed
	 * 
	 * @param taskId
	 *            the task id
	 * @throws TaskNotFoundException
	 */
	public void complete(String taskId) throws TaskNotFoundException;

	/**
	 * Create a task by a task object
	 * 
	 * @param task
	 * @return the created task
	 * @throws NotAuthorizedException
	 */
	public Task create(Task task) throws NotAuthorizedException;

	/**
	 * Get the details of a task
	 * 
	 * @param taskId
	 *            the id of the task
	 * @return the Task
	 */
	public Task getTaskById(String taskId) throws TaskNotFoundException;

	/**
	 * Query all tasks for a workbasket.
	 * 
	 * @param workbasketId
	 *            the workbasket to query
	 * @return the list of tasks, which reside in the workbasket
	 * @throws NotAuthorizedException
	 */
	public List<Task> getTasksForWorkbasket(String workbasketId) throws NotAuthorizedException;

	/**
	 * Query all tasks for a workbasket.
	 * 
	 * @param workbasketId
	 *            the workbasket to query
	 * @return the list of tasks, which reside in the workbasket
	 * @throws NotAuthorizedException
	 */
	public List<Task> getTasksForWorkbasket(List<String> workbaskets, List<TaskState> states)
			throws NotAuthorizedException;

	/**
	 * This method returns all Tasks
	 * 
	 * @return a {@link List<Task>} of {@link Task}
	 */
	public List<Task> getTasks();

	/**
	 * This method counts all tasks with a given state.
	 * 
	 * @param states
	 *            the countable states
	 * @return a List of {@link TaskStateCounter}
	 */
	public List<TaskStateCounter> getTaskCountForState(List<TaskState> states);

	/**
	 * This method returns all tasks with the specified states
	 * 
	 * @param states  all List with the needed states
	 * @return a list of Tasks
	 */
	public List<Task> findTasks(List<TaskState> states);

	/**
	 * Count all Tasks in a given workbasket with daysInPast as Days from today
	 * in the past and a specific state.
	 * 
	 * @param workbasketId
	 * @param daysInPast
	 * @param states
	 * @return
	 */
	public long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast,
			List<TaskState> states);

	/**
	 * Transfer task to another workbasket.
	 * 
	 * The transfer set the transferred flag and resets the read flag.
	 * 
	 * @param workbasketId
	 * @return the updated task
	 * @throws NotAuthorizedException
	 */
	public Task transfer(String taskId, String workbasketId)
			throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException;
	
	/**
	 * Marks a task as read. 
	 * 
	 * @param taskId the id of the task to be updated
	 * @param isRead the new status of the read flag.
	 * @return Task the updated Task
	 */
	public Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException;

	public List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast,
			List<TaskState> states);

}
