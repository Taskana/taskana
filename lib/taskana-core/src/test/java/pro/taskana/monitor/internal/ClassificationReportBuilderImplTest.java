package pro.taskana.monitor.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationQuery;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.ClassificationReport.DetailedClassificationReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Unit Test for ClassificationReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class ClassificationReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;
  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;
  @Mock private TaskanaEngine taskanaEngineMock;
  @Mock private TaskanaEngineConfiguration taskanaEngineConfiguration;
  @Mock private MonitorMapper monitorMapperMock;
  @Mock private ClassificationService classificationService;

  private Object[] mocks;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getClassificationService()).thenReturn(classificationService);
    mocks =
        new Object[] {
          internalTaskanaEngineMock,
          taskanaEngineMock,
          monitorMapperMock,
          taskanaEngineConfiguration,
          classificationService
        };
  }

  @Test
  void testGetTotalNumbersOfClassificationReport() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfClassifications(
            workbasketIds,
            states,
            categories,
            domains,
            TaskTimestamp.DUE,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    ClassificationQuery queryMock = mock(ClassificationQuery.class);
    when(classificationService.createClassificationQuery()).thenReturn(queryMock);
    when(queryMock.keyIn(any())).thenReturn(queryMock);
    when(queryMock.domainIn(any())).thenReturn(queryMock);
    when(queryMock.list()).thenReturn(Collections.emptyList());

    final ClassificationReport actualResult =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(taskanaEngineMock).getClassificationService();
    verify(internalTaskanaEngineMock, times(3)).getEngine();

    verify(monitorMapperMock)
        .getTaskCountOfClassifications(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();

    verifyNoMoreInteractions(queryMock);
    verifyNoMoreInteractions(mocks);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("CLI:000000000000000000000000000000000001").getTotalValue())
        .isEqualTo(1);
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
  }

  @Test
  void testGetClassificationReportWithReportLineItemDefinitions() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");

    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
    monitorQueryItem.setAgeInDays(0);
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfClassifications(
            workbasketIds,
            states,
            categories,
            domains,
            TaskTimestamp.DUE,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);
    ClassificationQuery queryMock = mock(ClassificationQuery.class);
    when(classificationService.createClassificationQuery()).thenReturn(queryMock);
    when(queryMock.keyIn(any())).thenReturn(queryMock);
    when(queryMock.domainIn(any())).thenReturn(queryMock);
    when(queryMock.list()).thenReturn(Collections.emptyList());

    final ClassificationReport actualResult =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(taskanaEngineMock).getClassificationService();
    verify(internalTaskanaEngineMock, times(3)).getEngine();

    verify(monitorMapperMock)
        .getTaskCountOfClassifications(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(queryMock);
    verifyNoMoreInteractions(mocks);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("CLI:000000000000000000000000000000000001").getTotalValue())
        .isOne();
    assertThat(actualResult.getRow("CLI:000000000000000000000000000000000001").getCells())
        .isEqualTo(new int[] {1});
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
  }

  @Test
  void testGetTotalNumbersOfDetailedClassificationReport() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");

    final List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
    DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
    detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
    detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
    detailedMonitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(detailedMonitorQueryItem);
    when(monitorMapperMock.getTaskCountOfDetailedClassifications(
            workbasketIds,
            states,
            categories,
            domains,
            TaskTimestamp.DUE,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);
    ClassificationQuery queryMock = mock(ClassificationQuery.class);
    when(classificationService.createClassificationQuery()).thenReturn(queryMock);
    when(queryMock.keyIn(any())).thenReturn(queryMock);
    when(queryMock.domainIn(any())).thenReturn(queryMock);
    when(queryMock.list()).thenReturn(Collections.emptyList());

    final DetailedClassificationReport actualResult =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildDetailedReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(taskanaEngineMock).getClassificationService();
    verify(internalTaskanaEngineMock, times(3)).getEngine();

    verify(monitorMapperMock)
        .getTaskCountOfDetailedClassifications(
            any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(queryMock);
    verifyNoMoreInteractions(mocks);

    FoldableRow<DetailedMonitorQueryItem> line =
        actualResult.getRow("CLI:000000000000000000000000000000000001");
    assertThat(actualResult).isNotNull();
    assertThat(line.getTotalValue()).isOne();
    assertThat(line.getFoldableRow("CLI:000000000000000000000000000000000006").getTotalValue())
        .isOne();
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
  }

  @Test
  void testGetDetailedClassificationReportWithReportLineItemDefinitions() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    final List<DetailedMonitorQueryItem> expectedResult = new ArrayList<>();
    DetailedMonitorQueryItem detailedMonitorQueryItem = new DetailedMonitorQueryItem();
    detailedMonitorQueryItem.setKey("CLI:000000000000000000000000000000000001");
    detailedMonitorQueryItem.setAttachmentKey("CLI:000000000000000000000000000000000006");
    detailedMonitorQueryItem.setAgeInDays(0);
    detailedMonitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(detailedMonitorQueryItem);
    when(monitorMapperMock.getTaskCountOfDetailedClassifications(
            workbasketIds,
            states,
            categories,
            domains,
            TaskTimestamp.DUE,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);
    ClassificationQuery queryMock = mock(ClassificationQuery.class);
    when(classificationService.createClassificationQuery()).thenReturn(queryMock);
    when(queryMock.keyIn(any())).thenReturn(queryMock);
    when(queryMock.domainIn(any())).thenReturn(queryMock);
    when(queryMock.list()).thenReturn(Collections.emptyList());

    final DetailedClassificationReport actualResult =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(taskanaEngineMock).getClassificationService();
    verify(internalTaskanaEngineMock, times(3)).getEngine();

    verify(monitorMapperMock)
        .getTaskCountOfDetailedClassifications(
            any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(queryMock);
    verifyNoMoreInteractions(mocks);

    FoldableRow<DetailedMonitorQueryItem> line =
        actualResult.getRow("CLI:000000000000000000000000000000000001");
    assertThat(actualResult).isNotNull();
    assertThat(line.getTotalValue()).isOne();
    assertThat(line.getFoldableRow("CLI:000000000000000000000000000000000006").getTotalValue())
        .isOne();
    assertThat(line.getCells()).isEqualTo(new int[] {1});
    assertThat(line.getFoldableRow("CLI:000000000000000000000000000000000006").getCells())
        .isEqualTo(new int[] {1});
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
    assertThat(actualResult.getSumRow().getCells()).isEqualTo(new int[] {1});
  }

  @Test
  void testGetTaskIdsForSelectedItems() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    final List<SelectedItem> selectedItems =
        Collections.singletonList(new SelectedItem("EXTERN", null, 1, 5));

    final List<String> expectedResult =
        Collections.singletonList("TKI:000000000000000000000000000000000001");
    when(monitorMapperMock.getTaskIdsForSelectedItems(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            null,
            "CLASSIFICATION_KEY",
            TaskTimestamp.DUE,
            selectedItems,
            false))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(3)).getEngine();

    verify(monitorMapperMock)
        .getTaskIdsForSelectedItems(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            eq(TaskTimestamp.DUE),
            any(),
            eq(false));
    verify(internalTaskanaEngineMock).returnConnection();
    verify(taskanaEngineMock).getClassificationService();
    verifyNoMoreInteractions(mocks);

    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testGetTaskIdsForSelectedItemsIsEmptyResult() throws Exception {
    List<SelectedItem> selectedItems =
        Collections.singletonList(new SelectedItem("GIBTSNED", null, 0, 0));
    List<String> result =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(Collections.singletonList("DieGibtsEhNed"))
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);
    assertThat(result).isNotNull();
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeName() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    final List<String> expectedResult = Collections.singletonList("Geschaeftsstelle A");
    when(monitorMapperMock.getCustomAttributeValuesForReport(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            null,
            TaskCustomField.CUSTOM_1))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_1);

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(3)).getEngine();

    verify(monitorMapperMock)
        .getCustomAttributeValuesForReport(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verify(taskanaEngineMock).getClassificationService();
    verifyNoMoreInteractions(mocks);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeNameIsEmptyResult() throws Exception {
    List<String> result =
        cut.createClassificationReportBuilder()
            .workbasketIdIn(Collections.singletonList("DieGibtsGarantiertNed"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_10);
    assertThat(result).isNotNull();
  }
}
