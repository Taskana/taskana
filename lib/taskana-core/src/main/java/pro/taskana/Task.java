package pro.taskana;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;

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
     * Returns the time when the task was {@link TaskState#READY}.
     *
     * @return created as exact {@link Timestamp}
     */
    Timestamp getCreated();

    /**
     * Returns the time when the task was set to {@link TaskState#CLAIMED} by/to a user.
     *
     * @return claimed as exact {@link Timestamp}
     */
    Timestamp getClaimed();

    /**
     * Returns the time when the task was set into {@link TaskState#COMPLETED}.
     *
     * @return completed as exact {@link Timestamp}
     */
    Timestamp getCompleted();

    /**
     * Returns the time when the task was modified the last time.
     *
     * @return modified as exact {@link Timestamp}
     */
    Timestamp getModified();

    /**
     * Returns the time when the work on this task was planned to be started.
     *
     * @return planned as exact {@link Timestamp}
     */
    Timestamp getPlanned();

    /**
     * Sets the time when the work on this task should be started.
     *
     * @param planned
     *            as exact {@link Timestamp}
     */
    void setPlanned(Timestamp planned);

    /**
     * Returns the time when this task should be finished.
     *
     * @return due as exact {@link Timestamp}
     */
    Timestamp getDue();

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
     * Returns the {@link Classification} of the task.
     *
     * @return classification for specification
     */
    Classification getClassification();

    /**
     * Sets the {@link Classification} to specify this kind of task.
     *
     * @param classification
     *            the classification of the task
     */
    void setClassification(Classification classification);

    /**
     * Returns the key of the Workbasket where the task is stored in.
     *
     * @return workbasketKey
     */
    String getWorkbasketKey();

    /**
     * Sets the key of the Workbasket where the task should be stored in.
     *
     * @param workbasketKey
     *            the key of the workbasket
     */
    void setWorkbasketKey(String workbasketKey);

    /**
     * Returns the businessProcessId of a task.
     *
     * @return businessProcessId
     */
    String getBusinessProcessId();

    /**
     * Returns the parentBusinessProcessId of a task.
     *
     * @return parentBusinessProcessId
     */
    String getParentBusinessProcessId();

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
     * Returns a collection of customAttributes with a max. amount of 10 entries.
     *
     * @return customAttributes as {@link Map}
     */
    Map<String, Object> getCustomAttributes();

    /**
     * Return the value for the 1. customAttribute.
     *
     * @return custom1
     */
    String getCustom1();

    /**
     * Sets the value for customAttribute1.
     *
     * @param custom1
     *            the custom1 property of the task
     */
    void setCustom1(String custom1);

    /**
     * Return the value for the 2. customAttribute.
     *
     * @return custom2
     */
    String getCustom2();

    /**
     * Sets the value for customAttribute2.
     *
     * @param custom2
     *            the custom2 property of the task
     */
    void setCustom2(String custom2);

    /**
     * Return the value for the 3. customAttribute.
     *
     * @return custom3
     */
    String getCustom3();

    /**
     * Sets the value for customAttribute3.
     *
     * @param custom3
     *            the custom3 property of the task
     */
    void setCustom3(String custom3);

    /**
     * Return the value for the 4. customAttribute.
     *
     * @return custom4
     */
    String getCustom4();

    /**
     * Sets the value for customAttribute4.
     *
     * @param custom4
     *            the custom4 property of the task
     */
    void setCustom4(String custom4);

    /**
     * Return the value for the 5. customAttribute.
     *
     * @return custom5
     */
    String getCustom5();

    /**
     * Sets the value for customAttribute25.
     *
     * @param custom5
     *            the custom5 property of the task
     */
    void setCustom5(String custom5);

    /**
     * Return the value for the 6. customAttribute.
     *
     * @return custom6
     */
    String getCustom6();

    /**
     * Sets the value for customAttribute6.
     *
     * @param custom6
     *            the custom6 property of the task
     */
    void setCustom6(String custom6);

    /**
     * Return the value for the 7. customAttribute.
     *
     * @return custom7
     */
    String getCustom7();

    /**
     * Sets the value for customAttribute7.
     *
     * @param custom7
     *            the custom7 property of the task
     */
    void setCustom7(String custom7);

    /**
     * Return the value for the 8. customAttribute.
     *
     * @return custom8
     */
    String getCustom8();

    /**
     * Sets the value for customAttribute8.
     *
     * @param custom8
     *            the custom8 property of the task
     */
    void setCustom8(String custom8);

    /**
     * Return the value for the 9. customAttribute.
     *
     * @return custom9
     */
    String getCustom9();

    /**
     * Sets the value for customAttribute9.
     *
     * @param custom9
     *            the custom9 property of the task
     */
    void setCustom9(String custom9);

    /**
     * Return the value for the 10. customAttribute.
     *
     * @return custom10
     */
    String getCustom10();

    /**
     * Sets the value for customAttribute10.
     *
     * @param custom10
     *            the custom10 property of the task
     */
    void setCustom10(String custom10);

    /**
     * Add an attachment.
     *
     * @param attachment
     *            the {@link Attachment attachment} to be added to the task
     */
    void addAttachment(Attachment attachment);

    /**
     * Return the attachments for this task.
     *
     * @return the {@link List list} of {@link Attachment attachments} for this task
     */
    List<Attachment> getAttachments();
}
