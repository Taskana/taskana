package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.TaskService;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test which does test the deletion of a workbasket and all wanted failures.
 */
@RunWith(JAASRunner.class)
public class DeleteWorkbasketAccTest extends AbstractAccTest {

    private WorkbasketService workbasketService;

    private TaskService taskService;

    public DeleteWorkbasketAccTest() {
        super();
    }

    @Before
    public void setUpMethod() {
        workbasketService = taskanaEngine.getWorkbasketService();
        taskService = taskanaEngine.getTaskService();
    }

    @WithAccessId(userName = "admin", groupNames = {"businessadmin"})
    @Test
    public void testDeleteWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasket("USER_2_2", "DOMAIN_A");

        try {
            workbasketService.deleteWorkbasket(wb.getId());
            workbasketService.getWorkbasket("USER_2_2", "DOMAIN_A");
            fail("There should be no result for a deleted Workbasket.");
        } catch (WorkbasketNotFoundException | WorkbasketInUseException e) {
            // Workbasket is deleted
        }
    }

    @WithAccessId(userName = "elena")
    @Test(expected = NotAuthorizedException.class)
    public void testDeleteWorkbasketNotAuthorized()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasket("TEAMLEAD_2", "DOMAIN_A");
        workbasketService.deleteWorkbasket(wb.getId());

        workbasketService.getWorkbasket("TEAMLEAD_2", "DOMAIN_A");
        fail("NotAuthorizedException was expected.");
    }

    @WithAccessId(userName = "user_1_1", groupNames = {"teamlead_1", "group_1", "businessadmin"})
    @Test
    public void testDeleteWorkbasketAlsoAsDistributionTarget()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
        int distTargets = workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001")
            .size();

        try {
            // WB deleted
            workbasketService.deleteWorkbasket(wb.getId());
            workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
            fail("There should be no result for a deleted Workbasket.");
        } catch (WorkbasketNotFoundException | WorkbasketInUseException e) {
            // Workbasket is deleted
        }

        int newDistTargets = workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001")
            .size();
        assertThat(newDistTargets, equalTo(3));
        assertTrue(newDistTargets < distTargets);
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testDeleteWorkbasketWithNullOrEmptyParam()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException {
        // Test Null-Value
        try {
            workbasketService.deleteWorkbasket(null);
            fail("delete() should have thrown an InvalidArgumentException, when the param ID is null.");
        } catch (InvalidArgumentException e) {
            // Nothing to do here
        }

        // Test EMPTY-Value
        try {
            workbasketService.deleteWorkbasket("");
            fail("delete() should have thrown an InvalidArgumentException, when the param ID is EMPTY-String.");
        } catch (InvalidArgumentException e) {
            // Nothing to do here
        }
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test(expected = WorkbasketNotFoundException.class)
    public void testDeleteWorkbasketButNotExisting()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        workbasketService.deleteWorkbasket("SOME NOT EXISTING ID");
    }

    @WithAccessId(userName = "user_1_2",
        groupNames = {"businessadmin"})
    @Test(expected = WorkbasketInUseException.class)
    public void testDeleteWorkbasketWhichIsUsed()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasket("USER_1_2", "DOMAIN_A");   // all rights, DOMAIN_A with Tasks
        workbasketService.deleteWorkbasket(wb.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"businessadmin"})
    @Test
    public void testCreateAndDeleteWorkbasket()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
        DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

        Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
        workbasket.setName("TheUltimate");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        workbasket = workbasketService.createWorkbasket(workbasket);

        boolean failed = false;
        try {
            workbasketService.deleteWorkbasket(workbasket.getId());
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed);
    }

    @WithAccessId(userName = "teamlead_2", groupNames = {"businessadmin"})
    @Test
    public void testCascadingDeleteOfAccessItems()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidArgumentException {
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
        try {
            workbasketService.deleteWorkbasket(wbId);
            workbasketService.getWorkbasket("WBI:100000000000000000000000000000000008");
            fail("There should be no result for a deleted Workbasket.");
        } catch (WorkbasketNotFoundException | WorkbasketInUseException e) {
            // Workbasket is deleted
        }

        List<WorkbasketAccessItem> accessItemsAfter = workbasketService.getWorkbasketAccessItems(wbId);
        assertEquals(0, accessItemsAfter.size());
    }

    @WithAccessId(userName = "admin", groupNames = {"businessadmin"})
    @Test
    public void testMarkWorkbasketForDeletion()
        throws WorkbasketInUseException, NotAuthorizedException, WorkbasketNotFoundException, InvalidArgumentException,
        InvalidOwnerException, InvalidStateException, TaskNotFoundException {
        Workbasket wb = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000006");
        boolean markedForDeletion;

        TaskImpl task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000000");
        taskService.forceCompleteTask(task.getId());
        task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000001");
        taskService.forceCompleteTask(task.getId());
        task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000002");
        taskService.forceCompleteTask(task.getId());

        markedForDeletion = workbasketService.deleteWorkbasket(wb.getId());
        assertFalse(markedForDeletion);

        wb = workbasketService.getWorkbasket(wb.getId());
        assertTrue(wb.isMarkedForDeletion());
    }

}
