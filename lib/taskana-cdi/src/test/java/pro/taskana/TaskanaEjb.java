package pro.taskana;

import javax.ejb.Stateless;
import javax.inject.Inject;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

@Stateless
public class TaskanaEjb {

  @Inject private TaskService taskService;

  @Inject private ClassificationService classificationService;

  @Inject private WorkbasketService workbasketService;

  public TaskService getTaskService() {
    return taskService;
  }

  public ClassificationService getClassificationService() {
    return classificationService;
  }

  public WorkbasketService getWorkbasketService() {
    return workbasketService;
  }

  public void triggerRollback()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException {
    final Task task = taskService.newTask(null);
    ObjectReference objRef = new ObjectReference();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);

    taskService.createTask(task);
    System.out.println("---------------->" + task.getId());
    throw new RuntimeException();
  }
}
