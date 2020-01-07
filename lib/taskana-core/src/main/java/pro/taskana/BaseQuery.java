package pro.taskana;

import java.util.List;

/**
 * Main query interface.
 *
 * @author EH
 * @param <T> specifies the return type of the follwing methods
 * @param <U> specifies the type of the enum used
 */
public interface BaseQuery<T, U extends Enum<U> & QueryColumnName> {

  /**
   * This method will return a list of defined {@link T} objects. In case of a TaskQuery, this
   * method can throw a NotAuthorizedToQueryWorkbasketException.
   *
   * @return List containing elements of type T
   */
  List<T> list();

  /**
   * This method will return a list of defined {@link T} objects with specified offset and an limit.
   * In case of a TaskQuery, this method can throw a NotAuthorizedToQueryWorkbasketException.
   *
   * @param offset index of the first element which should be returned.
   * @param limit number of elements which should be returned beginning with offset.
   * @return List containing elements of type T
   */
  List<T> list(int offset, int limit);

  /**
   * This method will return all currently existing values of a DB-Table once. The order of the
   * returning values can be configured ASC oder DEC - DEFAULT at NULL is ASC. <br>
   * All called orderBy()-Methods will be override. Just the current column-values will be ordered
   * itself by the given direction.
   *
   * @param dbColumnName column name of a existing DB Table.
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return a list of all existing values.
   */
  List<String> listValues(U dbColumnName, SortDirection sortDirection);

  /**
   * This method will return all results for page X with a size of Y of the current query.<br>
   * Negative pageNumber/size will be changed to 1 or 0 and the last page might contains less
   * elements. In case of a TaskQuery, this method can throw a
   * NotAuthorizedToQueryWorkbasketException.
   *
   * @param pageNumber current pagination page starting at 1.
   * @param pageSize amount of elements for this page.
   * @return resulList for the current query starting at X and returning max Y elements.
   */
  default List<T> listPage(int pageNumber, int pageSize) {
    int offset = (pageNumber < 1) ? 0 : ((pageNumber - 1) * pageSize);
    int limit = (pageSize < 0) ? 0 : pageSize;
    return list(offset, limit);
  }

  /**
   * This method will return a single object of {@link T}. In case of a TaskQuery, this method can
   * throw a NotAuthorizedToQueryWorkbasketException.
   *
   * @return T a single object of given Type.
   */
  T single();

  /**
   * Counting the amount of rows/results for the current query. This can be used for a pagination
   * afterwards. In case of a TaskQuery, this method can throw a
   * NotAuthorizedToQueryWorkbasketException.
   *
   * @return resultRowCount
   */
  long count();

  default String[] toUpperCopy(String... source) {
    if (source == null || source.length == 0) {
      return null;
    } else {
      String[] target = new String[source.length];
      for (int i = 0; i < source.length; i++) {
        target[i] = source[i].toUpperCase();
      }
      return target;
    }
  }

  /**
   * Determines the sort direction.
   *
   * @author bbr
   */
  enum SortDirection {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private final String sortDirection;

    SortDirection(String sortDirection) {
      this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
      return sortDirection;
    }
  }
}
