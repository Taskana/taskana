package pro.taskana;

import java.time.Instant;

/**
 * ClassificationQuery for generating dynamic sql.
 */
public interface ClassificationQuery extends BaseQuery<ClassificationSummary> {

    /**
     * Add your key to your query.
     *
     * @param key
     *            as String
     * @return the query
     */
    ClassificationQuery key(String... key);

    /**
     * Add your parentClassificationKey to your query.
     *
     * @param parentClassificationKey
     *            as String
     * @return the query
     */
    ClassificationQuery parentClassificationKey(String... parentClassificationKey);

    /**
     * Add your category to your query.
     *
     * @param category
     *            as String
     * @return the query
     */
    ClassificationQuery category(String... category);

    /**
     * Add your type to your query.
     *
     * @param type
     *            as String
     * @return the query
     */
    ClassificationQuery type(String... type);

    /**
     * Add your domains to your query which are used as filter.
     *
     * @param domain
     *            or domains for filtering.
     * @return the query
     */
    ClassificationQuery domain(String... domain);

    /**
     * Add to your query if the Classification shall be valid in its domain.
     *
     * @param validInDomain
     *            a simple flag showing if domain is valid
     * @return the query
     */
    ClassificationQuery validInDomain(Boolean validInDomain);

    /**
     * Add your created-Dates to your query.
     *
     * @param created
     *            date (as instant) of classification creation.
     * @return the query
     */
    ClassificationQuery created(Instant... created);

    /**
     * Add your name to your query.
     *
     * @param name
     *            as String
     * @return the query
     */
    ClassificationQuery name(String... name);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE. If you use a wildcard like % tehn it
     * will be transmitted to the database.
     *
     * @param description
     *            your description
     * @return the query
     */
    ClassificationQuery descriptionLike(String description);

    /**
     * Add your priority to your query.
     *
     * @param priorities
     *            as integers
     * @return the query
     */
    ClassificationQuery priority(int... priorities);

    /**
     * Add your serviceLevel to your query.
     *
     * @param serviceLevel
     *            as String
     * @return the query
     */
    ClassificationQuery serviceLevel(String... serviceLevel);

    /**
     * Add your applicationEntryPoint to your query.
     *
     * @param applicationEntryPoint
     *            name of the applications entrypoint
     * @return the query
     */
    ClassificationQuery applicationEntryPoint(String... applicationEntryPoint);

    /**
     * Add your customFields to your query.
     *
     * @param customFields
     *            filtering the content of all custom attributes
     * @return the query
     */
    ClassificationQuery customFields(String... customFields);
}
