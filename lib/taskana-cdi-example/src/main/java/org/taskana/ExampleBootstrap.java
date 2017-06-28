package org.taskana;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.TaskNotFoundException;
import org.taskana.model.Task;

@ApplicationScoped
public class ExampleBootstrap {

	@EJB
	private TaskanaEjb taskanaEjb;

	@PostConstruct
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) throws TaskNotFoundException, NotAuthorizedException {
		System.out.println("---------------------------> Start App");
		Task task = taskanaEjb.getTaskService().create(new Task());
		System.out.println("---------------------------> Task started: " + task.getId());
		taskanaEjb.getTaskService().claim(task.getId(), "John Doe");
		System.out.println(
		    "---------------------------> Task claimed: "
		        + taskanaEjb.getTaskService().getTaskById(task.getId()).getOwner());
		// taskService.complete(task.getId());
		// System.out.println("---------------------------> Task completed");
	}

}
