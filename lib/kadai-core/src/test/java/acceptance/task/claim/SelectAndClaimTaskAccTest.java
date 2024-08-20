package acceptance.task.claim;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.security.UserPrincipal;
import io.kadai.common.internal.util.CheckedConsumer;
import io.kadai.common.internal.util.Pair;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.common.test.util.ParallelThreadHelper;
import io.kadai.task.api.TaskQuery;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.security.auth.Subject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(JaasExtension.class)
class SelectAndClaimTaskAccTest extends AbstractAccTest {
  Workbasket wbWithoutRead;
  Workbasket wbWithoutReadTasks;
  Workbasket wbWithoutEditTasks;
  Task task1;
  Task task2;
  Task task3;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    wbWithoutRead = createWorkBasket();
    wbWithoutReadTasks = createWorkBasket();
    wbWithoutEditTasks = createWorkBasket();

    createWorkbasketAccessItem(wbWithoutRead, WorkbasketPermission.READ);
    createWorkbasketAccessItem(wbWithoutReadTasks, WorkbasketPermission.READTASKS);
    createWorkbasketAccessItem(wbWithoutEditTasks, WorkbasketPermission.EDITTASKS);

    task3 = createTask(wbWithoutEditTasks);
    task1 = createTask(wbWithoutRead);
    task2 = createTask(wbWithoutReadTasks);
  }

  @Test
  void should_ClaimDifferentTasks_For_ConcurrentSelectAndClaimCalls() throws Exception {

    List<Task> selectedAndClaimedTasks = Collections.synchronizedList(new ArrayList<>());

    List<String> accessIds =
        Collections.synchronizedList(
            Stream.of("admin", "teamlead-1", "teamlead-2", "taskadmin")
                .collect(Collectors.toList()));

    ParallelThreadHelper.runInThread(
        getRunnableTest(selectedAndClaimedTasks, accessIds), accessIds.size());

    assertThat(selectedAndClaimedTasks)
        .extracting(Task::getId)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000003",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000005",
            "TKI:000000000000000000000000000000000006");

    assertThat(selectedAndClaimedTasks)
        .extracting(Task::getOwner)
        .containsExactlyInAnyOrder("admin", "taskadmin", "teamlead-1", "teamlead-2");
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ReturnEmptyOptional_When_MissingOnePermission() {
    List<Pair<String, Task>> list =
        List.of(
            Pair.of("With Missing Read Permission", task1),
            Pair.of("With Missing ReadTasks Permission", task2),
            Pair.of("With Missing EditTasks Permission", task3));
    ThrowingConsumer<Pair<String, Task>> testSelectClaimTask =
        t -> {
          TaskQuery query = taskService.createTaskQuery().idIn(t.getRight().getId());
          Optional<Task> task = taskService.selectAndClaim(query);
          assertThat(task).isEmpty();
        };
    return DynamicTest.stream(list.iterator(), Pair::getLeft, testSelectClaimTask);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_ReturnEmptyOptional_When_TryingToSelectAndClaimNonExistingTask() throws Exception {

    TaskQuery query = kadaiEngine.getTaskService().createTaskQuery().idIn("notexisting");
    Optional<Task> task = kadaiEngine.getTaskService().selectAndClaim(query);
    assertThat(task).isEmpty();
  }

  private Runnable getRunnableTest(List<Task> selectedAndClaimedTasks, List<String> accessIds) {
    return () -> {
      Subject subject = new Subject();
      subject.getPrincipals().add(new UserPrincipal(accessIds.remove(0)));

      Consumer<TaskService> consumer =
          CheckedConsumer.wrap(
              taskService ->
                  taskService
                      .selectAndClaim(getTaskQuery())
                      .ifPresent(selectedAndClaimedTasks::add));
      PrivilegedAction<Void> action =
          () -> {
            consumer.accept(kadaiEngine.getTaskService());
            return null;
          };
      Subject.doAs(subject, action);
    };
  }

  private TaskQuery getTaskQuery() {
    return kadaiEngine.getTaskService().createTaskQuery().orderByTaskId(SortDirection.ASCENDING);
  }

  private Workbasket createWorkBasket() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    Workbasket workbasket =
        workbasketService.newWorkbasket(UUID.randomUUID().toString(), "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    return workbasket;
  }

  private void createWorkbasketAccessItem(Workbasket workbasket, WorkbasketPermission missingPerm)
      throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    if (missingPerm == WorkbasketPermission.READ) {
      wbai.setPermission(WorkbasketPermission.READTASKS, true);
      wbai.setPermission(WorkbasketPermission.EDITTASKS, true);
    } else if (missingPerm == WorkbasketPermission.READTASKS) {
      wbai.setPermission(WorkbasketPermission.READ, true);
      wbai.setPermission(WorkbasketPermission.EDITTASKS, true);
    } else {
      wbai.setPermission(WorkbasketPermission.READ, true);
      wbai.setPermission(WorkbasketPermission.READTASKS, true);
    }
    workbasketService.createWorkbasketAccessItem(wbai);
  }

  private Task createTask(Workbasket workbasket) throws Exception {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setCompany("Company1");
    objectReference.setSystem("System1");
    objectReference.setSystemInstance("Instance1");
    objectReference.setType("Type1");
    objectReference.setValue("Value1");

    Task task = taskService.newTask(workbasket.getId());
    task.setClassificationKey("L10000");
    task.setPrimaryObjRef(objectReference);
    task.setOwner("user-1-2");

    Task createdTask = taskService.createTask(task);
    return createdTask;
  }
}
