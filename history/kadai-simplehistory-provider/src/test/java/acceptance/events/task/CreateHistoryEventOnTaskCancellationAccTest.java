package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import io.kadai.task.api.TaskService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCancellationAccTest extends AbstractAccTest {

  private final TaskService taskService = kadaiEngine.getTaskService();
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
