package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Super class for all workbasket related events. */
public class WorkbasketHistoryEvent {

  protected String id;
  protected String eventType;
  protected Instant created;
  protected String userId;
  protected String domain;
  protected String workbasketId;
  protected String workbasketKey;
  protected String workbasketType;
  protected String owner;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String orgLevel1;
  protected String orgLevel2;
  protected String orgLevel3;
  protected String orgLevel4;
  protected String details;

  public WorkbasketHistoryEvent() {}

  public WorkbasketHistoryEvent(
      String id, WorkbasketSummary workbasket, String userId, String details) {
    this.id = id;
    this.userId = userId;
    this.details = details;
    workbasketId = workbasket.getId();
    domain = workbasket.getDomain();
    workbasketKey = workbasket.getKey();
    workbasketType = workbasket.getType().name();
    owner = workbasket.getOwner();
    custom1 = workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_1);
    custom2 = workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_2);
    custom3 = workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_3);
    custom4 = workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_4);
    orgLevel1 = workbasket.getOrgLevel1();
    orgLevel2 = workbasket.getOrgLevel2();
    orgLevel3 = workbasket.getOrgLevel3();
    orgLevel4 = workbasket.getOrgLevel4();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }

  public void setWorkbasketId(String workbasketId) {
    this.workbasketId = workbasketId;
  }

  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
  }

  public String getWorkbasketType() {
    return workbasketType;
  }

  public void setWorkbasketType(String workbasketType) {
    this.workbasketType = workbasketType;
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

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  @Override
  public String toString() {
    return "WorkbasketEvent [id="
        + id
        + ", eventType="
        + eventType
        + ", created="
        + created
        + ", userId="
        + userId
        + ", domain="
        + domain
        + ", workbasketId="
        + workbasketId
        + ", workbasketKey="
        + workbasketKey
        + ", workbasketType="
        + workbasketType
        + ", owner="
        + owner
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
        + ", details="
        + details
        + "]";
  }
}
