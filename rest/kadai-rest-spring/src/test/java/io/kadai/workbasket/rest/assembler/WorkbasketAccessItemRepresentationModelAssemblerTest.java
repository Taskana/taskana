package io.kadai.workbasket.rest.assembler;

import static io.kadai.workbasket.api.WorkbasketPermission.APPEND;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_1;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_10;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_11;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_12;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_2;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_3;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_4;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_5;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_6;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_7;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_8;
import static io.kadai.workbasket.api.WorkbasketPermission.CUSTOM_9;
import static io.kadai.workbasket.api.WorkbasketPermission.DISTRIBUTE;
import static io.kadai.workbasket.api.WorkbasketPermission.EDITTASKS;
import static io.kadai.workbasket.api.WorkbasketPermission.OPEN;
import static io.kadai.workbasket.api.WorkbasketPermission.READ;
import static io.kadai.workbasket.api.WorkbasketPermission.READTASKS;
import static io.kadai.workbasket.api.WorkbasketPermission.TRANSFER;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import io.kadai.workbasket.internal.models.WorkbasketAccessItemImpl;
import io.kadai.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Test for {@link WorkbasketAccessItemRepresentationModelAssembler}. */
@KadaiSpringBootTest
class WorkbasketAccessItemRepresentationModelAssemblerTest {

  private final WorkbasketAccessItemRepresentationModelAssembler assembler;
  private final WorkbasketService workbasketService;

