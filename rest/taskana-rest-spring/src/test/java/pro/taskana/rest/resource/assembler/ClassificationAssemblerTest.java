package pro.taskana.rest.resource.assembler;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.ClassificationResource;

/**
 * Test for {@link ClassificationResourceAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ClassificationAssemblerTest {

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @Autowired
    private ClassificationService classificationService;

    @Test
    public void classificationToResource() throws ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, ConcurrencyException, DomainNotFoundException, InvalidArgumentException {
        // given
        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification("DOMAIN_A",
            "1", "A");
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
        ClassificationResource classificationResource = classificationResourceAssembler.toResource(classification);
        // then
        testEquality(classification, classificationResource);
    }

    @Test
    public void resourceToClassification() {
        // given
        ClassificationResource classificationResource = new ClassificationResource();
        classificationResource.setClassificationId("1");
        classificationResource.setKey("12");
        classificationResource.setName("TestB");
        classificationResource.setType("AB");
        classificationResource.setDomain("DOMAIN_B");
        classificationResource.setApplicationEntryPoint("Test");
        classificationResource.setCategory("ABC");
        classificationResource.setCreated("2010-01-01T12:00:00Z");
        classificationResource.setModified("2011-11-11T11:00:00Z");
        classificationResource.setCustom1("Custom");
        classificationResource.setCustom2("Custom2");
        classificationResource.setCustom1("Custom1");
        classificationResource.setCustom3("Custom3");
        classificationResource.setCustom4("Custom4");
        classificationResource.setCustom5("Custom5");
        classificationResource.setCustom6("Custom6");
        classificationResource.setCustom7("Custom7");
        classificationResource.setCustom8("Custom8");
        classificationResource.setParentId("2");
        classificationResource.setPriority(2);
        classificationResource.setApplicationEntryPoint("12");
        classificationResource.setServiceLevel("P1D");
        classificationResource.setDescription("Test");
        classificationResource.setIsValidInDomain(true);
        // when
        ClassificationImpl classification = (ClassificationImpl) classificationResourceAssembler
            .toModel(classificationResource);
        // then
        testEquality(classification, classificationResource);
    }

    private void testEquality(Classification classification, ClassificationResource classificationResource) {
        Assert.assertEquals(classification.getApplicationEntryPoint(), classificationResource.applicationEntryPoint);
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
        Assert.assertEquals(classification.getIsValidInDomain(), classificationResource.isValidInDomain);
    }
}
