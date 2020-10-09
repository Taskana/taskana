package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_1;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_2;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_3;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_4;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/** Test for {@link WorkbasketSummaryRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class WorkbasketSummaryRepresentationModelAssemblerTest {

  private final WorkbasketSummaryRepresentationModelAssembler assembler;

  private final WorkbasketService workbasketService;

  @Autowired
  WorkbasketSummaryRepresentationModelAssemblerTest(
      WorkbasketSummaryRepresentationModelAssembler assembler,
      WorkbasketService workbasketService) {
    this.assembler = assembler;
    this.workbasketService = workbasketService;
  }

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
    WorkbasketSummaryRepresentationModel repModel = assembler.toModel(workbasketSummary);
    // then
    testEquality(workbasketSummary, repModel);
    testLinks(repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    WorkbasketSummaryRepresentationModel repModel = new WorkbasketSummaryRepresentationModel();
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
    WorkbasketSummary workbasket = assembler.toEntityModel(repModel);
    // then
    testEquality(workbasket, repModel);
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

    WorkbasketSummaryRepresentationModel repModel = assembler.toModel(workbasketSummary);
    WorkbasketSummary workbasketSummary2 = assembler.toEntityModel(repModel);

    assertThat(workbasketSummary)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(workbasketSummary2)
        .isEqualTo(workbasketSummary2);
  }

  static void testEquality(
      WorkbasketSummary summary, WorkbasketSummaryRepresentationModel repModel) {
    assertThat(summary).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();
    Assert.assertEquals(summary.getDescription(), repModel.getDescription());
    Assert.assertEquals(summary.getDomain(), repModel.getDomain());
    Assert.assertEquals(summary.getId(), repModel.getWorkbasketId());
    Assert.assertEquals(summary.getKey(), repModel.getKey());
    Assert.assertEquals(summary.getName(), repModel.getName());
    Assert.assertEquals(summary.getCustomAttribute(CUSTOM_1), repModel.getCustom1());
    Assert.assertEquals(summary.getCustomAttribute(CUSTOM_2), repModel.getCustom2());
    Assert.assertEquals(summary.getCustomAttribute(CUSTOM_3), repModel.getCustom3());
    Assert.assertEquals(summary.getCustomAttribute(CUSTOM_4), repModel.getCustom4());
    Assert.assertEquals(summary.getOrgLevel1(), repModel.getOrgLevel1());
    Assert.assertEquals(summary.getOrgLevel2(), repModel.getOrgLevel2());
    Assert.assertEquals(summary.getOrgLevel3(), repModel.getOrgLevel3());
    Assert.assertEquals(summary.getOrgLevel4(), repModel.getOrgLevel4());
    Assert.assertEquals(summary.getOwner(), repModel.getOwner());
    Assert.assertEquals(summary.getType(), repModel.getType());
  }

  private void testLinks(WorkbasketSummaryRepresentationModel repModel) {}
}
