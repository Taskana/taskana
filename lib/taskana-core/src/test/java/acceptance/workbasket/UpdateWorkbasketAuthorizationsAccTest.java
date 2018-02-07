package acceptance.workbasket;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketAccessItemImpl;
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
        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("key1", "user1");
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
        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("key1", "user1");
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

        ((WorkbasketAccessItemImpl) accessItem).setWorkbasketKey("key2");
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

        Task newTask = taskService.newTask(wbKey);
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskSummary createdTask = taskService.createTask(newTask);
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .workbasketKeyIn(wbKey)
            .list();
        Assert.assertEquals(1, tasks.size());

        List<WorkbasketAccessItem> accessItems = workbasketService.getWorkbasketAuthorizations(wbKey);
        WorkbasketAccessItem theAccessItem = accessItems.stream()
            .filter(x -> groupName.equals(x.getAccessId()))
            .findFirst()
            .orElse(null);

        Assert.assertTrue(theAccessItem != null);
        theAccessItem.setPermOpen(false);
        workbasketService.updateWorkbasketAuthorization(theAccessItem);

        try {
            taskService.createTaskQuery()
                .workbasketKeyIn(wbKey)
                .list();
            fail("NotAuthorizedException was expected ");
        } catch (NotAuthorizedException ignored) {
            // nothing to do
        }

    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
