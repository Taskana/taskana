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
     * This method will return a single object of {@link T}.
     *
     * @return T a single object of given Type.
     * @throws NotAuthorizedException
     *             if the user is not authorized to perform this query
     */
    T single() throws NotAuthorizedException;

}
