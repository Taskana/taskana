package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.Date;
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

import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMonitorMapper;

/**
 * Unit Test for TaskMonitorServiceImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskMonitorServiceImplTest {

    @InjectMocks
    private TaskMonitorServiceImpl cut;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineImpl;

    @Mock
    private TaskMonitorMapper taskMonitorMapperMock;

    @Mock
    private ObjectReferenceMapper objectReferenceMapperMock;

    @Mock
    private WorkbasketService workbasketServiceMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(taskanaEngineImpl).openConnection();
        Mockito.doNothing().when(taskanaEngineImpl).returnConnection();
    }

    @Test
    public void testGetTaskCountForState() {
        List<TaskState> taskStates = Arrays.asList(TaskState.CLAIMED, TaskState.COMPLETED);
        List<TaskStateCounter> expectedResult = new ArrayList<>();
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountForState(taskStates);

        List<TaskStateCounter> actualResult = cut.getTaskCountForState(taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountForState(taskStates);
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void testGetTaskCountForWorkbasketByDaysInPastAndState() {
        List<TaskState> taskStates = Arrays.asList(TaskState.CLAIMED, TaskState.COMPLETED);
        final long daysInPast = 10L;
        final long expectedResult = 5L;
        String workbasketId = "1";
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountForWorkbasketByDaysInPastAndState(any(), any(),
            any());

        long actualResult = cut.getTaskCountForWorkbasketByDaysInPastAndState(workbasketId, daysInPast, taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountForWorkbasketByDaysInPastAndState(any(), any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void testGetTaskCountByWorkbasketAndDaysInPastAndState() {
        final long daysInPast = 10L;
        List<TaskState> taskStates = Arrays.asList(TaskState.CLAIMED, TaskState.COMPLETED);
        List<DueWorkbasketCounter> expectedResult = new ArrayList<>();
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountByWorkbasketIdAndDaysInPastAndState(
            any(Date.class),
            any());

        List<DueWorkbasketCounter> actualResult = cut.getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast,
            taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountByWorkbasketIdAndDaysInPastAndState(any(Date.class), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

}
