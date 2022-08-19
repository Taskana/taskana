package acceptance.task.requestchanges;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static pro.taskana.common.internal.util.CheckedSupplier.wrap;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.jobs.PlainJavaTransactionProvider;
import pro.taskana.spi.task.api.BeforeRequestChangesProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
public class RequestChangesWithBeforeSpiAccTest {

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService);

    defaultObjectReference = DefaultTestEntities.defaultTestObjectReference().build();
  }

  private TaskBuilder createTaskInReviewByUser(String owner) {
    return createDefaultTask().owner(owner).state(TaskState.IN_REVIEW);
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }

  static class ExceptionThrower implements BeforeRequestChangesProvider {

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task beforeRequestChanges(Task task) throws Exception {
      task.setDescription("should not matter. Will get rolled back anyway");
      taskanaEngine.getTaskService().updateTask(task);
      throw new SystemException("I AM THE EXCEPTION THROWER (*_*)");
    }
  }

  static class TaskModifier implements BeforeRequestChangesProvider {
    private static final String NEW_CUSTOM_3_VALUE = "bla";

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task beforeRequestChanges(Task task) throws Exception {
      task.setCustomField(TaskCustomField.CUSTOM_3, NEW_CUSTOM_3_VALUE);
      task = taskanaEngine.getTaskService().updateTask(task);
      return task;
    }
  }

  static class ClassificationUpdater implements BeforeRequestChangesProvider {

    public static final String SPI_CLASSIFICATION_NAME = "Neuer Classification Name";

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task beforeRequestChanges(Task task) throws Exception {
      Classification newClassification =
          defaultTestClassification().buildAndStore(taskanaEngine.getClassificationService());

      task.setClassificationKey(newClassification.getKey());
      task = taskanaEngine.getTaskService().updateTask(task);

      newClassification.setName(SPI_CLASSIFICATION_NAME);
      taskanaEngine.getClassificationService().updateClassification(newClassification);

      return task;
    }
  }

  static class SetTaskOwner implements BeforeRequestChangesProvider {

    TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task beforeRequestChanges(Task task) throws Exception {
      task.setOwner("new owner");
      return taskanaEngine.getTaskService().updateTask(task);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = BeforeRequestChangesProvider.class,
      serviceProviders = TaskModifier.class)
  class SpiModifiesTask {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnModifiedTask_When_SpiModifiesAndTransfersTask() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      Task result = taskService.requestChanges(task.getId());

      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifier.NEW_CUSTOM_3_VALUE);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnPersistentModifiedTask_When_SpiModifiesAndTransfersTask() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      taskService.requestChanges(task.getId());
      Task result = taskService.getTask(task.getId());

      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifier.NEW_CUSTOM_3_VALUE);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = BeforeRequestChangesProvider.class,
      serviceProviders = SetTaskOwner.class)
  class SpiSetsValuesWhichGetOverridenByTaskana {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @Test
    void should_OverrideOwner_When_SpiModifiesOwner() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      taskService.forceRequestChanges(task.getId());
      Task result = taskService.getTask(task.getId());

      assertThat(result.getOwner()).isNull();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ThrowError_When_SpiModifiesOwner() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      ThrowingCallable call = () -> taskService.requestChanges(task.getId());

      InvalidOwnerException ex = catchThrowableOfType(call, InvalidOwnerException.class);
      assertThat(ex.getTaskId()).isEqualTo(task.getId());
      assertThat(ex.getCurrentUserId()).isEqualTo("user-1-1");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = BeforeRequestChangesProvider.class,
      serviceProviders = ClassificationUpdater.class)
  class SpiModifiesTaskAndClassification {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1", groups = "businessadmin")
    @Test
    void should_ChangeMultipleEntities_When_SpiModifiesMoreThanTheTask() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      taskService.requestChanges(task.getId());
      Task result = taskService.getTask(task.getId());

      assertThat(result.getClassificationSummary().getId())
          .isNotEqualTo(task.getClassificationSummary().getId());
      assertThat(result.getClassificationSummary().getName())
          .isEqualTo(ClassificationUpdater.SPI_CLASSIFICATION_NAME);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = BeforeRequestChangesProvider.class,
      serviceProviders = {TaskModifier.class, ClassificationUpdater.class})
  class MultipleSpisAreDefined {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1", groups = "businessadmin")
    @Test
    void should_ApplyMultipleChanges_When_MultipleSpisAreChained() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      taskService.requestChanges(task.getId());
      Task result = taskService.getTask(task.getId());

      // changes from TaskModifier
      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifier.NEW_CUSTOM_3_VALUE);
      // changes from ClassificationUpdater
      assertThat(result.getClassificationSummary().getId())
          .isNotEqualTo(task.getClassificationSummary().getId());
      assertThat(result.getClassificationSummary().getName())
          .isEqualTo(ClassificationUpdater.SPI_CLASSIFICATION_NAME);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = BeforeRequestChangesProvider.class,
      serviceProviders = ExceptionThrower.class)
  class SpiThrowsException {
    @TaskanaInject TaskService taskService;
    @TaskanaInject TaskanaEngine taskanaEngine;
    PlainJavaTransactionProvider transactionProvider;

    @BeforeAll
    void setup(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      transactionProvider =
          new PlainJavaTransactionProvider(
              taskanaEngine, taskanaEngineConfiguration.getDatasource());
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_RollbackTransaction_When_SpiThrowsAnException() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      ThrowingCallable call =
          () ->
              transactionProvider.executeInTransaction(
                  wrap(() -> taskService.requestChanges(task.getId())));

      assertThatThrownBy(call)
          .isInstanceOf(SystemException.class)
          .getCause() // unwrap the "wrap" within "call"
          .hasMessage("service provider '%s' threw an exception", ExceptionThrower.class.getName())
          .getCause() // unwrap the "wrap" from the service provider manager
          .hasMessage("I AM THE EXCEPTION THROWER (*_*)");

      Task persistentTask = taskService.getTask(task.getId());
      assertThat(persistentTask).isEqualTo(task);
    }
  }
}
