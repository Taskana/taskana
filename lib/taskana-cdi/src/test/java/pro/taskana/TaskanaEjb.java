package pro.taskana;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Task;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class TaskanaEjb {

	@Inject
	private TaskService taskService;

	@Inject
	private ClassificationService classificationService;

	@Inject
	private WorkbasketService workbasketService;

	public TaskService getTaskService() {
		return taskService;
	}

	public ClassificationService getClassificationService() {
		return classificationService;
	}

	public WorkbasketService getWorkbasketService() {
		return workbasketService;
	}

	public void triggerRollback() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
		Task t = taskService.createTask(new Task());
		System.out.println("---------------->" + t.getId());
		throw new RuntimeException();
	}

}
