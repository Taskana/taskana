package acceptance.priorityservice;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;

/** Acceptance test for all priority computation scenarios. */
@Disabled("Until we enable the use of Test-SPI's in only specific tests")
@ExtendWith(JaasExtension.class)
class PriorityServiceAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest> should_SetThePriorityAccordingToTestProvider_When_CreatingTask() {
    List<Pair<String, Integer>> testCases = List.of(Pair.of("false", 1), Pair.of("true", 10));

    ThrowingConsumer<Pair<String, Integer>> test =
        x -> {
          Task task = taskService.newTask("USER-1-1", "DOMAIN_A");
          task.setCustomAttribute(TaskCustomField.CUSTOM_6, x.getLeft());
          task.setClassificationKey("T2100");
          ObjectReference objectReference =
              createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
          task.setPrimaryObjRef(objectReference);

          Task createdTask = taskService.createTask(task);
          assertThat(createdTask.getPriority()).isEqualTo(x.getRight());
        };

    return DynamicTest.stream(testCases.iterator(), x -> "entry in custom6: " + x.getLeft(), test);
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest> should_SetThePriorityAccordingToTestProvider_When_UpdatingTask()
      throws Exception {
    List<Pair<String, Integer>> testCases = List.of(Pair.of("false", 1), Pair.of("true", 10));
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    int daysSinceCreated =
        Math.toIntExact(
            TimeUnit.DAYS.convert(
                Date.from(Instant.now()).getTime() - Date.from(task.getCreated()).getTime(),
                TimeUnit.MILLISECONDS));

    ThrowingConsumer<Pair<String, Integer>> test =
        x -> {
          task.setCustomAttribute(TaskCustomField.CUSTOM_6, x.getLeft());

          Task updatedTask = taskService.updateTask(task);
          assertThat(updatedTask.getPriority()).isEqualTo(daysSinceCreated * x.getRight());
        };

    return DynamicTest.stream(testCases.iterator(), x -> "entry in custom6: " + x.getLeft(), test);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotCreateClassificationChangedJob_When_PriorityProviderExisting() throws Exception {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    Classification classification =
        classificationService.getClassification("CLI:000000000000000000000000000000000001");
    classification.setPriority(10);

    classificationService.updateClassification(classification);
    List<ScheduledJob> jobsToRun = getJobMapper().findJobsToRun(Instant.now());
    assertThat(jobsToRun).isEmpty();

    classification.setServiceLevel("P4D");
    classificationService.updateClassification(classification);
    jobsToRun = getJobMapper().findJobsToRun(Instant.now());
    assertThat(jobsToRun).isEmpty();
  }
}
