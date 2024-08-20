package acceptance.task.delete;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.ObjectReferenceMapper;
import io.kadai.testapi.KadaiEngineProxy;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Acceptance test for all "delete task" scenarios that involve secondary {@link ObjectReference}s.
 */
@KadaiIntegrationTest
class DeleteTaskWithSorAccTest {

  @KadaiInject TaskService taskService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject KadaiEngine kadaiEngine;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;
  ObjectReference sor1;
  ObjectReference sor2;

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
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
    sor1 =
        ObjectReferenceBuilder.newObjectReference()
            .company("FirstCompany")
            .value("FirstValue")
            .type("FirstType")
            .build();
    sor2 =
        ObjectReferenceBuilder.newObjectReference()
            .company("SecondCompany")
            .value("SecondValue")
            .type("SecondType")
            .build();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteObjectReferences_When_DeletingTask() throws Exception {
    Task createdTask =
        createCompletedTask().objectReferences(sor1, sor2).buildAndStore(taskService);
    taskService.deleteTask(createdTask.getId());
    KadaiEngineProxy engineProxy = new KadaiEngineProxy(kadaiEngine);
    ObjectReferenceMapper objectReferenceMapper =
        engineProxy.getEngine().getSqlSession().getMapper(ObjectReferenceMapper.class);

    try {
      engineProxy.openConnection();
      assertThat(objectReferenceMapper.findObjectReferencesByTaskId(createdTask.getId())).isEmpty();
    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteObjectReferences_When_MultipleTasksAreDeleted() throws Exception {
    Task firstCreatedTask = createCompletedTask().objectReferences(sor1).buildAndStore(taskService);
    Task secondCreatedTask =
        createCompletedTask().objectReferences(sor1, sor2).buildAndStore(taskService);
    taskService.deleteTasks(List.of(firstCreatedTask.getId(), secondCreatedTask.getId()));
    KadaiEngineProxy engineProxy = new KadaiEngineProxy(kadaiEngine);
    ObjectReferenceMapper objectReferenceMapper =
        engineProxy.getEngine().getSqlSession().getMapper(ObjectReferenceMapper.class);

    try {
      engineProxy.openConnection();
      assertThat(
              objectReferenceMapper.findObjectReferencesByTaskIds(
                  List.of(firstCreatedTask.getId(), secondCreatedTask.getId())))
          .isEmpty();
    } finally {
      engineProxy.returnConnection();
    }
  }

  private TaskBuilder createCompletedTask() {
    return (TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference)
        .state(TaskState.COMPLETED));
  }
}
