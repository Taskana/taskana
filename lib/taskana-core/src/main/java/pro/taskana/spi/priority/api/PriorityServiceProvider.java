package pro.taskana.spi.priority.api;

import java.util.OptionalInt;

import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/**
 * The PriorityServiceProvider allows to determine the priority of a {@linkplain Task} according to
 * custom logic.
 */
public interface PriorityServiceProvider {

  /**
   * Determine the {@linkplain Task#getPriority() priority} of a certain {@linkplain Task} during
   * execution of {@linkplain pro.taskana.task.api.TaskService#createTask(Task)} and {@linkplain
   * pro.taskana.task.api.TaskService#updateTask(Task)}. This priority overwrites the priority from
   * Classification-driven logic.
   *
   * <p>The implemented method must calculate the {@linkplain Task#getPriority() priority}
   * efficiently. There can be a huge amount of {@linkplain Task Tasks} the SPI has to handle.
   *
   * <p>The behaviour is undefined if this method tries to apply persistent changes to any entity.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)} or {@linkplain
   * pro.taskana.task.api.TaskService#updateTask(Task)}.
   *
   * @param taskSummary the {@linkplain TaskSummary} to compute the {@linkplain Task#getPriority()
   *     priority} for
   * @return the computed {@linkplain Task#getPriority() priority}
   */
  OptionalInt calculatePriority(TaskSummary taskSummary);
}
