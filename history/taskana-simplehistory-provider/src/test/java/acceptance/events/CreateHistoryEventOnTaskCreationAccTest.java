package acceptance.events;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.impl.HistoryQueryImpl;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.TaskImpl;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCreationAccTest extends AbstractAccTest {

  private TaskService taskService;
  private SimpleHistoryServiceImpl historyService;

  @BeforeEach
  public void setUp() {

    taskService = taskanaEngine.getTaskService();
    historyService = getHistoryService();
  }

  protected ObjectReference createObjectRef(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReference objectRef = new ObjectReference();
    objectRef.setCompany(company);
    objectRef.setSystem(system);
    objectRef.setSystemInstance(systemInstance);
    objectRef.setType(type);
    objectRef.setValue(value);
    return objectRef;
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CreateCreatedHistoryEvent_When_TaskIsCreated() throws Exception {

    TaskImpl newTask = (TaskImpl) taskService.newTask("WBI:100000000000000000000000000000000006");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectRef("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    taskService.createTask(newTask);

    HistoryQueryMapper historyQueryMapper = getHistoryQueryMapper();

    List<HistoryEventImpl> listEvents =
        historyQueryMapper.queryHistoryEvent(
            (HistoryQueryImpl) historyService.createHistoryQuery().taskIdIn(newTask.getId()));

    assertThat(listEvents).hasSize(1);
    assertThat(listEvents.get(0).getEventType()).isEqualTo("TASK_CREATED");
  }
}
