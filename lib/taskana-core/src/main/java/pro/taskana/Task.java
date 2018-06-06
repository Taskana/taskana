package pro.taskana;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import pro.taskana.exceptions.InvalidArgumentException;

/**
 * task-Interface to specify attribute interactions.
 */
public interface Task {

    /**
     * Returns the current id of the task.
     *
     * @return taskId
     */
    String getId();

    /**
     * Gets the UserId of the task-creator.
     *
     * @return creator
     */
    String getCreator();

    /**
     * Returns the time when the task was {@link TaskState#READY}.
     *
     * @return created as exact {@link Instant}
     */
    Instant getCreated();

    /**
     * Returns the time when the task was set to {@link TaskState#CLAIMED} by/to a user.
     *
     * @return claimed as exact {@link Instant}
     */
    Instant getClaimed();

    /**
     * Returns the time when the task was set into {@link TaskState#COMPLETED}.
     *
     * @return completed as exact {@link Instant}
     */
    Instant getCompleted();

    /**
     * Returns the time when the task was modified the last time.
     *
     * @return modified as exact {@link Instant}
     */
    Instant getModified();

    /**
     * Returns the time when the work on this task was planned to be started.
     *
     * @return planned as exact {@link Instant}
     */
    Instant getPlanned();

    /**
     * Sets the time when the work on this task should be started.
     *
     * @param planned
     *            as exact {@link Instant}
     */
    void setPlanned(Instant planned);

    /**
     * Returns the time when this task should be finished.
     *
     * @return due as exact {@link Instant}
     */
    Instant getDue();

    /**
     * Return the name of the current task.
     *
     * @return name of the task
     */
    String getName();

    /**
     * Sets the name of the current task.
     *
     * @param name
     *            the name of the task
     */
    void setName(String name);

    /**
     * Return the task-description.
     *
     * @return description of a task
     */
    String getDescription();

    /**
     * Sets the description of the task.
     *
     * @param description
     *            the description of the task
     */
    void setDescription(String description);

    /**
     * Returns the numeric priority of a task.
     *
     * @return priority of the task
     */
    int getPriority();

    /**
     * Returns the current {@link TaskState} of the task.
     *
     * @return taskState
     */
    TaskState getState();

    /**
     * Returns the {@link ClassificationSummary} of the task.
     *
     * @return classification summary for the task
     */
    ClassificationSummary getClassificationSummary();

    /**
     * Sets the Classification key that - together with the Domain from this task's work basket - selects the
     * appropriate {@link Classification} for this task.
     *
     * @param classificationKey
     *            the classification key for the task
     */
    void setClassificationKey(String classificationKey);

    /**
     * Returns the key of the Workbasket where the task is stored in.
     *
     * @return workbasketKey
     */
    String getWorkbasketKey();

    /**
     * Returns the the Summary of the workbasket where the task is stored in.
     *
     * @return workbasketSummary
     */
    WorkbasketSummary getWorkbasketSummary();

    /**
     * Returns the Domain, to which the Task belongs at this moment.
     *
     * @return domain the current domain of the task
     */
    String getDomain();

    /**
     * Returns the businessProcessId of a task.
     *
     * @return businessProcessId
     */
    String getBusinessProcessId();

    /**
     * Sets the external business process id.
     *
     * @param businessProcessId
     */
    void setBusinessProcessId(String businessProcessId);

    /**
     * Returns the parentBusinessProcessId of a task.
     *
     * @return parentBusinessProcessId
     */
    String getParentBusinessProcessId();

    /**
     * Sets the parent business process id to group associated processes.
     *
     * @param parentBusinessProcessId
     */
    void setParentBusinessProcessId(String parentBusinessProcessId);

    /**
     * Return the id of the task-owner.
     *
     * @return taskOwnerId
     */
    String getOwner();

    /**
     * Sets the ownerId of this task.
     *
     * @param taskOwnerId
     *            the user id of the task's owner
     */
    void setOwner(String taskOwnerId);

