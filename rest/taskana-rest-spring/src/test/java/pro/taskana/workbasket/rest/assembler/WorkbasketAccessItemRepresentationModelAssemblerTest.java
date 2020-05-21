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
  void workBasketAccessItemToResourcePropertiesEqual() {
    // given
    WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "2");
    ((WorkbasketAccessItemImpl) accessItem).setWorkbasketKey("workbasketKey");
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
    WorkbasketAccessItemRepresentationModel resource =
        workbasketAccessItemRepresentationModelAssembler.toModel(accessItem);
    // then
    testEquality(accessItem, resource);
  }

  @Test
  void workBasketAccessItemToModelPropertiesEqual() {
    // given
    WorkbasketAccessItemRepresentationModel resource =
        new WorkbasketAccessItemRepresentationModel();
    resource.setAccessId("10");
    resource.setWorkbasketKey("workbasketKey");
    resource.setAccessItemId("120");
    resource.setWorkbasketId("1");
    resource.setPermRead(true);
    resource.setPermAppend(false);
    resource.setPermDistribute(false);
    resource.setPermOpen(false);
    resource.setPermTransfer(true);
    resource.setPermCustom1(false);
    resource.setPermCustom2(false);
    resource.setPermCustom3(false);
    resource.setPermCustom4(false);
    resource.setPermCustom5(true);
    resource.setPermCustom6(false);
    resource.setPermCustom7(false);
    resource.setPermCustom8(false);
    resource.setPermCustom9(false);
    resource.setPermCustom10(false);
    resource.setPermCustom11(true);
    resource.setPermCustom12(false);
    // when
    WorkbasketAccessItem accessItem =
        workbasketAccessItemRepresentationModelAssembler.toEntityModel(resource);
    // then
    testEquality(accessItem, resource);
  }

  private void testEquality(
      WorkbasketAccessItem accessItem, WorkbasketAccessItemRepresentationModel resource) {
    assertThat(resource.getAccessId()).isEqualTo(accessItem.getAccessId());
    assertThat(resource.getWorkbasketKey()).isEqualTo(accessItem.getWorkbasketKey());
    assertThat(resource.getAccessItemId()).isEqualTo(accessItem.getId());
    assertThat(resource.getWorkbasketId()).isEqualTo(accessItem.getWorkbasketId());
    assertThat(resource.isPermAppend()).isEqualTo(accessItem.isPermAppend());
    assertThat(resource.isPermCustom1()).isEqualTo(accessItem.isPermCustom1());
    assertThat(resource.isPermCustom2()).isEqualTo(accessItem.isPermCustom2());
    assertThat(resource.isPermCustom3()).isEqualTo(accessItem.isPermCustom3());
    assertThat(resource.isPermCustom4()).isEqualTo(accessItem.isPermCustom4());
    assertThat(resource.isPermCustom5()).isEqualTo(accessItem.isPermCustom5());
    assertThat(resource.isPermCustom6()).isEqualTo(accessItem.isPermCustom6());
    assertThat(resource.isPermCustom7()).isEqualTo(accessItem.isPermCustom7());
    assertThat(resource.isPermCustom8()).isEqualTo(accessItem.isPermCustom8());
    assertThat(resource.isPermCustom9()).isEqualTo(accessItem.isPermCustom9());
    assertThat(resource.isPermCustom10()).isEqualTo(accessItem.isPermCustom10());
    assertThat(resource.isPermCustom11()).isEqualTo(accessItem.isPermCustom11());
    assertThat(resource.isPermCustom12()).isEqualTo(accessItem.isPermCustom12());
    assertThat(resource.isPermDistribute()).isEqualTo(accessItem.isPermDistribute());
    assertThat(resource.isPermRead()).isEqualTo(accessItem.isPermRead());
    assertThat(resource.isPermOpen()).isEqualTo(accessItem.isPermOpen());
    assertThat(resource.isPermTransfer()).isEqualTo(accessItem.isPermTransfer());
  }
}
