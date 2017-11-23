package pro.taskana;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;

import java.util.List;

/**
 * This service manages the Workbaskets.
 */
public interface WorkbasketService {

    /**
     * Get Workbasket for a given id.
     * @param workbasketId
     * @return the requested Workbasket
     */
    Workbasket getWorkbasket(String workbasketId) throws WorkbasketNotFoundException;

    /**
     * Get all available Workbaskets.
     * @return List<Workbasket> the list of all workbaskets
     */
    List<Workbasket> getWorkbaskets();

    /**
     * Create a new Workbasket.
     * @param workbasket
     *            The workbasket to create
     * @throws NotAuthorizedException
     */
    Workbasket createWorkbasket(Workbasket workbasket);

    /**
     * Update a Workbasket.
     * @param workbasket
     *            The workbasket to update
     * @throws NotAuthorizedException
     */
    Workbasket updateWorkbasket(Workbasket workbasket) throws NotAuthorizedException;

    /**
     * Create a new Workbasket Authorization with a Workbasket and a AccessId.
     * @param workbasketAccessItem
     *            the new workbasketAccessItem
     * @return
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
     * @throws WorkbasketNotFoundException
     *             if the workbasket do not exist
     * @throws NotAuthorizedException
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
     * @param workbasketId
     * @return List<WorkbasketAccessItem>
     */
    List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketId);

    /**
     * This method provides workbaskets via an permission.
     * @param permission
     *            as String like in this enum: {@link WorkbasketAuthorization}
     * @return all filtered workbaskets
     */
    List<Workbasket> getWorkbaskets(List<WorkbasketAuthorization> permission);

}
