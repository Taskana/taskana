package pro.taskana;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Task;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class ExampleBootstrap {

	@EJB
	private TaskanaEjb taskanaEjb;

	@PostConstruct
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
		System.out.println("---------------------------> Start App");
		Task task = taskanaEjb.getTaskService().createTask(new Task());
		System.out.println("---------------------------> Task started: " + task.getId());
		taskanaEjb.getTaskService().claim(task.getId(), "John Doe");
		System.out.println(
		    "---------------------------> Task claimed: "
		        + taskanaEjb.getTaskService().getTaskById(task.getId()).getOwner());
		// taskService.complete(task.getId());
		// System.out.println("---------------------------> Task completed");
	}

}
