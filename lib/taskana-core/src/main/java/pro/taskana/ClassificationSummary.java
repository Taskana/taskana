package pro.taskana;

/**
 * Interface for ClassificationSummaries. This is a specific short model-object which only requieres the most important
 * informations. Specific ones can be load afterwards via ID.
 */
public interface ClassificationSummary {

    /**
     * Gets the id of the classification.
     *
     * @return classificationId
     */
    String getId();

    /**
     * Gets the key of the classification.
     *
     * @return classificationKey
     */
    String getKey();

    /**
     * Gets the category of the classification.
     *
     * @return classificationCategory
     */
    String getCategory();

    /**
     * Gets the type of the classification.
     *
     * @return classificationType
     */
    String getType();

    /**
     * Gets the domain of the classification.
     *
     * @return classificationDomain
     */
    String getDomain();

    /**
     * Gets the name of the classification.
     *
     * @return classificationName
     */
    String getName();

    /**
     * Gets the ID of the parent classification.
     *
     * @return parentId
     */
    String getParentId();

    /**
     * Gets the service level of the parent classification. It is a String in ISO-8601 duration format. See the parse()
     * method of {@code Duration} for details.
     *
     * @return the service level
     */
    String getServiceLevel();

    /**
     * Gets the priority of the lassification.
     *
     * @return the priority
     */
    int getPriority();
}
