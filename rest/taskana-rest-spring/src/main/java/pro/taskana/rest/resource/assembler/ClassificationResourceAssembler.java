package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.ClassificationController;
import pro.taskana.rest.resource.ClassificationResource;

/**
 * Transforms {@link Classification} to its resource counterpart {@link ClassificationResource} and vice versa.
 */
@Component
public class ClassificationResourceAssembler {

    @Autowired
    ClassificationService classificationService;

    public ClassificationResource toResource(Classification classification) throws ClassificationNotFoundException,
        NotAuthorizedException, ClassificationAlreadyExistException, ConcurrencyException, DomainNotFoundException,
        InvalidArgumentException {
        ClassificationResource resource = new ClassificationResource();
        BeanUtils.copyProperties(classification, resource);
        // need to be set by hand, because they are named different, or have different types
        resource.setClassificationId(classification.getId());
        resource.setCreated(classification.getCreated().toString());
        resource.setModified(classification.getModified().toString());
        return addLinks(resource, classification);
    }

    public Classification toModel(ClassificationResource classificationResource) {
        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification(
            classificationResource.domain, classificationResource.key, classificationResource.type);
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

    private ClassificationResource addLinks(ClassificationResource resource, Classification classification)
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException,
        ConcurrencyException, DomainNotFoundException, InvalidArgumentException {
        resource.add(
            linkTo(methodOn(ClassificationController.class).getClassification(classification.getId()))
                .withSelfRel());
        return resource;
    }
}
