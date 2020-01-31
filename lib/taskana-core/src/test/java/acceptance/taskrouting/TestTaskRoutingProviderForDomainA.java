package acceptance.taskrouting;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.Task;
import pro.taskana.task.api.TaskRoutingProvider;

/** This is a sample implementation of TaskRouter. */
public class TestTaskRoutingProviderForDomainA implements TaskRoutingProvider {

  TaskanaEngine theEngine;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    theEngine = taskanaEngine;
  }

  @Override
  public String determineWorkbasketId(Task task) {
    if ("DOMAIN_A".equals(task.getDomain())) {
      return "WBI:100000000000000000000000000000000001";
    } else {
      return null;
    }
  }
}
