package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.security.GroupPrincipal;
import pro.taskana.security.UserPrincipal;


/**
 * Integration Test for workbasketServiceImpl with connection management mode AUTOCOMMIT.
 * @author EH
 */
public class WorkbasketServiceImplIntAutocommitTest {

    private static final int SLEEP_TIME = 100;
    private static final int THREE = 3;

    private static final int DIFF1 = 200000;
    private static final int DIFF2 = 400000;

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
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        workBasketService = taskanaEngine.getWorkbasketService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void testInsertWorkbasket() throws NotAuthorizedException {
        int before = workBasketService.getWorkbaskets().size();
        Workbasket workbasket = new Workbasket();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id1);
        workbasket.setName("Megabasket");
        workBasketService.createWorkbasket(workbasket);
        Assert.assertEquals(before + 1, workBasketService.getWorkbaskets().size());
    }

    @Test
    public void testSelectAllWorkbaskets() throws NotAuthorizedException {
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
    }

    @Test
    public void testSelectWorkbasket() throws WorkbasketNotFoundException, NotAuthorizedException {
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
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail() throws WorkbasketNotFoundException {
        workBasketService.getWorkbasket("fail");
    }

    @Test
    public void testSelectWorkbasketWithDistribution() throws WorkbasketNotFoundException, NotAuthorizedException {
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
    }

    @Test
    public void testUpdateWorkbasket() throws Exception {
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
    }

    @Test
    public void testInsertWorkbasketAccessUser() throws NotAuthorizedException {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        String id1 = IdGenerator.generateWithPrefix("TWB");
        accessItem.setWorkbasketId(id1);
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workBasketService.getAllAuthorizations().size());
    }

    @Test
    public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException {
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
    }

    @Test
    public void testWorkbasketQuery() throws NotAuthorizedException, InvalidArgumentException {

        generateSampleDataForQuery();

        WorkbasketQuery query1 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.OPEN, "Bernd").name("Basket1");
        List<Workbasket> result1 = query1.list();

        int numDistTargets = 1 + 1 + 1;
        Assert.assertEquals(1, result1.size());
        Assert.assertEquals(numDistTargets, result1.get(0).getDistributionTargets().size());

        WorkbasketQuery query2 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.OPEN, "Bernd", "Konstantin");
        List<Workbasket> result2 = query2.list();
        Assert.assertEquals(1, result2.size());

        WorkbasketQuery query3 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.CUSTOM_5, "Bernd", "Konstantin");
        List<Workbasket> result3 = query3.list();
        Assert.assertEquals(0, result3.size());

        WorkbasketQuery query4 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.CUSTOM_1, "Bernd");
        List<Workbasket> result4 = query4.list();
        Assert.assertEquals(0, result4.size());
    }


    @Test
    public void testGetWorkbasketsForCurrentUserAndPermission() throws NotAuthorizedException {
        generateSampleDataForQuery();

        String userName = "eberhardt";
        String[] groupNames = {"group2", "group3"};
        List<WorkbasketAuthorization> authorizations = new ArrayList<WorkbasketAuthorization>();

        authorizations.add(WorkbasketAuthorization.OPEN);
        Assert.assertTrue(1 == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

        userName = "Bernd";
        Assert.assertTrue(2 == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

        authorizations.add(WorkbasketAuthorization.CUSTOM_4);
        Assert.assertTrue(0  == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

        userName = "Holger";
        authorizations = new ArrayList<WorkbasketAuthorization>();
        authorizations.add(WorkbasketAuthorization.APPEND);
        Assert.assertTrue(1  == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

    }

    private int getWorkbasketsForPrincipalesAndPermissions(String userName, String[] groupNames,
            List<WorkbasketAuthorization> authorizations) throws NotAuthorizedException {
        Subject subject = new Subject();
        List<Principal> principalList = new ArrayList<>();
        principalList.add(new UserPrincipal(userName));
        for (int i = 0; i < groupNames.length; i++) {
            principalList.add(new GroupPrincipal(groupNames[i]));
        }
        subject.getPrincipals().addAll(principalList);

        int result = -1;
        try {
            result = Subject.doAs(subject, new PrivilegedExceptionAction<Integer>() {
                public Integer run() throws TaskNotFoundException, FileNotFoundException, NotAuthorizedException, SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    List<Workbasket> wbsResult = workBasketService.getWorkbaskets(authorizations);
                    return wbsResult.size();
                }
            });
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                Assert.assertTrue(cause instanceof NotAuthorizedException);
                throw (NotAuthorizedException) cause;
            }
        }
        return result;
    }

    public void generateSampleDataForQuery() {
        Date now = new Date();

        Workbasket basket2 = new Workbasket();
        basket2.setCreated(new Timestamp(now.getTime() - DIFF2));
        basket2.setId("2");
        basket2.setName("Basket2");
        basket2.setOwner("Eberhardt");
        workBasketService.createWorkbasket(basket2);

        Workbasket basket3 = new Workbasket();
        basket3.setCreated(new Timestamp(now.getTime() - DIFF1));
        basket3.setId("3");
        basket3.setName("Basket3");
        basket3.setOwner("Konstantin");
        workBasketService.createWorkbasket(basket3);

        Workbasket basket4 = new Workbasket();
        basket4.setCreated(new Timestamp(now.getTime() - DIFF1));
        basket4.setId("4");
        basket4.setName("Basket4");
        basket4.setOwner("Holger");
        workBasketService.createWorkbasket(basket4);

        Workbasket basket1 = new Workbasket();
        basket1.setCreated(new Timestamp(now.getTime() - DIFF1));
        basket1.setId("1");
        basket1.setName("Basket1");
        basket1.setOwner("Holger");
        List<Workbasket> distTargets = new ArrayList<Workbasket>();
        distTargets.add(basket2);
        distTargets.add(basket3);
        distTargets.add(basket4);
        basket1.setDistributionTargets(distTargets);
        workBasketService.createWorkbasket(basket1);

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketId("1");
        accessItem.setAccessId("Bernd");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketId("2");
        accessItem2.setAccessId("Eberhardt");
        accessItem2.setPermTransfer(true);
        accessItem2.setPermCustom1(true);
        workBasketService.createWorkbasketAuthorization(accessItem2);

        WorkbasketAccessItem accessItem3 = new WorkbasketAccessItem();
        accessItem3.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem3.setWorkbasketId("3");
        accessItem3.setAccessId("group2");
        accessItem3.setPermCustom4(true);
        accessItem3.setPermCustom1(true);
        workBasketService.createWorkbasketAuthorization(accessItem3);

        WorkbasketAccessItem accessItem4 = new WorkbasketAccessItem();
        accessItem4.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem4.setWorkbasketId("1");
        accessItem4.setAccessId("group3");
        accessItem4.setPermOpen(true);
        accessItem4.setPermRead(true);
        accessItem4.setPermAppend(true);
        workBasketService.createWorkbasketAuthorization(accessItem4);

    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
