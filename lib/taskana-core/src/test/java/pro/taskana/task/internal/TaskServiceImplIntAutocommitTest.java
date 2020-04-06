package pro.taskana.task.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JunitHelper;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
@ExtendWith(JaasExtension.class)
class TaskServiceImplIntAutocommitTest {

  private DataSource dataSource;

  private TaskServiceImpl taskServiceImpl;

  private TaskanaEngine taskanaEngine;

  private TaskanaEngineImpl taskanaEngineImpl;

  private ClassificationService classificationService;

  private WorkbasketService workbasketService;

  private SampleDataGenerator sampleDataGenerator;

  private TaskanaEngineConfiguration taskanaEngineConfiguration;

  @BeforeAll
  void beforeAll() throws SQLException {
    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, false, schemaName);
  }

  @BeforeEach
  void setup() throws SQLException {
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
    classificationService = taskanaEngine.getClassificationService();
    workbasketService = taskanaEngine.getWorkbasketService();
  }

  @AfterEach
  void tearDown() {
    sampleDataGenerator.clearDb();
  }

  @Test
  void testStart()
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          ClassificationNotFoundException, ClassificationAlreadyExistException,
          TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
          WorkbasketAlreadyExistException, DomainNotFoundException {

    Workbasket wb = workbasketService.newWorkbasket("workbasket", "DOMAIN_A");
    wb.setName("workbasket");
    wb.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(wb);

    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    taskanaEngine.getClassificationService().createClassification(classification);

    Task task = taskServiceImpl.newTask(wb.getId());
    task.setName("Unit Test Task");

    task.setClassificationKey(classification.getKey());
    task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
    task = taskServiceImpl.createTask(task);

    TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
    TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
    Task resultTask = taskServiceImpl2.getTask(task.getId());
    assertNotNull(resultTask);
  }

  @Test
  void testStartTransactionFail()
      throws TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException,
          ClassificationNotFoundException, ClassificationAlreadyExistException,
          TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
          WorkbasketAlreadyExistException, DomainNotFoundException {

    Workbasket wb = workbasketService.newWorkbasket("wb1k1", "DOMAIN_A");
    wb.setName("sdf");
    wb.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(wb);

    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    classification = taskanaEngine.getClassificationService().createClassification(classification);
    classification =
        taskanaEngine
            .getClassificationService()
            .getClassification(classification.getKey(), classification.getDomain());

    TaskImpl task = (TaskImpl) taskServiceImpl.newTask(wb.getKey(), "DOMAIN_A");
    task.setName("Unit Test Task");
    task.setClassificationKey(classification.getKey());
    task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
    taskServiceImpl.createTask(task);
    taskServiceImpl.getTask(task.getId());

    TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
    TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();

    Assertions.assertThrows(
        TaskNotFoundException.class, () -> taskServiceImpl2.getTask(wb.getId()));
  }

  @Test
  void should_ReturnList_when_BuilderIsUsed()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          ClassificationAlreadyExistException, TaskAlreadyExistException,
          InvalidWorkbasketException, InvalidArgumentException, SystemException,
          WorkbasketAlreadyExistException, DomainNotFoundException {
    Workbasket wb = workbasketService.newWorkbasket("key", "DOMAIN_A");
    wb.setName("workbasket");
    wb.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(wb);
    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    taskanaEngine.getClassificationService().createClassification(classification);

    Task task = taskServiceImpl.newTask(wb.getKey(), wb.getDomain());
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
            .workbasketKeyDomainIn(
                new KeyDomain("asd", "novatec"), new KeyDomain("asdasdasd", "DOMAIN_A"))
            .ownerIn("test", "test2", "bla")
            .customAttributeIn("16", "test")
            .classificationKeyIn("pId1", "pId2")
            .primaryObjectReferenceCompanyIn("first comp", "sonstwo gmbh")
            .primaryObjectReferenceSystemIn("sys")
            .primaryObjectReferenceTypeIn("type1", "type2")
            .primaryObjectReferenceSystemInstanceIn("sysInst1", "sysInst2")
            .primaryObjectReferenceValueIn("val1", "val2", "val3")
            .list();

    assertEquals(0, results.size());
  }

  @Test
  void shouldTransferTaskToOtherWorkbasket()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException,
          TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
          WorkbasketAlreadyExistException, DomainNotFoundException, InvalidStateException {
    final int sleepTime = 100;

    // Source Workbasket
    WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket("key1", "DOMAIN_A");
    wb.setName("Basic-Workbasket");
    wb.setDescription("Just used as base WB for Task here");
    wb.setType(WorkbasketType.GROUP);
    wb.setOwner("The Tester ID");
    final Workbasket sourceWB = workbasketService.createWorkbasket(wb);

    // Destination Workbasket
    wb = (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
    wb.setName("Desination-WorkBasket");

    wb.setType(WorkbasketType.CLEARANCE);
    wb.setDescription("Destination WB where Task should be transfered to");
    wb.setOwner("The Tester ID");
    final Workbasket destinationWB = workbasketService.createWorkbasket(wb);

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
    task.setClassificationKey(classification.getKey());
    task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
    task = (TaskImpl) taskServiceImpl.createTask(task);
    Thread.sleep(sleepTime); // Sleep for modification-timestamp

    Task resultTask = taskServiceImpl.transfer(task.getId(), destinationWB.getId());
    assertThat(resultTask.isRead(), equalTo(false));
    assertThat(resultTask.isTransferred(), equalTo(true));
    assertThat(resultTask.getWorkbasketSummary().getId(), equalTo(destinationWB.getId()));
    assertThat(resultTask.getModified(), not(equalTo(null)));
    assertThat(resultTask.getModified(), not(equalTo(task.getModified())));
    assertThat(resultTask.getCreated(), not(equalTo(null)));
    assertThat(resultTask.getCreated(), equalTo(task.getCreated()));
  }

  @Test
  void shouldNotTransferAnyTask() {

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
          DomainNotFoundException, WorkbasketAccessItemAlreadyExistException {
    final String user = CurrentUserContext.getUserid();

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

    WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket("k5", "DOMAIN_A");
    wb.setName("BASE WB");
    wb.setDescription("Normal base WB");
    wb.setOwner(user);
    wb.setType(WorkbasketType.TOPIC);
    WorkbasketImpl wbCreated = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
    createWorkbasketWithSecurity(wbCreated, wbCreated.getOwner(), true, true, true, true);

    WorkbasketImpl wbNoAppend =
        (WorkbasketImpl) workbasketService.newWorkbasket("key77", "DOMAIN_A");
    wbNoAppend.setName("Test-Security-WorkBasket-APPEND");
    wbNoAppend.setDescription("Workbasket without permission APPEND on Task");
    wbNoAppend.setOwner(user);
    wbNoAppend.setType(WorkbasketType.PERSONAL);

    WorkbasketImpl wbNoAppendCreated =
        (WorkbasketImpl) workbasketService.createWorkbasket(wbNoAppend);
    createWorkbasketWithSecurity(
        wbNoAppendCreated, wbNoAppendCreated.getOwner(), true, true, false, true);

    WorkbasketImpl wbNoTransfer =
        (WorkbasketImpl) workbasketService.newWorkbasket("k99", "DOMAIN_B");
    wbNoTransfer.setName("Test-Security-WorkBasket-TRANSFER");
    wbNoTransfer.setDescription("Workbasket without permission TRANSFER on Task");
    wbNoTransfer.setOwner(user);
    wbNoTransfer.setType(WorkbasketType.CLEARANCE);

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
            "Transfer Task should be FAILD, because there are no APPEND-Rights on destination WB.");

    Assertions.assertTrue(
        e.getMessage().contains("APPEND"),
        "Transfer Task should be FAILD, because there are no APPEND-Rights on destination WB.");
    assertThat(taskCreated.isTransferred(), equalTo(false));
    assertThat(taskCreated.getWorkbasketKey(), not(equalTo(wbNoAppendCreated.getKey())));
    assertThat(taskCreated.getWorkbasketKey(), equalTo(wbCreated.getKey()));

    // Check failing with missing TRANSFER
    taskCreated.setId("");
    taskCreated.getWorkbasketSummaryImpl().setId(wbNoTransfer.getId());
    taskCreated.setWorkbasketKey(null);
    taskCreated.setExternalId(IdGenerator.generateWithPrefix("TST"));

    TaskImpl taskCreated2 = (TaskImpl) taskServiceImpl.createTask(taskCreated);

    e =
        Assertions.assertThrows(
            NotAuthorizedException.class,
            () -> taskServiceImpl.transfer(taskCreated2.getId(), wbCreated.getId()),
            "Transfer Task should be FAILED, because there are no TRANSFER-Rights on current WB.");
    Assertions.assertTrue(
        e.getMessage().contains("TRANSFER"),
        "Transfer Task should be FAILED, because there are no APPEND-Rights on current WB.");

    assertThat(taskCreated2.isTransferred(), equalTo(false));
    assertThat(taskCreated2.getWorkbasketKey(), not(equalTo(wbNoAppendCreated.getKey())));
  }

  @Test
  void testWithPrimaryObjectRef()
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          ClassificationNotFoundException, ClassificationAlreadyExistException,
          TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
          WorkbasketAlreadyExistException, DomainNotFoundException {
    Workbasket wb = workbasketService.newWorkbasket("workbasket", "DOMAIN_A");
    wb.setName("workbasket");
    wb.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(wb);
    Classification classification =
        classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
    taskanaEngine.getClassificationService().createClassification(classification);

    Task task = taskServiceImpl.newTask(wb.getId());
    task.setName("Unit Test Task");

    task.setClassificationKey(classification.getKey());

    ObjectReference objRef = new ObjectReference();
    objRef.setCompany("novatec");
    objRef.setSystem("linux");
    objRef.setSystemInstance("inst1");
    objRef.setType("fast");
    objRef.setValue("4711");
    task.setPrimaryObjRef(objRef);

    task = taskServiceImpl.createTask(task);

    Task task2 = taskServiceImpl.getTask(task.getId());
    // skanaEngineImpl.getSqlSession().commit(); // needed so that the change is visible in the
    // other session

    assertNotNull(task2);
  }

  private void createWorkbasketWithSecurity(
      Workbasket wb,
      String accessId,
      boolean permOpen,
      boolean permRead,
      boolean permAppend,
      boolean permTransfer)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(wb.getId(), accessId);
    accessItem.setPermOpen(permOpen);
    accessItem.setPermRead(permRead);
    accessItem.setPermAppend(permAppend);
    accessItem.setPermTransfer(permTransfer);
    workbasketService.createWorkbasketAccessItem(accessItem);
  }
}
