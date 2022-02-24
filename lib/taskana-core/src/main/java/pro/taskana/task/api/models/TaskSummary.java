package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.List;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * Interface for TaskSummary. This is a specific short model-object which only contains the most
 * important information.
 */
public interface TaskSummary {

  /**
   * Gets the id of the Task.
   *
   * @return taskId
   */
  String getId();

  /**
   * Gets the external id of the Task.
   *
   * @return the external Id
   */
  String getExternalId();

  /**
   * Gets the name of the creator of the Task.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Gets the time when the task was created.
   *
   * @return the created {@link Instant}
   */
  Instant getCreated();

  /**
   * Gets the time when the Task was claimed.
   *
   * @return the claimed {@link Instant}
   */
  Instant getClaimed();

  /**
   * Gets the time when the Task was completed.
   *
   * @return the completed {@link Instant}
   */
  Instant getCompleted();

  /**
   * Gets the time when the Task was last modified.
   *
   * @return the last modified {@link Instant}
   */
  Instant getModified();

  /**
   * Gets the time when the Task is planned to be executed.
   *
   * @return the planned {@link Instant}
   */
  Instant getPlanned();

  /**
   * Gets the time when the surrounding process was started.
   *
   * @return the received {@link Instant}
   */
  Instant getReceived();

  /**
   * Gets the time when the Task is due.
   *
   * @return the due {@link Instant}
   */
  Instant getDue();

  /**
   * Gets the name of the Task.
   *
   * @return the Task's name
   */
  String getName();

  /**
   * Gets the note attached to the Task.
   *
   * @return the Task's note
   */
  String getNote();

  /**
   * Gets the description of the Task.
   *
   * @return the Task's description
   */
  String getDescription();

  /**
   * Gets the priority of the Task.
   *
   * @return the Task's priority
   */
  int getPriority();

  /**
   * Gets the state of the Task.
   *
   * @return the Task's state
   */
  TaskState getState();

  /**
   * Gets the {@link ClassificationSummary} of the Task.
   *
   * @return the Task's {@link ClassificationSummary}
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Gets the {@link WorkbasketSummary} of the Task.
   *
   * @return the Task's {@link WorkbasketSummary}
   */
  WorkbasketSummary getWorkbasketSummary();

  /**
   * Gets the {@link AttachmentSummary attachmentSummaries} of the Task.
   *
   * @return the Task's {@link AttachmentSummary attachmentSummaries}
   */
  List<AttachmentSummary> getAttachmentSummaries();

  /**
   * Gets the secondary {@link ObjectReference}s of the Task.
   *
   * @return the Task's secondary {@link ObjectReference}s
   */
  List<ObjectReference> getSecondaryObjectReferences();

  /**
   * Add an {@link ObjectReference} to the list of secondary {@link ObjectReference}s.<br>
   * NULL will be ignored and an ObjectReference with the same ID will be replaced by the newer one.
   * <br>
   *
   * @param objectReference the secondary {@link ObjectReference objectReference} to be added to the
   *     Task
   */
  void addSecondaryObjectReference(ObjectReference objectReference);

  /**
   * Add an {@link ObjectReference} to the list of secondary {@link ObjectReference}s.<br>
   * NULL will be ignored and an ObjectReference with the same ID will be replaced by the newer one.
   * <br>
   *
   * @param company of the {@link ObjectReference objectReference} to be added to the Task
   * @param system of the {@link ObjectReference objectReference} to be added to the Task
   * @param systemInstance of the {@link ObjectReference objectReference} to be added to the Task
   * @param type of the {@link ObjectReference objectReference} to be added to the Task
   * @param value of the {@link ObjectReference objectReference} to be added to the Task
   */
  void addSecondaryObjectReference(
      String company, String system, String systemInstance, String type, String value);

  /**
   * Removes a secondary {@link ObjectReference} of the current Task locally, when the ID is
   * represented and does return the removed {@link ObjectReference} or null if there was no match.
   * <br>
   * The changed Task need to be updated calling the {@link TaskService#updateTask(Task)}.
   *
   * @param objectReferenceID ID of the {@link ObjectReference} which should be removed.
   * @return the {@link ObjectReference} which will be removed after updating OR null if there was
   *     no matching {@link ObjectReference}
   */
  ObjectReference removeSecondaryObjectReference(String objectReferenceID);

  /**
   * Gets the domain of the Task.
   *
   * @return the Task's domain
   */
  String getDomain();

  /**
   * Gets the businessProcessId of the Task.
   *
   * @return the Task's businessProcessId
   */
  String getBusinessProcessId();

  /**
   * Gets the parentBusinessProcessId of the Task.
   *
   * @return the Task's parentBusinessProcessId
   */
  String getParentBusinessProcessId();

  /**
   * Gets the owner of the Task.
   *
   * @return the Task's owner
   */
  String getOwner();

  /**
   * Gets the owner's long name of the Task.
   *
   * @return the long name of the Task owner
   */
  String getOwnerLongName();

  /**
   * Gets the primary {@link ObjectReference} of the Task.
   *
   * @return the Task's primary {@link ObjectReference}
   */
  ObjectReference getPrimaryObjRef();

  /**
   * Gets the isRead flag of the Task.
   *
   * @return the Task's isRead flag
   */
  boolean isRead();

  /**
   * Gets the isTransferred flag of the Task.
   *
   * @return the Task's isTransferred flag.
   */
  boolean isTransferred();

  /**
   * Gets the custom attribute of the Task.
   *
   * @param customField identifies which custom attribute is requested.
   * @return the value for the given customField
   * @deprecated Use {@link #getCustomField(TaskCustomField)} instead
   */
  String getCustomAttribute(TaskCustomField customField);

  /**
   * Gets the custom attribute of the task.
   *
   * @param customField identifies which custom attribute is requested.
   * @return the value for the given customField
   */
  String getCustomField(TaskCustomField customField);

  /**
   * Duplicates this TaskSummary without the internal and external id.
   *
   * @return a copy of this TaskSummary
   */
  TaskSummary copy();
}
