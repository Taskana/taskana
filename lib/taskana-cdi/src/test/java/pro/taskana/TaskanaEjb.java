package pro.taskana;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.WorkbasketService;

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
