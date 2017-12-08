package pro.taskana;

import java.util.Date;

/**
 * ClassificationQuery for generating dynamic sql.
 */
public interface ClassificationQuery extends BaseQuery<Classification> {


    /**
     * Add your parentClassification to your query.
     * @param parentClassificationId
     *            as String
     * @return the query
     */
    ClassificationQuery parentClassification(String... parentClassificationId);

    /**
     * Add your category to your query.
     * @param category
     *            as String
     * @return the query
     */
    ClassificationQuery category(String... category);

    /**
     * Add your type to your query.
     * @param type
     *            as String
     * @return the query
     */
    ClassificationQuery type(String... type);

    /**
     * Add your domain to your query.
     * @param domain
     * @return
     */
    ClassificationQuery domain(String... domain);

    /**
     * Add to your query if the Classification shall be valid in its domain.
     * @param validInDomain
     * @return
     */
    ClassificationQuery validInDomain(Boolean validInDomain);

    /**
     * Add your created-Dates to your query.
     * @param created
     * @return
     */
    ClassificationQuery created(Date... created);

    /**
     * Add your name to your query.
     * @param name
     *            as String
     * @return the query
     */
    ClassificationQuery name(String... name);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE.
     * If you use a wildcard like % tehn it will be transmitted to the database.
     * @param description
     *            your description
     * @return the query
     */
    ClassificationQuery descriptionLike(String description);

    /**
     * Add your priority to your query.
     * @param priorities
     *            as integers
     * @return the query
     */
    ClassificationQuery priority(int... priorities);

    /**
     * Add your serviceLevel to your query.
     * @param serviceLevel
     *            as String
     * @return the query
     */
    ClassificationQuery serviceLevel(String... serviceLevel);

    /**
     * Add your customFields to your query.
     * @param customFields
     * @return
     */
    ClassificationQuery customFields(String... customFields);

    /**
     * Define after which date the classifications should be valid.
     * @param validFrom
     * @return
     */
    ClassificationQuery validFrom(Date... validFrom);

    /**
     * Define until which date the classifications should be valid.
     * @param validUntil
     * @return
     */
    ClassificationQuery validUntil(Date... validUntil);
}
