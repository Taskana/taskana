package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskUpdateAccTest extends AbstractAccTest {

  private final TaskService taskService = kadaiEngine.getTaskService();
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
