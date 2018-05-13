package pro.taskana.rest.resource.assembler;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.ClassificationController;
import pro.taskana.rest.resource.ClassificationSummaryResource;

/**
 * Resource assembler for {@link ClassificationSummaryResource}.
 */
@Component
public class ClassificationSummaryResourceAssembler
    extends ResourceAssemblerSupport<ClassificationSummary, ClassificationSummaryResource> {

    @Autowired
    private ClassificationService classificationService;

    public ClassificationSummaryResourceAssembler() {
        super(ClassificationController.class, ClassificationSummaryResource.class);
    }

    @Override
    public ClassificationSummaryResource toResource(ClassificationSummary classificationSummary) {
        ClassificationSummaryResource resource = createResourceWithId(classificationSummary.getId(),
            classificationSummary);
        BeanUtils.copyProperties(classificationSummary, resource);
        // named different so needs to be set by hand
        resource.setClassificationId(classificationSummary.getId());
        return resource;
    }

    public ClassificationSummary toModel(ClassificationSummaryResource resource) {
        ClassificationImpl classification = (ClassificationImpl) classificationService
            .newClassification(
                resource.getKey(), resource.getDomain(),
                resource.getType());
        classification.setId(resource.getClassificationId());
        BeanUtils.copyProperties(resource, classification);
        return classification.asSummary();
    }

}
