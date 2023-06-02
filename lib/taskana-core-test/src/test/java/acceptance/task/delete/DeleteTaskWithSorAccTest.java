package acceptance.task.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.ObjectReferenceMapper;
import pro.taskana.testapi.TaskanaEngineProxy;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * Acceptance test for all "delete task" scenarios that involve secondary {@link ObjectReference}s.
 */
@TaskanaIntegrationTest
class DeleteTaskWithSorAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject TaskanaEngine taskanaEngine;

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
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
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
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
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
