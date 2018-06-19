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
     * Gets the key of the parent classification.
     *
     * @return parentKey
     */
    String getParentKey();

    /**
     * Gets the service level of the parent classification. It is a String in ISO-8601 duration format. See the parse()
     * method of {@code Duration} for details.
     *
     * @return the service level
     */
    String getServiceLevel();

    /**
     * Gets the priority of the classification.
     *
     * @return the priority
     */
    int getPriority();

    /**
     * Get the 1. custom-attribute.
     *
     * @return custom1
     */
    String getCustom1();

    /**
     * Set/Change the 1. custom-attribute.
     *
     * @param custom1
     *            the first custom attribute
     */
    void setCustom1(String custom1);

    /**
     * Get the 2. custom-attribute.
     *
     * @return custom2
     */
    String getCustom2();

    /**
     * Set/Change the 2. custom-attribute.
     *
     * @param custom2
     *            the second custom attribute
     */
    void setCustom2(String custom2);

    /**
     * Get the 3. custom-attribute.
     *
     * @return custom3
     */
    String getCustom3();

    /**
     * Set/Change the 3. custom-attribute.
     *
     * @param custom3
     *            the third custom attribute
     */
    void setCustom3(String custom3);

    /**
     * Get the 4. custom-attribute.
     *
     * @return custom4
     */
    String getCustom4();

    /**
     * Set/Change the 4. custom-attribute.
     *
     * @param custom4
     *            the fourth custom attribute
     */
    void setCustom4(String custom4);

    /**
     * Get the 5. custom-attribute.
     *
     * @return custom5
     */
    String getCustom5();

    /**
     * Set/Change the 5. custom-attribute.
     *
     * @param custom5
     *            the fifth custom attribute
     */
    void setCustom5(String custom5);

    /**
     * Get the 6. custom-attribute.
     *
     * @return custom6
     */
    String getCustom6();

    /**
     * Set/Change the 6. custom-attribute.
     *
     * @param custom6
     *            the sixth custom attribute
     */
    void setCustom6(String custom6);

    /**
     * Get the 7. custom-attribute.
     *
     * @return custom7
     */
    String getCustom7();

    /**
     * Set/Change the 7. custom-attribute.
     *
     * @param custom7
     *            the seventh custom attribute
     */
    void setCustom7(String custom7);

    /**
     * Get the 8. custom-attribute.
     *
     * @return custom8
     */
    String getCustom8();

    /**
     * Set/Change the 8. custom-attribute.
     *
     * @param custom8
     *            the eight custom attribute
     */
    void setCustom8(String custom8);

}
