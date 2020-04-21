package pro.taskana.rest.resource;

import java.util.Collection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.rest.ClassificationController;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.links.PageLinks;

/** EntityModel assembler for {@link ClassificationSummaryResource}. */
@Component
public class ClassificationSummaryResourceAssembler
    extends RepresentationModelAssemblerSupport<
        ClassificationSummary, ClassificationSummaryResource> {

  @Autowired private ClassificationService classificationService;

  public ClassificationSummaryResourceAssembler() {
    super(ClassificationController.class, ClassificationSummaryResource.class);
  }

  @Override
  public ClassificationSummaryResource toModel(ClassificationSummary classificationSummary) {
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
  public ClassificationSummaryListResource toCollectionModel(
      Collection<ClassificationSummary> entities, PageMetadata pageMetadata) {
    return new ClassificationSummaryListResource(
        toCollectionModel(entities).getContent(), pageMetadata);
  }
}
