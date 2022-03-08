package acceptance.task.create;

import static acceptance.DefaultTestEntities.defaultTestClassification;
import static acceptance.DefaultTestEntities.defaultTestObjectReference;
import static acceptance.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.TaskanaEngineProxy;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.ObjectReferenceMapper;
import pro.taskana.task.internal.builder.ObjectReferenceBuilder;
import pro.taskana.task.internal.builder.TaskBuilder;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.builder.WorkbasketAccessItemBuilder;

/**
 * Acceptance test for all "create task" scenarios that involve secondary {@link ObjectReference}.
 */
@TaskanaIntegrationTest
class CreateTaskWithSorAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject TaskanaEngine taskanaEngine;

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
  void should_createObjectReferences_When_CreatingTask() throws Exception {
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);

    ObjectReference sor1 =
        taskService.newObjectReference(
            "FirstCompany", "FirstSystem", null, "FirstType", "FirstValue");
    ObjectReference sor2 =
        taskService.newObjectReference("SecondCompany", null, null, "SecondType", "SecondValue");

    task.addSecondaryObjectReference(sor1);
    task.addSecondaryObjectReference(sor2);

    Task createdTask = taskService.createTask(task);

    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
    ObjectReferenceMapper objectReferenceMapper =
        engineProxy.getEngine().getSqlSession().getMapper(ObjectReferenceMapper.class);

    try {
      engineProxy.openConnection();
      assertThat(objectReferenceMapper.findObjectReferencesByTaskId(createdTask.getId()))
          .hasSize(2);
    } finally {
      engineProxy.returnConnection();
    }

    Task readTask = taskService.getTask(createdTask.getId());

    assertThat(readTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getSystem)
        .containsExactly("FirstSystem", null);

    assertThat(readTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactly("FirstType", "SecondType");

    assertThat(readTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getValue)
        .containsExactly("FirstValue", "SecondValue");

    assertThat(readTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getCompany)
        .containsExactly("FirstCompany", "SecondCompany");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_InvalidObjectReference() throws Exception {
    ObjectReference sor1 =
        taskService.newObjectReference(
            "FirstCompany", "FirstSystem", null, "FirstType", "FirstValue");
    ObjectReference invalidSor =
        taskService.newObjectReference(null, null, null, "Second Type", "Second Value");

    TaskImpl task = (TaskImpl) taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.addSecondaryObjectReference(sor1);
    task.addSecondaryObjectReference(invalidSor);
    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Company of ObjectReference of Task must not be empty");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_copyObjectReferences_When_DuplicatingTask() throws Exception {
    ObjectReference sor1 =
        ObjectReferenceBuilder.newObjectReference()
            .company("FirstCompany")
            .value("FirstValue")
            .type("FirstType")
            .build();
    ObjectReference sor2 =
        ObjectReferenceBuilder.newObjectReference()
            .company("SecondCompany")
            .value("SecondValue")
            .type("SecondType")
            .build();
    Task oldTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .objectReferences(sor1, sor2)
            .buildAndStore(taskService);
    Task newTask = oldTask.copy();
    newTask = taskService.createTask(newTask);

    Task readOldTask = taskService.getTask(oldTask.getId());

    assertThat(readOldTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getTaskId)
        .containsExactly(oldTask.getId(), oldTask.getId());

    Task readNewTask = taskService.getTask(newTask.getId());

    assertThat(readNewTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getTaskId)
        .containsExactly(newTask.getId(), newTask.getId());

    assertThat(readNewTask.getSecondaryObjectReferences())
        .extracting(ObjectReference::getId)
        .doesNotContainAnyElementsOf(
            readOldTask.getSecondaryObjectReferences().stream()
                .map(ObjectReference::getTaskId)
                .collect(Collectors.toList()));
  }
}
