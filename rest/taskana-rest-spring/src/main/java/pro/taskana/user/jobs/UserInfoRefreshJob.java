package pro.taskana.user.jobs;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.util.ApplicationContextProvider;
import pro.taskana.spi.user.internal.RefreshUserPostprocessorManager;
import pro.taskana.task.internal.jobs.helper.SqlConnectionRunner;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;

/** Job to refresh all user info after a period of time. */
public class UserInfoRefreshJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoRefreshJob.class);
  private final SqlConnectionRunner sqlConnectionRunner;
  private RefreshUserPostprocessorManager refreshUserPostprocessorManager;

  public UserInfoRefreshJob(TaskanaEngine taskanaEngine) {
    this(taskanaEngine, null, null);
  }

  public UserInfoRefreshJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob, true);
    runEvery = taskanaEngine.getConfiguration().getUserRefreshJobRunEvery();
    firstRun = taskanaEngine.getConfiguration().getUserRefreshJobFirstRun();
    sqlConnectionRunner = new SqlConnectionRunner(taskanaEngine);
    refreshUserPostprocessorManager = new RefreshUserPostprocessorManager();
  }

  /**
   * Initializes the {@linkplain UserInfoRefreshJob} schedule. <br>
   * All scheduled jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param taskanaEngine the TASKANA engine.
   */
  public static void initializeSchedule(TaskanaEngine taskanaEngine) {
    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    UserInfoRefreshJob job = new UserInfoRefreshJob(taskanaEngine);
    jobService.deleteJobs(job.getType());
    job.scheduleNextJob();
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
          users.stream()
              .map(user -> refreshUserPostprocessorManager.processUserAfterRefresh(user))
              .collect(Collectors.toList());
      addExistingConfigurationDataToUsers(usersAfterProcessing);
      clearExistingUsers();
      insertNewUsers(usersAfterProcessing);

      LOGGER.info("Job to refresh all user info has finished.");

    } catch (Exception e) {
      throw new SystemException("Error while processing UserRefreshJob.", e);
    }
  }

  private void clearExistingUsers() {

    sqlConnectionRunner.runWithConnection(
        connection -> {
          String sql = "DELETE FROM USER_INFO";
          PreparedStatement statement = connection.prepareStatement(sql);
          statement.execute();

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
            taskanaEngineImpl.getUserService().createUser(user);
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

            String userData = taskanaEngineImpl.getUserService().getUser(user.getId()).getData();
            if (userData != null) {
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Trying to set userData {} for user {}", userData, user);
              }
              user.setData(taskanaEngineImpl.getUserService().getUser(user.getId()).getData());
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
          }
        });
  }
}
