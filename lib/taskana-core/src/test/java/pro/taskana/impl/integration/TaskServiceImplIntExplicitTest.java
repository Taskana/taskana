package pro.taskana.impl.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.KeyDomain;
import pro.taskana.Task;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.DbSchemaCreator;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.JunitHelper;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.impl.configuration.TaskanaEngineTestConfiguration;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode EXPLICIT.
 *
 * @author EH
 */
@ExtendWith(JAASExtension.class)
class TaskServiceImplIntExplicitTest {

  private static DataSource dataSource;

  private static TaskServiceImpl taskServiceImpl;

  private static TaskanaEngineConfiguration taskanaEngineConfiguration;

  private static TaskanaEngine taskanaEngine;

  private static TaskanaEngineImpl taskanaEngineImpl;

  private static ClassificationService classificationService;

  private static WorkbasketService workbasketService;

  @BeforeAll
  static void setup() throws SQLException {
    String userHomeDirectory = System.getProperty("user.home");
    String propertiesFileName = userHomeDirectory + "/taskanaUnitTest.properties";

    dataSource =
        new File(propertiesFileName).exists()
            ? TaskanaEngineTestConfiguration.createDataSourceFromProperties(propertiesFileName)
            : TaskanaEngineConfiguration.createDefaultDataSource();
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(
            dataSource, false, TaskanaEngineTestConfiguration.getSchemaName());
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    classificationService = taskanaEngine.getClassificationService();
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
    workbasketService = taskanaEngine.getWorkbasketService();
    DbSchemaCreator creator =
        new DbSchemaCreator(dataSource, dataSource.getConnection().getSchema());
    creator.run();
  }

  @BeforeEach
  void resetDb() {
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testStartTransactionFail()
      throws SQLException, TaskNotFoundException, NotAuthorizedException,
          WorkbasketNotFoundException, ClassificationNotFoundException,
          ClassificationAlreadyExistException, TaskAlreadyExistException,
          InvalidWorkbasketException, InvalidArgumentException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);

    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
    workbasket.setName("workbasket");
    workbasket.setId("1"); // set id manually for authorization tests

    workbasket.setType(WorkbasketType.GROUP);
    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    taskanaEngineImpl.getWorkbasketService().createWorkbasket(workbasket);

    WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "Elena");
    accessItem.setPermAppend(true);
    accessItem.setPermRead(true);
    accessItem.setPermOpen(true);
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
    Assertions.assertThrows(
        TaskNotFoundException.class, () -> taskServiceImpl2.getTask(workbasket.getId()));
    connection.commit();
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testCreateTask()
      throws SQLException, TaskNotFoundException, NotAuthorizedException,
          WorkbasketNotFoundException, ClassificationNotFoundException,
          ClassificationAlreadyExistException, TaskAlreadyExistException,
          InvalidWorkbasketException, InvalidArgumentException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);

    Task task = this.generateDummyTask();
    connection.commit();

    WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "Elena");
    accessItem.setPermAppend(true);
    accessItem.setPermRead(true);
    accessItem.setPermOpen(true);
    workbasketService.createWorkbasketAccessItem(accessItem);

    task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
    task = taskServiceImpl.createTask(task);
    connection.commit(); // needed so that the change is visible in the other session

    TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
    TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
    Task resultTask = taskServiceImpl2.getTask(task.getId());
    assertNotNull(resultTask);
    connection.commit();
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void createTaskShouldThrowWorkbasketNotFoundException()
      throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
          InvalidWorkbasketException, InvalidArgumentException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);
    Task test = this.generateDummyTask();
    ((WorkbasketSummaryImpl) (test.getWorkbasketSummary())).setId("2");

    Assertions.assertThrows(
        WorkbasketNotFoundException.class, () -> taskServiceImpl.createTask(test));
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void createManualTaskShouldThrowClassificationNotFoundException()
      throws NotAuthorizedException, WorkbasketNotFoundException, SQLException,
          ClassificationAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
          WorkbasketAlreadyExistException, DomainNotFoundException {
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);

    Workbasket wb = workbasketService.newWorkbasket("WB NR.1", "DOMAIN_A");
    wb.setName("dummy-WB");
    wb.setType(WorkbasketType.PERSONAL);
    wb = workbasketService.createWorkbasket(wb);
    this.createWorkbasketWithSecurity(wb, CurrentUserContext.getUserid(), true, true, true, false);
    Classification classification =
        classificationService.newClassification(
            UUID.randomUUID().toString(), wb.getDomain(), "t1"); // not persisted,
    // not found.
    classification.setName("not persisted - so not found.");

    Task task = this.generateDummyTask();
    ((TaskImpl) task).setWorkbasketSummary(wb.asSummary());
    task.setClassificationKey(classification.getKey());

    Assertions.assertThrows(
        ClassificationNotFoundException.class, () -> taskServiceImpl.createTask(task));
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"DummyGroup", "businessadmin"})
  @Test
  void should_ReturnList_when_BuilderIsUsed()
      throws SQLException, NotAuthorizedException, WorkbasketNotFoundException,
          ClassificationNotFoundException, ClassificationAlreadyExistException,
          TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
          SystemException, WorkbasketAlreadyExistException, DomainNotFoundException {
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
    workbasket.setName("workbasket");
    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    classificationService.createClassification(classification);
    workbasket.setId("1"); // set id manually for authorization tests
    workbasket.setType(WorkbasketType.GROUP);
    workbasket = (WorkbasketImpl) workbasketService.createWorkbasket(workbasket);

    WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "Elena");
    accessItem.setPermAppend(true);
    accessItem.setPermRead(true);
    accessItem.setPermOpen(true);
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
            .customAttributeLike("13", "test")
            .classificationKeyIn("pId1", "pId2")
            .primaryObjectReferenceCompanyIn("first comp", "sonstwo gmbh")
            .primaryObjectReferenceSystemIn("sys")
            .primaryObjectReferenceTypeIn("type1", "type2")
            .primaryObjectReferenceSystemInstanceIn("sysInst1", "sysInst2")
            .primaryObjectReferenceValueIn("val1", "val2", "val3")
            .list();

    assertEquals(0, results.size());
    connection.commit();
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void shouldTransferTaskToOtherWorkbasket()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException,
          TaskAlreadyExistException, SQLException, InvalidWorkbasketException,
          InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException,
          InvalidStateException {
    Workbasket sourceWB;
    Workbasket destinationWB;
    WorkbasketImpl wb;
    ClassificationImpl classification;
    TaskImpl task;
    Task resultTask;
    final int sleepTime = 100;
    final String user = CurrentUserContext.getUserid();
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);

    // Source Workbasket
    wb = (WorkbasketImpl) workbasketService.newWorkbasket("sourceWbKey", "DOMAIN_A");
    wb.setName("Basic-Workbasket");
    wb.setDescription("Just used as base WB for Task here");
    wb.setOwner(user);
    wb.setType(WorkbasketType.PERSONAL);
    sourceWB = workbasketService.createWorkbasket(wb);

    createWorkbasketWithSecurity(wb, wb.getOwner(), false, false, false, false);
    createWorkbasketWithSecurity(sourceWB, sourceWB.getOwner(), true, true, true, true);

    // Destination Workbasket
    wb = (WorkbasketImpl) workbasketService.newWorkbasket("wb2Key", "DOMAIN_A");
    wb.setName("Desination-WorkBasket");
    wb.setDescription("Destination WB where Task should be transfered to");
    wb.setOwner(user);
    wb.setType(WorkbasketType.TOPIC);

    destinationWB = workbasketService.createWorkbasket(wb);
    createWorkbasketWithSecurity(destinationWB, destinationWB.getOwner(), false, true, true, true);

    // Classification required for Task
    classification =
        (ClassificationImpl) classificationService.newClassification("KEY", "DOMAIN_A", "TASK");
    classification.setCategory("EXTERNAL");
    classification.setName("Transfert-Task Classification");
    classificationService.createClassification(classification);

    // Task which should be transfered
    task = (TaskImpl) taskServiceImpl.newTask(sourceWB.getId());
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

    resultTask = taskServiceImpl.transfer(task.getId(), destinationWB.getId());
    connection.commit();
    assertThat(resultTask.isRead(), equalTo(false));
    assertThat(resultTask.isTransferred(), equalTo(true));
    assertThat(resultTask.getWorkbasketKey(), equalTo(destinationWB.getKey()));
    assertThat(resultTask.getModified(), not(equalTo(null)));
    assertThat(resultTask.getModified(), not(equalTo(task.getModified())));
    assertThat(resultTask.getCreated(), not(equalTo(null)));
    assertThat(resultTask.getCreated(), equalTo(task.getCreated()));
  }

  @Test
  void shouldNotTransferAnyTask() throws SQLException {
    Connection connection = dataSource.getConnection();
    taskanaEngineImpl.setConnection(connection);

    Assertions.assertThrows(
        TaskNotFoundException.class, () -> taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1"));
  }

  @WithAccessId(
      userName = "User",
      groupNames = {"businessadmin"})
  @Test
  void shouldNotTransferByFailingSecurity()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          ClassificationAlreadyExistException, SQLException, TaskAlreadyExistException,
          InvalidWorkbasketException, InvalidArgumentException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    final String user = "User";

    // Set up Security for this Test
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(
            dataSource, false, true, TaskanaEngineTestConfiguration.getSchemaName());
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
    classificationService = taskanaEngine.getClassificationService();
    workbasketService = taskanaEngine.getWorkbasketService();

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
    createWorkbasketWithSecurity(wbCreated, wbCreated.getOwner(), true, true, true, true);

    WorkbasketImpl wbNoAppend =
        (WorkbasketImpl) workbasketService.newWorkbasket("keyNoAppend", "DOMAIN_B");
    wbNoAppend.setName("Test-Security-WorkBasket-APPEND");
    wbNoAppend.setDescription("Workbasket without permission APPEND on Task");
    wbNoAppend.setOwner(user);

    wbNoAppend.setType(WorkbasketType.CLEARANCE);
    WorkbasketImpl wbNoAppendCreated =
        (WorkbasketImpl) workbasketService.createWorkbasket(wbNoAppend);
    createWorkbasketWithSecurity(
        wbNoAppendCreated, wbNoAppendCreated.getOwner(), true, true, false, true);

    WorkbasketImpl wbNoTransfer =
        (WorkbasketImpl) workbasketService.newWorkbasket("keyNoTransfer", "DOMAIN_A");
    wbNoTransfer.setName("Test-Security-WorkBasket-TRANSFER");
    wbNoTransfer.setDescription("Workbasket without permission TRANSFER on Task");
    wbNoTransfer.setOwner(user);
    wbNoTransfer.setType(WorkbasketType.GROUP);
    wbNoTransfer = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoTransfer);
    createWorkbasketWithSecurity(wbNoTransfer, wbNoTransfer.getOwner(), true, true, true, false);

    TaskImpl task = (TaskImpl) taskServiceImpl.newTask(wbCreated.getId());
    task.setName("Task Name");
    task.setDescription("Task used for transfer Test");
    task.setOwner(user);
    task.setClassificationKey(classification.getKey());
    task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
    TaskImpl taskCreated = (TaskImpl) taskServiceImpl.createTask(task);

    // Check failing with missing APPEND
    NotAuthorizedException e =
        Assertions.assertThrows(
            NotAuthorizedException.class,
            () -> taskServiceImpl.transfer(taskCreated.getId(), wbNoAppendCreated.getId()),
            "Transfer Task should be FAILED, because there are no APPEND-Rights on destination WB.");

    assertTrue(
        e.getMessage().contains("APPEND"),
        "Transfer Task should be FAILED, because there are no APPEND-Rights on destination WB.");

    assertThat(taskCreated.isTransferred(), equalTo(false));
    assertThat(taskCreated.getWorkbasketKey(), not(equalTo(wbNoAppendCreated.getKey())));
    assertThat(taskCreated.getWorkbasketKey(), equalTo(wbCreated.getKey()));

    // Check failing with missing TRANSFER
    taskCreated.setId("");
    taskCreated.setWorkbasketKey(wbNoTransfer.getKey());
    taskCreated.getWorkbasketSummaryImpl().setId(wbNoTransfer.getId());
    taskCreated.setExternalId(IdGenerator.generateWithPrefix("TST"));
    TaskImpl taskCreated2 = (TaskImpl) taskServiceImpl.createTask(taskCreated);
    e =
        Assertions.assertThrows(
            NotAuthorizedException.class,
            () -> taskServiceImpl.transfer(taskCreated2.getId(), wbCreated.getId()),
            "Transfer Task should be FAILED, because there are no TRANSFER-Rights on current WB.");

    assertTrue(
        e.getMessage().contains("TRANSFER"),
        "Transfer Task should be FAILED, because there are no APPEND-Rights on current WB.");
    assertThat(taskCreated2.isTransferred(), equalTo(false));
    assertThat(taskCreated2.getWorkbasketKey(), not(equalTo(wbNoAppendCreated.getKey())));
  }

  @AfterEach
  void cleanUp() throws SQLException {
    taskanaEngineImpl.setConnection(null);
  }

  private Task generateDummyTask()
      throws ClassificationAlreadyExistException, InvalidWorkbasketException,
          NotAuthorizedException, WorkbasketAlreadyExistException, DomainNotFoundException,
          InvalidArgumentException {
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

  private void createWorkbasketWithSecurity(
      Workbasket wb,
      String accessId,
      boolean permOpen,
      boolean permRead,
      boolean permAppend,
      boolean permTransfer)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(wb.getId(), accessId);
    accessItem.setPermOpen(permOpen);
    accessItem.setPermRead(permRead);
    accessItem.setPermAppend(permAppend);
    accessItem.setPermTransfer(permTransfer);
    workbasketService.createWorkbasketAccessItem(accessItem);
  }
}
