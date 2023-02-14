package pro.taskana.example;

import javax.ejb.Stateless;
import javax.inject.Inject;
import lombok.Getter;

import pro.taskana.task.api.TaskService;

/** example Taskana EJB. */
@Stateless
@Getter
public class TaskanaEjb {

  @Inject private TaskService taskService;
}
