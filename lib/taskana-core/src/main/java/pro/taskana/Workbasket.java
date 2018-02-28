package pro.taskana;

import java.time.Instant;

/**
 * Workbasket entity interface.
 */
public interface Workbasket {

    /**
     * Returns the unique id of a workbasket.
     *
     * @return workbasketId
     */
    String getId();

    /**
     * Returns the date when the workbasket was created.
     *
     * @return created as Instant
     */
    Instant getCreated();

    /**
     * Returns the key of the workbasket.
     *
     * @return the key of the workbasket
     */
    String getKey();

    /**
     * Returns the domain of the workbasket.
     *
     * @return domain of the workbasket
     */
    String getDomain();

    /**
     * Returns the type of the workbasket.
     *
     * @return the type of the workbasket
     */
    WorkbasketType getType();

    /**
     * Sets the type of the workbasket.
     *
     * @param type
     *            the type of the workbasket
     */
    void setType(WorkbasketType type);

    /**
     * Returns the date when the workbasket was modified the last time.
     *
     * @return modified as Instant
     */
    Instant getModified();

    /**
     * Returns the name of the workbasket.
     *
     * @return workbasketName
     */
    String getName();

    /**
     * Sets the name of the workbasket.
     *
     * @param workbasketName
     *            the name of the workbasket
     */
    void setName(String workbasketName);

    /**
     * Returns the workbasket-descriptions.
     *
     * @return description
     */
    String getDescription();

    /**
     * Sets the workbasket-descriptions.
     *
     * @param description
     *            the description of the workbasket
     */
    void setDescription(String description);

    /**
     * Returns the Id of the workbasket-owner.
     *
     * @return ownerId
     */
    String getOwner();

    /**
     * Sets the owner-ID of the workbasket.
     *
     * @param owner
     *            of the current workbasket
     */
    void setOwner(String owner);

    /**
     * Return the value for the custom1 attribute.
     *
     * @return custom1
     */
    String getCustom1();

    /**
     * Sets the value for custom1 Attribute.
     *
     * @param custom1
     *            the custom1 property of the workbasket
     */
    void setCustom1(String custom1);

    /**
     * Return the value for the custom2 attribute.
     *
     * @return custom2
     */
    String getCustom2();

    /**
     * Sets the value for custom2 attribute.
     *
     * @param custom2
     *            the custom2 property of the workbasket
     */
    void setCustom2(String custom2);

    /**
     * Return the value for the custom3 attribute.
     *
     * @return custom3
     */
    String getCustom3();

    /**
     * Sets the value for custom3 attribute.
     *
     * @param custom3
     *            the custom3 property of the workbasket
     */
    void setCustom3(String custom3);

    /**
     * Return the value for the custom4 attribute.
     *
     * @return custom4
     */
    String getCustom4();

    /**
     * Sets the value for custom4 attribute.
     *
     * @param custom4
     *            the custom4 property of the workbasket
     */
    void setCustom4(String custom4);

    /**
     * Return the value for the orgLevel1 attribute.
     *
     * @return orgLevel1
     */
    String getOrgLevel1();

    /**
     * Sets the value for orgLevel1 attribute.
     *
     * @param orgLevel1
     *            the orgLevel1 property of the workbasket
     */
    void setOrgLevel1(String orgLevel1);

    /**
     * Return the value for the orgLevel2 attribute.
     *
     * @return orgLevel2
     */
    String getOrgLevel2();

    /**
     * Sets the value for orgLevel2 attribute.
     *
     * @param orgLevel2
     *            the orgLevel2 property of the workbasket
     */
    void setOrgLevel2(String orgLevel2);

    /**
     * Return the value for the orgLevel3 attribute.
     *
     * @return orgLevel3
     */
    String getOrgLevel3();

    /**
     * Sets the value for orgLevel3 attribute.
     *
     * @param orgLevel3
     *            the orgLevel3 property of the workbasket
     */
    void setOrgLevel3(String orgLevel3);

    /**
     * Return the value for the orgLevel4 attribute.
     *
     * @return orgLevel4
     */
    String getOrgLevel4();

    /**
     * Sets the value for orgLevel4 attribute.
     *
     * @param orgLevel4
     *            the orgLevel4 property of the workbasket
     */
    void setOrgLevel4(String orgLevel4);

    /**
     * Return a summary of the current workbasket.
     *
     * @return the WorkbasketSummary object for the current work basket
     */
    WorkbasketSummary asSummary();
}
