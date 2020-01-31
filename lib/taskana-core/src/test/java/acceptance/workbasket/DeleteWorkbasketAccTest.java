package acceptance.workbasket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.InvalidOwnerException;
import pro.taskana.common.api.exceptions.InvalidStateException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.TaskImpl;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketAccessItem;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

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

  @WithAccessId(
      userName = "admin",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteWorkbasket() throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket wb = workbasketService.getWorkbasket("USER_2_2", "DOMAIN_A");

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> {
          workbasketService.deleteWorkbasket(wb.getId());
          workbasketService.getWorkbasket("USER_2_2", "DOMAIN_A");
        },
        "There should be no result for a deleted Workbasket.");
  }

  @WithAccessId(userName = "elena")
  @Test
  void testDeleteWorkbasketNotAuthorized() {

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> {
          Workbasket wb = workbasketService.getWorkbasket("TEAMLEAD_2", "DOMAIN_A");
          workbasketService.deleteWorkbasket(wb.getId());
        });
  }

  @WithAccessId(userName = "elena")
  @Test
  void testGetWorkbasketNotAuthorized() {

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> workbasketService.getWorkbasket("TEAMLEAD_2", "DOMAIN_A"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"teamlead_1", "group_1", "businessadmin"})
  @Test
  void testDeleteWorkbasketAlsoAsDistributionTarget()
      throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket wb = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
    int distTargets =
        workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001").size();

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> {
          // WB deleted
          workbasketService.deleteWorkbasket(wb.getId());
          workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
        },
        "There should be no result for a deleted Workbasket.");

    int newDistTargets =
        workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001").size();
    assertThat(newDistTargets, equalTo(3));
    assertTrue(newDistTargets < distTargets);
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteWorkbasketWithNullOrEmptyParam() {
    // Test Null-Value
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> workbasketService.deleteWorkbasket(null),
        "delete() should have thrown an InvalidArgumentException, " + "when the param ID is null.");

    // Test EMPTY-Value

    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> workbasketService.deleteWorkbasket(""),
        "delete() should have thrown an InvalidArgumentException, "
            + "when the param ID is EMPTY-String.");
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteWorkbasketButNotExisting() {
    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> workbasketService.deleteWorkbasket("SOME NOT EXISTING ID"));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testDeleteWorkbasketWhichIsUsed()
      throws WorkbasketNotFoundException, NotAuthorizedException {
    Workbasket wb =
        workbasketService.getWorkbasket("USER_1_2", "DOMAIN_A"); // all rights, DOMAIN_A with Tasks
    Assertions.assertThrows(
        WorkbasketInUseException.class, () -> workbasketService.deleteWorkbasket(wb.getId()));
  }

  @WithAccessId(
      userName = "teamlead_2",
      groupNames = {"businessadmin"})
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
    assertEquals(5, accessItemsBefore.size());

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> {
          workbasketService.deleteWorkbasket(wbId);
          workbasketService.getWorkbasket("WBI:100000000000000000000000000000000008");
        },
        "There should be no result for a deleted Workbasket.");

    List<WorkbasketAccessItem> accessItemsAfter = workbasketService.getWorkbasketAccessItems(wbId);
    assertEquals(0, accessItemsAfter.size());
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"businessadmin"})
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
    assertFalse(markedForDeletion);

    Workbasket wb2 = workbasketService.getWorkbasket(wb.getId());
    assertTrue(wb2.isMarkedForDeletion());
  }
}
