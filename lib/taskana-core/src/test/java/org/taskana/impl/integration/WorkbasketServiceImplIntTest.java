package org.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.taskana.TaskanaEngine;
import org.taskana.WorkbasketService;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.model.Workbasket;
import org.taskana.model.WorkbasketAccessItem;


/**
 * Integration Test for workbasketServiceImpl.
 * @author EH
 */
public class WorkbasketServiceImplIntTest {

    private static final int SLEEP_TIME = 100;
    private static final int THREE = 3;

    WorkbasketService workbasketServiceImpl;
    static int counter = 0;

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test-db-workbasket" + counter++);
        ds.setPassword("sa");
        ds.setUser("sa");
        TaskanaEngineConfiguration taskEngineConfiguration = new TaskanaEngineConfiguration(ds, false);

        TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();
        workbasketServiceImpl = te.getWorkbasketService();
    }

    @Test
    public void testInsertWorkbasket() throws NotAuthorizedException {
        int before = workbasketServiceImpl.getWorkbaskets().size();
        Workbasket workbasket = new Workbasket();
        workbasket.setId("1");
        workbasket.setName("Megabasket");
        workbasketServiceImpl.createWorkbasket(workbasket);
        Assert.assertEquals(before + 1, workbasketServiceImpl.getWorkbaskets().size());
    }

    @Test
    public void testSelectAllWorkbaskets() throws NotAuthorizedException {
        int before = workbasketServiceImpl.getWorkbaskets().size();
        Workbasket workbasket0 = new Workbasket();
        workbasket0.setId("0");
        workbasket0.setName("Superbasket");
        workbasketServiceImpl.createWorkbasket(workbasket0);
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("1");
        workbasket1.setName("Megabasket");
        workbasketServiceImpl.createWorkbasket(workbasket1);
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("2");
        workbasket2.setName("Hyperbasket");
        workbasketServiceImpl.createWorkbasket(workbasket2);
        Assert.assertEquals(before + THREE, workbasketServiceImpl.getWorkbaskets().size());
    }

    @Test
    public void testSelectWorkbasket() throws WorkbasketNotFoundException, NotAuthorizedException {
        Workbasket workbasket0 = new Workbasket();
        workbasket0.setId("0");
        workbasket0.setName("Superbasket");
        workbasketServiceImpl.createWorkbasket(workbasket0);
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("1");
        workbasket1.setName("Megabasket");
        workbasketServiceImpl.createWorkbasket(workbasket1);
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("2");
        workbasket2.setName("Hyperbasket");
        workbasketServiceImpl.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workbasketServiceImpl.getWorkbasket("2");
        Assert.assertEquals("2", foundWorkbasket.getId());
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail() throws WorkbasketNotFoundException {
        workbasketServiceImpl.getWorkbasket("fail");
    }

    @Test
    public void testSelectWorkbasketWithDistribution() throws WorkbasketNotFoundException, NotAuthorizedException {
        Workbasket workbasket0 = new Workbasket();
        workbasket0.setId("0");
        workbasket0.setName("Superbasket");
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("1");
        workbasket1.setName("Megabasket");
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setDistributionTargets(new ArrayList<>());
        workbasket2.getDistributionTargets().add(workbasket0);
        workbasket2.getDistributionTargets().add(workbasket1);
        workbasketServiceImpl.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workbasketServiceImpl.getWorkbasket("2");
        Assert.assertEquals("2", foundWorkbasket.getId());
        Assert.assertEquals(2, foundWorkbasket.getDistributionTargets().size());
    }

    @Test
    public void testUpdateWorkbasket() throws Exception {
        Workbasket workbasket0 = new Workbasket();
        workbasket0.setId("0");
        workbasket0.setName("Superbasket");
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("1");
        workbasket1.setName("Megabasket");
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("2");
        workbasket2.setName("Hyperbasket");
        workbasket2.getDistributionTargets().add(workbasket0);
        workbasket2.getDistributionTargets().add(workbasket1);
        workbasketServiceImpl.createWorkbasket(workbasket2);

        Workbasket workbasket3 = new Workbasket();
        workbasket3.setId("3");
        workbasket3.setName("hm ... irgend ein basket");
        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3);
        Thread.sleep(SLEEP_TIME);
        workbasketServiceImpl.updateWorkbasket(workbasket2);

        Workbasket foundBasket = workbasketServiceImpl.getWorkbasket(workbasket2.getId());

        List<Workbasket> distributionTargets = foundBasket.getDistributionTargets();
        Assert.assertEquals(1, distributionTargets.size());
        Assert.assertEquals("3", distributionTargets.get(0).getId());
        Assert.assertNotEquals(workbasketServiceImpl.getWorkbasket("2").getCreated(),
                workbasketServiceImpl.getWorkbasket("2").getModified());
        Assert.assertEquals(workbasketServiceImpl.getWorkbasket("1").getCreated(),
                workbasketServiceImpl.getWorkbasket("1").getModified());
        Assert.assertEquals(workbasketServiceImpl.getWorkbasket("3").getCreated(),
                workbasketServiceImpl.getWorkbasket("3").getModified());
    }

    @Test
    public void testInsertWorkbasketAccessUser() throws NotAuthorizedException {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketId("1");
        accessItem.setUserId("Arthur Dent");
        accessItem.setOpen(true);
        accessItem.setRead(true);
        workbasketServiceImpl.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workbasketServiceImpl.getAllAuthorizations().size());
    }

    @Test
    public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketId("1");
        accessItem.setUserId("Arthur Dent");
        accessItem.setOpen(true);
        accessItem.setRead(true);
        workbasketServiceImpl.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workbasketServiceImpl.getAllAuthorizations().size());

        accessItem.setUserId("Zaphod Beeblebrox");
        workbasketServiceImpl.updateWorkbasketAuthorization(accessItem);

        Assert.assertEquals("Zaphod Beeblebrox",
                workbasketServiceImpl.getWorkbasketAuthorization(accessItem.getId()).getUserId());
    }

}
