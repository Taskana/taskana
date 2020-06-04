package pro.taskana.classification.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.TaskanaSpringBootTest;

/** Test for {@link ClassificationSummaryRepresentationModelAssembler}. */
@TaskanaSpringBootTest
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
    // when
    ClassificationSummary classificationSummary = assembler.toEntityModel(repModel);
    // then
    testEquality(classificationSummary, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    ClassificationSummaryImpl classification =
        (ClassificationImpl) classificationService.newClassification("DOMAIN_A", "1", "A");
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
    classification.setPriority(2);
    classification.setApplicationEntryPoint("12");
    classification.setServiceLevel("P1D");
    // when
    ClassificationSummaryRepresentationModel repModel = assembler.toModel(classification);
    ClassificationSummary secondClassification = assembler.toEntityModel(repModel);
    assertThat(classification).isNotSameAs(secondClassification).isEqualTo(secondClassification);
  }

  private void testEquality(
      ClassificationSummary entity, ClassificationSummaryRepresentationModel repModel) {
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
    assertThat(repModel.getApplicationEntryPoint()).isEqualTo(entity.getApplicationEntryPoint());
    assertThat(repModel.getServiceLevel()).isEqualTo(entity.getServiceLevel());
  }

  private void testLinks(ClassificationSummaryRepresentationModel repModel) {}
}
