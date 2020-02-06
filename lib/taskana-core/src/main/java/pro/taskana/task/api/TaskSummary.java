package pro.taskana.task.api;

import java.time.Instant;
import java.util.List;

import pro.taskana.classification.api.ClassificationSummary;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.workbasket.api.WorkbasketSummary;

/**
 * Interface for TaskSummary. This is a specific short model-object which only contains the most
 * important information.
 */
public interface TaskSummary {

  /**
   * Gets the id of the task.
   *
   * @return taskId
   */
  String getId();

  /**
   * Gets the external id of the task.
   *
   * @return the external Id
   */
  String getExternalId();

  /**
   * Gets the name of the task-creator.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Gets the time when the task was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Gets the time when the task was claimed.
   *
   * @return the claimed Instant
   */
  Instant getClaimed();

  /**
   * Gets the time when the task was completed.
   *
   * @return the completed Instant
   */
  Instant getCompleted();

  /**
   * Gets the time when the task was last modified.
   *
   * @return the last modified Instant
   */
  Instant getModified();

  /**
   * Gets the time when the task is planned to be executed.
   *
   * @return the planned Instant
   */
  Instant getPlanned();

  /**
   * Gets the time when the task is due.
   *
   * @return the due Instant
   */
  Instant getDue();

  /**
   * Gets the name of the task.
   *
   * @return the task's name
   */
  String getName();

  /**
   * Gets the note attached to the task.
   *
   * @return the task's note
   */
  String getNote();

  /**
   * Gets the description of the task.
   *
   * @return the task's description
   */
  String getDescription();

  /**
   * Gets the priority of the task.
   *
   * @return the task's priority
   */
  int getPriority();

  /**
   * Gets the state of the task.
   *
   * @return the task's state
   */
  TaskState getState();

  /**
   * Gets the classification summary of the task.
   *
   * @return the task's classificationSummary
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Gets the workbasket summary of the task.
   *
   * @return the task's workbasketSummary
   */
  WorkbasketSummary getWorkbasketSummary();

  /**
   * Gets the attachment summaries of the task.
   *
   * @return the task's attachment summaries
   */
  List<AttachmentSummary> getAttachmentSummaries();

  /**
   * Gets the domain of the task.
   *
   * @return the task's domain
   */
  String getDomain();

  /**
   * Gets the businessProcessId of the task.
   *
   * @return the task's businessProcessId
   */
  String getBusinessProcessId();

  /**
   * Gets the parentBusinessProcessId of the task.
   *
   * @return the task's parentBusinessProcessId
   */
  String getParentBusinessProcessId();

  /**
   * Gets the owner of the task.
   *
   * @return the task's owner
   */
  String getOwner();

  /**
   * Gets the primary ObjectReference of the task.
   *
   * @return the task's primary ObjectReference
   */
  ObjectReference getPrimaryObjRef();

  /**
   * Gets the isRead flag of the task.
   *
   * @return the task's isRead flag
   */
  boolean isRead();

  /**
   * Gets the isTransferred flag of the task.
   *
   * @return the task's isTransferred flag.
   */
  boolean isTransferred();

  /**
   * Gets the custom attribute number num of the task.
   *
   * @param num identifies which custom attribute is requested. Taskana concatenates "custom_" with
   *     num and the resulting String must match the name of the database column that contains the
   *     custom attribute. Valid values are "1", "2" .. "16"
   * @return the value of custom attribute number num
   * @throws InvalidArgumentException if num has not a value of "1", "2" ... "16"
   */
  String getCustomAttribute(String num) throws InvalidArgumentException;
}
