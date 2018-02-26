package acceptance.workbasket;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query access items for workbaskets" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketAccessItemsAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryWorkbasketAccessItemsAccTest() {
        super();
    }

    @Test
    public void testQueryAccessItemsForAccessIds()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .list();
        Assert.assertEquals(8L, results.size());
    }

    @Test
    public void testQueryAccessItemsForAccessIdsOrderedAscending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItemQuery query = workbasketService.createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .orderByAccessId(desc)
            .orderByWorkbasketId(desc);
        List<WorkbasketAccessItem> results = query.list();
        long count = query.count();
        Assert.assertEquals(8L, results.size());
        Assert.assertEquals(results.size(), count);
        Assert.assertEquals("WAI:100000000000000000000000000000000003", results.get(0).getId());
    }

    @Test
    public void testQueryAccessItemsForAccessIdsAndWorkbasketKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .workbasketIdIn("WBI:100000000000000000000000000000000006", "WBI:100000000000000000000000000000000002")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @Test
    public void testQueryAccessItemsByWorkbasketKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @Test
    public void testQueryAccessItemsByWorkbasketKeyOrderedDescending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .orderByWorkbasketId(desc)
            .orderByAccessId(asc)
            .list();
        Assert.assertEquals(3L, results.size());
        Assert.assertEquals("WAI:100000000000000000000000000000000009", results.get(0).getId());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
