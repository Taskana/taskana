package acceptance.workbasket;

import java.sql.SQLException;
import java.time.Instant;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketType;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "update workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class UpdateWorkbasketAccTest extends AbstractAccTest {

    public UpdateWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdateWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket = workbasketService.getWorkbasketByKey("GPK_KSC");
        Instant modified = workbasket.getModified();

        workbasket.setName("new name");
        workbasket.setDescription("new description");
        workbasket.setType(WorkbasketType.TOPIC);
        workbasket.setDomain("DOMAIN_C");
        workbasket.setOrgLevel1("new level 1");
        workbasket.setOrgLevel2("new level 2");
        workbasket.setOrgLevel3("new level 3");
        workbasket.setOrgLevel4("new level 4");
        workbasket.setCustom1("new custom 1");
        workbasket.setCustom2("new custom 2");
        workbasket.setCustom3("new custom 3");
        workbasket.setCustom4("new custom 4");
        workbasket.setDescription("new description");
        workbasketService.updateWorkbasket(workbasket);

        Workbasket updatedWorkbasket = workbasketService.getWorkbasketByKey("GPK_KSC");
        Assert.assertEquals(workbasket.getId(), updatedWorkbasket.getId());
        Assert.assertEquals(workbasket.getCreated(), updatedWorkbasket.getCreated());
        Assert.assertNotEquals(modified, updatedWorkbasket.getModified());
        Assert.assertEquals("new name", updatedWorkbasket.getName());
        Assert.assertEquals(WorkbasketType.TOPIC, updatedWorkbasket.getType());
    }

    @Ignore
    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testCheckAuthorizationToUpdateWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket = workbasketService.getWorkbasketByKey("GPK_KSC");

        workbasket.setName("new name");
        workbasketService.updateWorkbasket(workbasket);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
