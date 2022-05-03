package acceptance.taskrouting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import acceptance.taskrouting.TaskRoutingAccTest.TaskRoutingProviderForDomainA;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@WithServiceProvider(
    serviceProviderInterface = TaskRoutingProvider.class,
    serviceProviders = TaskRoutingProviderForDomainA.class)
@TaskanaIntegrationTest
class TaskRoutingAccTest {

  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;

  ClassificationSummary classificationSummary;
  WorkbasketSummary domainAWorkbasket;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setUp(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    domainAWorkbasket =
        DefaultTestEntities.defaultTestWorkbasket()
            .key("DOMAIN_A_WORKBASKET")
            .buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(domainAWorkbasket.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    TaskRoutingProviderForDomainA.domainAWorkbasketId = domainAWorkbasket.getId();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TaskRouterDoesNotRouteTask() {
    Task task = taskService.newTask();
    task.setClassificationKey(classificationSummary.getKey());
    task.setPrimaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Cannot create a Task outside a Workbasket");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetWorkbasketForTask_When_TaskRouterDeterminesWorkbasket() throws Exception {
    Task task = taskService.newTask(null, "DOMAIN_A");
    task.setClassificationKey(classificationSummary.getKey());
    task.setPrimaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask.getWorkbasketSummary()).isEqualTo(domainAWorkbasket);
  }

  public static class TaskRoutingProviderForDomainA implements TaskRoutingProvider {

    static String domainAWorkbasketId;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {}

    @Override
    public String determineWorkbasketId(Task task) {
      if ("DOMAIN_A".equals(task.getDomain())) {
        return domainAWorkbasketId;
      }
      return null;
    }
  }
}
