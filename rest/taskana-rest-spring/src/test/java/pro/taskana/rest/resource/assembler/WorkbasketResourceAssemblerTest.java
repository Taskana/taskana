package pro.taskana.rest.resource.assembler;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.WorkbasketResource;

/**
 * Test for {@link WorkbasketResourceAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class WorkbasketResourceAssemblerTest {

    @Autowired
    WorkbasketService workbasketService;
    @Autowired
    WorkbasketResourceAssembler workbasketResourceAssembler;

    @Test
    public void workbasketToResource() throws NotAuthorizedException, WorkbasketNotFoundException {
        // given
        Workbasket workbasket = workbasketService.newWorkbasket("1", "DOMAIN_A");
        ((WorkbasketImpl) workbasket).setId("ID");
        workbasket.setType(WorkbasketType.PERSONAL);
        workbasket.setName("Testbasket");
        workbasket.setOrgLevel1("Org1");
        workbasket.setOrgLevel2("Org2");
        workbasket.setOrgLevel3("Org3");
        workbasket.setOrgLevel4("Org4");
        workbasket.setDescription("A test workbasket");
        workbasket.setCustom1("1");
        workbasket.setCustom2("2");
        workbasket.setCustom3("3");
        workbasket.setCustom4("4");
        workbasket.setOwner("Lars");
        ((WorkbasketImpl) workbasket).setCreated(Instant.parse("2010-01-01T12:00:00Z"));
        ((WorkbasketImpl) workbasket).setModified(Instant.parse("2010-01-01T12:00:00Z"));
        // when
        WorkbasketResource workbasketResource = workbasketResourceAssembler.toResource(workbasket);
        // then
        testEquality(workbasket, workbasketResource);
    }

    @Test
    public void resourceToWorkbasket() {
        // given
        WorkbasketResource workbasketResource = new WorkbasketResource();
        workbasketResource.setWorkbasketId("1");
        workbasketResource.setCreated("2010-01-01T12:00:00Z");
        workbasketResource.setModified("2010-01-01T12:00:00Z");
        workbasketResource.setCustom1("Custom1");
        workbasketResource.setCustom2("Custom2");
        workbasketResource.setCustom3("Custom3");
        workbasketResource.setCustom4("Custom4");
        workbasketResource.setDescription("Test Ressource");
        workbasketResource.setDomain("DOMAIN_A");
        workbasketResource.setKey("1");
        workbasketResource.setName("Ressource");
        workbasketResource.setOrgLevel1("Org1");
        workbasketResource.setOrgLevel2("Org2");
        workbasketResource.setOrgLevel3("Org3");
        workbasketResource.setOrgLevel4("Org4");
        workbasketResource.setOwner("Lars");
        workbasketResource.setType(WorkbasketType.PERSONAL);
        // when
        Workbasket workbasket = workbasketResourceAssembler.toModel(workbasketResource);
        // then
        testEquality(workbasket, workbasketResource);
    }

    private void testEquality(Workbasket workbasket, WorkbasketResource workbasketResource) {
        Assert.assertEquals(workbasket.getId(), workbasketResource.workbasketId);
        Assert.assertEquals(workbasket.getCustom1(), workbasketResource.custom1);
        Assert.assertEquals(workbasket.getCustom2(), workbasketResource.custom2);
        Assert.assertEquals(workbasket.getCustom3(), workbasketResource.custom3);
        Assert.assertEquals(workbasket.getCustom4(), workbasketResource.custom4);
        Assert.assertEquals(workbasket.getDescription(), workbasketResource.description);
        Assert.assertEquals(workbasket.getCreated().toString(), workbasketResource.created);
        Assert.assertEquals(workbasket.getModified().toString(), workbasketResource.modified);
        Assert.assertEquals(workbasket.getKey(), workbasketResource.key);
        Assert.assertEquals(workbasket.getDomain(), workbasketResource.domain);
        Assert.assertEquals(workbasket.getName(), workbasketResource.name);
        Assert.assertEquals(workbasket.getOrgLevel1(), workbasketResource.orgLevel1);
        Assert.assertEquals(workbasket.getOrgLevel2(), workbasketResource.orgLevel2);
        Assert.assertEquals(workbasket.getOrgLevel3(), workbasketResource.orgLevel3);
        Assert.assertEquals(workbasket.getOrgLevel4(), workbasketResource.orgLevel4);
        Assert.assertEquals(workbasket.getOwner(), workbasketResource.owner);
        Assert.assertEquals(workbasket.getType(), workbasketResource.type);
    }
}
