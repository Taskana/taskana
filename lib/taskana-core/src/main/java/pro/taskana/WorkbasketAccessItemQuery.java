package pro.taskana;

/**
 * WorkbasketAccessItemQuery for generating dynamic sql.
 */
public interface WorkbasketAccessItemQuery extends BaseQuery<WorkbasketAccessItem> {

    /**
     * Add your unique entry id to your query as filter.
     *
     * @param ids
     *            the unique entry IDs
     * @return the query
     */
    WorkbasketAccessItemQuery idIn(String... ids);

    /**
     * Add your workbasket id to your query.
     *
     * @param workbasketId
     *            the workbasket Id
     * @return the query
     */
    WorkbasketAccessItemQuery workbasketIdIn(String... workbasketId);

    /**
     * Add your accessIds to your query.
     *
     * @param accessId
     *            as access Ids
     * @return the query
     */
    WorkbasketAccessItemQuery accessIdIn(String... accessId);

    /**
     * Sort the query result by workbasket id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemQuery orderByWorkbasketId(SortDirection sortDirection);

    /**
     * Sort the query result by access Id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection);

    /**
     * Sort the query result by Id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketAccessItemQuery orderById(SortDirection sortDirection);
}
