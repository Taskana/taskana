package pro.taskana.workbasket.internal.models;

import java.util.Objects;

import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** This entity contains the most important information about a workbasket. */
public class WorkbasketSummaryImpl implements WorkbasketSummary {

  protected String id;
  protected String key;
  protected String name;
  protected String description;
  protected String owner;
  protected String domain;
  protected WorkbasketType type;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String orgLevel1;
  protected String orgLevel2;
  protected String orgLevel3;
  protected String orgLevel4;
  protected boolean markedForDeletion;

  public WorkbasketSummaryImpl() {}

  protected WorkbasketSummaryImpl(WorkbasketSummaryImpl copyFrom) {
    name = copyFrom.name;
    description = copyFrom.description;
    owner = copyFrom.owner;
    domain = copyFrom.domain;
    type = copyFrom.type;
    custom1 = copyFrom.custom1;
    custom2 = copyFrom.custom2;
    custom3 = copyFrom.custom3;
    custom4 = copyFrom.custom4;
    orgLevel1 = copyFrom.orgLevel1;
    orgLevel2 = copyFrom.orgLevel2;
    orgLevel3 = copyFrom.orgLevel3;
    orgLevel4 = copyFrom.orgLevel4;
    markedForDeletion = copyFrom.markedForDeletion;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getId()
   */
  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getKey()
   */
  @Override
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getDescription()
   */
  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getOwner()
   */
  @Override
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getDomain()
   */
  @Override
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getType()
   */
  @Override
  public WorkbasketType getType() {
    return type;
  }

  public void setType(WorkbasketType type) {
    this.type = type;
  }

  /*
   *  (non-Javadoc)
   *  @see pro.taskana.impl.WorkbasketSummary#getCustom1()
   */
  @Override
  public String getCustom1() {
    return custom1;
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1;
  }

  /*
   *  (non-Javadoc)
   *  @see pro.taskana.impl.WorkbasketSummary#getCustom2()
   */
  @Override
  public String getCustom2() {
    return custom2;
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2;
  }

  /*
   *  (non-Javadoc)
   *  @see pro.taskana.impl.WorkbasketSummary#getCustom3()
   */
  @Override
  public String getCustom3() {
    return custom3;
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3;
  }

  /*
   *  (non-Javadoc)
   *  @see pro.taskana.impl.WorkbasketSummary#getCustom4()
   */
  @Override
  public String getCustom4() {
    return custom4;
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel1()
   */
  @Override
  public String getOrgLevel1() {
    return orgLevel1;
  }

  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel2()
   */
  @Override
  public String getOrgLevel2() {
    return orgLevel2;
  }

  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel3()
   */
  @Override
  public String getOrgLevel3() {
    return orgLevel3;
  }

  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel4()
   */
  @Override
  public String getOrgLevel4() {
    return orgLevel4;
  }

  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4;
  }

  @Override
  public boolean isMarkedForDeletion() {
    return markedForDeletion;
  }

  public void setMarkedForDeletion(boolean markedForDeletion) {
    this.markedForDeletion = markedForDeletion;
  }

  @Override
  public WorkbasketSummaryImpl copy() {
    return new WorkbasketSummaryImpl(this);
  }

  protected boolean canEqual(Object other) {
    return (other instanceof WorkbasketSummaryImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        key,
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
    if (!(obj instanceof WorkbasketSummaryImpl)) {
      return false;
    }
    WorkbasketSummaryImpl other = (WorkbasketSummaryImpl) obj;
    if (!other.canEqual(this)) {
      return false;
    }
    return markedForDeletion == other.markedForDeletion
        && Objects.equals(id, other.id)
        && Objects.equals(key, other.key)
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
    return "WorkbasketSummaryImpl [id="
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
