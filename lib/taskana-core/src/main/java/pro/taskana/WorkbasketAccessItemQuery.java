package pro.taskana;

/**
 * WorkbasketAccessItemQuery for generating dynamic sql.
 */
public interface WorkbasketAccessItemQuery extends BaseQuery<WorkbasketAccessItem> {

    /**
     * Add your workbasket key to your query.
     *
     * @param workbasketKey
     *            the workbasket key
     * @return the query
     */
    WorkbasketAccessItemQuery workbasketKeyIn(String... workbasketKey);

    /**
     * Add your accessIds to your query.
     *
     * @param accessId
     *            as access Ids
     * @return the query
     */
    WorkbasketAccessItemQuery accessIdIn(String... accessId);

    /**
     * Sort the query result by workbasket key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemQuery orderByWorkbasketKey(SortDirection sortDirection);

    /**
     * Sort the query result by access Id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection);
}
