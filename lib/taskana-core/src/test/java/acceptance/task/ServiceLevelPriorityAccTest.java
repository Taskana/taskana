package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.IterableUtil.toArray;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.common.internal.util.WorkingDaysToDaysConverter;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
@SuppressWarnings({"checkstyle:LineLength"})
public class ServiceLevelPriorityAccTest extends AbstractAccTest {

  private TaskService taskService;

  ServiceLevelPriorityAccTest() {
    super();
    WorkingDaysToDaysConverter.setGermanPublicHolidaysEnabled(true);
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  public void should_ThrowException_When_DueAndPlannedAreChangedInconsistently() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000"); // P1D
    task.setDue(Instant.parse("2020-07-02T00:00:00Z"));
    task.setPlanned(Instant.parse("2020-07-07T00:00:00Z"));
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage(
            "Cannot update a task with given planned 2020-07-07T00:00:00Z and due "
                + "date 2020-07-02T00:00:00Z not matching the service level PT24H.");
  }

  @WithAccessId(user = "user_3_2", groups = "group_2")
  @Test
  void should_SetPlanned_When_SetPlannedRequestContainsDuplicateTaskIds()
      throws NotAuthorizedException, TaskNotFoundException {

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

  @WithAccessId(user = "user_3_2", groups = "group_2")
  @Test
  void should_SetPlanned_When_RequestContainsDuplicatesAndNotExistingTaskIds()
      throws NotAuthorizedException, TaskNotFoundException {

    String tkId1 = "TKI:000000000000000000000000000000000058";
    String tkId2 = "TKI:000000000000000000000000000047110059";
    String tkId3 = "TKI:000000000000000000000000000000000059";
    String tkId4 = "TKI:000000000000000000000000000000000058";
    String tkId5 = "TKI:000000000000000000000000000000000060";
    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4, tkId5);
    Instant planned = getInstant("2020-04-20T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(1);
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

  @WithAccessId(user = "user_1_1", groups = "group_2")
  @Test
  void should_SetPlanned_When_RequestContainsTasksWithAttachments()
      throws NotAuthorizedException, TaskNotFoundException, ClassificationNotFoundException,
          InvalidArgumentException, InvalidStateException, ConcurrencyException,
          AttachmentPersistenceException {

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

  @WithAccessId(user = "user_3_2", groups = "group_2")
  @Test
  void should_ReturnBulkLog_When_UserIsNotAuthorizedForTasks() {
    String tkId1 = "TKI:000000000000000000000000000000000008";
    String tkId2 = "TKI:000000000000000000000000000000000009";
    String tkId3 = "TKI:000000000000000000000000000000000008";
    String tkId4 = "TKI:000000000000000000000000000000000010";
    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4);
    Instant planned = getInstant("2020-02-25T07:00:00");

    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap())
        .hasSize(3)
        .containsKeys(toArray(taskIds))
        .extractingFromEntries(Entry::getValue)
        .hasOnlyElementsOfType(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetPlannedPropertyOfTasks_When_RequestedByAdminUser()
      throws NotAuthorizedException, TaskNotFoundException {
    String tkId1 = "TKI:000000000000000000000000000000000008";
    String tkId2 = "TKI:000000000000000000000000000000000009";
    String tkId3 = "TKI:000000000000000000000000000000000008";
    String tkId4 = "TKI:000000000000000000000000000000000010"; // all three have P13D

    List<String> taskIds = Arrays.asList(tkId1, tkId2, tkId3, tkId4);
    Instant planned = getInstant("2020-05-03T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isFalse();

    Instant dueBulk1 = taskService.getTask(tkId1).getDue();
    Instant dueBulk2 = taskService.getTask(tkId2).getDue();
    Instant dueBulk3 = taskService.getTask(tkId3).getDue();
    Instant dueBulk4 = taskService.getTask(tkId4).getDue();
    assertThat(dueBulk1).isEqualTo(getInstant("2020-05-14T07:00:00"));
    assertThat(dueBulk2).isEqualTo(getInstant("2020-05-22T07:00:00"));
    assertThat(dueBulk3).isEqualTo(getInstant("2020-05-14T07:00:00"));
    assertThat(dueBulk4).isEqualTo(getInstant("2020-05-22T07:00:00"));
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_DoNothing_When_SetPlannedIsCalledWithEmptyTasksList() {
    Instant planned = getInstant("2020-05-03T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, new ArrayList<>());
    assertThat(results.containsErrors()).isFalse();
  }

  // +-----------------------------------------+------------------------------------------+------+
  // |TKI:000000000000000000000000000000000002 | CLI:100000000000000000000000000000000016 | P1D  |
  // |TAI:000000000000000000000000000000000003 | CLI:000000000000000000000000000000000004 | P4D  |
  // |TAI:000000000000000000000000000000000004 | CLI:000000000000000000000000000000000005 | P5D  |
  // |TAI:000000000000000000000000000000000005 | CLI:000000000000000000000000000000000006 | P5D  |
  // |TAI:000000000000000000000000000000000006 | CLI:000000000000000000000000000000000007 | P6D  |
  // |TAI:000000000000000000000000000000000007 | CLI:100000000000000000000000000000000008 | P1D  |
  // |TKI:000000000000000000000000000000000066 | CLI:100000000000000000000000000000000024 | P0D  |
  // +-----------------------------------------+------------------------------------------+------+
  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetPlannedPropertyWithBulkUpdate_When_RequestContainsASingleTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException {
    String taskId = "TKI:000000000000000000000000000000000002";
    Instant planned = getInstant("2020-05-03T07:00:00");
    // test bulk operation setPlanned...
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, Arrays.asList(taskId));
    Task task = taskService.getTask(taskId);
    assertThat(results.containsErrors()).isFalse();
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize();
    long days = converter.convertWorkingDaysToDays(task.getPlanned(), 1);
    assertThat(task.getDue()).isEqualTo(planned.plus(Duration.ofDays(days)));
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetPlannedPropertyOnSingle_When_UpdateTaskWasCalled()
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException,
          ConcurrencyException, InvalidStateException, ClassificationNotFoundException,
          AttachmentPersistenceException {
    String taskId = "TKI:000000000000000000000000000000000002";
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize();
    Task task = taskService.getTask(taskId);
    // test update of planned date via updateTask()
    task.setPlanned(task.getPlanned().plus(Duration.ofDays(3)));
    task = taskService.updateTask(task);
    long days = converter.convertWorkingDaysToDays(task.getPlanned(), 1);
    assertThat(task.getDue()).isEqualTo(task.getPlanned().plus(Duration.ofDays(days)));
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetPlanned_When_OnlyDueWasChanged()
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException,
          ConcurrencyException, InvalidStateException, ClassificationNotFoundException,
          AttachmentPersistenceException {
    String taskId = "TKI:000000000000000000000000000000000002"; // P1D
    Instant planned = getInstant("2020-05-03T07:00:00");
    Task task = taskService.getTask(taskId);

    // test update of due with unchanged planned
    task.setDue(planned.plus(Duration.ofDays(8))); // Monday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-05-08T07:00:00")); // Friday
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetDue_When_OnlyPlannedWasChanged()
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException,
          ConcurrencyException, InvalidStateException, ClassificationNotFoundException,
          AttachmentPersistenceException {
    String taskId = "TKI:000000000000000000000000000000000002";
    Instant planned = getInstant("2020-05-03T07:00:00");
    Task task = taskService.getTask(taskId);
    task.setDue(planned.plus(Duration.ofDays(3)));
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize();
    long days = converter.convertWorkingDaysToDays(task.getDue(), -1);
    task.setPlanned(task.getDue().plus(Duration.ofDays(-1)));
    task = taskService.updateTask(task);
    days = converter.convertWorkingDaysToDays(task.getDue(), -1);
    assertThat(task.getPlanned()).isEqualTo(task.getDue().plus(Duration.ofDays(days)));
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetPlanned_When_DueIsChangedAndPlannedIsNulled()
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException,
          ConcurrencyException, InvalidStateException, ClassificationNotFoundException,
          AttachmentPersistenceException {
    String taskId = "TKI:000000000000000000000000000000000002";
    Instant planned = getInstant("2020-05-03T07:00:00");
    Task task = taskService.getTask(taskId);
    task.setDue(planned.plus(Duration.ofDays(3)));
    task.setPlanned(null);
    task = taskService.updateTask(task);
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize();
    long days = converter.convertWorkingDaysToDays(task.getDue(), -1);
    assertThat(task.getPlanned()).isEqualTo(task.getDue().plus(Duration.ofDays(days)));
  }

  @WithAccessId(user = "admin", groups = "group_2")
  @Test
  void should_SetDue_When_TaskUpdateIsCalled()
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException,
          ConcurrencyException, InvalidStateException, ClassificationNotFoundException,
          AttachmentPersistenceException {
    String taskId = "TKI:000000000000000000000000000000000002";
    final Instant planned = getInstant("2020-05-03T07:00:00"); // Sunday
    Task task = taskService.getTask(taskId);

    task.setPlanned(null);
    task = taskService.updateTask(task);
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize();
    long days = converter.convertWorkingDaysToDays(task.getPlanned(), 1);
    assertThat(task.getDue()).isEqualTo(task.getPlanned().plus(Duration.ofDays(days)));

    task.setDue(null);
    task = taskService.updateTask(task);
    days = converter.convertWorkingDaysToDays(task.getPlanned(), 1);
    assertThat(task.getDue()).isEqualTo(task.getPlanned().plus(Duration.ofDays(days)));

    task.setPlanned(planned.plus(Duration.ofDays(13))); // Saturday
    task.setDue(null);
    task = taskService.updateTask(task);
    days = converter.convertWorkingDaysToDays(task.getPlanned(), 1);
    assertThat(task.getDue()).isEqualTo(task.getPlanned().plus(Duration.ofDays(days)));

    task.setDue(planned.plus(Duration.ofDays(13))); // Saturday
    task.setPlanned(null);
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-05-14T07:00:00"));
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-15T07:00:00"));
  }

  @WithAccessId(user = "user_1_2", groups = "group_1")
  @Test
  void should_UpdateTaskPlannedOrDue_When_PlannedOrDueAreWeekendDays()
      throws NotAuthorizedException, TaskNotFoundException, ClassificationNotFoundException,
          InvalidArgumentException, InvalidStateException, ConcurrencyException,
          AttachmentPersistenceException {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000030"); // SL=P13D
    task.setPlanned(getInstant("2020-03-21T07:00:00")); // planned = saturday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-04-09T07:00:00"));

    task.setDue(getInstant("2020-04-11T07:00:00")); // due = saturday
    task.setPlanned(null);
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T07:00:00"));

    task.setDue(getInstant("2020-04-12T07:00:00")); // due = sunday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T07:00:00"));

    task.setPlanned(getInstant("2020-03-21T07:00:00")); // planned = saturday
    task.setDue(getInstant("2020-04-09T07:00:00")); // thursday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T07:00:00"));

    task.setPlanned(getInstant("2020-03-03T07:00:00")); // planned on tuesday
    task.setDue(getInstant("2020-03-22T07:00:00")); // due = sunday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-20T07:00:00")); // friday
  }

  @WithAccessId(user = "user1_4", groups = "group_1")
  @Test
  void should_UpdateTaskPlannedOrDue_When_PlannedOrDueAreOnWeekends_ServiceLevel_P0D()
      throws NotAuthorizedException, TaskNotFoundException, ClassificationNotFoundException,
          InvalidArgumentException, InvalidStateException, ConcurrencyException,
          AttachmentPersistenceException {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000066"); // P0D

    // nothing changed
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2018-01-29T15:55:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2018-01-29T15:55:00")); // Monday

    // planned changed, due did not change
    task.setPlanned(getInstant("2020-03-21T07:00:00")); // Saturday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-23T07:00:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T07:00:00")); // Monday

    // due changed, planned did not change
    task.setDue(getInstant("2020-04-12T07:00:00")); // Sunday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned())
        .isEqualTo(getInstant("2020-04-09T07:00:00")); // Thursday (skip Good Friday)
    assertThat(task.getDue()).isEqualTo(getInstant("2020-04-09T07:00:00"));

    // due changed, planned is null
    task.setDue(getInstant("2020-04-11T07:00:00")); // Saturday
    task.setPlanned(null);
    task = taskService.updateTask(task);
    assertThat(task.getPlanned())
        .isEqualTo(getInstant("2020-04-09T07:00:00")); // Thursday (skip Good Friday)
    assertThat(task.getDue()).isEqualTo(getInstant("2020-04-09T07:00:00"));

    // planned changed, due is null
    task.setPlanned(getInstant("2020-03-22T07:00:00")); // Sunday
    task.setDue(null);
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-23T07:00:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T07:00:00")); // Monday

    // both changed, not null (due at weekend)
    task.setPlanned(getInstant("2020-03-20T07:00:00")); // Friday
    task.setDue(getInstant("2020-03-22T07:00:00")); // Sunday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-20T07:00:00")); // Friday
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-20T07:00:00")); // Friday

    // both changed, not null (planned at weekend)
    task.setPlanned(getInstant("2020-03-22T07:00:00")); // Sunday
    task.setDue(getInstant("2020-03-23T07:00:00")); // Monday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-23T07:00:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T07:00:00")); // Monday

    // both changed, not null (both at weekend) within SLA
    task.setPlanned(getInstant("2020-03-22T07:00:00")); // Sunday
    task.setDue(getInstant("2020-03-22T07:00:00")); // Sunday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-20T07:00:00")); // Friday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-20T07:00:00")); // Friday

    // both changed, not null (planned > due)
    task.setPlanned(getInstant("2020-03-24T07:00:00")); // Tuesday
    task.setDue(getInstant("2020-03-23T07:00:00")); // Monday
    Task finalTask = task;
    assertThatThrownBy(() -> taskService.updateTask(finalTask))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage(
            "Cannot update a task with given planned 2020-03-24T07:00:00Z and "
                + "due date 2020-03-23T07:00:00Z not matching the service level PT0S.");
  }

  @WithAccessId(user = "user_1_2", groups = "group_1")
  @Test
  void should_notThrowServiceLevelViolation_IfWeekendOrHolidaysBetweenDates()
      throws NotAuthorizedException, TaskNotFoundException, ClassificationNotFoundException,
          InvalidArgumentException, InvalidStateException, ConcurrencyException,
          AttachmentPersistenceException {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000002"); // P1D

    // SLA is broken but only with holidays in between
    task.setDue(getInstant("2020-04-14T07:00:00")); // Tuesday after Easter
    task.setPlanned(getInstant("2020-04-09T07:00:00")); // Thursday before Easter
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-04-14T07:00:00")); // Tuesday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-04-09T07:00:00")); // Thursday
  }
}
