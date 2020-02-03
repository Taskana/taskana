package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;
import org.springframework.hateoas.ResourceSupport;

import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketType;

/** Resource class for {@link Workbasket}. */
public class WorkbasketResource extends ResourceSupport {

  public String workbasketId;
  @NotNull public String key;
  @NotNull public String name;
  @NotNull public String domain;
  @NotNull public WorkbasketType type;
  public String created; // ISO-8601
  public String modified; // ISO-8601
  public String description;
  public String owner;
  public String custom1;
  public String custom2;
  public String custom3;
  public String custom4;
  public String orgLevel1;
  public String orgLevel2;
  public String orgLevel3;
  public String orgLevel4;

  public WorkbasketResource() {}

  public WorkbasketResource(Workbasket workbasket) {
    this.workbasketId = workbasket.getId();
    this.key = workbasket.getKey();
    this.name = workbasket.getName();
    this.domain = workbasket.getDomain();
    this.type = workbasket.getType();
    this.created = workbasket.getCreated() != null ? workbasket.getCreated().toString() : null;
    this.modified = workbasket.getModified() != null ? workbasket.getModified().toString() : null;
    this.description = workbasket.getDescription();
    this.owner = workbasket.getOwner();
    this.custom1 = workbasket.getCustom1();
    this.custom2 = workbasket.getCustom2();
    this.custom3 = workbasket.getCustom3();
    this.custom4 = workbasket.getCustom4();
    this.orgLevel1 = workbasket.getOrgLevel1();
    this.orgLevel2 = workbasket.getOrgLevel2();
    this.orgLevel3 = workbasket.getOrgLevel3();
    this.orgLevel4 = workbasket.getOrgLevel4();
  }

  public String getWorkbasketId() {
    return workbasketId;
  }

  public void setWorkbasketId(String workbasketId) {
    this.workbasketId = workbasketId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public WorkbasketType getType() {
    return type;
  }

  public void setType(WorkbasketType type) {
    this.type = type;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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

  public String getOrgLevel1() {
    return orgLevel1;
  }

  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1;
  }

  public String getOrgLevel2() {
    return orgLevel2;
  }

  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2;
  }

  public String getOrgLevel3() {
    return orgLevel3;
  }

  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3;
  }

  public String getOrgLevel4() {
    return orgLevel4;
  }

  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4;
  }

  @Override
  public String toString() {
    return "WorkbasketResource ["
        + "workbasketId= "
        + this.workbasketId
        + "key= "
        + this.key
        + "name= "
        + this.name
        + "domain= "
        + this.domain
        + "type= "
        + this.type
        + "owner= "
        + this.owner
        + "]";
  }
}
