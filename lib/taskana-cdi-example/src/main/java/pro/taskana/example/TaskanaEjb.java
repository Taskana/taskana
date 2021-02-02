package pro.taskana.example;

import javax.ejb.Stateless;
import javax.inject.Inject;

import pro.taskana.task.api.TaskService;

/** example Taskana EJB. */
@Stateless
public class TaskanaEjb {

  @Inject private TaskService taskService;

  public TaskService getTaskService() {
    return taskService;
  }
}
