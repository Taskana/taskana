package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
@SuppressWarnings({"checkstyle:LineLength"})
public class SetPlannedAccTest extends AbstractAccTest {
  private TaskService taskService;
  private ClassificationService classificationService;

  @BeforeEach
  void setup() throws SQLException {
    taskService = taskanaEngine.getTaskService();
    classificationService = taskanaEngine.getClassificationService();
    resetDb(false);
  }

  @WithAccessId(
      userName = "user_3_2",
      groupNames = {"group_2"})
  @Test
  void testSetPlannedWithDuplicatesSucceeds()
      throws NotAuthorizedException, TaskNotFoundException, ClassificationNotFoundException,
          InvalidArgumentException, InvalidStateException, ConcurrencyException,
          AttachmentPersistenceException {

    // This test works with the following tasks (w/o attachments) and classifications
    //
    // +-----------------------------------------+------------------------------------------+------+
    // |   TaskId                                |  ClassificationId                        | SL   |
    // +-----------------------------------------+------------------------------------------+------+
    // |TKI:000000000000000000000000000000000058 | CLI:200000000000000000000000000000000017 | P1D  |
    // |TKI:000000000000000000000000000000000059 | CLI:200000000000000000000000000000000017 | P1D  |
    // |TKI:000000000000000000000000000000000060 | CLI:200000000000000000000000000000000017 | P1D  |
    // +-----------------------------------------+------------------------------------------+------+
    String tkId1 = "TKI:000000000000000000000000000000000058";
    String tkId2 = "TKI:000000000000000000000000000000000059";
    String tkId3 = "TKI:000000000000000000000000000000000058";
    String tkId4 = "TKI:000000000000000000000000000000000060";

    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4);
    TaskService taskService = taskanaEngine.getTaskService();

    Instant planned = getInstant("2020-02-11T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isFalse();
    Instant dueExpected = getInstant("2020-02-12T07:00:00");

    Instant due1 = taskService.getTask(tkId1).getDue();
    assertThat(due1).isEqualTo(dueExpected);
    Instant due2 = taskService.getTask(tkId2).getDue();
    assertThat(due2).isEqualTo(dueExpected);
    Instant due3 = taskService.getTask(tkId3).getDue();
    assertThat(due3).isEqualTo(dueExpected);
    Instant due4 = taskService.getTask(tkId4).getDue();
    assertThat(due4).isEqualTo(dueExpected);
  }

