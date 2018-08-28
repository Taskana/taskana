package acceptance.workbasket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketAccessItemExtended;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query access items extended for workbaskets" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketAccessItemsExtendedAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryWorkbasketAccessItemsExtendedAccTest() {
        super();
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryWorkbasketAccessItemExtendedValuesForColumnName() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<String> columnValueList = workbasketService.createWorkbasketAccessItemExtendedQuery()
            .listValues("WORKBASKET_ID", null);
        assertNotNull(columnValueList);
        assertEquals(24, columnValueList.size());

        columnValueList = workbasketService.createWorkbasketAccessItemExtendedQuery()
            .listValues("ACCESS_ID", null);
        assertNotNull(columnValueList);
        assertEquals(9, columnValueList.size());

        columnValueList = workbasketService.createWorkbasketAccessItemExtendedQuery()
            .listValues("WB.KEY", null);
        assertNotNull(columnValueList);
        assertEquals(24, columnValueList.size());

        long countEntries = workbasketService.createWorkbasketAccessItemExtendedQuery().count();
        assertTrue(columnValueList.size() < countEntries);  // DISTINCT
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsForAccessIds()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItemExtended> results = workbasketService.createWorkbasketAccessItemExtendedQuery()
            .accessIdIn("user_1_1", "group_1")
            .list();
        Assert.assertEquals(8L, results.size());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsForAccessIdsWorkbasketKeyLike()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItemExtended> results = workbasketService.createWorkbasketAccessItemExtendedQuery()
            .workbasketKeyLike("GPK_KSC%")
            .list();
        Assert.assertEquals(4L, results.size());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAccessItemsForAccessIdsWorkbasketKeyLikeAndOrderAsc()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketAccessItemExtended> results = workbasketService.createWorkbasketAccessItemExtendedQuery()
            .workbasketKeyLike("GPK_KSC%")
            .orderByWorkbasketKey(SortDirection.ASCENDING)
            .list();
        Assert.assertEquals(4L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getWorkbasketKey());
        Assert.assertEquals("GPK_KSC_2", results.get(3).getWorkbasketKey());

    }

}
