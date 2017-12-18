package pro.taskana;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

@Component
@Transactional
public class ExampleBootstrap {

	@Autowired
	private TaskService taskService;

	@PostConstruct
	public void test() throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, InvalidStateException, InvalidOwnerException {
		System.out.println("---------------------------> Start App");
		Task task = taskService.newTask();
		task.setName("Spring example task");
		task.setWorkbasketId("1");
		task = taskService.createTask(task);
		System.out.println("---------------------------> Task started: " + task.getId());
		taskService.claim(task.getId());
		System.out.println(
		    "---------------------------> Task claimed: " + taskService.getTaskById(task.getId()).getOwner());
		 taskService.completeTask(task.getId(), true);
		 System.out.println("---------------------------> Task completed");
	}

}
