package pro.taskana.rest.resource;

import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.api.models.ClassificationSummary;

/** EntityModel class for {@link ClassificationSummary}. */
public class ClassificationSummaryResource
    extends RepresentationModel<ClassificationSummaryResource> {

  private String classificationId;
  private String applicationEntryPoint;
  private String category;
  private String domain;
  private String key;
  private String name;
  private String parentId;
  private String parentKey;
  private int priority;
  private String serviceLevel;
  private String type;
  private String custom1;
  private String custom2;
  private String custom3;
  private String custom4;
  private String custom5;
  private String custom6;
  private String custom7;
  private String custom8;

  public ClassificationSummaryResource() {}

  public ClassificationSummaryResource(ClassificationSummary classification) {
    classificationId = classification.getId();
    applicationEntryPoint = classification.getApplicationEntryPoint();
    category = classification.getCategory();
    domain = classification.getDomain();
    key = classification.getKey();
    name = classification.getName();
    parentId = classification.getParentId();
    parentKey = classification.getParentKey();
    priority = classification.getPriority();
    serviceLevel = classification.getServiceLevel();
    type = classification.getType();
    custom1 = classification.getCustom1();
    custom2 = classification.getCustom2();
    custom3 = classification.getCustom3();
    custom4 = classification.getCustom4();
    custom5 = classification.getCustom5();
    custom6 = classification.getCustom6();
    custom7 = classification.getCustom7();
    custom8 = classification.getCustom8();
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public String getApplicationEntryPoint() {
    return applicationEntryPoint;
  }

  public void setApplicationEntryPoint(String applicationEntryPoint) {
    this.applicationEntryPoint = applicationEntryPoint;
  }

  public String getServiceLevel() {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel) {
    this.serviceLevel = serviceLevel;
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
    return "ClassificationSummaryResource ["
        + "classificationId="
        + this.classificationId
        + ", applicationEntryPoint="
        + this.applicationEntryPoint
        + ", category="
        + this.category
        + ", domain="
        + this.domain
        + ", key="
        + this.key
        + ", name="
        + this.name
        + ", parentId="
        + this.parentId
        + ", parentKey="
        + this.parentKey
        + ", priority="
        + this.priority
        + ", serviceLevel="
        + this.serviceLevel
        + ", type="
        + this.type
        + ", custom1="
        + this.custom1
        + ", custom2="
        + this.custom2
        + ", custom3="
        + this.custom3
        + ", custom4="
        + this.custom4
        + ", custom5="
        + this.custom5
        + ", custom6="
        + this.custom6
        + ", custom7="
        + this.custom7
        + ", custom8="
        + this.custom8
        + "]";
  }
}
