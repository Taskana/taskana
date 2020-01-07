package pro.taskana.rest.resource;

import java.time.Instant;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.Mapping;

/** Test for {@link ClassificationResourceAssembler}. */
@TaskanaSpringBootTest
class ClassificationAssemblerTest {

  @Autowired private ClassificationResourceAssembler classificationResourceAssembler;

  @Autowired private ClassificationService classificationService;

  @Test
  void classificationToResource() {
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
    ClassificationResource classificationResource =
        classificationResourceAssembler.toResource(classification);
    // then
    testEquality(classification, classificationResource);
    testLinks(classificationResource);
  }

  @Test
  void resourceToClassification() {
    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("12", "DOMAIN_B", "AB");

    // given
    classification.setId("1");
    classification.setType("AB");
    classification.setDomain("DOMAIN_B");
    classification.setApplicationEntryPoint("Test");
    classification.setCategory("ABC");
    classification.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    classification.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    classification.setCustom1("Custom");
    classification.setCustom2("Custom2");
    classification.setCustom1("Custom1");
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
    classification.setIsValidInDomain(true);

    ClassificationResource classificationResource = new ClassificationResource(classification);

    // when
    classification =
        (ClassificationImpl) classificationResourceAssembler.toModel(classificationResource);
    // then
    testEquality(classification, classificationResource);
  }

  private void testLinks(ClassificationResource resource) {
    Assert.assertEquals(1, resource.getLinks().size());
    Assert.assertEquals(
        Mapping.URL_CLASSIFICATIONS_ID.replaceAll("\\{.*}", resource.getClassificationId()),
        resource.getLink("self").getHref());
  }

  private void testEquality(
      Classification classification, ClassificationResource classificationResource) {
    Assert.assertEquals(
        classification.getApplicationEntryPoint(), classificationResource.applicationEntryPoint);
    Assert.assertEquals(classification.getKey(), classificationResource.key);
    Assert.assertEquals(classification.getDomain(), classificationResource.domain);
    Assert.assertEquals(classification.getId(), classificationResource.classificationId);
    Assert.assertEquals(classification.getDescription(), classificationResource.description);
    Assert.assertEquals(classification.getName(), classificationResource.name);
    Assert.assertEquals(classification.getServiceLevel(), classificationResource.serviceLevel);
    Assert.assertEquals(classification.getCategory(), classificationResource.category);
    Assert.assertEquals(classification.getCustom1(), classificationResource.custom1);
    Assert.assertEquals(classification.getCustom2(), classificationResource.custom2);
    Assert.assertEquals(classification.getCustom3(), classificationResource.custom3);
    Assert.assertEquals(classification.getCustom4(), classificationResource.custom4);
    Assert.assertEquals(classification.getCustom5(), classificationResource.custom5);
    Assert.assertEquals(classification.getCustom6(), classificationResource.custom6);
    Assert.assertEquals(classification.getCustom7(), classificationResource.custom7);
    Assert.assertEquals(classification.getCustom8(), classificationResource.custom8);
    Assert.assertEquals(classification.getParentId(), classificationResource.parentId);
    Assert.assertEquals(classification.getType(), classificationResource.type);
    Assert.assertEquals(classification.getPriority(), classificationResource.priority);
    Assert.assertEquals(
        classification.getIsValidInDomain(), classificationResource.isValidInDomain);
  }
}
