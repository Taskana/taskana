package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.rest.models.BulkOperationResultsRepresentationModel;

@TaskanaSpringBootTest
class BulkOperationResultsRepresentationModelAssemblerTest {

  TaskanaEngine taskanaEngine;
  TaskService taskService;
  BulkOperationResultsRepresentationModelAssembler assembler;

  @Autowired
  BulkOperationResultsRepresentationModelAssemblerTest(
      TaskanaEngine taskanaEngine,
      TaskService taskService,
      BulkOperationResultsRepresentationModelAssembler assembler) {
    this.taskanaEngine = taskanaEngine;
    this.taskService = taskService;
    this.assembler = assembler;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {

    BulkOperationResults<String, TaskanaException> result = new BulkOperationResults<>();
    String taskId = "TKI:000000000000000000000000000000000003";
    InvalidTaskStateException taskanaException =
        new InvalidTaskStateException(
            taskId, TaskState.COMPLETED, EnumUtil.allValuesExceptFor(TaskState.END_STATES));

    result.addError(taskId, taskanaException);

    BulkOperationResultsRepresentationModel repModel = assembler.toModel(result);

    assertEquality(result, repModel);
  }

  private void assertEquality(
      BulkOperationResults<String, TaskanaException> bulkOperationResults,
      BulkOperationResultsRepresentationModel repModel) {
    Map<String, ErrorCode> newErrorMap = new HashMap<>();
    for (Map.Entry<String, TaskanaException> entry :
        bulkOperationResults.getErrorMap().entrySet()) {
      newErrorMap.put(entry.getKey(), entry.getValue().getErrorCode());
    }
    assertThat(newErrorMap).isEqualTo(repModel.getTasksWithErrors());
  }
}
