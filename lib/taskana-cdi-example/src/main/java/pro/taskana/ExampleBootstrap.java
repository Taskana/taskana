package pro.taskana;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

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

@ApplicationScoped
public class ExampleBootstrap {

    @EJB
    private TaskanaEjb taskanaEjb;

    @PostConstruct
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
        throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, InvalidStateException, InvalidOwnerException, InvalidWorkbasketException,
        TaskAlreadyExistException, InvalidArgumentException {
        System.out.println("---------------------------> Start App");
        Task task = taskanaEjb.getTaskService().newTask();
        ObjectReference objRef = new ObjectReference();
        objRef.setCompany("aCompany");
        objRef.setSystem("aSystem");
        objRef.setSystemInstance("anInstance");
        objRef.setType("aType");
        objRef.setValue("aValue");
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
