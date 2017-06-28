package org.taskana;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.model.Task;

@Stateless
public class TaskanaEjb {

	@Inject
	private TaskService taskService;

	public TaskService getTaskService() {
		return taskService;
	}

	public void triggerRollback() throws NotAuthorizedException {
		Task t = taskService.create(new Task());
		System.out.println("---------------->" + t.getId());
		throw new RuntimeException();
	}

}
