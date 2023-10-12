package pro.taskana.example;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pro.taskana.task.api.TaskService;

/** example Taskana EJB. */
@Stateless
public class TaskanaEjb {

  @Inject private TaskService taskService;

  public TaskService getTaskService() {
    return taskService;
  }
}
