package io.kadai.task.rest.assembler;

import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.rest.models.BulkOperationResultsRepresentationModel;
import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class BulkOperationResultsRepresentationModelAssembler
    implements RepresentationModelAssembler<
        BulkOperationResults<String, KadaiException>, BulkOperationResultsRepresentationModel> {

  @NonNull
  @Override
  public BulkOperationResultsRepresentationModel toModel(
      BulkOperationResults<String, KadaiException> entity) {
    BulkOperationResultsRepresentationModel repModel =
        new BulkOperationResultsRepresentationModel();
    Map<String, ErrorCode> newErrorMap = new HashMap<>();
    for (Map.Entry<String, KadaiException> entry : entity.getErrorMap().entrySet()) {
      newErrorMap.put(entry.getKey(), entry.getValue().getErrorCode());
    }
    repModel.setTasksWithErrors(newErrorMap);
    return repModel;
  }
}
