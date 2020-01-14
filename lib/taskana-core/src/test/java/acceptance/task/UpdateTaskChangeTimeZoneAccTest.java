package acceptance.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import acceptance.AbstractAccTest;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for access to timestamps from different timezones. */
@ExtendWith(JaasExtension.class)
public class UpdateTaskChangeTimeZoneAccTest extends AbstractAccTest {

  UpdateTaskChangeTimeZoneAccTest() {
    super();
  }
  
  
  
  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testDatesOnTaskUpdate()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setCustomAttribute("1", "T2100");
    TimeZone originalZone = TimeZone.getDefault();
    Task updatedTask = taskService.updateTask(task);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Task retrievedTask = taskService.getTask(updatedTask.getId());
    TimeZone.setDefault(originalZone);
    assertEquals(updatedTask,retrievedTask);
  }
}
