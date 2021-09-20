package pro.taskana.spi.priority.api;

import java.util.OptionalInt;

import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/**
 * This SPI enables the computation of {@linkplain Task} priorities depending on individual
 * preferences.
 */
public interface PriorityServiceProvider {

  /**
   * Calculates the {@linkplain Task#getPriority() priority} of a certain {@linkplain Task}.
   *
   * <p>The implemented method must calculate the {@linkplain Task#getPriority() priority}
   * efficiently. There can be a huge amount of {@linkplain Task Tasks} the SPI has to handle.
   *
   * @param taskSummary the {@linkplain TaskSummary} to compute the {@linkplain Task#getPriority()
   *     priority} for
   * @return the computed {@linkplain Task#getPriority() priority}
   */
  OptionalInt calculatePriority(TaskSummary taskSummary);
}
