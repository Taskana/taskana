package pro.taskana;

import pro.taskana.model.WorkbasketType;

/**
 * Interface for WorkbasketSummary. This is a specific short model-object which only contains the most important
 * information.
 */
public interface WorkbasketSummary {

    /**
     * Gets the id of the workbasket.
     *
     * @return workbasketId
     */
    String getId();

    /**
     * Gets the key of the workbasket.
     *
     * @return workbasketKey
     */
    String getKey();

    /**
     * Gets the name of the workbasket.
     *
     * @return workbasket's name
     */
    String getName();

    /**
     * Gets the description of the workbasket.
     *
     * @return workbasket's description
     */
    String getDescription();

    /**
     * Gets the owner of the workbasket.
     *
     * @return workbasket's owner
     */
    String getOwner();

    /**
     * Gets the domain of the workbasket.
     *
     * @return workbasket's domain
     */
    String getDomain();

    /**
     * Gets the type of the workbasket.
     *
     * @return workbasket's type
     */
    WorkbasketType getType();

    /**
     * Gets the orglevel1 property of the task.
     *
     * @return the task's orglevel1 property
     */
    String getOrgLevel1();

    /**
     * Gets the orglevel2 property of the task.
     *
     * @return the task's orglevel2 property
     */
    String getOrgLevel2();

    /**
     * Gets the orglevel3 property of the task.
     *
     * @return the task's orglevel3 property
     */
    String getOrgLevel3();

    /**
     * Gets the orglevel4 property of the task.
     *
     * @return the task's orglevel4 property
     */
    String getOrgLevel4();

}
