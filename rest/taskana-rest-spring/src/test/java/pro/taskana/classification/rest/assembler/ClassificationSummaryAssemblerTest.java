package pro.taskana.classification.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.TaskanaSpringBootTest;

/** Test for {@link ClassificationSummaryRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class ClassificationSummaryAssemblerTest {
  @Autowired
  ClassificationSummaryRepresentationModelAssembler
      classificationSummaryRepresentationModelAssembler;

  @Autowired private ClassificationService classificationService;

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    // given
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("DOMAIN_A", "1", "A");
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
    classification.setPriority(2);
    classification.setApplicationEntryPoint("12");
    classification.setServiceLevel("P1D");
    classification.setDescription("Test");
    classification.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    classification.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    // when
    ClassificationSummaryRepresentationModel repModel =
        classificationSummaryRepresentationModelAssembler.toModel(classification);
    // then
    testEqualityAfterConversion(classification, repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    // given
    ClassificationSummaryRepresentationModel resource =
        new ClassificationSummaryRepresentationModel();
    resource.setCategory("EFG");
    resource.setClassificationId("2");
    resource.setCustom1("custom1");
    resource.setCustom2("custom2");
    resource.setCustom3("custom3");
    resource.setCustom4("custom4");
    resource.setCustom5("custom5");
    resource.setCustom6("custom6");
    resource.setCustom7("custom7");
    resource.setCustom8("custom8");
    resource.setDomain("domain1");
    resource.setKey("key");
    resource.setName("classSummary");
    resource.setParentId("ID2");
    resource.setParentKey("key2");
    resource.setPriority(1);
    resource.setType("A");
    // when
    ClassificationSummary classificationSummary =
        classificationSummaryRepresentationModelAssembler.toEntityModel(resource);
    // then
    testEqualityAfterConversion(classificationSummary, resource);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("DOMAIN_A", "1", "A");
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
    classification.setPriority(2);
    classification.setApplicationEntryPoint("12");
    classification.setServiceLevel("P1D");
    classification.setDescription("Test");
    classification.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    classification.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    // when
    ClassificationSummaryRepresentationModel repModel =
        classificationSummaryRepresentationModelAssembler.toModel(classification);
    ClassificationImpl secondClassification
        = (ClassificationImpl) classificationSummaryRepresentationModelAssembler
                                   .toEntityModel(repModel);
    testEqualityOfEntities(classification, secondClassification);
  }

  private void testEqualityAfterConversion(
      ClassificationSummary entity,
      ClassificationSummaryRepresentationModel repModel) {
    assertThat(repModel.getKey()).isEqualTo(entity.getKey());
    assertThat(repModel.getDomain()).isEqualTo(entity.getDomain());
    assertThat(repModel.getClassificationId()).isEqualTo(entity.getId());
    assertThat(repModel.getName()).isEqualTo(entity.getName());
    assertThat(repModel.getCategory()).isEqualTo(entity.getCategory());
    assertThat(repModel.getCustom1()).isEqualTo(entity.getCustom1());
    assertThat(repModel.getCustom2()).isEqualTo(entity.getCustom2());
    assertThat(repModel.getCustom3()).isEqualTo(entity.getCustom3());
    assertThat(repModel.getCustom4()).isEqualTo(entity.getCustom4());
    assertThat(repModel.getCustom5()).isEqualTo(entity.getCustom5());
    assertThat(repModel.getCustom6()).isEqualTo(entity.getCustom6());
    assertThat(repModel.getCustom7()).isEqualTo(entity.getCustom7());
    assertThat(repModel.getCustom8()).isEqualTo(entity.getCustom8());
    assertThat(repModel.getParentId()).isEqualTo(entity.getParentId());
    assertThat(repModel.getParentKey()).isEqualTo(entity.getParentKey());
    assertThat(repModel.getType()).isEqualTo(entity.getType());
    assertThat(repModel.getPriority()).isEqualTo(entity.getPriority());
    assertThat(repModel.getApplicationEntryPoint())
        .isEqualTo(entity.getApplicationEntryPoint());
    assertThat(repModel.getServiceLevel()).isEqualTo(entity.getServiceLevel());
  }

  private void testEqualityOfEntities(
      ClassificationSummary class1,
      ClassificationSummary class2) {
    assertThat(class1.getKey()).isEqualTo(class2.getKey());
    assertThat(class1.getDomain()).isEqualTo(class2.getDomain());
    assertThat(class1.getId()).isEqualTo(class2.getId());
    assertThat(class1.getName()).isEqualTo(class2.getName());
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
    assertThat(class1.getApplicationEntryPoint())
        .isEqualTo(class2.getApplicationEntryPoint());
    assertThat(class1.getServiceLevel()).isEqualTo(class2.getServiceLevel());
  }

  private void testLinks(ClassificationSummaryRepresentationModel repModel) {
  }
}
