package pro.taskana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

@Component
@Transactional
public class TaskanaComponent {

    @Autowired
    TaskService taskService;

    public TaskService getTaskService() {
        return taskService;
    }

    public void triggerRollback() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException, InvalidArgumentException {
        Task task = taskService.newTask("1");
        task.setName("Unit Test Task");
        ObjectReference objRef = new ObjectReference();
        objRef.setCompany("aCompany");
        objRef.setSystem("aSystem");
        objRef.setSystemInstance("anInstance");
        objRef.setType("aType");
        objRef.setValue("aValue");
        task.setPrimaryObjRef(objRef);

        task = taskService.createTask(task);
        throw new RuntimeException();
    }

}
