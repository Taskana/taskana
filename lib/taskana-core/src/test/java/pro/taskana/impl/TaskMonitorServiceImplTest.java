package pro.taskana.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
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

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.ClassificationReport;
import pro.taskana.impl.report.impl.CustomFieldValueReport;
import pro.taskana.impl.report.impl.DetailedClassificationReport;
import pro.taskana.impl.report.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.report.impl.DetailedReportRow;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TaskQueryItem;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.report.impl.WorkbasketLevelReport;
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
    public void testGetTotalNumbersOfWorkbasketLevelReport() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfWorkbaskets(workbasketIds, states,
            categories, domains, customField, customFieldValues);

        WorkbasketLevelReport actualResult = cut.getWorkbasketLevelReport(workbasketIds, states, categories, domains,
            customField, customFieldValues);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfWorkbaskets(any(), any(), any(),
            any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetWorkbasketLevelReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> reportLineItemDefinitions = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfWorkbaskets(workbasketIds, states,
            categories, domains, customField, customFieldValues);

        WorkbasketLevelReport actualResult = cut.getWorkbasketLevelReport(workbasketIds, states, categories, domains,
            customField, customFieldValues, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfWorkbaskets(any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue(), 1);
        assertEquals(actualResult.getRow("WBI:000000000000000000000000000000000001").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetTotalNumbersOfCatgoryReport() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfCategories(workbasketIds, states, categories,
            domains, customField, customFieldValues);

        CategoryReport actualResult = cut.getCategoryReport(workbasketIds, states, categories, domains,
            customField, customFieldValues);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategories(any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("EXTERN").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetCategoryReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> reportLineItemDefinitions = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfCategories(workbasketIds, states, categories,
            domains, customField, customFieldValues);

        CategoryReport actualResult = cut.getCategoryReport(workbasketIds, states, categories, domains,
            customField, customFieldValues, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategories(any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("EXTERN").getTotalValue(), 1);
        assertEquals(actualResult.getRow("EXTERN").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetTotalNumbersOfClassificationReport() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfClassifications(workbasketIds, states,
            categories, domains, customField, customFieldValues);

        ClassificationReport actualResult = cut.getClassificationReport(workbasketIds, states, categories, domains,
            customField, customFieldValues);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfClassifications(any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getRow("CLI:000000000000000000000000000000000001").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetClassificationReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");

        List<TimeIntervalColumnHeader> reportLineItemDefinitions = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfClassifications(workbasketIds, states,
            categories, domains, customField, customFieldValues);

        ClassificationReport actualResult = cut.getClassificationReport(workbasketIds, states, categories, domains,
            customField, customFieldValues, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfClassifications(any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getRow("CLI:000000000000000000000000000000000001").getTotalValue(), 1);
        assertEquals(actualResult.getRow("CLI:000000000000000000000000000000000001").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetTotalNumbersOfDetailedClassificationReport()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");

        List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
        DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
        detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
        detailedMonitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(detailedMonitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfDetailedClassifications(workbasketIds,
            states, categories, domains, customField, customFieldValues);

        DetailedClassificationReport actualResult = cut.getDetailedClassificationReport(workbasketIds, states,
            categories, domains, customField, customFieldValues);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfDetailedClassifications(any(), any(), any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        DetailedReportRow line = actualResult.getRow("CLI:000000000000000000000000000000000001");
        assertNotNull(actualResult);
        assertEquals(line.getTotalValue(), 1);
        assertEquals(line.getDetailRows().get("CLI:000000000000000000000000000000000006").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetDetailedClassificationReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> reportLineItemDefinitions = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
        DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
        detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
        detailedMonitorQueryItem.setAgeInDays(0);
        detailedMonitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(detailedMonitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock).getTaskCountOfDetailedClassifications(workbasketIds,
            states, categories, domains, customField, customFieldValues);

        DetailedClassificationReport actualResult = cut.getDetailedClassificationReport(workbasketIds, states,
            categories, domains, customField, customFieldValues, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfDetailedClassifications(any(), any(), any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        DetailedReportRow line = actualResult.getRow("CLI:000000000000000000000000000000000001");
        assertNotNull(actualResult);
        assertEquals(line.getTotalValue(), 1);
        assertEquals(line.getDetailRows().get("CLI:000000000000000000000000000000000006").getTotalValue(), 1);
        assertEquals(line.getCells()[0], 1);
        assertEquals(line.getDetailRows().get("CLI:000000000000000000000000000000000006").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getCells()[0], 1);
    }

    @Test
    public void testGetTotalNumbersOfCustomFieldValueReport() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("Geschaeftsstelle A");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfCustomFieldValues(workbasketIds, states, categories, domains, customField,
                customFieldValues);

        CustomFieldValueReport actualResult = cut.getCustomFieldValueReport(workbasketIds, states, categories, domains,
            customField, customFieldValues);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCustomFieldValues(any(), any(), any(), any(), any(),
            any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("Geschaeftsstelle A").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetCustomFieldValueReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> reportLineItemDefinitions = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("Geschaeftsstelle A");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfCustomFieldValues(workbasketIds, states, categories, domains, customField,
                customFieldValues);

        CustomFieldValueReport actualResult = cut.getCustomFieldValueReport(workbasketIds, states, categories, domains,
            customField, customFieldValues, reportLineItemDefinitions);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getTaskCountOfCustomFieldValues(any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("Geschaeftsstelle A").getTotalValue(), 1);
        assertEquals(actualResult.getRow("Geschaeftsstelle A").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testGetTaskIdsForSelectedItems() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> reportLineItemDefinitions = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        SelectedItem selectedItem = new SelectedItem();
        selectedItem.setKey("EXTERN");
        selectedItem.setLowerAgeLimit(1);
        selectedItem.setUpperAgeLimit(5);
        List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);

        List<String> expectedResult = Collections.singletonList("TKI:000000000000000000000000000000000001");
        when(taskMonitorMapperMock.getTaskIdsForSelectedItems(workbasketIds,
            states, categories, domains, classificationIds, excludedClassificationIds, customField, customFieldValues,
            "CLASSIFICATION_CATEGORY", selectedItems, false)).thenReturn(expectedResult);

        List<String> actualResult = cut.getTaskIdsForSelectedItems(workbasketIds, states, categories, domains,
            classificationIds, excludedClassificationIds,
            customField, customFieldValues, reportLineItemDefinitions, true, selectedItems,
            TaskMonitorService.DIMENSION_CLASSIFICATION_CATEGORY);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getTaskIdsForSelectedItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                eq(false));
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
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
        TaskStatusReport report = cut.getTaskStatusReport();

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
        TaskStatusReport report = cut.getTaskStatusReport(null, Collections.emptyList());

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
