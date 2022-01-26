package pro.taskana.common.internal;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.security.auth.Subject;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.internal.ClassificationMapper;
import pro.taskana.classification.internal.ClassificationQueryMapper;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.api.exceptions.AutocommitFailedException;
import pro.taskana.common.api.exceptions.ConnectionNotSetException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.api.security.UserPrincipal;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.internal.persistence.InstantTypeHandler;
import pro.taskana.common.internal.persistence.MapTypeHandler;
import pro.taskana.common.internal.security.CurrentUserContextImpl;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.spi.routing.internal.TaskRoutingManager;
import pro.taskana.spi.task.internal.CreateTaskPreprocessorManager;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.internal.AttachmentMapper;
import pro.taskana.task.internal.ObjectReferenceMapper;
import pro.taskana.task.internal.TaskCommentMapper;
import pro.taskana.task.internal.TaskCommentQueryMapper;
import pro.taskana.task.internal.TaskMapper;
import pro.taskana.task.internal.TaskQueryMapper;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.user.api.UserService;
import pro.taskana.user.internal.UserMapper;
import pro.taskana.user.internal.UserServiceImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.internal.DistributionTargetMapper;
import pro.taskana.workbasket.internal.WorkbasketAccessMapper;
import pro.taskana.workbasket.internal.WorkbasketMapper;
import pro.taskana.workbasket.internal.WorkbasketQueryMapper;
import pro.taskana.workbasket.internal.WorkbasketServiceImpl;

/** This is the implementation of TaskanaEngine. */
public class TaskanaEngineImpl implements TaskanaEngine {

