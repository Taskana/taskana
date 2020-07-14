package acceptance.events;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskTerminationAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateTerminatedHistoryEvent_When_TaskIsTerminated() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000001";

    List<HistoryEventImpl> listEvents = historyService.createHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).isEmpty();

    assertThat(taskService.getTask(taskId).getState()).isEqualTo(TaskState.CLAIMED);

    Task task = taskService.terminateTask(taskId);
    assertThat(task.getState()).isEqualTo(TaskState.TERMINATED);

    listEvents = historyService.createHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).hasSize(1);
    assertThat(historyService.getHistoryEvent(listEvents.get(0).getId()).getEventType())
        .isEqualTo("TASK_TERMINATED");
  }
}
