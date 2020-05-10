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

/** Test for {@link WorkbasketResourceAssembler}. */
@TaskanaSpringBootTest
class WorkbasketResourceAssemblerTest {

  @Autowired WorkbasketService workbasketService;
  @Autowired WorkbasketResourceAssembler workbasketResourceAssembler;

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
    WorkbasketResource resource = workbasketResourceAssembler.toModel(workbasket);
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

  private void testEquality(Workbasket workbasket, WorkbasketResource workbasketResource) {
    assertThat(workbasketResource.getWorkbasketId()).isEqualTo(workbasket.getId());
    assertThat(workbasketResource.getKey()).isEqualTo(workbasket.getKey());
    assertThat(workbasketResource.getCreated())
        .isEqualTo(workbasket.getCreated() == null ? null : workbasket.getCreated().toString());
    assertThat(workbasketResource.getModified())
        .isEqualTo(workbasket.getModified() == null ? null : workbasket.getModified().toString());
    assertThat(workbasketResource.getName()).isEqualTo(workbasket.getName());
    assertThat(workbasketResource.getDescription()).isEqualTo(workbasket.getDescription());
    assertThat(workbasketResource.getOwner()).isEqualTo(workbasket.getOwner());
    assertThat(workbasketResource.getDomain()).isEqualTo(workbasket.getDomain());
    assertThat(workbasketResource.getType()).isEqualTo(workbasket.getType());
    assertThat(workbasketResource.getCustom1()).isEqualTo(workbasket.getCustom1());
    assertThat(workbasketResource.getCustom2()).isEqualTo(workbasket.getCustom2());
    assertThat(workbasketResource.getCustom3()).isEqualTo(workbasket.getCustom3());
    assertThat(workbasketResource.getCustom4()).isEqualTo(workbasket.getCustom4());
    assertThat(workbasketResource.getOrgLevel1()).isEqualTo(workbasket.getOrgLevel1());
    assertThat(workbasketResource.getOrgLevel2()).isEqualTo(workbasket.getOrgLevel2());
    assertThat(workbasketResource.getOrgLevel3()).isEqualTo(workbasket.getOrgLevel3());
    assertThat(workbasketResource.getOrgLevel4()).isEqualTo(workbasket.getOrgLevel4());
  }
}
