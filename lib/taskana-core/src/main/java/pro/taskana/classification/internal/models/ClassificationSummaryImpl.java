package pro.taskana.classification.internal.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.SystemException;

/** Implementation for the short summaries of a classification entity. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ClassificationSummaryImpl implements ClassificationSummary {

  protected String id;

  @Setter(AccessLevel.NONE)
  protected String applicationEntryPoint;

  protected String category;

  @Setter(AccessLevel.NONE)
  protected String domain;

  @Setter(AccessLevel.NONE)
  protected String key;

  @Setter(AccessLevel.NONE)
  protected String name;

  protected String parentId;
  protected String parentKey;
  protected int priority;
  protected String serviceLevel; // PddDThhHmmM
  protected String type;

  @Setter(AccessLevel.NONE)
  protected String custom1;

  @Setter(AccessLevel.NONE)
  protected String custom2;

  @Setter(AccessLevel.NONE)
  protected String custom3;

  @Setter(AccessLevel.NONE)
  protected String custom4;

  @Setter(AccessLevel.NONE)
  protected String custom5;

  @Setter(AccessLevel.NONE)
  protected String custom6;

  @Setter(AccessLevel.NONE)
  protected String custom7;

  @Setter(AccessLevel.NONE)
  protected String custom8;

  protected ClassificationSummaryImpl(ClassificationSummaryImpl copyFrom) {
    applicationEntryPoint = copyFrom.applicationEntryPoint;
    category = copyFrom.category;
    domain = copyFrom.domain;
    name = copyFrom.name;
    parentId = copyFrom.parentId;
    parentKey = copyFrom.parentKey;
    priority = copyFrom.priority;
    serviceLevel = copyFrom.serviceLevel;
    type = copyFrom.type;
    custom1 = copyFrom.custom1;
    custom2 = copyFrom.custom2;
    custom3 = copyFrom.custom3;
    custom4 = copyFrom.custom4;
    custom5 = copyFrom.custom5;
    custom6 = copyFrom.custom6;
    custom7 = copyFrom.custom7;
    custom8 = copyFrom.custom8;
  }

  public void setKey(String key) {
    this.key = key == null ? null : key.trim();
  }

  public void setDomain(String domain) {
    this.domain = domain == null ? null : domain.trim();
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public void setApplicationEntryPoint(String applicationEntryPoint) {
    this.applicationEntryPoint =
        applicationEntryPoint == null ? null : applicationEntryPoint.trim();
  }

  @Deprecated
  @Override
  public String getCustomAttribute(ClassificationCustomField customField) {
    return getCustomField(customField);
  }

  @Override
  public String getCustomField(ClassificationCustomField customField) {
    switch (customField) {
      case CUSTOM_1:
        return custom1;
      case CUSTOM_2:
        return custom2;
      case CUSTOM_3:
        return custom3;
      case CUSTOM_4:
        return custom4;
      case CUSTOM_5:
        return custom5;
      case CUSTOM_6:
        return custom6;
      case CUSTOM_7:
        return custom7;
      case CUSTOM_8:
        return custom8;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  @Override
  public ClassificationSummaryImpl copy() {
    return new ClassificationSummaryImpl(this);
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1 == null ? null : custom1.trim();
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2 == null ? null : custom2.trim();
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3 == null ? null : custom3.trim();
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4 == null ? null : custom4.trim();
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5 == null ? null : custom5.trim();
  }

  public void setCustom6(String custom6) {
    this.custom6 = custom6 == null ? null : custom6.trim();
  }

  public void setCustom7(String custom7) {
    this.custom7 = custom7 == null ? null : custom7.trim();
  }

  public void setCustom8(String custom8) {
    this.custom8 = custom8 == null ? null : custom8.trim();
  }
}
