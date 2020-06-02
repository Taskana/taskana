package pro.taskana.classification.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.classification.rest.ClassificationController;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.assembler.TaskanaPagingAssembler;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;

/**
 * Transforms {@link Classification} to its resource counterpart {@link
 * ClassificationRepresentationModel} and vice versa.
 */
@Component
public class ClassificationRepresentationModelAssembler
    implements TaskanaPagingAssembler<Classification, ClassificationRepresentationModel> {

  final ClassificationService classificationService;

  @Autowired
  public ClassificationRepresentationModelAssembler(ClassificationService classificationService) {
    this.classificationService = classificationService;
  }

  @NonNull
  @Override
  public ClassificationRepresentationModel toModel(@NonNull Classification classification) {
    ClassificationRepresentationModel repModel =
        new ClassificationRepresentationModel();
    repModel.setClassificationId(classification.getId());
    repModel.setApplicationEntryPoint(classification.getApplicationEntryPoint());
    repModel.setCategory(classification.getCategory());
    repModel.setDomain(classification.getDomain());
    repModel.setKey(classification.getKey());
    repModel.setName(classification.getName());
    repModel.setParentId(classification.getParentId());
    repModel.setParentKey(classification.getParentKey());
    repModel.setPriority(classification.getPriority());
    repModel.setServiceLevel(classification.getServiceLevel());
    repModel.setType(classification.getType());
    repModel.setCustom1(classification.getCustom1());
    repModel.setCustom2(classification.getCustom2());
    repModel.setCustom3(classification.getCustom3());
    repModel.setCustom4(classification.getCustom4());
    repModel.setCustom5(classification.getCustom5());
    repModel.setCustom6(classification.getCustom6());
    repModel.setCustom7(classification.getCustom7());
    repModel.setCustom8(classification.getCustom8());
    repModel.setIsValidInDomain(classification.getIsValidInDomain());
    repModel.setCreated(classification.getCreated());
    repModel.setModified(classification.getModified());
    repModel.setDescription(classification.getDescription());
    try {
      repModel.add(
          WebMvcLinkBuilder.linkTo(
              methodOn(ClassificationController.class)
                  .getClassification(classification.getId()))
              .withSelfRel());
    } catch (ClassificationNotFoundException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return repModel;
  }

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return TaskanaPagedModelKeys.CLASSIFICATIONS;
  }

  public Classification toEntityModel(
      ClassificationRepresentationModel repModel) {
    ClassificationImpl classification =
        (ClassificationImpl)
            classificationService.newClassification(
                repModel.getKey(),
                repModel.getDomain(),
                repModel.getType());

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
    classification.setIsValidInDomain(repModel.getIsValidInDomain());
    classification.setDescription(repModel.getDescription());
    classification.setId(repModel.getClassificationId());
    classification.setCreated(repModel.getCreated());
    classification.setModified(repModel.getModified());
    return classification;
  }
}
