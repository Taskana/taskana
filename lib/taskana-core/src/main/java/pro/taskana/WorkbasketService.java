package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;

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
     * Create a new authorization for a specific workbasket and a specific user.
     * @param workbasket
     *            the choosen workbasket
     * @param user
     *            the choosen user
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
     * This method checks the authorization with the saved one.
     * @param workbasket
     *            the workbasket to check
     * @param userId
     *            the user to check
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
