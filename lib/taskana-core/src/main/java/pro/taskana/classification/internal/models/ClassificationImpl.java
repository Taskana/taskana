package pro.taskana.classification.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.SystemException;

/** Classification entity. */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClassificationImpl extends ClassificationSummaryImpl implements Classification {

  @Getter @Setter private Boolean isValidInDomain;
  private Instant created;
  private Instant modified;
  @Getter private String description;

  private ClassificationImpl(ClassificationImpl copyFrom, String key) {
    super(copyFrom);
    isValidInDomain = copyFrom.isValidInDomain;
    created = copyFrom.created;
    modified = copyFrom.modified;
    description = copyFrom.description;
    this.key = key;
  }

  @Override
  public ClassificationImpl copy(String key) {
    return new ClassificationImpl(this, key);
  }

  @Override
  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  @Deprecated
  @Override
  public void setCustomAttribute(ClassificationCustomField customField, String value) {
    setCustomField(customField, value);
  }

  @Override
  public void setCustomField(ClassificationCustomField customField, String value) {
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

  @Override
  public ClassificationSummary asSummary() {
    ClassificationSummaryImpl summary = new ClassificationSummaryImpl();
    summary.setCategory(this.category);
    summary.setDomain(this.domain);
    summary.setId(this.id);
    summary.setKey(this.key);
    summary.setName(this.name);
    summary.setType(this.type);
    summary.setParentId(this.parentId);
    summary.setParentKey(this.parentKey);
    summary.setPriority(this.priority);
    summary.setServiceLevel(this.serviceLevel);
    summary.setApplicationEntryPoint(this.applicationEntryPoint);
    summary.setCustom1(custom1);
    summary.setCustom2(custom2);
    summary.setCustom3(custom3);
    summary.setCustom4(custom4);
    summary.setCustom5(custom5);
    summary.setCustom6(custom6);
    summary.setCustom7(custom7);
    summary.setCustom8(custom8);
    return summary;
  }
}
