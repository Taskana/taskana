package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.List;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * Interface for TaskSummary. This is a specific short model-object which only contains the most
 * important information.
 */
public interface TaskSummary {

  /**
   * Returns the id of the {@linkplain Task}.
   *
   * @return taskId
   */
  String getId();

  /**
   * Returns the external id of the this {@linkplain Task}.
   *
   * @return the externalId
   */
  String getExternalId();

  /**
   * Returns the name of the creator of the {@linkplain Task}.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Returns the time when the {@linkplain Task} was created.
   *
   * @return the created {@linkplain Instant}
   */
  Instant getCreated();

  /**
   * Returns the time when the {@linkplain Task} was claimed.
   *
   * @return the claimed {@linkplain Instant}
   */
  Instant getClaimed();

  /**
   * Returns the time when the {@linkplain Task} was completed.
   *
   * @return the completed {@linkplain Instant}
   */
  Instant getCompleted();

  /**
   * Returns the time when the {@linkplain Task} was last modified.
   *
   * @return the last modified {@linkplain Instant}
   */
  Instant getModified();

  /**
   * Returns the time when the {@linkplain Task} is planned to be executed.
   *
   * @return the planned {@linkplain Instant}
   */
  Instant getPlanned();

  /**
   * Returns the time when the {@linkplain Task} is due.
   *
   * @return the due {@linkplain Instant}
   */
  Instant getDue();

  /**
   * Returns the name of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s name
   */
  String getName();

  /**
   * Returns the note attached to the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s note
   */
  String getNote();

  /**
   * Returns the description of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s description
   */
  String getDescription();

  /**
   * Returns the priority of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s priority
   */
  int getPriority();

  /**
   * Returns the state of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s state
   */
  TaskState getState();

  /**
   * Returns the {@linkplain ClassificationSummary} of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s {@linkplain ClassificationSummary}
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Returns the {@linkplain pro.taskana.workbasket.api.models.WorkbasketSummary WorkbasketSummary}
   * of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s {@linkplain WorkbasketSummary}
   */
  WorkbasketSummary getWorkbasketSummary();

  /**
   * Returns the {@linkplain AttachmentSummary AttachmentSummaries} of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s {@linkplain AttachmentSummary AttachmentSummaries}
   */
  List<AttachmentSummary> getAttachmentSummaries();

  /**
   * Returns the domain of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s domain
   */
  String getDomain();

  /**
   * Returns the businessProcessId of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s businessProcessId
   */
  String getBusinessProcessId();

  /**
   * Returns the parentBusinessProcessId of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s parentBusinessProcessId
   */
  String getParentBusinessProcessId();

  /**
   * Returns the owner of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s owner
   */
  String getOwner();

  /**
   * Returns the primary {@linkplain ObjectReference} of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s primary {@linkplain ObjectReference}
   */
  ObjectReference getPrimaryObjRef();

  /**
   * Returns the isRead flag of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s isRead flag
   */
  boolean isRead();

  /**
   * Returns the isTransferred flag of the {@linkplain Task}.
   *
   * @return the {@linkplain Task}'s isTransferred flag.
   */
  boolean isTransferred();

  /**
   * Returns the custom attribute of the {@linkplain Task}.
   *
   * @param customField identifies which custom attribute is requested
   * @return the value for the given customField
   */
  String getCustomAttribute(TaskCustomField customField);

  /**
   * Duplicates this TaskSummary without the internal and external id.
   *
   * @return a copy of this TaskSummary
   */
  TaskSummary copy();
}
