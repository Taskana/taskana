package pro.taskana.task.rest.assembler;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.rest.models.BulkOperationResultsRepresentationModel;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

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
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel()
      throws Exception {
    // given
    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000003",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000039",
            "TKI:000000000000000000000000000000000040");
    BulkOperationResults<String, TaskanaException> result =
        taskanaEngine.runAsAdmin(
            () -> {
              try {
                return taskService.transferTasksWithOwner(
                    "WBI:100000000000000000000000000000000006", taskIds, "user-1-1", true);
              } catch (WorkbasketNotFoundException e) {
                throw new RuntimeException(e);
              } catch (NotAuthorizedOnWorkbasketException e) {
                throw new RuntimeException(e);
              }
            });
    Set<String> failedIds = new HashSet<>(result.getFailedIds());
    List<String> successfullyTransferredTaskIds =
        taskIds.stream().filter(not(failedIds::contains)).toList();

    BulkOperationResultsRepresentationModel repModel = assembler.toModel(result);
    repModel.setSuccessfullyTransferredTaskIds(successfullyTransferredTaskIds);

    testEquality(result, repModel, successfullyTransferredTaskIds);
  }

  private void testEquality(
      BulkOperationResults<String, TaskanaException> bulkOperationResults,
      BulkOperationResultsRepresentationModel repModel,
      List<String> successfullyTransferredTaskIds)
      throws Exception {
    Map<String, ErrorCode> newErrorMap = new HashMap<>();
    for (Map.Entry<String, TaskanaException> entry :
        bulkOperationResults.getErrorMap().entrySet()) {
      newErrorMap.put(entry.getKey(), entry.getValue().getErrorCode());
    }
    assertThat(successfullyTransferredTaskIds)
        .isEqualTo(repModel.getSuccessfullyTransferredTaskIds());
    assertThat(newErrorMap).isEqualTo(repModel.getTasksWithErrors());
  }
}
