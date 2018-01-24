package acceptance.workbasket;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class GetWorkbasketAccTest extends AbstractAccTest {

    public GetWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "teamlead_1" })
    @Test
    public void testGetWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001");

        Assert.assertEquals("DOMAIN_A", workbasket.getDomain());
        Assert.assertEquals("Gruppenpostkorb KSC", workbasket.getDescription());
        Assert.assertEquals("Gruppenpostkorb KSC", workbasket.getName());
        Assert.assertEquals("GPK_KSC", workbasket.getKey());
        Assert.assertEquals(WorkbasketType.GROUP, workbasket.getType());
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testThrowsExceptionIfIdIsInvalid()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket = workbasketService.getWorkbasket("INVALID_ID");
    }

    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfNotAuthorized()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001");
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
