package org.taskana;

import org.taskana.configuration.TaskanaEngineConfiguration;

/**
 * The TaskanaEngine represents an overall set of all needed services
 */
public interface TaskanaEngine {

	/**
	 * The TaskService can be used for operations on all Tasks
	 * 
	 * @return the TaskService
	 */
	public TaskService getTaskService();

	/**
	 * The WorkbasketService can be used for operations on all Workbaskets
	 * 
	 * @return the TaskService
	 */
	public WorkbasketService getWorkbasketService();

	/**
	 * The CategoryService can be used for operations on all Categories
	 * 
	 * @return the TaskService
	 */
	public CategoryService getCategoryService();

	/**
	 * The Taskana configuration
	 * @return the TaskanaConfiguration
	 */
	public TaskanaEngineConfiguration getConfiguration();
}
