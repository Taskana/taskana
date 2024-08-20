package io.kadai.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import io.kadai.workbasket.rest.models.WorkbasketRepresentationModel;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Test for {@link WorkbasketRepresentationModelAssembler}. */
@KadaiSpringBootTest
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
    workbasket.setCustom5("5");
    workbasket.setCustom6("6");
    workbasket.setCustom7("7");
    workbasket.setCustom8("8");
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
    workbasketRepresentationModel.setCustom5("Custom5");
    workbasketRepresentationModel.setCustom6("Custom6");
    workbasketRepresentationModel.setCustom7("Custom7");
    workbasketRepresentationModel.setCustom8("Custom8");
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
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "1");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_2, "2");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_3, "3");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_4, "4");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_5, "5");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_6, "6");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_7, "7");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_8, "8");
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
        .endsWith(
            RestEndpoints.URL_WORKBASKET_ID.replaceAll("\\{.*}", workbasket.getWorkbasketId()));
    assertThat(workbasket.getRequiredLink("distributionTargets").getHref())
        .endsWith(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION.replaceAll(
                "\\{.*}", workbasket.getWorkbasketId()));
    assertThat(workbasket.getRequiredLink("allWorkbaskets").getHref())
        .endsWith(RestEndpoints.URL_WORKBASKET);
    assertThat(workbasket.getRequiredLink("removeDistributionTargets").getHref())
        .endsWith(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION.replaceAll(
                "\\{.*}", workbasket.getWorkbasketId()));
  }

  private void testEquality(Workbasket workbasket, WorkbasketRepresentationModel repModel) {
    WorkbasketSummaryRepresentationModelAssemblerTest.testEquality(workbasket, repModel);

    assertThat(repModel.getCreated()).isEqualTo(workbasket.getCreated());
    assertThat(repModel.getModified()).isEqualTo(workbasket.getModified());
  }
}
