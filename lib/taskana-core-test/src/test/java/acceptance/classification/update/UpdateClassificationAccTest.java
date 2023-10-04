package acceptance.classification.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static pro.taskana.testapi.builder.TaskBuilder.newTask;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "update classification" scenarios. */
@TaskanaIntegrationTest
class UpdateClassificationAccTest {
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject WorkingTimeCalculator workingTimeCalculator;
  @TaskanaInject CurrentUserContext currentUserContext;

  @WithAccessId(user = "businessadmin")
  @Test
  void should_SetFieldsCorrectly_When_TryingToUpdateClassification() throws Exception {
    Classification parentClassification =
        defaultTestClassification().buildAndStore(classificationService);
    Classification classification =
        defaultTestClassification().type("TASK").buildAndStore(classificationService);
    final Instant createdBefore = classification.getCreated();
    final Instant modifiedBefore = classification.getModified();

    classification.setApplicationEntryPoint("newEntrypoint");
    classification.setCategory("PROCESS");
    classification.setCustomField(ClassificationCustomField.CUSTOM_1, "newCustom1");
    classification.setCustomField(ClassificationCustomField.CUSTOM_2, "newCustom2");
    classification.setCustomField(ClassificationCustomField.CUSTOM_3, "newCustom3");
    classification.setCustomField(ClassificationCustomField.CUSTOM_4, "newCustom4");
    classification.setCustomField(ClassificationCustomField.CUSTOM_5, "newCustom5");
    classification.setCustomField(ClassificationCustomField.CUSTOM_6, "newCustom6");
    classification.setCustomField(ClassificationCustomField.CUSTOM_7, "newCustom7");
    classification.setCustomField(ClassificationCustomField.CUSTOM_8, "newCustom8");
    classification.setDescription("newDescription");
    classification.setIsValidInDomain(false);
    classification.setName("newName");
    classification.setParentId(parentClassification.getId());
    classification.setParentKey(parentClassification.getKey());
    classification.setPriority(1000);
    classification.setServiceLevel("P3D");
    classificationService.updateClassification(classification);

    Classification updatedClassification =
        classificationService.getClassification(classification.getKey(), "DOMAIN_A");
    ClassificationImpl expectedClassification =
        (ClassificationImpl)
            defaultTestClassification()
                .type("TASK")
                .applicationEntryPoint("newEntrypoint")
                .category("PROCESS")
                .customAttribute(ClassificationCustomField.CUSTOM_1, "newCustom1")
                .customAttribute(ClassificationCustomField.CUSTOM_2, "newCustom2")
                .customAttribute(ClassificationCustomField.CUSTOM_3, "newCustom3")
                .customAttribute(ClassificationCustomField.CUSTOM_4, "newCustom4")
                .customAttribute(ClassificationCustomField.CUSTOM_5, "newCustom5")
                .customAttribute(ClassificationCustomField.CUSTOM_6, "newCustom6")
                .customAttribute(ClassificationCustomField.CUSTOM_7, "newCustom7")
                .customAttribute(ClassificationCustomField.CUSTOM_8, "newCustom8")
                .description("newDescription")
                .isValidInDomain(false)
                .name("newName")
                .parentId(parentClassification.getId())
                .parentKey(parentClassification.getKey())
                .priority(1000)
                .serviceLevel("P3D")
                .created(createdBefore)
                .modified(updatedClassification.getModified())
                .buildAndStore(classificationService);
    expectedClassification.setKey(updatedClassification.getKey());
    expectedClassification.setId(updatedClassification.getId());

    assertThat(expectedClassification).hasNoNullFieldsOrProperties();
    assertThat(modifiedBefore).isBefore(classification.getModified());
    assertThat(updatedClassification).isEqualTo(expectedClassification);
  }

