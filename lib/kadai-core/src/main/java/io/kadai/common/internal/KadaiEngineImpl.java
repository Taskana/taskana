package io.kadai.common.internal;

import static io.kadai.common.api.KadaiEngine.ConnectionManagementMode.EXPLICIT;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.internal.ClassificationMapper;
import io.kadai.classification.internal.ClassificationQueryMapper;
import io.kadai.classification.internal.ClassificationServiceImpl;
import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.JobService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.common.api.exceptions.AutocommitFailedException;
import io.kadai.common.api.exceptions.ConnectionNotSetException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.api.security.UserPrincipal;
import io.kadai.common.internal.configuration.DB;
import io.kadai.common.internal.configuration.DbSchemaCreator;
import io.kadai.common.internal.jobs.JobScheduler;
import io.kadai.common.internal.jobs.RealClock;
import io.kadai.common.internal.persistence.InstantTypeHandler;
import io.kadai.common.internal.persistence.MapTypeHandler;
import io.kadai.common.internal.persistence.StringTypeHandler;
import io.kadai.common.internal.security.CurrentUserContextImpl;
import io.kadai.common.internal.workingtime.HolidaySchedule;
import io.kadai.common.internal.workingtime.WorkingDayCalculatorImpl;
import io.kadai.common.internal.workingtime.WorkingTimeCalculatorImpl;
import io.kadai.monitor.api.MonitorService;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.monitor.internal.MonitorServiceImpl;
import io.kadai.spi.history.internal.HistoryEventManager;
import io.kadai.spi.priority.internal.PriorityServiceManager;
import io.kadai.spi.routing.internal.TaskRoutingManager;
import io.kadai.spi.task.internal.AfterRequestChangesManager;
import io.kadai.spi.task.internal.AfterRequestReviewManager;
import io.kadai.spi.task.internal.BeforeRequestChangesManager;
import io.kadai.spi.task.internal.BeforeRequestReviewManager;
import io.kadai.spi.task.internal.CreateTaskPreprocessorManager;
import io.kadai.spi.task.internal.ReviewRequiredManager;
import io.kadai.spi.task.internal.TaskEndstatePreprocessorManager;
import io.kadai.task.api.TaskService;
import io.kadai.task.internal.AttachmentMapper;
import io.kadai.task.internal.ObjectReferenceMapper;
import io.kadai.task.internal.TaskCommentMapper;
import io.kadai.task.internal.TaskCommentQueryMapper;
import io.kadai.task.internal.TaskMapper;
import io.kadai.task.internal.TaskQueryMapper;
import io.kadai.task.internal.TaskServiceImpl;
import io.kadai.user.api.UserService;
import io.kadai.user.internal.UserMapper;
import io.kadai.user.internal.UserServiceImpl;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.internal.DistributionTargetMapper;
import io.kadai.workbasket.internal.WorkbasketAccessMapper;
import io.kadai.workbasket.internal.WorkbasketMapper;
import io.kadai.workbasket.internal.WorkbasketQueryMapper;
import io.kadai.workbasket.internal.WorkbasketServiceImpl;
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

/** This is the implementation of KadaiEngine. */
public class KadaiEngineImpl implements KadaiEngine {

  // must match the VERSION value in table
  private static final Logger LOGGER = LoggerFactory.getLogger(KadaiEngineImpl.class);
  private static final SessionStack SESSION_STACK = new SessionStack();
  protected final KadaiConfiguration kadaiConfiguration;
  private final TaskRoutingManager taskRoutingManager;
  private final CreateTaskPreprocessorManager createTaskPreprocessorManager;
  private final PriorityServiceManager priorityServiceManager;
  private final ReviewRequiredManager reviewRequiredManager;
  private final BeforeRequestReviewManager beforeRequestReviewManager;
  private final AfterRequestReviewManager afterRequestReviewManager;
  private final BeforeRequestChangesManager beforeRequestChangesManager;
  private final AfterRequestChangesManager afterRequestChangesManager;
  private final TaskEndstatePreprocessorManager taskEndstatePreprocessorManager;

  private final InternalKadaiEngineImpl internalKadaiEngineImpl;
  private final WorkingTimeCalculator workingTimeCalculator;
  private final HistoryEventManager historyEventManager;
  private final CurrentUserContext currentUserContext;
  private final JobScheduler jobScheduler;
  protected ConnectionManagementMode mode;
  protected TransactionFactory transactionFactory;
  protected SqlSessionManager sessionManager;
  protected Connection connection;

