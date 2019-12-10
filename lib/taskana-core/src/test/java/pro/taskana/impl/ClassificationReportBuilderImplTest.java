package pro.taskana.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DetailedMonitorQueryItem;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.impl.report.row.FoldableRow;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.ClassificationReport.DetailedClassificationReport;

/**
 * Unit Test for ClassificationReportBuilderImpl.
 */
@ExtendWith(MockitoExtension.class)
class ClassificationReportBuilderImplTest {

    @InjectMocks
    private TaskMonitorServiceImpl cut;

    @Mock
    private InternalTaskanaEngine internalTaskanaEngineMock;

    @Mock
    private TaskanaEngine taskanaEngineMock;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Mock
    private TaskMonitorMapper taskMonitorMapperMock;

    @BeforeEach
    void setup() {
        when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfiguration);
        when(taskanaEngineConfiguration.isGermanPublicHolidaysEnabled()).thenReturn(true);
        when(taskanaEngineConfiguration.getCustomHolidays()).thenReturn(null);
    }

    @Test
    void testGetTotalNumbersOfClassificationReport() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        when(taskMonitorMapperMock.getTaskCountOfClassifications(workbasketIds, states, categories, domains,
            classificationIds, excludedClassificationIds, customAttributeFilter)).thenReturn(expectedResult);

        ClassificationReport actualResult = cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfClassifications(any(), any(), any(), any(), any(), any(),
            any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getRow("CLI:000000000000000000000000000000000001").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    void testGetClassificationReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

        List<TimeIntervalColumnHeader> columnHeaders = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        when(taskMonitorMapperMock.getTaskCountOfClassifications(workbasketIds, states, categories, domains,
            classificationIds, excludedClassificationIds, customAttributeFilter)).thenReturn(expectedResult);

        ClassificationReport actualResult = cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(internalTaskanaEngineMock, times(3)).getEngine();

        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfClassifications(any(), any(), any(), any(), any(), any(),
            any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(
            actualResult.getRow("CLI:000000000000000000000000000000000001").getTotalValue(), 1);
        assertEquals(actualResult.getRow("CLI:000000000000000000000000000000000001").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    void testGetTotalNumbersOfDetailedClassificationReport()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

        List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
        DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
        detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
        detailedMonitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(detailedMonitorQueryItem);
        when(taskMonitorMapperMock.getTaskCountOfDetailedClassifications(workbasketIds, states, categories, domains,
            classificationIds, excludedClassificationIds, customAttributeFilter)).thenReturn(expectedResult);

        DetailedClassificationReport actualResult = cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildDetailedReport();

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfDetailedClassifications(any(), any(), any(), any(), any(),
            any(),
            any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        FoldableRow<DetailedMonitorQueryItem> line = actualResult.getRow("CLI:000000000000000000000000000000000001");
        assertNotNull(actualResult);
        assertEquals(line.getTotalValue(), 1);
        assertEquals(line.getFoldableRow("CLI:000000000000000000000000000000000006").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    void testGetDetailedClassificationReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
        DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
        detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
        detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
        detailedMonitorQueryItem.setAgeInDays(0);
        detailedMonitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(detailedMonitorQueryItem);
        when(taskMonitorMapperMock.getTaskCountOfDetailedClassifications(workbasketIds, states, categories, domains,
            classificationIds, excludedClassificationIds, customAttributeFilter)).thenReturn(expectedResult);

        DetailedClassificationReport actualResult = cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfDetailedClassifications(any(), any(), any(), any(), any(),
            any(),
            any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        FoldableRow<DetailedMonitorQueryItem> line = actualResult.getRow("CLI:000000000000000000000000000000000001");
        assertNotNull(actualResult);
        assertEquals(line.getTotalValue(), 1);
        assertEquals(line.getFoldableRow("CLI:000000000000000000000000000000000006").getTotalValue(), 1);
        assertEquals(line.getCells()[0], 1);
        assertEquals(line.getFoldableRow("CLI:000000000000000000000000000000000006").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getCells()[0], 1);
    }

    @Test
    void testGetTaskIdsForSelectedItems() throws InvalidArgumentException, NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        SelectedItem selectedItem = new SelectedItem();
        selectedItem.setKey("EXTERN");
        selectedItem.setLowerAgeLimit(1);
        selectedItem.setUpperAgeLimit(5);
        List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);

        List<String> expectedResult = Collections.singletonList("TKI:000000000000000000000000000000000001");
        when(taskMonitorMapperMock.getTaskIdsForSelectedItems(workbasketIds,
            states, categories, domains, classificationIds, excludedClassificationIds, customAttributeFilter,
            "CLASSIFICATION_KEY", selectedItems, false)).thenReturn(expectedResult);

        List<String> actualResult = cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listTaskIdsForSelectedItems(selectedItems);

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getTaskIdsForSelectedItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(false));
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetTaskIdsForSelectedItemsIsEmptyResult() throws NotAuthorizedException, InvalidArgumentException {
        SelectedItem selectedItem = new SelectedItem();
        selectedItem.setKey("GIBTSNED");
        List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);
        List<String> result = cut.createClassificationReportBuilder()
            .workbasketIdIn(Arrays.asList("DieGibtsEhNed"))
            .listTaskIdsForSelectedItems(selectedItems);
        assertNotNull(result);
    }

    @Test
    void testListCustomAttributeValuesForCustomAttributeName()
        throws NotAuthorizedException {
        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
        List<String> categories = Collections.singletonList("EXTERN");
        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        SelectedItem selectedItem = new SelectedItem();
        selectedItem.setKey("EXTERN");
        selectedItem.setLowerAgeLimit(1);
        selectedItem.setUpperAgeLimit(5);
        List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);

        List<String> expectedResult = Collections.singletonList("Geschaeftsstelle A");
        when(taskMonitorMapperMock.getCustomAttributeValuesForReport(workbasketIds,
            states, categories, domains, classificationIds, excludedClassificationIds, customAttributeFilter,
            CustomField.CUSTOM_1)).thenReturn(expectedResult);

        List<String> actualResult = cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_1);

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getCustomAttributeValuesForReport(any(), any(), any(), any(), any(), any(), any(), any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testListCustomAttributeValuesForCustomAttributeNameIsEmptyResult()
        throws NotAuthorizedException {
        List<String> result = cut.createClassificationReportBuilder()
            .workbasketIdIn(Collections.singletonList("DieGibtsGarantiertNed"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_10);
        assertNotNull(result);
    }

}
