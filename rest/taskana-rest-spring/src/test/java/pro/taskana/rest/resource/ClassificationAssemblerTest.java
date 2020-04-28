package pro.taskana.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.rest.Mapping;

/** Test for {@link ClassificationRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class ClassificationAssemblerTest {

  @Autowired ClassificationRepresentationModelAssembler classificationRepresentationModelAssembler;

  @Autowired ClassificationService classificationService;

  @Test
  void classificationToResource() {
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
    testEquality(classification, classificationRepresentationModel);
    testLinks(classificationRepresentationModel);
  }

  @Test
  void resourceToClassification() {
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
        new ClassificationRepresentationModel(classification);

    // when
    classification =
        (ClassificationImpl)
            classificationRepresentationModelAssembler
                .toEntityModel(classificationRepresentationModel);
    // then
    testEquality(classification, classificationRepresentationModel);
  }

  private void testLinks(ClassificationRepresentationModel resource) {
    assertThat(resource.getLinks()).hasSize(1);
    assertThat(Mapping.URL_CLASSIFICATIONS_ID.replaceAll("\\{.*}", resource.getClassificationId()))
        .isEqualTo(resource.getRequiredLink("self").getHref());
  }

  private void testEquality(
      Classification classification,
      ClassificationRepresentationModel classificationRepresentationModel) {
    assertThat(classification.getApplicationEntryPoint())
        .isEqualTo(classificationRepresentationModel.getApplicationEntryPoint());
    assertThat(classification.getKey()).isEqualTo(classificationRepresentationModel.getKey());
    assertThat(classification.getDomain()).isEqualTo(classificationRepresentationModel.getDomain());
    assertThat(classification.getId())
        .isEqualTo(classificationRepresentationModel.getClassificationId());
    assertThat(classification.getDescription())
        .isEqualTo(classificationRepresentationModel.getDescription());
    assertThat(classification.getName()).isEqualTo(classificationRepresentationModel.getName());
    assertThat(classification.getServiceLevel())
        .isEqualTo(classificationRepresentationModel.getServiceLevel());
    assertThat(classification.getCategory())
        .isEqualTo(classificationRepresentationModel.getCategory());
    assertThat(classification.getCustom1())
        .isEqualTo(classificationRepresentationModel.getCustom1());
    assertThat(classification.getCustom2())
        .isEqualTo(classificationRepresentationModel.getCustom2());
    assertThat(classification.getCustom3())
        .isEqualTo(classificationRepresentationModel.getCustom3());
    assertThat(classification.getCustom4())
        .isEqualTo(classificationRepresentationModel.getCustom4());
    assertThat(classification.getCustom5())
        .isEqualTo(classificationRepresentationModel.getCustom5());
    assertThat(classification.getCustom6())
        .isEqualTo(classificationRepresentationModel.getCustom6());
    assertThat(classification.getCustom7())
        .isEqualTo(classificationRepresentationModel.getCustom7());
    assertThat(classification.getCustom8())
        .isEqualTo(classificationRepresentationModel.getCustom8());
    assertThat(classification.getParentId())
        .isEqualTo(classificationRepresentationModel.getParentId());
    assertThat(classification.getParentKey())
        .isEqualTo(classificationRepresentationModel.getParentKey());
    assertThat(classification.getType()).isEqualTo(classificationRepresentationModel.getType());
    assertThat(classification.getPriority())
        .isEqualTo(classificationRepresentationModel.getPriority());
    assertThat(classification.getIsValidInDomain())
        .isEqualTo(classificationRepresentationModel.getIsValidInDomain());
    assertThat(classification.getCreated())
        .isEqualTo(classificationRepresentationModel.getCreated());
    assertThat(classification.getModified())
        .isEqualTo(classificationRepresentationModel.getModified());
  }
}
