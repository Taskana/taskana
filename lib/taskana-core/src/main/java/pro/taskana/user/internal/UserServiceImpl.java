package pro.taskana.user.internal;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;

public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private final InternalTaskanaEngine taskanaEngine;
  private final UserMapper userMapper;

  public UserServiceImpl(InternalTaskanaEngine taskanaEngine, UserMapper userMapper) {
    this.taskanaEngine = taskanaEngine;
    this.userMapper = userMapper;
  }

  @Override
  public User newUser() {
    return new UserImpl();
  }

  @Override
  public User getUser(String id) throws UserNotFoundException {
    User user = taskanaEngine.executeInDatabaseConnection(() -> userMapper.findById(id));
    if (user == null) {
      throw new UserNotFoundException(id);
    }

    return user;
  }

  @Override
  public User createUser(User userToCreate)
      throws InvalidArgumentException, NotAuthorizedException, UserAlreadyExistException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    validateAndPopulateFields(userToCreate);
    insertIntoDatabase(userToCreate);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method createUser() created User '{}'.", userToCreate);
    }

    return userToCreate;
  }

  @Override
  public User updateUser(User userToUpdate) throws UserNotFoundException, NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    getUser(userToUpdate.getId());

    taskanaEngine.executeInDatabaseConnection(() -> userMapper.update((UserImpl) userToUpdate));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method updateUser() updated User '{}'.", userToUpdate);
    }

    return userToUpdate;
  }

  @Override
  public void deleteUser(String id) throws UserNotFoundException, NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    getUser(id);

    taskanaEngine.executeInDatabaseConnection(() -> userMapper.delete(id));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method deleteUser() deleted User with id '{}'.", id);
    }
  }

  private void insertIntoDatabase(User userToCreate) throws UserAlreadyExistException {
    try {
      taskanaEngine.openConnection();
      userMapper.insert((UserImpl) userToCreate);
    } catch (PersistenceException e) {
      throw new UserAlreadyExistException(userToCreate.getId());
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void validateAndPopulateFields(User userToCreate) throws InvalidArgumentException {
    if (userToCreate.getId() == null || userToCreate.getId().isEmpty()) {
      throw new InvalidArgumentException("UserId must not be empty when creating User.");
    }
    if (userToCreate.getFirstName() == null || userToCreate.getLastName() == null) {
      throw new InvalidArgumentException("First and last name of User must be set or empty.");
    }

    if (userToCreate.getFullName() == null || userToCreate.getFullName().isEmpty()) {
      userToCreate.setFullName(userToCreate.getLastName() + ", " + userToCreate.getFirstName());
    }
    if (userToCreate.getLongName() == null || userToCreate.getLongName().isEmpty()) {
      userToCreate.setLongName(userToCreate.getFullName() + " - (" + userToCreate.getId() + ")");
    }
  }
}
