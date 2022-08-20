package pro.taskana.spi.task.api;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;

/**
 * The BeforeRequestChangesProvider allows to implement customized behaviour before changes have
 * been requested on a given {@linkplain Task}.
 */
public interface BeforeRequestChangesProvider {

  /**
   * Provide the active {@linkplain TaskanaEngine} which is initialized for this TASKANA
   * installation.
   *
   * <p>This method is called during TASKANA startup and allows the service provider to store the
   * active {@linkplain TaskanaEngine} for later usage.
   *
   * @param taskanaEngine the active {@linkplain TaskanaEngine} which is initialized for this
   *     installation
   */
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Perform any action before changes have been requested on a {@linkplain Task} through
   * {@linkplain pro.taskana.task.api.TaskService#requestChanges(String)} or {@linkplain
   * pro.taskana.task.api.TaskService#forceRequestChanges(String)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * pro.taskana.task.api.TaskService#requestChanges(String)}.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#requestChanges(String)}.
   *
   * @param task the {@linkplain Task} before {@linkplain
   *     pro.taskana.task.api.TaskService#requestChanges(String)} or {@linkplain
   *     pro.taskana.task.api.TaskService#forceRequestChanges(String)} has started
   * @return the modified {@linkplain Task}. <b>IMPORTANT:</b> persistent changes to the {@linkplain
   *     Task} have to be managed by the service provider
   * @throws Exception if the service provider throws any exception
   */
  Task beforeRequestChanges(Task task) throws Exception;
}
