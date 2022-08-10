package pro.taskana.spi.task.api;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;

/**
 * The ReviewRequiredProvider allows to determine whether a {@linkplain Task} requires a review
 * instead of completion.
 */
public interface ReviewRequiredProvider {

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
  default void initialize(TaskanaEngine taskanaEngine) {}

  /**
   * Determine if a {@linkplain Task} has to be reviewed instead of completed before {@linkplain
   * pro.taskana.task.api.TaskService#completeTask(String)} is executed.
   *
   * <p>The behaviour is undefined if this method tries to apply persistent changes to any entity.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#completeTask(String)}.
   *
   * @param task the {@linkplain Task} before {@linkplain
   *     pro.taskana.task.api.TaskService#completeTask(String)} has started
   * @return true, if {@linkplain pro.taskana.task.api.TaskService#requestReview(String)} should be
   *     executed instead of {@linkplain pro.taskana.task.api.TaskService#completeTask(String)}.
   *     False, if {@linkplain pro.taskana.task.api.TaskService#completeTask(String)} can be
   *     executed regularly
   */
  boolean reviewRequired(Task task);
}
