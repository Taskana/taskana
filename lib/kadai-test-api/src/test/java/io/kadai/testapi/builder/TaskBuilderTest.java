package io.kadai.testapi.builder;

import static io.kadai.common.internal.util.CheckedSupplier.wrap;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static io.kadai.testapi.builder.ClassificationBuilder.newClassification;
import static io.kadai.testapi.builder.TaskBuilder.newTask;
import static io.kadai.testapi.builder.WorkbasketAccessItemBuilder.newWorkbasketAccessItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.Quadruple;
import io.kadai.task.api.CallbackState;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskCustomIntField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.task.internal.models.TaskSummaryImpl;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

@KadaiIntegrationTest
class TaskBuilderTest {
  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject TaskService taskService;

  WorkbasketSummary workbasketSummary;
  ClassificationSummary classificationSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(WorkbasketService workbasketService, ClassificationService classificationService)
      throws Exception {
    workbasketSummary =
        defaultTestWorkbasket()
            .owner("user-1-1")
            .key("key0_D")
            .buildAndStoreAsSummary(workbasketService);
    classificationSummary =
        newClassification()
            .key("key0_D")
            .domain("DOMAIN_A")
            .buildAndStoreAsSummary(classificationService);
    newWorkbasketAccessItem()
        .workbasketId(workbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_PersistTask_When_UsingTaskBuilder() throws Exception {
    Task task =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task receivedTask = taskService.getTask(task.getId());
    assertThat(receivedTask).isEqualTo(task);
  }

  @Test
  void should_PersistTaskAsUser_When_UsingTaskBuilder() throws Exception {
    Task task =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "user-1-1");

    Task receivedTask = kadaiEngine.runAsAdmin(wrap(() -> taskService.getTask(task.getId())));
    assertThat(receivedTask).isEqualTo(task);
    assertThat(receivedTask.getCreator()).isEqualTo("user-1-1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_PopulateTask_When_UsingEveryBuilderFunction() throws Exception {
    Attachment attachment =
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(classificationSummary)
            .objectReference(defaultTestObjectReference().build())
            .build();

    final Task task =
        newTask()
            .externalId("external id")
            .received(Instant.parse("2020-04-19T13:13:00.000Z"))
            .created(Instant.parse("2020-04-20T13:13:00.000Z"))
            .claimed(Instant.parse("2020-04-21T13:13:00.000Z"))
            .completed(Instant.parse("2020-04-22T13:13:00.000Z"))
            .modified(Instant.parse("2020-04-22T13:13:00.000Z"))
            .planned(Instant.parse("2020-04-21T13:13:00.000Z"))
            .due(Instant.parse("2020-04-21T13:13:00.000Z"))
            .name("Voll der geile Task")
            .note("Jap, voll geil")
            .description("Mega!")
            .state(TaskState.COMPLETED)
            .classificationSummary(classificationSummary)
            .workbasketSummary(workbasketSummary)
            .businessProcessId("BPI:Cool")
            .parentBusinessProcessId("BPI:ParentIsCool")
            .owner("hanspeter")
            .primaryObjRef(defaultTestObjectReference().build())
            .manualPriority(7)
            .read(true)
            .transferred(true)
            .attachments(attachment)
            .customAttribute(TaskCustomField.CUSTOM_1, "custom1")
            .customAttribute(TaskCustomField.CUSTOM_2, "custom2")
            .customAttribute(TaskCustomField.CUSTOM_3, "custom3")
            .customAttribute(TaskCustomField.CUSTOM_4, "custom4")
            .customAttribute(TaskCustomField.CUSTOM_5, "custom5")
            .customAttribute(TaskCustomField.CUSTOM_6, "custom6")
            .customAttribute(TaskCustomField.CUSTOM_7, "custom7")
            .customAttribute(TaskCustomField.CUSTOM_8, "custom8")
            .customAttribute(TaskCustomField.CUSTOM_9, "custom9")
            .customAttribute(TaskCustomField.CUSTOM_10, "custom10")
            .customAttribute(TaskCustomField.CUSTOM_11, "custom11")
            .customAttribute(TaskCustomField.CUSTOM_12, "custom12")
            .customAttribute(TaskCustomField.CUSTOM_13, "custom13")
            .customAttribute(TaskCustomField.CUSTOM_14, "custom14")
            .customAttribute(TaskCustomField.CUSTOM_15, "custom15")
            .customAttribute(TaskCustomField.CUSTOM_16, "custom16")
            .customIntField(TaskCustomIntField.CUSTOM_INT_1, 1)
            .customIntField(TaskCustomIntField.CUSTOM_INT_2, 2)
            .customIntField(TaskCustomIntField.CUSTOM_INT_3, 3)
            .customIntField(TaskCustomIntField.CUSTOM_INT_4, 4)
            .customIntField(TaskCustomIntField.CUSTOM_INT_5, 5)
            .customIntField(TaskCustomIntField.CUSTOM_INT_6, 6)
            .customIntField(TaskCustomIntField.CUSTOM_INT_7, 7)
            .customIntField(TaskCustomIntField.CUSTOM_INT_8, 8)
            .callbackInfo(Map.of("custom", "value"))
            .callbackState(CallbackState.CALLBACK_PROCESSING_COMPLETED)
            .buildAndStore(taskService);

    TaskImpl expectedTask = (TaskImpl) taskService.newTask(workbasketSummary.getId());
    expectedTask.setExternalId("external id");
    expectedTask.setReceived(Instant.parse("2020-04-19T13:13:00.000Z"));
    expectedTask.setCreated(Instant.parse("2020-04-20T13:13:00.000Z"));
    expectedTask.setClaimed(Instant.parse("2020-04-21T13:13:00.000Z"));
    expectedTask.setCompleted(Instant.parse("2020-04-22T13:13:00.000Z"));
    expectedTask.setModified(Instant.parse("2020-04-22T13:13:00.000Z"));
    expectedTask.setPlanned(Instant.parse("2020-04-21T13:13:00.000Z"));
    expectedTask.setDue(Instant.parse("2020-04-21T13:13:00.000Z"));
    expectedTask.setName("Voll der geile Task");
    expectedTask.setNote("Jap, voll geil");
    expectedTask.setDescription("Mega!");
    expectedTask.setState(TaskState.COMPLETED);
    expectedTask.setClassificationSummary(classificationSummary);
    expectedTask.setWorkbasketSummary(workbasketSummary);
    expectedTask.setBusinessProcessId("BPI:Cool");
    expectedTask.setParentBusinessProcessId("BPI:ParentIsCool");
    expectedTask.setOwner("hanspeter");
    expectedTask.setPrimaryObjRef(defaultTestObjectReference().build());
    expectedTask.setManualPriority(7);
    expectedTask.setRead(true);
    expectedTask.setTransferred(true);
    expectedTask.setCreator("user-1-1");
    expectedTask.addAttachment(attachment);
    expectedTask.setCustomField(TaskCustomField.CUSTOM_1, "custom1");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_2, "custom2");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_3, "custom3");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_4, "custom4");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_5, "custom5");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_6, "custom6");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_7, "custom7");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_8, "custom8");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_9, "custom9");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_10, "custom10");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_11, "custom11");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_12, "custom12");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_13, "custom13");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_14, "custom14");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_15, "custom15");
    expectedTask.setCustomField(TaskCustomField.CUSTOM_16, "custom16");
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_1, 1);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_2, 2);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_3, 3);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_4, 4);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_5, 5);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_6, 6);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_7, 7);
    expectedTask.setCustomIntField(TaskCustomIntField.CUSTOM_INT_8, 8);
    expectedTask.setCallbackInfo(Map.of("custom", "value"));
    expectedTask.setCallbackState(CallbackState.CALLBACK_PROCESSING_COMPLETED);

    assertThat(task)
        .hasNoNullFieldsOrPropertiesExcept("ownerLongName", "groupByCount")
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ResetTaskId_When_StoringTaskMultipleTimes() {
    TaskBuilder builder =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build());

    assertThatCode(
            () -> {
              builder.buildAndStore(taskService);
              builder.buildAndStore(taskService);
            })
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskImpl_When_BuildingTask() throws Exception {
    Task task =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);

    assertThat(task.getClass()).isEqualTo(TaskImpl.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskSummaryImpl_When_BuildingTaskAsSummary() throws Exception {
    TaskSummary taskSummary =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStoreAsSummary(taskService);

    assertThat(taskSummary.getClass()).isEqualTo(TaskSummaryImpl.class);
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicContainer> should_PersistClassification_When_CreatingEntityWithInvalidApiValues() {
    List<
            Quadruple<
                String,
                Object,
                BiFunction<TaskBuilder, Object, TaskBuilder>,
                Function<Task, Object>>>
        list =
            List.of(
                Quadruple.of(
                    "state", TaskState.CANCELLED, (b, v) -> b.state((TaskState) v), Task::getState),
                Quadruple.of(
                    "created",
                    Instant.parse("2020-05-17T07:16:26.747Z"),
                    (b, v) -> b.created((Instant) v),
                    Task::getCreated),
                Quadruple.of(
                    "modified",
                    Instant.parse("2019-05-17T07:16:26.747Z"),
                    (b, v) -> b.modified((Instant) v),
                    Task::getModified),
                Quadruple.of("read", true, (b, v) -> b.read((Boolean) v), Task::isRead),
                Quadruple.of(
                    "transferred", true, (b, v) -> b.transferred((Boolean) v), Task::isTransferred),
                Quadruple.of(
                    "priority", 1337, (b, v) -> b.priority((Integer) v), Task::getPriority));

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
      T value, BiFunction<TaskBuilder, T, TaskBuilder> builderFunction, Function<Task, T> retriever)
      throws Exception {
    TaskBuilder builder =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build());

    builderFunction.apply(builder, value);
    Task task = builder.buildAndStore(taskService);
    T retrievedValue = retriever.apply(task);

    assertThat(retrievedValue).isEqualTo(value);
  }

  private <T> void applyAndOverrideWithApiDefaultValue(
      T value, BiFunction<TaskBuilder, T, TaskBuilder> builderFunction, Function<Task, T> retriever)
      throws Exception {
    TaskBuilder builder =
        newTask()
            .workbasketSummary(workbasketSummary)
            .classificationSummary(classificationSummary)
            .primaryObjRef(defaultTestObjectReference().build());

    builderFunction.apply(builder, value);
    builderFunction.apply(builder, null);

    Task task = builder.buildAndStore(taskService);
    T retrievedValue = retriever.apply(task);

    assertThat(retrievedValue).isNotNull().isNotEqualTo(value);
  }
}
