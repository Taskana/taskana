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
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
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

/**
 * Integration Test for workbasketServiceImpl with connection mode EXPLICIT.
 *
 * @author bbr
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

    @Test
    public void testSelectWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        workbasket0 = (WorkbasketImpl) workBasketService.createWorkbasket(workbasket0);
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
        workbasket2 = (WorkbasketImpl) workBasketService.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
        connection.commit();
        taskanaEngineImpl.closeConnection();
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail() throws WorkbasketNotFoundException, SQLException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        workBasketService.getWorkbasket("fail");
        connection.commit();
        taskanaEngineImpl.closeConnection();
    }

    @Test
    public void testSelectWorkbasketWithDistribution()
        throws WorkbasketNotFoundException, NotAuthorizedException, SQLException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        workBasketService = taskanaEngine.getWorkbasketService();
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        workbasket0 = (WorkbasketImpl) workBasketService.createWorkbasket(workbasket0);
        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setKey("key1");
        workbasket1.setName("Megabasket");
        workbasket1.setDomain("D2");
        workbasket1.setType(WorkbasketType.GROUP);
        workbasket1.setType(WorkbasketType.GROUP);
        workbasket1.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket1);
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setKey("key2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
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
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket0);

        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setKey("key1");
        workbasket1.setName("Megabasket");
        workbasket1.setType(WorkbasketType.GROUP);
        workbasket1.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket1);
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setKey("key2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setDistributionTargets(new ArrayList<>());
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
        workbasket2.getDistributionTargets().add(workbasket0);
        workbasket2.getDistributionTargets().add(workbasket1);
        workBasketService.createWorkbasket(workbasket2);
        Workbasket workbasket3 = workBasketService.newWorkbasket();
        workbasket3.setKey("key3");
        workbasket3.setName("hm ... irgend ein basket");
        workbasket3.setType(WorkbasketType.GROUP);
        workbasket3.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket3);
        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3);
        Thread.sleep(SLEEP_TIME);
        workBasketService.updateWorkbasket(workbasket2);

        Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

        List<Workbasket> distributionTargets = foundBasket.getDistributionTargets();
        Assert.assertEquals(1, distributionTargets.size());
        Assert.assertEquals(workbasket3.getId(), distributionTargets.get(0).getId());
        Assert.assertNotEquals(workBasketService.getWorkbasket(id2).getCreated(),
            workBasketService.getWorkbasket(id2).getModified());
        Assert.assertEquals(workBasketService.getWorkbasket(id1).getCreated(),
            workBasketService.getWorkbasket(id1).getModified());
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
