package acceptance.priorityservice;

import java.time.Duration;
import java.time.Instant;
import java.util.OptionalInt;

import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.TaskSummary;

public class TestPriorityServiceProvider implements PriorityServiceProvider {
  private static final int MULTIPLIER = 10;

  @Override
  public OptionalInt calculatePriority(TaskSummary taskSummary) {
    WorkingDaysToDaysConverter converter = new WorkingDaysToDaysConverter(true, true);
    WorkingTimeCalculator calculator = new WorkingTimeCalculator(converter);
    int priority;
    try {
      priority =
          Math.toIntExact(
                  calculator
                      .workingTimeBetweenTwoTimestamps(taskSummary.getCreated(), Instant.now())
                      .toMinutes())
              + 1;
    } catch (InvalidArgumentException | ArithmeticException e) {
      long diffInDays = Duration.between(taskSummary.getCreated(), Instant.now()).toDays();
      priority = diffInDays >= 1 ? Math.toIntExact(diffInDays) : 1;

      if ("true".equals(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_6))) {
        priority *= MULTIPLIER;
      }
    }

    return OptionalInt.of(priority);
  }
}
