package org.taskana;

import java.util.List;

import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.model.Workbasket;
import org.taskana.model.WorkbasketAccessItem;
import org.taskana.model.WorkbasketAuthorization;

public interface WorkbasketService {

	/**
	 * Get Workbasket for a given id.
	 * 
	 * @param workbasketId
	 * @return the requested Workbasket
	 */
	public Workbasket getWorkbasket(String workbasketId) throws WorkbasketNotFoundException;

	/**
	 * Get all available Workbaskets.
	 * 
	 * @return List<Workbasket> the list of all workbaskets
	 */
	public List<Workbasket> getWorkbaskets();

	/**
	 * Create a new Workbasket.
	 * 
	 * @param workbasket
	 *            The workbasket to create
	 * @throws NotAuthorizedException
	 */
	public Workbasket createWorkbasket(Workbasket workbasket);

	/**
	 * Update a Workbasket.
	 * 
	 * 
	 * @param workbasket
	 *            The workbasket to update
	 * @throws NotAuthorizedException
	 */
	public Workbasket updateWorkbasket(Workbasket workbasket) throws NotAuthorizedException;

	/**
	 * Create a new authorization for a specific workbasket and a specific user
	 * 
	 * @param workbasket
	 *            the choosen workbasket
	 * @param user
	 *            the choosen user
	 * @return
	 */
	public WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem);

	/**
	 * This method updates an Workbasket Authorization
	 * 
	 * @param workbasketAccessItem
	 *            the Authorization
	 * @return the updated entity
	 */
	public WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem);

	/**
	 * Get all authorizations of the workbasket
	 * 
	 * @return a WorkbasketAccessItem list
	 */
	public List<WorkbasketAccessItem> getAllAuthorizations();

	/**
	 * Deletes a specific authorization
	 * 
	 * @param id
	 *            the specific id
	 */
	public void deleteWorkbasketAuthorization(String id);

	/**
	 * This method checks the authorization with the saved one
	 * 
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
	public void checkPermission(String workbasketId, WorkbasketAuthorization authorization)
			throws NotAuthorizedException;

	/**
	 * This method get one WorkbasketAuthorization with an id
	 * 
	 * @param id
	 *            the id
	 * @return the full {@link WorkbasketAccessItem}
	 */
	public WorkbasketAccessItem getWorkbasketAuthorization(String id);

	/**
	 * Get all authorizations for a Workbasket.
	 * 
	 * @param workbasketId
	 * @return List<WorkbasketAccessItem>
	 */
	public List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketId);

	/**
	 * This method provides workbaskets via an permission
	 * 
	 * @param permission
	 *            as String like in this enum: {@link WorkbasketAuthorization}
	 * @return all filtered workbaskets
	 */
	List<Workbasket> getWorkbaskets(List<String> permission);

}
