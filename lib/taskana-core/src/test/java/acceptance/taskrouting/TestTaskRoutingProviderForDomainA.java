package acceptance.taskrouting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;

/** This is a sample implementation of TaskRouter. */
public class TestTaskRoutingProviderForDomainA implements TaskRoutingProvider {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(TestTaskRoutingProviderForDomainA.class);
  TaskanaEngine theEngine;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    theEngine = taskanaEngine;
  }

  @Override
  public String determineWorkbasketId(Task task) {
    String att7 = "";
    try {
      att7 = task.getCustomAttribute("7");
    } catch (InvalidArgumentException ex) {
      LOGGER.warn("caught exception ", ex);
    }

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
