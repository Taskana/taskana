package io.kadai.user.jobs;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.rest.ldap.LdapClient;
import io.kadai.common.rest.util.ApplicationContextProvider;
import io.kadai.spi.user.internal.RefreshUserPostprocessorManager;
import io.kadai.task.internal.jobs.helper.SqlConnectionRunner;
import io.kadai.user.api.exceptions.UserAlreadyExistException;
import io.kadai.user.api.exceptions.UserNotFoundException;
import io.kadai.user.api.models.User;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Job to refresh all user info after a period of time. */
public class UserInfoRefreshJob extends AbstractKadaiJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoRefreshJob.class);
  private final SqlConnectionRunner sqlConnectionRunner;
  private final RefreshUserPostprocessorManager refreshUserPostprocessorManager;

  public UserInfoRefreshJob(KadaiEngine kadaiEngine) {
    this(kadaiEngine, null, null);
  }

  public UserInfoRefreshJob(
      KadaiEngine kadaiEngine,
      KadaiTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    super(kadaiEngine, txProvider, scheduledJob, true);
    runEvery = kadaiEngine.getConfiguration().getUserRefreshJobRunEvery();
    firstRun = kadaiEngine.getConfiguration().getUserRefreshJobFirstRun();
    sqlConnectionRunner = new SqlConnectionRunner(kadaiEngine);
    refreshUserPostprocessorManager = new RefreshUserPostprocessorManager();
  }

  public static Duration getLockExpirationPeriod(KadaiConfiguration kadaiConfiguration) {
    return kadaiConfiguration.getUserRefreshJobLockExpirationPeriod();
  }

  @Override
  protected String getType() {
    return UserInfoRefreshJob.class.getName();
  }

  @Override
  protected void execute() {
    LOGGER.info("Running job to refresh all user info");

    LdapClient ldapClient =
        ApplicationContextProvider.getApplicationContext().getBean("ldapClient", LdapClient.class);

    try {

      List<User> users = ldapClient.searchUsersInUserRole();
      List<User> usersAfterProcessing =
          users.stream().map(refreshUserPostprocessorManager::processUserAfterRefresh).toList();
      addExistingConfigurationDataToUsers(usersAfterProcessing);
      clearExistingUsersAndGroupsAndPermissions();
      insertNewUsers(usersAfterProcessing);

      LOGGER.info("Job to refresh all user info has finished.");

    } catch (Exception e) {
      throw new SystemException("Error while processing UserRefreshJob.", e);
    }
  }

  private void clearExistingUsersAndGroupsAndPermissions() {

    sqlConnectionRunner.runWithConnection(
        connection -> {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to delete all users, groups and permissions");
          }
          String sql = "DELETE FROM USER_INFO; DELETE FROM GROUP_INFO; DELETE FROM PERMISSION_INFO";
          PreparedStatement statement = connection.prepareStatement(sql);
          statement.execute();
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully deleted all users, groups and permissions");
          }

          if (!connection.getAutoCommit()) {
            connection.commit();
          }
        });
  }

  private void insertNewUsers(List<User> users) {

    users.forEach(
        user -> {
          try {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("Trying to insert user {}", user);
            }
            kadaiEngineImpl.getUserService().createUser(user);
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("Successfully inserted user {}", user);
            }
          } catch (InvalidArgumentException
              | NotAuthorizedException
              | UserAlreadyExistException e) {
            throw new SystemException("Caught Exception while trying to insert new User", e);
          }
        });
  }

  private void addExistingConfigurationDataToUsers(List<User> users) {

    users.forEach(
        user -> {
          try {

            String userData = kadaiEngineImpl.getUserService().getUser(user.getId()).getData();
            if (userData != null) {
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Trying to set userData {} for user {}", userData, user);
              }
              user.setData(userData);
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Successfully set userData {} for user {}", userData, user);
              }
            }
          } catch (UserNotFoundException e) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug(
                  String.format(
                      "Failed to fetch configuration data for User "
                          + "with ID '%s' because it doesn't exist",
                      user.getId()));
            }
          } catch (InvalidArgumentException e) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("Failed to fetch configuration data because userId was NULL or empty");
            }
          }
        });
  }
}
