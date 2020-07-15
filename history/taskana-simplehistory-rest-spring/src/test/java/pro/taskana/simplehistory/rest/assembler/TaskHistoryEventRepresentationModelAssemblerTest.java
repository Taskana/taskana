package pro.taskana.simplehistory.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.simplehistory.rest.TaskHistoryRestConfiguration;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventRepresentationModel;
import pro.taskana.spi.history.api.events.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** Test for {@link TaskHistoryEventRepresentationModelAssembler}. */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {TaskHistoryRestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskHistoryEventRepresentationModelAssemblerTest {

  private final TaskHistoryEventRepresentationModelAssembler
      taskHistoryEventRepresentationModelAssembler;

  @Autowired
  TaskHistoryEventRepresentationModelAssemblerTest(
      TaskHistoryEventRepresentationModelAssembler taskHistoryEventRepresentationModelAssembler) {
    this.taskHistoryEventRepresentationModelAssembler =
        taskHistoryEventRepresentationModelAssembler;
  }

  @Test
  void taskHistoryEventModelToResource() {

    TaskHistoryEvent historyEvent = new TaskHistoryEvent();

    historyEvent.setEventType("TASK_CREATED");
    historyEvent.setBusinessProcessId("BPI:01");
    historyEvent.setParentBusinessProcessId("BPI:02");
    historyEvent.setTaskId("TKI:000000000000000000000000000000000000");
    historyEvent.setTaskClassificationCategory("MANUAL");
    historyEvent.setDomain("DOMAIN_A");
    historyEvent.setWorkbasketKey("WorkbasketKey");
    historyEvent.setAttachmentClassificationKey("L1050");
    historyEvent.setCreated(Instant.now());
    historyEvent.setOldValue("oldValue");
    historyEvent.setNewValue("newValue");
    historyEvent.setPorCompany("porCompany");
    historyEvent.setPorSystem("porSystem");
    historyEvent.setPorType("porType");
    historyEvent.setPorValue("porValue");
    historyEvent.setCustomAttribute(TaskHistoryCustomField.CUSTOM_1, "custom1");
    historyEvent.setCustomAttribute(TaskHistoryCustomField.CUSTOM_2, "custom2");
    historyEvent.setCustomAttribute(TaskHistoryCustomField.CUSTOM_3, "custom3");
    historyEvent.setCustomAttribute(TaskHistoryCustomField.CUSTOM_4, "custom4");

    TaskHistoryEventRepresentationModel taskHistoryEventRepresentationModel =
        taskHistoryEventRepresentationModelAssembler.toModel(historyEvent);

    testEquality(historyEvent, taskHistoryEventRepresentationModel);
  }

  private void testEquality(
      TaskHistoryEvent historyEvent,
      TaskHistoryEventRepresentationModel taskHistoryEventRepresentationModel) {

    assertThat(historyEvent.getEventType())
        .isEqualTo(taskHistoryEventRepresentationModel.getEventType());
    assertThat(historyEvent.getBusinessProcessId())
        .isEqualTo(taskHistoryEventRepresentationModel.getBusinessProcessId());
    assertThat(historyEvent.getParentBusinessProcessId())
        .isEqualTo(taskHistoryEventRepresentationModel.getParentBusinessProcessId());
    assertThat(historyEvent.getTaskId()).isEqualTo(taskHistoryEventRepresentationModel.getTaskId());
    assertThat(historyEvent.getTaskClassificationCategory())
        .isEqualTo(taskHistoryEventRepresentationModel.getTaskClassificationCategory());
    assertThat(historyEvent.getDomain()).isEqualTo(taskHistoryEventRepresentationModel.getDomain());
    assertThat(historyEvent.getWorkbasketKey())
        .isEqualTo(taskHistoryEventRepresentationModel.getWorkbasketKey());
    assertThat(historyEvent.getAttachmentClassificationKey())
        .isEqualTo(taskHistoryEventRepresentationModel.getAttachmentClassificationKey());
    assertThat(historyEvent.getCreated())
        .isEqualTo(taskHistoryEventRepresentationModel.getCreated());
    assertThat(historyEvent.getOldValue())
        .isEqualTo(taskHistoryEventRepresentationModel.getOldValue());
    assertThat(historyEvent.getNewValue())
        .isEqualTo(taskHistoryEventRepresentationModel.getNewValue());
    assertThat(historyEvent.getPorCompany())
        .isEqualTo(taskHistoryEventRepresentationModel.getPorCompany());
    assertThat(historyEvent.getPorSystem())
        .isEqualTo(taskHistoryEventRepresentationModel.getPorSystem());
    assertThat(historyEvent.getPorType())
        .isEqualTo(taskHistoryEventRepresentationModel.getPorType());
    assertThat(historyEvent.getPorValue())
        .isEqualTo(taskHistoryEventRepresentationModel.getPorValue());
    assertThat(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_1))
        .isEqualTo(taskHistoryEventRepresentationModel.getCustom1());
    assertThat(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_2))
        .isEqualTo(taskHistoryEventRepresentationModel.getCustom2());
    assertThat(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_3))
        .isEqualTo(taskHistoryEventRepresentationModel.getCustom3());
    assertThat(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_4))
        .isEqualTo(taskHistoryEventRepresentationModel.getCustom4());
  }
}