  protected KadaiEngineImpl(
      KadaiConfiguration kadaiConfiguration,
      ConnectionManagementMode connectionManagementMode,
      TransactionFactory transactionFactory)
      throws SQLException {
    LOGGER.info(
        "initializing KADAI with this configuration: {} and this mode: {}",
        kadaiConfiguration,
        connectionManagementMode);
    if (connectionManagementMode == EXPLICIT) {
      // at first we initialize Kadai DB with autocommit,
      // at the end of constructor the mode is set
      this.mode = ConnectionManagementMode.AUTOCOMMIT;
    } else {
      this.mode = connectionManagementMode;
    }
    this.kadaiConfiguration = kadaiConfiguration;
    internalKadaiEngineImpl = new InternalKadaiEngineImpl();
    HolidaySchedule holidaySchedule =
        new HolidaySchedule(
            kadaiConfiguration.isGermanPublicHolidaysEnabled(),
            kadaiConfiguration.isGermanPublicHolidaysCorpusChristiEnabled(),
            kadaiConfiguration.getCustomHolidays());
    if (kadaiConfiguration.isUseWorkingTimeCalculation()) {
      workingTimeCalculator =
          new WorkingTimeCalculatorImpl(
              holidaySchedule,
              kadaiConfiguration.getWorkingTimeSchedule(),
              kadaiConfiguration.getWorkingTimeScheduleTimeZone());
    } else {
      workingTimeCalculator =
          new WorkingDayCalculatorImpl(
              holidaySchedule, kadaiConfiguration.getWorkingTimeScheduleTimeZone());
    }

    currentUserContext =
        new CurrentUserContextImpl(KadaiConfiguration.shouldUseLowerCaseForAccessIds());
    if (transactionFactory == null) {
      createTransactionFactory(kadaiConfiguration.isUseManagedTransactions());
    } else {
      this.transactionFactory = transactionFactory;
    }
    sessionManager = createSqlSessionManager();

    initializeDbSchema(kadaiConfiguration);

    if (this.kadaiConfiguration.isJobSchedulerEnabled()) {
      KadaiConfiguration configuration =
          new KadaiConfiguration.Builder(this.kadaiConfiguration)
              .jobSchedulerEnabled(false)
              .build();
      KadaiEngine kadaiEngine =
          KadaiEngine.buildKadaiEngine(configuration, EXPLICIT, transactionFactory);
      RealClock clock =
          new RealClock(
              this.kadaiConfiguration.getJobSchedulerInitialStartDelay(),
              this.kadaiConfiguration.getJobSchedulerPeriod(),
              this.kadaiConfiguration.getJobSchedulerPeriodTimeUnit());
      jobScheduler = new JobScheduler(kadaiEngine, clock);
      jobScheduler.start();
    } else {
      jobScheduler = null;
    }

    // IMPORTANT: SPI has to be initialized last (and in this order) in order
    // to provide a fully initialized KadaiEngine instance during the SPI initialization!
    createTaskPreprocessorManager = new CreateTaskPreprocessorManager();
    priorityServiceManager = new PriorityServiceManager(this);
    historyEventManager = new HistoryEventManager(this);
    taskRoutingManager = new TaskRoutingManager(this);
    reviewRequiredManager = new ReviewRequiredManager(this);
    beforeRequestReviewManager = new BeforeRequestReviewManager(this);
    afterRequestReviewManager = new AfterRequestReviewManager(this);
    beforeRequestChangesManager = new BeforeRequestChangesManager(this);
    afterRequestChangesManager = new AfterRequestChangesManager(this);
    taskEndstatePreprocessorManager = new TaskEndstatePreprocessorManager();

    // don't remove, to reset possible explicit mode
    this.mode = connectionManagementMode;
  }

  public static KadaiEngine createKadaiEngine(
      KadaiConfiguration kadaiConfiguration,
      ConnectionManagementMode connectionManagementMode,
      TransactionFactory transactionFactory)
      throws SQLException {
    return new KadaiEngineImpl(kadaiConfiguration, connectionManagementMode, transactionFactory);
  }

  @Override
  public ConfigurationService getConfigurationService() {
    return new ConfigurationServiceImpl(
        internalKadaiEngineImpl, sessionManager.getMapper(ConfigurationMapper.class));
  }

