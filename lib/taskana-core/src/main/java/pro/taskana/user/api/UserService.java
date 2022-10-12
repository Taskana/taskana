package pro.taskana.user.api;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;

/** The UserService manages all operations concerning {@linkplain User Users}. */
public interface UserService {

  /**
   * Returns a new {@linkplain User} with all fields being empty.
   *
   * <p>It will be only generated but not yet inserted until method {@linkplain
   * UserService#createUser(User)} is called.
   *
   * @return the new {@linkplain User}
   */
  User newUser();

  /**
   * Creates a new {@linkplain User}.
   *
   * <p>If all checks have passed the specified {@linkplain User} will be inserted into the
   * database. The {@linkplain User#getId() id} of the {@linkplain User} must be set and should be
   * unique, such that no other {@linkplain User} with same {@linkplain User#getId() id} is already
   * existing in the database. The {@linkplain User#getFirstName() first name} and the {@linkplain
   * User#getLastName() last name} must not be null. The fields for the {@linkplain
   * User#getFullName() full name} and the {@linkplain User#getLongName() long name} are set
   * according to following rules:
   *
   * <ul>
   *   <li><b>fullName</b> = lastName, firstName
   *   <li><b>longName</b> = lastName, firstName - (id)
   * </ul>
   *
   * @param userToCreate the {@linkplain User} which should be inserted
   * @return the inserted {@linkplain User}
   * @throws InvalidArgumentException if some fields are not set properly
   * @throws NotAuthorizedException if the current user is not {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN admin} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN business-admin}
   * @throws UserAlreadyExistException if there already exists a {@linkplain User} with the
   *     specified {@linkplain User#getId() id} inside the database
   */
  User createUser(User userToCreate)
      throws InvalidArgumentException, NotAuthorizedException, UserAlreadyExistException;

  /**
   * Gets a {@linkplain User}.
   *
   * <p>If a {@linkplain User} with the specified {@linkplain User#getId() id} is existing in the
   * database, it is returned.
   *
   * @param id the {@linkplain User#getId() id} of the {@linkplain User} to be retrieved
   * @return the retrieved {@linkplain User}
   * @throws UserNotFoundException if there does not exist a {@linkplain User} with the specified
   *     {@linkplain User#getId() id} inside the database
   */
  User getUser(String id) throws UserNotFoundException;

  /**
   * Updates an existing {@linkplain User}.
   *
   * <p>If a {@linkplain User} with the specified {@linkplain User#getId() id} exists in the
   * database and if the current user is allowed to perform the operation, the {@linkplain User}
   * gets updated according to the set fields of the passed object.
   *
   * @param userToUpdate the {@linkplain User} which should be updated
   * @return the updated {@linkplain User}
   * @throws NotAuthorizedException if the current user is not {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN admin} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN business-admin}
   * @throws UserNotFoundException if there does not exist a {@linkplain User} with the specified
   *     {@linkplain User#getId() id} inside the database
   * @throws InvalidArgumentException if some fields are not set properly
   */
  User updateUser(User userToUpdate)
      throws UserNotFoundException, NotAuthorizedException, InvalidArgumentException;

  /**
   * Deletes a {@linkplain User}.
   *
   * <p>If a {@linkplain User} with the specified {@linkplain User#getId() id} exists in the
   * database and if the current user is allowed to perform the operation, the {@linkplain User}
   * gets deleted.
   *
   * @param id the {@linkplain User#getId() id} of the {@linkplain User} which should be deleted
   * @throws NotAuthorizedException if the current user is not {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN admin} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN business-admin}
   * @throws UserNotFoundException if there does not exist a {@linkplain User} with the specified
   *     {@linkplain User#getId() id} inside the database
   */
  void deleteUser(String id) throws UserNotFoundException, NotAuthorizedException;
}
