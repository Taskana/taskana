package io.kadai.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.internal.util.EnumUtil;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.rest.models.BulkOperationResultsRepresentationModel;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@KadaiSpringBootTest
class BulkOperationResultsRepresentationModelAssemblerTest {

  KadaiEngine kadaiEngine;
  TaskService taskService;
  BulkOperationResultsRepresentationModelAssembler assembler;

  @Autowired
  BulkOperationResultsRepresentationModelAssemblerTest(
      KadaiEngine kadaiEngine,
      TaskService taskService,
      BulkOperationResultsRepresentationModelAssembler assembler) {
    this.kadaiEngine = kadaiEngine;
    this.taskService = taskService;
    this.assembler = assembler;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {

    BulkOperationResults<String, KadaiException> result = new BulkOperationResults<>();
    String taskId = "TKI:000000000000000000000000000000000003";
    InvalidTaskStateException kadaiException =
        new InvalidTaskStateException(
            taskId, TaskState.COMPLETED, EnumUtil.allValuesExceptFor(TaskState.END_STATES));

    result.addError(taskId, kadaiException);

    BulkOperationResultsRepresentationModel repModel = assembler.toModel(result);

    assertEquality(result, repModel);
  }

  private void assertEquality(
      BulkOperationResults<String, KadaiException> bulkOperationResults,
      BulkOperationResultsRepresentationModel repModel) {
    Map<String, ErrorCode> newErrorMap = new HashMap<>();
    for (Map.Entry<String, KadaiException> entry : bulkOperationResults.getErrorMap().entrySet()) {
      newErrorMap.put(entry.getKey(), entry.getValue().getErrorCode());
    }
    assertThat(newErrorMap).isEqualTo(repModel.getTasksWithErrors());
  }
}
