package io.kadai.routing.dmn.spi.api;

import io.kadai.common.api.KadaiEngine;
import org.camunda.bpm.model.dmn.DmnModelInstance;

public interface DmnValidator {

  /**
   * Initialize DmnValidator.
   *
   * @param kadaiEngine {@link KadaiEngine} The Kadai engine needed for initialization.
   */
  void initialize(KadaiEngine kadaiEngine);

  /**
   * Validates a DmnModelInstance.
   *
   * @param dmnModelInstance the DMN model to validate
   */
  void validate(DmnModelInstance dmnModelInstance);
}
