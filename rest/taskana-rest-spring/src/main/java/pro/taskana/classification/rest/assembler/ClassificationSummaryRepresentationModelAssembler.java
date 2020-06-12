package pro.taskana.classification.rest.assembler;

import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.CLASSIFICATIONS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
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
    ClassificationSummaryRepresentationModel repModel =
        new ClassificationSummaryRepresentationModel();
    repModel.setClassificationId(classificationSummary.getId());
    repModel.setApplicationEntryPoint(classificationSummary.getApplicationEntryPoint());
    repModel.setCategory(classificationSummary.getCategory());
    repModel.setDomain(classificationSummary.getDomain());
    repModel.setKey(classificationSummary.getKey());
    repModel.setName(classificationSummary.getName());
    repModel.setParentId(classificationSummary.getParentId());
    repModel.setParentKey(classificationSummary.getParentKey());
    repModel.setPriority(classificationSummary.getPriority());
    repModel.setServiceLevel(classificationSummary.getServiceLevel());
    repModel.setType(classificationSummary.getType());
    repModel.setCustom1(classificationSummary.getCustom1());
    repModel.setCustom2(classificationSummary.getCustom2());
    repModel.setCustom3(classificationSummary.getCustom3());
    repModel.setCustom4(classificationSummary.getCustom4());
    repModel.setCustom5(classificationSummary.getCustom5());
    repModel.setCustom6(classificationSummary.getCustom6());
    repModel.setCustom7(classificationSummary.getCustom7());
    repModel.setCustom8(classificationSummary.getCustom8());
    return repModel;
  }

  public ClassificationSummary toEntityModel(ClassificationSummaryRepresentationModel repModel) {
    ClassificationSummaryImpl classification =
        (ClassificationSummaryImpl)
            classificationService
                .newClassification(repModel.getKey(), repModel.getDomain(), repModel.getType())
                .asSummary();
    classification.setId(repModel.getClassificationId());
    classification.setApplicationEntryPoint(repModel.getApplicationEntryPoint());
    classification.setCategory(repModel.getCategory());
    classification.setName(repModel.getName());
    classification.setParentId(repModel.getParentId());
    classification.setParentKey(repModel.getParentKey());
    classification.setPriority(repModel.getPriority());
    classification.setServiceLevel(repModel.getServiceLevel());
    classification.setCustom1(repModel.getCustom1());
    classification.setCustom2(repModel.getCustom2());
    classification.setCustom3(repModel.getCustom3());
    classification.setCustom4(repModel.getCustom4());
    classification.setCustom5(repModel.getCustom5());
    classification.setCustom6(repModel.getCustom6());
    classification.setCustom7(repModel.getCustom7());
    classification.setCustom8(repModel.getCustom8());
    return classification;
  }

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return CLASSIFICATIONS;
  }

  @Override
  @PageLinks(Mapping.URL_CLASSIFICATIONS)
  public TaskanaPagedModel<ClassificationSummaryRepresentationModel> toPageModel(
      Iterable<ClassificationSummary> entities, PageMetadata pageMetadata) {
    return TaskanaPagingAssembler.super.toPageModel(entities, pageMetadata);
  }
}
