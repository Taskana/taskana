package pro.taskana.rest.resource.mapper;

import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.rest.resource.ClassificationResource;

@Component
public class ClassificationMapper {

    @Autowired ClassificationService classificationService;

    public ClassificationResource toResource(Classification classification) {
        ClassificationResource resource = new ClassificationResource();
        BeanUtils.copyProperties(classification, resource);
        //need to be set by hand, because they are named different, or have different types
        resource.setClassificationId(classification.getId());
        resource.setCreated(classification.getCreated().toString());
        return resource;
    }

    public Classification toModel(ClassificationResource classificationResource) {
        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification(
            classificationResource.domain, classificationResource.key, classificationResource.type);
        BeanUtils.copyProperties(classificationResource, classification);

        classification.setId(classificationResource.getClassificationId());
        classification.setCreated(Instant.parse(classificationResource.getCreated()));
        return classification;
    }
}
