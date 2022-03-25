package acceptance.task;

import static acceptance.DefaultTestEntities.defaultTestClassification;
import static acceptance.DefaultTestEntities.defaultTestObjectReference;
import static acceptance.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.internal.util.Quadruple;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.builder.TaskAttachmentBuilder;
import pro.taskana.task.internal.builder.TaskBuilder;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.builder.WorkbasketAccessItemBuilder;

/**
 * Acceptance test for all "create task" scenarios that involve {@linkplain Attachment Attachments}.
 */
@TaskanaIntegrationTest
class CreateTaskWithAttachmentAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;
  Attachment defaultAttachment;
  Task defaultTaskWithAttachment;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification()
            .serviceLevel("P2D")
            .buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
    defaultAttachment =
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(defaultClassificationSummary)
            .objectReference(defaultObjectReference)
            .build();
    defaultTaskWithAttachment =
        TaskBuilder.newTask()
            .primaryObjRef(defaultObjectReference)
            .workbasketSummary(defaultWorkbasketSummary)
            .classificationSummary(defaultClassificationSummary)
            .attachments(defaultAttachment)
            .buildAndStore(taskService, "user-1-1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetTaskIdOfAttachmentCorrectly_WhenCopyingAttachment() throws Exception {
    Attachment copiedAttachment =
        taskService.getTask(defaultTaskWithAttachment.getId()).getAttachments().get(0).copy();
    Task taskToCreate = taskService.newTask(defaultWorkbasketSummary.getId());
    taskToCreate.setClassificationKey(defaultClassificationSummary.getKey());
    taskToCreate.setPrimaryObjRef(defaultObjectReference);
    taskToCreate.addAttachment(copiedAttachment);
    Task result = taskService.createTask(taskToCreate);

    assertThat(result.getAttachments().get(0).getTaskId()).isEqualTo(result.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FetchAttachmentClassification_When_CreatingTaskWithAttachments() throws Exception {
    Attachment attachment = taskService.newAttachment();
    attachment.setObjectReference(defaultObjectReference);
    ClassificationSummary classification =
        classificationService
            .newClassification(
                defaultClassificationSummary.getKey(),
                defaultClassificationSummary.getDomain(),
                defaultClassificationSummary.getType())
            .asSummary();
    attachment.setClassificationSummary(classification);
    Task taskToCreate = taskService.newTask(defaultWorkbasketSummary.getId());
    taskToCreate.setClassificationKey(defaultClassificationSummary.getKey());
    taskToCreate.setPrimaryObjRef(defaultObjectReference);
    taskToCreate.addAttachment(attachment);
    taskToCreate = taskService.createTask(taskToCreate);

    classification = taskToCreate.getAttachments().get(0).getClassificationSummary();
    assertThat(classification).isEqualTo(defaultClassificationSummary);
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest>
      should_ThrowException_When_CreatingTaskWithInvalidObjectReferenceOfAttachment() {
    ObjectReference objRefTypeNull =
        taskService.newObjectReference("Company", "System", "Instance", null, "Value");
    ObjectReference objRefValueNull =
        taskService.newObjectReference("Company", "System", "Instance", "Type", null);
    ObjectReference objRefCompanyNull =
        taskService.newObjectReference(null, "System", "Instance", "Type", "Value");
    List<Pair<String, ObjectReference>> valuesForTests =
        List.of(
            Pair.of("ObjRef is null", null),
            Pair.of("Type of objRef is null", objRefTypeNull),
            Pair.of("Value of objRef is null", objRefValueNull),
            Pair.of("Company of objRef is null", objRefCompanyNull));

    ThrowingConsumer<Pair<String, ObjectReference>> test =
        p -> {
          Task task = taskService.newTask(defaultWorkbasketSummary.getId());
          task.setClassificationKey(defaultClassificationSummary.getKey());
          task.setPrimaryObjRef(defaultObjectReference);
          Attachment attachment = taskService.newAttachment();
          attachment.setClassificationSummary(defaultClassificationSummary);
          attachment.setObjectReference(p.getRight());
          task.addAttachment(attachment);
          assertThatThrownBy(() -> taskService.createTask(task))
              .isInstanceOf(InvalidArgumentException.class);
        };

    return DynamicTest.stream(valuesForTests.iterator(), Pair::getLeft, test);
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest>
      should_ThrowException_When_CreatingTaskWithInvalidClassificationOfAttachment() {
    Classification nonExistingClassification =
        classificationService.newClassification(
            "123key345", defaultWorkbasketSummary.getDomain(), "non existing type");
    Classification classificationWithoutKey =
        classificationService.newClassification(
            null, defaultClassificationSummary.getDomain(), defaultClassificationSummary.getType());
    List<Quadruple<String, Classification, String, Class<?>>> valuesForTests =
        List.of(
            Quadruple.of(
                "Classification is null",
                null,
                "Classification of Attachment must not be null",
                InvalidArgumentException.class),
            Quadruple.of(
                "Classification doesn't exist",
                nonExistingClassification,
                "Classification with key '123key345' and domain '"
                    + defaultWorkbasketSummary.getDomain()
                    + "' could not be found",
                ClassificationNotFoundException.class),
            Quadruple.of(
                "Classification has no key",
                classificationWithoutKey,
                "ClassificationKey of Attachment must not be empty.",
                InvalidArgumentException.class));

    ThrowingConsumer<Quadruple<String, Classification, String, Class<?>>> test =
        q -> {
          Task task = taskService.newTask(defaultWorkbasketSummary.getId());
          task.setClassificationKey(defaultClassificationSummary.getKey());
          task.setPrimaryObjRef(defaultObjectReference);
          Attachment attachment = taskService.newAttachment();
          attachment.setClassificationSummary(q.getSecond());
          attachment.setObjectReference(defaultObjectReference);
          task.addAttachment(attachment);
          assertThatThrownBy(() -> taskService.createTask(task))
              .isInstanceOf(q.getFourth())
              .hasMessageContaining(q.getThird());
        };

    return DynamicTest.stream(valuesForTests.iterator(), Quadruple::getFirst, test);
  }
}
