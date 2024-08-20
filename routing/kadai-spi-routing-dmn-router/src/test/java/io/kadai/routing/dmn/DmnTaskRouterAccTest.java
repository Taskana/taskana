package io.kadai.routing.dmn;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.spi.routing.api.TaskRoutingProvider;
import io.kadai.spi.routing.internal.TaskRoutingManager;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.builder.WorkbasketBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithServiceProvider(
    serviceProviderInterface = TaskRoutingProvider.class,
    serviceProviders = DmnTaskRouter.class)
@KadaiIntegrationTest
class DmnTaskRouterAccTest {
  KadaiConfiguration kadaiConfiguration;
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject UserService userService;
  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject InternalKadaiEngine internalKadaiEngine;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    kadaiConfiguration = kadaiEngine.getConfiguration();
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    createWorkbasketWithDomainA("GPK_KSC").buildAndStoreAsSummary(workbasketService);
    createWorkbasketWithDomainA("GPK_KSC_1").buildAndStoreAsSummary(workbasketService);
    createWorkbasketWithDomainA("GPK_KSC_2").buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    UserBuilder.newUser()
        .id("user-1-2")
        .firstName("Max")
        .lastName("Mustermann")
        .longName("Long name of user-1-2")
        .buildAndStore(userService);

    changeDmnTable("/dmn-table.dmn");

    reinitializeTaskRoutingProviders();
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_RouteTaskToCorrectWorkbasket_When_DmnTaskRouterFindsRule() throws Exception {
    Task taskToRoute = taskService.newTask();
    taskToRoute.setClassificationKey(defaultClassificationSummary.getKey());
    ObjectReference objectReference =
        createObjectReference("company", null, null, "MyType1", "00000001");
    taskToRoute.setPrimaryObjRef(objectReference);

    Task routedTask = taskService.createTask(taskToRoute);
    assertThat(routedTask.getWorkbasketKey()).isEqualTo("GPK_KSC");
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_DmnTaskRouterFindsNoRule() {
    Task taskToRoute = taskService.newTask();
    taskToRoute.setClassificationKey(defaultClassificationSummary.getKey());
    ObjectReference objectReference =
        createObjectReference("company", null, null, "MyTeö", "000002");
    taskToRoute.setPrimaryObjRef(objectReference);

    ThrowingCallable call = () -> taskService.createTask(taskToRoute);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .extracting(Throwable::getMessage)
        .isEqualTo("Cannot create a Task outside a Workbasket");
  }

  ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    return ObjectReferenceBuilder.newObjectReference()
        .company(company)
        .system(system)
        .systemInstance(systemInstance)
        .type(type)
        .value(value)
        .build();
  }

  private void changeDmnTable(String newPath) throws Exception {
    Map<String, String> mutableProperties = new HashMap<>();
    String dmnTableProperty = "kadai.routing.dmn";
    mutableProperties.put(dmnTableProperty, newPath);
    Field property = kadaiConfiguration.getClass().getDeclaredField("properties");
    property.setAccessible(true);
    property.set(kadaiConfiguration, mutableProperties);
    property.setAccessible(false);
  }

  private Object getProperty(String declaredField, Object object) throws Exception {
    Field property = object.getClass().getDeclaredField(declaredField);
    property.setAccessible(true);
    Object returnObject = property.get(object);
    property.setAccessible(false);
    return returnObject;
  }

  private void reinitializeTaskRoutingProviders() throws Exception {
    TaskRoutingManager taskRoutingManager = internalKadaiEngine.getTaskRoutingManager();
    List<TaskRoutingProvider> taskRoutingProviders =
        (List<TaskRoutingProvider>) getProperty("taskRoutingProviders", taskRoutingManager);
    for (TaskRoutingProvider taskRoutingProvider : taskRoutingProviders) {
      taskRoutingProvider.initialize(kadaiEngine);
    }
  }

  private WorkbasketBuilder createWorkbasketWithDomainA(String key) {
    return WorkbasketBuilder.newWorkbasket()
        .key(key)
        .domain("DOMAIN_A")
        .name("Megabasket")
        .type(WorkbasketType.GROUP)
        .orgLevel1("company");
  }
}
