package acceptance.task.requestchanges;

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
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.jobs.PlainJavaTransactionProvider;
import pro.taskana.spi.task.api.AfterRequestChangesProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
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
public class RequestChangesWithAfterSpiAccTest {

  private static final String NEW_WORKBASKET_KEY = "W1000";
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  WorkbasketSummary newWorkbasket;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    newWorkbasket =
        defaultTestWorkbasket().key(NEW_WORKBASKET_KEY).buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(newWorkbasket.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
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

  static class ExceptionThrower implements AfterRequestChangesProvider {

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task afterRequestChanges(Task task) throws Exception {
      task.setDescription("should not matter. Will get rolled back anyway");
      taskanaEngine.getTaskService().updateTask(task);
      throw new SystemException("I AM THE EXCEPTION THROWER (*_*)");
    }
  }

  static class TaskModifierAndTransferrer implements AfterRequestChangesProvider {
    private static final String NEW_CUSTOM_3_VALUE = "bla";

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task afterRequestChanges(Task task) throws Exception {
      task.setCustomField(TaskCustomField.CUSTOM_3, NEW_CUSTOM_3_VALUE);
      task = taskanaEngine.getTaskService().updateTask(task);
      task = taskanaEngine.getTaskService().transfer(task.getId(), NEW_WORKBASKET_KEY, "DOMAIN_A");
      return task;
    }
  }

  static class ClassificationUpdater implements AfterRequestChangesProvider {

    public static final String SPI_CLASSIFICATION_NAME = "Neuer Classification Name";

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public Task afterRequestChanges(Task task) throws Exception {
      Classification newClassification =
          defaultTestClassification().buildAndStore(taskanaEngine.getClassificationService());

      task.setClassificationKey(newClassification.getKey());
      task = taskanaEngine.getTaskService().updateTask(task);

      newClassification.setName(SPI_CLASSIFICATION_NAME);
      taskanaEngine.getClassificationService().updateClassification(newClassification);

      return task;
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = AfterRequestChangesProvider.class,
      serviceProviders = TaskModifierAndTransferrer.class)
  class SpiModifiesTask {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnModifiedTask_When_SpiModifiesAndTransfersTask() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      Task result = taskService.requestChanges(task.getId());

      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifierAndTransferrer.NEW_CUSTOM_3_VALUE);
      assertThat(result.getWorkbasketSummary()).isEqualTo(newWorkbasket);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnPersistentModifiedTask_When_SpiModifiesAndTransfersTask() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      taskService.requestChanges(task.getId());
      Task result = taskService.getTask(task.getId());

      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifierAndTransferrer.NEW_CUSTOM_3_VALUE);
      assertThat(result.getWorkbasketSummary()).isEqualTo(newWorkbasket);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = AfterRequestChangesProvider.class,
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
      serviceProviderInterface = AfterRequestChangesProvider.class,
      serviceProviders = {TaskModifierAndTransferrer.class, ClassificationUpdater.class})
  class MultipleSpisAreDefined {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1", groups = "businessadmin")
    @Test
    void should_ApplyMultipleChanges_When_MultipleSpisAreChained() throws Exception {
      Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

      taskService.requestChanges(task.getId());
      Task result = taskService.getTask(task.getId());

      // changes from TaskModifierAndTransferrer
      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifierAndTransferrer.NEW_CUSTOM_3_VALUE);
      assertThat(result.getWorkbasketSummary()).isEqualTo(newWorkbasket);
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
      serviceProviderInterface = AfterRequestChangesProvider.class,
      serviceProviders = ExceptionThrower.class)
  class SpiThrowsException {
    @TaskanaInject TaskService taskService;
    @TaskanaInject TaskanaEngine taskanaEngine;
    PlainJavaTransactionProvider transactionProvider;

    @BeforeAll
    void setup(TaskanaConfiguration taskanaConfiguration) {
      transactionProvider =
          new PlainJavaTransactionProvider(taskanaEngine, taskanaConfiguration.getDataSource());
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
