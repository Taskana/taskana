package acceptance.priorityservice;

import java.time.Duration;
import java.time.Instant;
import java.util.OptionalInt;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.TaskSummary;

public class TestPriorityServiceProvider implements PriorityServiceProvider {

  private static final int MULTIPLIER = 10;

  private WorkingTimeCalculator calculator;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    calculator = taskanaEngine.getWorkingTimeCalculator();
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
