package pro.taskana.rest.resource;

import static pro.taskana.rest.resource.TaskanaPagedModelKeys.CLASSIFICATIONS;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.resource.links.PageLinks;

/**
 * EntityModel assembler for {@link ClassificationSummaryRepresentationModel}.
 */
@Component
public class ClassificationSummaryRepresentationModelAssembler
    implements
    RepresentationModelAssembler<ClassificationSummary, ClassificationSummaryRepresentationModel> {

  private final ClassificationService classificationService;

  @Autowired
  public ClassificationSummaryRepresentationModelAssembler(
      ClassificationService classificationService) {
    this.classificationService = classificationService;
  }

  @NonNull
  @Override
  public ClassificationSummaryRepresentationModel toModel(
      @NonNull ClassificationSummary classificationSummary) {
    return new ClassificationSummaryRepresentationModel(classificationSummary);
  }

  public ClassificationSummary toEntityModel(ClassificationSummaryRepresentationModel resource) {
    ClassificationImpl classification =
        (ClassificationImpl)
            classificationService.newClassification(
                resource.getKey(), resource.getDomain(), resource.getType());
    classification.setId(resource.getClassificationId());
    BeanUtils.copyProperties(resource, classification);
    return classification.asSummary();
  }
  
  @PageLinks(Mapping.URL_CLASSIFICATIONS)
  public TaskanaPagedModel<ClassificationSummaryRepresentationModel> toPageModel(
      List<ClassificationSummary> classificationSummaries, PageMetadata pageMetadata) {
    return classificationSummaries.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(CLASSIFICATIONS, list, pageMetadata)));
  }
}
