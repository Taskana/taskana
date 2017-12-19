package pro.taskana;

import javax.ejb.Stateless;
import javax.inject.Inject;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

@Stateless
public class TaskanaEjb {

    @Inject
    private TaskService taskService;

    @Inject
    private ClassificationService classificationService;

    @Inject
    private WorkbasketService workbasketService;

    public TaskService getTaskService() {
        return taskService;
    }

    public ClassificationService getClassificationService() {
        return classificationService;
    }

    public WorkbasketService getWorkbasketService() {
        return workbasketService;
    }

    public void triggerRollback() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException {
        Task task = taskService.newTask();
        taskService.createTask(task);
        System.out.println("---------------->" + task.getId());
        throw new RuntimeException();
    }

}
