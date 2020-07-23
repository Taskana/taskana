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

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskTerminationAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateTerminatedHistoryEvent_When_TerminatingTaskInStateClaimed() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000001";

    List<HistoryEventImpl> listEvents = historyService.createHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).isEmpty();

    taskService.terminateTask(taskId);

    listEvents = historyService.createHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).hasSize(1);

    String eventType = historyService.getHistoryEvent(listEvents.get(0).getId()).getEventType();

    assertThat(eventType).isEqualTo("TASK_TERMINATED");
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CreateTerminatedHistoryEvent_When_TerminatingTaskInStateReady() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000003";

    List<HistoryEventImpl> listEvents = historyService.createHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).isEmpty();

    taskService.terminateTask(taskId);

    listEvents = historyService.createHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).hasSize(1);

    String eventType = historyService.getHistoryEvent(listEvents.get(0).getId()).getEventType();

    assertThat(eventType).isEqualTo("TASK_TERMINATED");
  }
}
