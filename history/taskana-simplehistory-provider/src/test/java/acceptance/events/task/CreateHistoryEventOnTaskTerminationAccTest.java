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
class CreateHistoryEventOnTaskTerminationAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateTerminatedHistoryEvent_When_TerminatingTaskInStateClaimed() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000001";

    List<TaskHistoryEvent> events = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(events).isEmpty();

    taskService.terminateTask(taskId);

    events = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.TERMINATED.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CreateTerminatedHistoryEvent_When_TerminatingTaskInStateReady() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000003";

    List<TaskHistoryEvent> events = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(events).isEmpty();

    taskService.terminateTask(taskId);

    events = historyService.createTaskHistoryQuery().taskIdIn(taskId).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.TERMINATED.getName());
  }
}
