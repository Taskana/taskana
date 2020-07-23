package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.HashMap;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;

/** Acceptance test for all "get task" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskAccTest extends AbstractAccTest {

  GetTaskAccTest() {
    super();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTask_When_RequestingTaskByTaskId() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");

    assertThat(task.getCompleted()).isNull();
    assertThat(task.getName()).isEqualTo("Task99");
    assertThat(task.getCreator()).isEqualTo("creator_user_id");
    assertThat(task.getDescription()).isEqualTo("Lorem ipsum was n Quatsch dolor sit amet.");
    assertThat(task.getNote()).isEqualTo("Some custom Note");
    assertThat(task.getPriority()).isEqualTo(1);
    assertThat(task.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(task.getClassificationCategory()).isEqualTo("MANUAL");
    assertThat(task.getClassificationSummary().getKey()).isEqualTo("T2000");
    assertThat(task.getClassificationSummary().getId())
        .isEqualTo("CLI:100000000000000000000000000000000016");
    assertThat(task.getWorkbasketSummary().getId())
        .isEqualTo("WBI:100000000000000000000000000000000006");
    assertThat(task.getWorkbasketKey()).isEqualTo("USER-1-1");
    assertThat(task.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(task.getBusinessProcessId()).isEqualTo("BPI21");
    assertThat(task.getParentBusinessProcessId()).isEqualTo("PBPI21");
    assertThat(task.getOwner()).isEqualTo("user-1-1");
    assertThat(task.getPrimaryObjRef().getCompany()).isEqualTo("MyCompany1");
    assertThat(task.getPrimaryObjRef().getSystem()).isEqualTo("MySystem1");
    assertThat(task.getPrimaryObjRef().getSystemInstance()).isEqualTo("MyInstance1");
    assertThat(task.getPrimaryObjRef().getType()).isEqualTo("MyType1");
    assertThat(task.getPrimaryObjRef().getValue()).isEqualTo("MyValue1");
    assertThat(task.isRead()).isTrue();
    assertThat(task.isTransferred()).isFalse();
    assertThat(task.getCallbackInfo()).isEqualTo(new HashMap<String, String>());
    assertThat(task.getCustomAttributeMap()).isEqualTo(new HashMap<String, String>());
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_1)).isEqualTo("custom1");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_2)).isEqualTo("custom2");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_3)).isEqualTo("custom3");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_4)).isEqualTo("custom4");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_5)).isEqualTo("custom5");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_6)).isEqualTo("custom6");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_7)).isEqualTo("custom7");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_8)).isEqualTo("custom8");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_9)).isEqualTo("custom9");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_10)).isEqualTo("custom10");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_11)).isEqualTo("custom11");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_12)).isEqualTo("custom12");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_13)).isEqualTo("custom13");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_14)).isEqualTo("abc");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_15)).isEqualTo("custom15");
    assertThat(task.getCustomAttribute(TaskCustomField.CUSTOM_16)).isEqualTo("custom16");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_RequestedTaskByIdIsNotExisting() {
    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable call =
        () -> {
          taskService.getTask("INVALID");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_UserIsNotAuthorizedToGetTask() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable getTaskCall =
        () -> {
          taskService.getTask("TKI:000000000000000000000000000000000000");
        };
    assertThatThrownBy(getTaskCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    assertThat(task).isNotNull();
  }
}
