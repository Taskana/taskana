package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskTransferAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateTransferredHistoryEvent_When_TaskIstransferred() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000003";

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

    assertThat(events).isEmpty();

    taskService.transfer(taskId, "WBI:100000000000000000000000000000000006");

    events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.TRANSFERRED.getName());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateTransferredHistoryEvents_When_TaskBulkTransfer() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000004";
    final String taskId2 = "TKI:000000000000000000000000000000000002";

    final String destinationWorkbasketKey = "WBI:100000000000000000000000000000000007";

    List<String> taskIds = new ArrayList<>();

    taskIds.add(taskId);
    taskIds.add(taskId2);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskIds.toArray(new String[0])));

    assertThat(events).isEmpty();

    taskService.transferTasks(destinationWorkbasketKey, taskIds);
    events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService
                    .createTaskHistoryQuery()
                    .taskIdIn(taskIds.stream().toArray(String[]::new)));

    assertThat(events).hasSize(2);

    assertThat(events)
        .extracting(TaskHistoryEvent::getEventType)
        .containsOnly(TaskHistoryEventType.TRANSFERRED.getName());

    assertThat(events)
        .extracting(TaskHistoryEvent::getOldValue)
        .containsExactlyInAnyOrder(
            "WBI:100000000000000000000000000000000006", "WBI:100000000000000000000000000000000001");

    assertThat(events)
        .extracting(TaskHistoryEvent::getNewValue)
        .containsOnly("WBI:100000000000000000000000000000000007");
  }
}
