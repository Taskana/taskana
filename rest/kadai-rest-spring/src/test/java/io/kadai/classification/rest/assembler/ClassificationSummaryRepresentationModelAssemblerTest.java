package io.kadai.classification.rest.assembler;

import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_1;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_2;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_3;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_4;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_5;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_6;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_7;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_8;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.internal.models.ClassificationSummaryImpl;
import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Test for {@link ClassificationSummaryRepresentationModelAssembler}. */
@KadaiSpringBootTest
class ClassificationSummaryRepresentationModelAssemblerTest {

  private final ClassificationSummaryRepresentationModelAssembler assembler;

  private final ClassificationService classificationService;

  @Autowired
  ClassificationSummaryRepresentationModelAssemblerTest(
      ClassificationSummaryRepresentationModelAssembler assembler,
      ClassificationService classificationService) {
    this.assembler = assembler;
    this.classificationService = classificationService;
  }

  static void testEquality(
      ClassificationSummary entity, ClassificationSummaryRepresentationModel repModel) {
    assertThat(entity).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();
    assertThat(entity.getId()).isEqualTo(repModel.getClassificationId());
    assertThat(entity.getKey()).isEqualTo(repModel.getKey());
    assertThat(entity.getApplicationEntryPoint()).isEqualTo(repModel.getApplicationEntryPoint());
    assertThat(entity.getCategory()).isEqualTo(repModel.getCategory());
    assertThat(entity.getDomain()).isEqualTo(repModel.getDomain());
    assertThat(entity.getName()).isEqualTo(repModel.getName());
    assertThat(entity.getParentId()).isEqualTo(repModel.getParentId());
    assertThat(entity.getParentKey()).isEqualTo(repModel.getParentKey());
    assertThat(entity.getPriority()).isEqualTo(repModel.getPriority());
    assertThat(entity.getServiceLevel()).isEqualTo(repModel.getServiceLevel());
    assertThat(entity.getType()).isEqualTo(repModel.getType());
    assertThat(entity.getCustomField(CUSTOM_1)).isEqualTo(repModel.getCustom1());
    assertThat(entity.getCustomField(CUSTOM_2)).isEqualTo(repModel.getCustom2());
    assertThat(entity.getCustomField(CUSTOM_3)).isEqualTo(repModel.getCustom3());
    assertThat(entity.getCustomField(CUSTOM_4)).isEqualTo(repModel.getCustom4());
    assertThat(entity.getCustomField(CUSTOM_5)).isEqualTo(repModel.getCustom5());
    assertThat(entity.getCustomField(CUSTOM_6)).isEqualTo(repModel.getCustom6());
    assertThat(entity.getCustomField(CUSTOM_7)).isEqualTo(repModel.getCustom7());
    assertThat(entity.getCustomField(CUSTOM_8)).isEqualTo(repModel.getCustom8());
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    // given
    ClassificationSummaryImpl classification =
        (ClassificationSummaryImpl)
            classificationService.newClassification("DOMAIN_A", "1", "A").asSummary();
    classification.setId("1");
    classification.setCategory("ABC");
    classification.setName("Classification 1");
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
    // when
    ClassificationSummaryRepresentationModel repModel = assembler.toModel(classification);
    // then
    testEquality(classification, repModel);
    testLinks(repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    // given
    ClassificationSummaryRepresentationModel repModel =
        new ClassificationSummaryRepresentationModel();
    repModel.setCategory("EFG");
    repModel.setClassificationId("2");
    repModel.setCustom1("custom1");
    repModel.setCustom2("custom2");
    repModel.setCustom3("custom3");
    repModel.setCustom4("custom4");
    repModel.setCustom5("custom5");
    repModel.setCustom6("custom6");
    repModel.setCustom7("custom7");
    repModel.setCustom8("custom8");
    repModel.setDomain("domain1");
    repModel.setKey("key");
    repModel.setName("classSummary");
    repModel.setParentId("ID2");
    repModel.setParentKey("key2");
    repModel.setPriority(1);
    repModel.setType("A");
    repModel.setApplicationEntryPoint("applicationEntryPoint");
    repModel.setServiceLevel("ServiceLevel");
    // when
    ClassificationSummary classificationSummary = assembler.toEntityModel(repModel);
    // then
    testEquality(classificationSummary, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    ClassificationSummaryImpl classification =
        (ClassificationSummaryImpl)
            classificationService.newClassification("DOMAIN_A", "1", "A").asSummary();
    classification.setId("1");
    classification.setCategory("ABC");
    classification.setName("Classification 1");
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
    // when
    ClassificationSummaryRepresentationModel repModel = assembler.toModel(classification);
    ClassificationSummary secondClassification = assembler.toEntityModel(repModel);
    // then
    assertThat(classification)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(secondClassification)
        .isEqualTo(secondClassification);
  }

  private void testLinks(ClassificationSummaryRepresentationModel repModel) {}
}
