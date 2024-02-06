package pro.taskana.task.rest.models;

import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.RepresentationModel;
import pro.taskana.common.api.exceptions.ErrorCode;

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
