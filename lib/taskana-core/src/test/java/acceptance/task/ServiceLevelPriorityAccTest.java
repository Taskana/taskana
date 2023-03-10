package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class ServiceLevelPriorityAccTest extends AbstractAccTest {

  private final ClassificationService classificationService;

  ServiceLevelPriorityAccTest() {
    classificationService = taskanaEngine.getClassificationService();
  }

  /* CREATE TASK */

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CalculatePlannedDateAtCreate() throws Exception {

    // P16D
    Classification classification = classificationService.getClassification("L110105", "DOMAIN_A");
    long serviceLevelDays = Duration.parse(classification.getServiceLevel()).toDays();
    assertThat(serviceLevelDays).isEqualTo(16);

    Task newTask = taskService.newTask("USER-1-1", classification.getDomain());
    newTask.setClassificationKey(classification.getKey());
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    Instant due =
        moveBackToWorkingDay(Instant.now().truncatedTo(ChronoUnit.MILLIS).plus(40, ChronoUnit.DAYS))
            .minusMillis(1);
    newTask.setDue(due);
    Task createdTask = taskService.createTask(newTask);
    assertThat(createdTask.getId()).isNotNull();

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(readTask.getDue()).isEqualTo(due);

    Instant expectedPlanned =
        workingTimeCalculator.subtractWorkingTime(
            due.plusMillis(1), Duration.ofDays(serviceLevelDays));
    assertThat(readTask.getPlanned()).isEqualTo(expectedPlanned);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CalculateDueDateAtCreate() throws Exception {

    // P16D
    Classification classification = classificationService.getClassification("L110105", "DOMAIN_A");
    long serviceLevelDays = Duration.parse(classification.getServiceLevel()).toDays();
    assertThat(serviceLevelDays).isEqualTo(16);

    Task newTask = taskService.newTask("USER-1-1", classification.getDomain());
    newTask.setClassificationKey(classification.getKey());
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    Instant planned = Instant.parse("2023-03-03T10:37:16.456Z");
    newTask.setPlanned(planned);
    Task createdTask = taskService.createTask(newTask);
    assertThat(createdTask.getId()).isNotNull();

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(readTask.getPlanned()).isEqualTo(planned);
    assertThat(readTask.getDue()).isEqualTo(Instant.parse("2023-03-27T10:37:16.455Z"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotThrowException_When_DueAndPlannedAreConsistent() throws Exception {
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setPlanned(Instant.parse("2023-03-01T14:52:13.879Z"));
    newTask.setClassificationKey(classification.getKey());
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("user-1-1");

    newTask.setDue(Instant.parse("2023-03-03T14:52:13.878Z"));
    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatCode(call).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_DueAndPlannedAreInconsistent() {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    Instant planned = moveForwardToWorkingDay(Instant.now().plus(2, ChronoUnit.HOURS));
    newTask.setClassificationKey("T2100"); // P10D
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("user-1-1");

    newTask.setPlanned(planned);
    newTask.setDue(planned); // due date not according to service level
    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_VerifyThatCreateAndPlannedAreClose() throws Exception {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Instant inTwoHours = now.plus(2, ChronoUnit.HOURS);

    assumeThat(now.atZone(ZoneId.systemDefault()).getDayOfYear())
        .describedAs(
            "Today (%s) and in two hours (%s) should be the same day",
            now.atZone(ZoneId.systemDefault()), inTwoHours.atZone(ZoneId.systemDefault()))
        .isEqualTo(inTwoHours.atZone(ZoneId.systemDefault()).getDayOfYear());

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    Instant planned = moveForwardToWorkingDay(inTwoHours);
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("user-1-1");
    newTask.setPlanned(planned);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getPlanned()).isEqualTo(planned);
    assertThat(createdTask.getCreated()).isBefore(createdTask.getPlanned());

    assertThat(createdTask.getPlanned())
        .isCloseTo(
            moveForwardToWorkingDay(createdTask.getCreated()),
            new TemporalUnitWithinOffset(2L, ChronoUnit.HOURS));
  }

  /* UPDATE TASK */

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_DueAndPlannedAreChangedInconsistently() throws Exception {
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

  @WithAccessId(user = "user-b-2")
  @Test
  void should_SetPlanned_When_SetPlannedRequestContainsDuplicateTaskIds() throws Exception {

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

    List<String> taskIds = List.of(tkId1, tkId2, tkId3, tkId4);

    Instant planned = getInstant("2020-02-11T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isFalse();
    Instant dueExpected = getInstant("2020-02-12T06:59:59.999");

    Instant due1 = taskService.getTask(tkId1).getDue();
    assertThat(due1).isEqualTo(dueExpected);
    Instant due2 = taskService.getTask(tkId2).getDue();
    assertThat(due2).isEqualTo(dueExpected);
    Instant due3 = taskService.getTask(tkId3).getDue();
    assertThat(due3).isEqualTo(dueExpected);
    Instant due4 = taskService.getTask(tkId4).getDue();
    assertThat(due4).isEqualTo(dueExpected);
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void should_SetPlanned_When_RequestContainsDuplicatesAndNotExistingTaskIds() throws Exception {

    String tkId1 = "TKI:000000000000000000000000000000000058";
    String tkId2 = "TKI:000000000000000000000000000047110059";
    String tkId3 = "TKI:000000000000000000000000000000000059";
    String tkId4 = "TKI:000000000000000000000000000000000058";
    String tkId5 = "TKI:000000000000000000000000000000000060";
    List<String> taskIds = List.of(tkId1, tkId2, tkId3, tkId4, tkId5);
    Instant planned = getInstant("2020-04-20T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(1);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000047110059"))
        .isInstanceOf(TaskNotFoundException.class);
    Instant dueExpected = getInstant("2020-04-21T06:59:59.999");
    Instant due1 = taskService.getTask(tkId1).getDue();
    assertThat(due1).isEqualTo(dueExpected);
    Instant due3 = taskService.getTask(tkId3).getDue();
    assertThat(due3).isEqualTo(dueExpected);
    Instant due5 = taskService.getTask(tkId5).getDue();
    assertThat(due5).isEqualTo(dueExpected);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetPlanned_When_RequestContainsTasksWithAttachments() throws Exception {

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
    // |TAI:000000000000000000000000000000000001 | CLI:100000000000000000000000000000000002 | P2D  |
    // |TAI:000000000000000000000000000000000002 | CLI:000000000000000000000000000000000003 | P3D  |
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

    List<String> taskIds = List.of(tkId0, tkId1, tkId2);

    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    Instant dueBulk0 = taskService.getTask(tkId0).getDue();
    Instant dueBulk1 = taskService.getTask(tkId1).getDue();
    Instant dueBulk2 = taskService.getTask(tkId2).getDue();

    assertThat(dueBulk0).isEqualTo(getInstant("2020-04-22T12:59:59.999"));
    assertThat(dueBulk1).isEqualTo(getInstant("2020-04-23T12:59:59.999"));
    assertThat(dueBulk2).isEqualTo(getInstant("2020-04-22T12:59:59.999"));

    assertThat(results.containsErrors()).isFalse();
    assertThat(dueBulk0).isEqualTo(due0);
    assertThat(dueBulk1).isEqualTo(due1);
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

  @WithAccessId(user = "user-b-2")
  @Test
  void should_ReturnBulkLog_When_UserIsNotAuthorizedForTasks() {
    String tkId1 = "TKI:000000000000000000000000000000000008";
    String tkId2 = "TKI:000000000000000000000000000000000009";
    String tkId3 = "TKI:000000000000000000000000000000000008";
    String tkId4 = "TKI:000000000000000000000000000000000010";
    List<String> taskIds = List.of(tkId1, tkId2, tkId3, tkId4);
    Instant planned = getInstant("2020-02-25T07:00:00");

    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).hasSize(3).containsAnyElementsOf(taskIds);
    assertThat(results.getErrorMap().values())
        .hasOnlyElementsOfType(NotAuthorizedOnWorkbasketException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetPlannedPropertyOfTasks_When_RequestedByAdminUser() throws Exception {
    String tkId1 = "TKI:000000000000000000000000000000000008";
    String tkId2 = "TKI:000000000000000000000000000000000009";
    String tkId3 = "TKI:000000000000000000000000000000000008";
    String tkId4 = "TKI:000000000000000000000000000000000010"; // all three have P13D

    List<String> taskIds = List.of(tkId1, tkId2, tkId3, tkId4);
    Instant planned = getInstant("2020-05-03T07:00:00");
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, taskIds);
    assertThat(results.containsErrors()).isFalse();

    Instant dueBulk1 = taskService.getTask(tkId1).getDue();
    Instant dueBulk2 = taskService.getTask(tkId2).getDue();
    Instant dueBulk3 = taskService.getTask(tkId3).getDue();
    Instant dueBulk4 = taskService.getTask(tkId4).getDue();
    assertThat(dueBulk1).isEqualTo(getInstant("2020-05-13T23:59:59.999"));
    assertThat(dueBulk2).isEqualTo(getInstant("2020-05-20T23:59:59.999"));
    assertThat(dueBulk3).isEqualTo(getInstant("2020-05-13T23:59:59.999"));
    assertThat(dueBulk4).isEqualTo(getInstant("2020-05-20T23:59:59.999"));
  }

  @WithAccessId(user = "admin")
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
  @WithAccessId(user = "admin")
  @Test
  void should_SetPlannedPropertyWithBulkUpdate_When_RequestContainsASingleTask() throws Exception {
    String taskId = "TKI:000000000000000000000000000000000002";
    Instant planned = getInstant("2020-05-03T07:00:00");
    // test bulk operation setPlanned...
    BulkOperationResults<String, TaskanaException> results =
        taskService.setPlannedPropertyOfTasks(planned, List.of(taskId));
    Task task = taskService.getTask(taskId);
    assertThat(results.containsErrors()).isFalse();
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-04T23:59:59.999"));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetPlannedPropertyOnSingle_When_UpdateTaskWasCalled() throws Exception {
    String taskId = "TKI:000000000000000000000000000000000002";
    Task task = taskService.getTask(taskId);
    // test update of planned date via updateTask()
    task.setPlanned(task.getPlanned().plus(Duration.ofDays(3)));
    task = taskService.updateTask(task);

    assertThat(task.getDue()).isEqualTo(Instant.parse("2018-02-02T15:54:59.999Z"));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetPlanned_When_OnlyDueWasChanged() throws Exception {
    String taskId = "TKI:000000000000000000000000000000000002"; // P1D
    Instant planned = getInstant("2020-05-03T06:59:59.999");
    Task task = taskService.getTask(taskId);

    // test update of due with unchanged planned
    task.setDue(planned.plus(Duration.ofDays(8))); // Monday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-05-08T07:00:00")); // Friday
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetDue_When_OnlyPlannedWasChanged() throws Exception {
    String taskId = "TKI:000000000000000000000000000000000002";
    Instant planned = getInstant("2020-05-06T07:00:00");
    Task task = taskService.getTask(taskId);
    task.setPlanned(planned);
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(planned);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-07T06:59:59.999"));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetPlanned_When_DueIsChangedAndPlannedIsNulled() throws Exception {
    String taskId = "TKI:000000000000000000000000000000000002";
    Instant due = getInstant("2020-05-06T06:59:59.999");
    Task task = taskService.getTask(taskId);
    task.setDue(due);
    task.setPlanned(null);
    task = taskService.updateTask(task);

    assertThat(task.getPlanned()).isEqualTo("2020-05-05T07:00:00.000Z");
    assertThat(task.getDue()).isEqualTo(due);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetDue_When_TaskUpdateIsCalled() throws Exception {
    String taskId = "TKI:000000000000000000000000000000000002";
    final Instant planned = getInstant("2020-05-03T07:00:00"); // Sunday
    Task task = taskService.getTask(taskId);

    task.setPlanned(null);
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-07T06:59:59.999"));

    task.setDue(null);
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-07T06:59:59.999"));

    task.setPlanned(planned.plus(Duration.ofDays(13))); // Saturday
    task.setDue(null);
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-18T23:59:59.999"));

    task.setDue(planned.plus(Duration.ofDays(13))); // Saturday
    task.setPlanned(null);
    task = taskService.updateTask(task);

    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-05-15T00:00:00"));
    assertThat(task.getDue()).isEqualTo(getInstant("2020-05-15T23:59:59.999"));
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateTaskPlannedOrDue_When_PlannedOrDueAreWeekendDays() throws Exception {

    Task task = taskService.getTask("TKI:000000000000000000000000000000000030"); // SL=P13D
    task.setPlanned(getInstant("2020-03-23T07:00:00")); // planned = saturday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-04-09T06:59:59.999"));

    task.setDue(getInstant("2020-04-11T07:00:00")); // due = saturday
    task.setPlanned(null);
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-24T00:00:00"));

    task.setDue(getInstant("2020-04-12T07:00:00")); // due = sunday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-24T00:00:00"));

    task.setPlanned(getInstant("2020-03-21T07:00:00")); // planned = saturday
    task.setDue(getInstant("2020-04-09T23:59:59.999")); // thursday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T00:00:00"));

    task.setPlanned(getInstant("2020-03-04T00:00:00")); // planned on tuesday
    task.setDue(getInstant("2020-03-22T07:00:00")); // due = sunday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-20T23:59:59.999")); // friday, EOB
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateTaskPlannedOrDue_When_PlannedOrDueAreOnWeekends_ServiceLevel_P0D()
      throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000066"); // P0D

    // nothing changed
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2018-01-29T15:55:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2018-01-29T15:55:00")); // Monday

    // planned changed, due did not change
    task.setPlanned(getInstant("2020-03-21T07:00:00")); // Saturday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-23T00:00:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T00:00:00")); // Monday

    // due changed, planned did not change
    task.setDue(getInstant("2020-04-12T07:00:00")); // Sunday
    task = taskService.updateTask(task);
    Instant endOfHolyThursday = getInstant("2020-04-09T23:59:59.999");
    assertThat(task.getPlanned()).isEqualTo(endOfHolyThursday); // Thursday (skip Good Friday)
    assertThat(task.getDue()).isEqualTo(endOfHolyThursday);

    // due changed, planned is null
    task.setDue(getInstant("2020-04-11T07:00:00")); // Saturday
    task.setPlanned(null);
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(endOfHolyThursday); // Thursday (skip Good Friday)
    assertThat(task.getDue()).isEqualTo(endOfHolyThursday);

    // planned changed, due is null
    task.setPlanned(getInstant("2020-03-22T07:00:00")); // Sunday
    task.setDue(null);
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-23T00:00:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T00:00:00")); // Monday

    // both changed, not null (due at weekend)
    Instant fridayEndOfBusinessDay = getInstant("2020-03-20T23:59:59.999");
    task.setPlanned(fridayEndOfBusinessDay);
    task.setDue(getInstant("2020-03-22T07:00:00")); // Sunday
    task = taskService.updateTask(task);
    assertThat(task.getPlanned()).isEqualTo(fridayEndOfBusinessDay);
    assertThat(task.getDue()).isEqualTo(fridayEndOfBusinessDay);

    // both changed, not null (planned at weekend)
    task.setPlanned(getInstant("2020-03-22T07:00:00")); // Sunday
    task.setDue(getInstant("2020-03-23T00:00:00")); // Monday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-03-23T00:00:00")); // Monday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-03-23T00:00:00")); // Monday

    // both changed, not null (both at weekend) within SLA
    task.setPlanned(getInstant("2020-03-22T07:00:00")); // Sunday
    task.setDue(getInstant("2020-03-22T07:00:00")); // Sunday
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(fridayEndOfBusinessDay);
    assertThat(task.getPlanned()).isEqualTo(fridayEndOfBusinessDay);

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

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotThrowServiceLevelViolation_IfWeekendOrHolidaysBetweenDates() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000002"); // P1D

    // SLA is broken but only with holidays in between
    task.setDue(getInstant("2020-04-14T06:59:59.999")); // Tuesday after Easter
    task.setPlanned(getInstant("2020-04-09T07:00:00")); // Thursday before Easter
    task = taskService.updateTask(task);
    assertThat(task.getDue()).isEqualTo(getInstant("2020-04-14T06:59:59.999")); // Tuesday
    assertThat(task.getPlanned()).isEqualTo(getInstant("2020-04-09T07:00:00")); // Thursday
  }
}
