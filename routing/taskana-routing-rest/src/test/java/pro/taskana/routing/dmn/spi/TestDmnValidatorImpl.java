package pro.taskana.routing.dmn.spi;

import org.camunda.bpm.model.dmn.DmnModelInstance;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.routing.dmn.spi.api.DmnValidator;

public class TestDmnValidatorImpl implements DmnValidator {

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {}

  @Override
  public void validate(DmnModelInstance dmnModelInstance) {
    // custom validation logic
  }
}
