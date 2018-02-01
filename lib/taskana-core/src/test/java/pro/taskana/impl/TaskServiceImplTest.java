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
import pro.taskana.Task;
import pro.taskana.TaskSummary;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.AttachmentMapper;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * Unit Test for TaskServiceImpl.
 *
 * @author EH
 */
// @RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(CurrentUserContext.class)
@PowerMockIgnore("javax.management.*")
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
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(workbasketServiceMock).when(taskanaEngineMock).getWorkbasketService();
        doReturn(classificationServiceImplMock).when(taskanaEngineMock).getClassificationService();
        try {
            Mockito.doNothing().when(workbasketServiceMock).checkAuthorization(any(), any());
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
        }
        Mockito.doNothing().when(taskanaEngineImpl).openConnection();
        Mockito.doNothing().when(taskanaEngineImpl).returnConnection();
    }

    @Test
    public void testCreateSimpleTask() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, ClassificationAlreadyExistException, TaskAlreadyExistException,
        TaskNotFoundException, InvalidWorkbasketException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("", "DUMMYTASK", "k1", dummyClassification);
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("1");
        wb.setKey("k1");
        wb.setName("workbasket");
        wb.setDomain(dummyClassification.getDomain());
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(expectedTask.getId());
        doReturn(wb).when(workbasketServiceMock).getWorkbasketByKey(wb.getKey());
        doNothing().when(taskMapperMock).insert(expectedTask);
        doReturn(dummyClassification).when(
            classificationServiceImplMock)
            .getClassification(dummyClassification.getKey(), dummyClassification.getDomain());
        expectedTask.setPrimaryObjRef(JunitHelper.createDefaultObjRef());

        Task actualTask = cutSpy.createTask(expectedTask);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(any());
        verify(classificationServiceImplMock, times(1)).getClassification(any(), any());
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
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

    @Test
    public void testCreateSimpleTaskWithObjectReference() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, ClassificationAlreadyExistException, TaskAlreadyExistException,
        TaskNotFoundException, InvalidWorkbasketException, InvalidArgumentException {
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
        doReturn(wb).when(workbasketServiceMock).getWorkbasketByKey(expectedTask.getWorkbasketKey());
        doReturn(expectedObjectReference).when(objectReferenceMapperMock)
            .findByObjectReference(expectedObjectReference);
        doReturn(dummyClassification).when(
            classificationServiceImplMock)
            .getClassification(dummyClassification.getKey(), dummyClassification.getDomain());
        doNothing().when(taskMapperMock).insert(expectedTask);
        Task actualTask = cutSpy.createTask(expectedTask);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(wb.getKey());
        verify(workbasketServiceMock, times(1)).checkAuthorization(wb.getKey(), WorkbasketAuthorization.APPEND);
        verify(classificationServiceImplMock, times(1)).getClassification(classification.getKey(),
            classification.getDomain());
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
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
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, TaskNotFoundException, InvalidWorkbasketException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        ObjectReference expectedObjectReference = JunitHelper.createDefaultObjRef();
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setId("1");
        wb.setKey("key1");
        wb.setName("workbasket");
        wb.setDomain("dummy-domain");

        doReturn(wb).when(workbasketServiceMock).getWorkbasketByKey(wb.getKey());
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("", "DUMMYTASK", "key1", dummyClassification);
        expectedTask.setPrimaryObjRef(expectedObjectReference);
        ClassificationSummary classification = expectedTask.getClassificationSummary();
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(expectedTask.getId());
        doReturn(dummyClassification).when(classificationServiceImplMock).getClassification(classification.getKey(),
            classification.getDomain());
        doNothing().when(taskMapperMock).insert(expectedTask);
        doNothing().when(objectReferenceMapperMock).insert(expectedObjectReference);
        doReturn(null).when(objectReferenceMapperMock).findByObjectReference(expectedTask.getPrimaryObjRef());

        Task actualTask = cutSpy.createTask(expectedTask);
        expectedTask.getPrimaryObjRef().setId(actualTask.getPrimaryObjRef().getId());   // get only new ID

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(expectedTask.getWorkbasketKey());
        verify(workbasketServiceMock, times(1)).checkAuthorization(expectedTask.getWorkbasketKey(),
            WorkbasketAuthorization.APPEND);
        verify(classificationServiceImplMock, times(1)).getClassification(classification.getKey(),
            wb.getDomain());
        verify(taskMapperMock, times(1)).insert(expectedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
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
        TaskAlreadyExistException, TaskNotFoundException, InvalidWorkbasketException, InvalidArgumentException {
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
        TaskImpl task = new TaskImpl();
        task.setWorkbasketKey(wb.getKey());
        task.setClassificationKey("classificationKey");
        task.setClassificationSummary(classification.asSummary());
        task.setPrimaryObjRef(expectedObjectReference);
        task.setDescription("simply awesome task");
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());
        doReturn(wb).when(workbasketServiceMock).getWorkbasketByKey(wb.getKey());
        doReturn(classification).when(classificationServiceImplMock).getClassification(classification.getKey(),
            classification.getDomain());
        doReturn(expectedObjectReference).when(objectReferenceMapperMock)
            .findByObjectReference(expectedObjectReference);
        doNothing().when(taskMapperMock).insert(task);

        cutSpy.createTask(task);

        TaskImpl task2 = new TaskImpl();
        task2.setWorkbasketKey(wb.getKey());
        task2.setClassificationKey("classificationKey");
        task2.setPrimaryObjRef(expectedObjectReference);
        task2.setPlanned(Instant.now().minus(Duration.ofHours(1L)));
        task2.setName("Task2");
        task2.setPrimaryObjRef(JunitHelper.createDefaultObjRef());

        cutSpy.createTask(task2);

        verify(taskanaEngineImpl, times(2)).openConnection();
        verify(workbasketServiceMock, times(2)).checkAuthorization(any(), any());
        verify(workbasketServiceMock, times(2)).getWorkbasketByKey(any());
        verify(classificationServiceImplMock, times(2)).getClassification(any(), any());
        verify(taskMapperMock, times(1)).insert(task);
        verify(taskMapperMock, times(1)).insert(task2);
        verify(taskanaEngineImpl, times(2)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
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
        InvalidWorkbasketException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("12", "Task Name", "1", dummyClassification);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.createTask(task);
        } catch (TaskAlreadyExistException ex) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock,
                classificationServiceImplMock);
            throw ex;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreateThrowingAuthorizedOnWorkbasket()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, TaskNotFoundException, InvalidWorkbasketException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("", "dummyTask", "1", dummyClassification);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(task.getWorkbasketKey(),
            WorkbasketAuthorization.APPEND);
        try {
            cutSpy.createTask(task);
        } catch (NotAuthorizedException e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).getWorkbasketByKey(task.getWorkbasketKey());
            verify(workbasketServiceMock, times(1)).checkAuthorization(task.getWorkbasketKey(),
                WorkbasketAuthorization.APPEND);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock,
                classificationServiceImplMock);
            throw e;
        }
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testCreateThrowsWorkbasketNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidWorkbasketException, TaskAlreadyExistException, TaskNotFoundException, InvalidArgumentException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("", "dumma-task", "1", dummyClassification);
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(task.getId());
        doThrow(WorkbasketNotFoundException.class).when(workbasketServiceMock).getWorkbasketByKey(any());
        try {
            cutSpy.createTask(task);
        } catch (WorkbasketNotFoundException e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).getWorkbasketByKey(task.getWorkbasketKey());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock,
                classificationServiceImplMock);
            throw e;
        }
    }

    @Test
    public void testClaimSuccessfulToOwner() throws Exception {
        Classification dummyClassification = createDummyClassification();
        TaskImpl expectedTask = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        doReturn(null).when(attachmentMapperMock).findAttachmentsByTaskId(expectedTask.getId());
        Mockito.doReturn(expectedTask).when(taskMapperMock).findById(expectedTask.getId());
        Thread.sleep(SLEEP_TIME); // to have different timestamps
        String expectedOwner = "John Does";
        PowerMockito.mockStatic(CurrentUserContext.class);
        Mockito.when(CurrentUserContext.getUserid()).thenReturn(expectedOwner);
        doReturn(classificationQueryImplMock).when(classificationServiceImplMock).createClassificationQuery();
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).domain(any());
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).key(any());
        doReturn(new ArrayList<>()).when(classificationQueryImplMock).list();
        doReturn(dummyClassification).when(
            classificationServiceImplMock)
            .getClassification(dummyClassification.getKey(), dummyClassification.getDomain());

        List<ClassificationSummaryImpl> classificationList = Arrays
            .asList((ClassificationSummaryImpl) dummyClassification.asSummary());
        doReturn(classificationList).when(
            classificationQueryImplMock)
            .list();

        // Mockito.doReturn(expectedOwner).when(currentUserContext).getUserid();
        Task acturalTask = cut.claim(expectedTask.getId(), true);

        verify(taskanaEngineImpl, times(2)).openConnection();
        verify(taskMapperMock, times(1)).findById(expectedTask.getId());
        verify(attachmentMapperMock, times(1)).findAttachmentsByTaskId(expectedTask.getId());
        verify(classificationQueryImplMock, times(1)).domain(any());
        verify(classificationQueryImplMock, times(1)).key(any());
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(2)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(acturalTask.getState(), equalTo(TaskState.CLAIMED));
        assertThat(acturalTask.getCreated(), not(equalTo(expectedTask.getModified())));
        assertThat(acturalTask.getClaimed(), not(equalTo(null)));
        assertThat(acturalTask.getOwner(), equalTo(expectedOwner));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testClaimThrowinTaskNotFoundException() throws Exception {
        try {
            TaskImpl expectedTask = null;
            Mockito.doReturn(expectedTask).when(taskMapperMock).findById(any());
            // Mockito.doReturn("OWNER").when(currentUserContext).getUserid();

            cut.claim("1", true);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(2)).openConnection();
            verify(taskMapperMock, times(1)).findById(any());
            verify(taskanaEngineImpl, times(2)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCompleteTaskDefault()
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, InterruptedException,
        ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final long sleepTime = 100L;
        final boolean isForced = false;
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        Thread.sleep(sleepTime);
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        task.setOwner(CurrentUserContext.getUserid());
        doReturn(task).when(taskMapperMock).findById(task.getId());
        doReturn(null).when(attachmentMapperMock).findAttachmentsByTaskId(task.getId());
        doReturn(task).when(cutSpy).completeTask(task.getId(), isForced);
        doReturn(classificationQueryImplMock).when(classificationServiceImplMock).createClassificationQuery();
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).domain(any());
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).key(any());
        doReturn(new ArrayList<>()).when(classificationQueryImplMock).list();
        List<ClassificationSummaryImpl> classificationList = Arrays
            .asList((ClassificationSummaryImpl) dummyClassification.asSummary());
        doReturn(classificationList).when(
            classificationQueryImplMock)
            .list();

        Task actualTask = cut.completeTask(task.getId());

        verify(taskanaEngineImpl, times(2)).openConnection();
        verify(taskMapperMock, times(1)).findById(task.getId());
        verify(attachmentMapperMock, times(1)).findAttachmentsByTaskId(task.getId());
        verify(classificationServiceImplMock, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).domain(any());
        verify(classificationQueryImplMock, times(1)).key(any());
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(2)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(task.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
    }

    @Test
    public void testCompleteTaskNotForcedWorking()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, InterruptedException,
        ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final long sleepTime = 100L;
        final boolean isForced = false;
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        // created and modify should be able to be different.
        Thread.sleep(sleepTime);
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        task.setOwner(CurrentUserContext.getUserid());
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(task);

        Task actualTask = cutSpy.completeTask(task.getId(), isForced);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(task.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
        assertThat(actualTask.getCompleted(), equalTo(actualTask.getModified()));
    }

    @Test(expected = InvalidStateException.class)
    public void testCompleteTaskNotForcedNotClaimedBefore()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, ClassificationNotFoundException {
        final boolean isForced = false;
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setState(TaskState.READY);
        task.setClaimed(null);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.completeTask(task.getId(), isForced);
        } catch (InvalidStateException e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidOwnerException.class)
    public void testCompleteTaskNotForcedInvalidOwnerException()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, ClassificationNotFoundException {
        final boolean isForced = false;
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setOwner("Dummy-Owner-ID: 10");
        task.setState(TaskState.CLAIMED);
        task.setClaimed(Instant.now());
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.completeTask(task.getId(), isForced);
        } catch (InvalidOwnerException e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = TaskNotFoundException.class)
    public void testCompleteTaskTaskNotFound()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final boolean isForced = false;
        String taskId = "1";
        doThrow(TaskNotFoundException.class).when(cutSpy).getTask(taskId);
        try {
            cutSpy.completeTask(taskId, isForced);
        } catch (InvalidOwnerException e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(taskId);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCompleteForcedAndAlreadyClaimed()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, InterruptedException,
        ClassificationNotFoundException {
        final boolean isForced = true;
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

        Task actualTask = cutSpy.completeTask(task.getId(), isForced);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(task.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
        assertThat(actualTask.getCompleted(), equalTo(actualTask.getModified()));
    }

    @Test
    public void testCompleteForcedNotClaimed()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, InterruptedException,
        ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        final boolean isForced = true;
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
        doReturn(claimedTask).when(cutSpy).claim(task.getId(), isForced);
        doNothing().when(taskMapperMock).update(claimedTask);

        Task actualTask = cutSpy.completeTask(task.getId(), isForced);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(cutSpy, times(1)).claim(task.getId(), isForced);
        verify(taskMapperMock, times(1)).update(claimedTask);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
        assertThat(actualTask.getState(), equalTo(TaskState.COMPLETED));
        assertThat(actualTask.getCreated(), not(equalTo(claimedTask.getModified())));
        assertThat(actualTask.getCompleted(), not(equalTo(null)));
        assertThat(actualTask.getCompleted(), equalTo(actualTask.getModified()));
    }

    @Test
    public void testTransferTaskToDestinationWorkbasketWithoutSecurity()
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, InvalidWorkbasketException, ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Workbasket destinationWorkbasket = createWorkbasket("2", "k1");
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "k1", dummyClassification);
        task.setRead(true);
        doReturn(destinationWorkbasket).when(workbasketServiceMock).getWorkbasketByKey(destinationWorkbasket.getKey());
        doReturn(taskanaEngineConfigurationMock).when(taskanaEngineMock).getConfiguration();
        doReturn(false).when(taskanaEngineConfigurationMock).isSecurityEnabled();
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(any());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasket.getKey(),
            WorkbasketAuthorization.APPEND);
        doNothing().when(workbasketServiceMock).checkAuthorization(task.getWorkbasketKey(),
            WorkbasketAuthorization.TRANSFER);

        Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getKey());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasket.getKey(),
            WorkbasketAuthorization.APPEND);
        verify(workbasketServiceMock, times(1)).checkAuthorization(task.getWorkbasketKey(),
            WorkbasketAuthorization.TRANSFER);
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(destinationWorkbasket.getKey());
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.isRead(), equalTo(false));
        assertThat(actualTask.isTransferred(), equalTo(true));
        assertThat(actualTask.getWorkbasketKey(), equalTo(destinationWorkbasket.getKey()));
    }

    @Test
    public void testTransferTaskToDestinationWorkbasketUsingSecurityTrue()
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, InvalidWorkbasketException, ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Workbasket destinationWorkbasket = createWorkbasket("2", "k2");
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "k1", dummyClassification);
        task.setRead(true);
        doReturn(taskanaEngineConfigurationMock).when(taskanaEngineMock).getConfiguration();
        doReturn(true).when(taskanaEngineConfigurationMock).isSecurityEnabled();
        doReturn(task).when(cutSpy).getTask(task.getId());
        doReturn(destinationWorkbasket).when(workbasketServiceMock).getWorkbasketByKey(destinationWorkbasket.getKey());
        doNothing().when(taskMapperMock).update(any());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasket.getKey(),
            WorkbasketAuthorization.APPEND);
        doNothing().when(workbasketServiceMock).checkAuthorization(task.getWorkbasketKey(),
            WorkbasketAuthorization.TRANSFER);
        Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getKey());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasket.getKey(),
            WorkbasketAuthorization.APPEND);
        verify(workbasketServiceMock, times(1)).checkAuthorization("k1",
            WorkbasketAuthorization.TRANSFER);
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(destinationWorkbasket.getKey());
        verify(taskanaEngineMock, times(0)).getConfiguration();
        verify(taskanaEngineConfigurationMock, times(0)).isSecurityEnabled();
        verify(taskMapperMock, times(1)).update(any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);

        assertThat(actualTask.isRead(), equalTo(false));
        assertThat(actualTask.isTransferred(), equalTo(true));
        assertThat(actualTask.getWorkbasketKey(), equalTo(destinationWorkbasket.getKey()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testTransferDestinationWorkbasketDoesNotExist()
        throws Exception {

        String destinationWorkbasketKey = "2";
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doThrow(WorkbasketNotFoundException.class).when(workbasketServiceMock)
            .checkAuthorization(destinationWorkbasketKey, WorkbasketAuthorization.APPEND);
        doReturn(task).when(cutSpy).getTask(task.getId());

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketKey);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketKey,
                WorkbasketAuthorization.APPEND);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
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
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testTransferNotAuthorizationOnWorkbasketAppend()
        throws Exception {
        String destinationWorkbasketKey = "2";
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(destinationWorkbasketKey,
            WorkbasketAuthorization.APPEND);

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketKey);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketKey,
                WorkbasketAuthorization.APPEND);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void testTransferNotAuthorizationOnWorkbasketTransfer()
        throws Exception {
        String destinationWorkbasketKey = "2";
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(workbasketServiceMock).checkAuthorization(destinationWorkbasketKey,
            WorkbasketAuthorization.APPEND);
        doThrow(NotAuthorizedException.class).when(workbasketServiceMock).checkAuthorization(task.getWorkbasketKey(),
            WorkbasketAuthorization.TRANSFER);

        try {
            cutSpy.transfer(task.getId(), destinationWorkbasketKey);
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(workbasketServiceMock, times(1)).checkAuthorization(destinationWorkbasketKey,
                WorkbasketAuthorization.APPEND);
            verify(workbasketServiceMock, times(1)).checkAuthorization(task.getWorkbasketKey(),
                WorkbasketAuthorization.TRANSFER);
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testSetTaskReadWIthExistingTask()
        throws TaskNotFoundException, ClassificationAlreadyExistException, ClassificationNotFoundException {
        TaskServiceImpl cutSpy = Mockito.spy(cut);
        Classification dummyClassification = createDummyClassification();
        TaskImpl task = createUnitTestTask("1", "Unit Test Task 1", "1", dummyClassification);
        task.setModified(null);
        doReturn(task).when(cutSpy).getTask(task.getId());
        doNothing().when(taskMapperMock).update(task);

        Task actualTask = cutSpy.setTaskRead("1", true);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).update(task);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
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
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl,
                taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testGetTaskByIdWithExistingTask()
        throws TaskNotFoundException, ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification dummyClassification = createDummyClassification();
        Task expectedTask = createUnitTestTask("1", "DUMMY-TASK", "1", dummyClassification);
        doReturn(expectedTask).when(taskMapperMock).findById(expectedTask.getId());
        doReturn(null).when(attachmentMapperMock).findAttachmentsByTaskId(expectedTask.getId());

        doReturn(classificationQueryImplMock).when(classificationServiceImplMock).createClassificationQuery();
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).domain(any());
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).key(any());
        doReturn(new ArrayList<>()).when(classificationQueryImplMock).list();

        List<ClassificationSummaryImpl> classificationList = Arrays
            .asList((ClassificationSummaryImpl) dummyClassification.asSummary());
        doReturn(classificationList).when(
            classificationQueryImplMock)
            .list();
        Task actualTask = cut.getTask(expectedTask.getId());

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findById(expectedTask.getId());
        verify(attachmentMapperMock, times(1)).findAttachmentsByTaskId(expectedTask.getId());
        verify(classificationServiceImplMock, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).domain(any());
        verify(classificationQueryImplMock, times(1)).key(any());
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
        assertThat(actualTask, equalTo(expectedTask));
    }

    @Test(expected = TaskNotFoundException.class)
    public void testGetTaskByIdWhereTaskDoesNotExist() throws Exception {
        Classification dummyClassification = createDummyClassification();
        Task task = createUnitTestTask("1", "DUMMY-TASK", "1", dummyClassification);
        doThrow(TaskNotFoundException.class).when(taskMapperMock).findById(task.getId());

        try {
            cut.getTask(task.getId());
        } catch (Exception e) {
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(taskMapperMock, times(1)).findById(task.getId());
            verify(taskanaEngineImpl, times(1)).returnConnection();
            verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
                taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
                classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdWithInternalException()
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        // given - set behaviour and expected result
        String workbasketKey = "1";
        List<TaskSummaryImpl> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImpl).openConnection();
        doThrow(new IllegalArgumentException("Invalid ID: " + workbasketKey)).when(taskMapperMock)
            .findTaskSummariesByWorkbasketKey(workbasketKey);
        doNothing().when(taskanaEngineImpl).returnConnection();
        doReturn(new WorkbasketImpl()).when(workbasketServiceMock).getWorkbasket(any());

        // when - make the call
        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketKey(workbasketKey);

        // then - verify external communications and assert result
        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findTaskSummariesByWorkbasketKey(workbasketKey);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(any());
        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock, sqlSessionMock,
            classificationQueryImplMock);
        assertThat(actualResultList, equalTo(expectedResultList));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingResults()
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        String workbasketKey = "1";
        WorkbasketSummaryImpl wbs = new WorkbasketSummaryImpl();
        wbs.setDomain("domain");
        wbs.setKey("key");
        ClassificationSummaryImpl cl = new ClassificationSummaryImpl();
        cl.setDomain("domain");
        cl.setKey("clKey");
        TaskSummaryImpl t1 = new TaskSummaryImpl();
        t1.setWorkbasketSummary(wbs);
        t1.setDomain("domain");
        t1.setClassificationSummary(cl);
        t1.setTaskId("007");
        TaskSummaryImpl t2 = new TaskSummaryImpl();
        t2.setWorkbasketSummary(wbs);
        t2.setDomain("domain");
        t2.setClassificationSummary(cl);
        t2.setTaskId("008");
        List<TaskSummaryImpl> expectedResultList = Arrays.asList(t1, t2);
        List<ClassificationSummaryImpl> expectedClassifications = Arrays.asList(cl);
        List<WorkbasketSummaryImpl> expectedWorkbaskets = Arrays.asList(wbs);

        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();
        doReturn(new WorkbasketImpl()).when(workbasketServiceMock).getWorkbasketByKey(any());
        doReturn(expectedResultList).when(taskMapperMock).findTaskSummariesByWorkbasketKey(workbasketKey);
        doReturn(classificationQueryImplMock).when(classificationServiceImplMock).createClassificationQuery();
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).key(any());
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).domain(any());
        doReturn(workbasketQueryImplMock).when(workbasketServiceMock).createWorkbasketQuery();
        doReturn(workbasketQueryImplMock).when(workbasketQueryImplMock).keyIn(any());
        doReturn(expectedWorkbaskets).when(workbasketQueryImplMock).list();

        doReturn(expectedClassifications).when(classificationQueryImplMock).list();

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketKey(workbasketKey);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findTaskSummariesByWorkbasketKey(workbasketKey);
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(workbasketKey);
        verify(classificationServiceImplMock, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).domain(any());
        verify(classificationQueryImplMock, times(1)).key(any());
        verify(classificationQueryImplMock, times(1)).list();
        verify(workbasketServiceMock, times(1)).createWorkbasketQuery();
        verify(workbasketQueryImplMock, times(1)).keyIn(any());
        verify(workbasketQueryImplMock, times(1)).list();

        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(attachmentMapperMock, times(1)).findAttachmentSummariesByTaskIds(any());

        verifyNoMoreInteractions(attachmentMapperMock, taskanaEngineConfigurationMock, taskanaEngineMock,
            taskanaEngineImpl, taskMapperMock, objectReferenceMapperMock, workbasketServiceMock,
            workbasketQueryImplMock,
            sqlSessionMock, classificationQueryImplMock);
        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingNull()
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        String workbasketKey = "1";
        List<TaskSummaryImpl> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();
        doReturn(null).when(taskMapperMock).findTaskSummariesByWorkbasketKey(workbasketKey);
        doReturn(new WorkbasketImpl()).when(workbasketServiceMock).getWorkbasketByKey(any());

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketKey(workbasketKey);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMapperMock, times(1)).findTaskSummariesByWorkbasketKey(workbasketKey);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verify(workbasketServiceMock, times(1)).getWorkbasketByKey(any());
        verifyNoMoreInteractions(attachmentMapperMock, taskMapperMock, taskanaEngineImpl, workbasketServiceMock);

        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }

    @Test
    public void testUpdateTaskAddingValidAttachment() throws TaskNotFoundException, SystemException,
        WorkbasketNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        InvalidWorkbasketException, NotAuthorizedException, AttachmentPersistenceException {
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

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).insert(((AttachmentImpl) attachment));
        verify(taskanaEngineImpl, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(1));
    }

    @Test
    public void testUpdateTaskAddingValidAttachmentTwice() throws TaskNotFoundException, SystemException,
        WorkbasketNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        InvalidWorkbasketException, NotAuthorizedException, AttachmentPersistenceException {
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

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).insert(((AttachmentImpl) attachment));
        verify(taskanaEngineImpl, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(1));
    }

    @Test(expected = AttachmentPersistenceException.class)
    public void testUpdateTaskAddingAttachmentWithSameIdForcedUsingingListMethod()
        throws TaskNotFoundException, SystemException,
        WorkbasketNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        InvalidWorkbasketException, NotAuthorizedException, AttachmentPersistenceException {
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
            verify(taskanaEngineImpl, times(1)).openConnection();
            verify(cutSpy, times(1)).getTask(task.getId());
            verify(attachmentMapperMock, times(1)).insert(((AttachmentImpl) attachment));
            verify(taskanaEngineImpl, times(1)).returnConnection();
            throw e;
        }
    }

    @Test
    public void testUpdateTaskUpdateAttachment() throws TaskNotFoundException, SystemException,
        WorkbasketNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        InvalidWorkbasketException, NotAuthorizedException, AttachmentPersistenceException {
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

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).update(((AttachmentImpl) attachmentToUpdate));
        verify(taskanaEngineImpl, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(1));
        assertThat(actualTask.getAttachments().get(0).getChannel(), equalTo(channelUpdate));
    }

    @Test
    public void testUpdateTaskRemovingAttachment() throws TaskNotFoundException, SystemException,
        WorkbasketNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        InvalidWorkbasketException, NotAuthorizedException, AttachmentPersistenceException {
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

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(cutSpy, times(1)).getTask(task.getId());
        verify(attachmentMapperMock, times(1)).deleteAttachment(attachment.getId());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        assertThat(actualTask.getAttachments().size(), equalTo(0));
    }

    private TaskImpl createUnitTestTask(String id, String name, String workbasketKey, Classification classification) {
        TaskImpl task = new TaskImpl();
        task.setId(id);
        task.setName(name);
        task.setWorkbasketKey(workbasketKey);
        task.setDomain("");
        task.setAttachments(new ArrayList<>());
        Instant now = Instant.now();
        task.setCreated(now);
        task.setModified(now);
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
        return classification;
    }

    private WorkbasketImpl createWorkbasket(String id, String key) {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setId(id);
        workbasket.setKey(key);
        workbasket.setName("Workbasket " + id);
        return workbasket;
    }
}
