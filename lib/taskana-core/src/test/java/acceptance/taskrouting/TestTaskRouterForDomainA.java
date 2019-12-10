package acceptance.taskrouting;

import pro.taskana.Task;
import pro.taskana.TaskanaEngine;
import pro.taskana.taskrouting.api.TaskRouter;

/**
 * This is a sample implementation of TaskRouter.
 */
public class TestTaskRouterForDomainA implements TaskRouter {

    TaskanaEngine theEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
        theEngine = taskanaEngine;
    }

    @Override
    public String routeToWorkbasketId(Task task) {
        if ("DOMAIN_A".equals(task.getDomain())) {
            return "WBI:100000000000000000000000000000000001";
        } else {
            return null;
        }
    }

}
