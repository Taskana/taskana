package io.kadai.task.rest.models;

import io.kadai.common.api.exceptions.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.RepresentationModel;

/** EntityModel class for BulkOperationResults. */
public class BulkOperationResultsRepresentationModel
    extends RepresentationModel<BulkOperationResultsRepresentationModel> {

  /** Map of keys to the stored information. */
  protected Map<String, ErrorCode> tasksWithErrors = new HashMap<>();

  public Map<String, ErrorCode> getTasksWithErrors() {
    return tasksWithErrors;
  }

  public void setTasksWithErrors(Map<String, ErrorCode> tasksWithErrors) {
    this.tasksWithErrors = tasksWithErrors;
  }
}
