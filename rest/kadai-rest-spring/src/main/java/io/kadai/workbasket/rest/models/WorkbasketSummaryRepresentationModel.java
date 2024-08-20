package io.kadai.workbasket.rest.models;

import io.kadai.workbasket.api.WorkbasketType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "EntityModel class for WorkbasketSummary.")
public class WorkbasketSummaryRepresentationModel
    extends RepresentationModel<WorkbasketSummaryRepresentationModel> {

  @Schema(name = "workbasketId", description = "Unique Id.")
  protected String workbasketId;

  @Schema(name = "key", description = "the professional key for the workbasket.")
  protected String key;

  @Schema(name = "name", description = "The name of the workbasket.")
  protected String name;

  @Schema(name = "domain", description = "The domain the workbasket belongs to.")
  protected String domain;

  @Schema(name = "type", description = "The type of the workbasket.")
  protected WorkbasketType type;

  @Schema(name = "description", description = "the description of the workbasket.")
  protected String description;

  @Schema(
      name = "owner",
      description =
          "The owner of the workbasket. The owner is responsible for the on-time completion of "
              + "all tasks in the workbasket.")
  protected String owner;

  @Schema(name = "custom1", description = "A custom property with name \"1\".")
  protected String custom1;

  @Schema(name = "custom2", description = "A custom property with name \"2\".")
  protected String custom2;

  @Schema(name = "custom3", description = "A custom property with name \"3\".")
  protected String custom3;

  @Schema(name = "custom4", description = "A custom property with name \"4\".")
  protected String custom4;

  @Schema(name = "custom5", description = "A custom property with name \"5\".")
  protected String custom5;

  @Schema(name = "custom6", description = "A custom property with name \"6\".")
  protected String custom6;

  @Schema(name = "custom7", description = "A custom property with name \"7\".")
  protected String custom7;

  @Schema(name = "custom8", description = "A custom property with name \"8\".")
  protected String custom8;

  @Schema(
      name = "orgLevel1",
      description =
          "The first Org Level (the top one).<p>The Org Level is an association with an org "
              + "hierarchy level in the organization. The values are used for monitoring and "
              + "statistical purposes and should reflect who is responsible of the tasks in the"
              + " workbasket.")
  protected String orgLevel1;

  @Schema(name = "orgLevel2", description = "The second Org Level.")
  protected String orgLevel2;

  @Schema(name = "orgLevel3", description = "The third Org Level.")
  protected String orgLevel3;

  @Schema(name = "orgLevel4", description = "The fourth Org Level.")
  protected String orgLevel4;
  
  @Schema(
      name = "markedForDeletion",
      description = "Identifier to tell if this workbasket can be " + "deleted.")
  private boolean markedForDeletion;

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

  public String getCustom5() {
    return custom5;
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5;
  }

  public String getCustom6() {
    return custom6;
  }

  public void setCustom6(String custom6) {
    this.custom6 = custom6;
  }

  public String getCustom7() {
    return custom7;
  }

  public void setCustom7(String custom7) {
    this.custom7 = custom7;
  }

  public String getCustom8() {
    return custom8;
  }

  public void setCustom8(String custom8) {
    this.custom8 = custom8;
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
}
