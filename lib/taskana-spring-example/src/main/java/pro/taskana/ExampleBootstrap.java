package pro.taskana;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

@Component
@Transactional
public class ExampleBootstrap {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskanaEngine taskanaEngine;

    @PostConstruct
    public void test() throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, InvalidStateException, InvalidOwnerException, TaskAlreadyExistException,
        InvalidArgumentException, DomainNotFoundException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
        ClassificationAlreadyExistException {
        System.out.println("---------------------------> Start App");

        Workbasket wb = taskanaEngine.getWorkbasketService().newWorkbasket("workbasket", "DOMAIN_A");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = taskanaEngine.getClassificationService().newClassification("TEST",
            "DOMAIN_A",
            "TASK");
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
            "---------------------------> Task claimed: " + taskService.getTask(task.getId()).getOwner());
        taskService.forceCompleteTask(task.getId());
        System.out.println("---------------------------> Task completed");
    }

}
