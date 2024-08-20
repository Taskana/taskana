package io.kadai.routing.dmn.spi;

import io.kadai.common.api.KadaiEngine;
import io.kadai.routing.dmn.spi.api.DmnValidator;
import org.camunda.bpm.model.dmn.DmnModelInstance;

public class TestDmnValidatorImpl implements DmnValidator {

  @Override
  public void initialize(KadaiEngine kadaiEngine) {}

  @Override
  public void validate(DmnModelInstance dmnModelInstance) {
    // custom validation logic
  }
}
