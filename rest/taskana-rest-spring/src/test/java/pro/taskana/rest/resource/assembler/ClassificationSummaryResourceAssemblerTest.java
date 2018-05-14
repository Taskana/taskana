package pro.taskana.rest.resource.assembler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.ClassificationSummaryResource;

/**
 * Test for {@link ClassificationSummaryResourceAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ClassificationSummaryResourceAssemblerTest {

    @Autowired
    ClassificationSummaryResourceAssembler classificationSummaryResourceAssembler;

    @Autowired
    private ClassificationService classificationService;

    @Test
    public void ClassificationSummaryToRessource() {
        // given
        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification("123", "ABC", "DEF");
        classification.setId("C2P0");
        classification.setCategory("Some Category");
        classification.setName("Some Name");
        classification.setParentId("Some ParentId");
        classification.setPriority(1);
        ClassificationSummary classificationSummary = classification.asSummary();

        // when
        ClassificationSummaryResource classificationSummaryResource = classificationSummaryResourceAssembler.toResource(classificationSummary);

        // then
        testEquality(classificationSummary, classificationSummaryResource);
    }

    @Test
    public void ResourceToModel() {
        // given
        ClassificationSummaryResource resource = new ClassificationSummaryResource();
        resource.setClassificationId("C2P0");
        resource.setCategory("Some Category");
        resource.setDomain("Some Domain");
        resource.setKey("Some Key");
        resource.setName("Some Name");
        resource.setParentId("Some ParentId");
        resource.setPriority(1);
        resource.setType("Type A");

        // when
        ClassificationSummary classificationSummary = classificationSummaryResourceAssembler.toModel(resource);

        // then
        testEquality(classificationSummary, resource);
    }

    private void testEquality(ClassificationSummary classificationSummary, ClassificationSummaryResource classificationSummaryResource) {
        Assert.assertEquals(classificationSummary.getId(), classificationSummaryResource.getClassificationId());
        Assert.assertEquals(classificationSummary.getDomain(), classificationSummaryResource.domain);
        Assert.assertEquals(classificationSummary.getKey(), classificationSummaryResource.key);
        Assert.assertEquals(classificationSummary.getCategory(), classificationSummaryResource.category);
        Assert.assertEquals(classificationSummary.getName(), classificationSummaryResource.name);
        Assert.assertEquals(classificationSummary.getParentId(), classificationSummaryResource.parentId);
        Assert.assertEquals(classificationSummary.getPriority(), classificationSummaryResource.priority);
        Assert.assertEquals(classificationSummary.getType(), classificationSummaryResource.type);
    }

}
