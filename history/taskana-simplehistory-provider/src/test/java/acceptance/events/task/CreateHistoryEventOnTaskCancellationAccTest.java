package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCancellationAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateCancelledHistoryEvent_When_CancelTaskInStateClaimed() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000001";

    List<TaskHistoryEvent> listEvents =
        historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).isEmpty();

    taskService.cancelTask(taskId);

    listEvents = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(listEvents).hasSize(1);

    String eventType = listEvents.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.CANCELLED.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CreateCancelledHistoryEvent_When_CancelTaskInStateReady() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000003";

    List<TaskHistoryEvent> events = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(events).isEmpty();

    taskService.cancelTask(taskId);

    events = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.CANCELLED.getName());
  }
}
