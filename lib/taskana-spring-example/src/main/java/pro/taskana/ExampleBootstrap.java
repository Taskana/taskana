package pro.taskana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.NotOwnerException;
import pro.taskana.exceptions.TaskNotClaimedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Task;

import javax.annotation.PostConstruct;

@Component
@Transactional
public class ExampleBootstrap {

	@Autowired
	private TaskService taskService;

	@PostConstruct
	public void test() throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, TaskNotClaimedException, NotOwnerException {
		System.out.println("---------------------------> Start App");
		Task task = new Task();
		task.setName("Spring example task");
		task.setWorkbasketId("1");
		task = taskService.createTask(task);
		System.out.println("---------------------------> Task started: " + task.getId());
		taskService.claim(task.getId(), "John Doe");
		System.out.println(
		    "---------------------------> Task claimed: " + taskService.getTaskById(task.getId()).getOwner());
		 taskService.completeTask(task.getId(), true);
		 System.out.println("---------------------------> Task completed");
	}

}
