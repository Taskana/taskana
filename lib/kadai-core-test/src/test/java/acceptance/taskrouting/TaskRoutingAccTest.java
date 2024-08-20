package acceptance.taskrouting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import acceptance.taskrouting.TaskRoutingAccTest.TaskRoutingProviderForDomainA;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.spi.routing.api.TaskRoutingProvider;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithServiceProvider(
    serviceProviderInterface = TaskRoutingProvider.class,
    serviceProviders = TaskRoutingProviderForDomainA.class)
@KadaiIntegrationTest
class TaskRoutingAccTest {

  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject TaskService taskService;

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

  class TaskRoutingProviderForDomainA implements TaskRoutingProvider {

    @Override
    public void initialize(KadaiEngine kadaiEngine) {}

    @Override
    public String determineWorkbasketId(Task task) {
      if ("DOMAIN_A".equals(task.getDomain())) {
        return domainAWorkbasket.getId();
      }
      return null;
    }
  }
}
