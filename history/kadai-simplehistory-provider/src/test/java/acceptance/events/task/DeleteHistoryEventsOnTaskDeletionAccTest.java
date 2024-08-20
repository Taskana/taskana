package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.KadaiConfiguration;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.TaskHistoryQueryImpl;
import io.kadai.simplehistory.impl.task.TaskHistoryQueryMapper;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import java.sql.SQLException;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class DeleteHistoryEventsOnTaskDeletionAccTest extends AbstractAccTest {

  @Test
  @WithAccessId(user = "admin")
  void should_DeleteHistoryEvents_When_TaskIsDeletedWithHistoryDeletionEnabled() throws Exception {

    final String taskid = "TKI:000000000000000000000000000000000036";
    createKadaiEngineWithNewConfig(true);

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
    assertThat(listEvents).hasSize(1);
    assertThat(listEvents.get(0).getEventType()).isEqualTo(TaskHistoryEventType.DELETED.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_DeleteHistoryEvents_When_TasksAreDeletedWithHistoryDeletionEnabled()
      throws Exception {

    final String taskId_1 = "TKI:000000000000000000000000000000000037";
    final String taskId_2 = "TKI:000000000000000000000000000000000038";

    createKadaiEngineWithNewConfig(true);

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
    assertThat(listEvents).hasSize(2);
    assertThat(listEvents.get(0).getEventType()).isEqualTo(TaskHistoryEventType.DELETED.getName());
    assertThat(listEvents.get(1).getEventType()).isEqualTo(TaskHistoryEventType.DELETED.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotDeleteHistoryEvents_When_TaskIsDeletedWithHistoryDeletionDisabled()
      throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000039";

    createKadaiEngineWithNewConfig(false);

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
    assertThat(listEvents).hasSize(3);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotDeleteHistoryEvents_When_TasksAreDeletedWithHistoryDeletionDisabled()
      throws Exception {
    final String taskId_1 = "TKI:000000000000000000000000000000000040";
    final String taskId_2 = "TKI:000000000000000000000000000000000068";

    createKadaiEngineWithNewConfig(false);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(2);

    taskService.deleteTasks(List.of(taskId_1, taskId_2));

    // make sure the tasks got deleted
    ThrowingCallable getDeletedTaskCall = () -> taskService.getTask(taskId_1);
    ThrowingCallable getDeletedTaskCall2 = () -> taskService.getTask(taskId_2);

    assertThatThrownBy(getDeletedTaskCall).isInstanceOf(TaskNotFoundException.class);
    assertThatThrownBy(getDeletedTaskCall2).isInstanceOf(TaskNotFoundException.class);

    listEvents =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskId_1, taskId_2));
    assertThat(listEvents).hasSize(4);
  }

  private void createKadaiEngineWithNewConfig(boolean deleteHistoryOnTaskDeletionEnabled)
      throws SQLException {
    KadaiConfiguration configuration =
        new KadaiConfiguration.Builder(AbstractAccTest.kadaiConfiguration)
            .deleteHistoryEventsOnTaskDeletionEnabled(deleteHistoryOnTaskDeletionEnabled)
            .build();
    initKadaiEngine(configuration);
  }
}
