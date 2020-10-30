package pro.taskana.monitor.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketService;

/** Unit Test for TaskStatusReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class TaskStatusReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private MonitorMapper monitorMapperMock;

  @Mock private WorkbasketService workbasketService;

  private Object[] mocks;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getWorkbasketService()).thenReturn(workbasketService);
    mocks =
        new Object[] {
          internalTaskanaEngineMock, taskanaEngineMock, monitorMapperMock, workbasketService
        };
  }

  @Test
  void testGetTaskStateReportWithoutFilters() throws Exception {
    // given
    TaskQueryItem queryItem1 = new TaskQueryItem();
    queryItem1.setCount(50);
    queryItem1.setState(TaskState.READY);
    queryItem1.setWorkbasketKey("KEY_1");
    TaskQueryItem queryItem2 = new TaskQueryItem();
    queryItem2.setCount(30);
    queryItem2.setState(TaskState.COMPLETED);
    queryItem2.setWorkbasketKey("KEY_1");
    List<TaskQueryItem> queryItems = List.of(queryItem1, queryItem2);
    when(monitorMapperMock.getTasksCountByState(null, null, null)).thenReturn(queryItems);
    when(internalTaskanaEngineMock.runAsAdmin(any())).thenReturn(Map.of());

    // when
    final TaskStatusReport report = cut.createTaskStatusReportBuilder().buildReport();

    // then
    InOrder inOrder = inOrder(mocks);
    inOrder.verify(internalTaskanaEngineMock).getEngine();
    inOrder.verify(taskanaEngineMock).checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    inOrder.verify(internalTaskanaEngineMock).openConnection();
    inOrder.verify(monitorMapperMock).getTasksCountByState(eq(null), eq(null), eq(null));
    inOrder.verify(internalTaskanaEngineMock).runAsAdmin(any());
    inOrder.verify(internalTaskanaEngineMock).returnConnection();
    inOrder.verifyNoMoreInteractions();
    verifyNoMoreInteractions(mocks);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);
    assertThat(report.getRow("KEY_1").getCells()).isEqualTo(new int[] {50, 0, 30, 0, 0});
    assertThat(report.getSumRow().getCells()).isEqualTo(new int[] {50, 0, 30, 0, 0});
    assertThat(report.getRow("KEY_1").getTotalValue()).isEqualTo(80);
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(80);
  }

  @Test
  void testGetTotalNumberOfTaskStateReport() throws Exception {
    // given
    TaskQueryItem queryItem1 = new TaskQueryItem();
    queryItem1.setCount(50);
    queryItem1.setState(TaskState.READY);
    queryItem1.setWorkbasketKey("KEY_1");
    TaskQueryItem queryItem2 = new TaskQueryItem();
    queryItem2.setCount(30);
    queryItem2.setState(TaskState.COMPLETED);
    queryItem2.setWorkbasketKey("KEY_1");
    List<TaskQueryItem> queryItems = List.of(queryItem1, queryItem2);
    when(monitorMapperMock.getTasksCountByState(eq(null), eq(List.of()), eq(null)))
        .thenReturn(queryItems);
    when(internalTaskanaEngineMock.runAsAdmin(any())).thenReturn(Map.of());

    // when
    final TaskStatusReport report =
        cut.createTaskStatusReportBuilder().stateIn(List.of()).buildReport();

    // then
    InOrder inOrder = inOrder(mocks);
    inOrder.verify(internalTaskanaEngineMock).getEngine();
    inOrder.verify(taskanaEngineMock).checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    inOrder.verify(internalTaskanaEngineMock).openConnection();
    inOrder.verify(monitorMapperMock).getTasksCountByState(eq(null), eq(List.of()), eq(null));
    inOrder.verify(internalTaskanaEngineMock).runAsAdmin(any());
    inOrder.verify(internalTaskanaEngineMock).returnConnection();
    inOrder.verifyNoMoreInteractions();
    verifyNoMoreInteractions(mocks);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);
    assertThat(report.getRow("KEY_1").getCells()).isEqualTo(new int[0]);
    assertThat(report.getSumRow().getCells()).isEqualTo(new int[0]);
    assertThat(report.getRow("KEY_1").getTotalValue()).isEqualTo(80);
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(80);
  }
}
