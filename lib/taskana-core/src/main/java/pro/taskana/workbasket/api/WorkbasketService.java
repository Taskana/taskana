package pro.taskana.workbasket.api;

import java.util.List;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** This service manages Workbaskets. */
public interface WorkbasketService {

  /**
   * Get Workbasket for a given id.
   *
   * @param workbasketId the Id of the Workbasket requested
   * @return the requested Workbasket
   * @throws WorkbasketNotFoundException If the Workbasket with workbasketId is not found
   * @throws NotAuthorizedException If the current user or group does not have the permissions for
   *     interactions.
   */
  Workbasket getWorkbasket(String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedException;

  /**
   * Get Workbasket for a given key.
   *
   * @param workbasketKey the Key of the Workbasket requested
   * @param domain the domain of the workbasket
   * @return the requested Workbasket
   * @throws WorkbasketNotFoundException If the Workbasket with workbasketId is not found
   * @throws NotAuthorizedException If the current user or group does not have the permissions for
   *     interactions.
   */
  Workbasket getWorkbasket(String workbasketKey, String domain)
      throws WorkbasketNotFoundException, NotAuthorizedException;

  /**
   * Create a new Workbasket.
   *
   * @param workbasket The Workbasket to create
   * @return the created and persisted Workbasket
   * @throws InvalidWorkbasketException If a required property of the Workbasket is not set.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketAlreadyExistException if the workbasket exists already
   * @throws DomainNotFoundException if the domain does not exist in the configuration.
   */
  Workbasket createWorkbasket(Workbasket workbasket)
      throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException;

  /**
   * Update a Workbasket.
   *
   * @param workbasket The Workbasket to update
   * @return the updated Workbasket
   * @throws InvalidWorkbasketException if workbasket name or type is invalid
   * @throws NotAuthorizedException if the current user is not authorized to update the work basket
   * @throws WorkbasketNotFoundException if the workbasket cannot be found.
   * @throws ConcurrencyException if an attempt is made to update the workbasket and another user
   *     updated it already
   */
  Workbasket updateWorkbasket(Workbasket workbasket)
      throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketNotFoundException,
          ConcurrencyException;

  /**
   * Returns a new WorkbasketAccessItem which is not persisted.
   *
   * @param workbasketId the workbasket id used to identify the referenced workbasket
   * @param accessId the group id or user id for which access is controlled
   * @return new WorkbasketAccessItem
   */
  WorkbasketAccessItem newWorkbasketAccessItem(String workbasketId, String accessId);

  /**
   * Create and persist a new {@link WorkbasketAccessItem} with a WorkbasketId, an accessId and
   * permissions.
   *
   * @param workbasketAccessItem the new workbasketAccessItem
   * @return the created WorkbasketAccessItem
   * @throws InvalidArgumentException if the preconditions dont match the required ones.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketNotFoundException if the workbasketAccessItem refers to a not existing
   *     workbasket *
   * @throws WorkbasketAccessItemAlreadyExistException if there exists already a
   *     WorkbasketAccessItem for the same access id and workbasket
   */
  WorkbasketAccessItem createWorkbasketAccessItem(WorkbasketAccessItem workbasketAccessItem)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException;

  /**
   * This method updates a {@link WorkbasketAccessItem}.
   *
   * @param workbasketAccessItem the {@link WorkbasketAccessItem}
   * @return the updated entity
   * @throws InvalidArgumentException if accessid or workbasketId is changed in the
   *     workbasketAccessItem
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   */
  WorkbasketAccessItem updateWorkbasketAccessItem(WorkbasketAccessItem workbasketAccessItem)
      throws InvalidArgumentException, NotAuthorizedException;

  /**
   * Deletes a specific {@link WorkbasketAccessItem}.
   *
   * @param id the id of the WorbasketAccessItem to be deleted
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   */
  void deleteWorkbasketAccessItem(String id) throws NotAuthorizedException;

  /**
   * This method checks the authorization for the actual User.
   *
   * @param workbasketId the id of the workbasket we want to access
   * @param permission the needed {@link WorkbasketPermission} If more than one permission is
   *     specified, the current user needs all of them.
   * @throws NotAuthorizedException if the current user has not the requested authorization for the
   *     specified workbasket
   * @throws WorkbasketNotFoundException if the workbasket cannot be found for the given ID.
   */
  void checkAuthorization(String workbasketId, WorkbasketPermission... permission)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * This method checks the authorization for the actual User.
   *
   * @param workbasketKey the key of the workbasket we want to access
   * @param domain the domain of the workbasket we want to access
   * @param permission the needed {@link WorkbasketPermission}. If more than one permission is
   *     specified, the current user needs all of them.
   * @throws NotAuthorizedException if the current user has not the requested permission for the
   *     specified workbasket
   * @throws WorkbasketNotFoundException if no workbasket can be found for the given key+domain
   *     values.
   */
  void checkAuthorization(String workbasketKey, String domain, WorkbasketPermission... permission)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Get all {@link WorkbasketAccessItem s} for a Workbasket.
   *
   * @param workbasketId the id of the Workbasket
   * @return List of WorkbasketAccessItems for the Workbasket with workbasketKey
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   */
  List<WorkbasketAccessItem> getWorkbasketAccessItems(String workbasketId)
      throws NotAuthorizedException;

  /**
   * Setting up the new WorkbasketAccessItems for a Workbasket. Already stored values will be
   * completely replaced by the current ones.
   *
   * <p>Preconditions for each {@link WorkbasketAccessItem} in {@code wbAccessItems}:
   *
   * <ul>
   *   <li>{@link WorkbasketAccessItem#getWorkbasketId()} is not null
   *   <li>{@link WorkbasketAccessItem#getWorkbasketId()} is equal to {@code workbasketId}
   *   <li>{@link WorkbasketAccessItem#getAccessId()} is unique
   * </ul>
   *
   * @param workbasketId ID of the access-target workbasket.
   * @param wbAccessItems List of WorkbasketAccessItems which does replace all current stored ones.
   * @throws InvalidArgumentException will be thrown when the parameter {@code wbAccessItems} is
   *     NULL or member doesn't match the preconditions
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketAccessItemAlreadyExistException if {@code wbAccessItems} contains multiple
   *     accessItems with the same accessId.
   */
  void setWorkbasketAccessItems(String workbasketId, List<WorkbasketAccessItem> wbAccessItems)
      throws InvalidArgumentException, NotAuthorizedException,
          WorkbasketAccessItemAlreadyExistException;

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
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   */
  WorkbasketAccessItemQuery createWorkbasketAccessItemQuery() throws NotAuthorizedException;

  /**
   * Returns a new workbasket which is not persisted.
   *
   * @param key the workbasket key used to identify the workbasket
   * @param domain the domain of the new workbasket
   * @return new Workbasket
   */
  Workbasket newWorkbasket(String key, String domain);

  /**
   * Returns a set with all permissions of the current user at this workbasket.<br>
   * If the workbasketId is invalid, an empty list of permissions is returned since there is no
   * distinction made between the situation that the workbasket is not found and the caller has no
   * permissions on the workbasket.
   *
   * @param workbasketId the id of the referenced workbasket
   * @return a {@link List} with all {@link WorkbasketPermission}s of the caller on the requested
   *     workbasket.
   */
  List<WorkbasketPermission> getPermissionsForWorkbasket(String workbasketId);

  /**
   * Returns the distribution targets for a given workbasket.
   *
   * @param workbasketId the id of the referenced workbasket
   * @return the distribution targets of the specified workbasket
   * @throws NotAuthorizedException if the current user has no read permission for the specified
   *     workbasket
   * @throws WorkbasketNotFoundException if the workbasket doesn't exist
   */
  List<WorkbasketSummary> getDistributionTargets(String workbasketId)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Returns the distribution targets for a given workbasket.
   *
   * @param workbasketKey the key of the referenced workbasket
   * @param domain the domain of the referenced workbasket
   * @return the distribution targets of the specified workbasket
   * @throws NotAuthorizedException if the current user has no read permission for the specified
   *     workbasket
   * @throws WorkbasketNotFoundException if the workbasket doesn't exist
   */
  List<WorkbasketSummary> getDistributionTargets(String workbasketKey, String domain)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Set the distribution targets for a workbasket.
   *
   * @param sourceWorkbasketId the id of the source workbasket for which the distribution targets
   *     are to be set
   * @param targetWorkbasketIds a list of the ids of the target workbaskets
   * @throws NotAuthorizedException if the current used doesn't have READ permission for the source
   *     workbasket
   * @throws WorkbasketNotFoundException if either the source workbasket or any of the target
   *     workbaskets don't exist
   */
  void setDistributionTargets(String sourceWorkbasketId, List<String> targetWorkbasketIds)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Add a distribution target to a workbasket. If the specified distribution target exists already,
   * the method silently returns without doing anything.
   *
   * @param sourceWorkbasketId the id of the source workbasket
   * @param targetWorkbasketId the id of the target workbasket
   * @throws NotAuthorizedException if the current user doesn't have READ permission for the source
   *     workbasket
   * @throws WorkbasketNotFoundException if either the source workbasket or the target workbasket
   *     doesn't exist
   */
  void addDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Remove a distribution target from a workbasket. If the the specified distribution target
   * doesn't exist, the method silently returns without doing anything.
   *
   * @param sourceWorkbasketId The id of the source workbasket
   * @param targetWorkbasketId The id of the target workbasket
   * @throws NotAuthorizedException If the current user doesn't have READ permission for the source
   *     workbasket
   */
  void removeDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
      throws NotAuthorizedException;

  /**
   * Deletes the workbasket by the given ID of it.
   *
   * @param workbasketId Id of the workbasket which should be deleted.
   * @return true if the workbasket was deleted successfully. false if the workbasket is marked for
   *     deletion.
   * @throws NotAuthorizedException if the current user got no permissions for this interaction.
   * @throws WorkbasketNotFoundException if the workbasket does not exist.
   * @throws WorkbasketInUseException if the workbasket does contain task-content.
   * @throws InvalidArgumentException if the workbasketId is NULL or EMPTY
   */
  boolean deleteWorkbasket(String workbasketId)
      throws NotAuthorizedException, WorkbasketNotFoundException, WorkbasketInUseException,
          InvalidArgumentException;

  /**
   * Deletes a list of workbaskets.
   *
   * @param workbasketsIds the ids of the workbaskets to delete.
   * @return the result of the operations with Id and Exception for each failed workbasket deletion.
   * @throws InvalidArgumentException if the WorkbasketIds parameter list is NULL or empty
   * @throws NotAuthorizedException if the current user got no permission for this interaction.
   */
  BulkOperationResults<String, TaskanaException> deleteWorkbaskets(List<String> workbasketsIds)
      throws NotAuthorizedException, InvalidArgumentException;

  /**
   * Returns the distribution sources for a given workbasket.
   *
   * @param workbasketId the id of the referenced workbasket
   * @return the workbaskets that are distribution sources of the specified workbasket.
   * @throws NotAuthorizedException if the current user has no read permission for the specified
   *     workbasket
   * @throws WorkbasketNotFoundException if the workbasket doesn't exist
   */
  List<WorkbasketSummary> getDistributionSources(String workbasketId)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Returns the distribution sources for a given workbasket.
   *
   * @param workbasketKey the key of the referenced workbasket
   * @param domain the domain of the referenced workbasket
   * @return the workbaskets that are distribution sources of the specified workbasket.
   * @throws NotAuthorizedException if the current user has no read permission for the specified
   *     workbasket
   * @throws WorkbasketNotFoundException if the workbasket doesn't exist
   */
  List<WorkbasketSummary> getDistributionSources(String workbasketKey, String domain)
      throws NotAuthorizedException, WorkbasketNotFoundException;

  /**
   * Deletes all WorkbasketAccessItems using the given AccessId of a user.
   *
   * @param accessId of a taskana-user.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   */
  void deleteWorkbasketAccessItemsForAccessId(String accessId) throws NotAuthorizedException;
}
