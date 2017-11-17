package pro.taskana;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.model.Task;

@Component
@Transactional
public class ExampleBootstrap {

	@Autowired
	private TaskService taskService;

	@PostConstruct
	public void test() throws TaskNotFoundException, NotAuthorizedException {
		System.out.println("---------------------------> Start App");
		Task task = new Task();
		task.setName("Spring example task");
		task.setWorkbasketId("1");
		task = taskService.create(task);
		System.out.println("---------------------------> Task started: " + task.getId());
		taskService.claim(task.getId(), "John Doe");
		System.out.println(
		    "---------------------------> Task claimed: " + taskService.getTaskById(task.getId()).getOwner());
		// taskService.complete(task.getId());
		// System.out.println("---------------------------> Task completed");
	}

}
