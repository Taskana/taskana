package io.kadai.spi.task.api;

import io.kadai.common.api.KadaiEngine;
import io.kadai.task.api.models.Task;

/**
 * The AfterRequestChangesProvider allows to implement customized behaviour after changes have been
 * requested on a given {@linkplain Task}.
 */
public interface AfterRequestChangesProvider {

  /**
   * Provide the active {@linkplain KadaiEngine} which is initialized for this KADAI installation.
   *
   * <p>This method is called during KADAI startup and allows the service provider to store the
   * active {@linkplain KadaiEngine} for later usage.
   *
   * @param kadaiEngine the active {@linkplain KadaiEngine} which is initialized for this
   *     installation
   */
  void initialize(KadaiEngine kadaiEngine);

  /**
   * Perform any action after changes have been requested on a {@linkplain Task} through {@linkplain
   * io.kadai.task.api.TaskService#requestChanges(String)} or {@linkplain
   * io.kadai.task.api.TaskService#forceRequestChanges(String)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * io.kadai.task.api.TaskService#requestChanges(String)}.
   *
   * <p>This SPI is executed with the same {@linkplain io.kadai.common.api.security.UserPrincipal}
   * and {@linkplain io.kadai.common.api.security.GroupPrincipal} as in {@linkplain
   * io.kadai.task.api.TaskService#requestChanges(String)}.
   *
   * @param task the {@linkplain Task} after {@linkplain
   *     io.kadai.task.api.TaskService#requestChanges(String)} or {@linkplain
   *     io.kadai.task.api.TaskService#forceRequestChanges(String)} has completed
   * @return the modified {@linkplain Task}. <b>IMPORTANT:</b> persistent changes to the {@linkplain
   *     Task} have to be managed by the service provider
   * @throws Exception if the service provider throws any exception
   */
  Task afterRequestChanges(Task task) throws Exception;
}
