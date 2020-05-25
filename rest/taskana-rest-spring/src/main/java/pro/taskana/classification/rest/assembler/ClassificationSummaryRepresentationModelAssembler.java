package pro.taskana.classification.rest.assembler;

import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.CLASSIFICATIONS;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.assembler.TaskanaPagingAssembler;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;
import pro.taskana.resource.rest.PageLinks;

/** EntityModel assembler for {@link ClassificationSummaryRepresentationModel}. */
@Component
public class ClassificationSummaryRepresentationModelAssembler
    implements TaskanaPagingAssembler<
        ClassificationSummary, ClassificationSummaryRepresentationModel> {

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

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return CLASSIFICATIONS;
  }

  @PageLinks(Mapping.URL_CLASSIFICATIONS)
  @Override
  public TaskanaPagedModel<ClassificationSummaryRepresentationModel> toPageModel(
      Iterable<? extends ClassificationSummary> entities, PageMetadata pageMetadata) {
    return TaskanaPagingAssembler.super.toPageModel(entities, pageMetadata);
  }
}
