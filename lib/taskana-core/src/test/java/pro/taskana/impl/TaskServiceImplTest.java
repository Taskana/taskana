package pro.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.Workbasket;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;

/**
 * Unit Test for TaskServiceImpl.
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskServiceImplTest {

    private static final int SLEEP_TIME = 100;
    @InjectMocks
    TaskServiceImpl taskServiceImpl;
    @Mock
    TaskanaEngine taskanaEngine;
    @Mock
    TaskanaEngineConfiguration taskanaEngineConfiguration;
    @Mock
    WorkbasketServiceImpl workbasketServiceImpl;
    @Mock
    TaskMapper taskMapper;
    @Mock
    ObjectReferenceMapper objectReferenceMapper;

    @Test
    public void testCreateSimpleTask() throws NotAuthorizedException {
        registerBasicMocks(false);
        Mockito.doNothing().when(workbasketServiceImpl).checkAuthorization(any(), any());
        Mockito.doNothing().when(taskMapper).insert(any());

        Task task = new Task();
        task.setName("Unit Test Task");
        task.setWorkbasketId("1");
        task = taskServiceImpl.create(task);
        Assert.assertNull(task.getOwner());
        Assert.assertNotNull(task.getCreated());
        Assert.assertNotNull(task.getModified());
        Assert.assertNull(task.getCompleted());
        Assert.assertEquals(task.getWorkbasketId(), "1");
        Assert.assertEquals(task.getState(), TaskState.READY);
    }

    @Test
    public void testClaim() throws Exception {
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");

        Thread.sleep(SLEEP_TIME); // to have different timestamps
        taskServiceImpl.claim(task.getId(), "John Does");
        task = taskServiceImpl.getTaskById(task.getId());
        Assert.assertEquals(task.getState(), TaskState.CLAIMED);
        Assert.assertNotEquals(task.getCreated(), task.getModified());
        Assert.assertNotNull(task.getClaimed());
        Assert.assertEquals(task.getOwner(), "John Does");
    }

    @Test(expected = TaskNotFoundException.class)
    public void testClaimFailsWithNonExistingTaskId() throws TaskNotFoundException {
        taskServiceImpl.claim("test", "John Doe");
    }

    @Test
    public void testComplete() throws Exception {
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");

        Thread.sleep(SLEEP_TIME); // to have different timestamps
        taskServiceImpl.complete(task.getId());
        task = taskServiceImpl.getTaskById(task.getId());
        Assert.assertEquals(task.getState(), TaskState.COMPLETED);
        Assert.assertNotEquals(task.getCreated(), task.getModified());
        Assert.assertNotNull(task.getCompleted());
    }

    @Test(expected = TaskNotFoundException.class)
    public void testCompleteFailsWithNonExistingTaskId() throws TaskNotFoundException {
        taskServiceImpl.complete("test");
    }

    @Test
    public void testTransferTaskZuDestinationWorkbasket()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
        registerBasicMocks(false);
        Workbasket workbasket2 = createWorkbasket2();
        Mockito.when(workbasketServiceImpl.getWorkbasket("2")).thenReturn(workbasket2);

        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        task.setRead(true);

        Assert.assertEquals(taskServiceImpl.getTaskById(task.getId()).getWorkbasketId(), "1");
        taskServiceImpl.transfer(task.getId(), "2");
        Assert.assertEquals(taskServiceImpl.getTaskById(task.getId()).getWorkbasketId(), "2");

        Assert.assertTrue(task.isTransferred());
        Assert.assertFalse(task.isRead());
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testTransferFailsIfDestinationWorkbasketDoesNotExist_withSecurityDisabled()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
        registerBasicMocks(false);
        Mockito.doThrow(WorkbasketNotFoundException.class).when(workbasketServiceImpl)
                .checkAuthorization(eq("invalidWorkbasketId"), any());

        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");

        Assert.assertEquals(taskServiceImpl.getTaskById(task.getId()).getWorkbasketId(), "1");
        taskServiceImpl.transfer(task.getId(), "invalidWorkbasketId");
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testTransferFailsIfDestinationWorkbasketDoesNotExist_withSecurityEnabled()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
        registerBasicMocks(true);
        Mockito.doThrow(WorkbasketNotFoundException.class).when(workbasketServiceImpl)
                .checkAuthorization(eq("invalidWorkbasketId"), any());

        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");

        Assert.assertEquals(taskServiceImpl.getTaskById(task.getId()).getWorkbasketId(), "1");
        taskServiceImpl.transfer(task.getId(), "invalidWorkbasketId");
    }

    @Test
    public void should_setTheReadFlag_when_taskIsRead() throws TaskNotFoundException {
        createUnitTestTask("1", "Unit Test Task 1", "1");

        Task readTask = taskServiceImpl.setTaskRead("1", true);
        Assert.assertTrue(readTask.isRead());
    }

    @Test
    public void should_InsertObjectReference_when_TaskIsCreated() throws NotAuthorizedException {
        Mockito.when(taskanaEngine.getWorkbasketService()).thenReturn(workbasketServiceImpl);
        Mockito.doNothing().when(workbasketServiceImpl).checkAuthorization(any(), any());
        Mockito.when(objectReferenceMapper.findByObjectReference(any())).thenReturn(null);

        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        ObjectReference primaryObjRef = new ObjectReference();
        primaryObjRef.setSystem("Sol");
        task.setPrimaryObjRef(primaryObjRef);
        Task createdTask = taskServiceImpl.create(task);

        Assert.assertNotNull(createdTask.getPrimaryObjRef());
        Assert.assertNotNull(createdTask.getPrimaryObjRef().getId());
        Assert.assertEquals("Sol", createdTask.getPrimaryObjRef().getSystem());
    }

    @Test
    public void should_LinkObjectReference_when_TaskIsCreated() throws NotAuthorizedException {
        Mockito.when(taskanaEngine.getWorkbasketService()).thenReturn(workbasketServiceImpl);
        Mockito.doNothing().when(workbasketServiceImpl).checkAuthorization(any(), any());

        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        ObjectReference primaryObjRef = new ObjectReference();
        primaryObjRef.setSystem("Sol");
        task.setPrimaryObjRef(primaryObjRef);

        ObjectReference returnPrimaryObjRef = new ObjectReference();
        returnPrimaryObjRef.setId("1");
        returnPrimaryObjRef.setSystem("Sol");

        Mockito.when(objectReferenceMapper.findByObjectReference(any())).thenReturn(returnPrimaryObjRef);
        Task createdTask = taskServiceImpl.create(task);

        Assert.assertNotNull(createdTask.getPrimaryObjRef());
        Assert.assertEquals("1", createdTask.getPrimaryObjRef().getId());
        Assert.assertEquals("Sol", createdTask.getPrimaryObjRef().getSystem());
    }

    private Task createUnitTestTask(String id, String name, String workbasketId) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setWorkbasketId(workbasketId);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        task.setCreated(now);
        task.setModified(now);
        Mockito.when(taskMapper.findById(any())).thenReturn(task);
        return task;
    }

    private Workbasket createWorkbasket2() {
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("2");
        workbasket2.setName("Workbasket 2");
        return workbasket2;
    }

    private void registerBasicMocks(boolean securityEnabled) {
        Mockito.when(taskanaEngine.getConfiguration()).thenReturn(taskanaEngineConfiguration);
        Mockito.when(taskanaEngineConfiguration.isSecurityEnabled()).thenReturn(securityEnabled);
        Mockito.when(taskanaEngine.getWorkbasketService()).thenReturn(workbasketServiceImpl);
    }

}
