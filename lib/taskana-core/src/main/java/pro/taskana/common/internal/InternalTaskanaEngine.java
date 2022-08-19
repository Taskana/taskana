package pro.taskana.common.internal;

import java.util.function.Supplier;
import org.apache.ibatis.session.SqlSession;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.spi.routing.internal.TaskRoutingManager;
import pro.taskana.spi.task.internal.AfterRequestChangesManager;
import pro.taskana.spi.task.internal.AfterRequestReviewManager;
import pro.taskana.spi.task.internal.BeforeRequestChangesManager;
import pro.taskana.spi.task.internal.BeforeRequestReviewManager;
import pro.taskana.spi.task.internal.CreateTaskPreprocessorManager;
import pro.taskana.spi.task.internal.ReviewRequiredManager;

/**
 * FOR INTERNAL USE ONLY.
 *
 * <p>Contains all actions which are necessary within taskana.
 */
public interface InternalTaskanaEngine {

  /**
   * Opens the connection to the database. Has to be called at the beginning of each Api call that
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
   * Executes the given supplier after openConnection is called and then returns the connection.
   *
   * @param supplier a function that returns something of type T
   * @param <T> any type
   * @return the result of the supplier
   */
  <T> T executeInDatabaseConnection(Supplier<T> supplier);

  /**
   * Executes the given runnable after openConnection is called and then returns the connection.
   *
   * @see #executeInDatabaseConnection(Supplier)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default void executeInDatabaseConnection(Runnable runnable) {
    executeInDatabaseConnection(
        () -> {
          runnable.run();
          return null;
        });
  }

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
  HistoryEventManager getHistoryEventManager();

  /**
   * Retrieve TaskRoutingProducer.
   *
   * @return the TaskRoutingProducer instance.
   */
  TaskRoutingManager getTaskRoutingManager();

  /**
   * Retrieve CreateTaskPreprocessorManager.
   *
   * @return the CreateTaskPreprocessorManager instance.
   */
  CreateTaskPreprocessorManager getCreateTaskPreprocessorManager();

  /**
   * Retrieves the {@linkplain PriorityServiceManager}.
   *
   * @return the {@linkplain PriorityServiceManager} instance
   */
  PriorityServiceManager getPriorityServiceManager();

  /**
   * Retrieves the {@linkplain ReviewRequiredManager}.
   *
   * @return the {@linkplain ReviewRequiredManager} instance
   */
  ReviewRequiredManager getReviewRequiredManager();

  /**
   * Retrieves the {@linkplain BeforeRequestReviewManager}.
   *
   * @return the {@linkplain BeforeRequestReviewManager} instance
   */
  BeforeRequestReviewManager getBeforeRequestReviewManager();

  /**
   * Retrieves the {@linkplain AfterRequestReviewManager}.
   *
   * @return the {@linkplain AfterRequestReviewManager} instance
   */
  AfterRequestReviewManager getAfterRequestReviewManager();

  /**
   * Retrieves the {@linkplain BeforeRequestChangesManager}.
   *
   * @return the {@linkplain BeforeRequestChangesManager} instance
   */
  BeforeRequestChangesManager getBeforeRequestChangesManager();

  /**
   * Retrieves the {@linkplain AfterRequestChangesManager}.
   *
   * @return the {@linkplain AfterRequestChangesManager} instance
   */
  AfterRequestChangesManager getAfterRequestChangesManager();
}
