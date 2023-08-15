package pro.taskana.workbasket.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Workbasket entity. */
public class WorkbasketImpl extends WorkbasketSummaryImpl implements Workbasket {

  private Instant created;
  private Instant modified;

  public WorkbasketImpl() {}

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
    result.setCustom5(this.getCustom5());
    result.setCustom6(this.getCustom6());
    result.setCustom7(this.getCustom7());
    result.setCustom8(this.getCustom8());
    result.setOrgLevel1(this.getOrgLevel1());
    result.setOrgLevel2(this.getOrgLevel2());
    result.setOrgLevel3(this.getOrgLevel3());
    result.setOrgLevel4(this.getOrgLevel4());
    result.setMarkedForDeletion(this.isMarkedForDeletion());
    return result;
  }

  @Override
  protected boolean canEqual(Object other) {
    return (other instanceof WorkbasketImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        key,
        created,
        modified,
        name,
        description,
        owner,
        domain,
        type,
        custom1,
        custom2,
        custom3,
        custom4,
        custom5,
        custom6,
        custom7,
        custom8,
        orgLevel1,
        orgLevel2,
        orgLevel3,
        orgLevel4,
        markedForDeletion);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof WorkbasketImpl)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    WorkbasketImpl other = (WorkbasketImpl) obj;
    if (!other.canEqual(this)) {
      return false;
    }
    return Objects.equals(created, other.created) && Objects.equals(modified, other.modified);
  }

  @Override
  public String toString() {
    return "WorkbasketImpl [created="
        + created
        + ", modified="
        + modified
        + ", id="
        + id
        + ", key="
        + key
        + ", name="
        + name
        + ", description="
        + description
        + ", owner="
        + owner
        + ", domain="
        + domain
        + ", type="
        + type
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
        + ", orgLevel1="
        + orgLevel1
        + ", orgLevel2="
        + orgLevel2
        + ", orgLevel3="
        + orgLevel3
        + ", orgLevel4="
        + orgLevel4
        + ", markedForDeletion="
        + markedForDeletion
        + "]";
  }
}