  @WithAccessId(
      userName = "user_3_2",
      groupNames = {"group_2"})
  @Test
  void testSetPlannedOnTasksWithDuplicatesAndNotExistingSucceeds()
      throws NotAuthorizedException, TaskNotFoundException {

    String tkId1 = "TKI:000000000000000000000000000000000058";
    String tkId2 = "TKI:000000000000000000000000000047110059";
    String tkId3 = "TKI:000000000000000000000000000000000059";
    String tkId4 = "TKI:000000000000000000000000000000000058";
    String tkId5 = "TKI:000000000000000000000000000000000060";
    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4, tkId5);
    Instant planned = getInstant("2020-04-20T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().size()).isEqualTo(1);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000047110059"))
        .isInstanceOf(TaskNotFoundException.class);
    Instant dueExpected = getInstant("2020-04-21T07:00:00");
    Instant due1 = taskService.getTask(tkId1).getDue();
    assertThat(due1).isEqualTo(dueExpected);
    Instant due3 = taskService.getTask(tkId3).getDue();
    assertThat(due3).isEqualTo(dueExpected);
    Instant due5 = taskService.getTask(tkId5).getDue();
    assertThat(due5).isEqualTo(dueExpected);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_2"})
  @Test
  void testSetPlannedForTasksWithAttachmentsSucceeds()
      throws NotAuthorizedException, TaskNotFoundException, ClassificationNotFoundException,
          InvalidArgumentException, InvalidStateException, ConcurrencyException,
          AttachmentPersistenceException, SQLException {

    // This test works with the following tasks, attachments and classifications
    //
    // +-----------------------------------------+------------------------------------------+------+
    // |   Task  / associated attachment         |  Classification                          | SL   |
    // +-----------------------------------------+------------------------------------------+------+
    // |TKI:000000000000000000000000000000000000 | CLI:100000000000000000000000000000000016 | P1D  |
    // |TAI:000000000000000000000000000000000000 | CLI:100000000000000000000000000000000003 | P13D |
    // |TAI:000000000000000000000000000000000009 | CLI:100000000000000000000000000000000003 | P13D |
    // +-----------------------------------------+------------------------------------------+------+
    // |TKI:000000000000000000000000000000000001 | CLI:100000000000000000000000000000000005 | P15D |
    // |TAI:000000000000000000000000000000000001 | CLI:000000000000000000000000000000000002 | P2D  |
    // |TAI:000000000000000000000000000000000002 | CLI:000000000000000000000000000000000003 | P3d  |
    // +-----------------------------------------+------------------------------------------+------+
    // |TKI:000000000000000000000000000000000002 | CLI:100000000000000000000000000000000016 | P1D  |
    // |TAI:000000000000000000000000000000000003 | CLI:000000000000000000000000000000000004 | P4D  |
    // |TAI:000000000000000000000000000000000004 | CLI:000000000000000000000000000000000005 | P5D  |
    // |TAI:000000000000000000000000000000000005 | CLI:000000000000000000000000000000000006 | P5D  |
    // |TAI:000000000000000000000000000000000006 | CLI:000000000000000000000000000000000007 | P6D  |
    // |TAI:000000000000000000000000000000000007 | CLI:100000000000000000000000000000000008 | P1D  |
    // +-----------------------------------------+------------------------------------------+------+

    TaskService taskService = taskanaEngine.getTaskService();
    String tkId0 = "TKI:000000000000000000000000000000000000";
    String tkId1 = "TKI:000000000000000000000000000000000001";
    String tkId2 = "TKI:000000000000000000000000000000000002";

    // get due dates by updating the tasks individually
    Task task0 = taskService.getTask(tkId0);
    Task task1 = taskService.getTask(tkId1);
    Task task2 = taskService.getTask(tkId2);

    Instant planned = getInstant("2020-04-21T13:00:00");
    task0.setPlanned(planned);
    task1.setPlanned(planned);
    task2.setPlanned(planned);

    final Instant due0 = taskService.updateTask(task0).getDue();
    final Instant due1 = taskService.updateTask(task1).getDue();
    final Instant due2 = taskService.updateTask(task2).getDue();

    // now check that bulk update gives the same due dates

    List<String> taskIds = Arrays.asList(tkId0, tkId1, tkId2);

    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    Instant dueBulk0 = taskService.getTask(tkId0).getDue();
    Instant dueBulk1 = taskService.getTask(tkId1).getDue();
    Instant dueBulk2 = taskService.getTask(tkId2).getDue();

    assertThat(dueBulk0).isEqualTo(planned.plus(1, ChronoUnit.DAYS));
    assertThat(dueBulk1).isEqualTo(planned.plus(2, ChronoUnit.DAYS));
    assertThat(dueBulk2).isEqualTo(planned.plus(1, ChronoUnit.DAYS));
    long delta = Duration.between(planned, due1).toDays();

    assertThat(results.containsErrors()).isFalse();
    assertThat(dueBulk0).isEqualTo(due0);
    //    assertThat(dueBulk1).isEqualTo(due1); in this method, a bug in the code is visible
    assertThat(dueBulk2).isEqualTo(due2);
  }

  // the following tests use these tasks, attachments and classifications
  // +-----------------------------------------+------------------------------------------+------+
  // |   Task  / associated attachment         |  Classification                          | SL   |
  // +-----------------------------------------+------------------------------------------+------+
  // |TKI:000000000000000000000000000000000008 | CLI:100000000000000000000000000000000003 | P13D |
  // |TAI:000000000000000000000000000000000008 | CLI:000000000000000000000000000000000009 | P8D  |
  // +---------------------------------------- + -----------------------------------------+ -----+
  // |TKI:000000000000000000000000000000000009 | CLI:100000000000000000000000000000000003 | P13D |
  // +---------------------------------------- + -----------------------------------------+ -----+
  // |TKI:000000000000000000000000000000000010 | CLI:100000000000000000000000000000000003 | P13D |
  // |TAI:000000000000000000000000000000000014 | CLI:100000000000000000000000000000000004,| P14D |
  // +-----------------------------------------+------------------------------------------+------+

  @WithAccessId(
      userName = "user_3_2",
      groupNames = {"group_2"})
  @Test
  void testSetPlannedPropertyOfTasksWithNoQualifyingTasks() {
    String tkId1 = "TKI:000000000000000000000000000000000008";
    String tkId2 = "TKI:000000000000000000000000000000000009";
    String tkId3 = "TKI:000000000000000000000000000000000008";
    String tkId4 = "TKI:000000000000000000000000000000000010";

    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4);
    Instant planned = getInstant("2020-02-25T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().size()).isEqualTo(3);
    assertThat(results.getErrorForId(tkId1)).isInstanceOf(NotAuthorizedException.class);
    assertThat(results.getErrorForId(tkId2)).isInstanceOf(NotAuthorizedException.class);
    assertThat(results.getErrorForId(tkId3)).isInstanceOf(NotAuthorizedException.class);
    assertThat(results.getErrorForId(tkId4)).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_2"})
  @Test
  void testSetPlannedPropertyOfTasksWithAdminUser()
      throws NotAuthorizedException, TaskNotFoundException {
    String tkId1 = "TKI:000000000000000000000000000000000008";
    String tkId2 = "TKI:000000000000000000000000000000000009";
    String tkId3 = "TKI:000000000000000000000000000000000008";
    String tkId4 = "TKI:000000000000000000000000000000000010";

    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4);
    Instant planned = getInstant("2020-05-03T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isFalse();

    Instant dueBulk1 = taskService.getTask(tkId1).getDue();
    Instant dueBulk2 = taskService.getTask(tkId2).getDue();
    Instant dueBulk3 = taskService.getTask(tkId3).getDue();
    Instant dueBulk4 = taskService.getTask(tkId4).getDue();
    assertThat(dueBulk1).isEqualTo(getInstant("2020-05-14T07:00:00"));
    assertThat(dueBulk2).isEqualTo(getInstant("2020-05-21T07:00:00"));
    assertThat(dueBulk3).isEqualTo(getInstant("2020-05-14T07:00:00"));
    assertThat(dueBulk4).isEqualTo(getInstant("2020-05-21T07:00:00"));
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_2"})
  @Test
  void testSetPlannedPropertyOnEmptyTasksList()
      throws NotAuthorizedException, TaskNotFoundException {
    Instant planned = getInstant("2020-05-03T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setPlannedPropertyOfTasks(planned, new ArrayList<>());
    assertThat(results.containsErrors()).isFalse();
  }
}
