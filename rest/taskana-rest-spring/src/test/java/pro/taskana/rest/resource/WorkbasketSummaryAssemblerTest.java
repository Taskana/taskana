package pro.taskana.rest.resource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.internal.WorkbasketSummaryImpl;

/** Test for {@link WorkbasketSummaryResourceAssembler}. */
@TaskanaSpringBootTest
class WorkbasketSummaryAssemblerTest {

  @Autowired WorkbasketSummaryResourceAssembler workbasketSummaryAssembler;
  @Autowired WorkbasketService workbasketService;

  @Test
  void workbasketSummaryToResource() {
    // given
    WorkbasketSummaryImpl workbasketSummary =
        (WorkbasketSummaryImpl) workbasketService.newWorkbasket("1", "DOMAIN_A").asSummary();
    workbasketSummary.setDescription("WorkbasketSummaryImplTes");
    workbasketSummary.setId("1");
    workbasketSummary.setName("WorkbasketSummary");
    workbasketSummary.setCustom1("custom1");
    workbasketSummary.setCustom2("custom2");
    workbasketSummary.setCustom3("custom3");
    workbasketSummary.setCustom4("custom4");
    workbasketSummary.setOrgLevel1("Org1");
    workbasketSummary.setOrgLevel2("Org2");
    workbasketSummary.setOrgLevel3("Org3");
    workbasketSummary.setOrgLevel4("Org4");
    workbasketSummary.setOwner("Lars");
    workbasketSummary.setType(WorkbasketType.PERSONAL);
    // when
    WorkbasketSummaryResource workbasketSummaryResource =
        workbasketSummaryAssembler.toResource(workbasketSummary);
    // then
    Assert.assertEquals(
        workbasketSummary.getDescription(), workbasketSummaryResource.getDescription());
    Assert.assertEquals(workbasketSummary.getDomain(), workbasketSummaryResource.getDomain());
    Assert.assertEquals(workbasketSummary.getId(), workbasketSummaryResource.getWorkbasketId());
    Assert.assertEquals(workbasketSummary.getKey(), workbasketSummaryResource.getKey());
    Assert.assertEquals(workbasketSummary.getName(), workbasketSummaryResource.getName());
    Assert.assertEquals(workbasketSummary.getCustom1(), workbasketSummaryResource.getCustom1());
    Assert.assertEquals(workbasketSummary.getCustom2(), workbasketSummaryResource.getCustom2());
    Assert.assertEquals(workbasketSummary.getCustom3(), workbasketSummaryResource.getCustom3());
    Assert.assertEquals(workbasketSummary.getCustom4(), workbasketSummaryResource.getCustom4());
    Assert.assertEquals(workbasketSummary.getOrgLevel1(), workbasketSummaryResource.getOrgLevel1());
    Assert.assertEquals(workbasketSummary.getOrgLevel2(), workbasketSummaryResource.getOrgLevel2());
    Assert.assertEquals(workbasketSummary.getOrgLevel3(), workbasketSummaryResource.getOrgLevel3());
    Assert.assertEquals(workbasketSummary.getOrgLevel4(), workbasketSummaryResource.getOrgLevel4());
    Assert.assertEquals(workbasketSummary.getOwner(), workbasketSummaryResource.getOwner());
    Assert.assertEquals(workbasketSummary.getType(), workbasketSummaryResource.getType());
  }
}
