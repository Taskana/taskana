package pro.taskana.user.internal;

import static pro.taskana.common.internal.util.CheckedSupplier.wrap;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQueryColumnName;
import pro.taskana.workbasket.api.WorkbasketService;

public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
  private final InternalTaskanaEngine internalTaskanaEngine;
  private final UserMapper userMapper;
  private final WorkbasketService workbasketService;
  private final List<WorkbasketPermission> minimalWorkbasketPermissions;

  public UserServiceImpl(InternalTaskanaEngine internalTaskanaEngine, UserMapper userMapper) {
    this.internalTaskanaEngine = internalTaskanaEngine;
    this.userMapper = userMapper;
    this.workbasketService = internalTaskanaEngine.getEngine().getWorkbasketService();
    minimalWorkbasketPermissions =
        internalTaskanaEngine.getEngine().getConfiguration().getMinimalPermissionsToAssignDomains();
  }

  @Override
  public User newUser() {
    return new UserImpl();
  }

  @Override
  public User getUser(String id) throws UserNotFoundException {
    UserImpl user =
        internalTaskanaEngine.executeInDatabaseConnection(() -> userMapper.findById(id));
    if (user == null) {
      throw new UserNotFoundException(id);
    }

    user.setDomains(determineDomains(user.getId()));
    return user;
  }

  @Override
  public User createUser(User userToCreate)
      throws InvalidArgumentException, NotAuthorizedException, UserAlreadyExistException {
    internalTaskanaEngine
        .getEngine()
        .checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    validateAndPopulateFields(userToCreate);
    insertIntoDatabase(userToCreate);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method createUser() created User '{}'.", userToCreate);
    }

    return userToCreate;
  }

  @Override
  public User updateUser(User userToUpdate) throws UserNotFoundException, NotAuthorizedException {
    internalTaskanaEngine
        .getEngine()
        .checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    getUser(userToUpdate.getId());

    internalTaskanaEngine.executeInDatabaseConnection(() -> userMapper.update(userToUpdate));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method updateUser() updated User '{}'.", userToUpdate);
    }

    return userToUpdate;
  }

  @Override
  public void deleteUser(String id) throws UserNotFoundException, NotAuthorizedException {
    internalTaskanaEngine
        .getEngine()
        .checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    getUser(id);

    internalTaskanaEngine.executeInDatabaseConnection(() -> userMapper.delete(id));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method deleteUser() deleted User with id '{}'.", id);
    }
  }

  private Set<String> determineDomains(String... accessIds) {
    if (minimalWorkbasketPermissions != null && !minimalWorkbasketPermissions.isEmpty()) {
      // since WorkbasketService#accessIdsHavePermissions requires some role permissions we have to
      // execute this query as an admin. Since we're only determining the domains of a given user
      // (and any user can request information on any other user) this query is "harmless".
      return new HashSet<>(
          internalTaskanaEngine
              .getEngine()
              .runAsAdmin(
                  wrap(
                      () ->
                          workbasketService
                              .createWorkbasketQuery()
                              .accessIdsHavePermissions(minimalWorkbasketPermissions, accessIds)
                              .listValues(
                                  WorkbasketQueryColumnName.DOMAIN, SortDirection.ASCENDING))));
    }
    return Collections.emptySet();
  }

  private void insertIntoDatabase(User userToCreate) throws UserAlreadyExistException {
    try {
      internalTaskanaEngine.openConnection();
      userMapper.insert(userToCreate);
    } catch (PersistenceException e) {
      throw new UserAlreadyExistException(userToCreate.getId(), e);
    } finally {
      internalTaskanaEngine.returnConnection();
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
