package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;

/** Task-Interface to specify attribute interactions. */
public interface Task extends TaskSummary {

  /**
   * The key that is used to supply Callback_state within the CallbackInfo map.
   *
   * <p>The Callback_state is used predominantly by the taskana adapter. It controls synchronization
   * between taskana and the external system.
   */
  String CALLBACK_STATE = "callbackState";

  /**
   * Sets the external ID.
   *
   * <p>It can be used to correlate this Task to a task in an external system. The externalId is
   * enforced to be unique. An attempt to create a Task with an existing externalId will be
   * rejected. So, this ID can be used to enforce idempotency of Task creation. The externalId can
   * only be set before the Task is inserted. Taskana rejects attempts to modify externalId.
   *
   * @param externalId the external Id
   */
  void setExternalId(String externalId);

  /**
   * Sets the time when the work on this Task should be started.
   *
   * @param planned as exact {@linkplain Instant}
   */
  void setPlanned(Instant planned);

  /**
   * Sets the time when the work on this Task should be finished.
   *
   * @param due as exact {@linkplain Instant}
   */
  void setDue(Instant due);

  /**
   * Sets the name of this Task.
   *
   * @param name the name of this Task
   */
  void setName(String name);

  /**
   * Sets the description of this Task.
   *
   * @param description the description of this Task
   */
  void setDescription(String description);

  /**
   * Sets the classificationKey that - together with the domain from this Task's {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} - selects the appropriate {@linkplain
   * Classification} for this Task.
   *
   * @param classificationKey the classificationKey for this Task
   */
  void setClassificationKey(String classificationKey);

  /**
   * Returns the key of the {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket}
   * where this Task is stored in.
   *
   * @return workbasketKey
   */
  String getWorkbasketKey();

  /**
   * Returns a Map of custom attributes.
   *
   * @return customAttributes as Map
   */
  Map<String, String> getCustomAttributeMap();

  /**
   * Sets a Map of custom attributes.
   *
   * @param customAttributes a Map that contains the custom attributes
   */
  void setCustomAttributeMap(Map<String, String> customAttributes);

  /**
   * Returns a Map of callback info.
   *
   * @return callbackInfo as Map
   */
  Map<String, String> getCallbackInfo();

  /**
   * Sets a Map of callback info.
   *
   * @param callbackInfo a Map that contains the callback info
   */
  void setCallbackInfo(Map<String, String> callbackInfo);

  /**
   * Sets the value for custom attribute.
   *
   * @param customField identifies which custom attribute is to be set.
   * @param value the value of the custom attribute to be set
   */
  void setCustomAttribute(TaskCustomField customField, String value);

  /**
   * Adds an {@linkplain Attachment}.
   *
   * <p>NULL will be ignored and an {@linkplain Attachment} with the same ID will be replaced by the
   * newer one.
   *
   * @param attachment the {@linkplain Attachment} to be added to the Task
   */
  void addAttachment(Attachment attachment);

  /**
   * Returns the {@linkplain Attachment Attachments} for this Task.
   *
   * <p>Do not use List.add()/addAll() for adding elements, because it can cause redundant data. Use
   * addAttachment(). clear() and remove() can be used, because it´s a controllable change.
   *
   * @return the List of {@linkplain Attachment Attachments} for this Task
   */
  List<Attachment> getAttachments();

  /**
   * Sets the external business process id.
   *
   * @param businessProcessId sets the business process id this Task belongs to
   */
  void setBusinessProcessId(String businessProcessId);

  /**
   * Sets the parent business process id to group associated processes.
   *
   * @param parentBusinessProcessId sets the parent business process id this Task belongs to
   */
  void setParentBusinessProcessId(String parentBusinessProcessId);

  /**
   * Sets the ownerId of this Task.
   *
   * @param taskOwnerId the user id of the owner of this Task
   */
  void setOwner(String taskOwnerId);

  /**
   * Sets the {@linkplain ObjectReference primaryObjectReference} of this Task.
   *
   * @param primaryObjRef to the main-subject of this Task
   */
  void setPrimaryObjRef(ObjectReference primaryObjRef);

  /**
   * Sets the custom note for this Task.
   *
   * @param note the custom note for this Task.
   */
  void setNote(String note);

  /**
   * Returns a summary of this Task.
   *
   * @return the {@linkplain TaskSummary} object for this Task
   */
  TaskSummary asSummary();

  /**
   * Removes an {@linkplain Attachment} of this Task locally, when the ID is represented and does
   * return the removed {@linkplain Attachment} or null if there was no match.
   *
   * <p>The changed Task need to be updated calling the {@linkplain TaskService#updateTask(Task)}.
   *
   * @param attachmentID ID of the {@linkplain Attachment} which should be removed
   * @return {@linkplain Attachment} which will be removed after updating OR null if there was no
   *     matching {@linkplain Attachment}
   */
  Attachment removeAttachment(String attachmentID);

  /**
   * Returns the category of the current {@linkplain Classification}.
   *
   * @return classificationCategory
   */
  String getClassificationCategory();

  /**
   * Duplicates this Task without the internal and external id. All referenced {@linkplain
   * Attachment Attachments} are copied as well.
   *
   * @return a copy of this Task
   */
  Task copy();
}
