package pro.taskana.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.Mapping;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/** Test for {@link WorkbasketRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class WorkbasketRepresentationModelAssemblerTest {

  @Autowired WorkbasketService workbasketService;
  @Autowired WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler;

  @Test
  void workbasketToResource() {
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
    WorkbasketRepresentationModel resource =
        workbasketRepresentationModelAssembler.toModel(workbasket);
    // then
    testEquality(workbasket, resource);
    verifyLinks(resource);
  }

  @Test
  void resourceWithoutCreated() {
    // given
    WorkbasketRepresentationModel resource = new WorkbasketRepresentationModel();
    resource.setWorkbasketId("1");
    resource.setModified("2010-01-01T12:00:00Z");
    resource.setType(WorkbasketType.PERSONAL);
    // when
    Workbasket workbasket = workbasketRepresentationModelAssembler.toEntityModel(resource);
    // then
    testEquality(workbasket, resource);
  }

  @Test
  void resourceWithoutModified() {
    // given
    WorkbasketRepresentationModel resource = new WorkbasketRepresentationModel();
    resource.setWorkbasketId("1");
    resource.setCreated("2010-01-01T12:00:00Z");
    resource.setType(WorkbasketType.PERSONAL);
    // when
    Workbasket workbasket = workbasketRepresentationModelAssembler.toEntityModel(resource);
    // then
    testEquality(workbasket, resource);
  }

  @Test
  void resourceToWorkbasket() {
    // given
    WorkbasketRepresentationModel workbasketRepresentationModel =
        new WorkbasketRepresentationModel();
    workbasketRepresentationModel.setWorkbasketId("1");
    workbasketRepresentationModel.setCreated("2010-01-01T12:00:00Z");
    workbasketRepresentationModel.setModified("2010-01-01T12:00:00Z");
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

  private void verifyLinks(WorkbasketRepresentationModel workbasket) {
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

  private void testEquality(
      Workbasket workbasket, WorkbasketRepresentationModel workbasketRepresentationModel) {
    assertThat(workbasketRepresentationModel.getWorkbasketId()).isEqualTo(workbasket.getId());
    assertThat(workbasketRepresentationModel.getKey()).isEqualTo(workbasket.getKey());
    assertThat(workbasketRepresentationModel.getCreated())
        .isEqualTo(workbasket.getCreated() == null ? null : workbasket.getCreated().toString());
    assertThat(workbasketRepresentationModel.getModified())
        .isEqualTo(workbasket.getModified() == null ? null : workbasket.getModified().toString());
    assertThat(workbasketRepresentationModel.getName()).isEqualTo(workbasket.getName());
    assertThat(workbasketRepresentationModel.getDescription())
        .isEqualTo(workbasket.getDescription());
    assertThat(workbasketRepresentationModel.getOwner()).isEqualTo(workbasket.getOwner());
    assertThat(workbasketRepresentationModel.getDomain()).isEqualTo(workbasket.getDomain());
    assertThat(workbasketRepresentationModel.getType()).isEqualTo(workbasket.getType());
    assertThat(workbasketRepresentationModel.getCustom1()).isEqualTo(workbasket.getCustom1());
    assertThat(workbasketRepresentationModel.getCustom2()).isEqualTo(workbasket.getCustom2());
    assertThat(workbasketRepresentationModel.getCustom3()).isEqualTo(workbasket.getCustom3());
    assertThat(workbasketRepresentationModel.getCustom4()).isEqualTo(workbasket.getCustom4());
    assertThat(workbasketRepresentationModel.getOrgLevel1()).isEqualTo(workbasket.getOrgLevel1());
    assertThat(workbasketRepresentationModel.getOrgLevel2()).isEqualTo(workbasket.getOrgLevel2());
    assertThat(workbasketRepresentationModel.getOrgLevel3()).isEqualTo(workbasket.getOrgLevel3());
    assertThat(workbasketRepresentationModel.getOrgLevel4()).isEqualTo(workbasket.getOrgLevel4());
  }
}
