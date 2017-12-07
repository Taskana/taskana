package pro.taskana;

import java.sql.Date;

/**
 * Interface used to specify the Classification-Model.
 */
public interface Classification {

    /**
     * @return unique classification ID
     */
    String getId();

    /**
     * Set/Change the classification-ID. Only ID+Domain need to be unique.
     * @param id
     */
    void setId(String id);

    /**
     * Used to get the ID of the parent classification.
     * There will be no value if the current classification is
     * a parent-classification.
     * @return unique ID or null if parent itself.
     */
    String getParentClassificationId();

    /**
     * Set/Change a reference to the current parent classification via ID.
     * If this field would be set to NULL the classification will become
     * a parent-classification itself.
     * @param parentClassificationId
     */
    void setParentClassificationId(String parentClassificationId);

    /**
     * @return category of this classification.
     */
    String getCategory();

    /**
     * Set/Change the category of this classification.
     * @param category
     */
    void setCategory(String category);

    /**
     * Get the type of the current classification.
     * @return type
     */
    String getType();

    /**
     * Set/Change the current type of the classification.
     * @param type
     */
    void setType(String type);

    /**
     * Get the current domain-name of this
     * classification.
     * @return domain name
     */
    String getDomain();

    /**
     * Set/Change the domain of this classification.
     * @param domain name
     */
    void setDomain(String domain);

    /**
     * Get a flag if the classification if currently valid
     * in the used domain.
     * @return isValidInDomain - flag
     */
    Boolean getIsValidInDomain();

    /**
     * Set/Change the flag which marks the classification as
     * valid/invalid in the currently used domain.
     * @param isValidInDomain - flag
     */
    void setIsValidInDomain(Boolean isValidInDomain);

    /**
     * Get the Date when this classification was as created.
     * @return created as date
     */
    Date getCreated();

    /**
     * Get the classification name.
     * @return name
     */
    String getName();

    /**
     * Set/Change the classification name.
     * @param name
     */
    void setName(String name);

    /**
     * Get the description of a classification.
     * @return description
     */
    String getDescription();

    /**
     * Set/Change the classification description.
     * @param description
     */
    void setDescription(String description);

    /**
     * Get the current classification priority (numeric).
     * @return priority
     */
    int getPriority();

    /**
     * Set/Change the numeric priority of a classification.
     * @param priority
     */
    void setPriority(int priority);

    /**
     * Get the current service level.
     * @return serviceLevel
     */
    String getServiceLevel();

    /**
     * Set/Change the security level.
     * @param serviceLevel
     */
    void setServiceLevel(String serviceLevel);

    /**
     * Get the 1. custom-attribute.
     * @return custom1
     */
    String getCustom1();

    /**
     * Set/Change the 1. custom-attribute.
     * @param custom1
     */
    void setCustom1(String custom1);

    /**
     * Get the 2. custom-attribute.
     * @return custom2
     */
    String getCustom2();

    /**
     * Set/Change the 2. custom-attribute.
     * @param custom2
     */
    void setCustom2(String custom2);

    /**
     * Get the 3. custom-attribute.
     * @return custom3
     */
    String getCustom3();

    /**
     * Set/Change the 3. custom-attribute.
     * @param custom3
     */
    void setCustom3(String custom3);

    /**
     * Get the 4. custom-attribute.
     * @return custom4
     */
    String getCustom4();

    /**
     * Set/Change the 4. custom-attribute.
     * @param custom4
     */
    void setCustom4(String custom4);

    /**
     * Get the 5. custom-attribute.
     * @return custom5
     */
    String getCustom5();

    /**
     * Set/Change the 5. custom-attribute.
     * @param custom5
     */
    void setCustom5(String custom5);

    /**
     * Get the 6. custom-attribute.
     * @return custom6
     */
    String getCustom6();


    /**
     * Set/Change the 6. custom-attribute.
     * @param custom6
     */
    void setCustom6(String custom6);

    /**
     * Get the 7. custom-attribute.
     * @return custom7
     */
    String getCustom7();

    /**
     * Set/Change the 7. custom-attribute.
     * @param custom7
     */
    void setCustom7(String custom7);

    /**
     * Get the 8. custom-attribute.
     * @return custom8
     */
    String getCustom8();

    /**
     * Set/Change the 8. custom-attribute.
     * @param custom8
     */
    void setCustom8(String custom8);

    /**
     * Get the sql-date since/when the classification is valid from.
     * @return validFrom
     */
    Date getValidFrom();

    /**
     * Get the sql-date until the classification is valid.
     * @return validUntil
     */
    Date getValidUntil();
}
