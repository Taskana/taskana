package acceptance.task.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Triplet;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskAttachmentBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * Acceptance test for all "create task" scenarios that involve {@linkplain
 * pro.taskana.task.api.models.Attachment Attachments}.
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
  void should_SetTaskIdOfAttachmentCorrectly_When_CopyingAttachment() throws Exception {
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
    List<Triplet<String, ObjectReference, String>> valuesForTests =
        List.of(
            Triplet.of("ObjRef is null", null, "ObjectReference of Attachment must not be null."),
            Triplet.of(
                "Type of objRef is null",
                defaultTestObjectReference().type(null).build(),
                "Type of ObjectReference of Attachment must not be empty"),
            Triplet.of(
                "Value of objRef is null",
                defaultTestObjectReference().value(null).build(),
                "Value of ObjectReference of Attachment must not be empty"),
            Triplet.of(
                "Company of objRef is null",
                defaultTestObjectReference().company(null).build(),
                "Company of ObjectReference of Attachment must not be empty"));

    ThrowingConsumer<Triplet<String, ObjectReference, String>> test =
        t -> {
          ObjectReference objectReference = t.getMiddle();
          Attachment attachment = taskService.newAttachment();
          attachment.setClassificationSummary(defaultClassificationSummary);
          attachment.setObjectReference(objectReference);
          Task task = taskService.newTask(defaultWorkbasketSummary.getId());
          task.setClassificationKey(defaultClassificationSummary.getKey());
          task.setPrimaryObjRef(defaultObjectReference);
          task.addAttachment(attachment);
          ThrowingCallable call = () -> taskService.createTask(task);
          String errorMessage = t.getRight();
          assertThatThrownBy(call)
              .isInstanceOf(InvalidArgumentException.class)
              .hasMessageContaining(errorMessage);
        };
    return DynamicTest.stream(valuesForTests.iterator(), Triplet::getLeft, test);
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest>
      should_ThrowException_When_CreatingTaskWithInvalidClassificationOfAttachment() {
    ClassificationSummary nonExistingClassification =
        classificationService.newClassification("non existing", "DOMAIN_A", "Valid type");
    ClassificationSummary classificationWithoutKey =
        classificationService.newClassification(null, "DOMAIN_A", "Valid type");
    List<Triplet<String, ClassificationSummary, Exception>> valuesForTests =
        List.of(
            Triplet.of(
                "Classification doesn't exist",
                nonExistingClassification,
                new ClassificationNotFoundException("non existing", "DOMAIN_A")),
            Triplet.of(
                "Classification is null",
                null,
                new InvalidArgumentException("Classification of Attachment must not be null")),
            Triplet.of(
                "Classification has no key",
                classificationWithoutKey,
                new InvalidArgumentException(
                    "ClassificationKey of Attachment must not be empty.")));

    ThrowingConsumer<Triplet<String, ClassificationSummary, Exception>> test =
        q -> {
          Task task = taskService.newTask(defaultWorkbasketSummary.getId());
          task.setClassificationKey(defaultClassificationSummary.getKey());
          task.setPrimaryObjRef(defaultObjectReference);
          Attachment attachment = taskService.newAttachment();
          ClassificationSummary classificationSummary = q.getMiddle();
          attachment.setClassificationSummary(classificationSummary);
          attachment.setObjectReference(defaultObjectReference);
          task.addAttachment(attachment);
          Exception exception = q.getRight();
          assertThatThrownBy(() -> taskService.createTask(task))
              .usingRecursiveComparison()
              .isEqualTo(exception);
        };

    return DynamicTest.stream(valuesForTests.iterator(), Triplet::getLeft, test);
  }
}
