package acceptance.task.update;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for "update task" scenarios that involve secondary {@link ObjectReference}s. */
@TaskanaIntegrationTest
class UpdateTaskWithSorAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotChangeSor_When_UpdateWithoutChanges() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactlyInAnyOrder("Type0", "Type1");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getValue)
        .containsExactlyInAnyOrder("Value0", "Value1");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getCompany)
        .containsExactlyInAnyOrder("Company0", "Company1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateExistingSor_When_SorChangedInUpdatedTask() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);

    ObjectReference sorToUpdate = task.getSecondaryObjectReferences().get(0);
    sorToUpdate.setType("NewType");

    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactlyInAnyOrder("NewType", "Type1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AddNewSor_When_UpdatedTaskContainsNewSor() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    task.addSecondaryObjectReference("NewCompany", null, null, "NewType", "NewValue");
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactlyInAnyOrder("Type0", "Type1", "NewType");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getValue)
        .containsExactlyInAnyOrder("Value0", "Value1", "NewValue");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getCompany)
        .containsExactlyInAnyOrder("Company0", "Company1", "NewCompany");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DeleteOneSor_When_UpdatedTaskContainsOneLessSor() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    task.removeSecondaryObjectReference(task.getSecondaryObjectReferences().get(0).getId());
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences()).hasSize(1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DeleteAllSor_When_UpdatedTaskContainsNoSor() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    task.removeSecondaryObjectReference(task.getSecondaryObjectReferences().get(0).getId());
    task.removeSecondaryObjectReference(task.getSecondaryObjectReferences().get(0).getId());
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences()).isEmpty();
  }

  private TaskBuilder createDefaultTask() {
    return (TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference));
  }

  private ObjectReference defaultSecondaryObjectReference(String suffix) {
    return ObjectReferenceBuilder.newObjectReference()
        .company("Company" + suffix)
        .value("Value" + suffix)
        .type("Type" + suffix)
        .build();
  }
}
