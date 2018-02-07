package pro.taskana.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.model.MonitorQueryItem;
import pro.taskana.model.Report;
import pro.taskana.model.ReportLineItemDefinition;
import pro.taskana.model.TaskState;
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
    public void testGetTotalNumbersOfWorkbasketLevelReport() {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setName("workbasket");
        workbasket.setKey("wb1");
        List<Workbasket> workbaskets = Arrays.asList(workbasket);
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("wb1");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfWorkbasketsByWorkbasketsAndStates(
            workbaskets, states);

        Report actualResult = cut.getWorkbasketLevelReport(workbaskets, states);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfWorkbasketsByWorkbasketsAndStates(any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertNotNull(actualResult);
        assertEquals(actualResult.getDetailLines().get(workbasket.getKey()).getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetWorkbasketLevelReportWithReportLineItemDefinitions() {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setName("workbasket");
        workbasket.setKey("wb1");
        List<Workbasket> workbaskets = Arrays.asList(workbasket);
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("wb1");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfWorkbasketsByWorkbasketsAndStates(
            workbaskets, states);

        Report actualResult = cut.getWorkbasketLevelReport(workbaskets, states, reportLineItemDefinitions);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfWorkbasketsByWorkbasketsAndStates(any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertNotNull(actualResult);
        assertEquals(actualResult.getDetailLines().get(workbasket.getKey()).getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getDetailLines().get(workbasket.getKey()).getLineItems().get(0).getNumberOfTasks(),
            1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetTotalNumbersOfCatgoryReport() {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setName("workbasket");
        workbasket.setKey("wb1");
        List<Workbasket> workbaskets = Arrays.asList(workbasket);
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfCategoriesByWorkbasketsAndStates(
            workbaskets, states);

        Report actualResult = cut.getCategoryReport(workbaskets, states);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategoriesByWorkbasketsAndStates(any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertNotNull(actualResult);
        assertEquals(actualResult.getDetailLines().get("EXTERN").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetCategoryReportWithReportLineItemDefinitions() {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setName("workbasket");
        workbasket.setKey("wb1");
        List<Workbasket> workbaskets = Arrays.asList(workbasket);
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfCategoriesByWorkbasketsAndStates(
            workbaskets, states);

        Report actualResult = cut.getCategoryReport(workbaskets, states, reportLineItemDefinitions);

        verify(taskanaEngineImpl, times(1)).openConnection();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategoriesByWorkbasketsAndStates(any(), any());
        verify(taskanaEngineImpl, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineConfigurationMock, taskanaEngineMock, taskanaEngineImpl,
            taskMonitorMapperMock, objectReferenceMapperMock, workbasketServiceMock);

        assertNotNull(actualResult);
        assertEquals(actualResult.getDetailLines().get("EXTERN").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getDetailLines().get("EXTERN").getLineItems().get(0).getNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }
}
