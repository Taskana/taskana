package pro.taskana.task.internal.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskState;

public class MinimalTaskSummaryTest {
  @Test
  void testMinimalTaskSummary() {
    Instant now = Instant.now();
    MinimalTaskSummary summary = new MinimalTaskSummary();
    summary.setTaskId("task1");
    summary.setExternalId("exid");
    summary.setWorkbasketId("wb1");
    summary.setClassificationId("cfid");
    summary.setOwner("owner");
    summary.setTaskState(TaskState.CLAIMED);
    summary.setPlanned(now);
    summary.setDue(now);
    summary.setModified(now);
    summary.setCallbackState(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    assertThat(summary.getTaskId()).isEqualTo("task1");
    assertThat(summary.getExternalId()).isEqualTo("exid");
    assertThat(summary.getWorkbasketId()).isEqualTo("wb1");
    assertThat(summary.getClassificationId()).isEqualTo("cfid");
    assertThat(summary.getOwner()).isEqualTo("owner");
    assertThat(summary.getTaskState()).isEqualTo(TaskState.CLAIMED);
    assertThat(summary.getPlanned()).isEqualTo(now);
    assertThat(summary.getDue()).isEqualTo(now);
    assertThat(summary.getModified()).isEqualTo(now);
    assertThat(summary.getCallbackState()).isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);
    String summaryAsString = summary.toString();
    assertThat(summaryAsString).isNotNull();
  }
}
