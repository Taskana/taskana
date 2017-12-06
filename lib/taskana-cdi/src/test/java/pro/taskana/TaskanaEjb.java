package pro.taskana;

import javax.ejb.Stateless;
import javax.inject.Inject;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Task;

@Stateless
public class TaskanaEjb {

	@Inject
	private TaskService taskService;

	public TaskService getTaskService() {
		return taskService;
	}

	public void triggerRollback() throws NotAuthorizedException, WorkbasketNotFoundException {
		Task t = taskService.create(new Task());
		System.out.println("---------------->" + t.getId());
		throw new RuntimeException();
	}

}
