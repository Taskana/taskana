package pro.taskana.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.TaskanaEngineTestConfiguration;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for workbasketServiceImpl with connection mode EXPLICIT.
 *
 * @author bbr
 */
@ExtendWith(JaasExtension.class)
class WorkbasketServiceImplIntExplicitTest {

  private static final int SLEEP_TIME = 100;

  @SuppressWarnings("checkstyle:DeclarationOrder")
  static int counter = 0;

  private DataSource dataSource;
  private TaskanaEngineConfiguration taskanaEngineConfiguration;
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
  void setup() throws SQLException {
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testUpdateWorkbasket() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      workBasketService = taskanaEngine.getWorkbasketService();
      String id0 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket0 =
          createTestWorkbasket(id0, "key0", "DOMAIN_A", "Superbasket", WorkbasketType.GROUP);
      workbasket0 = workBasketService.createWorkbasket(workbasket0);
      createWorkbasketWithSecurity(workbasket0, "Elena", true, true, false, false);

      String id1 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket1 =
          createTestWorkbasket(id1, "key1", "DOMAIN_A", "Megabasket", WorkbasketType.GROUP);
      workbasket1 = workBasketService.createWorkbasket(workbasket1);
      createWorkbasketWithSecurity(workbasket1, "Elena", true, true, false, false);

      String id2 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket2 =
          createTestWorkbasket(id2, "key2", "DOMAIN_A", "Hyperbasket", WorkbasketType.GROUP);
      workbasket2 = workBasketService.createWorkbasket(workbasket2);
      createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);

      List<String> distTargets =
          new ArrayList<>(Arrays.asList(workbasket0.getId(), workbasket1.getId()));
      Thread.sleep(SLEEP_TIME);
      workBasketService.setDistributionTargets(workbasket2.getId(), distTargets);

      String id3 = IdGenerator.generateWithPrefix("TWB");
      Workbasket workbasket3 =
          createTestWorkbasket(
              id3, "key3", "DOMAIN_A", "hm ... irgend ein basket", WorkbasketType.GROUP);
      workbasket3 = workBasketService.createWorkbasket(workbasket3);
      createWorkbasketWithSecurity(workbasket3, "Elena", true, true, false, false);

      List<String> newDistTargets = new ArrayList<>(Arrays.asList(workbasket3.getId()));
      Thread.sleep(SLEEP_TIME);
      workBasketService.setDistributionTargets(workbasket2.getId(), newDistTargets);

      Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

      List<WorkbasketSummary> distributionTargets =
          workBasketService.getDistributionTargets(foundBasket.getId());
      assertEquals(1, distributionTargets.size());
      assertEquals(workbasket3.getId(), distributionTargets.get(0).getId());
      assertNotEquals(
          workBasketService.getWorkbasket(id2).getCreated(),
          workBasketService.getWorkbasket(id2).getModified());
      assertEquals(
          workBasketService.getWorkbasket(id1).getCreated(),
          workBasketService.getWorkbasket(id1).getModified());
      assertEquals(
          workBasketService.getWorkbasket(id3).getCreated(),
          workBasketService.getWorkbasket(id3).getModified());
      connection.commit();
    }
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testInsertWorkbasketAccessUser()
      throws NotAuthorizedException, SQLException, InvalidArgumentException,
          WorkbasketNotFoundException, DomainNotFoundException, InvalidWorkbasketException,
          WorkbasketAlreadyExistException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      workBasketService = taskanaEngine.getWorkbasketService();
      Workbasket wb =
          createTestWorkbasket("id1", "key1", "DOMAIN_A", "name", WorkbasketType.CLEARANCE);
      workBasketService.createWorkbasket(wb);
      WorkbasketAccessItem accessItem =
          workBasketService.newWorkbasketAccessItem("id1", "Arthur Dent");
      accessItem.setPermOpen(true);
      accessItem.setPermRead(true);
      workBasketService.createWorkbasketAccessItem(accessItem);

      assertEquals(1, workBasketService.getWorkbasketAccessItems("id1").size());
      connection.commit();
    }
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testUpdateWorkbasketAccessUser()
      throws NotAuthorizedException, SQLException, InvalidArgumentException,
          WorkbasketNotFoundException, DomainNotFoundException, InvalidWorkbasketException,
          WorkbasketAlreadyExistException {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngineImpl.setConnection(connection);
      workBasketService = taskanaEngine.getWorkbasketService();

      Workbasket wb =
          createTestWorkbasket("key2", "kkey2", "DOMAIN_A", "name", WorkbasketType.CLEARANCE);
      workBasketService.createWorkbasket(wb);
      WorkbasketAccessItem accessItem =
          workBasketService.newWorkbasketAccessItem("key2", "Zaphod Beeblebrox");
      accessItem.setPermOpen(true);
      accessItem.setPermRead(true);
      workBasketService.createWorkbasketAccessItem(accessItem);

      assertEquals(1, workBasketService.getWorkbasketAccessItems("key2").size());
      assertEquals("zaphod beeblebrox", accessItem.getAccessId());
      connection.commit();
    }
  }

  @AfterEach
  void cleanUp() throws SQLException {
    taskanaEngineImpl.setConnection(null);
  }

  private void createWorkbasketWithSecurity(
      Workbasket wb,
      String accessId,
      boolean permOpen,
      boolean permRead,
      boolean permAppend,
      boolean permTransfer)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketAccessItem accessItem =
        workBasketService.newWorkbasketAccessItem(wb.getId(), accessId);
    accessItem.setPermOpen(permOpen);
    accessItem.setPermRead(permRead);
    accessItem.setPermAppend(permAppend);
    accessItem.setPermTransfer(permTransfer);
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
