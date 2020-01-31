package pro.taskana.workbasket.internal;

import java.time.Instant;
import java.util.Objects;

import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketSummary;
import pro.taskana.workbasket.api.WorkbasketType;

/** Workbasket entity. */
public class WorkbasketImpl implements Workbasket {

  private String id;
  private String key;
  private Instant created;
  private Instant modified;
  private String name;
  private String description;
  private String owner;
  private String domain;
  private WorkbasketType type;
  private String custom1;
  private String custom2;
  private String custom3;
  private String custom4;
  private String orgLevel1;
  private String orgLevel2;
  private String orgLevel3;
  private String orgLevel4;
  private boolean markedForDeletion;

  public WorkbasketImpl() {}

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public Instant getCreated() {
    return created;
  }

  @Override
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  @Override
  public WorkbasketType getType() {
    return type;
  }

  @Override
  public void setType(WorkbasketType type) {
    this.type = type;
  }

  @Override
  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
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
  public String getOwner() {
    return owner;
  }

  @Override
  public void setOwner(String owner) {
    this.owner = owner;
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
  public String getOrgLevel1() {
    return orgLevel1;
  }

  @Override
  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1;
  }

  @Override
  public String getOrgLevel2() {
    return orgLevel2;
  }

  @Override
  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2;
  }

  @Override
  public String getOrgLevel3() {
    return orgLevel3;
  }

  @Override
  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3;
  }

  @Override
  public String getOrgLevel4() {
    return orgLevel4;
  }

  @Override
  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4;
  }

  @Override
  public boolean isMarkedForDeletion() {
    return markedForDeletion;
  }

  @Override
  public void setMarkedForDeletion(boolean markedForDeletion) {
    this.markedForDeletion = markedForDeletion;
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

  public void setCreated(Instant created) {
    this.created = created;
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
    WorkbasketImpl other = (WorkbasketImpl) obj;
    return markedForDeletion == other.markedForDeletion
        && Objects.equals(id, other.id)
        && Objects.equals(key, other.key)
        && Objects.equals(created, other.created)
        && Objects.equals(modified, other.modified)
        && Objects.equals(name, other.name)
        && Objects.equals(description, other.description)
        && Objects.equals(owner, other.owner)
        && Objects.equals(domain, other.domain)
        && type == other.type
        && Objects.equals(custom1, other.custom1)
        && Objects.equals(custom2, other.custom2)
        && Objects.equals(custom3, other.custom3)
        && Objects.equals(custom4, other.custom4)
        && Objects.equals(orgLevel1, other.orgLevel1)
        && Objects.equals(orgLevel2, other.orgLevel2)
        && Objects.equals(orgLevel3, other.orgLevel3)
        && Objects.equals(orgLevel4, other.orgLevel4);
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
