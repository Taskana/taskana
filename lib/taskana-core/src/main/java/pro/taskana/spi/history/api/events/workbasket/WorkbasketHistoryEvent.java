package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Super class for all workbasket related events. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class WorkbasketHistoryEvent {

  protected String id;
  protected String eventType;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected Instant created;

  protected String userId;
  protected String domain;
  protected String workbasketId;
  protected String key;
  protected String type;
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

  public WorkbasketHistoryEvent(
      String id, WorkbasketSummary workbasket, String userId, String details) {
    this.id = id;
    this.userId = userId;
    this.details = details;
    workbasketId = workbasket.getId();
    domain = workbasket.getDomain();
    key = workbasket.getKey();
    type = workbasket.getType().name();
    owner = workbasket.getOwner();
    custom1 = workbasket.getCustomField(WorkbasketCustomField.CUSTOM_1);
    custom2 = workbasket.getCustomField(WorkbasketCustomField.CUSTOM_2);
    custom3 = workbasket.getCustomField(WorkbasketCustomField.CUSTOM_3);
    custom4 = workbasket.getCustomField(WorkbasketCustomField.CUSTOM_4);
    orgLevel1 = workbasket.getOrgLevel1();
    orgLevel2 = workbasket.getOrgLevel2();
    orgLevel3 = workbasket.getOrgLevel3();
    orgLevel4 = workbasket.getOrgLevel4();
  }

  public void setCustomAttribute(WorkbasketCustomField customField, String value) {
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

  public String getCustomAttribute(WorkbasketCustomField customField) {
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

  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }
}
