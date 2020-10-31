package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;

@ExtendWith(JaasExtension.class)
class DeleteHistoryEventsOnTaskDeletionAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_DeleteHistoryEvents_When_TaskIsDeleted_With_HistoryDeletionEnabled()
      throws Exception {

    final String taskid = "TKI:000000000000000000000000000000000036";
    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(true);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskid));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTask(taskid);

    // make sure the task got deleted
    ThrowingCallable getDeletedTaskCall =
        () -> {
          taskService.getTask(taskid);
        };

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskid));
    assertThat(listEvents).isEmpty();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_DeleteHistoryEvents_When_TasksAreDeleted_With_HistoryDeletionEnabled()
      throws Exception {

    final String taskId_1 = "TKI:000000000000000000000000000000000037";
    final String taskId_2 = "TKI:000000000000000000000000000000000038";

    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(true);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(3);

    taskService.deleteTasks(List.of(taskId_1, taskId_2));

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
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).isEmpty();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotDeleteHistoryEvents_When_TaskIsDeleted_With_HistoryDeletionDisabled()
      throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000039";

    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(false);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTask(taskId);

    // make sure the task got deleted
    ThrowingCallable getDeletedTaskCall =
        () -> {
          taskService.getTask(taskId);
        };

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));
    assertThat(listEvents).hasSize(2);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotDeleteHistoryEvents_When_TasksAreDeleted_With_HistoryDeletionDisabled()
      throws Exception {
    final String taskId_1 = "TKI:000000000000000000000000000000000040";
    final String taskId_2 = "TKI:000000000000000000000000000000000068";

    taskanaEngineConfiguration.setDeleteHistoryOnTaskDeletionEnabled(false);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTasks(List.of(taskId_1, taskId_2));

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
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(2);
  }
}
