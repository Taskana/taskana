package pro.taskana;

import java.sql.Timestamp;
import java.util.List;

import pro.taskana.model.WorkbasketType;

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
     * Returns the timestamp when the workbasket was created.
     *
     * @return created timestamp
     */
    Timestamp getCreated();

    /**
     * Returns the key of the workbasket.
     *
     * @return the key of the workbasket
     */
    String getKey();

    /**
     * Set the key of the workbasket.
     *
     * @param key
     *            the key of the workbasket
     */
    void setKey(String key);

    /**
     * Returns the domain of the workbasket.
     *
     * @return domain of the workbasket
     */
    String getDomain();

    /**
     * Set the domain of the workbasket.
     *
     * @param domain
     *            the domain of the workbasket
     */
    void setDomain(String domain);

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
     * Returns the timestamp when the workbasket was modified the last time.
     *
     * @return modified timestamp
     */
    Timestamp getModified();

    /**
     * Sets the time when the workbasket was modified the last time.
     *
     * @param modified
     *            the timestamp when the workbasket was last modified
     */
    void setModified(Timestamp modified);

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
     * Returns a list of all distribution targets.
     *
     * @return distributionTargets
     */
    List<Workbasket> getDistributionTargets();

    /**
     * Sets the list of distribution targets for this workbasket.
     *
     * @param distributionTargets
     *            the distribution targets of the workbasket
     */
    void setDistributionTargets(List<Workbasket> distributionTargets);
}
