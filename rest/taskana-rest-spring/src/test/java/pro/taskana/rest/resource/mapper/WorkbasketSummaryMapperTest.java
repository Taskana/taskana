package pro.taskana.rest.resource.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

/**
 * Test for {@link WorkbasketSummaryMapper}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestConfiguration.class})
@WebAppConfiguration
public class WorkbasketSummaryMapperTest {

    @Autowired
    WorkbasketSummaryMapper workbasketSummaryMapper;
    @Autowired
    WorkbasketService workbasketService;

    @Test
    public void workbasketSummaryToResource() {
        // given
        WorkbasketSummaryImpl workbasketSummary = (WorkbasketSummaryImpl) workbasketService.newWorkbasket("1",
            "DOMAIN_A").asSummary();
        workbasketSummary.setDescription("WorkbasketSummaryImplTes");
        workbasketSummary.setId("1");
        workbasketSummary.setName("WorkbasketSummary");
        workbasketSummary.setOrgLevel1("Org1");
        workbasketSummary.setOrgLevel2("Org2");
        workbasketSummary.setOrgLevel3("Org3");
        workbasketSummary.setOrgLevel4("Org4");
        workbasketSummary.setOwner("Lars");
        workbasketSummary.setType(WorkbasketType.PERSONAL);
        // when
        WorkbasketSummaryResource workbasketSummaryResource = workbasketSummaryMapper.toResource(workbasketSummary);
        // then
        Assert.assertEquals(workbasketSummary.getDescription(), workbasketSummaryResource.description);
        Assert.assertEquals(workbasketSummary.getDomain(), workbasketSummaryResource.domain);
        Assert.assertEquals(workbasketSummary.getId(), workbasketSummaryResource.workbasketId);
        Assert.assertEquals(workbasketSummary.getKey(), workbasketSummaryResource.key);
        Assert.assertEquals(workbasketSummary.getName(), workbasketSummaryResource.name);
        Assert.assertEquals(workbasketSummary.getOrgLevel1(), workbasketSummaryResource.orgLevel1);
        Assert.assertEquals(workbasketSummary.getOrgLevel2(), workbasketSummaryResource.orgLevel2);
        Assert.assertEquals(workbasketSummary.getOrgLevel3(), workbasketSummaryResource.orgLevel3);
        Assert.assertEquals(workbasketSummary.getOrgLevel4(), workbasketSummaryResource.orgLevel4);
        Assert.assertEquals(workbasketSummary.getOwner(), workbasketSummaryResource.owner);
        Assert.assertEquals(workbasketSummary.getType(), workbasketSummaryResource.type);
    }
}
