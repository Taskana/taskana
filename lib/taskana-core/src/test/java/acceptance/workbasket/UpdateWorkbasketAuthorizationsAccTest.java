package acceptance.workbasket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.KeyDomain;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "update workbasket" scenarios.
 */
@ExtendWith(JAASExtension.class)
class UpdateWorkbasketAuthorizationsAccTest extends AbstractAccTest {

    UpdateWorkbasketAuthorizationsAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    void testUpdateWorkbasketAccessItemSucceeds()
        throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workbasketService
            .newWorkbasketAccessItem("WBI:100000000000000000000000000000000002", "user1");
        accessItem.setPermAppend(true);
        accessItem.setPermCustom11(true);
        accessItem.setPermRead(true);
        accessItem = workbasketService.createWorkbasketAccessItem(accessItem);

        WorkbasketAccessItem newItem = accessItem;
        accessItem.setPermCustom1(true);
        accessItem.setPermAppend(false);
        accessItem.setAccessName("Rojas, Miguel");
        workbasketService.updateWorkbasketAccessItem(accessItem);
        List<WorkbasketAccessItem> items = workbasketService
            .getWorkbasketAccessItems("WBI:100000000000000000000000000000000002");

        WorkbasketAccessItem updatedItem = items.stream()
            .filter(t -> newItem.getId().equals(t.getId()))
            .findFirst()
            .orElse(null);

        assertNotNull(updatedItem);
        assertEquals("Rojas, Miguel", updatedItem.getAccessName());
        assertFalse(updatedItem.isPermAppend());
        assertTrue(updatedItem.isPermRead());
        assertTrue(updatedItem.isPermCustom11());
        assertTrue(updatedItem.isPermCustom1());
        assertFalse(updatedItem.isPermCustom2());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    void testUpdateWorkbasketAccessItemRejected()
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workbasketService
            .newWorkbasketAccessItem("WBI:100000000000000000000000000000000001", "user1");
        accessItem.setPermAppend(true);
        accessItem.setPermCustom11(true);
        accessItem.setPermRead(true);
        WorkbasketAccessItem accessItemCreated = workbasketService.createWorkbasketAccessItem(accessItem);

        accessItemCreated.setPermCustom1(true);
        accessItemCreated.setPermAppend(false);
        ((WorkbasketAccessItemImpl) accessItemCreated).setAccessId("willi");

        Assertions.assertThrows(InvalidArgumentException.class, () ->
                workbasketService.updateWorkbasketAccessItem(accessItemCreated),
            "InvalidArgumentException was expected because access id was changed");

        ((WorkbasketAccessItemImpl) accessItemCreated).setAccessId("user1");
        WorkbasketAccessItem accessItemUpdated = workbasketService.updateWorkbasketAccessItem(accessItemCreated);
        assertFalse(accessItemUpdated.isPermAppend());
        assertTrue(accessItemUpdated.isPermRead());
        assertTrue(accessItemUpdated.isPermCustom11());
        assertTrue(accessItemUpdated.isPermCustom1());
        assertFalse(accessItemUpdated.isPermCustom2());

        ((WorkbasketAccessItemImpl) accessItemUpdated).setWorkbasketId("2");

        Assertions.assertThrows(InvalidArgumentException.class, () ->
                workbasketService.updateWorkbasketAccessItem(accessItemUpdated),
            "InvalidArgumentException was expected because key was changed");
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_2", "businessadmin"})
    @Test
    void testUpdatedAccessItemLeadsToNotAuthorizedException()
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException {
        TaskService taskService = taskanaEngine.getTaskService();
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        String wbKey = "USER_2_1";
        String wbDomain = "DOMAIN_A";
        String groupName = "group_2";

        Task newTask = taskService.newTask(wbKey, wbDomain);
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain(wbKey, wbDomain))
            .list();
        assertEquals(1, tasks.size());
        assertThat(createdTask, not(equalTo(null)));

        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAccessItems("WBI:100000000000000000000000000000000008");
        WorkbasketAccessItem theAccessItem = accessItems.stream()
            .filter(x -> groupName.equals(x.getAccessId()))
            .findFirst()
            .orElse(null);

        assertNotNull(theAccessItem);
        theAccessItem.setPermOpen(false);
        workbasketService.updateWorkbasketAccessItem(theAccessItem);

