package pro.taskana.rest.resource.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.rest.resource.ClassificationResource;

@Component
public class ClassificationMapper {

    @Autowired ClassificationService classificationService;

    public ClassificationResource toResource(Classification classification) {
        return new ClassificationResource(
            classification.getId(),
            classification.getKey(),
            classification.getParentId(),
            classification.getCategory(),
            classification.getType(),
            classification.getDomain(),
            classification.getIsValidInDomain(),
            classification.getCreated().toString(),
            classification.getName(),
            classification.getDescription(),
            classification.getPriority(),
            classification.getServiceLevel(),
            classification.getApplicationEntryPoint(),
            classification.getCustom1(),
            classification.getCustom2(),
            classification.getCustom3(),
            classification.getCustom4(),
            classification.getCustom5(),
            classification.getCustom6(),
            classification.getCustom7(),
            classification.getCustom8());
    }

    public Classification toModel(ClassificationResource classificationResource) {
        Classification classification = classificationService.newClassification(classificationResource.domain,
            classificationResource.key, classificationResource.type);
        classification.setServiceLevel(classificationResource.serviceLevel);
        classification.setPriority(classificationResource.priority);
        classification.setParentId(classificationResource.parentId);
        classification.setName(classificationResource.name);
        classification.setIsValidInDomain(classificationResource.isValidInDomain);
        classification.setDescription(classificationResource.description);
        classification.setCustom1(classificationResource.custom1);
        classification.setCustom2(classificationResource.custom2);
        classification.setCustom3(classificationResource.custom3);
        classification.setCustom4(classificationResource.custom4);
        classification.setCustom5(classificationResource.custom5);
        classification.setCustom6(classificationResource.custom6);
        classification.setCustom7(classificationResource.custom7);
        classification.setCustom8(classificationResource.custom8);
        classification.setCategory(classificationResource.category);
        classification.setApplicationEntryPoint(classificationResource.applicationEntryPoint);
        return classification;
    }
}
