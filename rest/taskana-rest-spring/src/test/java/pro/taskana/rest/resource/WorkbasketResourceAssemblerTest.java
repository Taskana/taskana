package pro.taskana.rest.resource;

import java.time.Instant;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.Mapping;

/** Test for {@link WorkbasketResourceAssembler}. */
@TaskanaSpringBootTest
class WorkbasketResourceAssemblerTest {

  @Autowired WorkbasketService workbasketService;
  @Autowired WorkbasketResourceAssembler workbasketResourceAssembler;

  @Test
  void workbasketToResource() throws NotAuthorizedException, WorkbasketNotFoundException {
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
    WorkbasketResource resource = workbasketResourceAssembler.toResource(workbasket);
    // then
    testEquality(workbasket, resource);
    verifyLinks(resource);
  }

  @Test
  void resourceWithoutCreated() {
    // given
    WorkbasketResource resource = new WorkbasketResource();
    resource.setWorkbasketId("1");
    resource.setModified("2010-01-01T12:00:00Z");
    resource.setType(WorkbasketType.PERSONAL);
    // when
    Workbasket workbasket = workbasketResourceAssembler.toModel(resource);
    // then
    testEquality(workbasket, resource);
  }

  @Test
  void resourceWithoutModified() {
    // given
    WorkbasketResource resource = new WorkbasketResource();
    resource.setWorkbasketId("1");
    resource.setCreated("2010-01-01T12:00:00Z");
    resource.setType(WorkbasketType.PERSONAL);
    // when
    Workbasket workbasket = workbasketResourceAssembler.toModel(resource);
    // then
    testEquality(workbasket, resource);
  }

  @Test
  void resourceToWorkbasket() {
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

  private void verifyLinks(WorkbasketResource workbasket) {
    Assert.assertEquals(5, workbasket.getLinks().size());
    Assert.assertEquals(
        Mapping.URL_WORKBASKET_ID.replaceAll("\\{.*}", workbasket.getWorkbasketId()),
        workbasket.getLink("self").getHref());
    Assert.assertEquals(
        Mapping.URL_WORKBASKET_ID_DISTRIBUTION.replaceAll("\\{.*}", workbasket.getWorkbasketId()),
        workbasket.getLink("distributionTargets").getHref());
    Assert.assertEquals(Mapping.URL_WORKBASKET, workbasket.getLink("allWorkbaskets").getHref());
    Assert.assertEquals(
        Mapping.URL_WORKBASKET_DISTRIBUTION_ID.replaceAll("\\{.*}", workbasket.getWorkbasketId()),
        workbasket.getLink("removeDistributionTargets").getHref());
  }

  private void testEquality(Workbasket workbasket, WorkbasketResource workbasketResource) {
    Assert.assertEquals(workbasket.getId(), workbasketResource.workbasketId);
    Assert.assertEquals(workbasket.getKey(), workbasketResource.key);
    Assert.assertEquals(
        workbasket.getCreated() == null ? null : workbasket.getCreated().toString(),
        workbasketResource.created);
    Assert.assertEquals(
        workbasket.getModified() == null ? null : workbasket.getModified().toString(),
        workbasketResource.modified);
    Assert.assertEquals(workbasket.getName(), workbasketResource.name);
    Assert.assertEquals(workbasket.getDescription(), workbasketResource.description);
    Assert.assertEquals(workbasket.getOwner(), workbasketResource.owner);
    Assert.assertEquals(workbasket.getDomain(), workbasketResource.domain);
    Assert.assertEquals(workbasket.getType(), workbasketResource.type);
    Assert.assertEquals(workbasket.getCustom1(), workbasketResource.custom1);
    Assert.assertEquals(workbasket.getCustom2(), workbasketResource.custom2);
    Assert.assertEquals(workbasket.getCustom3(), workbasketResource.custom3);
    Assert.assertEquals(workbasket.getCustom4(), workbasketResource.custom4);
    Assert.assertEquals(workbasket.getOrgLevel1(), workbasketResource.orgLevel1);
    Assert.assertEquals(workbasket.getOrgLevel2(), workbasketResource.orgLevel2);
    Assert.assertEquals(workbasket.getOrgLevel3(), workbasketResource.orgLevel3);
    Assert.assertEquals(workbasket.getOrgLevel4(), workbasketResource.orgLevel4);
  }
}
