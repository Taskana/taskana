package io.kadai.user.internal;

import static io.kadai.common.internal.util.CheckedSupplier.wrap;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.util.LogSanitizer;
import io.kadai.user.api.UserService;
import io.kadai.user.api.exceptions.UserAlreadyExistException;
import io.kadai.user.api.exceptions.UserNotFoundException;
import io.kadai.user.api.models.User;
import io.kadai.user.internal.models.UserImpl;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketQueryColumnName;
import io.kadai.workbasket.api.WorkbasketService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
  private final InternalKadaiEngine internalKadaiEngine;
  private final UserMapper userMapper;
  private final WorkbasketService workbasketService;
  private final List<WorkbasketPermission> minimalWorkbasketPermissions;

  public UserServiceImpl(InternalKadaiEngine internalKadaiEngine, UserMapper userMapper) {
    this.internalKadaiEngine = internalKadaiEngine;
    this.userMapper = userMapper;
    this.workbasketService = internalKadaiEngine.getEngine().getWorkbasketService();
    minimalWorkbasketPermissions =
        List.copyOf(
            internalKadaiEngine
                .getEngine()
                .getConfiguration()
                .getMinimalPermissionsToAssignDomains());
  }

  @Override
  public User newUser() {
    return new UserImpl();
  }

  @Override
  public User getUser(String userId) throws UserNotFoundException, InvalidArgumentException {
    if (userId == null || userId.equals("")) {
      throw new InvalidArgumentException("UserId can't be used as NULL-Parameter.");
    }
    String finalUserId;
    if (KadaiConfiguration.shouldUseLowerCaseForAccessIds()) {
      finalUserId = userId.toLowerCase();
    } else {
      finalUserId = userId;
    }

    UserImpl user =
        internalKadaiEngine.executeInDatabaseConnection(() -> userMapper.findById(finalUserId));
    if (user == null) {
      throw new UserNotFoundException(userId);
    }

    user.setDomains(determineDomains(user));
    return user;
  }

  @Override
  public List<User> getUsers(Set<String> userIds) throws InvalidArgumentException {
    if (userIds == null || userIds.isEmpty()) {
      throw new InvalidArgumentException("UserIds can't be used as NULL-Parameter.");
    }
    Set<String> finalUserIds;
    if (KadaiConfiguration.shouldUseLowerCaseForAccessIds()) {
      finalUserIds = userIds.stream().map(String::toLowerCase).collect(Collectors.toSet());
    } else {
      finalUserIds = userIds;
    }

    List<UserImpl> users =
        internalKadaiEngine.executeInDatabaseConnection(() -> userMapper.findByIds(finalUserIds));

    users.forEach(user -> user.setDomains(determineDomains(user)));

    return users.stream().map(User.class::cast).toList();
  }

  @Override
  public User createUser(User userToCreate)
      throws InvalidArgumentException, UserAlreadyExistException, NotAuthorizedException {
    internalKadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    validateFields(userToCreate);
    standardCreateActions(userToCreate);
    insertIntoDatabase(userToCreate);
    ((UserImpl) userToCreate).setDomains(determineDomains(userToCreate));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Method createUser() created User '{}'.",
          LogSanitizer.stripLineBreakingChars(userToCreate));
    }
    return userToCreate;
  }

  @Override
  public User updateUser(User userToUpdate)
      throws UserNotFoundException, InvalidArgumentException, NotAuthorizedException {
    internalKadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    validateFields(userToUpdate);
    standardUpdateActions(getUser(userToUpdate.getId()), userToUpdate);

    internalKadaiEngine.executeInDatabaseConnection(() -> userMapper.update(userToUpdate));
    internalKadaiEngine.executeInDatabaseConnection(
        () -> {
          userMapper.deleteGroups(userToUpdate.getId());
          userMapper.deletePermissions(userToUpdate.getId());
        });
    if (userToUpdate.getGroups() != null && !userToUpdate.getGroups().isEmpty()) {
      internalKadaiEngine.executeInDatabaseConnection(() -> userMapper.insertGroups(userToUpdate));
    }
    if (userToUpdate.getPermissions() != null && !userToUpdate.getPermissions().isEmpty()) {
      internalKadaiEngine.executeInDatabaseConnection(
          () -> userMapper.insertPermissions(userToUpdate));
    }
    ((UserImpl) userToUpdate).setDomains(determineDomains(userToUpdate));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Method updateUser() updated User '{}'.",
          LogSanitizer.stripLineBreakingChars(userToUpdate));
    }

    return userToUpdate;
  }

  @Override
  public void deleteUser(String id)
      throws UserNotFoundException, InvalidArgumentException, NotAuthorizedException {
    internalKadaiEngine.getEngine().checkRoleMembership(KadaiRole.BUSINESS_ADMIN, KadaiRole.ADMIN);
    getUser(id);

    internalKadaiEngine.executeInDatabaseConnection(
        () -> {
          userMapper.delete(id);
          userMapper.deleteGroups(id);
          userMapper.deletePermissions(id);
        });
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Method deleteUser() deleted User with id '{}'.", id);
    }
  }

  private Set<String> determineDomains(User user) {
    Set<String> accessIds = new HashSet<>(user.getGroups());
    accessIds.addAll(user.getPermissions());
    accessIds.add(user.getId());
    if (minimalWorkbasketPermissions != null && !minimalWorkbasketPermissions.isEmpty()) {
      // since WorkbasketService#accessIdsHavePermissions requires some role permissions we have to
      // execute this query as an admin. Since we're only determining the domains of a given user
      // (and any user can request information on any other user) this query is "harmless".
      return new HashSet<>(
          internalKadaiEngine
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
      internalKadaiEngine.openConnection();
      userMapper.insert(userToCreate);
      if (userToCreate.getGroups() != null && !userToCreate.getGroups().isEmpty()) {
        userMapper.insertGroups(userToCreate);
      }
      if (userToCreate.getPermissions() != null && !userToCreate.getPermissions().isEmpty()) {
        userMapper.insertPermissions(userToCreate);
      }
    } catch (PersistenceException e) {
      throw new UserAlreadyExistException(userToCreate.getId(), e);
    } finally {
      internalKadaiEngine.returnConnection();
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
    if (KadaiConfiguration.shouldUseLowerCaseForAccessIds()) {
      user.setId(user.getId().toLowerCase());
      user.setGroups(
          user.getGroups().stream().map((String::toLowerCase)).collect(Collectors.toSet()));
      user.setPermissions(
          user.getPermissions().stream().map((String::toLowerCase)).collect(Collectors.toSet()));
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
    if (KadaiConfiguration.shouldUseLowerCaseForAccessIds()) {
      newUser.setId(newUser.getId().toLowerCase());
      newUser.setGroups(
          newUser.getGroups().stream().map((String::toLowerCase)).collect(Collectors.toSet()));
      newUser.setPermissions(
          newUser.getPermissions().stream().map((String::toLowerCase)).collect(Collectors.toSet()));
    }
  }
}
