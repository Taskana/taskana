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
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModelWithoutLinks;

/**
 * Test for {@link WorkbasketDefinitionRepresentationModelAssembler}.
 */
//TODO:Since the Definiton has no Entity to fully represent it
// there are still many parts of the DefinitionRepresentationModel
// not being tested. Add Tests to test all parts of the Definition.
//@ExtendWith(JaasExtension.class)
@TaskanaSpringBootTest
class WorkbasketDefinitionRepresentationModelAssemblerTest {

  private final WorkbasketService workbasketService;
  private final WorkbasketDefinitionRepresentationModelAssembler assembler;
  private final WorkbasketRepresentationModelAssembler tempAssembler;

  @Autowired
  WorkbasketDefinitionRepresentationModelAssemblerTest(
      WorkbasketService workbasketService,
      WorkbasketDefinitionRepresentationModelAssembler assembler,
      WorkbasketRepresentationModelAssembler tempAssembler) {
    this.workbasketService = workbasketService;
    this.assembler = assembler;
    this.tempAssembler = tempAssembler;
  }

  /*  @WithAccessId(
        user = "teamlead_1",
        groups = {"group_1", "businessadmin"})*/
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

    WorkbasketRepresentationModel repModel = tempAssembler.toModel(workbasket);
    testEqualityOfWorkbasketsAfterConversion(workbasket, repModel);
  }

  @Test
  void should_EqualWorkbasketInDefinition_When_ConvertingRepresentationModelToEntity() {
    WorkbasketRepresentationModel repModel
        = new WorkbasketRepresentationModel();
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
    //repModel.setWorkbasket(basket);

    WorkbasketImpl workbasket = (WorkbasketImpl) tempAssembler.toEntityModel(repModel);

    testEqualityOfWorkbasketsAfterConversion(workbasket, repModel);
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

    WorkbasketRepresentationModel repModel = tempAssembler.toModel(workbasket);
    WorkbasketImpl workbasket2 = (WorkbasketImpl) tempAssembler.toEntityModel(repModel);

    testEqualityOfEntities(workbasket, workbasket2);
  }

  private void testEqualityOfWorkbasketsAfterConversion(Workbasket workbasket,
      WorkbasketRepresentationModel repModel) {
    assertThat(repModel.getWorkbasketId()).isEqualTo(workbasket.getId());
    assertThat(repModel.getKey()).isEqualTo(workbasket.getKey());
    assertThat(repModel.getCreated()).isEqualTo(workbasket.getCreated());
    assertThat(repModel.getModified()).isEqualTo(workbasket.getModified());
    assertThat(repModel.getName()).isEqualTo(workbasket.getName());
    assertThat(repModel.getDescription()).isEqualTo(workbasket.getDescription());
    assertThat(repModel.getOwner()).isEqualTo(workbasket.getOwner());
    assertThat(repModel.getDomain()).isEqualTo(workbasket.getDomain());
    assertThat(repModel.getType()).isEqualTo(workbasket.getType());
    assertThat(repModel.getCustom1()).isEqualTo(workbasket.getCustom1());
    assertThat(repModel.getCustom2()).isEqualTo(workbasket.getCustom2());
    assertThat(repModel.getCustom3()).isEqualTo(workbasket.getCustom3());
    assertThat(repModel.getCustom4()).isEqualTo(workbasket.getCustom4());
    assertThat(repModel.getOrgLevel1()).isEqualTo(workbasket.getOrgLevel1());
    assertThat(repModel.getOrgLevel2()).isEqualTo(workbasket.getOrgLevel2());
    assertThat(repModel.getOrgLevel3()).isEqualTo(workbasket.getOrgLevel3());
    assertThat(repModel.getOrgLevel4()).isEqualTo(workbasket.getOrgLevel4());
  }

  private void testEqualityOfEntities(Workbasket workbasket, Workbasket workbasket2) {
    assertThat(workbasket2.getId()).isEqualTo(workbasket.getId());
    assertThat(workbasket2.getKey()).isEqualTo(workbasket.getKey());
    assertThat(workbasket2.getCreated()).isEqualTo(workbasket.getCreated());
    assertThat(workbasket2.getModified()).isEqualTo(workbasket.getModified());
    assertThat(workbasket2.getName()).isEqualTo(workbasket.getName());
    assertThat(workbasket2.getDescription()).isEqualTo(workbasket.getDescription());
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

}
