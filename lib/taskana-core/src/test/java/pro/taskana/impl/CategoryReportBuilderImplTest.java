package pro.taskana.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

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
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.CategoryReport;

/**
 * Unit Test for CategoryBuilderImpl.
 */
@ExtendWith(MockitoExtension.class)
class CategoryReportBuilderImplTest {

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
        when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfiguration);
        when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
        when(taskanaEngineConfiguration.isGermanPublicHolidaysEnabled()).thenReturn(true);
        when(taskanaEngineConfiguration.getCustomHolidays()).thenReturn(null);
    }

    @Test
    void testGetTotalNumbersOfCatgoryReport() throws InvalidArgumentException, NotAuthorizedException {
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
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        when(taskMonitorMapperMock.getTaskCountOfCategories(workbasketIds, states, categories, domains,
            classificationIds, excludedClassificationIds, customAttributeFilter)).thenReturn(expectedResult);

        CategoryReport actualResult = cut.createCategoryReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

        verify(internalTaskanaEngineMock, times(1)).openConnection();
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategories(any(), any(), any(), any(), any(), any(),
            any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("EXTERN").getTotalValue(), 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    void testGetCategoryReportWithReportLineItemDefinitions()
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
        monitorQueryItem.setKey("EXTERN");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        when(taskMonitorMapperMock.getTaskCountOfCategories(workbasketIds, states, categories, domains,
            classificationIds,
            excludedClassificationIds, customAttributeFilter)).thenReturn(expectedResult);

        CategoryReport actualResult = cut.createCategoryReportBuilder()
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
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCategories(any(), any(), any(), any(), any(), any(),
            any());
        verify(internalTaskanaEngineMock, times(1)).returnConnection();
        verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, taskMonitorMapperMock,
            taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("EXTERN").getTotalValue(), 1);
        assertEquals(actualResult.getRow("EXTERN").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    void testListTaskIdsOfCategoryReportForSelectedItems()
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

        SelectedItem selectedItem = new SelectedItem();
        selectedItem.setKey("EXTERN");
        selectedItem.setLowerAgeLimit(1);
        selectedItem.setUpperAgeLimit(5);
        List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);

        List<String> expectedResult = Collections.singletonList("TKI:000000000000000000000000000000000001");
        when(taskMonitorMapperMock.getTaskIdsForSelectedItems(workbasketIds,
            states, categories, domains, classificationIds, excludedClassificationIds, customAttributeFilter,
            "CLASSIFICATION_CATEGORY", selectedItems, false)).thenReturn(expectedResult);

        List<String> actualResult = cut.createCategoryReportBuilder()
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
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
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
    void testListTaskIdsForSelectedItemsIsEmptyResult() throws NotAuthorizedException, InvalidArgumentException {
        SelectedItem selectedItem = new SelectedItem();
        List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);
        List<String> result = cut.createCategoryReportBuilder()
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

        List<String> actualResult = cut.createCategoryReportBuilder()
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
        verify(internalTaskanaEngineMock, times(3)).getEngine();
        verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineMock, times(2)).getConfiguration();
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
    void testListCustomAttributeValuesForCustomAttributeNameIsEmptyResult() throws NotAuthorizedException {
        List<String> result = cut.createCategoryReportBuilder()
            .workbasketIdIn(Arrays.asList("DieGibtsSicherNed"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_1);
        assertNotNull(result);
    }
}
