package acceptance.taskrouting;

import pro.taskana.Task;
import pro.taskana.TaskanaEngine;
import pro.taskana.taskrouting.api.TaskRoutingProvider;

/**
 * This is a sample implementation of TaskRouter.
 */
public class TestTaskRoutingProviderForDomainB implements TaskRoutingProvider {

    TaskanaEngine theEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
        theEngine = taskanaEngine;
    }

    @Override
    public String determineWorkbasketId(Task task) {
        if ("DOMAIN_B".equals(task.getDomain())) {
            return "WBI:100000000000000000000000000000000011";
        } else {
            return null;
        }
    }

}
