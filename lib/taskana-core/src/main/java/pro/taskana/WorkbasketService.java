package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;

/**
 * This service manages the Workbaskets.
 */
public interface WorkbasketService {

    /**
     * Get Workbasket for a given id.
     * @param workbasketId TODO
     * @return the requested Workbasket
     * @throws WorkbasketNotFoundException TODO
     */
    Workbasket getWorkbasket(String workbasketId) throws WorkbasketNotFoundException;

    /**
     * Get all available Workbaskets.
     * @return a list containing all workbaskets
     */
    List<Workbasket> getWorkbaskets();

    /**
     * Create a new Workbasket.
     * @param workbasket
     *            The workbasket to create
     * @return TODO
     */
    Workbasket createWorkbasket(Workbasket workbasket);

    /**
     * Update a Workbasket.
     * @param workbasket
     *            The workbasket to update
     * @return TODO
     * @throws NotAuthorizedException TODO
     */
    Workbasket updateWorkbasket(Workbasket workbasket) throws NotAuthorizedException;

    /**
     * Create a new Workbasket Authorization with a Workbasket and a AccessId.
     * @param workbasketAccessItem
     *            the new workbasketAccessItem
     * @return TODO
     */
    WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem);

    /**
     * This method updates an Workbasket Authorization.
     * @param workbasketAccessItem
     *            the Authorization
     * @return the updated entity
     */
    WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem);

    /**
     * Get all authorizations of the workbasket.
     * @return a WorkbasketAccessItem list
     */
    List<WorkbasketAccessItem> getAllAuthorizations();

    /**
     * Deletes a specific authorization.
     * @param id
     *            the specific id
     */
    void deleteWorkbasketAuthorization(String id);

    /**
     * This method checks the authorization with the saved one for the actual User.
     *
     * @param workbasketId
     *            the workbasket we want to access
     * @param authorization
     *            the needed Authorization
     * @throws NotAuthorizedException TODO
     */
    void checkAuthorization(String workbasketId, WorkbasketAuthorization authorization) throws NotAuthorizedException;

    /**
     * This method get one WorkbasketAuthorization with an id.
     * @param id
     *            the id
     * @return the full {@link WorkbasketAccessItem}
     */
    WorkbasketAccessItem getWorkbasketAuthorization(String id);

    /**
     * Get all authorizations for a Workbasket.
     * @param workbasketId TODO
     * @return List of WorkbasketAccessItems
     */
    List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketId);

    /**
     * This method returns the workbaskets for which the current user has all permissions
     * specified in the permissions list.
     *
     * @param permission
     *            a List of WorkbasketAuthorization enums
     * @return all filtered workbaskets
     */
    List<Workbasket> getWorkbaskets(List<WorkbasketAuthorization> permission);

    /**
     * This method provides a query builder for quering the database.
     * @return a {@link WorkbasketQuery}
     */
    WorkbasketQuery createWorkbasketQuery();

    /**
     * Returns a new workbasket which is not persisted.
     * @return newWorkbasket
     */
    Workbasket newWorkbasket();
}
