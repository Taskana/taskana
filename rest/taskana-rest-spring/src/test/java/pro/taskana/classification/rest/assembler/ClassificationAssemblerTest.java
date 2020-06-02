package pro.taskana.classification.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.TaskanaSpringBootTest;

/** Test for {@link ClassificationRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class ClassificationAssemblerTest {

  @Autowired ClassificationRepresentationModelAssembler classificationRepresentationModelAssembler;

  @Autowired ClassificationService classificationService;

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    // given
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("KEY_A", "DOMAIN_A", "A");
    classification.setId("1");
    classification.setCategory("ABC");
    classification.setName("Classification 1");
    classification.setIsValidInDomain(true);
    classification.setCustom1("Custom1");
    classification.setCustom2("Custom2");
    classification.setCustom3("Custom3");
    classification.setCustom4("Custom4");
    classification.setCustom5("Custom5");
    classification.setCustom6("Custom6");
    classification.setCustom7("Custom7");
    classification.setCustom8("Custom8");
    classification.setParentId("2");
    classification.setParentKey("parentKey");
    classification.setPriority(2);
    classification.setApplicationEntryPoint("12");
    classification.setServiceLevel("P1D");
    classification.setDescription("Test");
    classification.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    classification.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    // when
    ClassificationRepresentationModel classificationRepresentationModel =
        classificationRepresentationModelAssembler.toModel(classification);
    // then
    testEqualityAfterConversion(classification, classificationRepresentationModel);
    testLinks(classificationRepresentationModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("KEY_B", "DOMAIN_B", "AB");

    // given
    classification.setId("1");
    classification.setApplicationEntryPoint("Test");
    classification.setCategory("ABC");
    classification.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    classification.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    classification.setCustom1("Custom1");
    classification.setCustom2("Custom2");
    classification.setCustom3("Custom3");
    classification.setCustom4("Custom4");
    classification.setCustom5("Custom5");
    classification.setCustom6("Custom6");
    classification.setCustom7("Custom7");
    classification.setCustom8("Custom8");
    classification.setParentId("2");
    classification.setParentKey("parentKey");
    classification.setPriority(2);
    classification.setApplicationEntryPoint("12");
    classification.setServiceLevel("P1D");
    classification.setDescription("Test");
    classification.setIsValidInDomain(true);

    ClassificationRepresentationModel classificationRepresentationModel =
        classificationRepresentationModelAssembler.toModel(classification);

    // when
    classification =
        (ClassificationImpl)
            classificationRepresentationModelAssembler
                .toEntityModel(classificationRepresentationModel);
    // then
    testEqualityAfterConversion(classification, classificationRepresentationModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("KEY_A", "DOMAIN_A", "A");
    classification.setId("1");
    classification.setCategory("ABC");
    classification.setName("Classification 1");
    classification.setIsValidInDomain(true);
    classification.setCustom1("Custom1");
    classification.setCustom2("Custom2");
    classification.setCustom3("Custom3");
    classification.setCustom4("Custom4");
    classification.setCustom5("Custom5");
    classification.setCustom6("Custom6");
    classification.setCustom7("Custom7");
    classification.setCustom8("Custom8");
    classification.setParentId("2");
    classification.setParentKey("parentKey");
    classification.setPriority(2);
    classification.setApplicationEntryPoint("12");
    classification.setServiceLevel("P1D");
    classification.setDescription("Test");
    classification.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    classification.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    // when
    ClassificationRepresentationModel classificationRepresentationModel =
        classificationRepresentationModelAssembler.toModel(classification);
    ClassificationImpl secondClassification
        = (ClassificationImpl) classificationRepresentationModelAssembler
                                   .toEntityModel(classificationRepresentationModel);
    // then
    testEqualityOfEntities(classification, secondClassification);
  }

  private void testLinks(ClassificationRepresentationModel repModel) {
    assertThat(repModel.getLinks()).hasSize(1);
    assertThat(Mapping.URL_CLASSIFICATIONS_ID.replaceAll("\\{.*}", repModel.getClassificationId()))
        .isEqualTo(repModel.getRequiredLink("self").getHref());
  }

  private void testEqualityAfterConversion(
      Classification entity,
      ClassificationRepresentationModel repModel) {
    assertThat(entity.getApplicationEntryPoint()).isEqualTo(repModel.getApplicationEntryPoint());
    assertThat(entity.getKey()).isEqualTo(repModel.getKey());
    assertThat(entity.getDomain()).isEqualTo(repModel.getDomain());
    assertThat(entity.getId()).isEqualTo(repModel.getClassificationId());
    assertThat(entity.getDescription()).isEqualTo(repModel.getDescription());
    assertThat(entity.getName()).isEqualTo(repModel.getName());
    assertThat(entity.getServiceLevel()).isEqualTo(repModel.getServiceLevel());
    assertThat(entity.getCategory()).isEqualTo(repModel.getCategory());
    assertThat(entity.getCustom1()).isEqualTo(repModel.getCustom1());
    assertThat(entity.getCustom2()).isEqualTo(repModel.getCustom2());
    assertThat(entity.getCustom3()).isEqualTo(repModel.getCustom3());
    assertThat(entity.getCustom4()).isEqualTo(repModel.getCustom4());
    assertThat(entity.getCustom5()).isEqualTo(repModel.getCustom5());
    assertThat(entity.getCustom6()).isEqualTo(repModel.getCustom6());
    assertThat(entity.getCustom7()).isEqualTo(repModel.getCustom7());
    assertThat(entity.getCustom8()).isEqualTo(repModel.getCustom8());
    assertThat(entity.getParentId()).isEqualTo(repModel.getParentId());
    assertThat(entity.getParentKey()).isEqualTo(repModel.getParentKey());
    assertThat(entity.getType()).isEqualTo(repModel.getType());
    assertThat(entity.getPriority()).isEqualTo(repModel.getPriority());
    assertThat(entity.getIsValidInDomain()).isEqualTo(repModel.getIsValidInDomain());
    assertThat(entity.getCreated()).isEqualTo(repModel.getCreated());
    assertThat(entity.getModified()).isEqualTo(repModel.getModified());
  }

  private void testEqualityOfEntities(Classification class1, Classification class2) {
    assertThat(class1.getApplicationEntryPoint()).isEqualTo(class2.getApplicationEntryPoint());
    assertThat(class1.getKey()).isEqualTo(class2.getKey());
    assertThat(class1.getDomain()).isEqualTo(class2.getDomain());
    assertThat(class1.getId()).isEqualTo(class2.getId());
    assertThat(class1.getDescription()).isEqualTo(class2.getDescription());
    assertThat(class1.getName()).isEqualTo(class2.getName());
    assertThat(class1.getServiceLevel()).isEqualTo(class2.getServiceLevel());
    assertThat(class1.getCategory()).isEqualTo(class2.getCategory());
    assertThat(class1.getCustom1()).isEqualTo(class2.getCustom1());
    assertThat(class1.getCustom2()).isEqualTo(class2.getCustom2());
    assertThat(class1.getCustom3()).isEqualTo(class2.getCustom3());
    assertThat(class1.getCustom4()).isEqualTo(class2.getCustom4());
    assertThat(class1.getCustom5()).isEqualTo(class2.getCustom5());
    assertThat(class1.getCustom6()).isEqualTo(class2.getCustom6());
    assertThat(class1.getCustom7()).isEqualTo(class2.getCustom7());
    assertThat(class1.getCustom8()).isEqualTo(class2.getCustom8());
    assertThat(class1.getParentId()).isEqualTo(class2.getParentId());
    assertThat(class1.getParentKey()).isEqualTo(class2.getParentKey());
    assertThat(class1.getType()).isEqualTo(class2.getType());
    assertThat(class1.getPriority()).isEqualTo(class2.getPriority());
    assertThat(class1.getIsValidInDomain()).isEqualTo(class2.getIsValidInDomain());
    assertThat(class1.getCreated()).isEqualTo(class2.getCreated());
    assertThat(class1.getModified()).isEqualTo(class2.getModified());
  }


}
