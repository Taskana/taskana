package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.workbasket.api.WorkbasketType.PERSONAL;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModelWithoutLinks;

/**
 * Test for {@link WorkbasketDefinitionRepresentationModelAssembler}.
 */
@TaskanaSpringBootTest
class WorkbasketDefinitionRepresentationModelAssemblerTest {

  private final WorkbasketService workbasketService;
  private final WorkbasketDefinitionRepresentationModelAssembler assembler;

  @Autowired
  WorkbasketDefinitionRepresentationModelAssemblerTest(
      WorkbasketService workbasketService,
      WorkbasketDefinitionRepresentationModelAssembler assembler) {
    this.workbasketService = workbasketService;
    this.assembler = assembler;
  }

  @Test
  void should_EqualWorkbasketEntity_When_ConvertingEntityToRepresentationModel() {
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("1", "DOMAIN_A");
    workbasket.setId("1");
    workbasket.setName("name");
    workbasket.setType(PERSONAL);
    workbasket.setDescription("description");
    workbasket.setOwner("owner");
    workbasket.setMarkedForDeletion(false);
    workbasket.setCustom1("1");
    workbasket.setCustom2("2");
    workbasket.setCustom3("3");
    workbasket.setCustom4("4");
    workbasket.setOrgLevel1("1");
    workbasket.setOrgLevel2("2");
    workbasket.setOrgLevel3("3");
    workbasket.setOrgLevel4("4");
    workbasket.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    workbasket.setModified(Instant.parse("2010-01-01T12:00:00Z"));
    workbasketService.newWorkbasketAccessItem("1", "1");

    WorkbasketDefinitionRepresentationModel repModel = assembler.toModel(workbasket);
    testEquality(workbasket, repModel);
  }

  @Test
  void should_EqualWorkbasketInDefinition_When_ConvertingRepresentationModelToEntity() {
    WorkbasketDefinitionRepresentationModel repModel
        = new WorkbasketDefinitionRepresentationModel();
    WorkbasketRepresentationModelWithoutLinks basket =
        new WorkbasketRepresentationModelWithoutLinks();
    basket.setKey("1");
    basket.setModified(Instant.parse("2010-01-01T12:00:00Z"));
    basket.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    basket.setWorkbasketId("1");
    basket.setDescription("description");
    basket.setDomain("DOMAIN_A");
    basket.setName("name");
    basket.setType(PERSONAL);
    basket.setOwner("owner");
    basket.setCustom1("1");
    basket.setCustom2("2");
    basket.setCustom3("3");
    basket.setCustom4("4");
    basket.setOrgLevel1("1");
    basket.setOrgLevel2("2");
    basket.setOrgLevel3("3");
    basket.setOrgLevel4("4");
    repModel.setWorkbasket(basket);

    Workbasket workbasket = assembler.toEntityModel(repModel.getWorkbasket());

    testEquality(workbasket, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("1", "DOMAIN_A");
    workbasket.setId("1");
    workbasket.setName("name");
    workbasket.setType(PERSONAL);
    workbasket.setDescription("description");
    workbasket.setOwner("owner");
    workbasket.setMarkedForDeletion(false);
    workbasket.setCustom1("1");
    workbasket.setCustom2("2");
    workbasket.setCustom3("3");
    workbasket.setCustom4("4");
    workbasket.setOrgLevel1("1");
    workbasket.setOrgLevel2("2");
    workbasket.setOrgLevel3("3");
    workbasket.setOrgLevel4("4");
    workbasket.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    workbasket.setModified(Instant.parse("2010-01-01T12:00:00Z"));
    workbasketService.newWorkbasketAccessItem("1", "1");

    WorkbasketDefinitionRepresentationModel repModel = assembler.toModel(workbasket);
    Workbasket workbasket2 = assembler.toEntityModel(repModel.getWorkbasket());

    assertThat(workbasket).isNotSameAs(workbasket2).isEqualTo(workbasket2);
  }

  private void testEquality(Workbasket workbasket,
      WorkbasketDefinitionRepresentationModel repModel) {
    assertThat(repModel.getWorkbasket().getWorkbasketId()).isEqualTo(workbasket.getId());
    assertThat(repModel.getWorkbasket().getKey()).isEqualTo(workbasket.getKey());
    assertThat(repModel.getWorkbasket().getCreated()).isEqualTo(workbasket.getCreated());
    assertThat(repModel.getWorkbasket().getModified()).isEqualTo(workbasket.getModified());
    assertThat(repModel.getWorkbasket().getName()).isEqualTo(workbasket.getName());
    assertThat(repModel.getWorkbasket().getDescription()).isEqualTo(workbasket.getDescription());
    assertThat(repModel.getWorkbasket().getOwner()).isEqualTo(workbasket.getOwner());
    assertThat(repModel.getWorkbasket().getDomain()).isEqualTo(workbasket.getDomain());
    assertThat(repModel.getWorkbasket().getType()).isEqualTo(workbasket.getType());
    assertThat(repModel.getWorkbasket().getCustom1()).isEqualTo(workbasket.getCustom1());
    assertThat(repModel.getWorkbasket().getCustom2()).isEqualTo(workbasket.getCustom2());
    assertThat(repModel.getWorkbasket().getCustom3()).isEqualTo(workbasket.getCustom3());
    assertThat(repModel.getWorkbasket().getCustom4()).isEqualTo(workbasket.getCustom4());
    assertThat(repModel.getWorkbasket().getOrgLevel1()).isEqualTo(workbasket.getOrgLevel1());
    assertThat(repModel.getWorkbasket().getOrgLevel2()).isEqualTo(workbasket.getOrgLevel2());
    assertThat(repModel.getWorkbasket().getOrgLevel3()).isEqualTo(workbasket.getOrgLevel3());
    assertThat(repModel.getWorkbasket().getOrgLevel4()).isEqualTo(workbasket.getOrgLevel4());
  }
}
