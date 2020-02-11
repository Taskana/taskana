package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.classification.api.Classification;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.internal.ClassificationImpl;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.ClassificationController;

/**
 * Transforms {@link Classification} to its resource counterpart {@link ClassificationResource} and
 * vice versa.
 */
@Component
public class ClassificationResourceAssembler
    extends ResourceAssemblerSupport<Classification, ClassificationResource> {

  final ClassificationService classificationService;

  @Autowired
  public ClassificationResourceAssembler(ClassificationService classificationService) {
    super(ClassificationController.class, ClassificationResource.class);
    this.classificationService = classificationService;
  }

  public ClassificationResource toResource(Classification classification) {
    ClassificationResource resource = new ClassificationResource(classification);
    try {
      resource.add(
          linkTo(methodOn(ClassificationController.class).getClassification(classification.getId()))
              .withSelfRel());
    } catch (ClassificationNotFoundException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return resource;
  }

  public ClassificationResource toDefinition(Classification classification) {
    ClassificationResource resource = new ClassificationResource(classification);
    resource.add(
        linkTo(ClassificationController.class).slash(classification.getId()).withSelfRel());
    return resource;
  }

  public Classification toModel(ClassificationResource classificationResource) {
    ClassificationImpl classification =
        (ClassificationImpl)
            classificationService.newClassification(
                classificationResource.getDomain(),
                classificationResource.getKey(),
                classificationResource.getType());
    BeanUtils.copyProperties(classificationResource, classification);

    classification.setId(classificationResource.getClassificationId());
    if (classificationResource.getCreated() != null) {
      classification.setCreated(Instant.parse(classificationResource.getCreated()));
    }
    if (classificationResource.getModified() != null) {
      classification.setModified(Instant.parse(classificationResource.getModified()));
    }
    return classification;
  }
}
