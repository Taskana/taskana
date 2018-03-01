package pro.taskana;

import java.util.List;

/**
 * Main query interface.
 *
 * @author EH
 * @param <T>
 *            specifies the return type of the follwing methods
 */
public interface BaseQuery<T> {

    /**
     * This method will return a list of defined {@link T} objects. In case of a TaskQuery, this method can throw a
     * NotAuthorizedToQueryWorkbasketException.
     *
     * @return List containing elements of type T
     */
    List<T> list();

    /**
     * This method will return a list of defined {@link T} objects with specified offset and an limit. In case of a
     * TaskQuery, this method can throw a NotAuthorizedToQueryWorkbasketException.
     *
     * @param offset
     *            index of the first element which should be returned.
     * @param limit
     *            number of elements which should be returned beginning with offset.
     * @return List containing elements of type T
     */
    List<T> list(int offset, int limit);

    /**
     * This method will return all currently existing values of a DB-Table once. The order of the returning values can
     * be configured ASC oder DEC - DEFAULT at NULL is ASC. <br>
     * All called orderBy()-Methods will be override. Just the current column-values will be ordered itself by the given
     * direction.
     *
     * @param dbColumnName
     *            column name of a existing DB Table.
     * @return a list of all existing values.
     */
    List<String> listValues(String dbColumnName, SortDirection sortDirection);

    /**
     * This method will return all results for page X with a size of Y of the current query.<br>
     * Negative pageNumber/size will be changed to 0 and the last page got maybe less elements. In case of a TaskQuery,
     * this method can throw a NotAuthorizedToQueryWorkbasketException.
     *
     * @param pageNumber
     *            current pagination page starting at 0.
     * @param pageSize
     *            amount of elements for this page.
     * @return resulList for the current query starting at X and returning max Y elements.
     */
    default List<T> listPage(int pageNumber, int pageSize) {
        int offset = (pageNumber < 0) ? 0 : (pageNumber * pageSize);
        int limit = (pageSize < 0) ? 0 : pageSize;
        return list(offset, limit);
    }

    /**
     * This method will return a single object of {@link T}. In case of a TaskQuery, this method can throw a
     * NotAuthorizedToQueryWorkbasketException.
     *
     * @return T a single object of given Type.
     */
    T single();

    /**
     * Counting the amount of rows/results for the current query. This can be used for a pagination afterwards. In case
     * of a TaskQuery, this method can throw a NotAuthorizedToQueryWorkbasketException.
     *
     * @return resultRowCount
     */
    long count();

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
