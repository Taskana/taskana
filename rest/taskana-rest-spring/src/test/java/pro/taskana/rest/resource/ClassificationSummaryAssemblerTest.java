package pro.taskana.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;

/** Test for {@link ClassificationSummaryResourceAssembler}. */
@TaskanaSpringBootTest
class ClassificationSummaryAssemblerTest {
  @Autowired ClassificationSummaryResourceAssembler classificationSummaryResourceAssembler;
  @Autowired private ClassificationService classificationService;

  @Test
  void testModelToResource() {
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
    ClassificationSummary classificationSummary = classification.asSummary();
    // when
    ClassificationSummaryResource resource =
        classificationSummaryResourceAssembler.toResource(classification);
    // then
    testEquality(classificationSummary, resource);
  }

  @Test
  void testResourceToModel() {
    // given
    ClassificationSummaryResource resource = new ClassificationSummaryResource();
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
        classificationSummaryResourceAssembler.toModel(resource);
    // then
    testEquality(classificationSummary, resource);
  }

  private void testEquality(
      ClassificationSummary classificationSummary, ClassificationSummaryResource resource) {
    assertThat(resource.getKey()).isEqualTo(classificationSummary.getKey());
    assertThat(resource.getDomain()).isEqualTo(classificationSummary.getDomain());
    assertThat(resource.getClassificationId()).isEqualTo(classificationSummary.getId());
    assertThat(resource.getName()).isEqualTo(classificationSummary.getName());
    assertThat(resource.getCategory()).isEqualTo(classificationSummary.getCategory());
    assertThat(resource.getCustom1()).isEqualTo(classificationSummary.getCustom1());
    assertThat(resource.getCustom2()).isEqualTo(classificationSummary.getCustom2());
    assertThat(resource.getCustom3()).isEqualTo(classificationSummary.getCustom3());
    assertThat(resource.getCustom4()).isEqualTo(classificationSummary.getCustom4());
    assertThat(resource.getCustom5()).isEqualTo(classificationSummary.getCustom5());
    assertThat(resource.getCustom6()).isEqualTo(classificationSummary.getCustom6());
    assertThat(resource.getCustom7()).isEqualTo(classificationSummary.getCustom7());
    assertThat(resource.getCustom8()).isEqualTo(classificationSummary.getCustom8());
    assertThat(resource.getParentId()).isEqualTo(classificationSummary.getParentId());
    assertThat(resource.getParentKey()).isEqualTo(classificationSummary.getParentKey());
    assertThat(resource.getType()).isEqualTo(classificationSummary.getType());
    assertThat(resource.getPriority()).isEqualTo(classificationSummary.getPriority());
    assertThat(resource.getApplicationEntryPoint())
        .isEqualTo(classificationSummary.getApplicationEntryPoint());
    assertThat(resource.getServiceLevel()).isEqualTo(classificationSummary.getServiceLevel());
  }
}
