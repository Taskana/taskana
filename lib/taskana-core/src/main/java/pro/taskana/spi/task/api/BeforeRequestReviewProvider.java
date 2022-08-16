package pro.taskana.spi.task.api;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;

/**
 * The BeforeRequestReviewProvider allows to implement customized behaviour before a review has been
 * requested on a given {@linkplain Task}.
 */
public interface BeforeRequestReviewProvider {

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
   * Perform any action before a review has been requested on a {@linkplain Task} through
   * {@linkplain pro.taskana.task.api.TaskService#requestReview(String)} or {@linkplain
   * pro.taskana.task.api.TaskService#forceRequestReview(String)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * pro.taskana.task.api.TaskService#requestReview(String)}.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#requestReview(String)}.
   *
   * @param task the {@linkplain Task} before {@linkplain
   *     pro.taskana.task.api.TaskService#requestReview(String)} or {@linkplain
   *     pro.taskana.task.api.TaskService#forceRequestReview(String)} has started
   * @return the modified {@linkplain Task}. <b>IMPORTANT:</b> persistent changes to the {@linkplain
   *     Task} have to be managed by the service provider
   * @throws Exception if the service provider throws any exception
   */
  Task beforeRequestReview(Task task) throws Exception;
}
