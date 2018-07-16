package pro.taskana.rest.resource;

import org.springframework.hateoas.ResourceSupport;

/**
 * Resource class for {@link pro.taskana.Classification}.
 */
public class ClassificationResource extends ResourceSupport {

    public String classificationId;
    public String key;
    public String parentId;
    public String parentKey;
    public String category;
    public String type;
    public String domain;
    public Boolean isValidInDomain;
    public String created;      // ISO-8601
    public String modified;      // ISO-8601
    public String name;
    public String description;
    public int priority;
    public String serviceLevel; // PddDThhHmmM
    public String applicationEntryPoint;
    public String custom1;
    public String custom2;
    public String custom3;
    public String custom4;
    public String custom5;
    public String custom6;
    public String custom7;
    public String custom8;

    public Boolean getIsValidInDomain() {
        return isValidInDomain;
    }

    public void setIsValidInDomain(Boolean isValidInDomain) {
        this.isValidInDomain = isValidInDomain;
    }

    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
        this.classificationId = classificationId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public String getApplicationEntryPoint() {
        return applicationEntryPoint;
    }

    public void setApplicationEntryPoint(String applicationEntryPoint) {
        this.applicationEntryPoint = applicationEntryPoint;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public String getCustom5() {
        return custom5;
    }

    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    public String getCustom6() {
        return custom6;
    }

    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    public String getCustom7() {
        return custom7;
    }

    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    public String getCustom8() {
        return custom8;
    }

    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassificationResource [classificationId=");
        builder.append(classificationId);
        builder.append(", key=");
        builder.append(key);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", parentKey=");
        builder.append(parentKey);
        builder.append(", category=");
        builder.append(category);
        builder.append(", type=");
        builder.append(type);
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", isValidInDomain=");
        builder.append(isValidInDomain);
        builder.append(", created=");
        builder.append(created);
        builder.append(", modified=");
        builder.append(modified);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", priority=");
        builder.append(priority);
        builder.append(", serviceLevel=");
        builder.append(serviceLevel);
        builder.append(", applicationEntryPoint=");
        builder.append(applicationEntryPoint);
        builder.append(", custom1=");
        builder.append(custom1);
        builder.append(", custom2=");
        builder.append(custom2);
        builder.append(", custom3=");
        builder.append(custom3);
        builder.append(", custom4=");
        builder.append(custom4);
        builder.append(", custom5=");
        builder.append(custom5);
        builder.append(", custom6=");
        builder.append(custom6);
        builder.append(", custom7=");
        builder.append(custom7);
        builder.append(", custom8=");
        builder.append(custom8);
        builder.append("]");
        return builder.toString();
    }
}
