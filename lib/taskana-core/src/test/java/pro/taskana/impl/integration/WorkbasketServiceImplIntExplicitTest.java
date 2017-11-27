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

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;

/**
 * Integration Test for workbasketServiceImpl with connection mode EXPLICIT.
 * @author bbr
 *
 */
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
    public void testInsertWorkbasket() throws NotAuthorizedException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        int before = workBasketService.getWorkbaskets().size();
        Workbasket workbasket = new Workbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id1);
        workbasket.setName("Megabasket");
        workBasketService.createWorkbasket(workbasket);
        Assert.assertEquals(before + 1, workBasketService.getWorkbaskets().size());
        taskanaEngineImpl.closeConnection();
    }

    @Test
    public void testSelectAllWorkbaskets() throws NotAuthorizedException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        int before = workBasketService.getWorkbaskets().size();
        Workbasket workbasket0 = new Workbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setName("Superbasket");
        workBasketService.createWorkbasket(workbasket0);
        Workbasket workbasket1 = new Workbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setName("Megabasket");
        workBasketService.createWorkbasket(workbasket1);
        Workbasket workbasket2 = new Workbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setName("Hyperbasket");
        workBasketService.createWorkbasket(workbasket2);
        Assert.assertEquals(before + THREE, workBasketService.getWorkbaskets().size());
        connection.commit();
        taskanaEngineImpl.closeConnection();
    }

    @Test
    public void testSelectWorkbasket() throws WorkbasketNotFoundException, NotAuthorizedException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket0 = new Workbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setName("Superbasket");
        workBasketService.createWorkbasket(workbasket0);
        Workbasket workbasket1 = new Workbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setName("Megabasket");
        workBasketService.createWorkbasket(workbasket1);
        Workbasket workbasket2 = new Workbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setName("Hyperbasket");
        workBasketService.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
        connection.commit();
        taskanaEngineImpl.closeConnection();
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail() throws WorkbasketNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        workBasketService.getWorkbasket("fail");
        connection.commit();
        taskanaEngineImpl.closeConnection();
    }

    @Test
    public void testSelectWorkbasketWithDistribution() throws WorkbasketNotFoundException, NotAuthorizedException, SQLException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket0 = new Workbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setName("Superbasket");
        Workbasket workbasket1 = new Workbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setName("Megabasket");
        Workbasket workbasket2 = new Workbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setName("Hyperbasket");
        workbasket2.setDistributionTargets(new ArrayList<>());
        workbasket2.getDistributionTargets().add(workbasket0);
        workbasket2.getDistributionTargets().add(workbasket1);
        workBasketService.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
        Assert.assertEquals(2, foundWorkbasket.getDistributionTargets().size());
        connection.commit();
    }

    @Test
    public void testUpdateWorkbasket() throws Exception {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket0 = new Workbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setName("Superbasket");
        Workbasket workbasket1 = new Workbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setName("Megabasket");
        Workbasket workbasket2 = new Workbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setName("Hyperbasket");
        workbasket2.getDistributionTargets().add(workbasket0);
        workbasket2.getDistributionTargets().add(workbasket1);
        workBasketService.createWorkbasket(workbasket2);

        Workbasket workbasket3 = new Workbasket();
        String id3 = IdGenerator.generateWithPrefix("TWB");
        workbasket3.setId(id3);
        workbasket3.setName("hm ... irgend ein basket");
        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3);
        Thread.sleep(SLEEP_TIME);
        workBasketService.updateWorkbasket(workbasket2);

        Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

        List<Workbasket> distributionTargets = foundBasket.getDistributionTargets();
        Assert.assertEquals(1, distributionTargets.size());
        Assert.assertEquals(id3, distributionTargets.get(0).getId());
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
        String id1 = IdGenerator.generateWithPrefix("TWB");
        accessItem.setWorkbasketId(id1);
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
        String id1 = IdGenerator.generateWithPrefix("TWB");
        accessItem.setWorkbasketId(id1);
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workBasketService.getAllAuthorizations().size());

        accessItem.setAccessId("Zaphod Beeblebrox");
        workBasketService.updateWorkbasketAuthorization(accessItem);

        Assert.assertEquals("Zaphod Beeblebrox",
                workBasketService.getWorkbasketAuthorization(accessItem.getId()).getAccessId());
        connection.commit();
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
