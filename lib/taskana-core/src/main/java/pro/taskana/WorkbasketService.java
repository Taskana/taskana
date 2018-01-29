package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;

/**
 * This service manages Workbaskets.
 */
public interface WorkbasketService {

    /**
     * Get Workbasket for a given id.
     *
     * @param workbasketId
     *            the Id of the Workbasket requested
     * @return the requested Workbasket
     * @throws WorkbasketNotFoundException
     *             If the Workbasket with workbasketId is not found
     * @throws NotAuthorizedException
     *             If the current user or group does not have the permissions for interactions.
     */
    Workbasket getWorkbasket(String workbasketId)
        throws WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Get Workbasket for a given key.
     *
     * @param workbasketKey
     *            the Key of the Workbasket requested
     * @return the requested Workbasket
     * @throws WorkbasketNotFoundException
     *             If the Workbasket with workbasketId is not found
     * @throws NotAuthorizedException
     *             If the current user or group does not have the permissions for interactions.
     */
    Workbasket getWorkbasketByKey(String workbasketKey)
        throws WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Get all available Workbaskets without checking any permission.
     *
     * @return a list containing all Workbasket Summaries
     */
    List<WorkbasketSummary> getWorkbaskets();

    /**
     * Create a new Workbasket.
     *
     * @param workbasket
     *            The Workbasket to create
     * @return the created and persisted Workbasket
     * @throws InvalidWorkbasketException
     *             If a required property of the Workbasket is not set.
     * @throws WorkbasketNotFoundException
     *             If the to be created work basket references a distribution target that does not exist.
     * @throws NotAuthorizedException
     *             If the current user or group does not have the permissions for interactions.
     */
    Workbasket createWorkbasket(Workbasket workbasket)
        throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Update a Workbasket.
     *
     * @param workbasket
     *            The Workbasket to update
     * @return the updated Workbasket
     * @throws InvalidWorkbasketException
     *             if a required property of the workbasket is not set
     * @throws WorkbasketNotFoundException
     *             if the updated work basket references a distribution target that does not exist
     * @throws NotAuthorizedException
     *             if the current user is not authorized to update the work basket
     */
    Workbasket updateWorkbasket(Workbasket workbasket)
        throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException;

    /**
     * Create and persist a new Workbasket Authorization with a Workbasket and a AccessId.
     *
     * @param workbasketAccessItem
     *            the new workbasketAccessItem
     * @return the created WorkbasketAccessItem
     */
    WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem);

    /**
     * This method updates an Workbasket Authorization.
     *
     * @param workbasketAccessItem
     *            the Authorization
     * @return the updated entity
     */
    WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem);

    /**
     * Get all authorizations of the workbasket.
     *
     * @return a WorkbasketAccessItem list
     */
    List<WorkbasketAccessItem> getAllAuthorizations();

    /**
     * Deletes a specific authorization.
     *
     * @param id
     *            the id of the WorbasketAccessItem to be deleted
     */
    void deleteWorkbasketAuthorization(String id);

    /**
     * This method checks the authorization with the saved one for the actual User.
     *
     * @param workbasketKey
     *            the key of the workbasket we want to access
     * @param authorization
     *            the needed Authorization
     * @throws NotAuthorizedException
     *             if the current user has not the requested authorization fot the specified workbasket
     */
    void checkAuthorization(String workbasketKey, WorkbasketAuthorization authorization) throws NotAuthorizedException;

    /**
     * This method get one WorkbasketAccessItem with an id.
     *
     * @param id
     *            the id of the requested WorkbasketAccessItem
     * @return the full {@link WorkbasketAccessItem}
     */
    WorkbasketAccessItem getWorkbasketAuthorization(String id);

    /**
     * Get all authorizations for a Workbasket.
     *
     * @param workbasketKey
     *            the key of the Workbasket
     * @return List of WorkbasketAccessItems for the Workbasket with workbasketKey
     */
    List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketKey);

    /**
     * This method returns the workbaskets for which the current user has all permissions specified in the permissions
     * list.
     *
     * @param permission
     *            a List of WorkbasketAuthorization enums
     * @return the summaries of all Workbaskets for which the current user has the specified authorizations
     */
    List<WorkbasketSummary> getWorkbaskets(List<WorkbasketAuthorization> permission);

    /**
     * This method provides a query builder for querying the database.
     *
     * @return a {@link WorkbasketQuery}
     */
    WorkbasketQuery createWorkbasketQuery();

    /**
     * Returns a new workbasket which is not persisted.
     *
     * @return newWorkbasket
     */
    Workbasket newWorkbasket();

    /**
     * Returns a set with all permissions of the current user at this workbasket.
     *
     * @param workbasketKey
     *            The key of the referenced workbasket
     * @return a Set with all permissions
     */
    List<WorkbasketAuthorization> getPermissionsForWorkbasket(String workbasketKey);

}
