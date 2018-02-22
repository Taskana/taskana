package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
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
import pro.taskana.exceptions.InvalidWorkbasketException;
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
        groupNames = {"group_1"})
    @Test
    public void testUpdateWorkbasketAccessItemSucceeds() throws InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workbasketService
            .newWorkbasketAccessItem("key1000000000000000000000000000000000000", "user1");
        accessItem.setPermAppend(true);
        accessItem.setPermCustom11(true);
        accessItem.setPermRead(true);
        accessItem = workbasketService.createWorkbasketAuthorization(accessItem);

        accessItem.setPermCustom1(true);
        accessItem.setPermAppend(false);
        WorkbasketAccessItem updatedItem = workbasketService.updateWorkbasketAuthorization(accessItem);

        Assert.assertEquals(false, updatedItem.isPermAppend());
        Assert.assertEquals(true, updatedItem.isPermRead());
        Assert.assertEquals(true, updatedItem.isPermCustom11());
        Assert.assertEquals(true, updatedItem.isPermCustom1());
        Assert.assertEquals(false, updatedItem.isPermCustom2());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdateWorkbasketAccessItemRejected()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workbasketService
            .newWorkbasketAccessItem("1000000000000000000000000000000000000000", "user1");
        accessItem.setPermAppend(true);
        accessItem.setPermCustom11(true);
        accessItem.setPermRead(true);
        accessItem = workbasketService.createWorkbasketAuthorization(accessItem);

        accessItem.setPermCustom1(true);
        accessItem.setPermAppend(false);
        ((WorkbasketAccessItemImpl) accessItem).setAccessId("willi");
        try {
            workbasketService.updateWorkbasketAuthorization(accessItem);
            fail("InvalidArgumentException was expected because access id was changed");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        ((WorkbasketAccessItemImpl) accessItem).setAccessId("user1");
        accessItem = workbasketService.updateWorkbasketAuthorization(accessItem);
        Assert.assertEquals(false, accessItem.isPermAppend());
        Assert.assertEquals(true, accessItem.isPermRead());
        Assert.assertEquals(true, accessItem.isPermCustom11());
        Assert.assertEquals(true, accessItem.isPermCustom1());
        Assert.assertEquals(false, accessItem.isPermCustom2());

        ((WorkbasketAccessItemImpl) accessItem).setWorkbasketId("2");
        try {
            workbasketService.updateWorkbasketAuthorization(accessItem);
            fail("InvalidArgumentException was expected because key was changed");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_2"})
    @Test
    public void testUpdatedAccessItemLeadsToNotAuthorizedException()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {
        TaskService taskService = taskanaEngine.getTaskService();
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        String wbKey = "USER_2_1";
        String groupName = "group_2";

        Task newTask = taskService.newTask(wbKey, "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain(wbKey, "DOMAIN_A"))
            .list();
        Assert.assertEquals(1, tasks.size());
        assertThat(createdTask, not(equalTo(null)));

        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAuthorizations("WBI:100000000000000000000000000000000008");
        WorkbasketAccessItem theAccessItem = accessItems.stream()
            .filter(x -> groupName.equals(x.getAccessId()))
            .findFirst()
            .orElse(null);

        Assert.assertTrue(theAccessItem != null);
        theAccessItem.setPermOpen(false);
        workbasketService.updateWorkbasketAuthorization(theAccessItem);

        try {
            taskService.createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain(wbKey, "DOMAIN_A"))
                .list();
            fail("NotAuthorizedToQueryWorkbasketException was expected ");
        } catch (NotAuthorizedToQueryWorkbasketException ignored) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdatedAccessItemList() throws InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String wbId = "WBI:100000000000000000000000000000000004";
        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAuthorizations(wbId);
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

        workbasketService.setWorkbasketAuthorizations(wbId, accessItems);

        List<WorkbasketAccessItem> updatedAccessItems = workbasketService
            .getWorkbasketAuthorizations(wbId);
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
        groupNames = {"group_1"})
    @Test
    public void testInsertAccessItemList() throws InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        final String wbId = "WBI:100000000000000000000000000000000004";
        List<WorkbasketAccessItem> accessItems = workbasketService
            .getWorkbasketAuthorizations(wbId);
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

        workbasketService.setWorkbasketAuthorizations(wbId, accessItems);

        List<WorkbasketAccessItem> updatedAccessItems = workbasketService
            .getWorkbasketAuthorizations(wbId);
        int countAfter = updatedAccessItems.size();
        assertTrue((countBefore + 1) == countAfter);

        item0 = updatedAccessItems.stream().filter(i -> i.getId().equals(updateId0)).findFirst().get();
        assertFalse(item0.isPermAppend());
        assertFalse(item0.isPermOpen());
        assertFalse(item0.isPermTransfer());
        assertFalse(item0.isPermAppend());
        assertFalse(item0.isPermTransfer());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
