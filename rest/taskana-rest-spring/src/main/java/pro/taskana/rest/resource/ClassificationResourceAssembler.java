package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.time.Instant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.ClassificationController;

/**
 * Transforms {@link Classification} to its resource counterpart {@link ClassificationResource} and
 * vice versa.
 */
@Component
public class ClassificationResourceAssembler
    extends ResourceAssemblerSupport<Classification, ClassificationResource> {

  @Autowired ClassificationService classificationService;

  public ClassificationResourceAssembler() {
    super(ClassificationController.class, ClassificationResource.class);
  }

  public ClassificationResource toResource(Classification classification) {
    ClassificationResource resource = new ClassificationResource(classification);
    resource.add(
        linkTo(ClassificationController.class).slash(classification.getId()).withSelfRel());
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
                classificationResource.domain,
                classificationResource.key,
                classificationResource.type);
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
