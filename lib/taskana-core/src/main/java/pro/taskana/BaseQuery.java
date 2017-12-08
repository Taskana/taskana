package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Main query interface.
 * @author EH
 * @param <T>
 *            specifies the return type of the follwing methods
 */
public interface BaseQuery<T> {

    /**
     * This method will return a list of defined {@link T} objects.
     * @return TODO
     * @throws NotAuthorizedException TODO
     */
    List<T> list() throws NotAuthorizedException;

    /**
     * This method will return a list of defined {@link T} objects with specified
     * offset and an limit.
     * @param offset TODO
     * @param limit TODO
     * @return TODO
     * @throws NotAuthorizedException TODO
     */
    List<T> list(int offset, int limit) throws NotAuthorizedException;

    /**
     * This method will return a single object of {@link T}.
     * @return TODO
     * @throws NotAuthorizedException TODO
     */
    T single() throws NotAuthorizedException;

}
