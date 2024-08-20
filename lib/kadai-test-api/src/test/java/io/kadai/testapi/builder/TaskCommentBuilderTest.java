package io.kadai.testapi.builder;

import static io.kadai.common.internal.util.CheckedSupplier.wrap;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static io.kadai.testapi.builder.ClassificationBuilder.newClassification;
import static io.kadai.testapi.builder.TaskCommentBuilder.newTaskComment;
import static io.kadai.testapi.builder.WorkbasketAccessItemBuilder.newWorkbasketAccessItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.Quadruple;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskComment;
import io.kadai.task.internal.models.TaskCommentImpl;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.Workbasket;
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

@KadaiIntegrationTest
class TaskCommentBuilderTest {

  @KadaiInject TaskService taskService;
  @KadaiInject KadaiEngine kadaiEngine;

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
        .permission(WorkbasketPermission.READTASKS)
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
        kadaiEngine.runAsAdmin(wrap(() -> taskService.getTaskComment(taskComment.getId())));
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
        .hasNoNullFieldsOrPropertiesExcept("creatorFullName")
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
      BiFunction<TaskCommentBuilder, T, TaskCommentBuilder> builderFunction,
      Function<TaskComment, T> retriever)
      throws Exception {
    TaskCommentBuilder builder = newTaskComment().taskId(task.getId());
    builderFunction.apply(builder, value);
    TaskComment classification = builder.buildAndStore(taskService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isEqualTo(value);
  }

  private <T> void applyAndOverrideWithApiDefaultValue(
      T value,
      BiFunction<TaskCommentBuilder, T, TaskCommentBuilder> builderFunction,
      Function<TaskComment, T> retriever)
      throws Exception {
    TaskCommentBuilder builder = newTaskComment().taskId(task.getId());

    builderFunction.apply(builder, value);
    builderFunction.apply(builder, null);

    TaskComment classification = builder.buildAndStore(taskService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isNotNull().isNotEqualTo(value);
  }
}