  @Override
  public TaskService getTaskService() {
    return new TaskServiceImpl(
        internalKadaiEngineImpl,
        sessionManager.getMapper(TaskMapper.class),
        sessionManager.getMapper(TaskCommentMapper.class),
        sessionManager.getMapper(AttachmentMapper.class),
        sessionManager.getMapper(ObjectReferenceMapper.class),
        sessionManager.getMapper(UserMapper.class));
  }

  @Override
  public MonitorService getMonitorService() {
    return new MonitorServiceImpl(
        internalKadaiEngineImpl, sessionManager.getMapper(MonitorMapper.class));
  }

  @Override
  public WorkbasketService getWorkbasketService() {
    return new WorkbasketServiceImpl(
        internalKadaiEngineImpl,
        historyEventManager,
        sessionManager.getMapper(WorkbasketMapper.class),
        sessionManager.getMapper(DistributionTargetMapper.class),
        sessionManager.getMapper(WorkbasketAccessMapper.class));
  }

  @Override
  public ClassificationService getClassificationService() {
    return new ClassificationServiceImpl(
        internalKadaiEngineImpl,
        priorityServiceManager,
        sessionManager.getMapper(ClassificationMapper.class),
        sessionManager.getMapper(TaskMapper.class));
  }

  public Connection getConnection() {
    return connection;
  }

  @Override
  public void setConnection(Connection connection) throws SQLException {
    if (connection != null) {
      this.connection = connection;
      // disabling auto commit for passed connection in order to gain full control over the
      // connection management
      connection.setAutoCommit(false);
      connection.setSchema(kadaiConfiguration.getSchemaName());
      mode = EXPLICIT;
      if (transactionFactory.getClass().getSimpleName().equals("SpringManagedTransactionFactory")) {
        sessionManager.startManagedSession();
      } else {
        sessionManager.startManagedSession(connection);
      }
    } else if (this.connection != null) {
      closeConnection();
    }
  }

  // This should be part of the InternalKadaiEngine. Unfortunately the jobs don't have access to
  // that engine.
  // Therefore, this getter exits and will be removed as soon as our jobs will be refactored.
  public PriorityServiceManager getPriorityServiceManager() {
    return priorityServiceManager;
  }

  @Override
  public JobService getJobService() {
    return new JobServiceImpl(internalKadaiEngineImpl, sessionManager.getMapper(JobMapper.class));
  }

  @Override
  public UserService getUserService() {
    return new UserServiceImpl(internalKadaiEngineImpl, sessionManager.getMapper(UserMapper.class));
  }

  public JobScheduler getJobScheduler() {
    return jobScheduler;
  }

  @Override
  public KadaiConfiguration getConfiguration() {
    return this.kadaiConfiguration;
  }

  @Override
  public WorkingTimeCalculator getWorkingTimeCalculator() {
    return workingTimeCalculator;
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
    if (this.mode == EXPLICIT && connection != null && mode != EXPLICIT) {
      if (sessionManager.isManagedSessionStarted()) {
        sessionManager.close();
      }
      connection = null;
    }
    this.mode = mode;
  }

  @Override
  public void closeConnection() {
    if (this.mode == EXPLICIT) {
      this.connection = null;
      if (sessionManager.isManagedSessionStarted()) {
        sessionManager.close();
      }
      mode = ConnectionManagementMode.PARTICIPATE;
    }
  }

