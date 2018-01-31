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
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidRequestException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query workbasket by permission" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketAccTest extends AbstractAccTest {

    public QueryWorkbasketAccTest() {
        super();
    }

    @Test
    public void testQueryWorkbasketByDomain()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_B")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @Test
    public void testQueryWorkbasketByDomainAndType()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .typeIn(WorkbasketType.PERSONAL)
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByName()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameIn("Gruppenpostkorb KSC")
            .list();
        Assert.assertEquals(1L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getKey());
    }

    @Test
    public void testQueryWorkbasketByNameStartsWith()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByNameContains()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Teamlead%", "%Gruppenpostkorb KSC%")
            .list();
        Assert.assertEquals(8L, results.size());
    }

    @Test
    public void testQueryWorkbasketByNameContainsCaseInsensitive()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%TEAMLEAD%")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC")
            .list();
        Assert.assertEquals(1L, results.size());
    }

    @Test
    public void testQueryWorkbasketByMultipleKeys()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_KSC_2")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @Test
    public void testQueryWorkbasketByMultipleKeysWithUnknownKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_Ksc_2", "GPK_KSC_3")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKeyContains()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyLike("%KSC%")
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKeyContainsIgnoreCase()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyLike("%kSc%")
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKeyOrNameContainsIgnoreCase()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyOrNameLike("%kSc%")
            .list();
        Assert.assertEquals(12L, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByNameAscending()
        throws SQLException, NotAuthorizedException, InvalidRequestException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByName()
            .ascending()
            .list();
        Assert.assertEquals(6L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getKey());
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByNameDescending()
        throws SQLException, NotAuthorizedException, InvalidRequestException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByName()
            .descending()
            .list();
        Assert.assertEquals(6L, results.size());
        Assert.assertEquals("GPK_B_KSC_2", results.get(0).getKey());
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByKeyAscending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByKey()
            .ascending()
            .list();
        Assert.assertEquals(6L, results.size());
        Assert.assertEquals("GPK_B_KSC", results.get(0).getKey());
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByKeyDescending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByKey()
            .descending()
            .list();
        Assert.assertEquals(6L, results.size());
        Assert.assertEquals("GPK_KSC_2", results.get(0).getKey());
    }

    @Test
    public void testQuerySortingWithInvalidInput()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .orderByName()
                .orderByName()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .orderByKey()
                .orderByKey()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .ascending()
                .orderByName()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .descending()
                .orderByName()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .orderByName()
                .ascending()
                .ascending();

            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .orderByName()
                .ascending()
                .descending()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .orderByName()
                .descending()
                .ascending()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }

        try {
            workbasketService.createWorkbasketQuery()
                .nameLike("%Gruppenpostkorb KSC%")
                .orderByName()
                .orderByName()
                .list();
            fail("WorkbasketQuery should have thrown InvalidRequestException.");
        } catch (InvalidRequestException ignored) {
            // nothing to do
        }
    }
}
