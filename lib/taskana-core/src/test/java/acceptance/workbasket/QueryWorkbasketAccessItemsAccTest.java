package acceptance.workbasket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

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

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryWorkbasketAccessItemValuesForColumnName() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<String> columnValueList = workbasketService.createWorkbasketAccessItemQuery()
            .listValues("WORKBASKET_ID", null);
        assertNotNull(columnValueList);
        assertEquals(24, columnValueList.size());

        columnValueList = workbasketService.createWorkbasketAccessItemQuery()
            .listValues("ACCESS_ID", null);
        assertNotNull(columnValueList);
        assertEquals(9, columnValueList.size());

        long countEntries = workbasketService.createWorkbasketAccessItemQuery().count();
        assertTrue(columnValueList.size() < countEntries);  // DISTINCT
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsForAccessIds()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .list();
        Assert.assertEquals(8L, results.size());
    }

    @WithAccessId(
        userName = "dummy")
    @Test(expected = NotAuthorizedException.class)
    public void testQueryAccessItemsForAccessIdsNotAuthorized()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        workbasketService.createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .list();
        fail("NotAuthorizedException was expected.");
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsForAccessIdsOrderedDescending()
        throws NotAuthorizedException {
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

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsForAccessIdsAndWorkbasketKey()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .workbasketIdIn("WBI:100000000000000000000000000000000006", "WBI:100000000000000000000000000000000002")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsByWorkbasketKey()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsByWorkbasketKeyOrderedDescending()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .orderByWorkbasketId(desc)
            .orderByAccessId(asc)
            .list();
        Assert.assertEquals(3L, results.size());
        Assert.assertEquals("WAI:100000000000000000000000000000000009", results.get(0).getId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForIdIn() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        String[] expectedIds = {"WAI:100000000000000000000000000000000001",
                "WAI:100000000000000000000000000000000015",
                "WAI:100000000000000000000000000000000007"};
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
                .idIn(expectedIds)
                .list();
        for (String id : Arrays.asList(expectedIds)) {
            assertTrue(results.stream().anyMatch(accessItem -> accessItem.getId().equals(id)));
        }
    }

    @WithAccessId(
        userName = "businessadmin")
    @Test
    public void testQueryForOrderById() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItem> results = workbasketService.createWorkbasketAccessItemQuery()
                .orderById(asc)
                .list();
        assertEquals("0000000000000000000000000000000000000900", results.get(0).getId());
        assertEquals("WAI:100000000000000000000000000000000123", results.get(results.size() - 1).getId());
    }

}
