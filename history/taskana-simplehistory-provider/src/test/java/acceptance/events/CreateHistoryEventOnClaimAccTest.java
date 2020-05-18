package acceptance.events;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.impl.HistoryQueryImpl;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnClaimAccTest extends AbstractAccTest {

  private TaskService taskService;
  private SimpleHistoryServiceImpl historyService;

  @BeforeEach
  public void setUp() {

    taskService = taskanaEngine.getTaskService();
    historyService = getHistoryService();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateClaimedHistoryEvent_When_TaskIsClaimed() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000047";

    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId));

    assertThat(listEvents).hasSize(0);

    assertThat(taskService.getTask(taskId).getState()).isEqualTo(TaskState.READY);
    Task task = taskService.claim(taskId);
    assertThat(task.getState()).isEqualTo(TaskState.CLAIMED);

    listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId));

    assertThat(listEvents).hasSize(1);
    assertThat(historyService.getHistoryEvent(listEvents.get(0).getId()).getEventType())
        .isEqualTo("TASK_CLAIMED");
  }
}
