package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskService;
import pro.taskana.user.api.models.User;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Task-Interface to specify the model of a Task. */
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
   * @param externalId the externalId
   */
  void setExternalId(String externalId);

  /**
   * Sets the time when the work on this Task should be started.
   *
   * @param planned as exact {@linkplain Instant}
   */
  void setPlanned(Instant planned);

  /**
   * Sets the time when the surrounding process started.
   *
   * @param received as exact {@linkplain Instant}
   */
  void setReceived(Instant received);

  /**
   * Sets the time when the work on this Task should be finished.
   *
   * <p><code>due</code> denotes the last point in the allowed work time has ended or in short it is
   * inclusive.
   *
   * @param due as exact {@linkplain Instant}
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
   * Sets the {@linkplain Classification#getKey() key} of the {@linkplain Classification} that -
   * together with the {@linkplain WorkbasketSummary#getDomain() domain} from the {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} of the Task - selects the appropriate
   * {@linkplain Classification} for this Task.
   *
   * @param classificationKey the {@linkplain Classification#getKey() key} of the {@linkplain
   *     Classification} for the Task
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
   * Returns the {@linkplain WorkbasketSummary#getKey() key} of the {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} where the Task is stored in.
   *
   * @return workbasketKey
   */
  String getWorkbasketKey();

  /**
   * Returns a Map of customAttributes.
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
   * Returns the callbackInfo.
   *
   * @return callbackInfo as Map
   */
  Map<String, String> getCallbackInfo();

  /**
   * Sets the callbackInfo.
   *
   * @param callbackInfo a {@linkplain Map} that contains the callback information
   */
  void setCallbackInfo(Map<String, String> callbackInfo);

  /**
   * Sets the value for the specified {@linkplain TaskCustomField customField}.
   *
   * @param customField identifies which {@linkplain TaskCustomField customField} is to be set
   * @param value the value of the {@linkplain TaskCustomField customField} to be set
   * @deprecated Use {@linkplain #setCustomField(TaskCustomField, String)} instead
   */
  @Deprecated
  void setCustomAttribute(TaskCustomField customField, String value);

  /**
   * Sets the value for the specified {@linkplain TaskCustomField customField}.
   *
   * @param customField identifies which {@linkplain TaskCustomField customField} is to be set.
   * @param value the value of the {@linkplain TaskCustomField customField} to be set
   */
  void setCustomField(TaskCustomField customField, String value);

  /**
   * Sets the value for the specified {@linkplain TaskCustomIntField custoIntField}.
   *
   * @param customIntField identifies which {@linkplain TaskCustomIntField customIntField} is to be
   *     set
   * @param value the value of the {@linkplain TaskCustomIntField customIntField} to be set
   */
  void setCustomIntField(TaskCustomIntField customIntField, Integer value);

  /**
   * Add an {@linkplain Attachment}.<br>
   * NULL will be ignored and an {@linkplain Attachment} with the same id will be replaced by the
   * newer one.<br>
   *
   * @param attachment the {@linkplain Attachment attachment} to be added to the Task
   */
  void addAttachment(Attachment attachment);

  /**
   * Return the {@linkplain Attachment attachment} for the Task. <br>
   * Do not use List.add()/addAll() for adding elements, because it can cause redundant data. Use
   * addAttachment(). Clear() and remove() can be used, because it's a controllable change.
   *
   * @return the List of {@linkplain Attachment attachments} for this Task
   */
  List<Attachment> getAttachments();

  /**
   * Sets the associated businessProcessId.
   *
   * @param businessProcessId Sets the businessProcessId the Task belongs to.
   */
  void setBusinessProcessId(String businessProcessId);

  /**
   * Sets the parentBusinessProcessId. ParentBusinessProcessId is needed to group associated
   * processes and to identify the main process.
   *
   * @param parentBusinessProcessId the business process id of the parent the Task belongs to
   */
  void setParentBusinessProcessId(String parentBusinessProcessId);

  /**
   * Sets the id of the owner of the Task.
   *
   * @param taskOwnerId the {@linkplain User#getId() id} of the owner of the Task
   */
  void setOwner(String taskOwnerId);

  /**
   * Sets the {@linkplain ObjectReference primaryObjectReference} of the Task.
   *
   * @param primaryObjRef to Task main-subject
   */
  void setPrimaryObjRef(ObjectReference primaryObjRef);

  /**
   * Initializes and sets the {@linkplain ObjectReference primaryObjectReference} of the Task.
   *
   * @param company of the {@linkplain ObjectReference primaryObjectReference} to be set
   * @param system of the {@linkplain ObjectReference primaryObjectReference} to be set
   * @param systemInstance of the {@linkplain ObjectReference primaryObjectReference} to be set
   * @param type of the {@linkplain ObjectReference primaryObjectReference} to be set
   * @param value of the {@linkplain ObjectReference primaryObjectReference} to be set
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
   * Returns a summary of the current Task.
   *
   * @return the {@linkplain TaskSummary} object for the current Task
   */
  TaskSummary asSummary();

  /**
   * Removes an {@linkplain Attachment attachment} of the current Task locally, when the ID is
   * represented and does return the removed {@linkplain Attachment attachment} or null if there was
   * no match.<br>
   * The changed Task need to be updated calling the {@linkplain TaskService#updateTask(Task)}.
   *
   * @param attachmentID ID of the {@linkplain Attachment attachment} which should be removed.
   * @return {@linkplain Attachment attachment} which will be removed after updating OR null if
   *     there was no match.
   */
  Attachment removeAttachment(String attachmentID);

  /**
   * Returns the category of the current {@linkplain Classification}.
   *
   * @return classificationCategory
   */
  String getClassificationCategory();

  /**
   * Returns the count of the comments of the current {@linkplain Task}.
   *
   * @return numberOfComments
   */
  int getNumberOfComments();

  /**
   * Duplicates this Task without the internal and external id. All referenced {@linkplain
   * Attachment}s and {@linkplain ObjectReference}s are copied as well.
   *
   * @return a copy of this Task
   */
  Task copy();
}
