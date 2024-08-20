package acceptance.priorityservice;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.spi.priority.api.PriorityServiceProvider;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.models.TaskSummary;
import java.time.Duration;
import java.time.Instant;
import java.util.OptionalInt;

public class TestPriorityServiceProvider implements PriorityServiceProvider {

  private static final int MULTIPLIER = 10;

  private WorkingTimeCalculator calculator;

  @Override
  public void initialize(KadaiEngine kadaiEngine) {
    calculator = kadaiEngine.getWorkingTimeCalculator();
  }

  @Override
  public OptionalInt calculatePriority(TaskSummary taskSummary) {

    long priority;
    try {
      priority =
          calculator.workingTimeBetween(taskSummary.getCreated(), Instant.now()).toMinutes() + 1;
    } catch (Exception e) {
      priority = Duration.between(taskSummary.getCreated(), Instant.now()).toMinutes();
    }

    if (Boolean.parseBoolean(taskSummary.getCustomField(TaskCustomField.CUSTOM_6))) {
      priority *= MULTIPLIER;
    }

    return OptionalInt.of(Math.toIntExact(priority));
  }
}
