package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.TaskImpl;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCreationAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateCreatedHistoryEvent_When_TaskIsCreated() throws Exception {

    TaskImpl newTask = (TaskImpl) taskService.newTask("WBI:100000000000000000000000000000000006");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectRef("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    taskService.createTask(newTask);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(newTask.getId()));

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.TASK_CREATED.getName());
  }
}
