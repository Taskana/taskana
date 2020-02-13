package pro.taskana.workbasket.internal.models;

import java.time.Instant;
import java.util.Objects;

import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Workbasket entity. */
public class WorkbasketImpl extends WorkbasketSummaryImpl implements Workbasket {

  private Instant created;
  private Instant modified;

  public WorkbasketImpl() {}

  @Override
  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  @Override
  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
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
    return "WorkbasketImpl [id="
        + id
        + ", key="
        + key
        + ", created="
        + created
        + ", modified="
        + modified
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
