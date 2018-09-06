package pro.taskana;

/**
 * WorkbasketAccessItemQuery for generating dynamic sql.
 */
public interface WorkbasketAccessItemExtendedQuery extends BaseQuery<WorkbasketAccessItemExtended> {

    /**
     * Add your unique entry workbasket key to your query as filter.
     *
     * @param keys
     *            the unique entry Keys
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery workbasketKeyIn(String... keys);

    /**
     * Sort the query result by workbasket key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery orderByWorkbasketKey(SortDirection sortDirection);

    /**
     * Add your unique entry id to your query as filter.
     *
     * @param ids
     *            the unique entry IDs
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery idIn(String... ids);

    /**
     * Add your workbasket id to your query.
     *
     * @param workbasketId
     *            the workbasket Id
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery workbasketIdIn(String... workbasketId);

    /**
     * Add your accessIds to your query.
     *
     * @param accessId
     *            as access Ids
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery accessIdIn(String... accessId);

    /**
     * Sort the query result by workbasket id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery orderByWorkbasketId(SortDirection sortDirection);

    /**
     * Sort the query result by access Id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery orderByAccessId(SortDirection sortDirection);

    /**
     * Sort the query result by Id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery orderById(SortDirection sortDirection);

    /**
     * Add keys to your query. The keys are compared case-insensitively to the keys of access items with the SQL LIKE
     * operator. You may add a wildcard like '%' to search generically. If you specify multiple keys they are connected
     * with an OR operator, this is, the query searches access items workbaskets whose keys are like key1 or like key2, etc.
     *
     * @param key
     *            the keys as Strings
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery workbasketKeyLike(String... key);

    /**
     * Add keys to your query. The keys are compared case-insensitively to the keys of access items with the SQL LIKE
     * operator. You may add a wildcard like '%' to search generically. If you specify multiple keys they are connected
     * with an OR operator, this is, the query searches access items whose ids are like id1 or like id2, etc.
     *
     * @param ids
     *            the ids as Strings
     * @return the query
     */
    WorkbasketAccessItemExtendedQuery accessIdLike(String... ids);
}