        Assertions.assertThrows(NotAuthorizedToQueryWorkbasketException.class, () ->
            taskService.createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain(wbKey, wbDomain))
                .list());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    void testUpdatedAccessItemList() throws InvalidArgumentException, NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String wbId = "WBI:100000000000000000000000000000000004";
        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAccessItems(wbId);
        int countBefore = accessItems.size();

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

        List<WorkbasketAccessItem> updatedAccessItems = workbasketService
            .getWorkbasketAccessItems(wbId);
        int countAfter = updatedAccessItems.size();
        assertThat(countAfter, equalTo(countBefore));

        item0 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId0)).findFirst().get();
        assertFalse(item0.isPermAppend());
        assertFalse(item0.isPermOpen());
        assertFalse(item0.isPermTransfer());

        item1 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId1)).findFirst().get();
        assertFalse(item1.isPermAppend());
        assertFalse(item1.isPermOpen());
        assertFalse(item1.isPermTransfer());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    void testUpdatedAccessItemListToEmptyList() throws InvalidArgumentException, NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String wbId = "WBI:100000000000000000000000000000000004";
        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAccessItems(wbId);
        int countBefore = accessItems.size();
        assertThat(3, equalTo(countBefore));

        workbasketService.setWorkbasketAccessItems(wbId, new ArrayList<>());

        List<WorkbasketAccessItem> updatedAccessItems = workbasketService
            .getWorkbasketAccessItems(wbId);
        int countAfter = updatedAccessItems.size();
        assertThat(0, equalTo(countAfter));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    void testInsertAccessItemList() throws InvalidArgumentException, NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String wbId = "WBI:100000000000000000000000000000000004";
        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAccessItems(wbId);
        int countBefore = accessItems.size();

        // update some values
        WorkbasketAccessItem item0 = accessItems.get(0);
        item0.setPermAppend(false);
        item0.setPermOpen(false);
        item0.setPermTransfer(false);
        final String updateId0 = item0.getId();
        // insert new entry
        WorkbasketAccessItem newItem = workbasketService.newWorkbasketAccessItem(wbId, CurrentUserContext.getUserid());
        newItem.setPermRead(true);
        newItem.setPermOpen(true);
        newItem.setPermCustom12(true);
        accessItems.add(newItem);

        workbasketService.setWorkbasketAccessItems(wbId, accessItems);

        List<WorkbasketAccessItem> updatedAccessItems = workbasketService
            .getWorkbasketAccessItems(wbId);
        int countAfter = updatedAccessItems.size();
        assertEquals((countBefore + 1), countAfter);

        item0 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId0)).findFirst().get();
        assertFalse(item0.isPermAppend());
        assertFalse(item0.isPermOpen());
        assertFalse(item0.isPermTransfer());
        assertFalse(item0.isPermAppend());
        assertFalse(item0.isPermTransfer());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    void testDeleteAccessItemForAccessItemId() throws NotAuthorizedException, InvalidArgumentException {
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
        assertNotEquals(originalList, accessList);
        workbasketService.setWorkbasketAccessItems(wbId, accessList);

        List<WorkbasketAccessItem> modifiedList = new ArrayList<>(
            workbasketService.getWorkbasketAccessItems(wbId));
        for (WorkbasketAccessItem a : modifiedList) {
            if (!originalIds.contains(a.getId())) {
                workbasketService.deleteWorkbasketAccessItem(a.getId());
            }
        }
        List<WorkbasketAccessItem> listEqualToOriginal = new ArrayList<>(
            workbasketService.getWorkbasketAccessItems(wbId));
        assertEquals(originalList, listEqualToOriginal);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"businessadmin"})
    @Test
    void testDeleteAccessItemsForAccessId() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String accessId = "group_1";
        final long accessIdCountBefore = workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn(accessId)
            .count();

        workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);

        final long accessIdCountAfter = workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn(accessId)
            .count();
        assertTrue(accessIdCountBefore > accessIdCountAfter);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"businessadmin"})
    @Test
    void testDeleteAccessItemsForAccessIdWithUnusedValuesThrowingNoException() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        workbasketService.deleteWorkbasketAccessItemsForAccessId("");
        workbasketService.deleteWorkbasketAccessItemsForAccessId(null);
        workbasketService.deleteWorkbasketAccessItemsForAccessId("123UNUSED456");
    }

}
