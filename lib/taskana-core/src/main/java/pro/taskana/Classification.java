package pro.taskana;

import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import pro.taskana.impl.ClassificationImpl;

/**
 * Interface used to specify the Classification-Model.
 */
@JsonDeserialize(as = ClassificationImpl.class)
public interface Classification extends ClassificationSummary {

    /**
     * Set/Change a reference to the current parent classification via ID. EMPTY if there is no parent.
     *
     * @param parentId
     *            The ID of the parent classification.
     */
    void setParentId(String parentId);

    /**
     * Set/Change a reference to the current parent classification via key. EMPTY if there is no parent.
     *
     * @param parentKey
     *            The key of the parent classification.
     */
    void setParentKey(String parentKey);

    /**
     * Set/Change the category of this classification.
     *
     * @param category
     *            The category of the classification.
     */
    void setCategory(String category);

    /**
     * Get the current domain-name of this classification.
     *
     * @return domain name
     */
    String getDomain();

    /**
     * Get a flag if the classification if currently valid in the used domain.
     *
     * @return isValidInDomain - flag
     */
    Boolean getIsValidInDomain();

    /**
     * Set/Change the flag which marks the classification as valid/invalid in the currently used domain.
     *
     * @param isValidInDomain
     *            - flag
     */
    void setIsValidInDomain(Boolean isValidInDomain);

    /**
     * Get the timestamp when this classification was as created.
     *
     * @return created as instant
     */
    Instant getCreated();

    /**
     * Get the timestamp when this classification was as modified the last time.
     *
     * @return modified as instant
     */
    Instant getModified();

    /**
     * Set/Change the classification name.
     *
     * @param name
     *            the name of the Classification
     */
    void setName(String name);

    /**
     * Get the description of a classification.
     *
     * @return description
     */
    String getDescription();

    /**
     * Set/Change the classification description.
     *
     * @param description
     *            the description of the Classification
     */
    void setDescription(String description);

    /**
     * Set/Change the numeric priority of a classification.
     *
     * @param priority
     *            the Priority of the Classification
     */
    void setPriority(int priority);

    /**
     * Set/Change the service level.
     *
     * @param serviceLevel
     *            the service level. Must be a String in ISO-8601 duration format. See the parse() method of
     *            {@code Duration} for details.
     */
    void setServiceLevel(String serviceLevel);

    /**
     * Get the logical name of the associated application entry point.
     *
     * @return applicationEntryPoint
     */
    String getApplicationEntryPoint();

    /**
     * Set the logical name of the associated application entry point.
     *
     * @param applicationEntryPoint
     *            The application entry point
     */
    void setApplicationEntryPoint(String applicationEntryPoint);

    /**
     * Set/Change the 1. custom-attribute.
     *
     * @param custom1
     *            the first custom attribute
     */
    void setCustom1(String custom1);

    /**
     * Set/Change the 2. custom-attribute.
     *
     * @param custom2
     *            the second custom attribute
     */
    void setCustom2(String custom2);

    /**
     * Set/Change the 3. custom-attribute.
     *
     * @param custom3
     *            the third custom attribute
     */
    void setCustom3(String custom3);

    /**
     * Set/Change the 4. custom-attribute.
     *
     * @param custom4
     *            the fourth custom attribute
     */
    void setCustom4(String custom4);

    /**
     * Set/Change the 5. custom-attribute.
     *
     * @param custom5
     *            the fifth custom attribute
     */
    void setCustom5(String custom5);

    /**
     * Set/Change the 6. custom-attribute.
     *
     * @param custom6
     *            the sixth custom attribute
     */
    void setCustom6(String custom6);

    /**
     * Set/Change the 7. custom-attribute.
     *
     * @param custom7
     *            the seventh custom attribute
     */
    void setCustom7(String custom7);

    /**
     * Set/Change the 8. custom-attribute.
     *
     * @param custom8
     *            the eight custom attribute
     */
    void setCustom8(String custom8);

    /**
     * Return a summary of the current Classification.
     *
     * @return the ClassificationSummary object for the current classification
     */
    ClassificationSummary asSummary();
}
