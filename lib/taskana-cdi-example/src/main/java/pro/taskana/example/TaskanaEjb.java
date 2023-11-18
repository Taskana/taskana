package pro.taskana.example;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketService;

/** example Taskana EJB. */
@Stateless
public class TaskanaEjb {

  private final TaskService taskService;

  private final ClassificationService classificationService;

  private final WorkbasketService workbasketService;

  public TaskanaEjb() {
    this.taskService = null;
    this.classificationService = null;
    this.workbasketService = null;
  }

  @Inject
  public TaskanaEjb(
      TaskService taskService,
      ClassificationService classificationService,
      WorkbasketService workbasketService) {
    this.taskService = taskService;
    this.classificationService = classificationService;
    this.workbasketService = workbasketService;
  }

  public TaskService getTaskService() {
    return taskService;
  }

  public ClassificationService getClassificationService() {
    return classificationService;
  }

  public WorkbasketService getWorkbasketService() {
    return workbasketService;
  }
}
