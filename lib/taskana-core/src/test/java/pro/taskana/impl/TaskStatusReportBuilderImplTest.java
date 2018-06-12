package pro.taskana.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.TaskState;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.TaskQueryItem;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * Unit Test for TaskStatusReportBuilderImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskStatusReportBuilderImplTest {

    @InjectMocks
    private TaskMonitorServiceImpl cut;

    @Mock
    private TaskanaEngineImpl taskanaEngineImplMock;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Mock
    private TaskMonitorMapper taskMonitorMapperMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(taskanaEngineImplMock).openConnection();
        Mockito.doNothing().when(taskanaEngineImplMock).returnConnection();
    }

    @Test
    public void testGetTaskStateReportWithoutFilters() throws NotAuthorizedException {
        // given
        TaskQueryItem queryItem1 = new TaskQueryItem();
        queryItem1.setCount(50);
        queryItem1.setState(TaskState.READY);
        queryItem1.setDomain("DOMAIN_X");
        TaskQueryItem queryItem2 = new TaskQueryItem();
        queryItem2.setCount(30);
        queryItem2.setState(TaskState.COMPLETED);
        queryItem2.setDomain("DOMAIN_X");
        List<TaskQueryItem> queryItems = Arrays.asList(queryItem1, queryItem2);
        when(taskMonitorMapperMock.getTasksCountByState(null, null)).thenReturn(queryItems);

        // when
        TaskStatusReport report = cut.createTaskStatusReportBuilder().buildReport();

        // then
        InOrder inOrder = inOrder(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineImplMock);
        inOrder.verify(taskanaEngineImplMock).openConnection();
        inOrder.verify(taskMonitorMapperMock).getTasksCountByState(eq(null), eq(null));
        inOrder.verify(taskanaEngineImplMock).returnConnection();

        assertNotNull(report);
        assertEquals(1, report.rowSize());
        assertArrayEquals(new int[] {50, 0, 30}, report.getRow("DOMAIN_X").getCells());
        assertArrayEquals(new int[] {50, 0, 30}, report.getSumRow().getCells());
        assertEquals(80, report.getRow("DOMAIN_X").getTotalValue());
        assertEquals(80, report.getSumRow().getTotalValue());
    }

    @Test
    public void testGetTotalNumberOfTaskStateReport() throws NotAuthorizedException {
        // given
        TaskQueryItem queryItem1 = new TaskQueryItem();
        queryItem1.setCount(50);
        queryItem1.setState(TaskState.READY);
        queryItem1.setDomain("DOMAIN_X");
        TaskQueryItem queryItem2 = new TaskQueryItem();
        queryItem2.setCount(30);
        queryItem2.setState(TaskState.COMPLETED);
        queryItem2.setDomain("DOMAIN_X");
        List<TaskQueryItem> queryItems = Arrays.asList(queryItem1, queryItem2);
        when(taskMonitorMapperMock.getTasksCountByState(eq(null), eq(Collections.emptyList()))).thenReturn(queryItems);

        // when
        TaskStatusReport report = cut.createTaskStatusReportBuilder().stateIn(Collections.emptyList()).buildReport();

        // then
        InOrder inOrder = inOrder(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineImplMock);
        inOrder.verify(taskanaEngineImplMock).openConnection();
        inOrder.verify(taskMonitorMapperMock).getTasksCountByState(eq(null), eq(Collections.emptyList()));
        inOrder.verify(taskanaEngineImplMock).returnConnection();

        assertNotNull(report);
        assertEquals(1, report.rowSize());
        assertArrayEquals(new int[0], report.getRow("DOMAIN_X").getCells());
        assertArrayEquals(new int[0], report.getSumRow().getCells());
        assertEquals(80, report.getRow("DOMAIN_X").getTotalValue());
        assertEquals(80, report.getSumRow().getTotalValue());
    }
}
