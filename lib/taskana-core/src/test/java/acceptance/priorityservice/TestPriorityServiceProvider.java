package acceptance.priorityservice;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.TaskSummary;

public class TestPriorityServiceProvider implements PriorityServiceProvider {
  private static final int MULTIPLIER = 10;

  @Override
  public Optional<Integer> calculatePriority(TaskSummary taskSummary) {
    long diffInMillies =
        Date.from(Instant.now()).getTime() - Date.from(taskSummary.getCreated()).getTime();
    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    int priority = diffInDays >= 1 ? Math.toIntExact(diffInDays) : 1;

    if (taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_6) == "true") {
      priority *= MULTIPLIER;
    }
    return Optional.of(Integer.valueOf(priority));
  }
}
