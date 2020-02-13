package acceptance.taskrouting;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;

/** This is a sample implementation of TaskRouter. */
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
