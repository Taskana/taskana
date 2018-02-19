package pro.taskana.rest.resource.mapper;

import pro.taskana.Classification;
import pro.taskana.rest.resource.ClassificationResource;

public class ClassificationMapper {

    public ClassificationResource toResource(Classification classification) {
        return new ClassificationResource(
            classification.getId(),
            classification.getKey(),
            classification.getParentId(),
            classification.getCategory(),
            classification.getType(),
            classification.getDomain(),
            classification.getIsValidInDomain(),
            classification.getCreated(),
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
            classification.getCustom8()
        );
    }
}
