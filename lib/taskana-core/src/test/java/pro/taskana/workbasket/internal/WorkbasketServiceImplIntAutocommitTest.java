package pro.taskana.workbasket.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketAccessItem;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketSummary;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/**
 * Integration Test for workbasketServiceImpl with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
@ExtendWith(JaasExtension.class)
class WorkbasketServiceImplIntAutocommitTest {

  private static final int SLEEP_TIME = 100;
  private TaskanaEngine taskanaEngine;
  private WorkbasketService workBasketService;
  private Instant now;

  @BeforeAll
  static void resetDb() {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    new SampleDataGenerator(ds, schemaName).dropDb();
  }

  @BeforeEach
  void setup() throws SQLException {
    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    TaskanaEngineConfiguration taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    workBasketService = taskanaEngine.getWorkbasketService();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
    now = Instant.now();
  }

  @Test
  void testGetWorkbasketFail() {
    Assertions.assertThrows(
        WorkbasketNotFoundException.class, () -> workBasketService.getWorkbasket("fail"));
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testUpdateWorkbasket() throws Exception {
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
    assertEquals(id3, distributionTargets.get(0).getId());
    assertNotEquals(
        workBasketService.getWorkbasket(id2).getCreated(),
        workBasketService.getWorkbasket(id2).getModified());
    assertEquals(
        workBasketService.getWorkbasket(id1).getCreated(),
        workBasketService.getWorkbasket(id1).getModified());
    assertEquals(
        workBasketService.getWorkbasket(id3).getCreated(),
        workBasketService.getWorkbasket(id3).getModified());
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testInsertWorkbasketAccessUser()
      throws NotAuthorizedException, InvalidArgumentException, DomainNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException {

    Workbasket wb =
        createTestWorkbasket(
            "k100000000000000000000000000000000000000",
            "key1",
            "DOMAIN_A",
            "name",
            WorkbasketType.PERSONAL);
    workBasketService.createWorkbasket(wb);
    WorkbasketAccessItem accessItem =
        workBasketService.newWorkbasketAccessItem(
            "k100000000000000000000000000000000000000", "Arthur Dent");
    accessItem.setPermOpen(true);
    accessItem.setPermRead(true);
    workBasketService.createWorkbasketAccessItem(accessItem);

    assertEquals(
        1,
        workBasketService
            .getWorkbasketAccessItems("k100000000000000000000000000000000000000")
            .size());
  }

  @WithAccessId(
      userName = "Elena",
      groupNames = {"businessadmin"})
  @Test
  void testUpdateWorkbasketAccessUser()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          DomainNotFoundException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
          WorkbasketAccessItemAlreadyExistException {
    WorkbasketImpl wb = (WorkbasketImpl) workBasketService.newWorkbasket("key", "DOMAIN_A");
    wb.setId("k200000000000000000000000000000000000000");
    wb.setName("name");
    wb.setDescription("Description of a Workbasket...");
    wb.setType(WorkbasketType.GROUP);
    workBasketService.createWorkbasket(wb);

    WorkbasketAccessItem accessItem =
        workBasketService.newWorkbasketAccessItem(
            "k200000000000000000000000000000000000000", "Zaphod Beeblebrox");
    accessItem.setPermOpen(true);
    accessItem.setPermRead(true);
    workBasketService.createWorkbasketAccessItem(accessItem);

    assertEquals(
        1,
        workBasketService
            .getWorkbasketAccessItems("k200000000000000000000000000000000000000")
            .size());

    accessItem.setPermAppend(true);
    workBasketService.updateWorkbasketAccessItem(accessItem);

    if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
      assertEquals("zaphod beeblebrox", accessItem.getAccessId());
    } else {
      assertEquals("Zaphod Beeblebrox", accessItem.getAccessId());
    }
  }

  private void createWorkbasketWithSecurity(
      Workbasket wb,
      String accessId,
      boolean permOpen,
      boolean permRead,
      boolean permAppend,
      boolean permTransfer)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException {
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
