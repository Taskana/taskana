package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

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
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
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
    testEqualityAfterConversion(workbasketSummaryRepresentationModel, workbasketSummary);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    WorkbasketSummaryRepresentationModel repModel =
        new WorkbasketSummaryRepresentationModel();
    repModel.setWorkbasketId("1");
    repModel.setCustom1("Custom1");
    repModel.setCustom2("Custom2");
    repModel.setCustom3("Custom3");
    repModel.setCustom4("Custom4");
    repModel.setDescription("Test Ressource");
    repModel.setDomain("DOMAIN_A");
    repModel.setKey("1");
    repModel.setName("Ressource");
    repModel.setOrgLevel1("Org1");
    repModel.setOrgLevel2("Org2");
    repModel.setOrgLevel3("Org3");
    repModel.setOrgLevel4("Org4");
    repModel.setOwner("Lars");
    repModel.setType(WorkbasketType.PERSONAL);
    // when
    WorkbasketSummaryImpl workbasket =
        (WorkbasketSummaryImpl) workbasketSummaryRepresentationModelAssembler
                                    .toEntityModel(repModel);
    // then
    testEqualityAfterConversion(repModel, workbasket);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
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

    WorkbasketSummaryRepresentationModel repModel =
        workbasketSummaryRepresentationModelAssembler.toModel(workbasketSummary);
    WorkbasketSummaryImpl workbasketSummary2
        = (WorkbasketSummaryImpl) workbasketSummaryRepresentationModelAssembler
                                      .toEntityModel(repModel);

    testEqualityOfEntities(workbasketSummary, workbasketSummary2);
  }

  private void testEqualityAfterConversion(WorkbasketSummaryRepresentationModel repModel,
      WorkbasketSummary summary) {
    Assert.assertEquals(summary.getDescription(), repModel.getDescription());
    Assert.assertEquals(summary.getDomain(), repModel.getDomain());
    Assert.assertEquals(summary.getId(), repModel.getWorkbasketId());
    Assert.assertEquals(summary.getKey(), repModel.getKey());
    Assert.assertEquals(summary.getName(), repModel.getName());
    Assert.assertEquals(summary.getCustom1(), repModel.getCustom1());
    Assert.assertEquals(summary.getCustom2(), repModel.getCustom2());
    Assert.assertEquals(summary.getCustom3(), repModel.getCustom3());
    Assert.assertEquals(summary.getCustom4(), repModel.getCustom4());
    Assert.assertEquals(summary.getOrgLevel1(), repModel.getOrgLevel1());
    Assert.assertEquals(summary.getOrgLevel2(), repModel.getOrgLevel2());
    Assert.assertEquals(summary.getOrgLevel3(), repModel.getOrgLevel3());
    Assert.assertEquals(summary.getOrgLevel4(), repModel.getOrgLevel4());
    Assert.assertEquals(summary.getOwner(), repModel.getOwner());
    Assert.assertEquals(summary.getType(), repModel.getType());
  }

  private void testEqualityOfEntities(WorkbasketSummary workbasket, WorkbasketSummary workbasket2) {
    assertThat(workbasket2.getId()).isEqualTo(workbasket.getId());
    assertThat(workbasket2.getKey()).isEqualTo(workbasket.getKey());
    assertThat(workbasket2.getName()).isEqualTo(workbasket.getName());
    assertThat(workbasket2.getDescription())
        .isEqualTo(workbasket.getDescription());
    assertThat(workbasket2.getOwner()).isEqualTo(workbasket.getOwner());
    assertThat(workbasket2.getDomain()).isEqualTo(workbasket.getDomain());
    assertThat(workbasket2.getType()).isEqualTo(workbasket.getType());
    assertThat(workbasket2.getCustom1()).isEqualTo(workbasket.getCustom1());
    assertThat(workbasket2.getCustom2()).isEqualTo(workbasket.getCustom2());
    assertThat(workbasket2.getCustom3()).isEqualTo(workbasket.getCustom3());
    assertThat(workbasket2.getCustom4()).isEqualTo(workbasket.getCustom4());
    assertThat(workbasket2.getOrgLevel1()).isEqualTo(workbasket.getOrgLevel1());
    assertThat(workbasket2.getOrgLevel2()).isEqualTo(workbasket.getOrgLevel2());
    assertThat(workbasket2.getOrgLevel3()).isEqualTo(workbasket.getOrgLevel3());
    assertThat(workbasket2.getOrgLevel4()).isEqualTo(workbasket.getOrgLevel4());
  }

  private void testLinks() {
  }
}
