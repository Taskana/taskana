package pro.taskana;

import java.sql.Timestamp;
import java.util.List;

/**
 *  Workbasket entity interface.
 */
 public interface Workbasket {

     /**
      * Returns the unique id of a workbasket.
      * @return workbasketId
      */
    String getId();

    /**
     * Returns the timestamp when the workbasket was created.
     * @return created timestamp
     */
    Timestamp getCreated();

    /**
     * Returns the timestamp when the workbasket was modified the last time.
     * @return modified timestamp
     */
    Timestamp getModified();

    /**
     * Sets the time when the workbasket was modified the last time.
     * @param modified timestamp
     */
    void setModified(Timestamp modified);

    /**
     * Returns the name of the workbasket.
     * @return workbasketName
     */
    String getName();

    /**
     * Sets the name of the workbasket.
     * @param workbasketName
     */
    void setName(String workbasketName);

    /**
     * Returns the workbasket-descriptions.
     * @return description
     */
    String getDescription();

    /**
     * Sets the workbasket-descriptions.
     * @param description
     */
    void setDescription(String description);

    /**
     * Returns the Id of the workbasket-owner.
     * @return ownerId
     */
    String getOwner();

    /**
     * Returns a list of all distribution targets.
     * @return distributionTargets
     */
    List<Workbasket> getDistributionTargets();

    /**
     * Sets the list of distribution targets for this workbasket.
     * @param distributionTargets
     */
    void setDistributionTargets(List<Workbasket> distributionTargets);
}
