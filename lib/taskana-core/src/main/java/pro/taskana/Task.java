package pro.taskana;

import java.sql.Timestamp;
import java.util.Map;

import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;

/**
 * task-Interface to specify attribute interactions.
 */
public interface Task {

    /**
     * Returns the current id of the task.
     * @return taskId
     */
    String getId();

    /**
     * Returns the time when the task was {@link TaskState#READY}.
     * @return created as exact {@link Timestamp}
     */
    Timestamp getCreated();

    /**
     * Returns the time when the task was set to {@link TaskState#CLAIMED} by/to a user.
     * @return claimed as exact {@link Timestamp}
     */
    Timestamp getClaimed();

    /**
     * Returns the time when the task was set into {@link TaskState#COMPLETED}.
     * @return completed as exact {@link Timestamp}
     */
    Timestamp getCompleted();

    /**
     * Returns the time when the task was modified the last time.
     * @return modified as exact {@link Timestamp}
     */
    Timestamp getModified();

    /**
     * Returns the time when the work on this task was planned to be started.
     * @return planned as exact {@link Timestamp}
     */
    Timestamp getPlanned();

    /**
     * Sets the time when the work on this task should be started.
     * @param planned as exact {@link Timestamp}
     */
    void setPlanned(Timestamp planned);

    /**
     * Returns the time when this task should be finished.
     * @return due as exact {@link Timestamp}
     */
    Timestamp getDue();

    /**
     * Return the name of the current task.
     * @return name of the task
     */
    String getName();

    /**
     * Sets the name of the current task.
     * @param name of the task
     */
    void setName(String name);

    /**
     * Return the task-description.
     * @return description of a task
     */
    String getDescription();

    /**
     * Sets the description of the task.
     * @param description of the task
     */
    void setDescription(String description);

    /**
     * Returns the numeric priority of a task.
     * @return priority of the task
     */
    int getPriority();

    /**
     * Returns the current {@link TaskState} of the task.
     * @return taskState
     */
    TaskState getState();

    /**
     * Returns the {@link Classification} of the task.
     * @return classification for specification
     */
    Classification getClassification();

    /**
     * Sets the {@link Classification} to specify this kind of task.
     * @param classification of the task
     */
    void setClassification(Classification classification);

    /**
     * Returns the id of the Workbasket where the task is stored in.
     * @return workbasketId
     */
    String getWorkbasketId();

    /**
     * Sets the id of the Workbasket where the task should be stored in.
     * @param workbasketId
     */
    void setWorkbasketId(String workbasketId);

    /**
     * Returns the businessProcessId of a task.
     * @return businessProcessId
     */
    String getBusinessProcessId();

    /**
     * Returns the parentBusinessProcessId of a task.
     * @return parentBusinessProcessId
     */
    String getParentBusinessProcessId();

    /**
     * Return the id of the task-owner.
     * @return taskOwnerId
     */
    String getOwner();

    /**
     * Sets the ownerId of this task.
     * @param taskOwnerId
     */
    void setOwner(String taskOwnerId);

    /**
     * Returns the {@link ObjectReference primaryObjectReference} of the task.
     * @return primaryObjRef to task main-subject
     */
    ObjectReference getPrimaryObjRef();

    /**
     * Sets the {@link ObjectReference primaryObjectReference} of the task.
     * @param primaryObjRef to task main-subject
     */
    void setPrimaryObjRef(ObjectReference primaryObjRef);

    /**
     * Return the isRead-flag, which flags a task as viewed at least one time.
     * @return isRead-flag
     */
    boolean isRead();

    /**
     * Return the isTransferred-flag, which flags a task as transfered into an other workbasket.
     * @return isTransferred-flag
     */
    boolean isTransferred();

    /**
     * Returns a collection of customAttributes with a max. amount of 10 entries.
     * @return customAttributes as {@link Map}
     */
    Map<String, Object> getCustomAttributes();

    /**
     * Return the key for the 1. customAttribute.
     * @return custom1
     */
    String getCustom1();

    /**
     * Sets the key for customAttribute1.
     * @param custom1
     */
    void setCustom1(String custom1);

    /**
     * Return the key for the 2. customAttribute.
     * @return custom2
     */
    String getCustom2();

    /**
     * Sets the key for customAttribute2.
     * @param custom2
     */
    void setCustom2(String custom2);

    /**
     * Return the key for the 3. customAttribute.
     * @return custom3
     */
    String getCustom3();

    /**
     * Sets the key for customAttribute3.
     * @param custom3
     */
    void setCustom3(String custom3);

    /**
     * Return the key for the 4. customAttribute.
     * @return custom4
     */
    String getCustom4();

    /**
     * Sets the key for customAttribute4.
     * @param custom4
     */
    void setCustom4(String custom4);

    /**
     * Return the key for the 5. customAttribute.
     * @return custom5
     */
    String getCustom5();

    /**
     * Sets the key for customAttribute25.
     * @param custom5
     */
    void setCustom5(String custom5);

    /**
     * Return the key for the 6. customAttribute.
     * @return custom6
     */
    String getCustom6();

    /**
     * Sets the key for customAttribute6.
     * @param custom6
     */
    void setCustom6(String custom6);

    /**
     * Return the key for the 7. customAttribute.
     * @return custom7
     */
    String getCustom7();

    /**
     * Sets the key for customAttribute7.
     * @param custom7
     */
    void setCustom7(String custom7);

    /**
     * Return the key for the 8. customAttribute.
     * @return custom8
     */
    String getCustom8();

    /**
     * Sets the key for customAttribute8.
     * @param custom8
     */
    void setCustom8(String custom8);

    /**
     * Return the key for the 9. customAttribute.
     * @return custom9
     */
    String getCustom9();

    /**
     * Sets the key for customAttribute9.
     * @param custom9
     */
    void setCustom9(String custom9);

    /**
     * Return the key for the 10. customAttribute.
     * @return custom10
     */
    String getCustom10();

    /**
     * Sets the key for customAttribute10.
     * @param custom10
     */
    void setCustom10(String custom10);
}
