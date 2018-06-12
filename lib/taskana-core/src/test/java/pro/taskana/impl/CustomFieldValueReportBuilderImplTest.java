package pro.taskana.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.CustomFieldValueReport;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * Unit Test for CustomFieldValueReportBuilderImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomFieldValueReportBuilderImplTest {

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
    public void testGetTotalNumbersOfCustomFieldValueReport() throws InvalidArgumentException, NotAuthorizedException {
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
        monitorQueryItem.setKey("Geschaeftsstelle A");
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfCustomFieldValues(CustomField.CUSTOM_1, workbasketIds, states, categories, domains,
                classificationIds, excludedClassificationIds, customAttributeFilter);

        CustomFieldValueReport actualResult = cut.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1)).getTaskCountOfCustomFieldValues(any(), any(), any(), any(), any(),
            any(), any(),
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
        List<String> classificationIds = Collections.singletonList("L10000");
        List<String> excludedClassificationIds = Collections.singletonList("L20000");
        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = Collections.singletonList(
            new TimeIntervalColumnHeader(0, 0));

        List<MonitorQueryItem> expectedResult = new ArrayList<>();
        MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
        monitorQueryItem.setKey("Geschaeftsstelle A");
        monitorQueryItem.setAgeInDays(0);
        monitorQueryItem.setNumberOfTasks(1);
        expectedResult.add(monitorQueryItem);
        doReturn(expectedResult).when(taskMonitorMapperMock)
            .getTaskCountOfCustomFieldValues(CustomField.CUSTOM_1, workbasketIds, states, categories, domains,
                classificationIds, excludedClassificationIds, customAttributeFilter);

        CustomFieldValueReport actualResult = cut.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getTaskCountOfCustomFieldValues(any(), any(), any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(actualResult.getRow("Geschaeftsstelle A").getTotalValue(), 1);
        assertEquals(actualResult.getRow("Geschaeftsstelle A").getCells()[0], 1);
        assertEquals(actualResult.getSumRow().getTotalValue(), 1);
    }

    @Test
    public void testListCustomAttributeValuesForCustomAttributeName()
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

        List<String> actualResult = cut.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_1);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(2)).getConfiguration();
        verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
        verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
        verify(taskMonitorMapperMock, times(1))
            .getCustomAttributeValuesForReport(any(), any(), any(), any(), any(), any(), any(), any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskanaEngineImplMock, taskMonitorMapperMock, taskanaEngineConfiguration);

        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }
}
