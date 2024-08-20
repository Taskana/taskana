package io.kadai.example;

import io.kadai.classification.api.ClassificationService;
import io.kadai.task.api.TaskService;
import io.kadai.workbasket.api.WorkbasketService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/** example Kadai EJB. */
@Stateless
public class KadaiEjb {

  private final TaskService taskService;

  private final ClassificationService classificationService;

  private final WorkbasketService workbasketService;

  public KadaiEjb() {
    this.taskService = null;
    this.classificationService = null;
    this.workbasketService = null;
  }

  @Inject
  public KadaiEjb(
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
