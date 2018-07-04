package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

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
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "update workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class UpdateWorkbasketAuthorizationsAccTest extends AbstractAccTest {

    public UpdateWorkbasketAuthorizationsAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testUpdateWorkbasketAccessItemSucceeds()
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

        assertEquals("Rojas, Miguel", updatedItem.getAccessName());
        assertEquals(false, updatedItem.isPermAppend());
        assertEquals(true, updatedItem.isPermRead());
        assertEquals(true, updatedItem.isPermCustom11());
        assertEquals(true, updatedItem.isPermCustom1());
        assertEquals(false, updatedItem.isPermCustom2());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testUpdateWorkbasketAccessItemRejected()
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workbasketService
            .newWorkbasketAccessItem("WBI:100000000000000000000000000000000001", "user1");
        accessItem.setPermAppend(true);
        accessItem.setPermCustom11(true);
        accessItem.setPermRead(true);
        accessItem = workbasketService.createWorkbasketAccessItem(accessItem);

        accessItem.setPermCustom1(true);
        accessItem.setPermAppend(false);
        ((WorkbasketAccessItemImpl) accessItem).setAccessId("willi");
        try {
            workbasketService.updateWorkbasketAccessItem(accessItem);
            fail("InvalidArgumentException was expected because access id was changed");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        ((WorkbasketAccessItemImpl) accessItem).setAccessId("user1");
        accessItem = workbasketService.updateWorkbasketAccessItem(accessItem);
        assertEquals(false, accessItem.isPermAppend());
        assertEquals(true, accessItem.isPermRead());
        assertEquals(true, accessItem.isPermCustom11());
        assertEquals(true, accessItem.isPermCustom1());
        assertEquals(false, accessItem.isPermCustom2());

        ((WorkbasketAccessItemImpl) accessItem).setWorkbasketId("2");
        try {
            workbasketService.updateWorkbasketAccessItem(accessItem);
            fail("InvalidArgumentException was expected because key was changed");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_2", "businessadmin"})
    @Test
    public void testUpdatedAccessItemLeadsToNotAuthorizedException()
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

        assertTrue(theAccessItem != null);
        theAccessItem.setPermOpen(false);
        workbasketService.updateWorkbasketAccessItem(theAccessItem);

        try {
            taskService.createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain(wbKey, wbDomain))
                .list();
            fail("NotAuthorizedToQueryWorkbasketException was expected ");
        } catch (NotAuthorizedToQueryWorkbasketException ignored) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testUpdatedAccessItemList() throws InvalidArgumentException, NotAuthorizedException {
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
    public void testUpdatedAccessItemListToEmptyList() throws InvalidArgumentException, NotAuthorizedException {
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
    public void testInsertAccessItemList() throws InvalidArgumentException, NotAuthorizedException {
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
        assertTrue((countBefore + 1) == countAfter);

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
    public void testDeleteAccessItemForAccessItemId() throws NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String wbId = "WBI:100000000000000000000000000000000001";

        List<WorkbasketAccessItem> originalList = workbasketService.getWorkbasketAccessItems(wbId);
        List<String> originalIds = new ArrayList<String>();
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
    public void testDeleteAccessItemsForAccessId() throws NotAuthorizedException {
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
    public void testDeleteAccessItemsForAccessIdWithUnusedValuesThrowingNoException() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        workbasketService.deleteWorkbasketAccessItemsForAccessId("");
        workbasketService.deleteWorkbasketAccessItemsForAccessId(null);
        workbasketService.deleteWorkbasketAccessItemsForAccessId("123UNUSED456");
    }

}
