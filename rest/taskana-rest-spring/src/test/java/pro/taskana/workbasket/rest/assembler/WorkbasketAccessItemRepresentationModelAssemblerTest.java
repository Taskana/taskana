package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;

/** Test for {@link WorkbasketAccessItemRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class WorkbasketAccessItemRepresentationModelAssemblerTest {

  @Autowired
  WorkbasketAccessItemRepresentationModelAssembler workbasketAccessItemRepresentationModelAssembler;

  @Autowired WorkbasketService workbasketService;

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    // given
    WorkbasketAccessItemImpl accessItem
        = (WorkbasketAccessItemImpl) workbasketService.newWorkbasketAccessItem("1", "2");
    accessItem.setWorkbasketKey("workbasketKey");
    accessItem.setPermDistribute(false);
    accessItem.setPermOpen(true);
    accessItem.setPermAppend(false);
    accessItem.setPermRead(false);
    accessItem.setPermTransfer(true);
    accessItem.setPermCustom1(false);
    accessItem.setPermCustom2(false);
    accessItem.setPermCustom3(true);
    accessItem.setPermCustom4(true);
    accessItem.setPermCustom5(true);
    accessItem.setPermCustom6(true);
    accessItem.setPermCustom7(true);
    accessItem.setPermCustom8(true);
    accessItem.setPermCustom9(true);
    accessItem.setPermCustom10(true);
    accessItem.setPermCustom11(true);
    accessItem.setPermCustom12(true);
    // when
    WorkbasketAccessItemRepresentationModel repModel =
        workbasketAccessItemRepresentationModelAssembler.toModel(accessItem);
    // then
    testEqualityAfterConversion(accessItem, repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    // given
    WorkbasketAccessItemRepresentationModel repModel =
        new WorkbasketAccessItemRepresentationModel();
    repModel.setAccessId("10");
    repModel.setWorkbasketKey("workbasketKey");
    repModel.setAccessItemId("120");
    repModel.setWorkbasketId("1");
    repModel.setPermRead(true);
    repModel.setPermAppend(false);
    repModel.setPermDistribute(false);
    repModel.setPermOpen(false);
    repModel.setPermTransfer(true);
    repModel.setPermCustom1(false);
    repModel.setPermCustom2(false);
    repModel.setPermCustom3(false);
    repModel.setPermCustom4(false);
    repModel.setPermCustom5(true);
    repModel.setPermCustom6(false);
    repModel.setPermCustom7(false);
    repModel.setPermCustom8(false);
    repModel.setPermCustom9(false);
    repModel.setPermCustom10(false);
    repModel.setPermCustom11(true);
    repModel.setPermCustom12(false);
    // when
    WorkbasketAccessItem accessItem =
        workbasketAccessItemRepresentationModelAssembler.toEntityModel(repModel);
    // then
    testEqualityAfterConversion(accessItem, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    WorkbasketAccessItemImpl accessItem
        = (WorkbasketAccessItemImpl) workbasketService.newWorkbasketAccessItem("1", "2");
    accessItem.setWorkbasketKey("workbasketKey");
    accessItem.setPermDistribute(false);
    accessItem.setPermOpen(true);
    accessItem.setPermAppend(false);
    accessItem.setPermRead(false);
    accessItem.setPermTransfer(true);
    accessItem.setPermCustom1(false);
    accessItem.setPermCustom2(false);
    accessItem.setPermCustom3(true);
    accessItem.setPermCustom4(true);
    accessItem.setPermCustom5(true);
    accessItem.setPermCustom6(true);
    accessItem.setPermCustom7(true);
    accessItem.setPermCustom8(true);
    accessItem.setPermCustom9(true);
    accessItem.setPermCustom10(true);
    accessItem.setPermCustom11(true);
    accessItem.setPermCustom12(true);
    // when
    WorkbasketAccessItemRepresentationModel repModel =
        workbasketAccessItemRepresentationModelAssembler.toModel(accessItem);
    WorkbasketAccessItemImpl accessItem2
        = (WorkbasketAccessItemImpl) workbasketAccessItemRepresentationModelAssembler
                                         .toEntityModel(repModel);
    //then
    testEqualityOfEntities(accessItem, accessItem2);
  }

  private void testEqualityAfterConversion(
      WorkbasketAccessItem accessItem, WorkbasketAccessItemRepresentationModel repModel) {
    assertThat(repModel.getAccessId()).isEqualTo(accessItem.getAccessId());
    assertThat(repModel.getWorkbasketKey()).isEqualTo(accessItem.getWorkbasketKey());
    assertThat(repModel.getAccessItemId()).isEqualTo(accessItem.getId());
    assertThat(repModel.getWorkbasketId()).isEqualTo(accessItem.getWorkbasketId());
    assertThat(repModel.isPermAppend()).isEqualTo(accessItem.isPermAppend());
    assertThat(repModel.isPermCustom1()).isEqualTo(accessItem.isPermCustom1());
    assertThat(repModel.isPermCustom2()).isEqualTo(accessItem.isPermCustom2());
    assertThat(repModel.isPermCustom3()).isEqualTo(accessItem.isPermCustom3());
    assertThat(repModel.isPermCustom4()).isEqualTo(accessItem.isPermCustom4());
    assertThat(repModel.isPermCustom5()).isEqualTo(accessItem.isPermCustom5());
    assertThat(repModel.isPermCustom6()).isEqualTo(accessItem.isPermCustom6());
    assertThat(repModel.isPermCustom7()).isEqualTo(accessItem.isPermCustom7());
    assertThat(repModel.isPermCustom8()).isEqualTo(accessItem.isPermCustom8());
    assertThat(repModel.isPermCustom9()).isEqualTo(accessItem.isPermCustom9());
    assertThat(repModel.isPermCustom10()).isEqualTo(accessItem.isPermCustom10());
    assertThat(repModel.isPermCustom11()).isEqualTo(accessItem.isPermCustom11());
    assertThat(repModel.isPermCustom12()).isEqualTo(accessItem.isPermCustom12());
    assertThat(repModel.isPermDistribute()).isEqualTo(accessItem.isPermDistribute());
    assertThat(repModel.isPermRead()).isEqualTo(accessItem.isPermRead());
    assertThat(repModel.isPermOpen()).isEqualTo(accessItem.isPermOpen());
    assertThat(repModel.isPermTransfer()).isEqualTo(accessItem.isPermTransfer());
  }

  private void testEqualityOfEntities(WorkbasketAccessItem item1, WorkbasketAccessItem items2) {
    assertThat(item1.getAccessId()).isEqualTo(items2.getAccessId());
    assertThat(item1.getWorkbasketKey()).isEqualTo(items2.getWorkbasketKey());
    assertThat(item1.getId()).isEqualTo(items2.getId());
    assertThat(item1.getWorkbasketId()).isEqualTo(items2.getWorkbasketId());
    assertThat(item1.isPermAppend()).isEqualTo(items2.isPermAppend());
    assertThat(item1.isPermCustom1()).isEqualTo(items2.isPermCustom1());
    assertThat(item1.isPermCustom2()).isEqualTo(items2.isPermCustom2());
    assertThat(item1.isPermCustom3()).isEqualTo(items2.isPermCustom3());
    assertThat(item1.isPermCustom4()).isEqualTo(items2.isPermCustom4());
    assertThat(item1.isPermCustom5()).isEqualTo(items2.isPermCustom5());
    assertThat(item1.isPermCustom6()).isEqualTo(items2.isPermCustom6());
    assertThat(item1.isPermCustom7()).isEqualTo(items2.isPermCustom7());
    assertThat(item1.isPermCustom8()).isEqualTo(items2.isPermCustom8());
    assertThat(item1.isPermCustom9()).isEqualTo(items2.isPermCustom9());
    assertThat(item1.isPermCustom10()).isEqualTo(items2.isPermCustom10());
    assertThat(item1.isPermCustom11()).isEqualTo(items2.isPermCustom11());
    assertThat(item1.isPermCustom12()).isEqualTo(items2.isPermCustom12());
    assertThat(item1.isPermDistribute()).isEqualTo(items2.isPermDistribute());
    assertThat(item1.isPermRead()).isEqualTo(items2.isPermRead());
    assertThat(item1.isPermOpen()).isEqualTo(items2.isPermOpen());
    assertThat(item1.isPermTransfer()).isEqualTo(items2.isPermTransfer());
  }

  private void testLinks() {
  }
}
