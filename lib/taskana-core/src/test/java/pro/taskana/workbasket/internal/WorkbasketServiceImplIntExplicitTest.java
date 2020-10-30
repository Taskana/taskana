package pro.taskana.workbasket.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/** Integration Test for workbasketServiceImpl with connection mode EXPLICIT. */
@ExtendWith(JaasExtension.class)
class WorkbasketServiceImplIntExplicitTest {

  private static final int SLEEP_TIME = 100;

  private DataSource dataSource;
  private TaskanaEngine taskanaEngine;
  private TaskanaEngineImpl taskanaEngineImpl;
  private WorkbasketService workBasketService;

  @BeforeAll
  static void resetDb() {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    new SampleDataGenerator(ds, schemaName).dropDb();
  }

  @BeforeEach
  void setup() throws Exception {
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    TaskanaEngineConfiguration taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void testUpdateWorkbasket() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      workBasketService = taskanaEngine.getWorkbasketService();
      String id0 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket0 =
          createTestWorkbasket(id0, "key0", "DOMAIN_A", "Superbasket", WorkbasketType.GROUP);
      workbasket0 = workBasketService.createWorkbasket(workbasket0);
      createWorkbasketWithSecurity(workbasket0, "user-1-1", true, true, false, false);

      String id1 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket1 =
          createTestWorkbasket(id1, "key1", "DOMAIN_A", "Megabasket", WorkbasketType.GROUP);
      workbasket1 = workBasketService.createWorkbasket(workbasket1);
      createWorkbasketWithSecurity(workbasket1, "user-1-1", true, true, false, false);

      String id2 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket2 =
          createTestWorkbasket(id2, "key2", "DOMAIN_A", "Hyperbasket", WorkbasketType.GROUP);
      workbasket2 = workBasketService.createWorkbasket(workbasket2);
      createWorkbasketWithSecurity(workbasket2, "user-1-1", true, true, false, false);

      List<String> distTargets = Arrays.asList(workbasket0.getId(), workbasket1.getId());
      Thread.sleep(SLEEP_TIME);
      workBasketService.setDistributionTargets(workbasket2.getId(), distTargets);

      String id3 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket3 =
          createTestWorkbasket(
              id3, "key3", "DOMAIN_A", "hm ... irgend ein basket", WorkbasketType.GROUP);
      workbasket3 = workBasketService.createWorkbasket(workbasket3);
      createWorkbasketWithSecurity(workbasket3, "user-1-1", true, true, false, false);

      List<String> newDistTargets = List.of(workbasket3.getId());
      Thread.sleep(SLEEP_TIME);
      workBasketService.setDistributionTargets(workbasket2.getId(), newDistTargets);

      Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

      List<WorkbasketSummary> distributionTargets =
          workBasketService.getDistributionTargets(foundBasket.getId());
      assertThat(distributionTargets).hasSize(1);
      assertThat(distributionTargets.get(0).getId()).isEqualTo(workbasket3.getId());
      assertThat(workBasketService.getWorkbasket(id2).getCreated())
          .isNotEqualTo(workBasketService.getWorkbasket(id2).getModified());
      assertThat(workBasketService.getWorkbasket(id1).getCreated())
          .isEqualTo(workBasketService.getWorkbasket(id1).getModified());
      assertThat(workBasketService.getWorkbasket(id3).getCreated())
          .isEqualTo(workBasketService.getWorkbasket(id3).getModified());
      connection.commit();
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void testInsertWorkbasketAccessUser() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      workBasketService = taskanaEngine.getWorkbasketService();
      Workbasket wb =
          createTestWorkbasket("id1", "key1", "DOMAIN_A", "name", WorkbasketType.CLEARANCE);
      workBasketService.createWorkbasket(wb);
      WorkbasketAccessItem accessItem =
          workBasketService.newWorkbasketAccessItem("id1", "Arthur Dent");
      accessItem.setPermission(WorkbasketPermission.OPEN, true);
      accessItem.setPermission(WorkbasketPermission.READ, true);
      workBasketService.createWorkbasketAccessItem(accessItem);

      assertThat(workBasketService.getWorkbasketAccessItems("id1")).hasSize(1);
      connection.commit();
    }
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void testUpdateWorkbasketAccessUser() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      workBasketService = taskanaEngine.getWorkbasketService();

      Workbasket wb =
          createTestWorkbasket("key2", "kkey2", "DOMAIN_A", "name", WorkbasketType.CLEARANCE);
      workBasketService.createWorkbasket(wb);
      WorkbasketAccessItem accessItem =
          workBasketService.newWorkbasketAccessItem("key2", "Zaphod Beeblebrox");
      accessItem.setPermission(WorkbasketPermission.OPEN, true);
      accessItem.setPermission(WorkbasketPermission.READ, true);
      workBasketService.createWorkbasketAccessItem(accessItem);

      assertThat(workBasketService.getWorkbasketAccessItems("key2")).hasSize(1);
      assertThat(accessItem.getAccessId()).isEqualTo("zaphod beeblebrox");
      connection.commit();
    }
  }

  @AfterEach
  void cleanUp() throws Exception {
    taskanaEngineImpl.setConnection(null);
  }

  private void createWorkbasketWithSecurity(
      Workbasket wb,
      String accessId,
      boolean permOpen,
      boolean permRead,
      boolean permAppend,
      boolean permTransfer)
      throws Exception {
    WorkbasketAccessItem accessItem =
        workBasketService.newWorkbasketAccessItem(wb.getId(), accessId);
    accessItem.setPermission(WorkbasketPermission.OPEN, permOpen);
    accessItem.setPermission(WorkbasketPermission.READ, permRead);
    accessItem.setPermission(WorkbasketPermission.APPEND, permAppend);
    accessItem.setPermission(WorkbasketPermission.TRANSFER, permTransfer);
    workBasketService.createWorkbasketAccessItem(accessItem);
  }

  private Workbasket createTestWorkbasket(
      String id, String key, String domain, String name, WorkbasketType type) {
    WorkbasketImpl wb = (WorkbasketImpl) workBasketService.newWorkbasket(key, domain);
    wb.setId(id);
    wb.setName(name);
    wb.setDescription("Description of a Workbasket...");
    wb.setType(type);
    return wb;
  }
}
