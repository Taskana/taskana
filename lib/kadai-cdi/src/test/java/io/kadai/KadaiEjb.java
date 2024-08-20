package io.kadai;

import io.kadai.classification.api.ClassificationService;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.workbasket.api.WorkbasketService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

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

  public void triggerRollback(String workbasketId, String classificationKey) throws Exception {

    final Task task = taskService.newTask(workbasketId);
    task.setClassificationKey(classificationKey);
    task.setName("triggerRollback");
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);

    taskService.createTask(task);
    System.out.println("---------------->" + task.getId());
    throw new RuntimeException("Expected Test Exception");
  }
}