  private String createTaskWithExistingClassification(ClassificationSummary classificationSummary)
      throws Exception {
    WorkbasketSummary workbasketSummary =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary.getId())
        .accessId(currentUserContext.getUserid())
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "businessadmin");

    return newTask()
        .classificationSummary(classificationSummary)
        .workbasketSummary(workbasketSummary)
        .primaryObjRef(defaultTestObjectReference().build())
        .buildAndStore(taskService)
        .getId();
  }

  private List<String> createTasksWithExistingClassificationInAttachment(
      ClassificationSummary classificationSummary, String serviceLevel, int priority, int amount)
      throws Exception {
    List<String> taskList = new ArrayList<>();
    WorkbasketSummary workbasketSummary =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary.getId())
        .accessId(currentUserContext.getUserid())
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "businessadmin");
    ClassificationSummary classificationSummaryWithSpecifiedServiceLevel =
        defaultTestClassification()
            .serviceLevel(serviceLevel)
            .priority(priority)
            .buildAndStoreAsSummary(classificationService);
    for (int i = 0; i < amount; i++) {
      Attachment attachment = taskService.newAttachment();
      attachment.setClassificationSummary(classificationSummary);
      attachment.setObjectReference(defaultTestObjectReference().build());
      taskList.add(
          newTask()
              .classificationSummary(classificationSummaryWithSpecifiedServiceLevel)
              .workbasketSummary(workbasketSummary)
              .primaryObjRef(defaultTestObjectReference().build())
              .attachments(attachment)
              .buildAndStore(taskService)
              .getId());
    }
    return taskList;
  }

  @TestInstance(Lifecycle.PER_CLASS)
  @Nested
  class UpdatePriorityAndServiceLevelTest {

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ChangeDueDate_When_ServiceLevelOfClassificationHasChanged() throws Exception {
      Classification classification =
          defaultTestClassification()
              .priority(1)
              .serviceLevel("P1D")
              .buildAndStore(classificationService);
      WorkbasketSummary workbasketSummary =
          defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
          .workbasketId(workbasketSummary.getId())
          .accessId(currentUserContext.getUserid())
          .permission(WorkbasketPermission.OPEN)
          .permission(WorkbasketPermission.READ)
          .permission(WorkbasketPermission.READTASKS)
          .permission(WorkbasketPermission.APPEND)
          .buildAndStore(workbasketService, "businessadmin");

      Task task =
          new TaskBuilder()
              .classificationSummary(classification.asSummary())
              .workbasketSummary(workbasketSummary)
              .primaryObjRef(defaultTestObjectReference().build())
              .planned(Instant.parse("2021-04-27T15:34:00.000Z"))
              .due(null)
              .buildAndStore(taskService);

      classificationService.updateClassification(classification);
      runAssociatedJobs();
      // read again the task from DB
      task = taskService.getTask(task.getId());
      assertThat(task.getClassificationSummary().getServiceLevel()).isEqualTo("P1D");
      assertThat(task.getDue()).isAfterOrEqualTo("2021-04-28T15:33:59.999Z");

      classification.setServiceLevel("P3D");
      classificationService.updateClassification(classification);
      runAssociatedJobs();

      // read again the task from DB
      task = taskService.getTask(task.getId());
      assertThat(task.getClassificationSummary().getServiceLevel()).isEqualTo("P3D");
      assertThat(task.getDue()).isEqualTo("2021-04-30T15:33:59.999Z");
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_NotThrowException_When_UpdatingClassificationWithEmptyServiceLevel()
        throws Exception {
      Classification classification =
          defaultTestClassification().serviceLevel("P1D").buildAndStore(classificationService);
      classification.setServiceLevel("");
      assertThatCode(() -> classificationService.updateClassification(classification))
          .doesNotThrowAnyException();
      assertThat(classificationService.getClassification(classification.getId()).getServiceLevel())
          .isEqualTo("P0D");
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest>
        should_SetDefaultServiceLevel_When_TryingToUpdateClassificationWithMissingServiceLevel()
            throws Exception {
      Classification classification =
          defaultTestClassification().serviceLevel("P1D").buildAndStore(classificationService);
      List<Pair<Classification, String>> inputList =
          List.of(Pair.of(classification, null), Pair.of(classification, ""));

      ThrowingConsumer<Pair<Classification, String>> test =
          input -> {
            input.getLeft().setServiceLevel(input.getRight());
            classificationService.updateClassification(input.getLeft());
            assertThat(
                    classificationService
                        .getClassification(input.getLeft().getId())
                        .getServiceLevel())
                .isEqualTo("P0D");
          };

      return DynamicTest.stream(
          inputList.iterator(), i -> String.format("for %s", i.getRight()), test);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateTaskServiceLevel_When_UpdateClassificationInTask() throws Exception {
      final Instant before = Instant.now();
      Classification classification =
          defaultTestClassification()
              .priority(1)
              .serviceLevel("P13D")
              .buildAndStore(classificationService);
      final List<String> directLinkedTask =
          List.of(createTaskWithExistingClassification(classification.asSummary()));

      classification.setServiceLevel("P15D");
      classificationService.updateClassification(classification);
      runAssociatedJobs();

      validateTaskProperties(before, directLinkedTask, taskService, workingTimeCalculator, 15, 1);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateTaskPriority_When_UpdateClassificationInTask() throws Exception {
      final Instant before = Instant.now();
      Classification classification =
          defaultTestClassification()
              .priority(1)
              .serviceLevel("P13D")
              .buildAndStore(classificationService);
      final List<String> directLinkedTask =
          List.of(createTaskWithExistingClassification(classification.asSummary()));

      classification.setPriority(1000);
      classificationService.updateClassification(classification);
      runAssociatedJobs();

      validateTaskProperties(
          before, directLinkedTask, taskService, workingTimeCalculator, 13, 1000);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateTaskPriorityAndServiceLevel_When_UpdateClassificationInTask()
        throws Exception {
      final Instant before = Instant.now();
      Classification classification =
          defaultTestClassification()
              .priority(1)
              .serviceLevel("P13D")
              .buildAndStore(classificationService);
      final List<String> directLinkedTask =
          List.of(createTaskWithExistingClassification(classification.asSummary()));

      classification.setServiceLevel("P15D");
      classification.setPriority(1000);
      classificationService.updateClassification(classification);
      runAssociatedJobs();

      validateTaskProperties(
          before, directLinkedTask, taskService, workingTimeCalculator, 15, 1000);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_UpdateTaskServiceLevel_When_UpdateClassificationInAttachment() {
      List<Pair<String, Integer>> inputs =
          List.of(Pair.of("P5D", 2), Pair.of("P8D", 3), Pair.of("P16D", 4));

      List<Pair<Integer, Integer>> outputs = List.of(Pair.of(1, 2), Pair.of(1, 3), Pair.of(1, 4));

      List<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> zippedTestInputList =
          IntStream.range(0, inputs.size())
              .mapToObj(i -> Pair.of(inputs.get(i), outputs.get(i)))
              .collect(Collectors.toList());

      ThrowingConsumer<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> test =
          input -> {
            final Instant before = Instant.now();
            Classification classification =
                defaultTestClassification()
                    .priority(1)
                    .serviceLevel("P15D")
                    .buildAndStore(classificationService);
            ClassificationSummary classificationSummary = classification.asSummary();
            final List<String> indirectLinkedTasks =
                createTasksWithExistingClassificationInAttachment(
                    classificationSummary,
                    input.getLeft().getLeft(),
                    input.getLeft().getRight(),
                    5);

            classification.setServiceLevel("P1D");
            classificationService.updateClassification(classification);
            runAssociatedJobs();

            validateTaskProperties(
                before,
                indirectLinkedTasks,
                taskService,
                workingTimeCalculator,
                input.getRight().getLeft(),
                input.getRight().getRight());
          };

      return DynamicTest.stream(
          zippedTestInputList.iterator(),
          i ->
              String.format(
                  "for Task with ServiceLevel %s and Priority %s",
                  i.getLeft().getLeft(), i.getLeft().getRight()),
          test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_NotUpdateTaskServiceLevel_When_UpdateClassificationInAttachment() {
      List<Pair<String, Integer>> inputs =
          List.of(Pair.of("P5D", 2), Pair.of("P8D", 3), Pair.of("P14D", 4));

      List<Pair<Integer, Integer>> outputs = List.of(Pair.of(5, 2), Pair.of(8, 3), Pair.of(14, 4));

      List<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> zippedTestInputList =
          IntStream.range(0, inputs.size())
              .mapToObj(i -> Pair.of(inputs.get(i), outputs.get(i)))
              .collect(Collectors.toList());

      ThrowingConsumer<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> test =
          input -> {
            final Instant before = Instant.now();
            Classification classification =
                defaultTestClassification()
                    .priority(1)
                    .serviceLevel("P1D")
                    .buildAndStore(classificationService);
            ClassificationSummary classificationSummary = classification.asSummary();
            final List<String> indirectLinkedTasks =
                createTasksWithExistingClassificationInAttachment(
                    classificationSummary,
                    input.getLeft().getLeft(),
                    input.getLeft().getRight(),
                    5);

            classification.setServiceLevel("P15D");
            classificationService.updateClassification(classification);
            runAssociatedJobs();

            validateTaskProperties(
                before,
                indirectLinkedTasks,
                taskService,
                workingTimeCalculator,
                input.getRight().getLeft(),
                input.getRight().getRight());
          };

      return DynamicTest.stream(
          zippedTestInputList.iterator(),
          i ->
              String.format(
                  "for Task with ServiceLevel %s and Priority %s",
                  i.getLeft().getLeft(), i.getLeft().getRight()),
          test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_UpdateTaskPriority_When_UpdateClassificationInAttachment() {
      List<Pair<String, Integer>> inputs =
          List.of(Pair.of("P1D", 1), Pair.of("P8D", 2), Pair.of("P14D", 999));

      List<Pair<Integer, Integer>> outputs =
          List.of(Pair.of(1, 1000), Pair.of(8, 1000), Pair.of(14, 1000));

      List<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> zippedTestInputList =
          IntStream.range(0, inputs.size())
              .mapToObj(i -> Pair.of(inputs.get(i), outputs.get(i)))
              .collect(Collectors.toList());

      ThrowingConsumer<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> test =
          input -> {
            final Instant before = Instant.now();
            Classification classification =
                defaultTestClassification()
                    .priority(1)
                    .serviceLevel("P13D")
                    .buildAndStore(classificationService);
            ClassificationSummary classificationSummary = classification.asSummary();
            final List<String> indirectLinkedTasks =
                createTasksWithExistingClassificationInAttachment(
                    classificationSummary,
                    input.getLeft().getLeft(),
                    input.getLeft().getRight(),
                    5);

            classification.setServiceLevel("P15D");
            classification.setPriority(1000);
            classificationService.updateClassification(classification);
            runAssociatedJobs();

            validateTaskProperties(
                before,
                indirectLinkedTasks,
                taskService,
                workingTimeCalculator,
                input.getRight().getLeft(),
                input.getRight().getRight());
          };

      return DynamicTest.stream(
          zippedTestInputList.iterator(),
          i ->
              String.format(
                  "for Task with ServiceLevel %s and Priority %s",
                  i.getLeft().getLeft(), i.getLeft().getRight()),
          test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest> should_NotUpdateTaskPriority_When_UpdateClassificationInAttachment() {
      List<Pair<String, Integer>> inputs =
          List.of(Pair.of("P1D", 2), Pair.of("P8D", 3), Pair.of("P14D", 999));

      List<Pair<Integer, Integer>> outputs =
          List.of(Pair.of(1, 2), Pair.of(8, 3), Pair.of(14, 999));

      List<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> zippedTestInputList =
          IntStream.range(0, inputs.size())
              .mapToObj(i -> Pair.of(inputs.get(i), outputs.get(i)))
              .collect(Collectors.toList());

      ThrowingConsumer<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> test =
          input -> {
            final Instant before = Instant.now();
            Classification classification =
                defaultTestClassification()
                    .priority(1000)
                    .serviceLevel("P13D")
                    .buildAndStore(classificationService);
            ClassificationSummary classificationSummary = classification.asSummary();
            final List<String> indirectLinkedTasks =
                createTasksWithExistingClassificationInAttachment(
                    classificationSummary,
                    input.getLeft().getLeft(),
                    input.getLeft().getRight(),
                    5);

            classification.setServiceLevel("P15D");
            classification.setPriority(1);
            classificationService.updateClassification(classification);
            runAssociatedJobs();

            validateTaskProperties(
                before,
                indirectLinkedTasks,
                taskService,
                workingTimeCalculator,
                input.getRight().getLeft(),
                input.getRight().getRight());
          };

      return DynamicTest.stream(
          zippedTestInputList.iterator(),
          i ->
              String.format(
                  "for Task with ServiceLevel %s and Priority %s",
                  i.getLeft().getLeft(), i.getLeft().getRight()),
          test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest>
        should_UpdateTaskPriorityAndServiceLevel_When_UpdateClassificationInAttachment() {
      List<Pair<String, Integer>> inputs = List.of(Pair.of("P1D", 5), Pair.of("P14D", 98));

      List<Pair<Integer, Integer>> outputs = List.of(Pair.of(1, 99), Pair.of(1, 99));

      List<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> zippedTestInputList =
          IntStream.range(0, inputs.size())
              .mapToObj(i -> Pair.of(inputs.get(i), outputs.get(i)))
              .collect(Collectors.toList());

      ThrowingConsumer<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> test =
          input -> {
            final Instant before = Instant.now();
            Classification classification =
                defaultTestClassification()
                    .priority(1)
                    .serviceLevel("P13D")
                    .buildAndStore(classificationService);
            ClassificationSummary classificationSummary = classification.asSummary();
            final List<String> indirectLinkedTasks =
                createTasksWithExistingClassificationInAttachment(
                    classificationSummary,
                    input.getLeft().getLeft(),
                    input.getLeft().getRight(),
                    3);

            classification.setServiceLevel("P1D");
            classification.setPriority(99);
            classificationService.updateClassification(classification);
            runAssociatedJobs();

            validateTaskProperties(
                before,
                indirectLinkedTasks,
                taskService,
                workingTimeCalculator,
                input.getRight().getLeft(),
                input.getRight().getRight());
          };

      return DynamicTest.stream(
          zippedTestInputList.iterator(),
          i ->
              String.format(
                  "for Task with ServiceLevel %s and Priority %s",
                  i.getLeft().getLeft(), i.getLeft().getRight()),
          test);
    }

    @WithAccessId(user = "businessadmin")
    @TestFactory
    Stream<DynamicTest>
        should_NotUpdateTaskPriorityAndServiceLevel_When_UpdateClassificationInAttachment() {
      List<Pair<String, Integer>> inputs = List.of(Pair.of("P1D", 5), Pair.of("P14D", 98));

      List<Pair<Integer, Integer>> outputs = List.of(Pair.of(1, 5), Pair.of(14, 98));

      List<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> zippedTestInputList =
          IntStream.range(0, inputs.size())
              .mapToObj(i -> Pair.of(inputs.get(i), outputs.get(i)))
              .collect(Collectors.toList());

      ThrowingConsumer<Pair<Pair<String, Integer>, Pair<Integer, Integer>>> test =
          input -> {
            final Instant before = Instant.now();
            Classification classification =
                defaultTestClassification()
                    .priority(1000)
                    .serviceLevel("P1D")
                    .buildAndStore(classificationService);
            ClassificationSummary classificationSummary = classification.asSummary();
            final List<String> indirectLinkedTasks =
                createTasksWithExistingClassificationInAttachment(
                    classificationSummary,
                    input.getLeft().getLeft(),
                    input.getLeft().getRight(),
                    3);

            classification.setServiceLevel("P15D");
            classification.setPriority(1);
            classificationService.updateClassification(classification);
            runAssociatedJobs();

            validateTaskProperties(
                before,
                indirectLinkedTasks,
                taskService,
                workingTimeCalculator,
                input.getRight().getLeft(),
                input.getRight().getRight());
          };

      return DynamicTest.stream(
          zippedTestInputList.iterator(),
          i ->
              String.format(
                  "for Task with ServiceLevel %s and Priority %s",
                  i.getLeft().getLeft(), i.getLeft().getRight()),
          test);
    }

    private void runAssociatedJobs() throws Exception {
      Thread.sleep(10);
      // run the ClassificationChangedJob
      JobRunner runner = new JobRunner(taskanaEngine);
      // run the TaskRefreshJob that was scheduled by the ClassificationChangedJob.
      runner.runJobs();
      Thread.sleep(
          10); // otherwise the next runJobs call intermittently doesn't find the Job created
      // by the previous step (it searches with DueDate < CurrentTime)
      runner.runJobs();
    }

    private void validateTaskProperties(
        Instant before,
        List<String> tasksUpdated,
        TaskService taskService,
        WorkingTimeCalculator workingTimeCalculator,
        int serviceLevel,
        int priority)
        throws Exception {
      for (String taskId : tasksUpdated) {
        Task task = taskService.getTask(taskId);

        Instant expDue =
            workingTimeCalculator
                .addWorkingTime(task.getPlanned(), Duration.ofDays(serviceLevel))
                .minusMillis(1);
        assertThat(task.getModified())
            .describedAs("Task " + task.getId() + " has not been refreshed.")
            .isAfter(before);
        assertThat(task.getDue()).isEqualTo(expDue);
        assertThat(task.getPriority()).isEqualTo(priority);
      }
    }
  }

  @TestInstance(Lifecycle.PER_CLASS)
  @Nested
  class UpdateClassificationExceptionTest {
    /**
     * This BeforeAll method is needed for this {@linkplain
     * #should_ThrowException_When_UserIsNotAuthorized test} and {@linkplain
     * #should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin test} since it can't create an
     * own classification.
     *
     * @throws Exception for errors in the building or reading process of entities.
     */
    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void createClassifications() throws Exception {
      defaultTestClassification()
          .key("BeforeAllClassification")
          .buildAndStore(classificationService);
    }

    @Test
    void should_ThrowException_When_UserIsNotAuthorized() throws Exception {
      Classification classification =
          classificationService.getClassification("BeforeAllClassification", "DOMAIN_A");
      classification.setCustomField(ClassificationCustomField.CUSTOM_1, "newCustom1");

      NotAuthorizedException expectedException =
          new NotAuthorizedException(
              currentUserContext.getUserid(), TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
      assertThatThrownBy(() -> classificationService.updateClassification(classification))
          .usingRecursiveComparison()
          .isEqualTo(expectedException);
    }

    @WithAccessId(user = "taskadmin")
    @WithAccessId(user = "user-1-1")
    @TestTemplate
    void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() throws Exception {
      Classification classification =
          classificationService.getClassification("BeforeAllClassification", "DOMAIN_A");

      classification.setApplicationEntryPoint("updated EntryPoint");
      classification.setName("updated Name");

      NotAuthorizedException expectedException =
          new NotAuthorizedException(
              currentUserContext.getUserid(), TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
      assertThatThrownBy(() -> classificationService.updateClassification(classification))
          .usingRecursiveComparison()
          .isEqualTo(expectedException);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowException_When_UpdatingClassificationConcurrently() throws Exception {
      Classification classification =
          defaultTestClassification().buildAndStore(classificationService);
      final Classification classificationSecondUpdate =
          classificationService.getClassification(
              classification.getKey(), classification.getDomain());

      classification.setApplicationEntryPoint("Application Entry Point");
      classification.setDescription("Description");
      classification.setName("Name");
      Thread.sleep(20); // to avoid identity of modified timestamps between classification and base
      classificationService.updateClassification(classification);
      classificationSecondUpdate.setName("Name again");
      classificationSecondUpdate.setDescription("Description again");

      ConcurrencyException expectedException =
          new ConcurrencyException(classificationSecondUpdate.getId());
      assertThatThrownBy(
              () -> classificationService.updateClassification(classificationSecondUpdate))
          .usingRecursiveComparison()
          .isEqualTo(expectedException);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowException_When_TryingToUpdateClassificationWithInvalidParentId()
        throws Exception {
      Classification classification =
          defaultTestClassification().buildAndStore(classificationService);

      classification.setParentId("NON EXISTING ID");

      ClassificationNotFoundException expectedException =
          new ClassificationNotFoundException("NON EXISTING ID");
      assertThatThrownBy(() -> classificationService.updateClassification(classification))
          .usingRecursiveComparison()
          .isEqualTo(expectedException);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowException_When_TryingToUpdateClassificationWithInvalidParentKey()
        throws Exception {
      Classification classification =
          defaultTestClassification().buildAndStore(classificationService);

      classification.setParentKey("NON EXISTING KEY");

      ClassificationNotFoundException expectedException =
          new ClassificationNotFoundException("NON EXISTING KEY", "DOMAIN_A");
      assertThatThrownBy(() -> classificationService.updateClassification(classification))
          .usingRecursiveComparison()
          .isEqualTo(expectedException);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_ThrowException_When_TryingToUpdateClassificationWithOwnKeyAsParentKey()
        throws Exception {
      Classification classification =
          defaultTestClassification().buildAndStore(classificationService);

      classification.setParentKey(classification.getKey());

      InvalidArgumentException expectedException =
          new InvalidArgumentException(
              String.format(
                  "The Classification '%s' has the same key and parent key",
                  classification.getName()));
      assertThatThrownBy(() -> classificationService.updateClassification(classification))
          .usingRecursiveComparison()
          .isEqualTo(expectedException);
    }
  }

  @TestInstance(Lifecycle.PER_CLASS)
  @Nested
  class UpdateClassificationCategoryTest {
    Classification classification;
    Task task;
    Instant createdBefore;
    Instant modifiedBefore;

    @WithAccessId(user = "businessadmin")
    @BeforeEach
    void createClassificationAndTask() throws Exception {
      classification =
          defaultTestClassification()
              .category("MANUAL")
              .type("TASK")
              .buildAndStore(classificationService);
      createdBefore = classification.getCreated();
      modifiedBefore = classification.getModified();
      String taskId = createTaskWithExistingClassification(classification.asSummary());
      task = taskService.getTask(taskId);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateTask_When_UpdatingClassificationCategory() throws Exception {
      classification.setCategory("PROCESS");
      classificationService.updateClassification(classification);
      final Task updatedTask = taskService.getTask(task.getId());

      TaskImpl expectedUpdatedTask = (TaskImpl) task.copy();
      expectedUpdatedTask.setId(task.getId());
      expectedUpdatedTask.setClassificationCategory("PROCESS");
      expectedUpdatedTask.setClassificationSummary(
          classificationService.getClassification(classification.getId()).asSummary());
      expectedUpdatedTask.setExternalId(task.getExternalId());
      assertThat(expectedUpdatedTask)
          .usingRecursiveComparison()
          .ignoringFields("modified")
          .isEqualTo(updatedTask);
      assertThat(expectedUpdatedTask.getModified()).isAfterOrEqualTo(modifiedBefore);
    }

    @WithAccessId(user = "businessadmin")
    @Test
    void should_UpdateClassification_When_UpdatingClassificationCategory() throws Exception {
      classification.setCategory("PROCESS");
      classificationService.updateClassification(classification);

      Classification updatedClassification =
          classificationService.getClassification(classification.getId());
      assertThat(updatedClassification)
          .usingRecursiveComparison()
          .ignoringFields("modified")
          .isEqualTo(classification);
      assertThat(updatedClassification.getModified()).isAfterOrEqualTo(modifiedBefore);
    }
  }
}
