package acceptance.builder;

import static acceptance.DefaultTestEntities.defaultTestObjectReference;
import static acceptance.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static pro.taskana.classification.internal.builder.ClassificationBuilder.newClassification;
import static pro.taskana.common.internal.util.CheckedSupplier.wrap;
import static pro.taskana.task.internal.builder.TaskCommentBuilder.newTaskComment;
import static pro.taskana.workbasket.internal.builder.WorkbasketAccessItemBuilder.newWorkbasketAccessItem;

import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.Quadruple;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.builder.TaskBuilder;
import pro.taskana.task.internal.builder.TaskCommentBuilder;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
class TaskCommentBuilderTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  Task task;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(WorkbasketService workbasketService, ClassificationService classificationService)
      throws Exception {
    ObjectReference objectReference = defaultTestObjectReference().build();
    Workbasket workbasket =
        defaultTestWorkbasket().owner("user-1-1").key("key0_E").buildAndStore(workbasketService);
    Classification classification =
        newClassification().key("key0_E").domain("DOMAIN_A").buildAndStore(classificationService);
    newWorkbasketAccessItem()
        .workbasketId(workbasket.getId())
        .permission(WorkbasketPermission.READ)
        .accessId("user-1-1")
        .buildAndStore(workbasketService);
    task =
        TaskBuilder.newTask()
            .workbasketSummary(workbasket)
            .classificationSummary(classification)
            .primaryObjRef(objectReference)
            .buildAndStore(taskService, "admin");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_PersistTaskComment_When_UsingTaskCommentBuilder() throws Exception {
    TaskComment taskComment = newTaskComment().taskId(task.getId()).buildAndStore(taskService);

    TaskComment receivedTaskComment = taskService.getTaskComment(taskComment.getId());
    assertThat(receivedTaskComment).isEqualTo(taskComment);
  }

  @Test
  void should_PersistTaskCommentAsUser_When_UsingTaskCommentBuilder() throws Exception {
    TaskComment taskComment =
        newTaskComment().taskId(task.getId()).buildAndStore(taskService, "user-1-1");

    TaskComment receivedTaskComment =
        taskanaEngine.runAsAdmin(wrap(() -> taskService.getTaskComment(taskComment.getId())));
    assertThat(receivedTaskComment).isEqualTo(taskComment);
    assertThat(receivedTaskComment.getCreator()).isEqualTo("user-1-1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_PopulateTaskComment_When_UsingEveryBuilderFunction() throws Exception {
    final TaskComment taskComment =
        newTaskComment()
            .taskId(task.getId())
            .textField("A comment from the TEST-API. :)")
            .created(Instant.parse("2020-04-30T07:12:00.000Z"))
            .modified(Instant.parse("2020-04-30T07:12:00.000Z"))
            .buildAndStore(taskService);

    TaskCommentImpl expectedTaskComment =
        (TaskCommentImpl) taskService.newTaskComment(task.getId());
    expectedTaskComment.setTextField("A comment from the TEST-API. :)");
    expectedTaskComment.setCreated(Instant.parse("2020-04-30T07:12:00.000Z"));
    expectedTaskComment.setModified(Instant.parse("2020-04-30T07:12:00.000Z"));
    expectedTaskComment.setCreator("user-1-1");

    assertThat(taskComment)
        .hasNoNullFieldsOrPropertiesExcept("creatorLongName")
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedTaskComment);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ResetTaskCommentId_When_StoringTaskCommentMultipleTimes() {
    TaskCommentBuilder builder = newTaskComment().taskId(task.getId());

    assertThatCode(
            () -> {
              builder.buildAndStore(taskService);
              builder.buildAndStore(taskService);
            })
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicContainer> should_PersistTaskComment_When_CreatingEntityWithInvalidApiValues() {
    List<
            Quadruple<
                String,
                Object,
                BiFunction<TaskCommentBuilder, Object, TaskCommentBuilder>,
                Function<TaskComment, Object>>>
        list =
            List.of(
                Quadruple.of(
                    "created",
                    Instant.parse("2020-05-17T07:16:26.747Z"),
                    (b, v) -> b.created((Instant) v),
                    TaskComment::getCreated),
                Quadruple.of(
                    "modified",
                    Instant.parse("2019-05-17T07:16:26.747Z"),
                    (b, v) -> b.modified((Instant) v),
                    TaskComment::getModified));

    Stream<DynamicTest> applyBuilderFunction =
        DynamicTest.stream(
            list.iterator(),
            q -> String.format("for field: '%s'", q.getFirst()),
            q -> applyBuilderFunctionAndVerifyValue(q.getSecond(), q.getThird(), q.getFourth()));

    Stream<DynamicTest> overrideBuilderFunctionWithApiDefault =
        DynamicTest.stream(
            list.iterator(),
            q -> String.format("for field: '%s'", q.getFirst()),
            t -> applyAndOverrideWithApiDefaultValue(t.getSecond(), t.getThird(), t.getFourth()));

    return Stream.of(
        DynamicContainer.dynamicContainer(
            "set values which are invalid through API", applyBuilderFunction),
        DynamicContainer.dynamicContainer(
            "override with API default value", overrideBuilderFunctionWithApiDefault));
  }

  private <T> void applyBuilderFunctionAndVerifyValue(
      T value,
      BiFunction<TaskCommentBuilder, T, TaskCommentBuilder> builderfunction,
      Function<TaskComment, T> retriever)
      throws Exception {
    TaskCommentBuilder builder = newTaskComment().taskId(task.getId());
    builderfunction.apply(builder, value);
    TaskComment classification = builder.buildAndStore(taskService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isEqualTo(value);
  }

  private <T> void applyAndOverrideWithApiDefaultValue(
      T value,
      BiFunction<TaskCommentBuilder, T, TaskCommentBuilder> builderfunction,
      Function<TaskComment, T> retriever)
      throws Exception {
    TaskCommentBuilder builder = newTaskComment().taskId(task.getId());

    builderfunction.apply(builder, value);
    builderfunction.apply(builder, null);

    TaskComment classification = builder.buildAndStore(taskService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isNotNull().isNotEqualTo(value);
  }
}
