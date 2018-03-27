package pro.taskana.rest.resource.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.ClassificationSummary;
import pro.taskana.rest.ClassificationController;
import pro.taskana.rest.resource.ClassificationSummaryResource;

/**
 * @author HH
 */
public class ClassificationSummaryResourceAssembler
    extends ResourceAssemblerSupport<ClassificationSummary, ClassificationSummaryResource> {

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

}
