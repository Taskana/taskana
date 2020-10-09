package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;

/** Test for {@link WorkbasketRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class WorkbasketRepresentationModelAssemblerTest {

  private final WorkbasketService workbasketService;
  private final WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler;

  @Autowired
  WorkbasketRepresentationModelAssemblerTest(
      WorkbasketService workbasketService,
      WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler) {
    this.workbasketService = workbasketService;
    this.workbasketRepresentationModelAssembler = workbasketRepresentationModelAssembler;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    // given
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("1", "DOMAIN_A");
    workbasket.setId("ID");
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
    workbasket.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    workbasket.setModified(Instant.parse("2010-01-01T12:00:00Z"));
    // when
    WorkbasketRepresentationModel repModel =
        workbasketRepresentationModelAssembler.toModel(workbasket);
    // then
    testEquality(workbasket, repModel);
    testLinks(repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    // given
    WorkbasketRepresentationModel workbasketRepresentationModel =
        new WorkbasketRepresentationModel();
    workbasketRepresentationModel.setWorkbasketId("1");
    workbasketRepresentationModel.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    workbasketRepresentationModel.setModified(Instant.parse("2010-01-01T12:00:00Z"));
    workbasketRepresentationModel.setCustom1("Custom1");
    workbasketRepresentationModel.setCustom2("Custom2");
    workbasketRepresentationModel.setCustom3("Custom3");
    workbasketRepresentationModel.setCustom4("Custom4");
    workbasketRepresentationModel.setDescription("Test Ressource");
    workbasketRepresentationModel.setDomain("DOMAIN_A");
    workbasketRepresentationModel.setKey("1");
    workbasketRepresentationModel.setName("Ressource");
    workbasketRepresentationModel.setOrgLevel1("Org1");
    workbasketRepresentationModel.setOrgLevel2("Org2");
    workbasketRepresentationModel.setOrgLevel3("Org3");
    workbasketRepresentationModel.setOrgLevel4("Org4");
    workbasketRepresentationModel.setOwner("Lars");
    workbasketRepresentationModel.setType(WorkbasketType.PERSONAL);
    // when
    Workbasket workbasket =
        workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
    // then
    testEquality(workbasket, workbasketRepresentationModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("1", "DOMAIN_A");
    workbasket.setId("ID");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket.setName("Testbasket");
    workbasket.setOrgLevel1("Org1");
    workbasket.setOrgLevel2("Org2");
    workbasket.setOrgLevel3("Org3");
    workbasket.setOrgLevel4("Org4");
    workbasket.setDescription("A test workbasket");
    workbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_1, "1");
    workbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_2, "2");
    workbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_3, "3");
    workbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_4, "4");
    workbasket.setOwner("Lars");
    workbasket.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    workbasket.setModified(Instant.parse("2010-01-01T12:00:00Z"));
    // when
    WorkbasketRepresentationModel repModel =
        workbasketRepresentationModelAssembler.toModel(workbasket);
    Workbasket workbasket2 = workbasketRepresentationModelAssembler.toEntityModel(repModel);

    assertThat(workbasket)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(workbasket2)
        .isEqualTo(workbasket2);
  }

  private void testLinks(WorkbasketRepresentationModel workbasket) {
    assertThat(workbasket.getLinks()).hasSize(5);
    assertThat(workbasket.getRequiredLink("self").getHref())
        .isEqualTo(Mapping.URL_WORKBASKET_ID.replaceAll("\\{.*}", workbasket.getWorkbasketId()));
    assertThat(workbasket.getRequiredLink("distributionTargets").getHref())
        .isEqualTo(
            Mapping.URL_WORKBASKET_ID_DISTRIBUTION.replaceAll(
                "\\{.*}", workbasket.getWorkbasketId()));
    assertThat(workbasket.getRequiredLink("allWorkbaskets").getHref())
        .isEqualTo(Mapping.URL_WORKBASKET);
    assertThat(workbasket.getRequiredLink("removeDistributionTargets").getHref())
        .isEqualTo(
            Mapping.URL_WORKBASKET_ID_DISTRIBUTION.replaceAll(
                "\\{.*}", workbasket.getWorkbasketId()));
  }

  private void testEquality(Workbasket workbasket, WorkbasketRepresentationModel repModel) {
    WorkbasketSummaryRepresentationModelAssemblerTest.testEquality(workbasket, repModel);

    assertThat(repModel.getCreated()).isEqualTo(workbasket.getCreated());
    assertThat(repModel.getModified()).isEqualTo(workbasket.getModified());
  }
}
