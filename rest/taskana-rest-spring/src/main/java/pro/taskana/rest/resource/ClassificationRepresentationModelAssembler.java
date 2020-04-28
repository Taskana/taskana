package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.Instant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.ClassificationController;

/**
 * Transforms {@link Classification} to its resource counterpart {@link
 * ClassificationRepresentationModel} and vice versa.
 */
@Component
public class ClassificationRepresentationModelAssembler
    implements RepresentationModelAssembler<Classification, ClassificationRepresentationModel> {

  final ClassificationService classificationService;

  @Autowired
  public ClassificationRepresentationModelAssembler(
      ClassificationService classificationService) {
    this.classificationService = classificationService;
  }

  @NonNull
  @Override
  public ClassificationRepresentationModel toModel(@NonNull Classification classification) {
    ClassificationRepresentationModel resource =
        new ClassificationRepresentationModel(classification);
    try {
      resource.add(
          linkTo(methodOn(ClassificationController.class).getClassification(classification.getId()))
              .withSelfRel());
    } catch (ClassificationNotFoundException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return resource;
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
    if (classificationRepresentationModel.getCreated() != null) {
      classification.setCreated(Instant.parse(classificationRepresentationModel.getCreated()));
    }
    if (classificationRepresentationModel.getModified() != null) {
      classification.setModified(Instant.parse(classificationRepresentationModel.getModified()));
    }
    return classification;
  }
}