    /**
     * Returns the {@link ObjectReference primaryObjectReference} of the task.
     *
     * @return primaryObjRef to task main-subject
     */
    ObjectReference getPrimaryObjRef();

    /**
     * Sets the {@link ObjectReference primaryObjectReference} of the task.
     *
     * @param primaryObjRef
     *            to task main-subject
     */
    void setPrimaryObjRef(ObjectReference primaryObjRef);

    /**
     * Return the isRead-flag, which flags a task as viewed at least one time.
     *
     * @return isRead-flag
     */
    boolean isRead();

    /**
     * Return the isTransferred-flag, which flags a task as transfered into an other workbasket.
     *
     * @return isTransferred-flag
     */
    boolean isTransferred();

    /**
     * Returns a Map of custom Attributes.
     *
     * @return customAttributes as {@link Map}
     */
    Map<String, String> getCustomAttributes();

    /**
     * Sets a Map of custom Attributes.
     *
     * @param customAttributes
     *            a {@link Map} that contains the custom attributes
     */
    void setCustomAttributes(Map<String, String> customAttributes);

    /**
     * Returns a Map of Callback info.
     *
     * @return callbackInfo as {@link Map}
     */
    Map<String, String> getCallbackInfo();

    /**
     * Sets a Map of callback info.
     *
     * @param callbackInfo
     *            a {@link Map} that contains the callback info
     */
    void setCallbackInfo(Map<String, String> callbackInfo);

    /**
     * Return the value for custom Attribute number num.
     *
     * @param num
     *            identifies which custom attribute is requested. Taskana concatenates "custom_" with num and the
     *            resulting String must match the name of the database column that contains the custom attribute. Valid
     *            values are "1", "2" .. "16"
     * @return the value of custom attribute number num
     * @throws InvalidArgumentException
     *             if num has not a value of "1", "2" ... "16"
     */
    String getCustomAttribute(String num) throws InvalidArgumentException;

    /**
     * Sets the value for custom Attribute number num.
     *
     * @param num
     *            identifies which custom attribute is to be set. Taskana concatenates "custom_" with num and the
     *            resulting String must match the name of the database column that contains the custom attribute. Valid
     *            values are "1", "2" .. "16"
     * @param value
     *            the value of the custom attribute to be set
     * @throws InvalidArgumentException
     *             if num has not a value of "1", "2" ... "16"
     */
    void setCustomAttribute(String num, String value) throws InvalidArgumentException;

    /**
     * Add an attachment.<br>
     * NULL will be ignored and an attachment with the same ID will be replaced by the newer one.<br>
     *
     * @param attachment
     *            the {@link Attachment attachment} to be added to the task
     */
    void addAttachment(Attachment attachment);

    /**
     * Return the attachments for this task. <br>
     * Do not use List.add()/addAll() for adding Elements, because it can cause redundant data. Use addAttachment().
     * Clear() and remove() can be used, because it´s a controllable change.
     *
     * @return the {@link List list} of {@link Attachment attachments} for this task
     */
    List<Attachment> getAttachments();

    /**
     * Returns the custom note for this Task.
     *
     * @return the custom note for this TAsk
     */
    String getNote();

    /**
     * Sets/Changing the custom note for this Task.
     *
     * @param note
     *            the custom note for this Task.
     */
    void setNote(String note);

    /**
     * Return a summary of the current Task.
     *
     * @return the TaskSummary object for the current task
     */
    TaskSummary asSummary();

    /**
     * Removes an attachment of the current task locally, when the ID is represented and does return the removed
     * attachment or null if there was no match.<br>
     * The changed Task need to be updated calling the {@link TaskService#updateTask(Task)}.
     *
     * @param attachmentID
     *            ID of the attachment which should be removed.
     * @return attachment which will be removed after updating OR null if there was no matching attachment
     */
    Attachment removeAttachment(String attachmentID);

    /**
     * Returns the category of the current classification.
     *
     * @return classificationCategory
     */
    String getClassificationCategory();
}
