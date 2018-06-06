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
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query workbasket by permission" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1_1"})
    @Test
    public void testQueryAllForUserMultipleTimes() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketQuery query = workbasketService.createWorkbasketQuery();
        long count = query.count();
        assertEquals(4, count);
        List<WorkbasketSummary> workbaskets = query.list();
        assertNotNull(workbaskets);
        assertEquals(count, workbaskets.size());
        workbaskets = query.list();
        assertNotNull(workbaskets);
        assertEquals(count, workbaskets.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllForBusinessAdminMultipleTimes() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketQuery query = workbasketService.createWorkbasketQuery();
        long count = query.count();
        assertTrue(count == 25);
        List<WorkbasketSummary> workbaskets = query.list();
        assertNotNull(workbaskets);
        assertEquals(count, workbaskets.size());
        workbaskets = query.list();
        assertNotNull(workbaskets);
        assertEquals(count, workbaskets.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"admin"})
    @Test
    public void testQueryAllForAdminMultipleTimes() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketQuery query = workbasketService.createWorkbasketQuery();
        long count = query.count();
        assertTrue(count == 25);
        List<WorkbasketSummary> workbaskets = query.list();
        assertNotNull(workbaskets);
        assertEquals(count, workbaskets.size());
        workbaskets = query.list();
        assertNotNull(workbaskets);
        assertEquals(count, workbaskets.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketValuesForColumnName() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<String> columnValueList = workbasketService.createWorkbasketQuery()
            .listValues("NAME", null);
        assertNotNull(columnValueList);
        assertEquals(10, columnValueList.size());

        columnValueList = workbasketService.createWorkbasketQuery()
            .nameLike("%korb%")
            .orderByName(asc)
            .listValues("NAME", SortDirection.DESCENDING);  // will override
        assertNotNull(columnValueList);
        assertEquals(4, columnValueList.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByDomain() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_B")
            .list();
        Assert.assertEquals(1L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByDomainAndType() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .typeIn(WorkbasketType.PERSONAL)
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByName() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameIn("Gruppenpostkorb KSC")
            .list();
        Assert.assertEquals(1L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByNameStartsWith() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByNameContains() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Teamlead%", "%Gruppenpostkorb KSC%")
            .list();
        Assert.assertEquals(5L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByNameContainsCaseInsensitive() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%TEAMLEAD%")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByDescription() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .descriptionLike("%ppk%", "%gruppen%")
            .orderByType(desc)
            .orderByDescription(asc)
            .list();
        Assert.assertEquals(9L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByOwnerLike() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .ownerLike("%an%", "%te%")
            .orderByOwner(asc)
            .list();
        Assert.assertEquals(1L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByKey() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC")
            .list();
        Assert.assertEquals(1L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByMultipleKeys() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_KSC")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByMultipleKeysWithUnknownKey() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_Ksc", "GPK_KSC_3")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByKeyContains() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyLike("%KSC%")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByKeyContainsIgnoreCase() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyLike("%kSc%")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByKeyOrNameContainsIgnoreCase() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyOrNameLike("%kSc%")
            .list();
        Assert.assertEquals(9L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByNameAscending() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByName(asc)
            .list();
        Assert.assertEquals(3L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getKey());

        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getName().compareToIgnoreCase(
                    previousSummary.getName()) >= 0);
            }
            previousSummary = wbSummary;
        }

    }

    @WithAccessId(
        userName = "max")
    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByNameDescending() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByName(desc)
            .list();
        Assert.assertEquals(10L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getName().compareToIgnoreCase(
                    previousSummary.getName()) <= 0);
            }
            previousSummary = wbSummary;
        }
    }

    @WithAccessId(
        userName = "max")
    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByKeyAscending() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByKey(asc)
            .list();
        Assert.assertEquals(10L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getKey().compareToIgnoreCase(
                    previousSummary.getKey()) >= 0);
            }
            previousSummary = wbSummary;
        }
    }

    @WithAccessId(
        userName = "max")
    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByKeyDescending() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByKey(desc)
            .list();
        Assert.assertEquals(10L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getKey().compareToIgnoreCase(
                    previousSummary.getKey()) <= 0);
            }
            previousSummary = wbSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByCreated() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .createdWithin(todaysInterval())
            .list();
        Assert.assertEquals(9L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryWorkbasketByModified() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .modifiedWithin(todaysInterval())
            .list();
        Assert.assertEquals(9L, results.size());
    }

    @WithAccessId(
        userName = "unknown",
        groupNames = "admin")
    @Test
    public void testQueryWorkbasketByAdmin()
        throws NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .orderByName(desc)
            .list();
        Assert.assertEquals(25L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getName().compareToIgnoreCase(
                    previousSummary.getName()) <= 0);
            }
            previousSummary = wbSummary;
        }

        results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
            .orderByName(desc)
            .list();

        Assert.assertEquals(13L, results.size());

    }

}
