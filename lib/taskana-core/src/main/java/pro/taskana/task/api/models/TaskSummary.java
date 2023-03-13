package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.List;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * Interface for TaskSummary. This is a specific short model-object which only contains the most
 * important information.
 */
public interface TaskSummary {

  public static int DEFAULT_MANUAL_PRIORITY = -1;

  /**
   * Returns the id of the {@linkplain Task}.
   *
   * @return taskId
   */
  String getId();

  /**
   * Returns the externalId of the {@linkplain Task}.
   *
   * @return externalId
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
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the {@linkplain Task} was claimed.
   *
   * @return the claimed Instant
   */
  Instant getClaimed();

  /**
   * Returns the time when the {@linkplain Task} was completed.
   *
   * @return the completed Instant
   */
  Instant getCompleted();

  /**
   * Returns the time when the {@linkplain Task} was last modified.
   *
   * @return the last modified Instant
   */
  Instant getModified();

  /**
   * Returns the time when the {@linkplain Task} is planned to be executed.
   *
   * @return the planned Instant
   */
  Instant getPlanned();

  /**
   * Returns the time when the surrounding process was started.
   *
   * @return the received Instant
   */
  Instant getReceived();

  /**
   * Returns the time when the {@linkplain Task} is due.
   *
   * <p>This instant denotes the last point in the allowed work time has ended or in short it is
   * inclusive.
   *
   * @return the due Instant
   */
  Instant getDue();

  /**
   * Returns the name of the {@linkplain Task}.
   *
   * @return name
   */
  String getName();

  /**
   * Returns the note attached to the {@linkplain Task}.
   *
   * @return note
   */
  String getNote();

  /**
   * Returns the description of the {@linkplain Task}.
   *
   * @return description
   */
  String getDescription();

  /**
   * Returns the priority of the {@linkplain Task}.
   *
   * @return priority
   */
  int getPriority();

  /**
   * Gets the manualPriority of the {@linkplain Task}. If the value of manualPriority is zero or
   * greater, the priority is automatically set to manualPriority. In this case, all computations of
   * priority are disabled. If the value of manualPriority is negative, Tasks are not prioritized
   * manually.
   *
   * @return the manualPriority of the Task
   */
  int getManualPriority();

  /**
   * Returns the state of the {@linkplain Task}.
   *
   * @return state
   */
  TaskState getState();

  /**
   * Returns the {@linkplain ClassificationSummary} of the {@linkplain Task}.
   *
   * @return {@linkplain ClassificationSummary}
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Returns the {@linkplain WorkbasketSummary} of the {@linkplain Task}.
   *
   * @return {@linkplain WorkbasketSummary}
   */
  WorkbasketSummary getWorkbasketSummary();

  /**
   * Returns the {@linkplain AttachmentSummary attachmentSummaries} of the {@linkplain Task}.
   *
   * @return {@linkplain AttachmentSummary attachmentSummaries}
   */
  List<AttachmentSummary> getAttachmentSummaries();

  /**
   * Returns the {@linkplain ObjectReference secondaryObjectReferences} of the {@linkplain Task}.
   *
   * @return {@linkplain ObjectReference secondaryObjectReferences}
   */
  List<ObjectReference> getSecondaryObjectReferences();

  /**
   * Add an {@linkplain ObjectReference} to the List of secondary {@linkplain ObjectReference
   * objectReferences}.<br>
   * NULL will be ignored and an {@linkplain ObjectReference} with the same {@linkplain
   * ObjectReference#getId() id} will be replaced by the newer one. <br>
   *
   * @param objectReference the secondary {@linkplain ObjectReference objectReference} to be added
   *     to the {@linkplain Task}
   */
  void addSecondaryObjectReference(ObjectReference objectReference);