  @Override
  public boolean isUserInRole(KadaiRole... roles) {
    if (!getConfiguration().isSecurityEnabled()) {
      return true;
    }

    List<String> accessIds = currentUserContext.getAccessIds();
    Set<String> rolesMembers = new HashSet<>();
    for (KadaiRole role : roles) {
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
  public void checkRoleMembership(KadaiRole... roles) throws NotAuthorizedException {
    if (!isUserInRole(roles)) {
      if (LOGGER.isDebugEnabled()) {
        String rolesAsString = Arrays.toString(roles);
        LOGGER.debug(
            "Throwing NotAuthorizedException because accessIds {} are not member of roles {}",
            currentUserContext.getAccessIds(),
            rolesAsString);
      }
      throw new NotAuthorizedException(currentUserContext.getUserid(), roles);
    }
  }

  public <T> T runAsAdmin(Supplier<T> supplier) {
    if (isUserInRole(KadaiRole.ADMIN)) {
      return supplier.get();
    }

    String adminName =
        this.getConfiguration().getRoleMap().get(KadaiRole.ADMIN).stream()
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

  @Override
  public void clearSqlSessionCache() {
    sessionManager.clearCache();
  }

  /**
   * This method creates the sqlSessionManager of myBatis. It integrates all the SQL mappers and
   * sets the databaseId attribute.
   *
   * @return a {@linkplain SqlSessionFactory}
   * @throws SystemException when a connection to the database could not be opened.
   */
  protected SqlSessionManager createSqlSessionManager() {
    Environment environment =
        new Environment("default", this.transactionFactory, kadaiConfiguration.getDataSource());
    Configuration configuration = new Configuration(environment);

    // set databaseId
    DB db;
    try (Connection con = kadaiConfiguration.getDataSource().getConnection()) {
      db = DB.getDB(con);
      configuration.setDatabaseId(db.dbProductId);
    } catch (SQLException e) {
      throw new SystemException(
          "Method createSqlSessionManager() could not open a connection "
              + "to the database. No databaseId has been set.",
          e.getCause());
    }

    // register type handlers
    if (DB.ORACLE == db) {
      // Use NULL instead of OTHER when jdbcType is not specified for null values,
      // otherwise oracle driver will chunk on null values
      configuration.setJdbcTypeForNull(JdbcType.NULL);
      configuration.getTypeHandlerRegistry().register(String.class, new StringTypeHandler());
    }

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

    SqlSessionFactory localSessionFactory;
    if (DB.ORACLE == db) {
      localSessionFactory =
          new SqlSessionFactoryBuilder() {
            @Override
            public SqlSessionFactory build(Configuration config) {
              return new OracleSqlSessionFactory(config);
            }
          }.build(configuration);
    } else {
      localSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    return SqlSessionManager.newInstance(localSessionFactory);
  }

  private void initializeDbSchema(KadaiConfiguration kadaiConfiguration) throws SQLException {
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(kadaiConfiguration.getDataSource(), kadaiConfiguration.getSchemaName());
    boolean schemaCreated = dbSchemaCreator.run();

    if (!schemaCreated && !dbSchemaCreator.isValidSchemaVersion(MINIMAL_KADAI_SCHEMA_VERSION)) {
      throw new SystemException(
          "The Database Schema Version doesn't match the expected minimal version "
              + MINIMAL_KADAI_SCHEMA_VERSION);
    }
    ((ConfigurationServiceImpl) getConfigurationService())
        .checkSecureAccess(kadaiConfiguration.isSecurityEnabled());
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
   * kadaiEngineImpl.openConnection(); ..... kadaiEngineImpl.returnConnection(); calls. In order to
   * avoid duplicate opening / closing of connections, we use the sessionStack in the following way:
   * Each time, an openConnection call is received, we push the current sessionManager onto the
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
  private class InternalKadaiEngineImpl implements InternalKadaiEngine {

    @Override
    public void openConnection() {
      initSqlSession();
      try {
        sessionManager.getConnection().setSchema(kadaiConfiguration.getSchemaName());
      } catch (SQLException e) {
        throw new SystemException(
            "Method openConnection() could not open a connection "
                + "to the database. No schema has been created.",
            e.getCause());
      }
      if (mode != EXPLICIT) {
        SESSION_STACK.pushSessionToStack(sessionManager);
      }
    }

    @Override
    public void returnConnection() {
      if (mode != EXPLICIT) {
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
      if (mode == EXPLICIT && connection == null) {
        throw new ConnectionNotSetException();
      } else if (mode != EXPLICIT && !sessionManager.isManagedSessionStarted()) {
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
    public KadaiEngine getEngine() {
      return KadaiEngineImpl.this;
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

    @Override
    public ReviewRequiredManager getReviewRequiredManager() {
      return reviewRequiredManager;
    }

    @Override
    public BeforeRequestReviewManager getBeforeRequestReviewManager() {
      return beforeRequestReviewManager;
    }

    @Override
    public AfterRequestReviewManager getAfterRequestReviewManager() {
      return afterRequestReviewManager;
    }

    @Override
    public BeforeRequestChangesManager getBeforeRequestChangesManager() {
      return beforeRequestChangesManager;
    }

    @Override
    public AfterRequestChangesManager getAfterRequestChangesManager() {
      return afterRequestChangesManager;
    }

    @Override
    public TaskEndstatePreprocessorManager getTaskEndstatePreprocessorManager() {
      return taskEndstatePreprocessorManager;
    }
  }
}
