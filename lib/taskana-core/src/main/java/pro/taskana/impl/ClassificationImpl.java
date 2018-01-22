package pro.taskana.impl;

import java.time.Duration;
import java.time.Instant;

import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;

/**
 * Classification entity.
 */
public class ClassificationImpl implements Classification {

    private String id;
    private String key;
    private String parentClassificationKey;
    private String category;
    private String type;
    private String domain;
    private Boolean isValidInDomain;
    private Instant created;
    private String name;
    private String description;
    private int priority;
    private String serviceLevel; // PddDThhHmmM
    private String applicationEntryPoint;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;
    private String custom6;
    private String custom7;
    private String custom8;

    ClassificationImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getParentClassificationKey() {
        return parentClassificationKey;
    }

    @Override
    public void setParentClassificationKey(String parentClassificationKey) {
        this.parentClassificationKey = parentClassificationKey;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public Boolean getIsValidInDomain() {
        return isValidInDomain;
    }

    @Override
    public void setIsValidInDomain(Boolean isValidInDomain) {
        this.isValidInDomain = isValidInDomain;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String getServiceLevel() {
        return serviceLevel;
    }

    @Override
    public void setServiceLevel(String serviceLevel) {
        try {
            Duration.parse(serviceLevel);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid duration. Please use the format defined by ISO 8601");
        }
        this.serviceLevel = serviceLevel;
    }

    @Override
    public String getApplicationEntryPoint() {
        return applicationEntryPoint;
    }

    @Override
    public void setApplicationEntryPoint(String applicationEntryPoint) {
        this.applicationEntryPoint = applicationEntryPoint;
    }

    @Override
    public String getCustom1() {
        return custom1;
    }

    @Override
    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    @Override
    public String getCustom2() {
        return custom2;
    }

    @Override
    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    @Override
    public String getCustom3() {
        return custom3;
    }

    @Override
    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    @Override
    public String getCustom4() {
        return custom4;
    }

    @Override
    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    @Override
    public String getCustom5() {
        return custom5;
    }

    @Override
    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    @Override
    public String getCustom6() {
        return custom6;
    }

    @Override
    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    @Override
    public String getCustom7() {
        return custom7;
    }

    @Override
    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    @Override
    public String getCustom8() {
        return custom8;
    }

    @Override
    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    @Override
    public ClassificationSummary asSummary() {
        ClassificationSummaryImpl summary = new ClassificationSummaryImpl();
        summary.setCategory(this.category);
        summary.setDomain(this.domain);
        summary.setId(this.id);
        summary.setKey(this.key);
        summary.setName(this.name);
        summary.setType(this.type);
        return summary;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Classification [id=");
        builder.append(id);
        builder.append(", key=");
        builder.append(key);
        builder.append(", parentClassificationId=");
        builder.append(parentClassificationKey);
        builder.append(", category=");
        builder.append(category);
        builder.append(", type=");
        builder.append(type);
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", isValidInDomain=");
        builder.append(isValidInDomain);
        builder.append(", created=");
        builder.append(created.toString());
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
