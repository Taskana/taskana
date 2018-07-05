package pro.taskana;

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
     *  Gets the custom1 property of the workbasket.
     *
     * @return the workbasket's custom1 property.
     */
    String getCustom1();

    /**
     *  Gets the custom2 property of the workbasket.
     *
     * @return the workbasket's custom2 property.
     */
    String getCustom2();

    /**
     *  Gets the custom3 property of the workbasket.
     *
     * @return the workbasket's custom3 property.
     */
    String getCustom3();

    /**
     *  Gets the custom4 property of the workbasket.
     *
     * @return the workbasket's custom4 property.
     */
    String getCustom4();

    /**
     * Gets the orglevel1 property of the workbasket.
     *
     * @return the workbasket's orglevel1 property
     */
    String getOrgLevel1();

    /**
     * Gets the orglevel2 property of the workbasket.
     *
     * @return the workbasket's orglevel2 property
     */
    String getOrgLevel2();

    /**
     * Gets the orglevel3 property of the workbasket.
     *
     * @return the workbasket's orglevel3 property
     */
    String getOrgLevel3();

    /**
     * Gets the orglevel4 property of the workbasket.
     *
     * @return the workbasket's orglevel4 property
     */
    String getOrgLevel4();

}
