package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Resource class for {@link WorkbasketSummary}. */
@Relation(collectionRelation = "workbaskets")
public class WorkbasketSummaryResource extends ResourceSupport {

  private String workbasketId;

  @NotNull private String key;

  @NotNull private String name;

  @NotNull private String domain;

  @NotNull private WorkbasketType type;

  private String description;
  private String owner;
  private String custom1;
  private String custom2;
  private String custom3;
  private String custom4;
  private String orgLevel1;
  private String orgLevel2;
  private String orgLevel3;
  private String orgLevel4;
  private boolean markedForDeletion;

  public WorkbasketSummaryResource() {}

  public WorkbasketSummaryResource(WorkbasketSummary workbasketSummary) {
    this.workbasketId = workbasketSummary.getId();
    this.key = workbasketSummary.getKey();
    this.name = workbasketSummary.getName();
    this.domain = workbasketSummary.getDomain();
    this.type = workbasketSummary.getType();
    this.description = workbasketSummary.getDescription();
    this.owner = workbasketSummary.getOwner();
    this.markedForDeletion = workbasketSummary.isMarkedForDeletion();
    this.custom1 = workbasketSummary.getCustom1();
    this.custom2 = workbasketSummary.getCustom2();
    this.custom3 = workbasketSummary.getCustom3();
    this.custom4 = workbasketSummary.getCustom4();
    this.orgLevel1 = workbasketSummary.getOrgLevel1();
    this.orgLevel2 = workbasketSummary.getOrgLevel2();
    this.orgLevel3 = workbasketSummary.getOrgLevel3();
    this.orgLevel4 = workbasketSummary.getOrgLevel4();
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

  public boolean getMarkedForDeletion() {
    return markedForDeletion;
  }

  public void setMarkedForDeletion(boolean markedForDeletion) {
    this.markedForDeletion = markedForDeletion;
  }

  @Override
  public String toString() {
    return "WorkbasketSummaryResource ["
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
