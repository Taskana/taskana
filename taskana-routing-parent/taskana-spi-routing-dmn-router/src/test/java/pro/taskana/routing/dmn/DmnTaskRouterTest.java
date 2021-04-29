package pro.taskana.routing.dmn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class DmnTaskRouterTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();

  @WithAccessId(user = "taskadmin")
  @Test
  void should_RouteTaskToCorrectWorkbasket_When_DmnTaskRouterFindsRule() throws Exception {

    Task taskToRoute = taskService.newTask();
    taskToRoute.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("company", null, null, "MyType1", "00000001");
    taskToRoute.setPrimaryObjRef(objectReference);

    Task routedTask = taskService.createTask(taskToRoute);
    assertThat(routedTask.getWorkbasketKey()).isEqualTo("GPK_KSC");
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_DmnTaskRouterFindsNoRule() throws Exception {

    Task taskToRoute = taskService.newTask();
    taskToRoute.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("company", null, null, "MyTeö", "000002");
    taskToRoute.setPrimaryObjRef(objectReference);

    ThrowingCallable call = () -> taskService.createTask(taskToRoute);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .extracting(ex -> ex.getMessage())
        .isEqualTo("Cannot create a task outside a workbasket");
  }
}
