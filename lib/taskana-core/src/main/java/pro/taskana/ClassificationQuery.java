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
    ClassificationQuery keyIn(String... key);

    /**
     * Add your parentClassificationKey to your query.
     *
     * @param parentClassificationKey
     *            as String
     * @return the query
     */
    ClassificationQuery parentClassificationKeyIn(String... parentClassificationKey);

    /**
     * Add your category to your query.
     *
     * @param category
     *            as String
     * @return the query
     */
    ClassificationQuery categoryIn(String... category);

    /**
     * Add your type to your query.
     *
     * @param type
     *            as String
     * @return the query
     */
    ClassificationQuery typeIn(String... type);

    /**
     * Add your domains to your query which are used as filter.
     *
     * @param domain
     *            or domains for filtering.
     * @return the query
     */
    ClassificationQuery domainIn(String... domain);

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
     * @param nameIn
     *            as String
     * @return the query
     */
    ClassificationQuery nameIn(String... nameIn);

    /**
     * Add your name to your query. It will be compared in SQL with an LIKE.
     *
     * @param nameLike
     *            as String
     * @return the query
     */
    ClassificationQuery nameLike(String... nameLike);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE. If you use a wildcard like % then it
     * will be transmitted to the database.
     *
     * @param descriptionLike
     *            your description
     * @return the query
     */
    ClassificationQuery descriptionLike(String descriptionLike);

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
     * @param serviceLevelIn
     *            as String
     * @return the query
     */
    ClassificationQuery serviceLevelIn(String... serviceLevelIn);

    /**
     * Add your serviceLevel to your query. It will be compared in SQL with an LIKE.
     *
     * @param serviceLevelLike
     *            as String
     * @return the query
     */
    ClassificationQuery serviceLevelLike(String... serviceLevelLike);

    /**
     * Add your applicationEntryPoint to your query.
     *
     * @param applicationEntryPointIn
     *            name of the applications entrypoint
     * @return the query
     */
    ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn);

    /**
     * Add your applicationEntryPoint to your query. It will be compared in SQL with an LIKE.
     *
     * @param applicationEntryPointLike
     *            name of the applications entrypoint
     * @return the query
     */
    ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike);

    /**
     * Add custom1 to your query.
     *
     * @param custom1In
     *            filter for custom1
     * @return the query
     */
    ClassificationQuery custom1In(String... custom1In);

    /**
     * Add custom1 to your query.
     *
     * @param custom1Like
     *            filter for custom1 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom1Like(String... custom1Like);

    /**
     * Add custom2 to your query.
     *
     * @param custom2In
     *            filter for custom2
     * @return the query
     */
    ClassificationQuery custom2In(String... custom2In);

    /**
     * Add custom2 to your query.
     *
     * @param custom2Like
     *            filter for custom2 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom2Like(String... custom2Like);

    /**
     * Add custom3 to your query.
     *
     * @param custom3In
     *            filter for custom3
     * @return the query
     */
    ClassificationQuery custom3In(String... custom3In);

    /**
     * Add custom3 to your query.
     *
     * @param custom3Like
     *            filter for custom3 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom3Like(String... custom3Like);

    /**
     * Add custom4 to your query.
     *
     * @param custom4In
     *            filter for custom4
     * @return the query
     */
    ClassificationQuery custom4In(String... custom4In);

    /**
     * Add custom4 to your query.
     *
     * @param custom4Like
     *            filter for custom4 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom4Like(String... custom4Like);

    /**
     * Add custom5 to your query.
     *
     * @param custom5In
     *            filter for custom5
     * @return the query
     */
    ClassificationQuery custom5In(String... custom5In);

    /**
     * Add custom5 to your query.
     *
     * @param custom5Like
     *            filter for custom5 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom5Like(String... custom5Like);

    /**
     * Add custom6 to your query.
     *
     * @param custom6In
     *            filter for custom6
     * @return the query
     */
    ClassificationQuery custom6In(String... custom6In);

    /**
     * Add custom6 to your query.
     *
     * @param custom6Like
     *            filter for custom6 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom6Like(String... custom6Like);

    /**
     * Add custom7 to your query.
     *
     * @param custom7In
     *            filter for custom7
     * @return the query
     */
    ClassificationQuery custom7In(String... custom7In);

    /**
     * Add custom7 to your query.
     *
     * @param custom7Like
     *            filter for custom7 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom7Like(String... custom7Like);

    /**
     * Add custom8 to your query.
     *
     * @param custom8In
     *            filter for custom8
     * @return the query
     */
    ClassificationQuery custom8In(String... custom8In);

    /**
     * Add custom8 to your query.
     *
     * @param custom8Like
     *            filter for custom8 with a LIKE-query
     * @return the query
     */
    ClassificationQuery custom8Like(String... custom8Like);
}
