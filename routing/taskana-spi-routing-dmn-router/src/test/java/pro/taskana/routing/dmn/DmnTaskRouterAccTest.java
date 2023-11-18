package pro.taskana.routing.dmn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.spi.routing.internal.TaskRoutingManager;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.UserBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.builder.WorkbasketBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@WithServiceProvider(
    serviceProviderInterface = TaskRoutingProvider.class,
    serviceProviders = DmnTaskRouter.class)
@TaskanaIntegrationTest
class DmnTaskRouterAccTest {
  TaskanaConfiguration taskanaConfiguration;
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject InternalTaskanaEngine internalTaskanaEngine;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    taskanaConfiguration = taskanaEngine.getConfiguration();
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
    String dmnTableProperty = "taskana.routing.dmn";
    mutableProperties.put(dmnTableProperty, newPath);
    Field property = taskanaConfiguration.getClass().getDeclaredField("properties");
    property.setAccessible(true);
    property.set(taskanaConfiguration, mutableProperties);
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
    TaskRoutingManager taskRoutingManager = internalTaskanaEngine.getTaskRoutingManager();
    List<TaskRoutingProvider> taskRoutingProviders =
        (List<TaskRoutingProvider>) getProperty("taskRoutingProviders", taskRoutingManager);
    for (TaskRoutingProvider taskRoutingProvider : taskRoutingProviders) {
      taskRoutingProvider.initialize(taskanaEngine);
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
