package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
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

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testSelectWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, InvalidWorkbasketException,
        InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketImpl workbasket = (WorkbasketImpl) workBasketService.newWorkbasket("key0", "novatec");
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id0);
        workbasket.setName("Superbasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket = (WorkbasketImpl) workBasketService.createWorkbasket(workbasket);

        createWorkbasketWithSecurity(workbasket, "Elena", true, true, true, true);
        connection.commit();
        workbasket = (WorkbasketImpl) workBasketService.getWorkbasket(workbasket.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = NotAuthorizedException.class)
    public void testGetWorkbasketFail()
        throws WorkbasketNotFoundException, SQLException, InvalidWorkbasketException, NotAuthorizedException,
        InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();

        Workbasket wb = createTestWorkbasket("ID-1", "KEY-1", "DOMAIN", "Name-1", WorkbasketType.PERSONAL);
        wb = workBasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(wb, "Elena", false, false, false, false);

        workBasketService.getWorkbasket(wb.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testSelectWorkbasketWithDistribution()
        throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, InvalidWorkbasketException,
        InvalidArgumentException {
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
        workbasket2 = workBasketService.createWorkbasket(workbasket2);
        createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);

        List<String> distributionTargets = new ArrayList<>(Arrays.asList(workbasket0.getId(), workbasket1.getId()));
        workBasketService.setDistributionTargets(workbasket2.getId(), distributionTargets);

        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
        Assert.assertEquals(2, workBasketService.getDistributionTargets(foundWorkbasket.getId()).size());
        connection.commit();
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
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
        workbasket2 = workBasketService.createWorkbasket(workbasket2);
        createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);

        List<String> distTargets = new ArrayList<>(Arrays.asList(workbasket0.getId(), workbasket1.getId()));
        Thread.sleep(SLEEP_TIME);
        workBasketService.setDistributionTargets(workbasket2.getId(), distTargets);

        String id3 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket3 = createTestWorkbasket(id3, "key3", "novatec", "hm ... irgend ein basket",
            WorkbasketType.GROUP);
        workbasket3 = workBasketService.createWorkbasket(workbasket3);
        createWorkbasketWithSecurity(workbasket3, "Elena", true, true, false, false);

        List<String> newDistTargets = new ArrayList<>(Arrays.asList(workbasket3.getId()));
        Thread.sleep(SLEEP_TIME);
        workBasketService.setDistributionTargets(workbasket2.getId(), newDistTargets);

        Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

        List<WorkbasketSummary> distributionTargets = workBasketService.getDistributionTargets(foundBasket.getId());
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

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testInsertWorkbasketAccessUser() throws NotAuthorizedException, SQLException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(
            "Key1",
            "Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAccessItem(accessItem);

        Assert.assertEquals(1, workBasketService.getWorkbasketAccessItems("Key1").size());
        connection.commit();
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException, SQLException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(
            "key2",
            "Zaphod Beeblebrox");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAccessItem(accessItem);

        Assert.assertEquals(1, workBasketService.getWorkbasketAccessItems("key2").size());
        Assert.assertEquals("zaphod beeblebrox", accessItem.getAccessId());
        connection.commit();
    }

    private void createWorkbasketWithSecurity(Workbasket wb, String accessId, boolean permOpen,
        boolean permRead, boolean permAppend, boolean permTransfer)
        throws InvalidArgumentException, NotAuthorizedException {
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(
            wb.getId(), accessId);
        accessItem.setPermOpen(permOpen);
        accessItem.setPermRead(permRead);
        accessItem.setPermAppend(permAppend);
        accessItem.setPermTransfer(permTransfer);
        workBasketService.createWorkbasketAccessItem(accessItem);
    }

    private Workbasket createTestWorkbasket(String id, String key, String domain, String name, WorkbasketType type)
        throws NotAuthorizedException {
        WorkbasketImpl wb = (WorkbasketImpl) workBasketService.newWorkbasket(key, domain);
        wb.setId(id);
        wb.setName(name);
        wb.setDescription("Description of a Workbasket...");
        wb.setType(type);
        return wb;
    }

    @After
    public void cleanUp() {
        taskanaEngineImpl.setConnection(null);
    }

}
