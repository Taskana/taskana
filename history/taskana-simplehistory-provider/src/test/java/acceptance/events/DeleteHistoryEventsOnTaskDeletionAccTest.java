package acceptance.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.impl.HistoryQueryImpl;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;

@ExtendWith(JaasExtension.class)
class DeleteHistoryEventsOnTaskDeletionAccTest extends AbstractAccTest {

  private TaskService taskService;
  private SimpleHistoryServiceImpl historyService;

  @BeforeEach
  public void setUp() {

    taskService = taskanaEngine.getTaskService();
    historyService = getHistoryService();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_deleteHistoryEvents_When_TaskIsDeleted_With_HistoryDeletionEnabled()
      throws Exception {

    final String taskid = "TKI:000000000000000000000000000000000036";
    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(true);

    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskid));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTask(taskid);

    // make sure the task got deleted
    ThrowingCallable getDeletedTaskCall =
        () -> {
          taskService.getTask(taskid);
        };

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskid));
    assertThat(listEvents).hasSize(0);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_deleteHistoryEvents_When_TasksAreDeleted_With_HistoryDeletionEnabled()
      throws Exception {

    final String taskId_1 = "TKI:000000000000000000000000000000000037";
    final String taskId_2 = "TKI:000000000000000000000000000000000038";

    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(true);

    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(3);

    taskService.deleteTasks(Arrays.asList(taskId_1, taskId_2));

    // make sure the tasks got deleted
    ThrowingCallable getDeletedTaskCall =
        () -> {
          taskService.getTask(taskId_1);
        };
    ThrowingCallable getDeletedTaskCall2 =
        () -> {
          taskService.getTask(taskId_2);
        };

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);
    assertThatThrownBy(getDeletedTaskCall2).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(0);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_notDeleteHistoryEvents_When_TaskIsDeleted_With_HistoryDeletionDisabled()
      throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000039";

    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(false);

    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTask(taskId);

    // make sure the task got deleted
    ThrowingCallable getDeletedTaskCall =
        () -> {
          taskService.getTask(taskId);
        };

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId));
    assertThat(listEvents).hasSize(2);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_notDeleteHistoryEvents_When_TasksAreDeleted_With_HistoryDeletionDisabled()
      throws Exception {
    final String taskId_1 = "TKI:000000000000000000000000000000000040";
    final String taskId_2 = "TKI:000000000000000000000000000000000068";

    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(false);

    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTasks(Arrays.asList(taskId_1, taskId_2));

    // make sure the tasks got deleted
    ThrowingCallable getDeletedTaskCall =
        () -> {
          taskService.getTask(taskId_1);
        };
    ThrowingCallable getDeletedTaskCall2 =
        () -> {
          taskService.getTask(taskId_2);
        };

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);
    assertThatThrownBy(getDeletedTaskCall2).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(2);
  }
}