  // must match the VERSION value in table
  private static final String MINIMAL_TASKANA_SCHEMA_VERSION = "5.0.0";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineImpl.class);
  private static final SessionStack SESSION_STACK = new SessionStack();
  private final TaskRoutingManager taskRoutingManager;
  private final CreateTaskPreprocessorManager createTaskPreprocessorManager;
  private final PriorityServiceManager priorityServiceManager;
  private final InternalTaskanaEngineImpl internalTaskanaEngineImpl;
  private final WorkingDaysToDaysConverter workingDaysToDaysConverter;
  private final HistoryEventManager historyEventManager;
  private final CurrentUserContext currentUserContext;
  protected TaskanaEngineConfiguration taskanaEngineConfiguration;
  protected TransactionFactory transactionFactory;
  protected SqlSessionManager sessionManager;
  protected ConnectionManagementMode mode;
  protected Connection connection;

  protected TaskanaEngineImpl(
      TaskanaEngineConfiguration taskanaEngineConfiguration,
      ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    this.taskanaEngineConfiguration = taskanaEngineConfiguration;
    this.mode = connectionManagementMode;
    internalTaskanaEngineImpl = new InternalTaskanaEngineImpl();
    workingDaysToDaysConverter =
        new WorkingDaysToDaysConverter(
            taskanaEngineConfiguration.isGermanPublicHolidaysEnabled(),
            taskanaEngineConfiguration.isCorpusChristiEnabled(),
            taskanaEngineConfiguration.getCustomHolidays());
    currentUserContext =
        new CurrentUserContextImpl(TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds());
    createTransactionFactory(taskanaEngineConfiguration.getUseManagedTransactions());
    sessionManager = createSqlSessionManager();

    initializeDbSchema(taskanaEngineConfiguration);

    // IMPORTANT: SPI has to be initialized last (and in this order) in order
    // to provide a fully initialized TaskanaEngine instance during the SPI initialization!
    priorityServiceManager = new PriorityServiceManager();
    createTaskPreprocessorManager = new CreateTaskPreprocessorManager();
    historyEventManager = new HistoryEventManager(this);
    taskRoutingManager = new TaskRoutingManager(this);
  }

  public static TaskanaEngine createTaskanaEngine(
      TaskanaEngineConfiguration taskanaEngineConfiguration) throws SQLException {
    return createTaskanaEngine(taskanaEngineConfiguration, ConnectionManagementMode.PARTICIPATE);
  }

  public static TaskanaEngine createTaskanaEngine(
      TaskanaEngineConfiguration taskanaEngineConfiguration,
      ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return new TaskanaEngineImpl(taskanaEngineConfiguration, connectionManagementMode);
  }

  @Override
  public ConfigurationService getConfigurationService() {
    return new ConfigurationServiceImpl(
        internalTaskanaEngineImpl, sessionManager.getMapper(ConfigurationMapper.class));
  }

  @Override
  public TaskService getTaskService() {
    return new TaskServiceImpl(
        internalTaskanaEngineImpl,
        sessionManager.getMapper(TaskMapper.class),
        sessionManager.getMapper(TaskCommentMapper.class),
        sessionManager.getMapper(AttachmentMapper.class),
        sessionManager.getMapper(UserMapper.class));
  }

  @Override
  public MonitorService getMonitorService() {
    return new MonitorServiceImpl(
        internalTaskanaEngineImpl, sessionManager.getMapper(MonitorMapper.class));
  }

  @Override
  public WorkbasketService getWorkbasketService() {
    return new WorkbasketServiceImpl(
        internalTaskanaEngineImpl,
        historyEventManager,
        sessionManager.getMapper(WorkbasketMapper.class),
        sessionManager.getMapper(DistributionTargetMapper.class),
        sessionManager.getMapper(WorkbasketAccessMapper.class));
  }

  @Override
  public ClassificationService getClassificationService() {
    return new ClassificationServiceImpl(
        internalTaskanaEngineImpl,
        priorityServiceManager,
        sessionManager.getMapper(ClassificationMapper.class),
        sessionManager.getMapper(TaskMapper.class));
  }

  // This should be part of the InternalTaskanaEngine. Unfortunately the jobs don't have access to
  // that engine.
  // Therefore, this getter exits and will be removed as soon as our jobs will be refactored.
  public PriorityServiceManager getPriorityServiceManager() {
    return priorityServiceManager;
  }

  @Override
  public JobService getJobService() {
    return new JobServiceImpl(internalTaskanaEngineImpl, sessionManager.getMapper(JobMapper.class));
  }

  @Override
  public UserService getUserService() {
    return new UserServiceImpl(
        internalTaskanaEngineImpl, sessionManager.getMapper(UserMapper.class));
  }

  @Override
  public TaskanaEngineConfiguration getConfiguration() {
    return this.taskanaEngineConfiguration;
  }

  @Override
  public WorkingDaysToDaysConverter getWorkingDaysToDaysConverter() {
    return workingDaysToDaysConverter;
  }

  @Override
  public boolean isHistoryEnabled() {
    return historyEventManager.isEnabled();
  }

  @Override
  public ConnectionManagementMode getConnectionManagementMode() {
    return mode;
  }

  @Override
  public void setConnectionManagementMode(ConnectionManagementMode mode) {
    if (this.mode == ConnectionManagementMode.EXPLICIT
        && connection != null
        && mode != ConnectionManagementMode.EXPLICIT) {
      if (sessionManager.isManagedSessionStarted()) {
        sessionManager.close();
      }
      connection = null;
    }
    this.mode = mode;
  }

  @Override
  public void setConnection(Connection connection) throws SQLException {
    if (connection != null) {
      this.connection = connection;
      // disabling auto commit for passed connection in order to gain full control over the
      // connection management
      connection.setAutoCommit(false);
      connection.setSchema(taskanaEngineConfiguration.getSchemaName());
      mode = ConnectionManagementMode.EXPLICIT;
      sessionManager.startManagedSession(connection);
    } else if (this.connection != null) {
      closeConnection();
    }
  }

  @Override
  public void closeConnection() {
    if (this.mode == ConnectionManagementMode.EXPLICIT) {
      this.connection = null;
      if (sessionManager.isManagedSessionStarted()) {
        sessionManager.close();
      }
      mode = ConnectionManagementMode.PARTICIPATE;
    }
  }

  @Override
  public boolean isUserInRole(TaskanaRole... roles) {
    if (!getConfiguration().isSecurityEnabled()) {
      return true;
    }

    List<String> accessIds = currentUserContext.getAccessIds();
    Set<String> rolesMembers = new HashSet<>();
    for (TaskanaRole role : roles) {
      rolesMembers.addAll(getConfiguration().getRoleMap().get(role));
    }
    for (String accessId : accessIds) {
      if (rolesMembers.contains(accessId)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException {
    if (!isUserInRole(roles)) {
      if (LOGGER.isDebugEnabled()) {
        String rolesAsString = Arrays.toString(roles);
        LOGGER.debug(
            "Throwing NotAuthorizedException because accessIds {} are not member of roles {}",
            currentUserContext.getAccessIds(),
            rolesAsString);
      }
      throw new MismatchedRoleException(currentUserContext.getUserid(), roles);
    }
  }

  public <T> T runAsAdmin(Supplier<T> supplier) {
    if (isUserInRole(TaskanaRole.ADMIN)) {
      return supplier.get();
    }

    String adminName =
        this.getConfiguration().getRoleMap().get(TaskanaRole.ADMIN).stream()
            .findFirst()
            .orElseThrow(() -> new SystemException("There is no admin configured"));

    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(adminName));

    return Subject.doAs(subject, (PrivilegedAction<T>) supplier::get);
  }

  @Override
  public CurrentUserContext getCurrentUserContext() {
    return currentUserContext;
  }

  /**
   * This method creates the sqlSessionManager of myBatis. It integrates all the SQL mappers and
   * sets the databaseId attribute.
   *
   * @return a {@link SqlSessionFactory}
   * @throws SystemException when a connection to the database could not be opened.
   */
  protected SqlSessionManager createSqlSessionManager() {
    Environment environment =
        new Environment(
            "default", this.transactionFactory, taskanaEngineConfiguration.getDatasource());
    Configuration configuration = new Configuration(environment);

    // set databaseId
    try (Connection con = taskanaEngineConfiguration.getDatasource().getConnection()) {
      String databaseProductName = con.getMetaData().getDatabaseProductName();
      String databaseProductId = DB.getDatabaseProductId(databaseProductName);
      configuration.setDatabaseId(databaseProductId);

    } catch (SQLException e) {
      throw new SystemException(
          "Method createSqlSessionManager() could not open a connection "
              + "to the database. No databaseId has been set.",
          e.getCause());
    }

    // register type handlers
    configuration.getTypeHandlerRegistry().register(new MapTypeHandler());
    configuration.getTypeHandlerRegistry().register(Instant.class, new InstantTypeHandler());
    configuration.getTypeHandlerRegistry().register(JdbcType.TIMESTAMP, new InstantTypeHandler());
    // add mappers
    configuration.addMapper(TaskMapper.class);
    configuration.addMapper(MonitorMapper.class);
    configuration.addMapper(WorkbasketMapper.class);
    configuration.addMapper(DistributionTargetMapper.class);
    configuration.addMapper(ClassificationMapper.class);
    configuration.addMapper(WorkbasketAccessMapper.class);
    configuration.addMapper(ObjectReferenceMapper.class);
    configuration.addMapper(WorkbasketQueryMapper.class);
    configuration.addMapper(TaskQueryMapper.class);
    configuration.addMapper(TaskCommentMapper.class);
    configuration.addMapper(TaskCommentQueryMapper.class);
    configuration.addMapper(ClassificationQueryMapper.class);
    configuration.addMapper(AttachmentMapper.class);
    configuration.addMapper(JobMapper.class);
    configuration.addMapper(UserMapper.class);
    configuration.addMapper(ConfigurationMapper.class);
    SqlSessionFactory localSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    return SqlSessionManager.newInstance(localSessionFactory);
  }

  private void initializeDbSchema(TaskanaEngineConfiguration taskanaEngineConfiguration)
      throws SQLException {
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(
            taskanaEngineConfiguration.getDatasource(), taskanaEngineConfiguration.getSchemaName());
    dbSchemaCreator.run();

    if (!dbSchemaCreator.isValidSchemaVersion(MINIMAL_TASKANA_SCHEMA_VERSION)) {
      throw new SystemException(
          "The Database Schema Version doesn't match the expected minimal version "
              + MINIMAL_TASKANA_SCHEMA_VERSION);
    }
    ((ConfigurationServiceImpl) getConfigurationService())
        .checkSecureAccess(taskanaEngineConfiguration.isSecurityEnabled());
    ((ConfigurationServiceImpl) getConfigurationService()).setupDefaultCustomAttributes();
  }

  /**
   * creates the MyBatis transaction factory.
   *
   * @param useManagedTransactions true, if managed transations should be used. Otherwise false.
   */
  private void createTransactionFactory(boolean useManagedTransactions) {
    if (useManagedTransactions) {
      this.transactionFactory = new ManagedTransactionFactory();
    } else {
      this.transactionFactory = new JdbcTransactionFactory();
    }
  }

  /**
   * With sessionStack, we maintain a Stack of SqlSessionManager objects on a per thread basis.
   * SqlSessionManager is the MyBatis object that wraps database connections. The purpose of this
   * stack is to keep track of nested calls. Each external API call is wrapped into
   * taskanaEngineImpl.openConnection(); ..... taskanaEngineImpl.returnConnection(); calls. In order
   * to avoid duplicate opening / closing of connections, we use the sessionStack in the following
   * way: Each time, an openConnection call is received, we push the current sessionManager onto the
   * stack. On the first call to openConnection, we call sessionManager.startManagedSession() to
   * open a database connection. On each call to returnConnection() we pop one instance of
   * sessionManager from the stack. When the stack becomes empty, we close the database connection
   * by calling sessionManager.close().
   */
  private static class SessionStack {

    private final ThreadLocal<Deque<SqlSessionManager>> sessionStack = new ThreadLocal<>();

    /**
     * Get latest SqlSession from session stack.
     *
     * @return Stack of SqlSessionManager
     */
    private Deque<SqlSessionManager> getSessionStack() {
      Deque<SqlSessionManager> stack = sessionStack.get();
      if (stack == null) {
        stack = new ArrayDeque<>();
        sessionStack.set(stack);
      }
      return stack;
    }

    private void pushSessionToStack(SqlSessionManager session) {
      getSessionStack().push(session);
    }

    private void popSessionFromStack() {
      Deque<SqlSessionManager> stack = getSessionStack();
      if (!stack.isEmpty()) {
        stack.pop();
      }
    }
  }

  /** Internal Engine for internal operations. */
  private class InternalTaskanaEngineImpl implements InternalTaskanaEngine {

    @Override
    public void openConnection() {
      initSqlSession();
      try {
        sessionManager.getConnection().setSchema(taskanaEngineConfiguration.getSchemaName());
      } catch (SQLException e) {
        throw new SystemException(
            "Method openConnection() could not open a connection "
                + "to the database. No schema has been created.",
            e.getCause());
      }
      if (mode != ConnectionManagementMode.EXPLICIT) {
        SESSION_STACK.pushSessionToStack(sessionManager);
      }
    }

    @Override
    public void returnConnection() {
      if (mode != ConnectionManagementMode.EXPLICIT) {
        SESSION_STACK.popSessionFromStack();
        if (SESSION_STACK.getSessionStack().isEmpty()
            && sessionManager != null
            && sessionManager.isManagedSessionStarted()) {
          if (mode == ConnectionManagementMode.AUTOCOMMIT) {
            try {
              sessionManager.commit();
            } catch (Exception e) {
              throw new AutocommitFailedException(e.getCause());
            }
          }
          sessionManager.close();
        }
      }
    }

    @Override
    public <T> T executeInDatabaseConnection(Supplier<T> supplier) {
      try {
        openConnection();
        return supplier.get();
      } finally {
        // will be called before return & in case of exceptions
        returnConnection();
      }
    }

    @Override
    public void initSqlSession() {
      if (mode == ConnectionManagementMode.EXPLICIT && connection == null) {
        throw new ConnectionNotSetException();
      } else if (mode != ConnectionManagementMode.EXPLICIT
          && !sessionManager.isManagedSessionStarted()) {
        sessionManager.startManagedSession();
      }
    }

    @Override
    public boolean domainExists(String domain) {
      return getConfiguration().getDomains().contains(domain);
    }

    @Override
    public SqlSession getSqlSession() {
      return sessionManager;
    }

    @Override
    public TaskanaEngine getEngine() {
      return TaskanaEngineImpl.this;
    }

    @Override
    public HistoryEventManager getHistoryEventManager() {
      return historyEventManager;
    }

    @Override
    public TaskRoutingManager getTaskRoutingManager() {
      return taskRoutingManager;
    }

    @Override
    public CreateTaskPreprocessorManager getCreateTaskPreprocessorManager() {
      return createTaskPreprocessorManager;
    }

    @Override
    public PriorityServiceManager getPriorityServiceManager() {
      return priorityServiceManager;
    }
  }
}
