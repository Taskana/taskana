package pro.taskana.rest.resource.assembler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.DistributionTargetResource;

/**
 * Test for {@link DistributionTargetResourceAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class DistributionTargetResourceAssemblerTest {

    @Autowired
    DistributionTargetResourceAssembler assembler;

    @Autowired
    WorkbasketService workbasketService;

    @Test
    public void WorkbasketSummaryToResource() throws NotAuthorizedException, WorkbasketNotFoundException {
        // given
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("Some key", "Some domain");
        workbasket.setId("Some Id");
        workbasket.setName("Some Name");
        workbasket.setDescription("A cool workbasket");
        workbasket.setOwner("A cool owner");
        workbasket.setType(WorkbasketType.PERSONAL);
        workbasket.setOrgLevel1("Level 1");
        workbasket.setOrgLevel2("Level 2");
        workbasket.setOrgLevel3("Level 3");
        workbasket.setOrgLevel4("Level 4");
        WorkbasketSummary workbasketSummary = workbasket.asSummary();

        // when
        DistributionTargetResource distributionTargetResource = assembler.toResource(workbasketSummary);

        // then
        Assert.assertEquals(distributionTargetResource.key, workbasketSummary.getKey());
        Assert.assertEquals(distributionTargetResource.domain, workbasketSummary.getDomain());
        Assert.assertEquals(distributionTargetResource.getWorkbasketId(), workbasketSummary.getId());
        Assert.assertEquals(distributionTargetResource.name, workbasketSummary.getName());
        Assert.assertEquals(distributionTargetResource.description, workbasketSummary.getDescription());
        Assert.assertEquals(distributionTargetResource.owner, workbasketSummary.getOwner());
        Assert.assertEquals(distributionTargetResource.type, workbasketSummary.getType());
        Assert.assertEquals(distributionTargetResource.orgLevel1, workbasketSummary.getOrgLevel1());
        Assert.assertEquals(distributionTargetResource.orgLevel2, workbasketSummary.getOrgLevel2());
        Assert.assertEquals(distributionTargetResource.orgLevel3, workbasketSummary.getOrgLevel3());
        Assert.assertEquals(distributionTargetResource.orgLevel4, workbasketSummary.getOrgLevel4());
    }
}
