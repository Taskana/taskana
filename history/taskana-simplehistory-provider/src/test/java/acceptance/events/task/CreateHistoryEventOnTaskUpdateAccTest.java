package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskUpdateAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateUpdatedHistoryEvent_When_TaskIsCreated() throws Exception {
    final String taskId = "TKI:000000000000000000000000000000000000";
    Instant before = Instant.now();

    Task task = taskService.getTask(taskId);
    task.setName("someUpdatedName");
    taskService.updateTask(task);
    List<TaskHistoryEvent> events =
        historyService
            .createTaskHistoryQuery()
            .taskIdIn(taskId)
            .createdWithin(new TimeInterval(before, null))
            .list();

    assertThat(events)
        .extracting(TaskHistoryEvent::getEventType)
        .containsExactly(TaskHistoryEventType.UPDATED.getName());
  }
}
