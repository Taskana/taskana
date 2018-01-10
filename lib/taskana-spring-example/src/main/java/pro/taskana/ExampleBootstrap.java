package pro.taskana;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.ObjectReference;

@Component
@Transactional
public class ExampleBootstrap {

    @Autowired
    private TaskService taskService;

    @PostConstruct
    public void test() throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, InvalidStateException, InvalidOwnerException, InvalidWorkbasketException,
        TaskAlreadyExistException, InvalidArgumentException {
        System.out.println("---------------------------> Start App");
        Task task = taskService.newTask();
        task.setName("Spring example task");
        task.setWorkbasketKey("1");
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
        taskService.completeTask(task.getId(), true);
        System.out.println("---------------------------> Task completed");
    }

}
