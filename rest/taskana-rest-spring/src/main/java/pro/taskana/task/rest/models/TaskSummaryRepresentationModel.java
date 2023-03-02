package pro.taskana.task.rest.models;

import static pro.taskana.task.api.models.TaskSummary.DEFAULT_MANUAL_PRIORITY;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

@Getter
@Setter
public class TaskSummaryRepresentationModel
    extends RepresentationModel<TaskSummaryRepresentationModel> {

  /** Unique Id. */
  protected String taskId;
  /**
   * External Id. Can be used to enforce idempotence at task creation. Can identify an external
   * task.
   */
  protected String externalId;
  /** The creation timestamp in the system. */
  protected Instant created;
  /** The timestamp of the last claim-operation. */
  protected Instant claimed;
  /** The timestamp of the completion. */
  protected Instant completed;
  /** The timestamp of the last modification. */
  protected Instant modified;
  /**
   * Planned start of the task. The actual completion of the task should be between PLANNED and DUE.
   */
  protected Instant planned;
  /**
   * Timestamp when the task has been received. It notes when the surrounding process started and
   * not just when the actual task was created.
   */
  protected Instant received;
  /**
   * Timestamp when the task is due. The actual completion of the task should be between PLANNED and
   * DUE.
   */
  protected Instant due;
  /** The name of the task. */
  protected String name;
  /** the creator of the task. */
  protected String creator;
  /** note. */
  protected String note;
  /** The description of the task. */
  protected String description;
  /** The priority of the task. */
  protected int priority;
  /**
   * The manual priority of the task. If the value of manualPriority is zero or greater, the
   * priority is automatically set to manualPriority. In this case, all computations of priority are
   * disabled. If the value of manualPriority is negative, Tasks are not prioritized manually.
   */
  protected int manualPriority = DEFAULT_MANUAL_PRIORITY;
  /** The current task state. */
  protected TaskState state;
  /** The classification of this task. */
  @NotNull protected ClassificationSummaryRepresentationModel classificationSummary;
  /** The workbasket this task resides in. */
  @NotNull protected WorkbasketSummaryRepresentationModel workbasketSummary;
  /** The business process id. */
  protected String businessProcessId;
  /** the parent business process id. */
  protected String parentBusinessProcessId;
  /** The owner of the task. The owner is set upon claiming of the task. */
  protected String owner;
  /** The long name of the task owner. */
  protected String ownerLongName;
  /** The Objects primary ObjectReference. */
  @NotNull protected ObjectReferenceRepresentationModel primaryObjRef;
  /** Indicator if the task has been read. */
  protected boolean isRead;
  /** Indicator if the task has been transferred. */
  protected boolean isTransferred;
  /** A custom property with name "1". */
  protected String custom1;
  /** A custom property with name "2". */
  protected String custom2;
  /** A custom property with name "3". */
  protected String custom3;
  /** A custom property with name "4". */
  protected String custom4;
  /** A custom property with name "5". */
  protected String custom5;
  /** A custom property with name "6". */
  protected String custom6;
  /** A custom property with name "7". */
  protected String custom7;
  /** A custom property with name "8". */
  protected String custom8;
  /** A custom property with name "9". */
  protected String custom9;
  /** A custom property with name "10". */
  protected String custom10;
  /** A custom property with name "11". */
  protected String custom11;
  /** A custom property with name "12". */
  protected String custom12;
  /** A custom property with name "13". */
  protected String custom13;
  /** A custom property with name "14". */
  protected String custom14;
  /** A custom property with name "15". */
  protected String custom15;
  /** A custom property with name "16". */
  protected String custom16;
  /** A custom int property with name "1". */
  protected Integer customInt1;
  /** A custom int property with name "2". */
  protected Integer customInt2;
  /** A custom int property with name "3". */
  protected Integer customInt3;
  /** A custom int property with name "4". */
  protected Integer customInt4;
  /** A custom int property with name "5". */
  protected Integer customInt5;
  /** A custom int property with name "6". */
  protected Integer customInt6;
  /** A custom int property with name "7". */
  protected Integer customInt7;
  /** A custom int property with name "8". */
  protected Integer customInt8;
  /** Secondary object references of the task. */
  protected List<ObjectReferenceRepresentationModel> secondaryObjectReferences = new ArrayList<>();
  /** The attachment summaries of this task. */
  private List<AttachmentSummaryRepresentationModel> attachmentSummaries = new ArrayList<>();
}
