package pro.taskana.rest.resource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/**
 * Test for {@link WorkbasketSummaryRepresentationModelAssembler}.
 */
@TaskanaSpringBootTest
class WorkbasketSummaryAssemblerTest {

  @Autowired
  WorkbasketSummaryRepresentationModelAssembler
      workbasketSummaryRepresentationModelAssembler;
  @Autowired
  WorkbasketService workbasketService;

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
    WorkbasketSummaryRepresentationModel workbasketSummaryRepresentationModel =
        workbasketSummaryRepresentationModelAssembler.toModel(workbasketSummary);
    // then
    Assert.assertEquals(
        workbasketSummary.getDescription(), workbasketSummaryRepresentationModel.getDescription());
    Assert.assertEquals(
        workbasketSummary.getDomain(), workbasketSummaryRepresentationModel.getDomain());
    Assert.assertEquals(
        workbasketSummary.getId(), workbasketSummaryRepresentationModel.getWorkbasketId());
    Assert.assertEquals(workbasketSummary.getKey(), workbasketSummaryRepresentationModel.getKey());
    Assert.assertEquals(
        workbasketSummary.getName(), workbasketSummaryRepresentationModel.getName());
    Assert.assertEquals(
        workbasketSummary.getCustom1(), workbasketSummaryRepresentationModel.getCustom1());
    Assert.assertEquals(
        workbasketSummary.getCustom2(), workbasketSummaryRepresentationModel.getCustom2());
    Assert.assertEquals(
        workbasketSummary.getCustom3(), workbasketSummaryRepresentationModel.getCustom3());
    Assert.assertEquals(
        workbasketSummary.getCustom4(), workbasketSummaryRepresentationModel.getCustom4());
    Assert.assertEquals(
        workbasketSummary.getOrgLevel1(), workbasketSummaryRepresentationModel.getOrgLevel1());
    Assert.assertEquals(
        workbasketSummary.getOrgLevel2(), workbasketSummaryRepresentationModel.getOrgLevel2());
    Assert.assertEquals(
        workbasketSummary.getOrgLevel3(), workbasketSummaryRepresentationModel.getOrgLevel3());
    Assert.assertEquals(
        workbasketSummary.getOrgLevel4(), workbasketSummaryRepresentationModel.getOrgLevel4());
    Assert.assertEquals(
        workbasketSummary.getOwner(), workbasketSummaryRepresentationModel.getOwner());
    Assert.assertEquals(
        workbasketSummary.getType(), workbasketSummaryRepresentationModel.getType());
  }
}
