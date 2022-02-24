package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_1;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_2;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_3;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_4;

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
    assertThat(summary.getDescription()).isEqualTo(repModel.getDescription());
    assertThat(summary.getDomain()).isEqualTo(repModel.getDomain());
    assertThat(summary.getId()).isEqualTo(repModel.getWorkbasketId());
    assertThat(summary.getKey()).isEqualTo(repModel.getKey());
    assertThat(summary.getName()).isEqualTo(repModel.getName());
    assertThat(summary.getCustomField(CUSTOM_1)).isEqualTo(repModel.getCustom1());
    assertThat(summary.getCustomField(CUSTOM_2)).isEqualTo(repModel.getCustom2());
    assertThat(summary.getCustomField(CUSTOM_3)).isEqualTo(repModel.getCustom3());
    assertThat(summary.getCustomField(CUSTOM_4)).isEqualTo(repModel.getCustom4());
    assertThat(summary.getOrgLevel1()).isEqualTo(repModel.getOrgLevel1());
    assertThat(summary.getOrgLevel2()).isEqualTo(repModel.getOrgLevel2());
    assertThat(summary.getOrgLevel3()).isEqualTo(repModel.getOrgLevel3());
    assertThat(summary.getOrgLevel4()).isEqualTo(repModel.getOrgLevel4());
    assertThat(summary.getOwner()).isEqualTo(repModel.getOwner());
    assertThat(summary.getType()).isEqualTo(repModel.getType());
  }

  private void testLinks(WorkbasketSummaryRepresentationModel repModel) {}
}
