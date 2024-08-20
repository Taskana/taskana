package io.kadai.task.rest.routing;

import io.kadai.common.api.KadaiEngine;
import io.kadai.spi.routing.api.TaskRoutingProvider;
import io.kadai.task.api.models.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTestTaskRouter implements TaskRoutingProvider {

  public static final String DEFAULT_ROUTING_TARGET = "WBI:100000000000000000000000000000000002";
  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestTaskRouter.class);

  private KadaiEngine kadaiEngine;

  @Override
  public void initialize(KadaiEngine kadaiEngine) {
    this.kadaiEngine = kadaiEngine;
  }

  @Override
  public String determineWorkbasketId(Task task) {
    return DEFAULT_ROUTING_TARGET;
  }
}
