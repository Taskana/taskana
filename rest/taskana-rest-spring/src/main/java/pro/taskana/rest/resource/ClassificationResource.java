package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.api.models.Classification;

/** EntityModel class for {@link Classification}. */
public class ClassificationResource extends RepresentationModel<ClassificationResource> {

  @NotNull private String classificationId;
  @NotNull private String key;
  private String parentId;
  private String parentKey;
  private String category;
  private String type;
  private String domain;
  private Boolean isValidInDomain;
  private String created; // ISO-8601
  private String modified; // ISO-8601
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

  public ClassificationResource() {}

  public ClassificationResource(Classification classification) {
    this.classificationId = classification.getId();
    this.key = classification.getKey();
    this.parentId = classification.getParentId();
    this.parentKey = classification.getParentKey();
    this.category = classification.getCategory();
    this.type = classification.getType();
    this.domain = classification.getDomain();
    this.isValidInDomain = classification.getIsValidInDomain();
    this.created =
        classification.getCreated() != null ? classification.getCreated().toString() : null;
    this.modified =
        classification.getModified() != null ? classification.getModified().toString() : null;
    this.name = classification.getName();
    this.description = classification.getDescription();
    this.priority = classification.getPriority();
    this.serviceLevel = classification.getServiceLevel();
    this.applicationEntryPoint = classification.getApplicationEntryPoint();
    this.custom1 = classification.getCustom1();
    this.custom2 = classification.getCustom2();
    this.custom3 = classification.getCustom3();
    this.custom4 = classification.getCustom4();
    this.custom5 = classification.getCustom5();
    this.custom6 = classification.getCustom6();
    this.custom7 = classification.getCustom7();
    this.custom8 = classification.getCustom8();
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

  public Boolean getIsValidInDomain() {
    return isValidInDomain;
  }

  public void setIsValidInDomain(Boolean validInDomain) {
    isValidInDomain = validInDomain;
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
    return "ClassificationResource [classificationId="
        + classificationId
        + ", key="
        + key
        + ", parentId="
        + parentId
        + ", parentKey="
        + parentKey
        + ", category="
        + category
        + ", type="
        + type
        + ", domain="
        + domain
        + ", isValidInDomain="
        + isValidInDomain
        + ", created="
        + created
        + ", modified="
        + modified
        + ", name="
        + name
        + ", description="
        + description
        + ", priority="
        + priority
        + ", serviceLevel="
        + serviceLevel
        + ", applicationEntryPoint="
        + applicationEntryPoint
        + ", custom1="
        + custom1
        + ", custom2="
        + custom2
        + ", custom3="
        + custom3
        + ", custom4="
        + custom4
        + ", custom5="
        + custom5
        + ", custom6="
        + custom6
        + ", custom7="
        + custom7
        + ", custom8="
        + custom8
        + "]";
  }
}
