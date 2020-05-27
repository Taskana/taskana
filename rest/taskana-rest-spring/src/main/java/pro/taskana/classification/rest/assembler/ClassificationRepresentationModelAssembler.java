package pro.taskana.classification.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
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
    ClassificationRepresentationModel resource =
        new ClassificationRepresentationModel(classification);
    try {
      resource.add(
          WebMvcLinkBuilder.linkTo(
                  methodOn(ClassificationController.class)
                      .getClassification(classification.getId()))
              .withSelfRel());
    } catch (ClassificationNotFoundException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return resource;
  }

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return TaskanaPagedModelKeys.CLASSIFICATIONS;
  }

  public Classification toEntityModel(
      ClassificationRepresentationModel classificationRepresentationModel) {
    ClassificationImpl classification =
        (ClassificationImpl)
            classificationService.newClassification(
                classificationRepresentationModel.getKey(),
                classificationRepresentationModel.getDomain(),
                classificationRepresentationModel.getType());
    BeanUtils.copyProperties(classificationRepresentationModel, classification);

    classification.setId(classificationRepresentationModel.getClassificationId());
    classification.setCreated(classificationRepresentationModel.getCreated());
    classification.setModified(classificationRepresentationModel.getModified());
    return classification;
  }
}
