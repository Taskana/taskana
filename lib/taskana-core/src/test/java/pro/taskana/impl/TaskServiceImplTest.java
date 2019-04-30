package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import pro.taskana.Attachment;
import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.ObjectReferenceMapper;
import pro.taskana.mappings.TaskMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * Unit Test for TaskServiceImpl.
 *
 * @author EH
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CurrentUserContext.class)
@PowerMockIgnore("javax.management.*")
public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl cut;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineMock;

    @Mock
    private TaskMapper taskMapperMock;

    @Mock
    private ObjectReferenceMapper objectReferenceMapperMock;

    @Mock
    private WorkbasketService workbasketServiceMock;

    @Mock
    private ClassificationServiceImpl classificationServiceImplMock;

    @Mock
    private AttachmentMapper attachmentMapperMock;

    @Mock
    private ClassificationQueryImpl classificationQueryImplMock;

    @Mock
    private WorkbasketQueryImpl workbasketQueryImplMock;

    @Mock
    private SqlSession sqlSessionMock;

    @Before
    public void setup() throws WorkbasketNotFoundException {
        MockitoAnnotations.initMocks(this);
        when(taskanaEngineMock.getWorkbasketService()).thenReturn(workbasketServiceMock);
        when(taskanaEngineMock.getClassificationService()).thenReturn(classificationServiceImplMock);
        try {
            Mockito.doNothing().when(workbasketServiceMock).checkAuthorization(any(), any());
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
        }
        Mockito.doNothing().when(taskanaEngineMock).openConnection();
        Mockito.doNothing().when(taskanaEngineMock).returnConnection();
    }

    @Test
    public void testCreateSimpleTask() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException, TaskNotFoundException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("", "DUMMYTASK", "k1", dummyClassification);
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("1");
        wb.setKey("k1");
        wb.setName("workbasket");
        wb.setDomain(dummyClassification.getDomain());
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(expectedTask.getId());
        when(workbasketServiceMock.getWorkbasket(wb.getKey(), wb.getDomain())).thenReturn(wb);
        doNothing().when(taskMapperMock).insert(expectedTask);
        when(classificationServiceImplMock.getClassification(
            dummyClassification.getKey(),
            dummyClassification.getDomain())).thenReturn(dummyClassification);
        expectedTask.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(false);

        Task actualTask = cutSpy.createTask(expectedTask);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(1)).getWorkbasket(any(), any());
        verify(classificationServiceImplMock, times(1)).getClassification(any(), any());
        verify(taskanaEngineMock, times(1)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(1)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock,
            classificationServiceImplMock);

        assertNull(actualTask.getOwner());
        assertNotNull(actualTask.getCreated());
        assertNotNull(actualTask.getModified());
        assertNull(actualTask.getCompleted());
        assertThat(actualTask.getWorkbasketKey(), equalTo(expectedTask.getWorkbasketKey()));
        assertThat(actualTask.getName(), equalTo(expectedTask.getName()));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
    }

    @Test(expected = SystemException.class)
    public void testCreateTaskWithSecurityButNoUserId()
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
        ClassificationNotFoundException, TaskAlreadyExistException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("", "DUMMYTASK", "k1", dummyClassification);
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("1");
        wb.setKey("k1");
        wb.setName("workbasket");
        wb.setDomain(dummyClassification.getDomain());
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(expectedTask.getId());
        when(workbasketServiceMock.getWorkbasket(wb.getKey(), wb.getDomain())).thenReturn(wb);
        doNothing().when(taskMapperMock).insert(expectedTask);
        when(classificationServiceImplMock.getClassification(dummyClassification.getKey(),
            dummyClassification.getDomain())).thenReturn(dummyClassification);
        expectedTask.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(true);

        cutSpy.createTask(expectedTask);
    }

    @Test
    public void testCreateSimpleTaskWithObjectReference() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException, TaskNotFoundException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        ObjectReference expectedObjectReference = JunitHelper.createDefaultObjRef();
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("1");
        wb.setName("workbasket");
        wb.setKey("k33");
        wb.setDomain("dummy-domain");

        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("", "DUMMYTASK", wb.getKey(), dummyClassification);
        expectedTask.setPrimaryObjRef(expectedObjectReference);
        ClassificationSummary classification = expectedTask.getClassificationSummary();
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(expectedTask.getId());
        when(workbasketServiceMock.getWorkbasket(expectedTask.getWorkbasketKey(),
            expectedTask.getDomain())).thenReturn(wb);
        when(objectReferenceMapperMock.findByObjectReference(expectedObjectReference)).thenReturn(
            expectedObjectReference);
        when(classificationServiceImplMock.getClassification(dummyClassification.getKey(),
            dummyClassification.getDomain())).thenReturn(dummyClassification);
        doNothing().when(taskMapperMock).insert(expectedTask);
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(false);
        Task actualTask = cutSpy.createTask(expectedTask);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).getWorkbasket(wb.getKey(), wb.getDomain());
        verify(workbasketServiceMock, times(1)).checkAuthorization(wb.getId(),
            WorkbasketPermission.APPEND);
        verify(classificationServiceImplMock, times(1)).getClassification(classification.getKey(),
            classification.getDomain());
        verify(taskanaEngineMock, times(1)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(1)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock,
            classificationServiceImplMock);
        assertNull(actualTask.getOwner());
        assertNotNull(actualTask.getCreated());
        assertNotNull(actualTask.getModified());
        assertNull(actualTask.getCompleted());
        assertThat(actualTask.getWorkbasketKey(), equalTo(expectedTask.getWorkbasketKey()));
        assertThat(actualTask.getName(), equalTo(expectedTask.getName()));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
        assertThat(actualTask.getPrimaryObjRef(), equalTo(expectedObjectReference));
    }

    @Test
    public void testCreateSimpleTaskWithObjectReferenceIsNull() throws NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, TaskAlreadyExistException, TaskNotFoundException,
        InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        ObjectReference expectedObjectReference = JunitHelper.createDefaultObjRef();
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("1");
        wb.setKey("key1");
        wb.setName("workbasket");
        wb.setDomain("dummy-domain");

        when(workbasketServiceMock.getWorkbasket(wb.getKey(), wb.getDomain())).thenReturn(wb);
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("", "DUMMYTASK", "key1", dummyClassification);
        expectedTask.setPrimaryObjRef(expectedObjectReference);
        ClassificationSummary classification = expectedTask.getClassificationSummary();
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(expectedTask.getId());
        when(classificationServiceImplMock.getClassification(classification.getKey(),
            classification.getDomain())).thenReturn(dummyClassification);
        doNothing().when(taskMapperMock).insert(expectedTask);
        doNothing().when(objectReferenceMapperMock).insert(expectedObjectReference);
        when(objectReferenceMapperMock.findByObjectReference(expectedTask.getPrimaryObjRef())).thenReturn(null);
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(false);

        Task actualTask = cutSpy.createTask(expectedTask);
        expectedTask.getPrimaryObjRef().setId(actualTask.getPrimaryObjRef().getId());   // get only new ID

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).getWorkbasket(expectedTask.getWorkbasketKey(),
            expectedTask.getDomain());
        verify(workbasketServiceMock, times(1)).checkAuthorization(expectedTask.getWorkbasketSummary().getId(),
            WorkbasketPermission.APPEND);
        verify(classificationServiceImplMock, times(1)).getClassification(classification.getKey(),
            wb.getDomain());
        verify(taskanaEngineMock, times(1)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(1)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineMock, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock,
            classificationServiceImplMock);
        assertNull(actualTask.getOwner());
        assertNotNull(actualTask.getCreated());
        assertNotNull(actualTask.getModified());
        assertNull(actualTask.getCompleted());
        assertThat(actualTask.getWorkbasketKey(), equalTo(expectedTask.getWorkbasketKey()));
        assertThat(actualTask.getName(), equalTo(expectedTask.getName()));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
        assertThat(actualTask.getPrimaryObjRef(), equalTo(expectedObjectReference));
    }

    @Test
    public void testCreateTaskWithPlanned()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, TaskNotFoundException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);

        ObjectReference expectedObjectReference = JunitHelper.createDefaultObjRef();
        ClassificationImpl classification = new ClassificationImpl();
        classification.setName("Name");
        classification.setCategory("MANUAL");
        classification.setKey("classificationKey");
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("workbasketId");
        wb.setKey("workbasketKey");
        wb.setName("workbasket");
        wb.setDomain("Domain1");
        TaskImpl task = new TaskImpl();
        task.setWorkbasketSummary(wb.asSummary());
        task.setClassificationKey("classificationKey");
        task.setClassificationSummary(classification.asSummary());
        task.setPrimaryObjRef(expectedObjectReference);
        task.setDescription("simply awesome task");
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());
        when(workbasketServiceMock.getWorkbasket(wb.getId())).thenReturn(wb);
        when(classificationServiceImplMock.getClassification(classification.getKey(),
            wb.getDomain())).thenReturn(classification);
        when(objectReferenceMapperMock.findByObjectReference(expectedObjectReference)).thenReturn(
            expectedObjectReference);
        doNothing().when(taskMapperMock).insert(task);
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(false);

        cutSpy.createTask(task);

        TaskImpl task2 = new TaskImpl();
        task2.setWorkbasketKey(wb.getKey());
        task2.getWorkbasketSummaryImpl().setId(wb.getId());
        task2.setClassificationKey("classificationKey");
        task2.setPrimaryObjRef(expectedObjectReference);
        task2.setPlanned(Instant.now().minus(Duration.ofHours(1L)));
        task2.setName("Task2");
        task2.setPrimaryObjRef(JunitHelper.createDefaultObjRef());

        cutSpy.createTask(task2);

        verify(taskanaEngineMock, times(2)).openConnection();
        verify(workbasketServiceMock, times(2)).getWorkbasket(any());
        verify(workbasketServiceMock, times(2)).checkAuthorization(any(), any());
        verify(classificationServiceImplMock, times(2)).getClassification(any(), any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(2)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).insert(task);
        verify(taskMapperMock, times(1)).insert(task2);
        verify(taskanaEngineMock, times(2)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineMock, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock,
            classificationServiceImplMock);

        assertNull(task.getOwner());
        assertNotNull(task.getCreated());
        assertNotNull(task.getModified());
        assertNull(task.getCompleted());
        assertNull(task.getDue());
        assertThat(task.getWorkbasketKey(), equalTo(task2.getWorkbasketKey()));
        assertThat(task.getName(), equalTo(classification.getName()));
        assertThat(task.getState(), equalTo(TaskState.READY));
        assertThat(task.getPrimaryObjRef(), equalTo(expectedObjectReference));
        assertThat(task.getName(), not(task2.getName()));
        assertThat(task.getPlanned(), not(task2.getPlanned()));
        assertThat(task2.getPlanned(), not(task2.getCreated()));
    }

    @Test(expected = TaskAlreadyExistException.class)
    public void testCreateTaskThrowingAlreadyExistException() throws WorkbasketNotFoundException,
        ClassificationNotFoundException, NotAuthorizedException, TaskAlreadyExistException, TaskNotFoundException,
        InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("12", "Task Name", "1", dummyClassification);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.createTask(task);
        } catch (TaskAlreadyExistException ex) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskanaEngineMock, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock,
                classificationServiceImplMock);
            throw ex;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreateThrowingAuthorizedOnWorkbasket()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, TaskNotFoundException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("", "dummyTask", "1", dummyClassification);
        Workbasket dummyWorkbasket = createWorkbasket("2", "k1");
        task.setWorkbasketSummary(dummyWorkbasket.asSummary());
        when(workbasketServiceMock.getWorkbasket(any())).thenReturn(dummyWorkbasket);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(
            task.getWorkbasketSummary().getId(),
            WorkbasketPermission.APPEND);
        try {
            cutSpy.createTask(task);
        } catch (NotAuthorizedException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).getWorkbasket(task.getWorkbasketSummary().getId());
            verify(workbasketServiceMock, times(1)).checkAuthorization(task.getWorkbasketSummary().getId(),
                WorkbasketPermission.APPEND);
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock,
                classificationServiceImplMock);
            throw e;
        }
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testCreateThrowsWorkbasketNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, TaskNotFoundException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("", "dumma-task", "1", dummyClassification);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());
        when(workbasketServiceMock.getWorkbasket(any(), any())).thenThrow(WorkbasketNotFoundException.class);
        try {
            cutSpy.createTask(task);
        } catch (WorkbasketNotFoundException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).getWorkbasket(task.getWorkbasketKey(),
                task.getWorkbasketSummary().getDomain());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock,
                classificationServiceImplMock);
            throw e;
        }
    }

    @Test
    public void testClaimDefaultFlag()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        doReturn(expectedTask).when(cutSpy).claim(expectedTask.getId());
        cutSpy.claim(expectedTask.getId());
        verify(cutSpy, times(1)).claim(expectedTask.getId());
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
    }

    @Test
    public void testClaimSuccessfulToOwner() throws Exception {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        Mockito.doReturn(expectedTask).when(cutSpy).getTask(expectedTask.getId());
        String expectedOwner = "John Does";
        Instant before = Instant.now().minus(Duration.ofSeconds(3L));
        PowerMockito.mockStatic(CurrentUserContext.class);
        Mockito.when(CurrentUserContext.getUserid()).thenReturn(expectedOwner);

        Task acturalTask = cutSpy.forceClaim(expectedTask.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(expectedTask.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(acturalTask.getState(), equalTo(TaskState.CLAIMED));
        assertThat(acturalTask.getCreated(), not(equalTo(expectedTask.getModified())));
        assertTrue(acturalTask.getClaimed().isAfter(before));
        assertTrue(acturalTask.getModified().isAfter(before));
        assertThat(acturalTask.getOwner(), equalTo(expectedOwner));
        assertThat(acturalTask.isRead(), equalTo(true));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testClaimThrowinTaskNotFoundException() throws Exception {
        TaskImpl expectedTask = null;
        when(taskMapperMock.findById(any())).thenReturn(expectedTask);

        try {
            cut.forceClaim("1");
        } catch (Exception e) {
            verify(taskanaEngineMock, times(2)).openConnection();
            verify(taskMapperMock, times(1)).findById(any());
            verify(taskanaEngineMock, times(2)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidStateException.class)
    public void testClaimWithInvalidState() throws Exception {
        TaskService cutSpy = Mockito.spy(cut);
        TaskImpl task = createUnitTestTask("1", "taskName", "wbKey", null);
        task.setState(TaskState.COMPLETED);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.forceClaim("1");
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidOwnerException.class)
    public void testClaimWithInvalidOwner() throws Exception {
        TaskService cutSpy = Mockito.spy(cut);
        TaskImpl task = createUnitTestTask("1", "taskName", "wbKey", null);
        task.setState(TaskState.CLAIMED);
        task.setOwner("Max Mustermann");
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.claim("1");
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidStateException.class)
    public void testCancelClaimForcedWithInvalidState()
        throws TaskNotFoundException,
        InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        expectedTask.setState(TaskState.COMPLETED);
        Mockito.doReturn(expectedTask).when(cutSpy).getTask(expectedTask.getId());

        try {
            cutSpy.cancelClaim(expectedTask.getId());
        } catch (InvalidStateException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(expectedTask.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidOwnerException.class)
    public void testCancelClaimNotForcedWithInvalidOwner()
        throws TaskNotFoundException,
        InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        expectedTask.setOwner("Thomas");
        expectedTask.setState(TaskState.CLAIMED);
        Mockito.doReturn(expectedTask).when(cutSpy).getTask(expectedTask.getId());
        PowerMockito.mockStatic(CurrentUserContext.class);
        Mockito.when(CurrentUserContext.getUserid()).thenReturn("Heinz");

        try {
            cutSpy.cancelClaim(expectedTask.getId());
        } catch (InvalidOwnerException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(expectedTask.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCancelClaimDefaultFlag()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        doReturn(expectedTask).when(cutSpy).cancelClaim(expectedTask.getId());
        cutSpy.cancelClaim(expectedTask.getId());
        verify(cutSpy, times(1)).cancelClaim(expectedTask.getId());
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
    }

    @Test
    public void testCancelClaimSuccesfullForced()
        throws TaskNotFoundException,
        InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        String owner = "John Does";
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        expectedTask.setOwner("Some other owner");
        expectedTask.setState(TaskState.CLAIMED);
        Mockito.doReturn(expectedTask).when(cutSpy).getTask(expectedTask.getId());
        Instant before = Instant.now().minus(Duration.ofSeconds(3L));
        PowerMockito.mockStatic(CurrentUserContext.class);
        Mockito.when(CurrentUserContext.getUserid()).thenReturn(owner);

        Task acturalTask = cutSpy.forceCancelClaim(expectedTask.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(expectedTask.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(acturalTask.getState(), equalTo(TaskState.READY));
        assertThat(acturalTask.getClaimed(), equalTo(null));
        assertTrue(acturalTask.getModified().isAfter(before));
        assertThat(acturalTask.getOwner(), equalTo(null));
        assertThat(acturalTask.isRead(), equalTo(true));
    }

    @Test
    public void testCancelClaimInvalidState()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        String owner = "John Does";
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", null);
        expectedTask.setOwner("Some other owner");
        expectedTask.setState(TaskState.CLAIMED);
        Mockito.doReturn(expectedTask).when(cutSpy).getTask(expectedTask.getId());
        Instant before = Instant.now().minus(Duration.ofSeconds(3L));
        PowerMockito.mockStatic(CurrentUserContext.class);
        Mockito.when(CurrentUserContext.getUserid()).thenReturn(owner);

        Task acturalTask = cutSpy.forceCancelClaim(expectedTask.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(expectedTask.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(acturalTask.getState(), equalTo(TaskState.READY));
        assertThat(acturalTask.getClaimed(), equalTo(null));
        assertTrue(acturalTask.getModified().isAfter(before));
        assertThat(acturalTask.getOwner(), equalTo(null));
        assertThat(acturalTask.isRead(), equalTo(true));
    }

    @Test
    public void testCompleteTaskDefault()
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, InterruptedException,
        NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final long sleepTime = 100L;
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        Thread.sleep(sleepTime);
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        task.setOwner(CurrentUserContext.getUserid());
        when(taskMapperMock.findById(task.getId())).thenReturn(task);
        when(attachmentMapperMock.findAttachmentsByTaskId(task.getId())).thenReturn(null);
        doReturn(task).when(cutSpy).completeTask(task.getId());
        when(classificationServiceImplMock.createClassificationQuery()).thenReturn(classificationQueryImplMock);
        when(classificationQueryImplMock.idIn(any())).thenReturn(classificationQueryImplMock);
        when(classificationQueryImplMock.list()).thenReturn(new ArrayList<>());
        List<ClassificationSummary> classificationList = Arrays
            .asList((ClassificationSummaryImpl) dummyClassification.asSummary());
        when(
            classificationQueryImplMock
                .list()).thenReturn(classificationList);
        when(workbasketServiceMock.createWorkbasketQuery()).thenReturn(workbasketQueryImplMock);
        when(workbasketQueryImplMock.idIn(any())).thenReturn(workbasketQueryImplMock);
        List<WorkbasketSummary> wbList = new ArrayList<>();
        WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
        wb.setDomain("dummy-domain");
        wbList.add(wb);
        when(workbasketQueryImplMock.list()).thenReturn(wbList);

        Task actualTask = cut.completeTask(task.getId());

        verify(taskanaEngineMock, times(2)).openConnection();
        verify(taskMapperMock, times(1)).findById(task.getId());
        verify(attachmentMapperMock, times(1)).findAttachmentsByTaskId(task.getId());
        verify(classificationServiceImplMock, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).idIn(any());
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineMock, times(2)).returnConnection();
        verify(workbasketServiceMock, times(1)).createWorkbasketQuery();
        verify(workbasketQueryImplMock, times(1)).idIn(any());
        verify(workbasketQueryImplMock, times(1)).list();

        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(task.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
    }

    @Test
    public void testCompleteTaskNotForcedWorking()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, InterruptedException,
        NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final long sleepTime = 100L;
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        // created and modify should be able to be different.
        Thread.sleep(sleepTime);
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        task.setOwner(CurrentUserContext.getUserid());
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(task);

        Task actualTask = cutSpy.completeTask(task.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(task.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
        assertThat(actualTask.getCompleted(), equalTo(actualTask.getModified()));
    }

    @Test(expected = InvalidStateException.class)
    public void testCompleteTaskNotForcedNotClaimedBefore()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setState(TaskState.READY);
        task.setClaimed(null);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.completeTask(task.getId());
        } catch (InvalidStateException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidOwnerException.class)
    public void testCompleteTaskNotForcedInvalidOwnerException()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setOwner("Dummy-Owner-ID: 10");
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.completeTask(task.getId());
        } catch (InvalidOwnerException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = TaskNotFoundException.class)
    public void testCompleteTaskTaskNotFound()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        String taskId = "1";
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(taskId);
        try {
            cutSpy.completeTask(taskId);
        } catch (InvalidOwnerException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(taskId);
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCompleteForcedAndAlreadyClaimed()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, InterruptedException,
        NotAuthorizedException {
        final long sleepTime = 100L;
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        // created and modify should be able to be different.
        Thread.sleep(sleepTime);
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(task);

        Task actualTask = cutSpy.forceCompleteTask(task.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(task.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
        assertThat(actualTask.getCompleted(), equalTo(actualTask.getModified()));
    }

    @Test
    public void testCompleteForcedNotClaimed()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, InterruptedException,
        NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final long sleepTime = 100L;
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setState(TaskState.READY);
        task.setClaimed(null);
        doReturn(task).when(cutSpy).getTask(task.getId());
        TaskImpl claimedTask = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        // created and modify should be able to be different.
        Thread.sleep(sleepTime);
        claimedTask.setState(TaskState.CLAIMED);
        claimedTask.setClaimed(Instant.now());
        doReturn(claimedTask).when(cutSpy).forceClaim(task.getId());
        doNothing().when(taskMapperMock).update(claimedTask);

        Task actualTask = cutSpy.forceCompleteTask(task.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(cutSpy, times(1)).forceClaim(task.getId());
        verify(taskMapperMock, times(1)).update(claimedTask);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(claimedTask.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
        assertThat(actualTask.getCompleted(), equalTo(actualTask.getModified()));
    }

    @Test
    public void testTransferTaskToDestinationWorkbasketWithoutSecurity()
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidStateException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Workbasket destinationWorkbasket = createWorkbasket("2", "k1");
        Workbasket sourceWorkbasket = createWorkbasket("47", "key47");
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "key47", dummyClassification);
        task.setWorkbasketSummary(sourceWorkbasket.asSummary());
        task.setRead(true);
        when(workbasketServiceMock.getWorkbasket(destinationWorkbasket.getId())).thenReturn(destinationWorkbasket);
        when(workbasketServiceMock.getWorkbasket(sourceWorkbasket.getId())).thenReturn(sourceWorkbasket);
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(false);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(any());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasket.getId(),
            WorkbasketPermission.APPEND);
        doNothing().when(workbasketServiceMock).checkAuthorization(sourceWorkbasket.getId(),
            WorkbasketPermission.TRANSFER);

        Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasket.getId(),
            WorkbasketPermission.APPEND);
        verify(workbasketServiceMock, times(1)).checkAuthorization(sourceWorkbasket.getId(),
            WorkbasketPermission.TRANSFER);
        verify(workbasketServiceMock, times(1)).getWorkbasket(destinationWorkbasket.getId());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.isRead(), equalTo(false));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
        assertThat(actualTask.isTransferred(), equalTo(true));
        assertThat(actualTask.getWorkbasketKey(), equalTo(destinationWorkbasket.getKey()));
    }

    @Test
    public void testTransferTaskToDestinationWorkbasketUsingSecurityTrue()
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidStateException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Workbasket destinationWorkbasket = createWorkbasket("2", "k2");
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "k1", dummyClassification);
        task.setRead(true);
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
        when(taskanaEngineConfigurationMock.isSecurityEnabled()).thenReturn(true);
        doReturn(task).when(cutSpy).getTask(task.getId());
        when(workbasketServiceMock.getWorkbasket(destinationWorkbasket.getId())).thenReturn(destinationWorkbasket);
        doNothing().when(taskMapperMock).update(any());
        doNothing().when(workbasketServiceMock).checkAuthorization(any(), any());
        // doNothing().when(workbasketServiceMock).checkAuthorizationById(any(), WorkbasketAuthorization.TRANSFER);
        Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(workbasketServiceMock, times(2)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(1)).getWorkbasket(destinationWorkbasket.getId());
        verify(taskanaEngineMock, times(0)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(0)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.isRead(), equalTo(false));
        assertThat(actualTask.getState(), equalTo(TaskState.READY));
        assertThat(actualTask.isTransferred(), equalTo(true));
        assertThat(actualTask.getWorkbasketKey(), equalTo(destinationWorkbasket.getKey()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testTransferDestinationWorkbasketDoesNotExist()
        throws Exception {

        String destinationWorkbasketId = "2";
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doThrow(WorkbasketNotFoundException.class).when(workbasketServiceMock)
            .checkAuthorization(destinationWorkbasketId, WorkbasketPermission.APPEND);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketId);
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketId,
                WorkbasketPermission.APPEND);
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = TaskNotFoundException.class)
    public void testTransferTaskDoesNotExist()
        throws Exception {

        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.transfer(task.getId(), "2");
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testTransferNotAuthorizationOnWorkbasketAppend()
        throws Exception {
        String destinationWorkbasketId = "2";
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(
            destinationWorkbasketId,
            WorkbasketPermission.APPEND);

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketId);
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketId,
                WorkbasketPermission.APPEND);
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testTransferNotAuthorizationOnWorkbasketTransfer()
        throws Exception {
        String destinationWorkbasketId = "2";
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasketId,
            WorkbasketPermission.APPEND);
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(
            task.getWorkbasketSummary().getId(),
            WorkbasketPermission.TRANSFER);

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketId);
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketId,
                WorkbasketPermission.APPEND);
            verify(workbasketServiceMock, times(1)).checkAuthorization(task.getWorkbasketSummary().getId(),
                WorkbasketPermission.TRANSFER);
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testSetTaskReadWIthExistingTask()
        throws TaskNotFoundException, NotAuthorizedException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setModified(null);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(task);

        Task actualTask = cutSpy.setTaskRead("1", true);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
        assertThat(actualTask.getModified(), not(equalTo(null)));
        assertThat(actualTask.isRead(), equalTo(true));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testSetTaskReadTaskNotBeFound() throws Exception {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setModified(null);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.setTaskRead("1", true);
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testGetTaskByIdWithExistingTask()
        throws TaskNotFoundException, NotAuthorizedException {
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("1", "DUMMY-TASK", "1", dummyClassification);
        when(taskMapperMock.findById(expectedTask.getId())).thenReturn(expectedTask);
        when(attachmentMapperMock.findAttachmentsByTaskId(expectedTask.getId())).thenReturn(null);

        when(classificationServiceImplMock.createClassificationQuery()).thenReturn(classificationQueryImplMock);
        when(classificationQueryImplMock.idIn(any())).thenReturn(classificationQueryImplMock);
        when(workbasketServiceMock.createWorkbasketQuery()).thenReturn(workbasketQueryImplMock);
        when(workbasketQueryImplMock.idIn(any())).thenReturn(workbasketQueryImplMock);
        List<WorkbasketSummary> wbList = new ArrayList<>();
        WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
        wb.setDomain("dummy-domain");
        wbList.add(wb);
        when(workbasketQueryImplMock.list()).thenReturn(wbList);

        List<ClassificationSummary> classificationList = Arrays
            .asList((ClassificationSummaryImpl) dummyClassification.asSummary());
        when(
            classificationQueryImplMock
                .list()).thenReturn(classificationList);
        Task actualTask = cut.getTask(expectedTask.getId());

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findById(expectedTask.getId());
        verify(attachmentMapperMock, times(1)).findAttachmentsByTaskId(expectedTask.getId());
        verify(classificationServiceImplMock, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).idIn(any());
        verify(classificationQueryImplMock, times(1)).list();
        verify(workbasketServiceMock, times(1)).createWorkbasketQuery();
        verify(workbasketQueryImplMock, times(1)).idIn(any());
        verify(workbasketQueryImplMock, times(1)).list();

        verify(taskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
        assertThat(actualTask, equalTo(expectedTask));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testGetTaskByIdWhereTaskDoesNotExist() throws Exception {
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "DUMMY-TASK", "1", dummyClassification);
        when(taskMapperMock.findById(task.getId())).thenThrow(TaskNotFoundException.class);

        try {
            cut.getTask(task.getId());
        } catch (Exception e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(taskMapperMock, times(1)).findById(task.getId());
            verify(taskanaEngineMock, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock,
                taskanaEngineMock,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testUpdateTaskAddingValidAttachment() throws TaskNotFoundException, SystemException,
        ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
        Classification classification = createDummyClassification();
        Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
        Attachment attachment = JunitHelper.createDefaultAttachment();
        ObjectReference objectReference = JunitHelper.createDefaultObjRef();
        TaskImpl taskBeforeAttachment = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        TaskImpl task = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        task.setPrimaryObjRef(objectReference);
        task.addAttachment(attachment);
        taskBeforeAttachment.setModified(null);
        taskBeforeAttachment.setCreated(Instant.now());
        task.setModified(null);
        task.setCreated(taskBeforeAttachment.getCreated());
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(taskBeforeAttachment).when(cutSpy).getTask(task.getId());

        Task actualTask = cutSpy.updateTask(task);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).insert(((AttachmentImpl) attachment));
        verify(taskanaEngineMock, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(1));
    }

    @Test
    public void testUpdateTaskAddingValidAttachmentTwice() throws TaskNotFoundException, SystemException,
        ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
        Classification classification = createDummyClassification();
        Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
        Attachment attachment = JunitHelper.createDefaultAttachment();
        ObjectReference objectReference = JunitHelper.createDefaultObjRef();
        TaskImpl taskBeforeAttachment = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        TaskImpl task = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskBeforeAttachment.setModified(null);
        taskBeforeAttachment.setCreated(Instant.now());
        task.setModified(null);
        task.setCreated(taskBeforeAttachment.getCreated());
        task.setPrimaryObjRef(objectReference);
        task.addAttachment(attachment);
        task.addAttachment(attachment);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(taskBeforeAttachment).when(cutSpy).getTask(task.getId());

        Task actualTask = cutSpy.updateTask(task);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).insert(((AttachmentImpl) attachment));
        verify(taskanaEngineMock, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(1));
    }

    @Test(expected = AttachmentPersistenceException.class)
    public void testUpdateTaskAddingAttachmentWithSameIdForcedUsingingListMethod()
        throws TaskNotFoundException, SystemException,
        ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
        Classification classification = createDummyClassification();
        Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
        Attachment attachment = JunitHelper.createDefaultAttachment();
        ObjectReference objectReference = JunitHelper.createDefaultObjRef();
        TaskImpl taskBeforeAttachment = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        TaskImpl task = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskBeforeAttachment.setModified(null);
        taskBeforeAttachment.setCreated(Instant.now());
        task.setModified(null);
        task.setCreated(taskBeforeAttachment.getCreated());
        task.setPrimaryObjRef(objectReference);
        task.setAttachments(new ArrayList<>());
        task.getAttachments().add(attachment);
        task.getAttachments().add(attachment);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(taskBeforeAttachment).when(cutSpy).getTask(task.getId());
        doThrow(PersistenceException.class).when(attachmentMapperMock).insert(any());

        try {
            cutSpy.updateTask(task);
        } catch (AttachmentPersistenceException e) {
            verify(taskanaEngineMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(attachmentMapperMock, times(1)).insert(((AttachmentImpl) attachment));
            verify(taskanaEngineMock, times(1)).returnConnection();
            throw e;
        }
    }

    @Test
    public void testUpdateTaskUpdateAttachment() throws TaskNotFoundException, SystemException,
        ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
        String channelUpdate = "OTHER CHANNEL";
        Classification classification = createDummyClassification();
        Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
        Attachment attachment = JunitHelper.createDefaultAttachment();
        Attachment attachmentToUpdate = JunitHelper.createDefaultAttachment();
        attachmentToUpdate.setChannel(channelUpdate);
        ObjectReference objectReference = JunitHelper.createDefaultObjRef();
        TaskImpl taskBefore = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskBefore.addAttachment(attachment);
        TaskImpl task = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskBefore.setModified(null);
        taskBefore.setCreated(Instant.now());
        task.setModified(null);
        task.setCreated(taskBefore.getCreated());
        task.addAttachment(taskBefore.getAttachments().get(0));
        task.setPrimaryObjRef(objectReference);
        task.addAttachment(attachmentToUpdate); // should override old one and differ at comparison
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(taskBefore).when(cutSpy).getTask(task.getId());

        Task actualTask = cutSpy.updateTask(task);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).update(((AttachmentImpl) attachmentToUpdate));
        verify(taskanaEngineMock, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(1));
        assertThat(actualTask.getAttachments().get(0).getChannel(), equalTo(channelUpdate));
    }

    @Test
    public void testUpdateTaskRemovingAttachment() throws TaskNotFoundException, SystemException,
        ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
        Classification classification = createDummyClassification();
        Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
        Attachment attachment = JunitHelper.createDefaultAttachment();
        ObjectReference objectReference = JunitHelper.createDefaultObjRef();
        TaskImpl taskBefore = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskBefore.setPrimaryObjRef(objectReference);
        taskBefore.addAttachment(attachment);
        TaskImpl task = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        task.setPrimaryObjRef(objectReference);
        taskBefore.setModified(null);
        taskBefore.setCreated(Instant.now());
        task.setModified(null);
        task.setCreated(taskBefore.getCreated());
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(taskBefore).when(cutSpy).getTask(task.getId());

        Task actualTask = cutSpy.updateTask(task);

        verify(taskanaEngineMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).deleteAttachment(attachment.getId());
        verify(taskanaEngineMock, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(0));
    }

    @Test
    public void testTaskSummaryEqualsHashCode() throws InterruptedException {
        Classification classification = createDummyClassification();
        Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
        Attachment attachment = JunitHelper.createDefaultAttachment();
        ObjectReference objectReference = JunitHelper.createDefaultObjRef();
        TaskImpl taskBefore = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskBefore.setPrimaryObjRef(objectReference);
        Thread.sleep(10);
        TaskImpl taskAfter = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
        taskAfter.setPrimaryObjRef(objectReference);
        TaskSummary summaryBefore = taskBefore.asSummary();
        TaskSummary summaryAfter = taskAfter.asSummary();

        assertNotEquals(summaryBefore, summaryAfter);
        assertNotEquals(summaryBefore.hashCode(), summaryAfter.hashCode());

        taskAfter.setCreated(taskBefore.getCreated());
        taskAfter.setModified(taskBefore.getModified());
        summaryAfter = taskAfter.asSummary();
        assertEquals(summaryBefore, summaryAfter);
        assertEquals(summaryBefore.hashCode(), summaryAfter.hashCode());

        taskBefore.setModified(null);
        summaryBefore = taskBefore.asSummary();
        assertNotEquals(summaryBefore, summaryAfter);
        assertNotEquals(summaryBefore.hashCode(), summaryAfter.hashCode());

        taskAfter.setModified(null);
        summaryAfter = taskAfter.asSummary();
        assertEquals(summaryBefore, summaryAfter);
        assertEquals(summaryBefore.hashCode(), summaryAfter.hashCode());
    }

    private TaskImpl createUnitTestTask(String id, String name, String workbasketKey, Classification classification) {
        TaskImpl task = new TaskImpl();
        task.setId(id);
        task.setExternalId(id);
        task.setName(name);
        task.setWorkbasketKey(workbasketKey);
        task.setDomain("");
        task.setAttachments(new ArrayList<>());
        Instant now = Instant.now().minus(Duration.ofMinutes(1L));
        task.setCreated(now);
        task.setModified(now);
        if (classification == null) {
            classification = createDummyClassification();
        }
        task.setClassificationSummary(classification.asSummary());
        task.setClassificationKey(classification.getKey());
        task.setDomain(classification.getDomain());
        return task;
    }

    private Classification createDummyClassification() {
        ClassificationImpl classification = new ClassificationImpl();
        classification.setName("dummy-classification");
        classification.setDomain("dummy-domain");
        classification.setKey("dummy-classification-key");
        classification.setId("DummyClassificationId");
        return classification;
    }

    private WorkbasketImpl createWorkbasket(String id, String key) {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setId(id);
        workbasket.setDomain("Domain1");
        workbasket.setKey(key);
        workbasket.setName("Workbasket " + id);
        return workbasket;
    }
}
