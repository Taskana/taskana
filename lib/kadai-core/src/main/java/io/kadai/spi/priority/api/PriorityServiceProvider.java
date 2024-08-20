package io.kadai.spi.priority.api;

import io.kadai.common.api.KadaiEngine;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import java.util.OptionalInt;

/**
 * The PriorityServiceProvider allows to determine the priority of a {@linkplain Task} according to
 * custom logic.
 */
public interface PriorityServiceProvider {

  /**
   * Provide the active {@linkplain KadaiEngine} which is initialized for this KADAI installation.
   *
   * <p>This method is called during KADAI startup and allows the service provider to store the
   * active {@linkplain KadaiEngine} for later usage.
   *
   * @param kadaiEngine the active {@linkplain KadaiEngine} which is initialized for this
   *     installation
   */
  default void initialize(KadaiEngine kadaiEngine) {}

  /**
   * Determine the {@linkplain Task#getPriority() priority} of a certain {@linkplain Task} during
   * execution of {@linkplain io.kadai.task.api.TaskService#createTask(Task)} and {@linkplain
   * io.kadai.task.api.TaskService#updateTask(Task)}. This priority overwrites the priority from
   * Classification-driven logic.
   *
   * <p>The implemented method must calculate the {@linkplain Task#getPriority() priority}
   * efficiently. There can be a huge amount of {@linkplain Task Tasks} the SPI has to handle.
   *
   * <p>The behaviour is undefined if this method tries to apply persistent changes to any entity.
   *
   * <p>This SPI is executed with the same {@linkplain io.kadai.common.api.security.UserPrincipal}
   * and {@linkplain io.kadai.common.api.security.GroupPrincipal} as in {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task)} or {@linkplain
   * io.kadai.task.api.TaskService#updateTask(Task)}.
   *
   * @param taskSummary the {@linkplain TaskSummary} to compute the {@linkplain Task#getPriority()
   *     priority} for
   * @return the computed {@linkplain Task#getPriority() priority}
   */
  OptionalInt calculatePriority(TaskSummary taskSummary);
}
