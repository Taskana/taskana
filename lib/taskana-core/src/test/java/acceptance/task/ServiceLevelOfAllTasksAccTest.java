package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for planned and prio of all tasks. */
@ExtendWith(JaasExtension.class)
class ServiceLevelOfAllTasksAccTest extends AbstractAccTest {
  TaskService taskService = taskanaEngine.getTaskService();

  @WithAccessId(user = "admin")
  @Test
  void testSetPlannedPropertyOnAllTasks() throws Exception {
    Instant planned = getInstant("2020-05-03T07:00:00");
    List<TaskSummary> allTasks = taskService.createTaskQuery().list();
    // Now update each task with updateTask() and new planned
    final List<TaskSummary> individuallyUpdatedTasks = new ArrayList<>();
    allTasks.forEach(t -> individuallyUpdatedTasks.add(getUpdatedTaskSummary(t, planned)));
    // reset DB and do the same with bulk update
    resetDb(false);
    List<String> taskIds = allTasks.stream().map(TaskSummary::getId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> bulkLog =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    // check that there was no error and compare the result of the 2 operations
    assertThat(bulkLog.containsErrors()).isFalse();
    Map<String, Instant> bulkUpdatedTaskMap =
        taskService.createTaskQuery().list().stream()
            .collect(Collectors.toMap(TaskSummary::getId, TaskSummary::getDue));
    individuallyUpdatedTasks.forEach(
        t -> assertThat(t.getDue().equals(bulkUpdatedTaskMap.get(t.getId()))));
  }

  private TaskSummary getUpdatedTaskSummary(TaskSummary t, Instant planned) {
    try {
      Task task = taskService.getTask(t.getId());
      task.setPlanned(planned);
      return taskService.updateTask(task);
    } catch (Exception e) {
      return null;
    }
  }
}
