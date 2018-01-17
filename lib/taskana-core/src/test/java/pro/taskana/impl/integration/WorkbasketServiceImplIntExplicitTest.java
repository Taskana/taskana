package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for workbasketServiceImpl with connection mode EXPLICIT.
 *
 * @author bbr
 */
@RunWith(JAASRunner.class)
public class WorkbasketServiceImplIntExplicitTest {

    private static final int SLEEP_TIME = 100;
    private static final int THREE = 3;
    static int counter = 0;
    private DataSource dataSource;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private WorkbasketService workBasketService;

    @BeforeClass
    public static void resetDb() throws SQLException {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
    }

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void testInsertWorkbasket()
        throws NotAuthorizedException, SQLException, InvalidWorkbasketException, WorkbasketNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        int before = workBasketService.getWorkbaskets().size();
        WorkbasketImpl workbasket = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id1);
        workbasket.setKey("key");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket);
        Assert.assertEquals(before + 1, workBasketService.getWorkbaskets().size());
        taskanaEngineImpl.closeConnection();
    }

    @Test
    public void testSelectAllWorkbaskets()
        throws NotAuthorizedException, SQLException, InvalidWorkbasketException, WorkbasketNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        int before = workBasketService.getWorkbaskets().size();
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket0);
        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setKey("key1");
        workbasket1.setName("Megabasket");
        workbasket1.setType(WorkbasketType.GROUP);
        workbasket1.setDomain("novatec");
        workbasket1 = (WorkbasketImpl) workBasketService.createWorkbasket(workbasket1);
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setKey("key2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket2);
        Assert.assertEquals(before + THREE, workBasketService.getWorkbaskets().size());
        connection.commit();
        taskanaEngineImpl.closeConnection();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testSelectWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketImpl workbasket = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id0);
        workbasket.setKey("key0");
        workbasket.setName("Superbasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasket = (WorkbasketImpl) workBasketService.createWorkbasket(workbasket);

        createWorkbasketWithSecurity(workbasket, "Elena", true, true, true, true);
        connection.commit();
        workbasket = (WorkbasketImpl) workBasketService.getWorkbasket(workbasket.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = NotAuthorizedException.class)
    public void testGetWorkbasketFail()
        throws WorkbasketNotFoundException, SQLException, InvalidWorkbasketException, NotAuthorizedException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();

        Workbasket wb = createTestWorkbasket("ID-1", "KEY-1", "DOMAIN", "Name-1", WorkbasketType.PERSONAL);
        wb = workBasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(wb, "Elena", false, false, false, false);

        workBasketService.getWorkbasket(wb.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testSelectWorkbasketWithDistribution()
        throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();

        String id0 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket0 = createTestWorkbasket(id0, "key0", "novatec", "Superbasket", WorkbasketType.GROUP);
        workbasket0 = workBasketService.createWorkbasket(workbasket0);
        createWorkbasketWithSecurity(workbasket0, "Elena", true, true, false, false);

        String id1 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket1 = createTestWorkbasket(id1, "key1", "novatec", "Megabasket", WorkbasketType.GROUP);
        workbasket1 = workBasketService.createWorkbasket(workbasket1);
        createWorkbasketWithSecurity(workbasket1, "Elena", true, true, false, false);

        String id2 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket2 = createTestWorkbasket(id2, "key2", "novatec", "Hyperbasket", WorkbasketType.GROUP);
        workbasket2.setDistributionTargets(new ArrayList<>());
        workbasket2.getDistributionTargets().add(workbasket0.asSummary());
        workbasket2.getDistributionTargets().add(workbasket1.asSummary());
        workbasket2 = workBasketService.createWorkbasket(workbasket2);
        createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);

        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
        Assert.assertEquals(2, foundWorkbasket.getDistributionTargets().size());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testUpdateWorkbasket() throws Exception {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket0 = createTestWorkbasket(id0, "key0", "novatec", "Superbasket", WorkbasketType.GROUP);
        workbasket0 = workBasketService.createWorkbasket(workbasket0);
        createWorkbasketWithSecurity(workbasket0, "Elena", true, true, false, false);

        String id1 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket1 = createTestWorkbasket(id1, "key1", "novatec", "Megabasket", WorkbasketType.GROUP);
        workbasket1 = workBasketService.createWorkbasket(workbasket1);
        createWorkbasketWithSecurity(workbasket1, "Elena", true, true, false, false);

        String id2 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket2 = createTestWorkbasket(id2, "key2", "novatec", "Hyperbasket", WorkbasketType.GROUP);
        workbasket2.setDistributionTargets(new ArrayList<>());
        workbasket2.getDistributionTargets().add(workbasket0.asSummary());
        workbasket2.getDistributionTargets().add(workbasket1.asSummary());
        workbasket2 = workBasketService.createWorkbasket(workbasket2);
        createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);

        String id3 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket3 = createTestWorkbasket(id3, "key3", "novatec", "hm ... irgend ein basket",
            WorkbasketType.GROUP);
        workbasket3.setDistributionTargets(new ArrayList<>());
        workbasket3.getDistributionTargets().add(workbasket0.asSummary());
        workbasket3.getDistributionTargets().add(workbasket1.asSummary());
        workbasket3 = workBasketService.createWorkbasket(workbasket3);
        createWorkbasketWithSecurity(workbasket3, "Elena", true, true, false, false);

        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3.asSummary());
        Thread.sleep(SLEEP_TIME);
        workbasket2 = workBasketService.updateWorkbasket(workbasket2);

        Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

        List<WorkbasketSummary> distributionTargets = foundBasket.getDistributionTargets();
        Assert.assertEquals(1, distributionTargets.size());
        Assert.assertEquals(workbasket3.getId(), distributionTargets.get(0).getId());
        Assert.assertNotEquals(workBasketService.getWorkbasket(id2).getCreated(),
            workBasketService.getWorkbasket(id2).getModified());
        Assert.assertEquals(workBasketService.getWorkbasket(id1).getCreated(),
            workBasketService.getWorkbasket(id1).getModified());
        Assert.assertEquals(workBasketService.getWorkbasket(id3).getCreated(),
            workBasketService.getWorkbasket(id3).getModified());
        connection.commit();
    }

    @Test
    public void testInsertWorkbasketAccessUser() throws NotAuthorizedException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketKey("Key1");
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workBasketService.getAllAuthorizations().size());
        connection.commit();
    }

    @Test
    public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketKey("key2");
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workBasketService.getAllAuthorizations().size());

        accessItem.setAccessId("Zaphod Beeblebrox");
        workBasketService.updateWorkbasketAuthorization(accessItem);

        Assert.assertEquals("zaphod beeblebrox",
            workBasketService.getWorkbasketAuthorization(accessItem.getId()).getAccessId());
        connection.commit();
    }

    private void createWorkbasketWithSecurity(Workbasket wb, String accessId, boolean permOpen,
        boolean permRead, boolean permAppend, boolean permTransfer) {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey(wb.getKey());
        accessItem.setAccessId(accessId);
        accessItem.setPermOpen(permOpen);
        accessItem.setPermRead(permRead);
        accessItem.setPermAppend(permAppend);
        accessItem.setPermTransfer(permTransfer);
        workBasketService.createWorkbasketAuthorization(accessItem);
    }

    private Workbasket createTestWorkbasket(String id, String key, String domain, String name, WorkbasketType type) {
        WorkbasketImpl wb = (WorkbasketImpl) workBasketService.newWorkbasket();
        wb.setId(id);
        wb.setKey(key);
        wb.setDomain(domain);
        wb.setName(name);
        wb.setDescription("Description of a Workbasket...");
        wb.setType(type);
        return wb;
    }

    @After
    public void cleanUp() {
        taskanaEngineImpl.setConnection(null);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
