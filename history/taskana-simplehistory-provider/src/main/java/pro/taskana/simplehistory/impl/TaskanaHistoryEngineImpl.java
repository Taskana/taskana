package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.history.api.TaskanaHistory;
import pro.taskana.simplehistory.TaskanaHistoryEngine;
import pro.taskana.simplehistory.configuration.DbSchemaCreator;
import pro.taskana.simplehistory.impl.mappings.HistoryEventMapper;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;

/** This is the implementation of TaskanaHistoryEngine. */
public class TaskanaHistoryEngineImpl implements TaskanaHistoryEngine {

  protected static final ThreadLocal<Deque<SqlSessionManager>> SESSION_STACK = new ThreadLocal<>();

  private static final String DEFAULT = "default";
  protected SqlSessionManager sessionManager;
  protected TransactionFactory transactionFactory;
  protected TaskanaHistory taskanaHistoryService;

  TaskanaEngineConfiguration taskanaEngineConfiguration;

  protected TaskanaHistoryEngineImpl(TaskanaEngineConfiguration taskanaEngineConfiguration)
      throws SQLException {
    this.taskanaEngineConfiguration = taskanaEngineConfiguration;

    createTransactionFactory(this.taskanaEngineConfiguration.getUseManagedTransactions());
    this.sessionManager = createSqlSessionManager();
    new DbSchemaCreator(
            taskanaEngineConfiguration.getDatasource(), taskanaEngineConfiguration.getSchemaName())
        .run();
  }

  public static TaskanaHistoryEngineImpl createTaskanaEngine(
      TaskanaEngineConfiguration taskanaEngineConfiguration) throws SQLException {
    return new TaskanaHistoryEngineImpl(taskanaEngineConfiguration);
  }

  @Override
  public TaskanaHistory getTaskanaHistoryService() {
    if (taskanaHistoryService == null) {
      SimpleHistoryServiceImpl historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(taskanaEngineConfiguration);
      this.taskanaHistoryService = historyService;
    }
    return this.taskanaHistoryService;
  }

  /**
   * Open the connection to the database. to be called at the begin of each Api call that accesses
   * the database
   *
   * @throws SQLException thrown if the connection could not be opened.
   */
  void openConnection() throws SQLException {
    initSqlSession();
    this.sessionManager.getConnection().setSchema(taskanaEngineConfiguration.getSchemaName());
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
        //ignore
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

  protected SqlSessionManager createSqlSessionManager() {
    Environment environment =
        new Environment(
            DEFAULT, this.transactionFactory, taskanaEngineConfiguration.getDatasource());
    Configuration configuration = new Configuration(environment);

    // add mappers
    configuration.addMapper(HistoryEventMapper.class);
    configuration.addMapper(HistoryQueryMapper.class);
    SqlSessionFactory localSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
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
