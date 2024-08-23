package acceptance.persistence;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.JobService;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.internal.JobServiceImpl;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for access to timestamps from different timezones. */
@ExtendWith(JaasExtension.class)
class UpdateObjectsUseUtcTimeStampsAccTest extends AbstractAccTest {

  @WithAccessId(user = "admin")
  @Test
  void testTimestampsOnTaskUpdate() throws Exception {
    TaskService taskService = kadaiEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    Instant now = Instant.now();

    task.setPlanned(now.plus(Duration.ofHours(17)));

    // associated Classification has ServiceLevel 'P1D'
    task.setDue(
        workingTimeCalculator.addWorkingTime(task.getPlanned(), Duration.ofDays(1)).minusMillis(1));

    TaskImpl ti = (TaskImpl) task;
    ti.setCompleted(now.plus(Duration.ofHours(27)));
    TimeZone originalZone = TimeZone.getDefault();
    Task updatedTask = taskService.updateTask(task);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Task retrievedTask = taskService.getTask(updatedTask.getId());
    TimeZone.setDefault(originalZone);
    assertThat(retrievedTask).isEqualTo(updatedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testCreatedTaskObjectEqualsReadTaskObjectInNewTimezone() throws Exception {

    TaskService taskService = kadaiEngine.getTaskService();
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    for (TaskCustomField taskCustomField : TaskCustomField.values()) {
      newTask.setCustomField(taskCustomField, taskCustomField.name());
    }
    newTask.setCustomAttributeMap(createSimpleCustomPropertyMap(5));
    newTask.setDescription("Description of test task");
    newTask.setNote("My note");
    newTask.addAttachment(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));

    TimeZone originalZone = TimeZone.getDefault();
    Task createdTask = taskService.createTask(newTask);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Task readTask = taskService.getTask(createdTask.getId());
    TimeZone.setDefault(originalZone);
    assertThat(readTask).isEqualTo(createdTask);
  }

  @WithAccessId(user = "admin")
  @Test
  void testTimestampsOnClassificationUpdate() throws Exception {
    ClassificationService classificationService = kadaiEngine.getClassificationService();
    Classification classification =
        classificationService.getClassification("CLI:000000000000000000000000000000000001");
    classification.setPriority(17);

    TimeZone originalZone = TimeZone.getDefault();
    Classification updatedClassification =
        classificationService.updateClassification(classification);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Classification retrievedClassification =
        classificationService.getClassification(updatedClassification.getId());
    TimeZone.setDefault(originalZone);
    assertThat(retrievedClassification).isEqualTo(updatedClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testTimestampsOnCreateMasterClassification() throws Exception {
    ClassificationService classificationService = kadaiEngine.getClassificationService();
    final long amountOfClassificationsBefore =
        classificationService.createClassificationQuery().count();
    Classification classification =
        classificationService.newClassification("Key0", MASTER_DOMAIN, "TASK");
    classification.setIsValidInDomain(true);
    classification.setServiceLevel("P1D");
    classification = classificationService.createClassification(classification);

    // check only 1 created
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter).isEqualTo(amountOfClassificationsBefore + 1);

    TimeZone originalZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Classification retrievedClassification =
        classificationService.getClassification(classification.getId());
    TimeZone.setDefault(originalZone);

    assertThat(retrievedClassification).isEqualTo(classification);
  }

  @WithAccessId(user = "admin")
  @Test
  void testTimestampsOnWorkbasketUpdate() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    Workbasket workbasket =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "bla");

    TimeZone originalZone = TimeZone.getDefault();
    Workbasket updatedWorkbasket = workbasketService.updateWorkbasket(workbasket);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Workbasket retrievedWorkbasket = workbasketService.getWorkbasket(updatedWorkbasket.getId());
    TimeZone.setDefault(originalZone);
    assertThat(retrievedWorkbasket).isEqualTo(updatedWorkbasket);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testTimestampsOnCreateWorkbasket() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    final int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
    assertThat(after).isEqualTo(before + 1);

    TimeZone originalZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Workbasket retrievedWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
    TimeZone.setDefault(originalZone);
    assertThat(retrievedWorkbasket).isEqualTo(workbasket);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testTimestampsOnCreateScheduledJob() throws Exception {
    resetDb(true);
    ScheduledJob job = new ScheduledJob();
    job.setArguments(Map.of("keyBla", "valueBla"));
    job.setType(TaskCleanupJob.class.getName());
    job.setDue(Instant.now().minus(Duration.ofHours(5)));
    job.setLockExpires(Instant.now().minus(Duration.ofHours(5)));
    JobService jobService = kadaiEngine.getJobService();
    job = jobService.createJob(job);
    TimeZone originalZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));

    JobServiceImpl jobServiceImpl = (JobServiceImpl) jobService;
    List<ScheduledJob> jobs = jobServiceImpl.findJobsToRun();
    final ScheduledJob jobForLambda = job;
    ScheduledJob retrievedJob =
        jobs.stream()
            .filter(
                j ->
                    j.getJobId().equals(jobForLambda.getJobId())
                        && j.getArguments() != null
                        && "valueBla".equals(j.getArguments().get("keyBla")))
            .findFirst()
            .orElse(null);

    TimeZone.setDefault(originalZone);
    assertThat(retrievedJob).isEqualTo(job);
  }
}
