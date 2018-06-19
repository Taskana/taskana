package pro.taskana;

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
     * Add your Id to your query.
     *
     * @param id
     *            as String
     * @return the query
     */
    ClassificationQuery idIn(String... id);

    /**
     * Add your parentIds to your query.
     *
     * @param parentId
     *            as an array of Strings
     * @return the query
     */
    ClassificationQuery parentIdIn(String... parentId);

    /**
     * Add your parentKeys to your query.
     *
     * @param parentKey
     *            as an array of Strings
     * @return the query
     */
    ClassificationQuery parentKeyIn(String... parentKey);

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
    ClassificationQuery validInDomainEquals(Boolean validInDomain);

    /**
     * Add your created-Dates to your query.
     *
     * @param createdIn
     *            the {@link TimeInterval} within which the searched-for classifications were created.
     * @return the query
     */
    ClassificationQuery createdWithin(TimeInterval... createdIn);

    /**
     * Add your modified-Dates to your query.
     *
     * @param modifiedIn
     *            the {@link TimeInterval} within which the searched-for classifications were modified the last time.
     * @return the query
     */
    ClassificationQuery modifiedWithin(TimeInterval... modifiedIn);

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
    ClassificationQuery priorityIn(int... priorities);

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

    /**
     * Sort the query result by key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByKey(SortDirection sortDirection);

    /**
     * Sort the query result by the parent classification ID.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByParentId(SortDirection sortDirection);

    /**
     * Sort the query result by the parent classification key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByParentKey(SortDirection sortDirection);

    /**
     * Sort the query result by category.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCategory(SortDirection sortDirection);

    /**
     * Sort the query result by domain.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByDomain(SortDirection sortDirection);

    /**
     * Sort the query result by name.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByName(SortDirection sortDirection);

    /**
     * Sort the query result by service level.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByServiceLevel(SortDirection sortDirection);

    /**
     * Sort the query result by priority.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByPriority(SortDirection sortDirection);

    /**
     * Sort the query result by the application entry point name.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 1.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom1(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 2.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom2(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 3.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom3(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 4.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom4(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 5.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom5(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 6.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom6(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 7.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom7(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 8.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    ClassificationQuery orderByCustom8(SortDirection sortDirection);
}
