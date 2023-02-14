package pro.taskana.workbasket.internal.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** This entity contains the most important information about a workbasket. */
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class WorkbasketSummaryImpl implements WorkbasketSummary {

  protected String id;
  protected String key;
  protected String name;
  protected String description;
  protected String owner;
  protected String domain;
  @Setter protected WorkbasketType type;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String orgLevel1;
  protected String orgLevel2;
  protected String orgLevel3;
  protected String orgLevel4;
  @Setter protected boolean markedForDeletion;

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

  public void setId(String id) {
    this.id = id;
  }

  public void setKey(String key) {
    this.key = key == null ? null : key.trim();
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public void setOwner(String owner) {
    this.owner = owner == null ? null : owner.trim();
  }

  public void setDomain(String domain) {
    this.domain = domain == null ? null : domain.trim();
  }

  @Deprecated
  @Override
  public String getCustomAttribute(WorkbasketCustomField customField) {
    return getCustomField(customField);
  }

  @Override
  public String getCustomField(WorkbasketCustomField customField) {
    switch (customField) {
      case CUSTOM_1:
        return custom1;
      case CUSTOM_2:
        return custom2;
      case CUSTOM_3:
        return custom3;
      case CUSTOM_4:
        return custom4;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1 == null ? null : orgLevel1.trim();
  }

  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2 == null ? null : orgLevel2.trim();
  }

  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3 == null ? null : orgLevel3.trim();
  }

  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4 == null ? null : orgLevel4.trim();
  }

  @Override
  public WorkbasketSummaryImpl copy() {
    return new WorkbasketSummaryImpl(this);
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
}
