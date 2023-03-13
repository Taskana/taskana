package acceptance.task.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import acceptance.TaskanaEngineProxy;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.AttachmentMapper;

/** Acceptance test for all "delete task" scenarios. */
@ExtendWith(JaasExtension.class)
class DeleteTaskAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-2")
  @Test
  void testDeleteSingleTaskNotAuthorized() {
    ThrowingCallable call =
        () -> taskService.deleteTask("TKI:000000000000000000000000000000000037");

    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowNotAuthorizedException_When_UserIsMemberOfTaskRouterRole() {
    ThrowingCallable call =
        () -> taskService.deleteTask("TKI:000000000000000000000000000000000037");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteAttachments_When_MultipleTasksAreDeleted() throws Exception {
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
    AttachmentMapper attachmentMapper =
        engineProxy.getEngine().getSqlSession().getMapper(AttachmentMapper.class);

    try {
      engineProxy.openConnection();
      assertThat(
              attachmentMapper.findAttachmentSummariesByTaskIds(
                  List.of(
                      "TKI:000000000000000000000000000000000067",
                      "TKI:000000000000000000000000000000000068")))
          .hasSize(4);
    } finally {
      engineProxy.returnConnection();
    }

    taskService.deleteTasks(
        List.of(
            "TKI:000000000000000000000000000000000067",
            "TKI:000000000000000000000000000000000068"));
    try {
      engineProxy.openConnection();
      assertThat(
              attachmentMapper.findAttachmentSummariesByTaskIds(
                  List.of(
                      "TKI:000000000000000000000000000000000067",
                      "TKI:000000000000000000000000000000000068")))
          .isEmpty();
    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteAttachments_When_SingleTaskIsDeleted() throws Exception {
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
    AttachmentMapper attachmentMapper =
        engineProxy.getSqlSession().getMapper(AttachmentMapper.class);

    try {
      engineProxy.openConnection();
      assertThat(
              attachmentMapper.findAttachmentsByTaskId("TKI:000000000000000000000000000000000069"))
          .hasSize(1);

    } finally {
      engineProxy.returnConnection();
    }

    taskService.deleteTask("TKI:000000000000000000000000000000000069");

    try {
      engineProxy.openConnection();
      assertThat(
              attachmentMapper.findAttachmentsByTaskId("TKI:000000000000000000000000000000000069"))
          .isEmpty();
    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdminRoleButTriesToBulkDeleteTasks() {
    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000010");

    ThrowingCallable call = () -> taskService.deleteTasks(taskIds);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testDeleteSingleTask() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000036");

    taskService.deleteTask(task.getId());

    ThrowingCallable call = () -> taskService.getTask("TKI:000000000000000000000000000000000036");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdminRole() {
    ThrowingCallable deleteTaskCall =
        () -> taskService.deleteTask("TKI:000000000000000000000000000000000041");
    assertThatThrownBy(deleteTaskCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testThrowsExceptionIfTaskIsNotCompleted() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000029");

    ThrowingCallable call = () -> taskService.deleteTask(task.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidTaskStateException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testForceDeleteTaskIfNotCompleted() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

    ThrowingCallable call = () -> taskService.deleteTask(task.getId());
    assertThatThrownBy(call)
        .describedAs("Should not be possible to delete claimed task without force flag")
        .isInstanceOf(InvalidTaskStateException.class);

    taskService.forceDeleteTask(task.getId());

    call = () -> taskService.getTask("TKI:000000000000000000000000000000000027");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testBulkDeleteTask() throws Exception {
    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000037", "TKI:000000000000000000000000000000000038");

    BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    ThrowingCallable call = () -> taskService.getTask("TKI:000000000000000000000000000000000038");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testBulkDeleteTasksWithException() throws Exception {
    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000039",
            "TKI:000000000000000000000000000000000040",
            "TKI:000000000000000000000000000000000028",
            "INVALID_TASK_ID");

    BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();

    assertThat(results.getErrorMap().keySet())
        .containsExactlyInAnyOrder("TKI:000000000000000000000000000000000028", "INVALID_TASK_ID");
    assertThat(results.getErrorMap().get("TKI:000000000000000000000000000000000028"))
        .isInstanceOf(InvalidTaskStateException.class);
    assertThat(results.getErrorMap().get("INVALID_TASK_ID"))
        .isInstanceOf(TaskNotFoundException.class);

    Task notDeletedTask = taskService.getTask("TKI:000000000000000000000000000000000028");
    assertThat(notDeletedTask).isNotNull();
    ThrowingCallable call = () -> taskService.getTask("TKI:000000000000000000000000000000000040");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }
}
