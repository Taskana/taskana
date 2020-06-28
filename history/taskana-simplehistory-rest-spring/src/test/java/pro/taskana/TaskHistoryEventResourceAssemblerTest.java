package pro.taskana;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.rest.TaskHistoryRestConfiguration;
import pro.taskana.simplehistory.rest.resource.TaskHistoryEventResource;
import pro.taskana.simplehistory.rest.resource.TaskHistoryEventResourceAssembler;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;

/** Test for {@link TaskHistoryEventResourceAssembler}. */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {TaskHistoryRestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskHistoryEventResourceAssemblerTest {

  private final TaskHistoryEventResourceAssembler taskHistoryEventResourceAssembler;

  @Autowired
  public TaskHistoryEventResourceAssemblerTest(
      TaskHistoryEventResourceAssembler taskHistoryEventResourceAssembler) {
    this.taskHistoryEventResourceAssembler = taskHistoryEventResourceAssembler;
  }

  @Test
  void taskHistoryEventModelToResource() {

    HistoryEventImpl historyEvent = new HistoryEventImpl("user1", "someDetails");

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
    historyEvent.setCustom1("custom1");
    historyEvent.setCustom2("custom2");
    historyEvent.setCustom3("custom3");
    historyEvent.setCustom4("custom4");

    TaskHistoryEventResource taskHistoryEventResource =
        taskHistoryEventResourceAssembler.toModel(historyEvent);

    testEquality(historyEvent, taskHistoryEventResource);
  }

  private void testEquality(
      TaskanaHistoryEvent historyEvent, TaskHistoryEventResource taskHistoryEventResource) {

    assertThat(historyEvent.getEventType()).isEqualTo(taskHistoryEventResource.getEventType());
    assertThat(historyEvent.getBusinessProcessId())
        .isEqualTo(taskHistoryEventResource.getBusinessProcessId());
    assertThat(historyEvent.getParentBusinessProcessId())
        .isEqualTo(taskHistoryEventResource.getParentBusinessProcessId());
    assertThat(historyEvent.getTaskId()).isEqualTo(taskHistoryEventResource.getTaskId());
    assertThat(historyEvent.getTaskClassificationCategory())
        .isEqualTo(taskHistoryEventResource.getTaskClassificationCategory());
    assertThat(historyEvent.getDomain()).isEqualTo(taskHistoryEventResource.getDomain());
    assertThat(historyEvent.getWorkbasketKey())
        .isEqualTo(taskHistoryEventResource.getWorkbasketKey());
    assertThat(historyEvent.getAttachmentClassificationKey())
        .isEqualTo(taskHistoryEventResource.getAttachmentClassificationKey());
    assertThat(historyEvent.getCreated())
        .isEqualTo(Instant.parse(taskHistoryEventResource.getCreated()));
    assertThat(historyEvent.getOldValue()).isEqualTo(taskHistoryEventResource.getOldValue());
    assertThat(historyEvent.getNewValue()).isEqualTo(taskHistoryEventResource.getNewValue());
    assertThat(historyEvent.getPorCompany()).isEqualTo(taskHistoryEventResource.getPorCompany());
    assertThat(historyEvent.getPorSystem()).isEqualTo(taskHistoryEventResource.getPorSystem());
    assertThat(historyEvent.getPorType()).isEqualTo(taskHistoryEventResource.getPorType());
    assertThat(historyEvent.getPorValue()).isEqualTo(taskHistoryEventResource.getPorValue());
    assertThat(historyEvent.getCustom1()).isEqualTo(taskHistoryEventResource.getCustom1());
    assertThat(historyEvent.getCustom2()).isEqualTo(taskHistoryEventResource.getCustom2());
    assertThat(historyEvent.getCustom3()).isEqualTo(taskHistoryEventResource.getCustom3());
    assertThat(historyEvent.getCustom4()).isEqualTo(taskHistoryEventResource.getCustom4());
  }
}
