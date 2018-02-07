package acceptance.workbasket;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query access items for workbaskets" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketAccessItemsAccTest extends AbstractAccTest {

    public QueryWorkbasketAccessItemsAccTest() {
        super();
    }

    @Ignore
    @Test
    public void testQueryAccessItemsForAccessIds()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        // List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
        // .accessIdIn("user_1_1", "group_1")
        // .list();
        // Assert.assertEquals(7L, results.size());
    }

    @Ignore
    @Test
    public void testQueryAccessItemsForAccessIdsOrderedAscending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        // List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
        // .accessIdIn("user_1_1", "group_1")
        // .orderByAccessId(ASC)
        // .list();
        // Assert.assertEquals(7L, results.size());
        // Assert.assertEquals("??", results.get(0));
    }

    @Ignore
    @Test
    public void testQueryAccessItemsForAccessIdsAndWorkbasketKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        // List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
        // .accessIdIn("user_1_1", "group_1")
        // .workbasketKeyIn("USER_1_1", "GPK_KSC_1")
        // .list();
        // Assert.assertEquals(3L, results.size());
    }

    @Ignore
    @Test
    public void testQueryAccessItemsByWorkbasketKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        // List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
        // .workbasketKeyIn("USER_1_1")
        // .list();
        // Assert.assertEquals(3L, results.size());
    }

    @Ignore
    @Test
    public void testQueryAccessItemsByWorkbasketKeyOrderedDescending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        // List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
        // .workbasketKeyIn("USER_1_1")
        // .orderByWorkbasketKey(DESC)
        // .list();
        // Assert.assertEquals(3L, results.size());
        // Assert.assertEquals("??", results.get(0));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
