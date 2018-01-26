package pro.taskana;

import java.time.Instant;

import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;

/**
 * Interface for TaskSummary. This is a specific short model-object which only contains the most important information.
 */
public interface TaskSummary {

    /**
     * Gets the id of the task..
     *
     * @return taskId
     */
    String getTaskId();

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
     * Gets the custom1 property of the task.
     *
     * @return the task's custom1 property
     */
    String getCustom1();

    /**
     * Gets the custom2 property of the task.
     *
     * @return the task's custom2 property
     */
    String getCustom2();

    /**
     * Gets the custom3 property of the task.
     *
     * @return the task's custom3 property
     */
    String getCustom3();

    /**
     * Gets the custom4 property of the task.
     *
     * @return the task's custom4 property
     */
    String getCustom4();

    /**
     * Gets the custom5 property of the task.
     *
     * @return the task's custom5 property
     */
    String getCustom5();

    /**
     * Gets the custom6 property of the task.
     *
     * @return the task's custom6 property
     */
    String getCustom6();

    /**
     * Gets the custom7 property of the task.
     *
     * @return the task's custom7 property
     */
    String getCustom7();

    /**
     * Gets the custom8 property of the task.
     *
     * @return the task's custom8 property
     */
    String getCustom8();

    /**
     * Gets the custom9 property of the task.
     *
     * @return the task's custom9 property
     */
    String getCustom9();

    /**
     * Gets the custom10 property of the task.
     *
     * @return the task's custom10 property
     */
    String getCustom10();
}
