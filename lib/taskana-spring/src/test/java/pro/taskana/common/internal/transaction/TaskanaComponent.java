package pro.taskana.common.internal.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** TODO. */
@Component
@Transactional
public class TaskanaComponent {

  @Autowired TaskService taskService;

  public TaskService getTaskService() {
    return taskService;
  }

  public void triggerRollback()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException {
    Task task = taskService.newTask("1");
    task.setName("Unit Test Task");
    ObjectReference objRef = new ObjectReference();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);

    taskService.createTask(task);
    throw new RuntimeException();
  }
}
