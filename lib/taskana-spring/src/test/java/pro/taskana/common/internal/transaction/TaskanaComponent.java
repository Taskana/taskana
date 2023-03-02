package pro.taskana.common.internal.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
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
      throws WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException,
          ObjectReferencePersistenceException, NotAuthorizedOnWorkbasketException {
    Task task = taskService.newTask("1");
    task.setName("Unit Test Task");
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
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
