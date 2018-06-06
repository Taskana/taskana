package acceptance.security;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for workbasket queries and authorization.
 */
@RunWith(JAASRunner.class)
public class WorkbasketQueryAccTest extends AbstractAccTest {

    public WorkbasketQueryAccTest() {
        super();
    }

    @Test
    public void testQueryWorkbasketByUnauthenticated() throws InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .list();
        Assert.assertEquals(0L, results.size());

        try {
            results = workbasketService.createWorkbasketQuery()
                .nameLike("%")
                .accessIdsHavePermission(WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
                .list();
            Assert.fail("NotAuthrorizedException was expected");
        } catch (NotAuthorizedException ex) {

        }

    }

    @WithAccessId(
        userName = "unknown")
    @Test
    public void testQueryWorkbasketByUnknownUser() throws InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .list();
        Assert.assertEquals(0L, results.size());

        try {
            results = workbasketService.createWorkbasketQuery()
                .nameLike("%")
                .accessIdsHavePermission(WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
                .list();
            Assert.fail("NotAuthrorizedException was expected");
        } catch (NotAuthorizedException ex) {

        }

    }

    @WithAccessId(
        userName = "unknown",
        groupNames = "businessadmin")
    @Test
    public void testQueryWorkbasketByBusinessAdmin() throws NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .list();
        Assert.assertEquals(25L, results.size());

        results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
            .list();

        Assert.assertEquals(13L, results.size());

    }

    @WithAccessId(
        userName = "unknown",
        groupNames = "admin")
    @Test
    public void testQueryWorkbasketByAdmin() throws NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .list();
        Assert.assertEquals(25L, results.size());

        results = workbasketService.createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
            .list();

        Assert.assertEquals(13L, results.size());
    }
}
