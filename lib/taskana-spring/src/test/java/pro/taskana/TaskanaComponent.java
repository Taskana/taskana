package pro.taskana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.TaskImpl;

@Component
@Transactional
public class TaskanaComponent {

	@Autowired
	TaskService taskService;

	public TaskService getTaskService() {
		return taskService;
	}

	public void triggerRollback() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
		Task task = new TaskImpl();
		task.setName("Unit Test Task");
		task.setWorkbasketId("1");
		task = taskService.createTask(task);
		throw new RuntimeException();
	}
}
