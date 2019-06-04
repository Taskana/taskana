package pro.taskana;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * example Taskana EJB.
 */
@Stateless
public class TaskanaEjb {

    @Inject
    private TaskService taskService;

    public TaskService getTaskService() {
        return taskService;
    }
}
