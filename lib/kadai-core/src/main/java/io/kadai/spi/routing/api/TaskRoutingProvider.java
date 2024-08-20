package io.kadai.spi.routing.api;

import io.kadai.common.api.KadaiEngine;
import io.kadai.task.api.models.Task;
import io.kadai.workbasket.api.models.Workbasket;

/**
 * The TaskRoutingProvider allows to determine the {@linkplain Workbasket} for a {@linkplain Task}
 * that has no {@linkplain Workbasket} on {@linkplain io.kadai.task.api.TaskService#createTask(Task)
 * creation}.
 */
public interface TaskRoutingProvider {

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
   * Determine the {@linkplain Workbasket#getId() id} of the {@linkplain Workbasket} for a given
   * {@linkplain Task}.This method will be invoked by KADAI when it is asked to {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task) create} a {@linkplain Task} that has no
   * {@linkplain Workbasket} assigned.
   *
   * <p>If more than one TaskRoutingProvider class is registered, KADAI calls them all and uses
   * their results only if they agree on the {@linkplain Workbasket}. This is, if more than one
   * {@linkplain Workbasket#getId() ids} are returned, KADAI uses them only if they are identical.
   * If different ids are returned, the {@linkplain Task} will not be {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task) created}.
   *
   * <p>If the {@linkplain Workbasket} cannot be computed, the method should return NULL. If every
   * registered TaskRoutingProvider return NULL, the {@linkplain Task} will not be {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task) created}
   *
   * <p>The behaviour is undefined if this method tries to apply persistent changes to any entity.
   *
   * <p>This SPI is executed with the same {@linkplain io.kadai.common.api.security.UserPrincipal}
   * and {@linkplain io.kadai.common.api.security.GroupPrincipal} as in {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task)}.
   *
   * @param task the {@linkplain Task} for which a {@linkplain Workbasket} must be determined
   * @return the {@linkplain Workbasket#getId() id} of the {@linkplain Workbasket}
   */
  String determineWorkbasketId(Task task);
}
