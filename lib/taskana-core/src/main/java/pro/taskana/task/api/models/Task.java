package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;

/** task-Interface to specify attribute interactions. */
public interface Task extends TaskSummary {

  /**
   * The key that is used to supply Callback_state within the CallbackInfo map. The Callback_state
   * is used predominantly by the taskana adapter. It controls synchronization between taskana and
   * the external system.
   */
  String CALLBACK_STATE = "callbackState";

  /**
   * Sets the external Id. It can be used to correlate the Task to a Task in an external system. The
   * external Id is enforced to be unique. An attempt to create a Task with an existing external Id
   * will be rejected. So, this Id can be used to enforce idempotency of Task creation. The
   * externalId can only be set before the Task is inserted. Taskana rejects attempts to modify
   * externalId.
   *
   * @param externalId the external Id
   */
  void setExternalId(String externalId);

  /**
   * Sets the time when the work on this Task should be started.
   *
   * @param planned as exact {@link Instant}
   */
  void setPlanned(Instant planned);

  /**
   * Sets the time when when the surrounding process started.
   *
   * @param received as exact {@link Instant}
   */
  void setReceived(Instant received);

  /**
   * Sets the time when the work on this Task should be finished.
   *
   * @param due as exact {@link Instant}
   */
  void setDue(Instant due);

  /**
   * Sets the name of the current Task.
   *
   * @param name the name of the Task
   */
  void setName(String name);

  /**
   * Sets the description of the Task.
   *
   * @param description the description of the Task
   */
  void setDescription(String description);

  /**
   * Sets the Classification key that - together with the Domain from this Task's work basket -
   * selects the appropriate {@link Classification} for this Task.
   *
   * @param classificationKey the classification key for the Task
   */
  void setClassificationKey(String classificationKey);

  /**
   * Sets the manualPriority of the Task. If the value of manualPriority is zero or greater, the
   * priority is automatically set to manualPriority. In this case, all computations of priority are
   * disabled. If the value of manualPriority is negative, Tasks are not prioritized manually.
   *
   * @param manualPriority the value for manualPriority of the Task
   */
  void setManualPriority(int manualPriority);

  /**
   * Returns the key of the Workbasket where the Task is stored in.
   *
   * @return workbasketKey
   */
  String getWorkbasketKey();

  /**
   * Returns a Map of custom Attributes.
   *
   * @return customAttributes as {@link Map}
   */
  Map<String, String> getCustomAttributeMap();

  /**
   * Sets a Map of custom Attributes.
   *
   * @param customAttributes a {@link Map} that contains the custom attributes
   */
  void setCustomAttributeMap(Map<String, String> customAttributes);

  /**
   * Returns a Map of Callback info.
   *
   * @return callbackInfo as {@link Map}
   */
  Map<String, String> getCallbackInfo();

  /**
   * Sets a Map of callback info.
   *
   * @param callbackInfo a {@link Map} that contains the callback info
   */
  void setCallbackInfo(Map<String, String> callbackInfo);

  /**
   * Sets the value for custom Attribute.
   *
   * @param customField identifies which custom attribute is to be set.
   * @param value the value of the custom attribute to be set
   */
  void setCustomAttribute(TaskCustomField customField, String value);

  /**
   * Add an attachment.<br>
   * NULL will be ignored and an attachment with the same ID will be replaced by the newer one.<br>
   *
   * @param attachment the {@link Attachment attachment} to be added to the Task
   */
  void addAttachment(Attachment attachment);

  /**
   * Return the attachments for this Task. <br>
   * Do not use List.add()/addAll() for adding Elements, because it can cause redundant data. Use
   * addAttachment(). Clear() and remove() can be used, because it's a controllable change.
   *
   * @return the {@link List list} of {@link Attachment attachments} for this Task
   */
  List<Attachment> getAttachments();

  /**
   * Sets the external business process id.
   *
   * @param businessProcessId Sets the business process id the Task belongs to.
   */
  void setBusinessProcessId(String businessProcessId);

  /**
   * Sets the parent business process id to group associated processes.
   *
   * @param parentBusinessProcessId Sets the parent business process id the Task belongs to
   */
  void setParentBusinessProcessId(String parentBusinessProcessId);

  /**
   * Sets the ownerId of this Task.
   *
   * @param taskOwnerId the user id of the Task's owner
   */
  void setOwner(String taskOwnerId);

  /**
   * Sets the {@link ObjectReference primaryObjectReference} of the Task.
   *
   * @param primaryObjRef to Task main-subject
   */
  void setPrimaryObjRef(ObjectReference primaryObjRef);

  /**
   * Initializes and sets the {@link ObjectReference primaryObjectReference} of the Task.
   *
   * @param company of the {@link ObjectReference primaryObjectReference} to be set
   * @param system of the {@link ObjectReference primaryObjectReference} to be set
   * @param systemInstance of the {@link ObjectReference primaryObjectReference} to be set
   * @param type of the {@link ObjectReference primaryObjectReference} to be set
   * @param value of the {@link ObjectReference primaryObjectReference} to be set
   */
  void setPrimaryObjRef(
      String company, String system, String systemInstance, String type, String value);

  /**
   * Sets/Changing the custom note for this Task.
   *
   * @param note the custom note for this Task.
   */
  void setNote(String note);

  /**
   * Return a summary of the current Task.
   *
   * @return the TaskSummary object for the current Task
   */
  TaskSummary asSummary();

  /**
   * Removes an attachment of the current Task locally, when the ID is represented and does return
   * the removed attachment or null if there was no match.<br>
   * The changed Task need to be updated calling the {@link TaskService#updateTask(Task)}.
   *
   * @param attachmentID ID of the attachment which should be removed.
   * @return attachment which will be removed after updating OR null if there was no matching
   *     attachment
   */
  Attachment removeAttachment(String attachmentID);

  /**
   * Returns the category of the current classification.
   *
   * @return classificationCategory
   */
  String getClassificationCategory();

  /**
   * Duplicates this Task without the internal and external id. All referenced {@link Attachment}s
   * and {@link ObjectReference}s are copied as well.
   *
   * @return a copy of this Task
   */
  Task copy();
}
