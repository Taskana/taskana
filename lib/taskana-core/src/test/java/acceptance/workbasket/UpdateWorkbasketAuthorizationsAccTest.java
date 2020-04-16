package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

/**
 * Acceptance test for all "update workbasket" scenarios.
 */
@ExtendWith(JaasExtension.class)
class UpdateWorkbasketAuthorizationsAccTest extends AbstractAccTest {

  UpdateWorkbasketAuthorizationsAccTest() {
    super();
  }

  @WithAccessId(user = "taskadmin")
  @Test
  public void should_ThrowException_When_TaskAdminTriesToGetWorkbasketAccItem() {

    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable retrieveWorkbasketAccessItemCall =
        () -> {
          workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000008");
        };

    assertThatThrownBy(retrieveWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  public void should_ThrowException_When_TaskAdminTriesToDeleteWorkbasketAccItemById() {

    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable deleteWorkbasketAccessItemCall =
        () -> {
          workbasketService.deleteWorkbasketAccessItemsForAccessId("group_1");
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  public void should_ThrowException_When_TaskAdminTriesToDeleteWorkbasketAccessItemByAccessId() {

    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    WorkbasketAccessItem workbasketAccessItem =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000008", "newAccessIdForUpdate");

    workbasketAccessItem.setPermCustom1(true);

    ThrowingCallable deleteWorkbasketAccessItemCall =
        () -> {
          workbasketService.deleteWorkbasketAccessItem(workbasketAccessItem.getId());
        };

    assertThatThrownBy(deleteWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  public void should_ThrowException_When_TaskAdminTriesToUpdateWorkbasketAccItem() {

    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    WorkbasketAccessItem workbasketAccessItem =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000008", "newAccessIdForUpdate");

    workbasketAccessItem.setPermCustom1(true);

    ThrowingCallable updateWorkbasketAccessItemCall =
        () -> {
          workbasketService.updateWorkbasketAccessItem(workbasketAccessItem);
        };

    assertThatThrownBy(updateWorkbasketAccessItemCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testUpdateWorkbasketAccessItemSucceeds()
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException,
                 WorkbasketAccessItemAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000002", "user1");
    accessItem.setPermAppend(true);
    accessItem.setPermCustom11(true);
    accessItem.setPermRead(true);
    accessItem = workbasketService.createWorkbasketAccessItem(accessItem);

    final WorkbasketAccessItem newItem = accessItem;
    accessItem.setPermCustom1(true);
    accessItem.setPermAppend(false);
    accessItem.setAccessName("Rojas, Miguel");
    workbasketService.updateWorkbasketAccessItem(accessItem);
    List<WorkbasketAccessItem> items =
        workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000002");

    WorkbasketAccessItem updatedItem =
        items.stream().filter(t -> newItem.getId().equals(t.getId())).findFirst().orElse(null);

    assertThat(updatedItem).isNotNull();
    assertThat(updatedItem.getAccessName()).isEqualTo("Rojas, Miguel");
    assertThat(updatedItem.isPermAppend()).isFalse();
    assertThat(updatedItem.isPermRead()).isTrue();
    assertThat(updatedItem.isPermCustom11()).isTrue();
    assertThat(updatedItem.isPermCustom1()).isTrue();
    assertThat(updatedItem.isPermCustom2()).isFalse();
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testUpdateWorkbasketAccessItemRejected()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
                 WorkbasketAccessItemAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000001", "user1");
    accessItem.setPermAppend(true);
    accessItem.setPermCustom11(true);
    accessItem.setPermRead(true);
    WorkbasketAccessItem accessItemCreated =
        workbasketService.createWorkbasketAccessItem(accessItem);

    accessItemCreated.setPermCustom1(true);
    accessItemCreated.setPermAppend(false);
    ((WorkbasketAccessItemImpl) accessItemCreated).setAccessId("willi");

    ThrowingCallable call =
        () -> {
          workbasketService.updateWorkbasketAccessItem(accessItemCreated);
        };
    assertThatThrownBy(call)
        .describedAs("InvalidArgumentException was expected because access id was changed")
        .isInstanceOf(InvalidArgumentException.class);

    ((WorkbasketAccessItemImpl) accessItemCreated).setAccessId("user1");
    WorkbasketAccessItem accessItemUpdated =
        workbasketService.updateWorkbasketAccessItem(accessItemCreated);
    assertThat(accessItemUpdated.isPermAppend()).isFalse();
    assertThat(accessItemUpdated.isPermRead()).isTrue();
    assertThat(accessItemUpdated.isPermCustom11()).isTrue();
    assertThat(accessItemUpdated.isPermCustom1()).isTrue();
    assertThat(accessItemUpdated.isPermCustom2()).isFalse();

    ((WorkbasketAccessItemImpl) accessItemUpdated).setWorkbasketId("2");

    call =
        () -> {
          workbasketService.updateWorkbasketAccessItem(accessItemUpdated);
        };
    assertThatThrownBy(call)
        .describedAs("InvalidArgumentException was expected because key was changed")
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(
      user = "user_1_1",
      groups = {"group_2", "businessadmin"})
  @Test
  void testUpdatedAccessItemLeadsToNotAuthorizedException()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
                 ClassificationNotFoundException, TaskAlreadyExistException {
    TaskService taskService = taskanaEngine.getTaskService();
    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    String wbKey = "USER_2_1";
    String wbDomain = "DOMAIN_A";
    final String groupName = "group_2";

    Task newTask = taskService.newTask(wbKey, wbDomain);
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);
    List<TaskSummary> tasks =
        taskService.createTaskQuery().workbasketKeyDomainIn(new KeyDomain(wbKey, wbDomain)).list();
    assertThat(tasks).hasSize(1);
    assertThat(createdTask).isNotNull();

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000008");
    WorkbasketAccessItem theAccessItem =
        accessItems.stream()
            .filter(x -> groupName.equals(x.getAccessId()))
            .findFirst()
            .orElse(null);

    assertThat(theAccessItem).isNotNull();
    theAccessItem.setPermOpen(false);
    workbasketService.updateWorkbasketAccessItem(theAccessItem);

    ThrowingCallable call =
        () -> {
          taskService
              .createTaskQuery()
              .workbasketKeyDomainIn(new KeyDomain(wbKey, wbDomain))
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testUpdatedAccessItemList() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final String wbId = "WBI:100000000000000000000000000000000004";
    List<WorkbasketAccessItem> accessItems = workbasketService.getWorkbasketAccessItems(wbId);
    final int countBefore = accessItems.size();

    // update some values
    WorkbasketAccessItem item0 = accessItems.get(0);
    item0.setPermAppend(false);
    item0.setPermOpen(false);
    item0.setPermTransfer(false);
    final String updateId0 = item0.getId();
    WorkbasketAccessItem item1 = accessItems.get(1);
    item1.setPermAppend(false);
    item1.setPermOpen(false);
    item1.setPermTransfer(false);
    final String updateId1 = item1.getId();

    workbasketService.setWorkbasketAccessItems(wbId, accessItems);

    List<WorkbasketAccessItem> updatedAccessItems =
        workbasketService.getWorkbasketAccessItems(wbId);
    int countAfter = updatedAccessItems.size();
    assertThat(countAfter).isEqualTo(countBefore);

    item0 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId0)).findFirst().get();
    assertThat(item0.isPermAppend()).isFalse();
    assertThat(item0.isPermOpen()).isFalse();
    assertThat(item0.isPermTransfer()).isFalse();

    item1 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId1)).findFirst().get();
    assertThat(item1.isPermAppend()).isFalse();
    assertThat(item1.isPermOpen()).isFalse();
    assertThat(item1.isPermTransfer()).isFalse();
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testInsertAccessItemList() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final String wbId = "WBI:100000000000000000000000000000000004";
    List<WorkbasketAccessItem> accessItems = workbasketService.getWorkbasketAccessItems(wbId);
    final int countBefore = accessItems.size();

    // update some values
    WorkbasketAccessItem item0 = accessItems.get(0);
    item0.setPermAppend(false);
    item0.setPermOpen(false);
    item0.setPermTransfer(false);
    final String updateId0 = item0.getId();
    // insert new entry
    WorkbasketAccessItem newItem = workbasketService.newWorkbasketAccessItem(wbId, "dummyUser1");
    newItem.setPermRead(true);
    newItem.setPermOpen(true);
    newItem.setPermCustom12(true);
    accessItems.add(newItem);

    workbasketService.setWorkbasketAccessItems(wbId, accessItems);

    List<WorkbasketAccessItem> updatedAccessItems =
        workbasketService.getWorkbasketAccessItems(wbId);
    int countAfter = updatedAccessItems.size();
    assertThat(countAfter).isEqualTo((countBefore + 1));

    item0 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId0)).findFirst().get();
    assertThat(item0.isPermAppend()).isFalse();
    assertThat(item0.isPermOpen()).isFalse();
    assertThat(item0.isPermTransfer()).isFalse();
    assertThat(item0.isPermAppend()).isFalse();
    assertThat(item0.isPermTransfer()).isFalse();
  }

  @WithAccessId(
      user = "teamlead_1",
      groups = {"group_1", "businessadmin"})
  @Test
  void testDeleteAccessItemForAccessItemId() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final String wbId = "WBI:100000000000000000000000000000000001";

    List<WorkbasketAccessItem> originalList = workbasketService.getWorkbasketAccessItems(wbId);
    List<String> originalIds = new ArrayList<>();
    for (WorkbasketAccessItem a : originalList) {
      originalIds.add(a.getId());
    }

    List<WorkbasketAccessItem> accessList = new ArrayList<>(originalList);
    WorkbasketAccessItem newItem = workbasketService.newWorkbasketAccessItem(wbId, "group_1");
    accessList.add(newItem);
    assertThat(accessList).isNotEqualTo(originalList);
    workbasketService.setWorkbasketAccessItems(wbId, accessList);

    List<WorkbasketAccessItem> modifiedList =
        new ArrayList<>(workbasketService.getWorkbasketAccessItems(wbId));
    for (WorkbasketAccessItem a : modifiedList) {
      if (!originalIds.contains(a.getId())) {
        workbasketService.deleteWorkbasketAccessItem(a.getId());
      }
    }
    List<WorkbasketAccessItem> listEqualToOriginal =
        new ArrayList<>(workbasketService.getWorkbasketAccessItems(wbId));

    // with DB2 V 11, the lists are sorted differently...
    assertThat(new HashSet<>(listEqualToOriginal)).isEqualTo(new HashSet<>(originalList));
  }

  @WithAccessId(user = "teamlead_1", groups = "businessadmin")
  @Test
  void testDeleteAccessItemsForAccessId() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final String accessId = "group_1";
    final long accessIdCountBefore =
        workbasketService.createWorkbasketAccessItemQuery().accessIdIn(accessId).count();

    workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);

    final long accessIdCountAfter =
        workbasketService.createWorkbasketAccessItemQuery().accessIdIn(accessId).count();
    assertThat(accessIdCountBefore > accessIdCountAfter).isTrue();
  }

  @WithAccessId(user = "teamlead_1", groups = "businessadmin")
  @Test
  void testDeleteAccessItemsForAccessIdWithUnusedValuesThrowingNoException()
      throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    workbasketService.deleteWorkbasketAccessItemsForAccessId("");
    workbasketService.deleteWorkbasketAccessItemsForAccessId(null);
    workbasketService.deleteWorkbasketAccessItemsForAccessId("123UNUSED456");
  }
}
