package pro.taskana.simplehistory.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
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

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.OracleSqlSessionFactory;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.persistence.InstantTypeHandler;
import pro.taskana.common.internal.persistence.MapTypeHandler;
import pro.taskana.common.internal.persistence.StringTypeHandler;
import pro.taskana.simplehistory.TaskanaHistoryEngine;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryQueryMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryEventMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.user.internal.UserMapper;

/** This is the implementation of TaskanaHistoryEngine. */
public class TaskanaHistoryEngineImpl implements TaskanaHistoryEngine {

  protected static final ThreadLocal<Deque<SqlSessionManager>> SESSION_STACK = new ThreadLocal<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaHistoryEngineImpl.class);
  private static final String DEFAULT = "default";
  private final SqlSessionManager sessionManager;
  private final TaskanaConfiguration taskanaConfiguration;
  private final TaskanaEngine taskanaEngine;
  private TransactionFactory transactionFactory;
  private TaskanaHistory taskanaHistoryService;

  protected TaskanaHistoryEngineImpl(TaskanaEngine taskanaEngine) {
    this.taskanaConfiguration = taskanaEngine.getConfiguration();
    this.taskanaEngine = taskanaEngine;

    createTransactionFactory(taskanaConfiguration.isUseManagedTransactions());
    sessionManager = createSqlSessionManager();
  }

  public static TaskanaHistoryEngineImpl createTaskanaEngine(TaskanaEngine taskanaEngine) {
    return new TaskanaHistoryEngineImpl(taskanaEngine);
  }

  @Override
  public TaskanaHistory getTaskanaHistoryService() {
    if (taskanaHistoryService == null) {
      SimpleHistoryServiceImpl historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(taskanaEngine);
      taskanaHistoryService = historyService;
    }
    return taskanaHistoryService;
  }

  public boolean isUserInRole(TaskanaRole... roles) {
    if (!getConfiguration().isSecurityEnabled()) {
      return true;
    }

    Set<String> rolesMembers =
        Arrays.stream(roles)
            .map(role -> getConfiguration().getRoleMap().get(role))
            .collect(HashSet::new, Set::addAll, Set::addAll);

    return taskanaEngine.getCurrentUserContext().getAccessIds().stream()
        .anyMatch(rolesMembers::contains);
  }

  public void checkRoleMembership(TaskanaRole... roles) throws MismatchedRoleException {
    if (!isUserInRole(roles)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Throwing NotAuthorizedException because accessIds {} are not member of roles {}",
            taskanaEngine.getCurrentUserContext().getAccessIds(),
            Arrays.toString(roles));
      }
      throw new MismatchedRoleException(taskanaEngine.getCurrentUserContext().getUserid(), roles);
    }
  }

  public TaskanaConfiguration getConfiguration() {
    return this.taskanaConfiguration;
  }

  protected SqlSessionManager createSqlSessionManager() {
    Environment environment =
        new Environment(DEFAULT, this.transactionFactory, taskanaConfiguration.getDatasource());
    Configuration configuration = new Configuration(environment);

    // set databaseId
    DB db;
    try (Connection con = taskanaConfiguration.getDatasource().getConnection()) {
      db = DB.getDB(con);
      configuration.setDatabaseId(db.dbProductId);
    } catch (SQLException e) {
      throw new SystemException("Could not open a connection to set the databaseId", e);
    }

    // register type handlers
    if (DB.ORACLE == db) {
      // Use NULL instead of OTHER when jdbcType is not specified for null values,
      // otherwise oracle driver will chunck on null values
      configuration.setJdbcTypeForNull(JdbcType.NULL);
      configuration.getTypeHandlerRegistry().register(String.class, new StringTypeHandler());
    }
    configuration.getTypeHandlerRegistry().register(new MapTypeHandler());
    configuration.getTypeHandlerRegistry().register(Instant.class, new InstantTypeHandler());
    configuration.getTypeHandlerRegistry().register(JdbcType.TIMESTAMP, new InstantTypeHandler());

    // add mappers
    configuration.addMapper(TaskHistoryEventMapper.class);
    configuration.addMapper(TaskHistoryQueryMapper.class);
    configuration.addMapper(WorkbasketHistoryEventMapper.class);
    configuration.addMapper(WorkbasketHistoryQueryMapper.class);
    configuration.addMapper(ClassificationHistoryEventMapper.class);
    configuration.addMapper(ClassificationHistoryQueryMapper.class);
    configuration.addMapper(UserMapper.class);

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

  protected static void pushSessionToStack(SqlSessionManager session) {
    getSessionStack().push(session);
  }

  protected static void popSessionFromStack() {
    Deque<SqlSessionManager> stack = getSessionStack();
    if (!stack.isEmpty()) {
      stack.pop();
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
   * by calling sessionManager.close()
   *
   * @return Stack of SqlSessionManager
   */
  protected static Deque<SqlSessionManager> getSessionStack() {
    Deque<SqlSessionManager> stack = SESSION_STACK.get();
    if (stack == null) {
      stack = new ArrayDeque<>();
      SESSION_STACK.set(stack);
    }
    return stack;
  }

  protected static SqlSessionManager getSessionFromStack() {
    Deque<SqlSessionManager> stack = getSessionStack();
    if (stack.isEmpty()) {
      return null;
    }
    return stack.peek();
  }

  /**
   * Open the connection to the database. to be called at the begin of each Api call that accesses
   * the database
   *
   * @throws SQLException thrown if the connection could not be opened.
   */
  void openConnection() throws SQLException {
    initSqlSession();
    this.sessionManager.getConnection().setSchema(taskanaConfiguration.getSchemaName());
  }

  /**
   * Returns the database connection into the pool. In the case of nested calls, simply pops the
   * latest session from the session stack. Closes the connection if the session stack is empty. In
   * mode AUTOCOMMIT commits before the connection is closed. To be called at the end of each Api
   * call that accesses the database
   */
  void returnConnection() {
    popSessionFromStack();
    if (getSessionStack().isEmpty()
        && this.sessionManager != null
        && this.sessionManager.isManagedSessionStarted()) {
      try {
        this.sessionManager.commit();
      } catch (Exception e) {
        // ignore
      }
      this.sessionManager.close();
    }
  }

  /** Initializes the SqlSessionManager. */
  void initSqlSession() {
    this.sessionManager.startManagedSession();
  }

  /**
   * retrieve the SqlSession used by taskana.
   *
   * @return the myBatis SqlSession object used by taskana
   */
  SqlSession getSqlSession() {
    return this.sessionManager;
  }

  /**
   * creates the MyBatis transaction factory.
   *
   * @param useManagedTransactions true if TASKANA should use a ManagedTransactionFactory.
   */
  private void createTransactionFactory(boolean useManagedTransactions) {
    if (useManagedTransactions) {
      this.transactionFactory = new ManagedTransactionFactory();
    } else {
      this.transactionFactory = new JdbcTransactionFactory();
    }
  }
}
