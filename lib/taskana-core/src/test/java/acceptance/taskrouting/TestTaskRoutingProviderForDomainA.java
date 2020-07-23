package acceptance.taskrouting;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.Task;

/** This is a sample implementation of TaskRouter. */
public class TestTaskRoutingProviderForDomainA implements TaskRoutingProvider {

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    // no-op
  }

  @Override
  public String determineWorkbasketId(Task task) {
    String att7 = task.getCustomAttribute(TaskCustomField.CUSTOM_7);

    if (att7 != null && att7.equals("multipleWorkbaskets")) {
      return "WBI:100000000000000000000000000000000005";
    } else if ("DOMAIN_A".equals(task.getDomain())) {
      if (att7 != null && att7.equals("noRouting")) {
        return null;
      } else {
        return "WBI:100000000000000000000000000000000001";
      }
    } else {
      return null;
    }
  }
}
