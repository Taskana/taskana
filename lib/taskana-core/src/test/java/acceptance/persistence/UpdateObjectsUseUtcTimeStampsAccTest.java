package acceptance.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.JobService;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.JobServiceImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.jobs.ScheduledJob;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for access to timestamps from different timezones. */
@ExtendWith(JaasExtension.class)
public class UpdateObjectsUseUtcTimeStampsAccTest extends AbstractAccTest {

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTimestampsOnTaskUpdate()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    TaskImpl ti = (TaskImpl) task;
    task.setPlanned(Instant.now().plus(Duration.ofHours(17)));
    task.setDue(Instant.now().plus(Duration.ofHours(27)));
    ti.setCompleted(Instant.now().plus(Duration.ofHours(27)));

    TimeZone originalZone = TimeZone.getDefault();
    Task updatedTask = taskService.updateTask(task);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Task retrievedTask = taskService.getTask(updatedTask.getId());
    TimeZone.setDefault(originalZone);
    assertEquals(updatedTask, retrievedTask);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCreatedTaskObjectEqualsReadTaskObjectInNewTimezone()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    for (int i = 1; i < 16; i++) {
      newTask.setCustomAttribute(Integer.toString(i), "VALUE " + i);
    }
    newTask.setCustomAttributes(createSimpleCustomProperties(5));
    newTask.setDescription("Description of test task");
    newTask.setNote("My note");
    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));

    TimeZone originalZone = TimeZone.getDefault();
    Task createdTask = taskService.createTask(newTask);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Task readTask = taskService.getTask(createdTask.getId());
    TimeZone.setDefault(originalZone);
    assertEquals(createdTask, readTask);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTimestampsOnClassificationUpdate()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
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
    assertEquals(updatedClassification, retrievedClassification);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  void testTimestampsOnCreateMasterClassification()
      throws ClassificationAlreadyExistException, ClassificationNotFoundException,
          NotAuthorizedException, DomainNotFoundException, InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
    Classification classification = classificationService.newClassification("Key0", "", "TASK");
    classification.setIsValidInDomain(true);
    classification = classificationService.createClassification(classification);

    // check only 1 created
    long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
    assertThat(amountOfClassificationsAfter, equalTo(amountOfClassificationsBefore + 1));

    TimeZone originalZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Classification retrievedClassification =
        classificationService.getClassification(classification.getId());
    TimeZone.setDefault(originalZone);

    assertEquals(classification, retrievedClassification);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTimestampsOnWorkbasketUpdate()
      throws TaskNotFoundException, InvalidArgumentException, ConcurrencyException,
          NotAuthorizedException, AttachmentPersistenceException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001");
    workbasket.setCustom1("bla");

    TimeZone originalZone = TimeZone.getDefault();
    Workbasket updatedWorkbasket = workbasketService.updateWorkbasket(workbasket);
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Workbasket retrievedWorkbasket = workbasketService.getWorkbasket(updatedWorkbasket.getId());
    TimeZone.setDefault(originalZone);
    assertEquals(updatedWorkbasket, retrievedWorkbasket);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testTimestampsOnCreateWorkbasket()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    final int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_1_2");
    wbai.setPermRead(true);
    workbasketService.createWorkbasketAccessItem(wbai);

    int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
    assertEquals(before + 1, after);

    TimeZone originalZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    Workbasket retrievedWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
    TimeZone.setDefault(originalZone);
    assertEquals(workbasket, retrievedWorkbasket);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"businessadmin"})
  @Test
  void testTimestampsOnCreateScheduledJob()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException,
          SQLException {
    resetDb(true);
    ScheduledJob job = new ScheduledJob();
    job.setArguments(Collections.singletonMap("keyBla", "valueBla"));
    job.setType(ScheduledJob.Type.TASKCLEANUPJOB);
    job.setDue(Instant.now().minus(Duration.ofHours(5)));
    job.setLockExpires(Instant.now().minus(Duration.ofHours(5)));
    JobService jobService = taskanaEngine.getJobService();
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
    assertEquals(job, retrievedJob);
  }
}
