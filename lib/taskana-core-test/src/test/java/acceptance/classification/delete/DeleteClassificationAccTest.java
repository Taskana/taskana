package acceptance.classification.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.common.api.SharedConstants.MASTER_DOMAIN;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskAttachmentBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
class DeleteClassificationAccTest {

  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;

  Workbasket defaultWorkbasket;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultWorkbasket =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasket.getId())
        .accessId("businessadmin")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "admin");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_NotFindClassificationById_When_DeletingClassificationInDomain() throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification()
            .domain("DOMAIN_A")
            .buildAndStore(classificationService);

    classificationService.deleteClassification(classification.getKey(), classification.getDomain());
    ThrowingCallable call = () -> classificationService.getClassification(classification.getId());

    ClassificationNotFoundException e =
        catchThrowableOfType(call, ClassificationNotFoundException.class);
    assertThat(e.getClassificationId()).isEqualTo(classification.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UsingKeyAndDomainAndUserIsNeitherAdminNorBusinessAdmin()
      throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStore(classificationService, "admin");

    ThrowingCallable call =
        () ->
            classificationService.deleteClassification(
                classification.getKey(), classification.getDomain());

    NotAuthorizedException e = catchThrowableOfType(call, NotAuthorizedException.class);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getRoles())
        .containsExactlyInAnyOrder(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UsingIdAndUserIsNeitherAdminNorBusinessAdmin() throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStore(classificationService, "admin");

    ThrowingCallable call =
        () -> classificationService.deleteClassification(classification.getId());

    NotAuthorizedException e = catchThrowableOfType(call, NotAuthorizedException.class);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getRoles())
        .containsExactlyInAnyOrder(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_DeletingClassificationWithExistingTasks() throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    TaskBuilder.newTask()
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
        .classificationSummary(classification)
        .workbasketSummary(defaultWorkbasket)
        .buildAndStore(taskService);

    ThrowingCallable call =
        () -> classificationService.deleteClassification(classification.getId());

    ClassificationInUseException e = catchThrowableOfType(call, ClassificationInUseException.class);
    assertThat(e.getClassificationKey()).isEqualTo(classification.getKey());
    assertThat(e.getDomain()).isEqualTo(classification.getDomain());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_DeletingMasterClassificationWithExistingTasks() throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    TaskBuilder.newTask()
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
        .classificationSummary(classification)
        .workbasketSummary(defaultWorkbasket)
        .buildAndStore(taskService);

    ThrowingCallable call =
        () -> classificationService.deleteClassification(classification.getKey(), MASTER_DOMAIN);

    ClassificationInUseException e = catchThrowableOfType(call, ClassificationInUseException.class);
    assertThat(e.getClassificationKey()).isEqualTo(classification.getKey());
    assertThat(e.getDomain()).isEqualTo(classification.getDomain());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_DeleteChildren_When_DeletingParentClassification() throws Exception {
    Classification parent =
        DefaultTestEntities.defaultTestClassification()
            .domain(MASTER_DOMAIN)
            .buildAndStore(classificationService);
    Classification child =
        DefaultTestEntities.defaultTestClassification()
            .domain(MASTER_DOMAIN)
            .parentId(parent.getId())
            .buildAndStore(classificationService);

    classificationService.deleteClassification(parent.getId());
    ThrowingCallable call = () -> classificationService.getClassification(child.getId());

    ClassificationNotFoundException e =
        catchThrowableOfType(call, ClassificationNotFoundException.class);
    assertThat(e.getClassificationId()).isEqualTo(child.getId());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_DeleteFromAllDomains_When_DeletingMasterClassification() throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);

    classificationService.deleteClassification(classification.getKey(), MASTER_DOMAIN);
    ThrowingCallable call =
        () ->
            classificationService.getClassification(
                classification.getKey(), classification.getDomain());

    ClassificationNotFoundException e =
        catchThrowableOfType(call, ClassificationNotFoundException.class);
    assertThat(e.getClassificationKey()).isEqualTo(classification.getKey());
    assertThat(e.getDomain()).isEqualTo(classification.getDomain());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_DeletingMasterClassificationWithExistingAttachment()
      throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    Classification attachmentClassification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    Attachment attachment =
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(attachmentClassification.asSummary())
            .objectReference(DefaultTestEntities.defaultTestObjectReference().build())
            .build();
    TaskBuilder.newTask()
        .classificationSummary(classification.asSummary())
        .workbasketSummary(defaultWorkbasket)
        .attachments(attachment)
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
        .buildAndStore(taskService);

    ThrowingCallable call =
        () -> classificationService.deleteClassification(attachmentClassification.getId());

    ClassificationInUseException e = catchThrowableOfType(call, ClassificationInUseException.class);
    assertThat(e.getClassificationKey()).isEqualTo(attachmentClassification.getKey());
    assertThat(e.getDomain()).isEqualTo(attachmentClassification.getDomain());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowExceptionAndRollback_When_ChildClassificationIsInUse() throws Exception {
    Classification parent =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    Classification child =
        DefaultTestEntities.defaultTestClassification()
            .parentId(parent.getId())
            .buildAndStore(classificationService);
    TaskBuilder.newTask()
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
        .classificationSummary(child)
        .workbasketSummary(defaultWorkbasket)
        .buildAndStore(taskService);

    ThrowingCallable call = () -> classificationService.deleteClassification(parent.getId());

    ClassificationInUseException e = catchThrowableOfType(call, ClassificationInUseException.class);
    assertThat(e.getClassificationKey()).isEqualTo(child.getKey());
    assertThat(e.getDomain()).isEqualTo(child.getDomain());
    Classification rollback =
        classificationService.getClassification(parent.getKey(), parent.getDomain());
    assertThat(rollback).isEqualTo(parent);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowExceptionAndRollbackAllDomains_When_ChildClassificationIsInUse()
      throws Exception {
    Classification parent =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    Classification child =
        DefaultTestEntities.defaultTestClassification()
            .parentId(parent.getId())
            .buildAndStore(classificationService);
    TaskBuilder.newTask()
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
        .classificationSummary(child)
        .workbasketSummary(defaultWorkbasket)
        .buildAndStore(taskService);
    Classification parentMaster =
        classificationService.getClassification(parent.getKey(), MASTER_DOMAIN);

    ThrowingCallable call =
        () -> classificationService.deleteClassification(parent.getKey(), MASTER_DOMAIN);

    ClassificationInUseException e = catchThrowableOfType(call, ClassificationInUseException.class);
    assertThat(e.getClassificationKey()).isEqualTo(child.getKey());
    assertThat(e.getDomain()).isEqualTo(child.getDomain());
    Classification rollbackMaster =
        classificationService.getClassification(parent.getKey(), MASTER_DOMAIN);
    Classification rollbackDomain =
        classificationService.getClassification(parent.getKey(), parent.getDomain());
    assertThat(rollbackDomain).isEqualTo(parent);
    assertThat(rollbackMaster).isEqualTo(parentMaster);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowClassificationNotFoundException_When_ClassificationDoesNotExist() {
    ThrowingCallable call =
        () ->
            classificationService.deleteClassification(
                "not existing classification key", MASTER_DOMAIN);

    ClassificationNotFoundException e =
        catchThrowableOfType(call, ClassificationNotFoundException.class);
    assertThat(e.getClassificationKey()).isEqualTo("not existing classification key");
    assertThat(e.getDomain()).isEqualTo(MASTER_DOMAIN);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowClassificationNotFoundException_When_ClassificationDoesNotExistInDomain()
      throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);

    ThrowingCallable call =
        () -> classificationService.deleteClassification(classification.getKey(), "DOMAIN_B");

    ClassificationNotFoundException e =
        catchThrowableOfType(call, ClassificationNotFoundException.class);
    assertThat(e.getClassificationKey()).isEqualTo(classification.getKey());
    assertThat(e.getDomain()).isEqualTo("DOMAIN_B");
  }
}
