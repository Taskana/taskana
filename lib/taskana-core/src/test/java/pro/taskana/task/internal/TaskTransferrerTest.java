package pro.taskana.task.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.ClassificationQueryImpl;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * Unit Test for TaskServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class TaskTransferrerTest {

  private TaskTransferrer cut;
  @Mock private TaskServiceImpl taskServiceImplMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskMapper taskMapperMock;

  @Mock private ObjectReferenceMapper objectReferenceMapperMock;

  @Mock private WorkbasketService workbasketServiceMock;

  @Mock private ClassificationServiceImpl classificationServiceImplMock;

  @Mock private AttachmentMapper attachmentMapperMock;

  @Mock private ClassificationQueryImpl classificationQueryImplMock;

  @Mock private SqlSession sqlSessionMock;

  @Test
  void testTransferTaskToDestinationWorkbasketWithoutSecurity()
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {

    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getWorkbasketService()).thenReturn(workbasketServiceMock);
    cut = new TaskTransferrer(internalTaskanaEngineMock, taskMapperMock, taskServiceImplMock);

    final TaskTransferrer cutSpy = Mockito.spy(cut);
    Workbasket destinationWorkbasket = CreateTaskModelHelper.createWorkbasket("2", "k1");
    Workbasket sourceWorkbasket = CreateTaskModelHelper.createWorkbasket("47", "key47");
    Classification dummyClassification = CreateTaskModelHelper.createDummyClassification();
    TaskImpl task =
        CreateTaskModelHelper
            .createUnitTestTask("1", "Unit Test Task 1", "key47", dummyClassification);
    task.setWorkbasketSummary(sourceWorkbasket.asSummary());
    task.setRead(true);
    when(workbasketServiceMock.getWorkbasket(destinationWorkbasket.getId()))
        .thenReturn(destinationWorkbasket);
    doReturn(task).when(taskServiceImplMock).getTask(task.getId());

    final Task actualTask = cutSpy.transfer(task.getId(), destinationWorkbasket.getId());

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(workbasketServiceMock, times(1))
        .checkAuthorization(destinationWorkbasket.getId(), WorkbasketPermission.APPEND);
    verify(workbasketServiceMock, times(1))
        .checkAuthorization(sourceWorkbasket.getId(), WorkbasketPermission.TRANSFER);
    verify(workbasketServiceMock, times(1)).getWorkbasket(destinationWorkbasket.getId());
    verify(taskMapperMock, times(1)).update(any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verify(internalTaskanaEngineMock, times(1)).getEngine();
    verify(internalTaskanaEngineMock).getHistoryEventProducer();
    verify(taskanaEngineMock).getWorkbasketService();
    verifyNoMoreInteractions(
        attachmentMapperMock,
        taskanaEngineConfigurationMock,
        taskanaEngineMock,
        internalTaskanaEngineMock,
        taskMapperMock,
        objectReferenceMapperMock,
        workbasketServiceMock,
        sqlSessionMock,
        classificationQueryImplMock);

    assertThat(actualTask.isRead(), equalTo(false));
    assertThat(actualTask.getState(), equalTo(TaskState.READY));
    assertThat(actualTask.isTransferred(), equalTo(true));
    assertThat(actualTask.getWorkbasketKey(), equalTo(destinationWorkbasket.getKey()));
  }
}
