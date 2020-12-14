package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;

@TaskanaSpringBootTest
class ObjectReferenceRepresentationModelAssemblerTest {

  private final ObjectReferenceRepresentationModelAssembler assembler;

  @Autowired
  ObjectReferenceRepresentationModelAssemblerTest(
      ObjectReferenceRepresentationModelAssembler assembler) {
    this.assembler = assembler;
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    ObjectReferenceRepresentationModel repModel = new ObjectReferenceRepresentationModel();
    repModel.setId("id");
    repModel.setValue("value");
    repModel.setType("type");
    repModel.setSystem("system");
    repModel.setSystemInstance("instance");
    repModel.setCompany("company");

    ObjectReference objectReference = assembler.toEntity(repModel);

    testEquality(objectReference, repModel);
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    ObjectReference entity = new ObjectReference();
    entity.setId("id");
    entity.setValue("value");
    entity.setType("type");
    entity.setSystem("system");
    entity.setSystemInstance("instance");
    entity.setCompany("company");

    ObjectReferenceRepresentationModel representationModel = assembler.toModel(entity);

    testEquality(entity, representationModel);
  }

  static void testEquality(
      ObjectReference objectReference, ObjectReferenceRepresentationModel repModel) {
    assertThat(objectReference).isNotNull();
    assertThat(objectReference.getId()).isEqualTo(repModel.getId());
    assertThat(objectReference.getCompany()).isEqualTo(repModel.getCompany());
    assertThat(objectReference.getSystem()).isEqualTo(repModel.getSystem());
    assertThat(objectReference.getSystemInstance()).isEqualTo(repModel.getSystemInstance());
    assertThat(objectReference.getType()).isEqualTo(repModel.getType());
    assertThat(objectReference.getValue()).isEqualTo(repModel.getValue());
  }
}
