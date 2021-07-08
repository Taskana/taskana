package pro.taskana.example;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.exceptions.MalformedServiceLevelException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/** TODO. */
@Component
@Transactional
public class ExampleBootstrap {

  @Autowired private TaskService taskService;

  @Autowired private TaskanaEngine taskanaEngine;

  @PostConstruct
  public void test()
      throws InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException,
          NotAuthorizedException, ClassificationAlreadyExistException,
          MalformedServiceLevelException, TaskAlreadyExistException, WorkbasketNotFoundException,
          ClassificationNotFoundException, AttachmentPersistenceException, TaskNotFoundException,
          InvalidOwnerException, InvalidStateException {
    System.out.println("---------------------------> Start App");

    Workbasket wb = taskanaEngine.getWorkbasketService().newWorkbasket("workbasket", "DOMAIN_A");
    wb.setName("workbasket");
    wb.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(wb);
    Classification classification =
        taskanaEngine.getClassificationService().newClassification("TEST", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    taskanaEngine.getClassificationService().createClassification(classification);

    Task task = taskanaEngine.getTaskService().newTask(wb.getId());
    task.setName("Spring example task");
    task.setClassificationKey(classification.getKey());
    ObjectReference objRef = new ObjectReference();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);
    task = taskService.createTask(task);
    System.out.println("---------------------------> Task started: " + task.getId());
    taskService.claim(task.getId());
    System.out.println(
        "---------------------------> Task claimed: "
            + taskService.getTask(task.getId()).getOwner());
    taskService.forceCompleteTask(task.getId());
    System.out.println("---------------------------> Task completed");
  }
}
