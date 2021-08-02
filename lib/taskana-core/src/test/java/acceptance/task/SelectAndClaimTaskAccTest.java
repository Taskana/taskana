package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.security.auth.Subject;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.security.UserPrincipal;
import pro.taskana.common.internal.util.CheckedConsumer;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class SelectAndClaimTaskAccTest extends AbstractAccTest {

  @Test
  void should_ClaimDifferentTasks_For_ConcurrentSelectAndClaimCalls() throws Exception {

    List<Task> selectedAndClaimedTasks = Collections.synchronizedList(new ArrayList<>());

    List<String> accessIds =
        Collections.synchronizedList(
            Stream.of("admin", "teamlead-1", "teamlead-2", "taskadmin")
                .collect(Collectors.toList()));

    runInThread(getRunnableTest(selectedAndClaimedTasks, accessIds), accessIds.size());

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

  @Test
  @WithAccessId(user = "admin")
  void should_ThrowException_When_TryingToSelectAndClaimNonExistingTask() {

    TaskQuery query = taskanaEngine.getTaskService().createTaskQuery().idIn("notexisting");
    ThrowingCallable call = () -> taskanaEngine.getTaskService().selectAndClaim(query);
    assertThatThrownBy(call)
        .isInstanceOf(SystemException.class)
        .hasMessageContaining(
            "No tasks matched the specified filter and sorting options, "
                + "task query returned nothing!");
  }

  private void runInThread(Runnable runnable, int threadCount) throws InterruptedException {
    Thread[] threads = new Thread[threadCount];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(runnable);
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  private Runnable getRunnableTest(List<Task> selectedAndClaimedTasks, List<String> accessIds) {
    return () -> {
      Subject subject = new Subject();
      subject.getPrincipals().add(new UserPrincipal(accessIds.remove(0)));

      Consumer<TaskService> consumer =
          CheckedConsumer.wrap(
              taskService -> {
                Task task = taskService.selectAndClaim(getTaskQuery());
                selectedAndClaimedTasks.add(task);
              });
      PrivilegedAction<Void> action =
          () -> {
            consumer.accept(taskanaEngine.getTaskService());
            return null;
          };
      Subject.doAs(subject, action);
    };
  }

  private TaskQuery getTaskQuery() {
    return taskanaEngine.getTaskService().createTaskQuery().orderByTaskId(SortDirection.ASCENDING);
  }
}
