package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketAuthorization;

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
     * @param domain
     *            the domain of the workbasket
     * @return the requested Workbasket
     * @throws WorkbasketNotFoundException
     *             If the Workbasket with workbasketId is not found
     * @throws NotAuthorizedException
     *             If the current user or group does not have the permissions for interactions.
     */
    Workbasket getWorkbasket(String workbasketKey, String domain)
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
     */
    Workbasket createWorkbasket(Workbasket workbasket)
        throws InvalidWorkbasketException;

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
     * Returns a new WorkbasketAccessItem which is not persisted.
     *
     * @param workbasketId
     *            the workbasket id used to identify the referenced workbasket
     * @param accessId
     *            the group id or user id for which access is controlled
     * @return new WorkbasketAccessItem
     */
    WorkbasketAccessItem newWorkbasketAccessItem(String workbasketId, String accessId);

    /**
     * Create and persist a new Workbasket Authorization with a Workbasket and a AccessId.
     *
     * @param workbasketAccessItem
     *            the new workbasketAccessItem
     * @return the created WorkbasketAccessItem
     * @throws InvalidArgumentException
     *             when the preconditions doesn´t match the required ones.
     */
    WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem)
        throws InvalidArgumentException;

    /**
     * This method updates an Workbasket Authorization.
     *
     * @param workbasketAccessItem
     *            the Authorization
     * @return the updated entity
     * @throws InvalidArgumentException
     *             if accessid or workbasketkey is changed in the workbasketAccessItem
     */
    WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem)
        throws InvalidArgumentException;

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
     * @param workbasketId
     *            the id of the workbasket we want to access
     * @param authorization
     *            the needed Authorization
     * @throws NotAuthorizedException
     *             if the current user has not the requested authorization for the specified workbasket
     */
    void checkAuthorization(String workbasketId, WorkbasketAuthorization authorization)
        throws NotAuthorizedException;

    /**
     * This method checks the authorization with the saved one for the actual User.
     *
     * @param workbasketKey
     *            the key of the workbasket we want to access
     * @param domain
     *            the domain of the workbasket we want to access
     * @param authorization
     *            the needed Authorization
     * @throws NotAuthorizedException
     *             if the current user has not the requested authorization for the specified workbasket
     */
    void checkAuthorization(String workbasketKey, String domain, WorkbasketAuthorization authorization)
        throws NotAuthorizedException;

    /**
     * Get all authorizations for a Workbasket.
     *
     * @param workbasketId
     *            the id of the Workbasket
     * @return List of WorkbasketAccessItems for the Workbasket with workbasketKey
     */
    List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketId);

    /**
     * Setting up the new WorkbasketAccessItems for a Workbasket. Already stored values will be completely replaced by
     * the current ones.
     *
     * @param workbasketId
     *            ID of the access-target workbasket.
     * @param wbAccessItems
     *            List of WorkbasketAccessItems which does replace all current stored ones.
     * @throws InvalidArgumentException
     *             will be thrown when the parameter is NULL or member doesn´t match the preconditions
     */
    void setWorkbasketAuthorizations(String workbasketId, List<WorkbasketAccessItem> wbAccessItems)
        throws InvalidArgumentException;

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
     * This method provides a query builder for querying the database.
     *
     * @return a {@link WorkbasketAccessItemQuery}
     */
    WorkbasketAccessItemQuery createWorkbasketAccessItemQuery();

    /**
     * Returns a new workbasket which is not persisted.
     *
     * @param key
     *            the workbasket key used to identify the workbasket
     * @param domain
     *            the domain of the new workbasket
     * @return new Workbasket
     */
    Workbasket newWorkbasket(String key, String domain);

    /**
     * Returns a set with all permissions of the current user at this workbasket.
     *
     * @param workbasketId
     *            the id of the referenced workbasket
     * @return a Set with all permissions
     */
    List<WorkbasketAuthorization> getPermissionsForWorkbasket(String workbasketId);

    /**
     * Returns the distribution targets for a given workbasket.
     *
     * @param workbasketId
     *            the id of the referenced workbasket
     * @return the distribution targets of the specified workbasket
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the specified workbasket
     * @throws WorkbasketNotFoundException
     *             if the workbasket doesn't exist
     */
    List<WorkbasketSummary> getDistributionTargets(String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException;

    /**
     * Returns the distribution targets for a given workbasket.
     *
     * @param workbasketKey
     *            the key of the referenced workbasket
     * @param domain
     *            the domain of the referenced workbasket
     * @return the distribution targets of the specified workbasket
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the specified workbasket
     * @throws WorkbasketNotFoundException
     *             if the workbasket doesn't exist
     */
    List<WorkbasketSummary> getDistributionTargets(String workbasketKey, String domain)
        throws NotAuthorizedException, WorkbasketNotFoundException;

    /**
     * Set the distribution targets for a workbasket.
     *
     * @param sourceWorkbasketId
     *            the id of the source workbasket for which the distribution targets are to be set
     * @param targetWorkbasketIds
     *            a list of the ids of the target workbaskets
     * @throws NotAuthorizedException
     *             if the current used doesn't have READ permission for the source workbasket
     * @throws WorkbasketNotFoundException
     *             if either the source workbasket or any of the target workbaskets don't exist
     */
    void setDistributionTargets(String sourceWorkbasketId, List<String> targetWorkbasketIds)
        throws NotAuthorizedException, WorkbasketNotFoundException;

    /**
     * Add a distribution target to a workbasket. If the specified distribution target exists already, the method
     * silently returns without doing anything.
     *
     * @param sourceWorkbasketId
     *            the id of the source workbasket
     * @param targetWorkbasketId
     *            the id of the target workbasket
     * @throws NotAuthorizedException
     *             if the current user doesn't have READ permission for the source workbasket
     * @throws WorkbasketNotFoundException
     *             if either the source workbasket or the target workbasket doesn't exist
     */
    void addDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException;

    /**
     * Remove a distribution target from a workbasket. If the the specified distribution target doesn't exist, the
     * method silently returns without doing anything.
     *
     * @param sourceWorkbasketId
     *            The id of the source workbasket
     * @param targetWorkbasketId
     *            The id of the target workbasket
     * @throws NotAuthorizedException
     *             If the current user doesn't have READ permission for the source workbasket
     */
    void removeDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
        throws NotAuthorizedException;

    /**
     * Deletes the workbasket by the given ID of it.
     *
     * @param workbasketId
     *            Id of the workbasket which should be deleted.
     * @throws NotAuthorizedException
     *             if the current user got no permissions for this interaction.
     * @throws WorkbasketNotFoundException
     *             if the workbasket does not exist.
     * @throws WorkbasketInUseException
     *             if the workbasket does contain task-content.
     * @throws InvalidArgumentException
     *             if the workbasketId is NULL or EMPTY
     */
    void deleteWorkbasket(String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException, WorkbasketInUseException, InvalidArgumentException;

    /**
     * Returns the distribution sources for a given workbasket.
     *
     * @param workbasketId
     *            the id of the referenced workbasket
     * @return the workbaskets that are distribution sources of the specified workbasket.
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the specified workbasket
     * @throws WorkbasketNotFoundException
     *             if the workbasket doesn't exist
     */
    List<WorkbasketSummary> getDistributionSources(String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException;

    /**
     * Returns the distribution sources for a given workbasket.
     *
     * @param workbasketKey
     *            the key of the referenced workbasket
     * @param domain
     *            the domain of the referenced workbasket
     * @return the workbaskets that are distribution sources of the specified workbasket.
     * @throws NotAuthorizedException
     *             if the current user has no read permission for the specified workbasket
     * @throws WorkbasketNotFoundException
     *             if the workbasket doesn't exist
     */
    List<WorkbasketSummary> getDistributionSources(String workbasketKey, String domain)
        throws NotAuthorizedException, WorkbasketNotFoundException;
}
