package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** Acceptance test which does test the deletion of a workbasket and all wanted failures. */
@ExtendWith(JaasExtension.class)
class DeleteWorkbasketAccTest extends AbstractAccTest {

  private WorkbasketService workbasketService;

  private TaskService taskService;

  @BeforeEach
  void setUpMethod() {
    workbasketService = taskanaEngine.getWorkbasketService();
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(user = "admin", groups = "businessadmin")
  @Test
  void testDeleteWorkbasket() throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket wb = workbasketService.getWorkbasket("USER_2_2", "DOMAIN_A");

    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket(wb.getId());
          workbasketService.getWorkbasket("USER_2_2", "DOMAIN_A");
        };
    assertThatThrownBy(call)
        .describedAs("There should be no result for a deleted Workbasket.")
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "elena")
  @Test
  void testDeleteWorkbasketNotAuthorized() {

    ThrowingCallable call =
        () -> {
          Workbasket wb = workbasketService.getWorkbasket("TEAMLEAD_2", "DOMAIN_A");
          workbasketService.deleteWorkbasket(wb.getId());
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "elena")
  @Test
  void testGetWorkbasketNotAuthorized() {

    ThrowingCallable call =
        () -> {
          workbasketService.getWorkbasket("TEAMLEAD_2", "DOMAIN_A");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "user_1_1",
      groups = {"teamlead_1", "group_1", "businessadmin"})
  @Test
  void testDeleteWorkbasketAlsoAsDistributionTarget()
      throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket wb = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
    int distTargets =
        workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001").size();

    ThrowingCallable call =
        () -> {
          // WB deleted
          workbasketService.deleteWorkbasket(wb.getId());
          workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
        };
    assertThatThrownBy(call)
        .describedAs("There should be no result for a deleted Workbasket.")
        .isInstanceOf(WorkbasketNotFoundException.class);

    int newDistTargets =
        workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001").size();
    assertThat(newDistTargets).isEqualTo(3).isLessThan(distTargets);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testDeleteWorkbasketWithNullOrEmptyParam() {
    // Test Null-Value
    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket(null);
        };
    assertThatThrownBy(call)
        .describedAs(
            "delete() should have thrown an InvalidArgumentException, "
                + "when the param ID is null.")
        .isInstanceOf(InvalidArgumentException.class);

    // Test EMPTY-Value
    call =
        () -> {
          workbasketService.deleteWorkbasket("");
        };
    assertThatThrownBy(call)
        .describedAs(
            "delete() should have thrown an InvalidArgumentException, \"\n"
                + "            + \"when the param ID is EMPTY-String.")
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testDeleteWorkbasketButNotExisting() {
    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket("SOME NOT EXISTING ID");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user_1_2", groups = "businessadmin")
  @Test
  void testDeleteWorkbasketWhichIsUsed()
      throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket wb =
        workbasketService.getWorkbasket("USER_1_2", "DOMAIN_A"); // all rights, DOMAIN_A with Tasks
    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket(wb.getId());
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketInUseException.class);
  }

  @WithAccessId(user = "teamlead_2", groups = "businessadmin")
  @Test
  void testCascadingDeleteOfAccessItems()
      throws WorkbasketNotFoundException, NotAuthorizedException, InvalidArgumentException,
          WorkbasketAccessItemAlreadyExistException {
    Workbasket wb = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000008");
    String wbId = wb.getId();
    // create 2 access Items
    WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem(wbId, "teamlead_2");
    accessItem.setPermAppend(true);
    accessItem.setPermRead(true);
    accessItem.setPermOpen(true);
    workbasketService.createWorkbasketAccessItem(accessItem);
    accessItem = workbasketService.newWorkbasketAccessItem(wbId, "elena");
    accessItem.setPermAppend(true);
    accessItem.setPermRead(true);
    accessItem.setPermOpen(true);
    workbasketService.createWorkbasketAccessItem(accessItem);
    List<WorkbasketAccessItem> accessItemsBefore = workbasketService.getWorkbasketAccessItems(wbId);
    assertThat(accessItemsBefore).hasSize(5);

    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket(wbId);
          workbasketService.getWorkbasket("WBI:100000000000000000000000000000000008");
        };
    assertThatThrownBy(call)
        .describedAs("There should be no result for a deleted Workbasket.")
        .isInstanceOf(WorkbasketNotFoundException.class);

    List<WorkbasketAccessItem> accessItemsAfter = workbasketService.getWorkbasketAccessItems(wbId);
    assertThat(accessItemsAfter).isEmpty();
  }

  @WithAccessId(user = "admin", groups = "businessadmin")
  @Test
  void testMarkWorkbasketForDeletion()
      throws WorkbasketInUseException, NotAuthorizedException, WorkbasketNotFoundException,
          InvalidArgumentException, InvalidOwnerException, InvalidStateException,
          TaskNotFoundException {
    final Workbasket wb =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000006");

    TaskImpl task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000000");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000001");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000002");
    taskService.forceCompleteTask(task.getId());

    boolean markedForDeletion = workbasketService.deleteWorkbasket(wb.getId());
    assertThat(markedForDeletion).isFalse();

    Workbasket wb2 = workbasketService.getWorkbasket(wb.getId());
    assertThat(wb2.isMarkedForDeletion()).isTrue();
  }
}
