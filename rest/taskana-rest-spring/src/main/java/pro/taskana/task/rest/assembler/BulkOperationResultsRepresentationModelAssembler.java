package pro.taskana.task.rest.assembler;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.rest.models.BulkOperationResultsRepresentationModel;

@Component
public class BulkOperationResultsRepresentationModelAssembler<K, V extends Exception>
    implements RepresentationModelAssembler<
        BulkOperationResults<String, TaskanaException>,
        BulkOperationResultsRepresentationModel<K, V>> {

  @Autowired
  public BulkOperationResultsRepresentationModelAssembler() {}

  @Override
  public BulkOperationResultsRepresentationModel<K, V> toModel(
      BulkOperationResults<String, TaskanaException> entity) {
    BulkOperationResultsRepresentationModel<K, V> repModel =
        new BulkOperationResultsRepresentationModel<>();
    Map<String, ErrorCode> newErrorMap = new HashMap<>();
    for (Map.Entry<String, TaskanaException> entry : entity.getErrorMap().entrySet()) {
      newErrorMap.put(entry.getKey(), entry.getValue().getErrorCode());
    }
    repModel.setTasksWithErrors(newErrorMap);
    return repModel;
  }
}
