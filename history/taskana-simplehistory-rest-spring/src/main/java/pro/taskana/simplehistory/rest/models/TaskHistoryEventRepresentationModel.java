package pro.taskana.simplehistory.rest.models;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** Resource class for {@link TaskHistoryEvent}. */
@Getter
@Setter
@ToString
public class TaskHistoryEventRepresentationModel
    extends RepresentationModel<TaskHistoryEventRepresentationModel> {

  /** Unique Id. */
  private String taskHistoryId;
  /** The Id of the business process. */
  private String businessProcessId;
  /** The Id of the parent business process. */
  private String parentBusinessProcessId;
  /** The Id of the task. */
  private String taskId;
  /** The type of the event. */
  private String eventType;
  /**
   * The time of event creation.
   *
   * <p>The format is ISO-8601.
   */
  private Instant created;
  /** The Id of the user. */
  private String userId;
  /** The long name of the user. */
  private String userLongName;
  /** Domain. */
  private String domain;
  /** The key of the Workbasket. */
  private String workbasketKey;
  /** The company the referenced primary object belongs to. */
  private String porCompany;
  /** The type of the referenced primary object (contract, claim, policy, customer, ...). */
  private String porType;
  /** The (kind of) system, the referenced primary object resides in (e.g. SAP, MySystem A, ...). */
  private String porSystem;
  /** The instance of the system where the referenced primary object is located. */
  private String porInstance;
  /** The value of the primary object reference. */
  private String porValue;
  /** The long name of the task owner. */
  private String taskOwnerLongName;
  /** The key of the task's classification. */
  private String taskClassificationKey;
  /** The category of the task's classification. */
  private String taskClassificationCategory;
  /** The classification key of the task's attachment. */
  private String attachmentClassificationKey;
  /** The old value. */
  private String oldValue;
  /** The new value. */
  private String newValue;
  /** A custom property with name "1". */
  private String custom1;
  /** A custom property with name "2". */
  private String custom2;
  /** A custom property with name "3". */
  private String custom3;
  /** A custom property with name "4". */
  private String custom4;
  /** details of changes within the task. */
  private String details;
}