  /**
   * Add an {@linkplain ObjectReference} to the List of secondary {@linkplain ObjectReference}s.<br>
   * NULL will be ignored and an ObjectReference with the same {@linkplain ObjectReference#getId()
   * id} will be replaced by the newer one. <br>
   *
   * @param company of the {@linkplain ObjectReference objectReference} to be added to the Task
   * @param system of the {@linkplain ObjectReference objectReference} to be added to the Task
   * @param systemInstance of the {@linkplain ObjectReference objectReference} to be added to the
   *     Task
   * @param type of the {@linkplain ObjectReference objectReference} to be added to the Task
   * @param value of the {@linkplain ObjectReference objectReference} to be added to the Task
   */
  void addSecondaryObjectReference(
      String company, String system, String systemInstance, String type, String value);

  /**
   * Removes a secondary {@linkplain ObjectReference} of the current Task locally, when the ID is
   * represented and does return the removed {@linkplain ObjectReference} or null if there was no
   * match. <br>
   * The changed Task need to be updated calling the {@linkplain TaskService#updateTask(Task)}.
   *
   * @param objectReferenceID {@linkplain ObjectReference#getId() id} of the {@linkplain
   *     ObjectReference} which should be removed
   * @return the {@linkplain ObjectReference} which will be removed after updating or null if there
   *     was no matching {@linkplain ObjectReference}
   */
  ObjectReference removeSecondaryObjectReference(String objectReferenceID);

  /**
   * Returns the domain of the {@linkplain Task}.
   *
   * @return domain
   */
  String getDomain();

  /**
   * Returns the businessProcessId of the {@linkplain Task}.
   *
   * @return businessProcessId
   */
  String getBusinessProcessId();

  /**
   * Returns the parentBusinessProcessId of the {@linkplain Task}.
   *
   * @return parentBusinessProcessId
   */
  String getParentBusinessProcessId();

  /**
   * Returns the owner of the {@linkplain Task}.
   *
   * @return owner
   */
  String getOwner();

  /**
   * Returns long name of the owner of the {@linkplain Task}.
   *
   * @return the long name of the owner
   */
  String getOwnerLongName();

  /**
   * Returns the primary {@linkplain ObjectReference} of the {@linkplain Task}.
   *
   * @return the Tasks primary {@linkplain ObjectReference}
   */
  ObjectReference getPrimaryObjRef();

  /**
   * Returns the isRead flag of the {@linkplain Task}.
   *
   * @return the Tasks isRead flag
   */
  boolean isRead();

  /**
   * Returns the isTransferred flag of the {@linkplain Task}.
   *
   * @return the Tasks isTransferred flag
   */
  boolean isTransferred();

  /**
   * Returns whether the {@linkplain Task} is prioritized manually. That means that the priority is
   * set to the value of the manualPriority of the {@linkplain Task}.
   *
   * @return true, if Tasks are prioritized manually; false otherwise
   */
  boolean isManualPriorityActive();

  /**
   * Returns the value of the specified {@linkplain TaskCustomField} of the {@linkplain Task}.
   *
   * @param customField identifies which {@linkplain TaskCustomField} is requested
   * @return the value for the given customField
   * @deprecated Use {@linkplain #getCustomField(TaskCustomField)} instead
   */
  @Deprecated
  String getCustomAttribute(TaskCustomField customField);

  /**
   * Returns the value of the specified {@linkplain TaskCustomField} of the {@linkplain Task}.
   *
   * @param customField identifies which {@linkplain TaskCustomField} is requested
   * @return the value for the given {@linkplain TaskCustomField}
   */
  String getCustomField(TaskCustomField customField);

  /**
   * Returns the value of the specified {@linkplain TaskCustomIntField} of the {@linkplain Task}.
   *
   * @param customIntField identifies which {@linkplain TaskCustomIntField} is requested
   * @return the value for the given {@linkplain TaskCustomIntField}
   */
  Integer getCustomIntField(TaskCustomIntField customIntField);

  /**
   * Duplicates this TaskSummary without the internal and external id.
   *
   * @return a copy of this TaskSummary
   */
  TaskSummary copy();
}
