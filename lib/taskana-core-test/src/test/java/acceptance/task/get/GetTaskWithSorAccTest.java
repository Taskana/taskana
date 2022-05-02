package acceptance.task.get;

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
import pro.taskana.task.internal.builder.ObjectReferenceBuilder;
import pro.taskana.task.internal.builder.TaskBuilder;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.builder.WorkbasketAccessItemBuilder;

@TaskanaIntegrationTest
class GetTaskWithSorAccTest {

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
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskWithSor_When_RequestingTaskByTaskId() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactly("Type0", "Type1");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getValue)
        .containsExactly("Value0", "Value1");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getCompany)
        .containsExactly("Company0", "Company1");
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
