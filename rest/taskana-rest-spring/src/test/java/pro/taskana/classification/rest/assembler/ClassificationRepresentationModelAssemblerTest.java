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
class ClassificationRepresentationModelAssemblerTest {

  private final ClassificationRepresentationModelAssembler assembler;

  private final ClassificationService classificationService;

  @Autowired
  ClassificationRepresentationModelAssemblerTest(
      ClassificationRepresentationModelAssembler assembler,
      ClassificationService classificationService) {
    this.assembler = assembler;
    this.classificationService = classificationService;
  }

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
    ClassificationRepresentationModel repModel = assembler.toModel(classification);
    // then
    testEquality(classification, repModel);
    testLinks(repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    // given
    ClassificationRepresentationModel repModel = new ClassificationRepresentationModel();
    repModel.setKey("KEY_B");
    repModel.setName("name");
    repModel.setDomain("DOMAIN_B");
    repModel.setType("AB");
    repModel.setClassificationId("1");
    repModel.setApplicationEntryPoint("Test");
    repModel.setCategory("ABC");
    repModel.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    repModel.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    repModel.setCustom1("Custom1");
    repModel.setCustom2("Custom2");
    repModel.setCustom3("Custom3");
    repModel.setCustom4("Custom4");
    repModel.setCustom5("Custom5");
    repModel.setCustom6("Custom6");
    repModel.setCustom7("Custom7");
    repModel.setCustom8("Custom8");
    repModel.setParentId("2");
    repModel.setParentKey("parentKey");
    repModel.setPriority(2);
    repModel.setApplicationEntryPoint("12");
    repModel.setServiceLevel("P1D");
    repModel.setDescription("Test");
    repModel.setIsValidInDomain(true);
    // when
    Classification classification = assembler.toEntityModel(repModel);
    // then
    testEquality(classification, repModel);
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
    ClassificationRepresentationModel repModel = assembler.toModel(classification);
    Classification secondClassification = assembler.toEntityModel(repModel);
    // then
    assertThat(classification)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(secondClassification)
        .isEqualTo(secondClassification);
  }

  private void testLinks(ClassificationRepresentationModel repModel) {
    assertThat(repModel.getLinks()).hasSize(1);
    assertThat(Mapping.URL_CLASSIFICATIONS_ID.replaceAll("\\{.*}", repModel.getClassificationId()))
        .isEqualTo(repModel.getRequiredLink("self").getHref());
  }

  private static void testEquality(
      Classification entity, ClassificationRepresentationModel repModel) {
    ClassificationSummaryRepresentationModelAssemblerTest.testEquality(entity, repModel);

    assertThat(entity.getIsValidInDomain()).isEqualTo(repModel.getIsValidInDomain());
    assertThat(entity.getCreated()).isEqualTo(repModel.getCreated());
    assertThat(entity.getModified()).isEqualTo(repModel.getModified());
    assertThat(entity.getDescription()).isEqualTo(repModel.getDescription());
  }
}
