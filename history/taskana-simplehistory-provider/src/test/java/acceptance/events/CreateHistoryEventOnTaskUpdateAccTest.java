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
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskUpdateAccTest extends AbstractAccTest {

  private TaskService taskService;
  private SimpleHistoryServiceImpl historyService;

  @BeforeEach
  public void setUp() {

    taskService = taskanaEngine.getTaskService();
    historyService = getHistoryService();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CreateUpdatedHistoryEvent_When_TaskIsCreated() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000000";
    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId));

    assertThat(listEvents).hasSize(2);

    Task task = taskService.getTask(taskId);
    task.setName("someUpdatedName");
    taskService.updateTask(task);

    listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId));

    assertThat(listEvents).hasSize(3);
    assertThat(listEvents.get(2).getEventType()).isEqualTo("TASK_UPDATED");

    assertThat(historyService.getHistoryEvent(listEvents.get(2).getId()).getDetails())
        .contains("someUpdatedName");
  }
}
