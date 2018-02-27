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

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.TaskState;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * Unit Test for TaskMonitorServiceImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskMonitorServiceImplTest {

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
        doReturn(taskanaEngineConfiguration).when(taskanaEngineImplMock).getConfiguration();
        doReturn(true).when(taskanaEngineConfiguration).isGermanPublicHolidaysEnabled();
        doReturn(null).when(taskanaEngineConfiguration).getCustomHolidays();
    }

    @Test
    public void testGetTotalNumbersOfWorkbasketLevelReport() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfWorkbasketsByWorkbasketsAndStates(
            workbasketIds, states, categories, domains);

        Report actualResult = cut.getWorkbasketLevelReport(workbasketIds, states, categories, domains);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfWorkbasketsByWorkbasketsAndStates(any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getReportLines().get("WBI:000000000000000000000000000000000001").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetWorkbasketLevelReportWithReportLineItemDefinitions() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfWorkbasketsByWorkbasketsAndStates(
            workbasketIds, states, categories, domains);

        Report actualResult = cut.getWorkbasketLevelReport(workbasketIds, states, categories, domains,
            reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfWorkbasketsByWorkbasketsAndStates(any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getReportLines().get("WBI:000000000000000000000000000000000001").getTotalNumberOfTasks(), 1);
        assertEquals(
            actualResult.getReportLines()
                .get("WBI:000000000000000000000000000000000001")
                .getLineItems()
                .get(0)
                .getNumberOfTasks(),
            1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetTotalNumbersOfCatgoryReport() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfCategoriesByWorkbasketsAndStates(
            workbasketIds, states, categories, domains);

        Report actualResult = cut.getCategoryReport(workbasketIds, states, categories, domains);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategoriesByWorkbasketsAndStates(any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getReportLines().get("EXTERN").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetCategoryReportWithReportLineItemDefinitions() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfCategoriesByWorkbasketsAndStates(
            workbasketIds, states, categories, domains);

        Report actualResult = cut.getCategoryReport(workbasketIds, states, categories, domains,
            reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategoriesByWorkbasketsAndStates(any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getReportLines().get("EXTERN").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getReportLines().get("EXTERN").getLineItems().get(0).getNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetTotalNumbersOfClassificationReport() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfClassificationsByWorkbasketsAndStates(
            workbasketIds, states, categories, domains);

        ClassificationReport actualResult = cut.getClassificationReport(workbasketIds, states, categories, domains);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfClassificationsByWorkbasketsAndStates(any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getReportLines().get("CLI:000000000000000000000000000000000001").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetClassificationReportWithReportLineItemDefinitions() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfClassificationsByWorkbasketsAndStates(
            workbasketIds, states, categories, domains);

        ClassificationReport actualResult = cut.getClassificationReport(workbasketIds, states, categories, domains,
            reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfClassificationsByWorkbasketsAndStates(any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getReportLines().get("CLI:000000000000000000000000000000000001").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getReportLines()
            .get("CLI:000000000000000000000000000000000001")
            .getLineItems()
            .get(0)
            .getNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetTotalNumbersOfDetailedClassificationReport() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");

        List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
        DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
        detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
        detailedMonitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(detailedMonitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(workbasketIds, states, categories, domains);

        DetailedClassificationReport actualResult = cut.getDetailedClassificationReport(workbasketIds, states,
            categories, domains);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(any(),
            any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        DetailedReportLine line = (DetailedReportLine) actualResult.getReportLines()
            .get("CLI:000000000000000000000000000000000001");
        assertNotNull(actualResult);
        assertEquals(line.getTotalNumberOfTasks(), 1);
        assertEquals(line.getDetailLines().get("CLI:000000000000000000000000000000000006").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetDetailedClassificationReportWithReportLineItemDefinitions() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
        DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
        detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
        detailedMonitorQueryItem.setAgeInDays(0);
        detailedMonitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(detailedMonitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(workbasketIds, states, categories, domains);

        DetailedClassificationReport actualResult = cut.getDetailedClassificationReport(workbasketIds, states,
            categories, domains, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(any(),
            any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        DetailedReportLine line = (DetailedReportLine) actualResult.getReportLines()
            .get("CLI:000000000000000000000000000000000001");
        assertNotNull(actualResult);
        assertEquals(line.getTotalNumberOfTasks(), 1);
        assertEquals(line.getDetailLines().get("CLI:000000000000000000000000000000000006").getTotalNumberOfTasks(), 1);
        assertEquals(line.getLineItems().get(0).getNumberOfTasks(), 1);
        assertEquals(line.getDetailLines()
            .get("CLI:000000000000000000000000000000000006")
            .getLineItems()
            .get(0)
            .getNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getLineItems().get(0).getNumberOfTasks(), 1);
    }

    @Test
    public void testGetTotalNumbersOfCustomFieldValueReport() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("Geschaeftsstelle A");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(
                workbasketIds, states, categories, domains, CustomField.CUSTOM_1);

        Report actualResult = cut.getCustomFieldValueReport(workbasketIds, states, categories, domains,
            CustomField.CUSTOM_1);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getReportLines().get("Geschaeftsstelle A").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }

    @Test
    public void testGetCustomFieldValueReportWithReportLineItemDefinitions() {
        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Arrays.asList("EXTERN");
        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = Arrays.asList(new ReportLineItemDefinition(),
            new ReportLineItemDefinition());

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("Geschaeftsstelle A");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(
                workbasketIds, states, categories, domains, CustomField.CUSTOM_1);

        Report actualResult = cut.getCustomFieldValueReport(workbasketIds, states, categories, domains,
            CustomField.CUSTOM_1, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getReportLines().get("Geschaeftsstelle A").getTotalNumberOfTasks(), 1);
        assertEquals(actualResult.getReportLines().get("Geschaeftsstelle A").getLineItems().get(0).getNumberOfTasks(),
            1);
        assertEquals(actualResult.getSumLine().getTotalNumberOfTasks(), 1);
    }
}
