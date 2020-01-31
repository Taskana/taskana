package pro.taskana.rest.resource;

import java.util.Collection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.ClassificationSummary;
import pro.taskana.classification.internal.ClassificationImpl;
import pro.taskana.rest.ClassificationController;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.links.PageLinks;

/** Resource assembler for {@link ClassificationSummaryResource}. */
@Component
public class ClassificationSummaryResourceAssembler
    extends ResourceAssemblerSupport<ClassificationSummary, ClassificationSummaryResource> {

  @Autowired private ClassificationService classificationService;

  public ClassificationSummaryResourceAssembler() {
    super(ClassificationController.class, ClassificationSummaryResource.class);
  }

  @Override
  public ClassificationSummaryResource toResource(ClassificationSummary classificationSummary) {
    return new ClassificationSummaryResource(classificationSummary);
  }

  public ClassificationSummary toModel(ClassificationSummaryResource resource) {
    ClassificationImpl classification =
        (ClassificationImpl)
            classificationService.newClassification(
                resource.getKey(), resource.getDomain(), resource.getType());
    classification.setId(resource.getClassificationId());
    BeanUtils.copyProperties(resource, classification);
    return classification.asSummary();
  }

  @PageLinks(Mapping.URL_CLASSIFICATIONS)
  public ClassificationSummaryListResource toResources(
      Collection<ClassificationSummary> entities, PageMetadata pageMetadata) {
    return new ClassificationSummaryListResource(toResources(entities), pageMetadata);
  }
}
