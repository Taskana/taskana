package pro.taskana.task.rest.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;

public class IntegrationTestTaskRouter implements TaskRoutingProvider {

  public static final String DEFAULT_ROUTING_TARGET = "WBI:100000000000000000000000000000000002";
  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestTaskRouter.class);

  private TaskanaEngine taskanaEngine;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
  }

  @Override
  public String determineWorkbasketId(Task task) {
    return DEFAULT_ROUTING_TARGET;
  }
}
