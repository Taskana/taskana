package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.ClassificationImpl;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAuthorization;
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
    private TaskServiceImpl cut;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineImpl;

    @Mock
    private TaskMapper taskMapperMock;

    @Mock
    private ObjectReferenceMapper objectReferenceMapperMock;

    @Mock
    private WorkbasketService workbasketServiceMock;

    @Mock
    private ClassificationService classificationServiceMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(workbasketServiceMock).when(taskanaEngineMock).getWorkbasketService();
        doReturn(classificationServiceMock).when(taskanaEngineMock).getClassificationService();
        try {
            Mockito.doNothing().when(workbasketServiceMock).checkAuthorization(any(), any());
        } catch (NotAuthorizedException e) {
             e.printStackTrace();
        }
        Mockito.doNothing().when(taskanaEngineImpl).openConnection();
        Mockito.doNothing().when(taskanaEngineImpl).returnConnection();
    }

    @Test
    public void testCreateSimpleTask() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Mockito.doNothing().when(taskMapperMock).insert(any());
        Task expectedTask = createUnitTestTask("1", "DUMMYTASK", "1");
        Workbasket wb = new Workbasket();
        wb.setId("1");
        wb.setName("workbasket");
        doReturn(wb).when(workbasketServiceMock).getWorkbasket(wb.getId());

        Task actualTask = cut.createTask(expectedTask);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskanaEngineMock, times(2)).getWorkbasketService();
        verify(taskanaEngineMock, times(1)).getClassificationService();
        verify(workbasketServiceMock, times(1)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(classificationServiceMock, times(1)).createClassification(any());
        verify(classificationServiceMock, times(1)).getClassification(any(), any());
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
                classificationServiceMock);

        assertNull(actualTask.getOwner());
        assertNotNull(actualTask.getCreated());
        assertNotNull(actualTask.getModified());
        assertNull(actualTask.getCompleted());
        assertThat(actualTask.getWorkbasketId(), equalTo(expectedTask.getWorkbasketId()));
        assertThat(actualTask.getName(), equalTo(expectedTask.getName()));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
    }

    @Test
    public void testCreateSimpleTaskWithObjectReference() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        ObjectReference expectedObjectReference = new ObjectReference();
        expectedObjectReference.setId("1");
        expectedObjectReference.setType("DUMMY");
        Workbasket wb = new Workbasket();
        wb.setId("1");
        wb.setName("workbasket");
        doReturn(wb).when(workbasketServiceMock).getWorkbasket(wb.getId());

        Task expectedTask = createUnitTestTask("1", "DUMMYTASK", "1");
        expectedTask.setPrimaryObjRef(new ObjectReference());

        Mockito.doNothing().when(taskMapperMock).insert(any());
        Mockito.doReturn(expectedObjectReference).when(objectReferenceMapperMock).findByObjectReference(any());

        Task actualTask = cut.createTask(expectedTask);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskanaEngineMock, times(2)).getWorkbasketService();
        verify(taskanaEngineMock, times(1)).getClassificationService();
        verify(workbasketServiceMock, times(1)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verify(objectReferenceMapperMock, times(1)).findByObjectReference(any());
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(classificationServiceMock, times(1)).createClassification(any());
        verify(classificationServiceMock, times(1)).getClassification(any(),
                any());
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
                classificationServiceMock);

        assertNull(actualTask.getOwner());
        assertNotNull(actualTask.getCreated());
        assertNotNull(actualTask.getModified());
        assertNull(actualTask.getCompleted());
        assertThat(actualTask.getWorkbasketId(), equalTo(expectedTask.getWorkbasketId()));
        assertThat(actualTask.getName(), equalTo(expectedTask.getName()));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
        assertThat(actualTask.getPrimaryObjRef(), equalTo(expectedObjectReference));
    }

    @Test
    public void testCreateSimpleTaskWithObjectReferenceIsNull() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        ObjectReference expectedObjectReference = new ObjectReference();
        expectedObjectReference.setId("1");
        expectedObjectReference.setType("DUMMY");
        Workbasket wb = new Workbasket();
        wb.setId("1");
        wb.setName("workbasket");
        doReturn(wb).when(workbasketServiceMock).getWorkbasket(wb.getId());
        Task expectedTask = createUnitTestTask("1", "DUMMYTASK", "1");
        expectedTask.setPrimaryObjRef(expectedObjectReference);

        Mockito.doNothing().when(taskMapperMock).insert(any());
        Mockito.doNothing().when(objectReferenceMapperMock).insert(any());
        Mockito.doReturn(null).when(objectReferenceMapperMock).findByObjectReference(any());

        Task actualTask = cut.createTask(expectedTask);
        expectedTask.getPrimaryObjRef().setId(actualTask.getPrimaryObjRef().getId());   // get only new ID

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskanaEngineMock, times(2)).getWorkbasketService();
        verify(taskanaEngineMock, times(1)).getClassificationService();
        verify(workbasketServiceMock, times(1)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verify(classificationServiceMock, times(1)).createClassification(any());
        verify(classificationServiceMock, times(1)).getClassification(any(), any());
        verify(objectReferenceMapperMock, times(1)).findByObjectReference(any());
        verify(objectReferenceMapperMock, times(1)).insert(any());
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
                classificationServiceMock);

        assertNull(actualTask.getOwner());
        assertNotNull(actualTask.getCreated());
        assertNotNull(actualTask.getModified());
        assertNull(actualTask.getCompleted());
        assertThat(actualTask.getWorkbasketId(), equalTo(expectedTask.getWorkbasketId()));
        assertThat(actualTask.getName(), equalTo(expectedTask.getName()));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
        assertThat(actualTask.getPrimaryObjRef(), equalTo(expectedObjectReference));
    }

    @Test
    public void testCreateTaskWithPlanned() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        ObjectReference expectedObjectReference = new ObjectReference();
        expectedObjectReference.setId("1");
        expectedObjectReference.setType("DUMMY");

        Classification classification = (Classification) new ClassificationImpl();
        classification.setName("Name");
        classification.setCategory("MANUAL");
        Workbasket wb = new Workbasket();
        wb.setId("workbasketId");
        wb.setName("workbasket");
        doReturn(wb).when(workbasketServiceMock).getWorkbasket(wb.getId());
        Mockito.doReturn(classification).when(classificationServiceMock).getClassification(any(), any());
        Mockito.doNothing().when(taskMapperMock).insert(any());
        Mockito.doNothing().when(objectReferenceMapperMock).insert(any());

        Task test = new Task();
        test.setWorkbasketId("workbasketId");
        test.setClassification(classification);
        test.setPrimaryObjRef(expectedObjectReference);
        test.setDescription("simply awesome task");

        cut.createTask(test);

        Task test2 = new Task();
        test2.setWorkbasketId("workbasketId");
        test2.setClassification(classification);
        test2.setPrimaryObjRef(expectedObjectReference);
        test2.setPlanned(Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
        test2.setName("Task2");

        cut.createTask(test2);

        verify(taskanaEngineImpl, times(2)).openConnection();
        verify(taskanaEngineMock, times(2 + 2)).getWorkbasketService();
        verify(taskanaEngineMock, times(2)).getClassificationService();
        verify(workbasketServiceMock, times(2)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(2)).getWorkbasket(any());
        verify(classificationServiceMock, times(2)).getClassification(any(), any());
        verify(objectReferenceMapperMock, times(2)).findByObjectReference(any());
        verify(objectReferenceMapperMock, times(2)).insert(any());
        verify(taskMapperMock, times(1)).insert(test);
        verify(taskMapperMock, times(1)).insert(test2);
        verify(taskanaEngineImpl, times(2)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
                classificationServiceMock);

        assertNull(test.getOwner());
        assertNotNull(test.getCreated());
        assertNotNull(test.getModified());
        assertNull(test.getCompleted());
        assertNull(test.getDue());
        assertThat(test.getWorkbasketId(), equalTo(test2.getWorkbasketId()));
        assertThat(test.getName(), equalTo(classification.getName()));
        assertThat(test.getState(), equalTo(TaskState.READY));
        assertThat(test.getPrimaryObjRef(), equalTo(expectedObjectReference));
        assertThat(test.getName(), not(test2.getName()));
        assertThat(test.getPlanned(), not(test2.getPlanned()));
        assertThat(test2.getPlanned(), not(test2.getCreated()));

    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreateThrowingAuthorizedOnWorkbasket() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        try {
            Mockito.doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(any(), any());
            Task task = new Task();
            task.setWorkbasketId("1");
        task.setBusinessProcessId("BPI1");
        task.setParentBusinessProcessId("PBPI1");

            cut.createTask(task);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineMock, times(2)).getWorkbasketService();
            verify(workbasketServiceMock, times(1)).getWorkbasket(any());
            verify(workbasketServiceMock, times(1)).checkAuthorization(any(), any());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
                    classificationServiceMock);
            throw e;
        }
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testCreateThrowsWorkbasketNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        try {
            Mockito.doThrow(WorkbasketNotFoundException.class).when(workbasketServiceMock).getWorkbasket(any());
            Task task = new Task();
            task.setWorkbasketId("1");

            cut.createTask(task);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineMock, times(1)).getWorkbasketService();
            verify(workbasketServiceMock, times(1)).getWorkbasket(any());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
                    classificationServiceMock);
            throw e;
        }
    }

    @Test
    public void testClaimSuccessfulToOwner() throws Exception {
        Task expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1");
        Mockito.doReturn(expectedTask).when(taskMapperMock).findById(expectedTask.getId());
        Thread.sleep(SLEEP_TIME); // to have different timestamps
        String expectedOwner = "John Does";

        Task acturalTask = cut.claim(expectedTask.getId(), expectedOwner, true);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findById(expectedTask.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertThat(acturalTask.getState(), equalTo(TaskState.CLAIMED));
        assertThat(acturalTask.getCreated(), not(equalTo(expectedTask.getModified())));
        assertThat(acturalTask.getClaimed(), not(equalTo(null)));
        assertThat(acturalTask.getOwner(), equalTo(expectedOwner));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testClaimThrowinTaskNotFoundException() throws Exception {
        try {
            Task expectedTask = null;
            Mockito.doReturn(expectedTask).when(taskMapperMock).findById(any());

            cut.claim("1", "OWNER", true);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskMapperMock, times(1)).findById(any());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test
    public void testCompleteTask() throws TaskNotFoundException, InterruptedException, ClassificationAlreadyExistException {
        Task expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1");
        Thread.sleep(SLEEP_TIME); // to have different timestamps
        Mockito.doReturn(expectedTask).when(taskMapperMock).findById(expectedTask.getId());

        Task actualTask = cut.complete(expectedTask.getId());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findById(expectedTask.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(expectedTask.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testCompleteFailsWithNonExistingTaskId() throws TaskNotFoundException {
        String invalidTaskId = "";
        try {
            cut.complete(invalidTaskId);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskMapperMock, times(1)).findById(invalidTaskId);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test
    public void testTransferTaskToDestinationWorkbasketWithoutSecurity()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Workbasket destinationWorkbasket = createWorkbasket("2");
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        final int workServiceMockCalls = 3;
        task.setRead(true);
        doReturn(destinationWorkbasket).when(workbasketServiceMock).getWorkbasket(destinationWorkbasket.getId());
        doReturn(taskanaEngineConfigurationMock).when(taskanaEngineMock).getConfiguration();
        doReturn(false).when(taskanaEngineConfigurationMock).isSecurityEnabled();
        doReturn(task).when(cutSpy).getTaskById(task.getId());
        doNothing().when(taskMapperMock).update(any());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasket.getId(), WorkbasketAuthorization.APPEND);
        doNothing().when(workbasketServiceMock).checkAuthorization(task.getId(), WorkbasketAuthorization.TRANSFER);

        Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getId());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskanaEngineMock, times(workServiceMockCalls)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasket.getId(), WorkbasketAuthorization.APPEND);
        verify(workbasketServiceMock, times(1)).checkAuthorization(task.getId(), WorkbasketAuthorization.TRANSFER);
        verify(taskanaEngineMock, times(1)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(1)).isSecurityEnabled();
        verify(workbasketServiceMock, times(1)).getWorkbasket(destinationWorkbasket.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertThat(actualTask.isRead(), equalTo(false));
        assertThat(actualTask.isTransferred(), equalTo(true));
        assertThat(actualTask.getWorkbasketId(), equalTo(destinationWorkbasket.getId()));
    }

    @Test
    public void testTransferTaskToDestinationWorkbasketUsingSecurityTrue()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Workbasket destinationWorkbasket = createWorkbasket("2");
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        task.setRead(true);
        doReturn(taskanaEngineConfigurationMock).when(taskanaEngineMock).getConfiguration();
        doReturn(true).when(taskanaEngineConfigurationMock).isSecurityEnabled();
        doReturn(task).when(cutSpy).getTaskById(task.getId());
        doNothing().when(taskMapperMock).update(any());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasket.getId(), WorkbasketAuthorization.APPEND);
        doNothing().when(workbasketServiceMock).checkAuthorization(task.getId(), WorkbasketAuthorization.TRANSFER);

        Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getId());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskanaEngineMock, times(2)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasket.getId(), WorkbasketAuthorization.APPEND);
        verify(workbasketServiceMock, times(1)).checkAuthorization(task.getId(), WorkbasketAuthorization.TRANSFER);
        verify(taskanaEngineMock, times(1)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(1)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertThat(actualTask.isRead(), equalTo(false));
        assertThat(actualTask.isTransferred(), equalTo(true));
        assertThat(actualTask.getWorkbasketId(), equalTo(destinationWorkbasket.getId()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testTransferDestinationWorkbasketDoesNotExist()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException {

        String destinationWorkbasketId = "2";
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doThrow(WorkbasketNotFoundException.class).when(workbasketServiceMock).checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
        doReturn(task).when(cutSpy).getTaskById(task.getId());

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketId);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineMock, times(1)).getWorkbasketService();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test(expected = TaskNotFoundException.class)
    public void testTransferTaskDoesNotExist()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException {

        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTaskById(task.getId());

        try {
            cutSpy.transfer(task.getId(), "2");
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testTransferNotAuthorizationOnWorkbasketAppend()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException {
        String destinationWorkbasketId = "2";
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(task).when(cutSpy).getTaskById(task.getId());
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketId);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineMock, times(1)).getWorkbasketService();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testTransferNotAuthorizationOnWorkbasketTransfer()
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException {
        String destinationWorkbasketId = "2";
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(task).when(cutSpy).getTaskById(task.getId());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.TRANSFER);

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketId);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineMock, times(2)).getWorkbasketService();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
            verify(workbasketServiceMock, times(1)).checkAuthorization(task.getId(), WorkbasketAuthorization.TRANSFER);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test
    public void testGetTaskCountForState() {
        List<TaskState> taskStates = Arrays.asList(TaskState.CLAIMED, TaskState.COMPLETED);
        List<TaskStateCounter> expectedResult = new ArrayList<>();
        doReturn(expectedResult).when(taskMapperMock).getTaskCountForState(taskStates);

        List<TaskStateCounter> actualResult = cut.getTaskCountForState(taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).getTaskCountForState(taskStates);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void testGetTaskCountForWorkbasketByDaysInPastAndState() {
        List<TaskState> taskStates = Arrays.asList(TaskState.CLAIMED, TaskState.COMPLETED);
        final long daysInPast = 10L;
        final long expectedResult = 5L;
        String workbasketId = "1";
        doReturn(expectedResult).when(taskMapperMock).getTaskCountForWorkbasketByDaysInPastAndState(any(), any(), any());

        long actualResult = cut.getTaskCountForWorkbasketByDaysInPastAndState(workbasketId, daysInPast, taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).getTaskCountForWorkbasketByDaysInPastAndState(any(), any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void testGetTaskCountByWorkbasketAndDaysInPastAndState() {
        final long daysInPast = 10L;
        List<TaskState> taskStates = Arrays.asList(TaskState.CLAIMED, TaskState.COMPLETED);
        List<DueWorkbasketCounter> expectedResult = new ArrayList<>();
        doReturn(expectedResult).when(taskMapperMock).getTaskCountByWorkbasketIdAndDaysInPastAndState(any(Date.class), any());

        List<DueWorkbasketCounter> actualResult = cut.getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast, taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).getTaskCountByWorkbasketIdAndDaysInPastAndState(any(Date.class), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void testSetTaskReadWIthExistingTask() throws TaskNotFoundException, ClassificationAlreadyExistException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        task.setModified(null);
        doReturn(task).when(cutSpy).getTaskById(task.getId());
        doNothing().when(taskMapperMock).update(task);

        Task actualTask = cutSpy.setTaskRead("1", true);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualTask.getModified(), not(equalTo(null)));
        assertThat(actualTask.isRead(), equalTo(true));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testSetTaskReadTaskNotBeFound() throws TaskNotFoundException, ClassificationAlreadyExistException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1");
        task.setModified(null);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTaskById(task.getId());

        try {
            cutSpy.setTaskRead("1", true);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test
    public void testGetTaskByIdWithExistingTask() throws TaskNotFoundException, ClassificationAlreadyExistException {
        Task expectedTask = createUnitTestTask("1", "DUMMY-TASK", "1");
        doReturn(expectedTask).when(taskMapperMock).findById(expectedTask.getId());

        Task actualTask = cut.getTaskById(expectedTask.getId());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findById(expectedTask.getId());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualTask, equalTo(expectedTask));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testGetTaskByIdWhereTaskDoesNotExist() throws Exception {
        Task task = createUnitTestTask("1", "DUMMY-TASK", "1");
        doThrow(TaskNotFoundException.class).when(taskMapperMock).findById(task.getId());

        try {
            cut.getTaskById(task.getId());
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskMapperMock, times(1)).findById(task.getId());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
                    taskMapperMock, objectReferenceMapperMock, workbasketServiceMock);
            throw e;
        }
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdWithInternalException() throws WorkbasketNotFoundException {
        // given - set behaviour and expected result
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImpl).openConnection();
        doThrow(new IllegalArgumentException("Invalid ID: " + workbasketId)).when(taskMapperMock).findTaskSummariesByWorkbasketId(workbasketId);
        doNothing().when(taskanaEngineImpl).returnConnection();
        doReturn(workbasketServiceMock).when(taskanaEngineImpl).getWorkbasketService();
        doReturn(new Workbasket()).when(workbasketServiceMock).getWorkbasket(any());

        // when - make the call
        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        // then - verify external communications and assert result
        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findTaskSummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(taskanaEngineImpl, times(1)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());

        verifyNoMoreInteractions(taskMapperMock, taskanaEngineImpl, workbasketServiceMock);
        assertThat(actualResultList, equalTo(expectedResultList));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingResults() throws WorkbasketNotFoundException {
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = Arrays.asList(new TaskSummary(), new TaskSummary());
        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();
        doReturn(workbasketServiceMock).when(taskanaEngineImpl).getWorkbasketService();
        doReturn(new Workbasket()).when(workbasketServiceMock).getWorkbasket(any());
        doReturn(expectedResultList).when(taskMapperMock).findTaskSummariesByWorkbasketId(workbasketId);

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        verify(taskanaEngineImpl, times(1)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findTaskSummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskMapperMock, taskanaEngineImpl,  workbasketServiceMock);
        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingNull() throws WorkbasketNotFoundException {
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();
        doReturn(null).when(taskMapperMock).findTaskSummariesByWorkbasketId(workbasketId);
        doReturn(workbasketServiceMock).when(taskanaEngineImpl).getWorkbasketService();
        doReturn(new Workbasket()).when(workbasketServiceMock).getWorkbasket(any());

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findTaskSummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(taskanaEngineImpl, times(1)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verifyNoMoreInteractions(taskMapperMock, taskanaEngineImpl, workbasketServiceMock);

        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }

    private Task createUnitTestTask(String id, String name, String workbasketId) throws ClassificationAlreadyExistException {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setWorkbasketId(workbasketId);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        task.setCreated(now);
        task.setModified(now);
        Classification classification = (Classification) new ClassificationImpl();
        classificationServiceMock.createClassification(classification);
        task.setClassification(classification);
        return task;
    }

    private Workbasket createWorkbasket(String id) {
        Workbasket workbasket = new Workbasket();
        workbasket.setId(id);
        workbasket.setName("Workbasket " + id);
        return workbasket;
    }
}
