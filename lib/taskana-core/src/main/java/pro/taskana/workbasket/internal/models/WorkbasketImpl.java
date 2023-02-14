package pro.taskana.workbasket.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Workbasket entity. */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WorkbasketImpl extends WorkbasketSummaryImpl implements Workbasket {

  private Instant created;
  private Instant modified;

  private WorkbasketImpl(WorkbasketImpl copyFrom, String key) {
    super(copyFrom);
    created = copyFrom.created;
    modified = copyFrom.modified;
    this.key = key == null ? null : key.trim();
  }

  @Deprecated
  @Override
  public void setCustomAttribute(WorkbasketCustomField customField, String value) {
    setCustomField(customField, value);
  }

  @Override
  public void setCustomField(WorkbasketCustomField customField, String value) {
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
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  @Override
  public WorkbasketImpl copy(String key) {
    return new WorkbasketImpl(this, key);
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
  public WorkbasketSummary asSummary() {
    WorkbasketSummaryImpl result = new WorkbasketSummaryImpl();
    result.setId(this.getId());
    result.setKey(this.getKey());
    result.setName(this.getName());
    result.setDescription(this.getDescription());
    result.setOwner(this.getOwner());
    result.setDomain(this.getDomain());
    result.setType(this.getType());
    result.setCustom1(this.getCustom1());
    result.setCustom2(this.getCustom2());
    result.setCustom3(this.getCustom3());
    result.setCustom4(this.getCustom4());
    result.setOrgLevel1(this.getOrgLevel1());
    result.setOrgLevel2(this.getOrgLevel2());
    result.setOrgLevel3(this.getOrgLevel3());
    result.setOrgLevel4(this.getOrgLevel4());
    result.setMarkedForDeletion(this.isMarkedForDeletion());
    return result;
  }
}
