package pro.taskana.example;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** The ExampleBootstrap contains an example Bootstrap Application. */
@ApplicationScoped
public class ExampleBootstrap {

  @EJB private TaskanaEjb taskanaEjb;

  @PostConstruct
  public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
      throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException,
          ClassificationNotFoundException, InvalidStateException, InvalidOwnerException,
          TaskAlreadyExistException, InvalidArgumentException, AttachmentPersistenceException,
          ObjectReferencePersistenceException {
    System.out.println("---------------------------> Start App");
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    Task task = taskanaEjb.getTaskService().newTask(null);
    task.setPrimaryObjRef(objRef);
    task = taskanaEjb.getTaskService().createTask(task);
    System.out.println("---------------------------> Task started: " + task.getId());
    taskanaEjb.getTaskService().claim(task.getId());
    System.out.println(
        "---------------------------> Task claimed: "
            + taskanaEjb.getTaskService().getTask(task.getId()).getOwner());
    taskanaEjb.getTaskService().completeTask(task.getId());
    System.out.println("---------------------------> Task completed");
  }
}
