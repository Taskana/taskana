package pro.taskana.rest.resource;

import java.time.Instant;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.ClassificationSummary;
import pro.taskana.classification.internal.ClassificationImpl;

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
    Assert.assertEquals(classificationSummary.getKey(), resource.key);
    Assert.assertEquals(classificationSummary.getDomain(), resource.domain);
    Assert.assertEquals(classificationSummary.getId(), resource.classificationId);
    Assert.assertEquals(classificationSummary.getName(), resource.name);
    Assert.assertEquals(classificationSummary.getCategory(), resource.category);
    Assert.assertEquals(classificationSummary.getCustom1(), resource.custom1);
    Assert.assertEquals(classificationSummary.getCustom2(), resource.custom2);
    Assert.assertEquals(classificationSummary.getCustom3(), resource.custom3);
    Assert.assertEquals(classificationSummary.getCustom4(), resource.custom4);
    Assert.assertEquals(classificationSummary.getCustom5(), resource.custom5);
    Assert.assertEquals(classificationSummary.getCustom6(), resource.custom6);
    Assert.assertEquals(classificationSummary.getCustom7(), resource.custom7);
    Assert.assertEquals(classificationSummary.getCustom8(), resource.custom8);
    Assert.assertEquals(classificationSummary.getParentId(), resource.parentId);
    Assert.assertEquals(classificationSummary.getParentKey(), resource.parentKey);
    Assert.assertEquals(classificationSummary.getType(), resource.type);
    Assert.assertEquals(classificationSummary.getPriority(), resource.priority);
  }
}
