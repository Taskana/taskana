package acceptance.task.requestreview;

import static io.kadai.common.internal.util.CheckedSupplier.wrap;
import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.PlainJavaTransactionProvider;
import io.kadai.spi.task.api.AfterRequestReviewProvider;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@KadaiIntegrationTest
public class RequestReviewWithAfterSpiAccTest {

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
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(newWorkbasket.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = DefaultTestEntities.defaultTestObjectReference().build();
  }

  private TaskBuilder createTaskClaimedByUser(String owner) {
    return createDefaultTask().owner(owner).state(TaskState.CLAIMED).claimed(Instant.now());
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }

  static class ExceptionThrower implements AfterRequestReviewProvider {

    private KadaiEngine kadaiEngine;

    @Override
    public void initialize(KadaiEngine kadaiEngine) {
      this.kadaiEngine = kadaiEngine;
    }

    @Override
    public Task afterRequestReview(Task task) throws Exception {
      task.setDescription("should not matter. Will get rolled back anyway");
      kadaiEngine.getTaskService().updateTask(task);
      throw new SystemException("I AM THE EXCEPTION THROWER (*_*)");
    }
  }

  static class TaskModifierAndTransferrer implements AfterRequestReviewProvider {
    private static final String NEW_CUSTOM_3_VALUE = "bla";

    private KadaiEngine kadaiEngine;

    @Override
    public void initialize(KadaiEngine kadaiEngine) {
      this.kadaiEngine = kadaiEngine;
    }

    @Override
    public Task afterRequestReview(Task task) throws Exception {
      task.setCustomField(TaskCustomField.CUSTOM_3, NEW_CUSTOM_3_VALUE);
      task = kadaiEngine.getTaskService().updateTask(task);
      task = kadaiEngine.getTaskService().transfer(task.getId(), NEW_WORKBASKET_KEY, "DOMAIN_A");
      return task;
    }
  }

  static class ClassificationUpdater implements AfterRequestReviewProvider {

    public static final String SPI_CLASSIFICATION_NAME = "Neuer Classification Name";

    private KadaiEngine kadaiEngine;

    @Override
    public void initialize(KadaiEngine kadaiEngine) {
      this.kadaiEngine = kadaiEngine;
    }

    @Override
    public Task afterRequestReview(Task task) throws Exception {
      Classification newClassification =
          defaultTestClassification().buildAndStore(kadaiEngine.getClassificationService());

      task.setClassificationKey(newClassification.getKey());
      task = kadaiEngine.getTaskService().updateTask(task);

      newClassification.setName(SPI_CLASSIFICATION_NAME);
      kadaiEngine.getClassificationService().updateClassification(newClassification);

      return task;
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = AfterRequestReviewProvider.class,
      serviceProviders = TaskModifierAndTransferrer.class)
  class SpiModifiesTask {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnModifiedTask_When_SpiModifiesAndTransfersTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      Task result = taskService.requestReview(task.getId());

      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifierAndTransferrer.NEW_CUSTOM_3_VALUE);
      assertThat(result.getWorkbasketSummary()).isEqualTo(newWorkbasket);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ReturnPersistentModifiedTask_When_SpiModifiesAndTransfersTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      taskService.requestReview(task.getId());
      Task result = taskService.getTask(task.getId());

      assertThat(result.getCustomField(TaskCustomField.CUSTOM_3))
          .isEqualTo(TaskModifierAndTransferrer.NEW_CUSTOM_3_VALUE);
      assertThat(result.getWorkbasketSummary()).isEqualTo(newWorkbasket);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = AfterRequestReviewProvider.class,
      serviceProviders = ClassificationUpdater.class)
  class SpiModifiesTaskAndClassification {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1", groups = "businessadmin")
    @Test
    void should_ChangeMultipleEntities_When_SpiModifiesMoreThanTheTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      taskService.requestReview(task.getId());
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
      serviceProviderInterface = AfterRequestReviewProvider.class,
      serviceProviders = {TaskModifierAndTransferrer.class, ClassificationUpdater.class})
  class MultipleSpisAreDefined {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1", groups = "businessadmin")
    @Test
    void should_ApplyMultipleChanges_When_MultipleSpisAreChained() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      taskService.requestReview(task.getId());
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
      serviceProviderInterface = AfterRequestReviewProvider.class,
      serviceProviders = ExceptionThrower.class)
  class SpiThrowsException {
    @KadaiInject TaskService taskService;
    @KadaiInject KadaiEngine kadaiEngine;
    PlainJavaTransactionProvider transactionProvider;

    @BeforeAll
    void setup(KadaiConfiguration kadaiConfiguration) {
      transactionProvider =
          new PlainJavaTransactionProvider(kadaiEngine, kadaiConfiguration.getDataSource());
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_RollbackTransaction_When_SpiThrowsAnException() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      ThrowingCallable call =
          () ->
              transactionProvider.executeInTransaction(
                  wrap(() -> taskService.requestReview(task.getId())));

      assertThatThrownBy(call)
          .isInstanceOf(SystemException.class)
          .cause() // unwrap the "wrap" within "call"
          .hasMessage("service provider '%s' threw an exception", ExceptionThrower.class.getName())
          .cause() // unwrap the "wrap" from the service provider manager
          .hasMessage("I AM THE EXCEPTION THROWER (*_*)");

      Task persistentTask = taskService.getTask(task.getId());
      assertThat(persistentTask).isEqualTo(task);
    }
  }
}
