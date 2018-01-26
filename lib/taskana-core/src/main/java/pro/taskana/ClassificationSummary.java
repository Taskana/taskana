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
}
