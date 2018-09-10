package pro.taskana;

/**
 * WorkbasketAccessItemQuery for generating dynamic SQL.
 */
public interface WorkbasketAccessItemQuery
    extends AbstractWorkbasketAccessItemQuery<WorkbasketAccessItemQuery, WorkbasketAccessItem> {

    /**
     * Extended version of {@link WorkbasketAccessItemQuery}.
     */
    interface Extended extends AbstractWorkbasketAccessItemQuery<Extended, WorkbasketAccessItemExtended> {

        /**
         * Add your unique entry workbasket key to your query as filter.
         *
         * @param keys
         *            the unique entry Keys
         * @return the query
         */
        Extended workbasketKeyIn(String... keys);

        /**
         * Sort the query result by workbasket key.
         *
         * @param sortDirection
         *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
         *            the result is sorted in ascending order
         * @return the query
         */
        Extended orderByWorkbasketKey(SortDirection sortDirection);

        /**
         * Add keys to your query. The keys are compared case-insensitively to the keys of access items with the SQL LIKE
         * operator. You may add a wildcard like '%' to search generically. If you specify multiple keys they are connected
         * with an OR operator, this is, the query searches access items workbaskets whose keys are like key1 or like key2, etc.
         *
         * @param key
         *            the keys as Strings
         * @return the query
         */
        Extended workbasketKeyLike(String... key);

        /**
         * Add keys to your query. The keys are compared case-insensitively to the keys of access items with the SQL LIKE
         * operator. You may add a wildcard like '%' to search generically. If you specify multiple keys they are connected
         * with an OR operator, this is, the query searches access items whose ids are like id1 or like id2, etc.
         *
         * @param ids
         *            the ids as Strings
         * @return the query
         */
        Extended accessIdLike(String... ids);
    }
}
