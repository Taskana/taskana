package pro.taskana.impl;

import java.util.function.Supplier;
import org.apache.ibatis.session.SqlSession;

import pro.taskana.TaskanaEngine;
import pro.taskana.history.HistoryEventProducer;
import pro.taskana.taskrouting.TaskRoutingManager;

/**
 * FOR INTERNAL USE ONLY.
 *
 * <p>Contains all actions which are necessary within taskana.
 */
public interface InternalTaskanaEngine {

  /**
   * Opens the connection to the database. Has to be called at the begin of each Api call that
   * accesses the database
   */
  void openConnection();

  /**
   * Returns the database connection into the pool. In the case of nested calls, simply pops the
   * latest session from the session stack. Closes the connection if the session stack is empty. In
   * mode AUTOCOMMIT commits before the connection is closed. To be called at the end of each Api
   * call that accesses the database
   */
  void returnConnection();

  /**
   * Executes the supplier after openConnection is called and then returns the connection.
   *
   * @param supplier a function that returns something of type T
   * @param <T> any type
   * @return the result of the supplier
   */
  <T> T openAndReturnConnection(Supplier<T> supplier);

  /** Initializes the SqlSessionManager. */
  void initSqlSession();

  /**
   * Returns true if the given domain does exist in the configuration.
   *
   * @param domain the domain specified in the configuration
   * @return <code>true</code> if the domain exists
   */
  boolean domainExists(String domain);

  /**
   * retrieve the SqlSession used by taskana.
   *
   * @return the myBatis SqlSession object used by taskana
   */
  SqlSession getSqlSession();

  /**
   * Retrieve TaskanaEngine.
   *
   * @return The nested TaskanaEngine.
   */
  TaskanaEngine getEngine();

  /**
   * Retrieve HistoryEventProducer.
   *
   * @return the HistoryEventProducer instance.
   */
  HistoryEventProducer getHistoryEventProducer();

  /**
   * Retrieve TaskRoutingProducer.
   *
   * @return the TaskRoutingProducer instance.
   */
  TaskRoutingManager getTaskRoutingManager();


  /**
   * This method is supposed to skip further permission checks if we are already in a secured
   * environment. With great power comes great responsibility.
   *
   * @param supplier will be executed with admin privileges
   * @param <T> defined with the supplier return value
   * @return output from supplier
   */
  <T> T runAsAdmin(Supplier<T> supplier);
}
