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

/** Test for {@link ClassificationResourceAssembler}. */
@TaskanaSpringBootTest
class ClassificationAssemblerTest {

  @Autowired ClassificationResourceAssembler classificationResourceAssembler;

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
    ClassificationResource classificationResource =
        classificationResourceAssembler.toModel(classification);
    // then
    testEquality(classification, classificationResource);
    testLinks(classificationResource);
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

    ClassificationResource classificationResource = new ClassificationResource(classification);

    // when
    classification =
        (ClassificationImpl) classificationResourceAssembler.toModel(classificationResource);
    // then
    testEquality(classification, classificationResource);
  }

  private void testLinks(ClassificationResource resource) {
    assertThat(resource.getLinks()).hasSize(1);
    assertThat(Mapping.URL_CLASSIFICATIONS_ID.replaceAll("\\{.*}", resource.getClassificationId()))
        .isEqualTo(resource.getRequiredLink("self").getHref());
  }

  private void testEquality(
      Classification classification, ClassificationResource classificationResource) {
    assertThat(classification.getApplicationEntryPoint())
        .isEqualTo(classificationResource.getApplicationEntryPoint());
    assertThat(classification.getKey()).isEqualTo(classificationResource.getKey());
    assertThat(classification.getDomain()).isEqualTo(classificationResource.getDomain());
    assertThat(classification.getId()).isEqualTo(classificationResource.getClassificationId());
    assertThat(classification.getDescription()).isEqualTo(classificationResource.getDescription());
    assertThat(classification.getName()).isEqualTo(classificationResource.getName());
    assertThat(classification.getServiceLevel())
        .isEqualTo(classificationResource.getServiceLevel());
    assertThat(classification.getCategory()).isEqualTo(classificationResource.getCategory());
    assertThat(classification.getCustom1()).isEqualTo(classificationResource.getCustom1());
    assertThat(classification.getCustom2()).isEqualTo(classificationResource.getCustom2());
    assertThat(classification.getCustom3()).isEqualTo(classificationResource.getCustom3());
    assertThat(classification.getCustom4()).isEqualTo(classificationResource.getCustom4());
    assertThat(classification.getCustom5()).isEqualTo(classificationResource.getCustom5());
    assertThat(classification.getCustom6()).isEqualTo(classificationResource.getCustom6());
    assertThat(classification.getCustom7()).isEqualTo(classificationResource.getCustom7());
    assertThat(classification.getCustom8()).isEqualTo(classificationResource.getCustom8());
    assertThat(classification.getParentId()).isEqualTo(classificationResource.getParentId());
    assertThat(classification.getParentKey()).isEqualTo(classificationResource.getParentKey());
    assertThat(classification.getType()).isEqualTo(classificationResource.getType());
    assertThat(classification.getPriority()).isEqualTo(classificationResource.getPriority());
    assertThat(classification.getIsValidInDomain())
        .isEqualTo(classificationResource.getIsValidInDomain());
    assertThat(classification.getCreated()).isEqualTo(classificationResource.getCreated());
    assertThat(classification.getModified()).isEqualTo(classificationResource.getModified());
  }
}