  @Autowired
  WorkbasketAccessItemRepresentationModelAssemblerTest(
      WorkbasketAccessItemRepresentationModelAssembler assembler,
      WorkbasketService workbasketService) {
    this.assembler = assembler;
    this.workbasketService = workbasketService;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    // given
    WorkbasketAccessItemImpl accessItem =
        (WorkbasketAccessItemImpl) workbasketService.newWorkbasketAccessItem("1", "2");
    accessItem.setId("id");
    accessItem.setAccessName("accessName");
    accessItem.setWorkbasketKey("workbasketKey");
    accessItem.setPermission(READ, false);
    accessItem.setPermission(READTASKS, false);
    accessItem.setPermission(OPEN, true);
    accessItem.setPermission(APPEND, false);
    accessItem.setPermission(EDITTASKS, false);
    accessItem.setPermission(DISTRIBUTE, false);
    accessItem.setPermission(TRANSFER, true);
    accessItem.setPermission(CUSTOM_1, false);
    accessItem.setPermission(CUSTOM_2, false);
    accessItem.setPermission(CUSTOM_3, true);
    accessItem.setPermission(CUSTOM_4, true);
    accessItem.setPermission(CUSTOM_5, true);
    accessItem.setPermission(CUSTOM_6, true);
    accessItem.setPermission(CUSTOM_7, true);
    accessItem.setPermission(CUSTOM_8, true);
    accessItem.setPermission(CUSTOM_9, true);
    accessItem.setPermission(CUSTOM_10, true);
    accessItem.setPermission(CUSTOM_11, true);
    accessItem.setPermission(CUSTOM_12, true);
    // when
    WorkbasketAccessItemRepresentationModel repModel = assembler.toModel(accessItem);
    // then
    testEquality(accessItem, repModel);
    testLinks(repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    WorkbasketAccessItemImpl accessItem =
        (WorkbasketAccessItemImpl) workbasketService.newWorkbasketAccessItem("1", "2");
    accessItem.setId("accessItemId");
    accessItem.setWorkbasketKey("workbasketKey");
    accessItem.setAccessName("accessName");
    accessItem.setPermission(OPEN, true);
    accessItem.setPermission(READ, false);
    accessItem.setPermission(READTASKS, false);
    accessItem.setPermission(APPEND, false);
    accessItem.setPermission(EDITTASKS, false);
    accessItem.setPermission(TRANSFER, true);
    accessItem.setPermission(DISTRIBUTE, false);
    accessItem.setPermission(CUSTOM_1, false);
    accessItem.setPermission(CUSTOM_2, false);
    accessItem.setPermission(CUSTOM_3, true);
    accessItem.setPermission(CUSTOM_4, true);
    accessItem.setPermission(CUSTOM_5, true);
    accessItem.setPermission(CUSTOM_6, true);
    accessItem.setPermission(CUSTOM_7, true);
    accessItem.setPermission(CUSTOM_8, true);
    accessItem.setPermission(CUSTOM_9, true);
    accessItem.setPermission(CUSTOM_10, true);
    accessItem.setPermission(CUSTOM_11, true);
    accessItem.setPermission(CUSTOM_12, true);
    // when
    WorkbasketAccessItemRepresentationModel repModel = assembler.toModel(accessItem);
    WorkbasketAccessItem accessItem2 = assembler.toEntityModel(repModel);
    // then
    assertThat(accessItem)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(accessItem2)
        .isEqualTo(accessItem2);
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
    repModel.setAccessName("accessName");
    repModel.setPermRead(true);
    repModel.setPermReadTasks(true);
    repModel.setPermAppend(false);
    repModel.setPermEditTasks(true);
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
    WorkbasketAccessItem accessItem = assembler.toEntityModel(repModel);
    // then
    testEquality(accessItem, repModel);
  }

  private void testEquality(
      WorkbasketAccessItem accessItem, WorkbasketAccessItemRepresentationModel repModel) {
    assertThat(accessItem).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();
    assertThat(repModel.getAccessItemId()).isEqualTo(accessItem.getId());
    assertThat(repModel.getWorkbasketId()).isEqualTo(accessItem.getWorkbasketId());
    assertThat(repModel.getWorkbasketKey()).isEqualTo(accessItem.getWorkbasketKey());
    assertThat(repModel.getAccessId()).isEqualTo(accessItem.getAccessId());
    assertThat(repModel.getAccessName()).isEqualTo(accessItem.getAccessName());
    assertThat(repModel.isPermRead()).isEqualTo(accessItem.getPermission(READ));
    assertThat(repModel.isPermReadTasks()).isEqualTo(accessItem.getPermission(READTASKS));
    assertThat(repModel.isPermOpen()).isEqualTo(accessItem.getPermission(OPEN));
    assertThat(repModel.isPermAppend()).isEqualTo(accessItem.getPermission(APPEND));
    assertThat(repModel.isPermEditTasks()).isEqualTo(accessItem.getPermission(EDITTASKS));
    assertThat(repModel.isPermTransfer()).isEqualTo(accessItem.getPermission(TRANSFER));
    assertThat(repModel.isPermDistribute()).isEqualTo(accessItem.getPermission(DISTRIBUTE));
    assertThat(repModel.isPermCustom1()).isEqualTo(accessItem.getPermission(CUSTOM_1));
    assertThat(repModel.isPermCustom2()).isEqualTo(accessItem.getPermission(CUSTOM_2));
    assertThat(repModel.isPermCustom3()).isEqualTo(accessItem.getPermission(CUSTOM_3));
    assertThat(repModel.isPermCustom4()).isEqualTo(accessItem.getPermission(CUSTOM_4));
    assertThat(repModel.isPermCustom5()).isEqualTo(accessItem.getPermission(CUSTOM_5));
    assertThat(repModel.isPermCustom6()).isEqualTo(accessItem.getPermission(CUSTOM_6));
    assertThat(repModel.isPermCustom7()).isEqualTo(accessItem.getPermission(CUSTOM_7));
    assertThat(repModel.isPermCustom8()).isEqualTo(accessItem.getPermission(CUSTOM_8));
    assertThat(repModel.isPermCustom9()).isEqualTo(accessItem.getPermission(CUSTOM_9));
    assertThat(repModel.isPermCustom10()).isEqualTo(accessItem.getPermission(CUSTOM_10));
    assertThat(repModel.isPermCustom11()).isEqualTo(accessItem.getPermission(CUSTOM_11));
    assertThat(repModel.isPermCustom12()).isEqualTo(accessItem.getPermission(CUSTOM_12));
  }

  private void testLinks(WorkbasketAccessItemRepresentationModel repModel) {}
}
