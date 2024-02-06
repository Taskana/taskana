package pro.taskana.task.rest.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.RepresentationModel;
import pro.taskana.common.api.exceptions.ErrorCode;

/** EntityModel class for {@link pro.taskana.common.api.BulkOperationResults}. */
public class BulkOperationResultsRepresentationModel<K, V extends Exception>
    extends RepresentationModel<BulkOperationResultsRepresentationModel<K, V>> {

  /** Map of keys to the stored information. */
  protected Map<String, ErrorCode> tasksWithErrors = new HashMap<>();

  /** List of succesfully transferred task Ids. */
  protected List<String> successfullyTransferredTaskIds = List.of();

  public Map<String, ErrorCode> getTasksWithErrors() {
    return tasksWithErrors;
  }

  public void setTasksWithErrors(Map<String, ErrorCode> tasksWithErrors) {
    this.tasksWithErrors = tasksWithErrors;
  }

  public List<String> getSuccessfullyTransferredTaskIds() {
    return successfullyTransferredTaskIds;
  }

  public void setSuccessfullyTransferredTaskIds(List<String> successfullyTransferredTaskIds) {
    this.successfullyTransferredTaskIds = successfullyTransferredTaskIds;
  }
}
