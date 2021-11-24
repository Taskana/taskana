package pro.taskana.routing.dmn.spi.api;

import org.camunda.bpm.model.dmn.DmnModelInstance;

import pro.taskana.common.api.TaskanaEngine;

public interface DmnValidator {

  /**
   * Initialize DmnValidator.
   *
   * @param taskanaEngine {@link TaskanaEngine} The Taskana engine needed for initialization.
   */
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Validates a DmnModelInstance.
   *
   * @param dmnModelInstance the DMN model to validate
   */
  void validate(DmnModelInstance dmnModelInstance);
}
