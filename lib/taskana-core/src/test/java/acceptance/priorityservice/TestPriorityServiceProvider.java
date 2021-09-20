package acceptance.priorityservice;

import java.time.Duration;
import java.time.Instant;
import java.util.OptionalInt;

import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.TaskSummary;

public class TestPriorityServiceProvider implements PriorityServiceProvider {
  private static final int MULTIPLIER = 10;

  @Override
  public OptionalInt calculatePriority(TaskSummary taskSummary) {
    long diffInDays = Duration.between(taskSummary.getCreated(), Instant.now()).toDays();
    int priority = diffInDays >= 1 ? Math.toIntExact(diffInDays) : 1;

    if ("true".equals(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_6))) {
      priority *= MULTIPLIER;
    }
    return OptionalInt.of(priority);
  }
}
