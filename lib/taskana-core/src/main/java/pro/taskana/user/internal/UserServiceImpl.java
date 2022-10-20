package pro.taskana.user.internal;

import static pro.taskana.common.internal.util.CheckedSupplier.wrap;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
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

    Set<String> groups =
        internalTaskanaEngine.executeInDatabaseConnection(() -> userMapper.findGroupsById(id));
    if (groups != null) {
      user.setGroups(groups);
    }

    user.setDomains(determineDomains(user));
    return user;
  }

  @Override
  public User createUser(User userToCreate)
      throws InvalidArgumentException, NotAuthorizedException, UserAlreadyExistException {
    internalTaskanaEngine
        .getEngine()
        .checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    validateFields(userToCreate);
    standardCreateActions(userToCreate);
    insertIntoDatabase(userToCreate);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method createUser() created User '{}'.", userToCreate);
    }
    return userToCreate;
  }

  @Override
  public User updateUser(User userToUpdate)
      throws UserNotFoundException, NotAuthorizedException, InvalidArgumentException {
    internalTaskanaEngine
        .getEngine()
        .checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    validateFields(userToUpdate);
    standardUpdateActions(getUser(userToUpdate.getId()), userToUpdate);

    internalTaskanaEngine.executeInDatabaseConnection(() -> userMapper.update(userToUpdate));
    internalTaskanaEngine.executeInDatabaseConnection(
        () -> userMapper.deleteGroups(userToUpdate.getId()));
    if (userToUpdate.getGroups() != null && !userToUpdate.getGroups().isEmpty()) {
      internalTaskanaEngine.executeInDatabaseConnection(
          () -> userMapper.insertGroups(userToUpdate));
    }
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

    internalTaskanaEngine.executeInDatabaseConnection(
        () -> {
          userMapper.delete(id);
          userMapper.deleteGroups(id);
        });
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method deleteUser() deleted User with id '{}'.", id);
    }
  }

  private Set<String> determineDomains(User user) {
    Set<String> accessIds = new HashSet<>(user.getGroups());
    accessIds.add(user.getId());
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
                              .accessIdsHavePermissions(
                                  minimalWorkbasketPermissions, accessIds.toArray(String[]::new))
                              .listValues(
                                  WorkbasketQueryColumnName.DOMAIN, SortDirection.ASCENDING))));
    }
    return Collections.emptySet();
  }

  private void insertIntoDatabase(User userToCreate) throws UserAlreadyExistException {
    try {
      internalTaskanaEngine.openConnection();
      userMapper.insert(userToCreate);
      if (userToCreate.getGroups() != null && !userToCreate.getGroups().isEmpty()) {
        userMapper.insertGroups(userToCreate);
      }
    } catch (PersistenceException e) {
      throw new UserAlreadyExistException(userToCreate.getId(), e);
    } finally {
      internalTaskanaEngine.returnConnection();
    }
  }

  private void validateFields(User userToValidate) throws InvalidArgumentException {
    if (userToValidate.getId() == null || userToValidate.getId().isEmpty()) {
      throw new InvalidArgumentException(
          "UserId must not be empty when creating or updating User.");
    }
    if (userToValidate.getFirstName() == null || userToValidate.getLastName() == null) {
      throw new InvalidArgumentException("First and last name of User must be set or empty.");
    }
  }

  private void standardCreateActions(User user) {
    if (user.getFullName() == null || user.getFullName().isEmpty()) {
      user.setFullName(user.getLastName() + ", " + user.getFirstName());
    }
    if (user.getLongName() == null || user.getLongName().isEmpty()) {
      user.setLongName(user.getFullName() + " - (" + user.getId() + ")");
    }
    if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
      user.setId(user.getId().toLowerCase());
      user.setGroups(
          user.getGroups().stream().map((String::toLowerCase)).collect(Collectors.toSet()));
    }
  }

  private void standardUpdateActions(User oldUser, User newUser) {
    if (!newUser.getFirstName().equals(oldUser.getFirstName())
        || !newUser.getLastName().equals(oldUser.getLastName())) {
      if (newUser.getFullName() == null
          || newUser.getFullName().isEmpty()
          || newUser.getFullName().equals(oldUser.getFullName())) {
        newUser.setFullName(newUser.getLastName() + ", " + newUser.getFirstName());
      }
      if (newUser.getLongName() == null
          || newUser.getLongName().isEmpty()
          || newUser.getLongName().equals(oldUser.getLongName())) {
        newUser.setLongName(newUser.getFullName() + " - (" + newUser.getId() + ")");
      }
    }
    if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
      newUser.setId(newUser.getId().toLowerCase());
      newUser.setGroups(
          newUser.getGroups().stream().map((String::toLowerCase)).collect(Collectors.toSet()));
    }
  }
}
