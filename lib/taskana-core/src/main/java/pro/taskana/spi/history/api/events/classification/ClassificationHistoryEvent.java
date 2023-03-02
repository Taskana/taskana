package pro.taskana.spi.history.api.events.classification;

import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.SystemException;

/** Super class for all classification related events. */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClassificationHistoryEvent {

  protected String id;
  protected String eventType;
  protected Instant created;
  protected String userId;
  protected String classificationId;
  protected String applicationEntryPoint;
  protected String category;
  protected String domain;
  protected String key;
  protected String name;
  protected String parentId;
  protected String parentKey;
  protected int priority;
  protected String serviceLevel;
  protected String type;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String custom5;
  protected String custom6;
  protected String custom7;
  protected String custom8;
  protected String details;

  public ClassificationHistoryEvent(
      String id, ClassificationSummary classification, String userId, String details) {
    this.id = id;
    this.userId = userId;
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
    custom1 = classification.getCustomField(ClassificationCustomField.CUSTOM_1);
    custom2 = classification.getCustomField(ClassificationCustomField.CUSTOM_2);
    custom3 = classification.getCustomField(ClassificationCustomField.CUSTOM_3);
    custom4 = classification.getCustomField(ClassificationCustomField.CUSTOM_4);
    custom5 = classification.getCustomField(ClassificationCustomField.CUSTOM_5);
    custom6 = classification.getCustomField(ClassificationCustomField.CUSTOM_6);
    custom7 = classification.getCustomField(ClassificationCustomField.CUSTOM_7);
    custom8 = classification.getCustomField(ClassificationCustomField.CUSTOM_8);
    this.details = details;
  }

  public void setCustomAttribute(ClassificationCustomField customField, String value) {
    switch (customField) {
      case CUSTOM_1:
        custom1 = value;
        break;
      case CUSTOM_2:
        custom2 = value;
        break;
      case CUSTOM_3:
        custom3 = value;
        break;
      case CUSTOM_4:
        custom4 = value;
        break;
      case CUSTOM_5:
        custom5 = value;
        break;
      case CUSTOM_6:
        custom6 = value;
        break;
      case CUSTOM_7:
        custom7 = value;
        break;
      case CUSTOM_8:
        custom8 = value;
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  public String getCustomAttribute(ClassificationCustomField customField) {
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
}
