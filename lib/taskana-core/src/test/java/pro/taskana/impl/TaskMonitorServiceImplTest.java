package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.Report;
import pro.taskana.model.ReportLine;
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
            any(Instant.class),
            any());

        List<DueWorkbasketCounter> actualResult = cut.getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast,
            taskStates);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountByWorkbasketIdAndDaysInPastAndState(any(Instant.class),
            any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void testGetWorkbasketLevelReport() {
        List<Workbasket> workbaskets = Arrays.asList(new WorkbasketImpl(), new WorkbasketImpl());
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);

        Report expectedResult = new Report();
        List<ReportLine> expectedDetailLines = new ArrayList<>();
        doReturn(expectedDetailLines).when(taskMonitorMapperMock).getDetailLinesByWorkbasketIdsAndStates(any(), any());

        Report actualResult = cut.getWorkbasketLevelReport(workbaskets, states);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getDetailLinesByWorkbasketIdsAndStates(any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);
        Assert.assertNotNull(actualResult);
        assertThat(actualResult.getDetailLines(), equalTo(expectedResult.getDetailLines()));
    }

}
