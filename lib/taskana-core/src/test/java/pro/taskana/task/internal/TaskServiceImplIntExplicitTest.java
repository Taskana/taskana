package pro.taskana.task.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode EXPLICIT.
 *
 * @author EH
 */
@ExtendWith(JaasExtension.class)
class TaskServiceImplIntExplicitTest {

  private static DataSource dataSource;
  private static SampleDataGenerator sampleDataGenerator;
  private static TaskanaEngineConfiguration taskanaEngineConfiguration;
  private TaskServiceImpl taskServiceImpl;
  private TaskanaEngine taskanaEngine;
  private TaskanaEngineImpl taskanaEngineImpl;
  private ClassificationService classificationService;
  private WorkbasketService workbasketService;

  @BeforeAll
  static void beforeAll() throws Exception {
    String userHomeDirectory = System.getProperty("user.home");
    String propertiesFileName = userHomeDirectory + "/taskanaUnitTest.properties";

    dataSource =
        new File(propertiesFileName).exists()
            ? TaskanaEngineTestConfiguration.createDataSourceFromProperties(propertiesFileName)
            : TaskanaEngineConfiguration.createDefaultDataSource();
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(
            dataSource, false, TaskanaEngineTestConfiguration.getSchemaName());
    sampleDataGenerator =
        new SampleDataGenerator(dataSource, TaskanaEngineTestConfiguration.getSchemaName());
  }

  @BeforeEach
  void setup() throws Exception {
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    classificationService = taskanaEngine.getClassificationService();
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
    workbasketService = taskanaEngine.getWorkbasketService();
    try (Connection connection = dataSource.getConnection()) {
      sampleDataGenerator.clearDb();
      DbSchemaCreator creator = new DbSchemaCreator(dataSource, connection.getSchema());
      creator.run();
    }
  }

  @AfterEach
  void tearDown() {
    sampleDataGenerator.clearDb();
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void testStartTransactionFail() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);

      WorkbasketImpl workbasket =
          (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
      workbasket.setName("workbasket");
      workbasket.setId("1"); // set id manually for authorization tests

      workbasket.setType(WorkbasketType.GROUP);
      final Classification classification =
          classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
      taskanaEngineImpl.getWorkbasketService().createWorkbasket(workbasket);

      WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "user-1-1");
      accessItem.setPermission(WorkbasketPermission.APPEND, true);
      accessItem.setPermission(WorkbasketPermission.READ, true);
      accessItem.setPermission(WorkbasketPermission.OPEN, true);
      workbasketService.createWorkbasketAccessItem(accessItem);

      taskanaEngineImpl.getClassificationService().createClassification(classification);
      connection.commit();
      Task task = taskServiceImpl.newTask(workbasket.getId());
      task.setName("Unit Test Task");
      task.setClassificationKey(classification.getKey());
      task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
      task = taskServiceImpl.createTask(task);
      connection.commit();
      taskServiceImpl.getTask(task.getId());

      TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
      TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
      assertThatThrownBy(() -> taskServiceImpl2.getTask(workbasket.getId()))
          .isInstanceOf(TaskNotFoundException.class);
      connection.commit();
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void testCreateTask() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);

      final Task task = this.generateDummyTask();
      connection.commit();

      WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "user-1-1");
      accessItem.setPermission(WorkbasketPermission.APPEND, true);
      accessItem.setPermission(WorkbasketPermission.READ, true);
      accessItem.setPermission(WorkbasketPermission.OPEN, true);
      workbasketService.createWorkbasketAccessItem(accessItem);

      task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
      final Task task2 = taskServiceImpl.createTask(task);
      connection.commit(); // needed so that the change is visible in the other session

      TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
      TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
      Task resultTask = taskServiceImpl2.getTask(task2.getId());
      assertThat(resultTask).isNotNull();
      connection.commit();
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void createTaskShouldThrowWorkbasketNotFoundException() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      Task test = this.generateDummyTask();
      ((WorkbasketSummaryImpl) (test.getWorkbasketSummary())).setId("2");

      assertThatThrownBy(() -> taskServiceImpl.createTask(test))
          .isInstanceOf(WorkbasketNotFoundException.class);
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void createManualTaskShouldThrowClassificationNotFoundException() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);

      Workbasket wb = workbasketService.newWorkbasket("WB NR.1", "DOMAIN_A");
      wb.setName("dummy-WB");
      wb.setType(WorkbasketType.PERSONAL);
      wb = workbasketService.createWorkbasket(wb);
      workbasketService.createWorkbasketAccessItem(
          this.createWorkbasketWithSecurity(
              wb, CurrentUserContext.getUserid(), true, true, true, false));
      Classification classification =
          classificationService.newClassification(
              UUID.randomUUID().toString(), wb.getDomain(), "t1"); // not persisted,
      // not found.
      classification.setName("not persisted - so not found.");

      Task task = this.generateDummyTask();
      ((TaskImpl) task).setWorkbasketSummary(wb.asSummary());
      task.setClassificationKey(classification.getKey());

      assertThatThrownBy(() -> taskServiceImpl.createTask(task))
          .isInstanceOf(ClassificationNotFoundException.class);
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void should_ReturnList_When_BuilderIsUsed() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      WorkbasketImpl workbasket =
          (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
      workbasket.setName("workbasket");
      Classification classification =
          classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
      classificationService.createClassification(classification);
      workbasket.setId("1"); // set id manually for authorization tests
      workbasket.setType(WorkbasketType.GROUP);
      workbasket = (WorkbasketImpl) workbasketService.createWorkbasket(workbasket);

      WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "user-1-1");
      accessItem.setPermission(WorkbasketPermission.APPEND, true);
      accessItem.setPermission(WorkbasketPermission.READ, true);
      accessItem.setPermission(WorkbasketPermission.OPEN, true);
      workbasketService.createWorkbasketAccessItem(accessItem);

      Task task = taskServiceImpl.newTask(workbasket.getId());
      task.setName("Unit Test Task");
      task.setClassificationKey(classification.getKey());
      task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
      taskServiceImpl.createTask(task);

      List<TaskSummary> results =
          taskServiceImpl
              .createTaskQuery()
              .nameIn("bla", "test")
              .descriptionLike("test")
              .priorityIn(1, 2, 2)
              .stateIn(TaskState.CLAIMED)
              .workbasketKeyDomainIn(new KeyDomain("k1", "DOMAIN_A"))
              .ownerIn("test", "test2", "bla")
              .customAttributeLike(TaskCustomField.CUSTOM_13, "test")
              .classificationKeyIn("pId1", "pId2")
              .primaryObjectReferenceCompanyIn("first comp", "sonstwo gmbh")
              .primaryObjectReferenceSystemIn("sys")
              .primaryObjectReferenceTypeIn("type1", "type2")
              .primaryObjectReferenceSystemInstanceIn("sysInst1", "sysInst2")
              .primaryObjectReferenceValueIn("val1", "val2", "val3")
              .list();

      assertThat(results).isEmpty();
      connection.commit();
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void shouldTransferTaskToOtherWorkbasket() throws Exception {
    final int sleepTime = 100;
    final String user = CurrentUserContext.getUserid();
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);

      // Source Workbasket
      WorkbasketImpl wb =
          (WorkbasketImpl) workbasketService.newWorkbasket("sourceWbKey", "DOMAIN_A");
      wb.setName("Basic-Workbasket");
      wb.setDescription("Just used as base WB for Task here");
      wb.setOwner(user);
      wb.setType(WorkbasketType.PERSONAL);
      Workbasket sourceWB = workbasketService.createWorkbasket(wb);

      workbasketService.createWorkbasketAccessItem(
          createWorkbasketWithSecurity(wb, wb.getOwner(), true, true, true, true));
      connection.commit();
      ThrowingCallable call =
          () ->
              workbasketService.createWorkbasketAccessItem(
                  createWorkbasketWithSecurity(
                      sourceWB, sourceWB.getOwner(), false, false, false, false));
      assertThatThrownBy(call).isInstanceOf(WorkbasketAccessItemAlreadyExistException.class);
      connection.rollback();

      // Destination Workbasket
      wb = (WorkbasketImpl) workbasketService.newWorkbasket("wb2Key", "DOMAIN_A");
      wb.setName("Desination-WorkBasket");
      wb.setDescription("Destination WB where Task should be transfered to");
      wb.setOwner(user);
      wb.setType(WorkbasketType.TOPIC);

      Workbasket destinationWB = workbasketService.createWorkbasket(wb);
      workbasketService.createWorkbasketAccessItem(
          createWorkbasketWithSecurity(
              destinationWB, destinationWB.getOwner(), false, true, true, true));

      // Classification required for Task
      ClassificationImpl classification =
          (ClassificationImpl) classificationService.newClassification("KEY", "DOMAIN_A", "TASK");
      classification.setCategory("EXTERNAL");
      classification.setName("Transfert-Task Classification");
      classificationService.createClassification(classification);

      // Task which should be transfered
      TaskImpl task = (TaskImpl) taskServiceImpl.newTask(sourceWB.getId());
      task.setName("Task Name");
      task.setDescription("Task used for transfer Test");
      task.setRead(true);
      task.setTransferred(false);
      task.setModified(null);
      task.setClassificationKey("KEY");
      task.setOwner(user);
      task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
      task = (TaskImpl) taskServiceImpl.createTask(task);
      Thread.sleep(sleepTime); // Sleep for modification-timestamp
      connection.commit();

      Task resultTask = taskServiceImpl.transfer(task.getId(), destinationWB.getId());
      connection.commit();
      assertThat(resultTask.isRead()).isFalse();
      assertThat(resultTask.isTransferred()).isTrue();
      assertThat(resultTask.getWorkbasketKey()).isEqualTo(destinationWB.getKey());
      assertThat(resultTask.getModified()).isNotNull();
      assertThat(resultTask.getModified()).isNotEqualTo(task.getModified());
      assertThat(resultTask.getCreated()).isNotNull();
      assertThat(resultTask.getCreated()).isEqualTo(task.getCreated());
    }
  }

  @Test
  void shouldNotTransferAnyTask() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);

      assertThatThrownBy(() -> taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1"))
          .isInstanceOf(TaskNotFoundException.class);
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void shouldNotTransferByFailingSecurity() throws Exception {
    final String user = "user-1-1";

    // Set up Security for this Test
    final DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    final TaskanaEngineConfiguration taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(
            dataSource, false, true, TaskanaEngineTestConfiguration.getSchemaName());
    final TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    final TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    final TaskServiceImpl taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
    final ClassificationService classificationService = taskanaEngine.getClassificationService();
    final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ClassificationImpl classification =
        (ClassificationImpl) classificationService.newClassification("KEY", "DOMAIN_A", "TASK");
    classification.setCategory("EXTERNAL");
    classification.setName("Transfert-Task Classification");
    classificationService.createClassification(classification);

    WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket("wbKey1", "DOMAIN_A");
    wb.setName("BASE WB");
    wb.setDescription("Normal base WB");
    wb.setOwner(user);
    wb.setType(WorkbasketType.GROUP);
    WorkbasketImpl wbCreated = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
    workbasketService.createWorkbasketAccessItem(
        createWorkbasketWithSecurity(wbCreated, wbCreated.getOwner(), true, true, true, true));

    WorkbasketImpl wbNoAppend =
        (WorkbasketImpl) workbasketService.newWorkbasket("keyNoAppend", "DOMAIN_B");
    wbNoAppend.setName("Test-Security-WorkBasket-APPEND");
    wbNoAppend.setDescription("Workbasket without permission APPEND on Task");
    wbNoAppend.setOwner(user);

    wbNoAppend.setType(WorkbasketType.CLEARANCE);
    WorkbasketImpl wbNoAppendCreated =
        (WorkbasketImpl) workbasketService.createWorkbasket(wbNoAppend);
    workbasketService.createWorkbasketAccessItem(
        createWorkbasketWithSecurity(
            wbNoAppendCreated, wbNoAppendCreated.getOwner(), true, true, false, true));

    WorkbasketImpl wbNoTransfer =
        (WorkbasketImpl) workbasketService.newWorkbasket("keyNoTransfer", "DOMAIN_A");
    wbNoTransfer.setName("Test-Security-WorkBasket-TRANSFER");
    wbNoTransfer.setDescription("Workbasket without permission TRANSFER on Task");
    wbNoTransfer.setOwner(user);
    wbNoTransfer.setType(WorkbasketType.GROUP);
    wbNoTransfer = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoTransfer);
    workbasketService.createWorkbasketAccessItem(
        createWorkbasketWithSecurity(
            wbNoTransfer, wbNoTransfer.getOwner(), true, true, true, false));

    TaskImpl task = (TaskImpl) taskServiceImpl.newTask(wbCreated.getId());
    task.setName("Task Name");
    task.setDescription("Task used for transfer Test");
    task.setOwner(user);
    task.setClassificationKey(classification.getKey());
    task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
    TaskImpl taskCreated = (TaskImpl) taskServiceImpl.createTask(task);

    // Check failing with missing APPEND
    ThrowingCallable call =
        () -> taskServiceImpl.transfer(taskCreated.getId(), wbNoAppendCreated.getId());
    assertThatThrownBy(call)
        .describedAs(
            "Transfer Task should be FAILED, "
                + "because there are no APPEND-Rights on destination WB.")
        .isInstanceOf(NotAuthorizedException.class)
        .hasMessageContaining("APPEND");

    assertThat(taskCreated.isTransferred()).isFalse();
    assertThat(taskCreated.getWorkbasketKey()).isNotEqualTo(wbNoAppendCreated.getKey());
    assertThat(taskCreated.getWorkbasketKey()).isEqualTo(wbCreated.getKey());

    // Check failing with missing TRANSFER
    taskCreated.setId("");
    taskCreated.setWorkbasketKey(wbNoTransfer.getKey());
    taskCreated.getWorkbasketSummaryImpl().setId(wbNoTransfer.getId());
    taskCreated.setExternalId(IdGenerator.generateWithPrefix("TST"));
    TaskImpl taskCreated2 = (TaskImpl) taskServiceImpl.createTask(taskCreated);
    assertThatThrownBy(() -> taskServiceImpl.transfer(taskCreated2.getId(), wbCreated.getId()))
        .describedAs(
            "Transfer Task should be FAILED, because there are no TRANSFER-Rights on current WB.")
        .isInstanceOf(NotAuthorizedException.class)
        .hasMessageContaining("TRANSFER");

    assertThat(taskCreated2.isTransferred()).isFalse();
    assertThat(taskCreated2.getWorkbasketKey()).isNotEqualTo(wbNoAppendCreated.getKey());
    assertThat(taskCreated2.isTransferred()).isFalse();
    assertThat(taskCreated2.getWorkbasketKey()).isNotEqualTo(wbNoAppendCreated.getKey());
  }

  @AfterEach
  void cleanUp() throws Exception {
    taskanaEngineImpl.setConnection(null);
  }

  private Task generateDummyTask() throws Exception {
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("wb", "DOMAIN_A");
    workbasket.setName("wb");
    workbasket.setId("1"); // set id manually for authorization tests
    workbasket.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    taskanaEngine.getClassificationService().createClassification(classification);

    Task task = taskServiceImpl.newTask(workbasket.getId());
    task.setClassificationKey(classification.getKey());
    return task;
  }

  private WorkbasketAccessItem createWorkbasketWithSecurity(
      Workbasket wb,
      String accessId,
      boolean permOpen,
      boolean permRead,
      boolean permAppend,
      boolean permTransfer) {
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(wb.getId(), accessId);
    accessItem.setPermission(WorkbasketPermission.OPEN, permOpen);
    accessItem.setPermission(WorkbasketPermission.READ, permRead);
    accessItem.setPermission(WorkbasketPermission.APPEND, permAppend);
    accessItem.setPermission(WorkbasketPermission.TRANSFER, permTransfer);
    return accessItem;
  }
}
