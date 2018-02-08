package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Main query interface.
 *
 * @author EH
 * @param <T>
 *            specifies the return type of the follwing methods
 */
public interface BaseQuery<T> {

    /**
     * This method will return a list of defined {@link T} objects.
     *
     * @return List containing elements of type T
     * @throws NotAuthorizedException
     *             if the user is not authorized to perform this query
     */
    List<T> list() throws NotAuthorizedException;

    /**
     * This method will return a list of defined {@link T} objects with specified offset and an limit.
     *
     * @param offset
     *            index of the first element which should be returned.
     * @param limit
     *            number of elements which should be returned beginning with offset.
     * @return List containing elements of type T
     * @throws NotAuthorizedException
     *             if the user is not authorized to perform this query
     */
    List<T> list(int offset, int limit) throws NotAuthorizedException;

    /**
     * This method will return all results for page X with a size of Y of the current query.<br>
     * Negative pageNumber/size will be changed to 0 and the last page got maybe less elements.
     *
     * @param pageNumber
     *            current pagination page starting at 0.
     * @param pageSize
     *            amount of elements for this page.
     * @return resulList for the current query starting at X and returning max Y elements.
     * @throws NotAuthorizedException
     *             if the user is not authorized to perform this query
     */
    default List<T> listPage(int pageNumber, int pageSize) throws NotAuthorizedException {
        int offset = (pageNumber < 0) ? 0 : (pageNumber * pageSize);
        int limit = (pageSize < 0) ? 0 : pageSize;
        return list(offset, limit);
    }

    /**
     * This method will return a single object of {@link T}.
     *
     * @return T a single object of given Type.
     * @throws NotAuthorizedException
     *             if the user is not authorized to perform this query
     */
    T single() throws NotAuthorizedException;

    /**
     * Counting the amount of rows/results for the current query. This can be used for a pagination afterwards.
     *
     * @throws NotAuthorizedException
     *             when permissions not granted.
     * @return resultRowCount
     */
    long count() throws NotAuthorizedException;

    /**
     * Determines the sort direction.
     *
     * @author bbr
     */
    enum SortDirection {
        ASCENDING,
        DESCENDING
    }

}
