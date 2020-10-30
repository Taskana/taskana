package pro.taskana.workbasket.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/** Unit Test for workbasketServiceImpl. */
@ExtendWith(MockitoExtension.class)
class WorkbasketServiceImplTest {

  @Spy @InjectMocks private WorkbasketServiceImpl workbasketServiceSpy;

  @Mock private WorkbasketMapper workbasketMapperMock;

  @Mock private DistributionTargetMapper distributionTargetMapperMock;

  @Mock private WorkbasketAccessMapper workbasketAccessMapperMock;

  @Mock private TaskService taskServiceMock;

  @Mock private TaskQuery taskQueryMock;

  @Mock private TaskanaEngine taskanaEngine;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

  @BeforeEach
  void setup() {
    lenient().when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngine);
  }

  @Test
  void testCreateWorkbasket_WithDistributionTargets() throws Exception {
    final int distTargetAmount = 2;
    WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
    doReturn(expectedWb).when(workbasketServiceSpy).getWorkbasket(any());
    when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);

    final Workbasket actualWb = workbasketServiceSpy.createWorkbasket(expectedWb);
    workbasketServiceSpy.setDistributionTargets(
        expectedWb.getId(), createTestDistributionTargets(distTargetAmount));

    verify(internalTaskanaEngineMock, times(4)).openConnection();
    verify(workbasketMapperMock, times(3)).insert(any());
    verify(workbasketServiceSpy, times(distTargetAmount + 1)).getWorkbasket(any());
    verify(distributionTargetMapperMock, times(1)).deleteAllDistributionTargetsBySourceId(any());
    verify(distributionTargetMapperMock, times(distTargetAmount)).insert(any(), any());
    verify(workbasketMapperMock, times(3)).findByKeyAndDomain(any(), any());
    verify(workbasketMapperMock, times(1)).update(any());
    verify(internalTaskanaEngineMock, times(4)).returnConnection();
    verify(taskanaEngine, times(4)).checkRoleMembership(any());
    verify(internalTaskanaEngineMock, times(4)).getEngine();
    verify(internalTaskanaEngineMock, times(3)).domainExists(any());
    verify(internalTaskanaEngineMock, times(1)).getHistoryEventManager();
    verifyNoMoreInteractions(
        taskQueryMock,
        taskServiceMock,
        workbasketMapperMock,
        workbasketAccessMapperMock,
        distributionTargetMapperMock,
        internalTaskanaEngineMock,
        taskanaEngine,
        taskanaEngineConfigurationMock);
    assertThat(actualWb.getId()).isNotNull();
    assertThat(actualWb.getId()).startsWith("WBI");
    assertThat(actualWb.getCreated()).isNotNull();
    assertThat(actualWb.getModified()).isNotNull();
  }

  @Test
  void testCreateWorkbasket_DistributionTargetNotExisting() throws Exception {
    WorkbasketImpl expectedWb = createTestWorkbasket("ID-1", "Key-1");
    when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);
    String otherWorkbasketId = "4711";
    List<String> destinations = List.of(otherWorkbasketId);
    workbasketServiceSpy.createWorkbasket(expectedWb);
    doReturn(expectedWb).when(workbasketServiceSpy).getWorkbasket(eq(expectedWb.getId()));

    ThrowingCallable call =
        () -> {
          workbasketServiceSpy.setDistributionTargets(expectedWb.getId(), destinations);
        };
    assertThatThrownBy(call)
        .isInstanceOf(WorkbasketNotFoundException.class)
        .hasFieldOrPropertyWithValue("id", otherWorkbasketId)
        .hasFieldOrPropertyWithValue("key", null)
        .hasFieldOrPropertyWithValue("domain", null);

    verify(internalTaskanaEngineMock, times(3)).openConnection();
    verify(workbasketMapperMock, times(1)).insert(expectedWb);
    verify(workbasketMapperMock, times(1)).findById(any());
    verify(workbasketMapperMock, times(1)).findByKeyAndDomain(any(), any());
    verify(workbasketServiceSpy, times(2)).getWorkbasket(any());
    verify(internalTaskanaEngineMock, times(3)).returnConnection();
    verify(taskanaEngine, times(2)).checkRoleMembership(any());
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(internalTaskanaEngineMock, times(1)).domainExists(any());
    verify(distributionTargetMapperMock)
        .deleteAllDistributionTargetsBySourceId(eq(expectedWb.getId()));
    verify(workbasketMapperMock).update(eq(expectedWb));
    verify(internalTaskanaEngineMock, times(1)).getHistoryEventManager();

    verifyNoMoreInteractions(
        taskQueryMock,
        taskServiceMock,
        workbasketMapperMock,
        workbasketAccessMapperMock,
        distributionTargetMapperMock,
        internalTaskanaEngineMock,
        taskanaEngine,
        taskanaEngineConfigurationMock);
  }

  @Test
  void testDeleteWorkbasketIsUsed() throws Exception {
    Workbasket wb = createTestWorkbasket("WBI:0", "wb-key");

    ThrowingCallable call =
        () -> {
          workbasketServiceSpy.deleteWorkbasket(wb.getId());
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);

    verify(internalTaskanaEngineMock, times(2)).openConnection();
    verify(workbasketServiceSpy, times(1)).getWorkbasket(wb.getId());
    verify(taskanaEngine, times(0)).getTaskService();
    verify(taskServiceMock, times(0)).createTaskQuery();
    verify(taskQueryMock, times(0)).workbasketIdIn(wb.getId());
    verify(taskQueryMock, times(0)).count();
    verify(internalTaskanaEngineMock, times(2)).returnConnection();
    verifyNoMoreInteractions(
        taskQueryMock,
        taskServiceMock,
        workbasketAccessMapperMock,
        distributionTargetMapperMock,
        taskanaEngineConfigurationMock);
  }

  @Test
  void testSetWorkbasketAccessItemsWithMultipleAccessIds() {

    String wid = "workbasketId";
    List<WorkbasketAccessItem> accessItems =
        IntStream.rangeClosed(0, 10)
            .mapToObj(i -> createWorkbasketAccessItem("id" + i, "access" + i, wid))
            .collect(Collectors.toList());
    accessItems.add(createWorkbasketAccessItem("id5", "access5", wid));

    assertThatThrownBy(() -> workbasketServiceSpy.setWorkbasketAccessItems(wid, accessItems))
        .isInstanceOf(WorkbasketAccessItemAlreadyExistException.class);
  }

  @Test
  void testCheckModifiedHasNotChanged() {

    Instant expectedModifiedTimestamp = Instant.now();

    WorkbasketImpl oldWb = createTestWorkbasket(null, "Key-1");
    WorkbasketImpl workbasketImplToUpdate = createTestWorkbasket(null, "Key-2");
    oldWb.setModified(expectedModifiedTimestamp);
    workbasketImplToUpdate.setModified(expectedModifiedTimestamp);

    ThrowingCallable call =
        () -> workbasketServiceSpy.checkModifiedHasNotChanged(oldWb, workbasketImplToUpdate);
    assertThatCode(call).doesNotThrowAnyException();

    workbasketImplToUpdate.setModified(expectedModifiedTimestamp.minus(1, ChronoUnit.HOURS));

    call = () -> workbasketServiceSpy.checkModifiedHasNotChanged(oldWb, workbasketImplToUpdate);
    assertThatThrownBy(call).isInstanceOf(ConcurrencyException.class);
  }

  private WorkbasketImpl createTestWorkbasket(String id, String key) {
    WorkbasketImpl workbasket = new WorkbasketImpl();
    workbasket.setId(id);
    workbasket.setKey(key);
    workbasket.setName("Workbasket " + id);
    workbasket.setDescription("Description WB with Key " + key);
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket.setDomain("DOMAIN_A");
    return workbasket;
  }

  private List<String> createTestDistributionTargets(int amount) throws Exception {
    List<String> distributionsTargets = new ArrayList<>();
    amount = Math.max(amount, 0);
    for (int i = 0; i < amount; i++) {
      WorkbasketImpl wb = createTestWorkbasket("WB-ID-" + i, "WB-KEY-" + i);
      workbasketServiceSpy.createWorkbasket(wb);
      distributionsTargets.add(wb.getId());
    }
    return distributionsTargets;
  }

  private WorkbasketAccessItem createWorkbasketAccessItem(
      String id, String accessId, String workbasketId) {
    WorkbasketAccessItemImpl workbasketAccessItem = new WorkbasketAccessItemImpl();
    workbasketAccessItem.setId(id);
    workbasketAccessItem.setAccessId(accessId);
    workbasketAccessItem.setWorkbasketId(workbasketId);
    return workbasketAccessItem;
  }
}
