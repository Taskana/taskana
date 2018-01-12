package pro.taskana.impl.integration;

import static org.junit.Assert.assertTrue;

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

import org.apache.ibatis.session.SqlSession;
import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.TaskanaEngineProxyForTest;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketSummary;
import pro.taskana.model.WorkbasketType;
import pro.taskana.model.mappings.WorkbasketMapper;
import pro.taskana.security.GroupPrincipal;
import pro.taskana.security.UserPrincipal;

/**
 * Integration Test for workbasketServiceImpl with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
public class WorkbasketServiceImplIntAutocommitTest {

    private static final int SLEEP_TIME = 100;

    private static final int THREE = 3;

    private static final long ONE_DAY = 24 * 3600 * 1000;

    private static final long TEN_DAYS = 10 * ONE_DAY;

    private static final long FIFTEEN_DAYS = 15 * ONE_DAY;

    private static final long TWENTY_DAYS = 2 * TEN_DAYS;

    static int counter = 0;

    private DataSource dataSource;

    private TaskanaEngineConfiguration taskanaEngineConfiguration;

    private TaskanaEngine taskanaEngine;

    private TaskanaEngineImpl taskanaEngineImpl;

    private WorkbasketService workBasketService;

    private Date workBasketsCreated;

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
    public void testInsertWorkbasket()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketNotFoundException {
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
    }

    @Test
    public void testSelectAllWorkbaskets()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketNotFoundException {
        int before = workBasketService.getWorkbaskets().size();
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        workbasket0.setType(WorkbasketType.PERSONAL);
        workbasket0.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket0);
        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
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
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket2);
        Assert.assertEquals(before + THREE, workBasketService.getWorkbaskets().size());
    }

    @Test
    public void testSelectWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
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
        workBasketService.createWorkbasket(workbasket1);
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setKey("key2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail() throws WorkbasketNotFoundException, InvalidWorkbasketException {
        workBasketService.getWorkbasket("fail");
    }

    @Test
    public void testSelectWorkbasketWithDistribution()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket0);
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setKey("key1");
        workbasket1.setName("Megabasket");
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket1.setType(WorkbasketType.GROUP);
        workbasket1.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket1);
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setKey("key2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
        workbasket2.setDistributionTargets(new ArrayList<>());
        workbasket2.getDistributionTargets().add(workbasket0.asSummary());
        workbasket2.getDistributionTargets().add(workbasket1.asSummary());
        workBasketService.createWorkbasket(workbasket2);
        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id2);
        Assert.assertEquals(id2, foundWorkbasket.getId());
        Assert.assertEquals(2, foundWorkbasket.getDistributionTargets().size());

    }

    @Test
    public void testUpdateWorkbasket() throws Exception {
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setKey("key0");
        workbasket0.setName("Superbasket");
        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket0.setType(WorkbasketType.GROUP);
        workbasket0.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket0);
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setKey("key1");
        workbasket1.setName("Megabasket");
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket1.setType(WorkbasketType.GROUP);
        workbasket1.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket1);
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setKey("key2");
        workbasket2.setName("Hyperbasket");
        workbasket2.setType(WorkbasketType.GROUP);
        workbasket2.setDomain("novatec");
        workbasket2.getDistributionTargets().add(workbasket0.asSummary());
        workbasket2.getDistributionTargets().add(workbasket1.asSummary());
        workBasketService.createWorkbasket(workbasket2);

        WorkbasketImpl workbasket3 = (WorkbasketImpl) workBasketService.newWorkbasket();
        String id3 = IdGenerator.generateWithPrefix("TWB");
        workbasket3.setId(id3);
        workbasket3.setKey("key3");
        workbasket3.setName("hm ... irgend ein basket");
        workbasket3.setType(WorkbasketType.GROUP);
        workbasket3.setDomain("novatec");
        workBasketService.createWorkbasket(workbasket3);
        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3.asSummary());
        Thread.sleep(SLEEP_TIME);
        workBasketService.updateWorkbasket(workbasket2);

        Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

        List<WorkbasketSummary> distributionTargets = foundBasket.getDistributionTargets();
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
        accessItem.setWorkbasketKey("k1");
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workBasketService.getAllAuthorizations().size());
    }

    @Test
    public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketKey("k2");
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1, workBasketService.getAllAuthorizations().size());

        accessItem.setAccessId("Zaphod Beeblebrox");
        workBasketService.updateWorkbasketAuthorization(accessItem);

        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            Assert.assertEquals("zaphod beeblebrox",
                workBasketService.getWorkbasketAuthorization(accessItem.getId()).getAccessId());
        } else {
            Assert.assertEquals("zaphod beeblebrox",
                workBasketService.getWorkbasketAuthorization(accessItem.getId()).getAccessId());
        }
    }

    @Test
    public void testWorkbasketQuery()
        throws NotAuthorizedException, InvalidArgumentException, InvalidWorkbasketException,
        WorkbasketNotFoundException {

        generateSampleDataForQuery();

        Date tomorrow = new Date(workBasketsCreated.getTime() + ONE_DAY);
        Date yesterday = new Date(workBasketsCreated.getTime() - ONE_DAY);
        Date tenDaysAgo = new Date(workBasketsCreated.getTime() - TEN_DAYS);
        Date fifteenDaysAgo = new Date(workBasketsCreated.getTime() - FIFTEEN_DAYS);
        Date twentyDaysAgo = new Date(workBasketsCreated.getTime() - TWENTY_DAYS);
        Date thirtyDaysAgo = new Date(workBasketsCreated.getTime() - THREE * TEN_DAYS);

        WorkbasketQuery query1 = workBasketService.createWorkbasketQuery()
            .access(WorkbasketAuthorization.OPEN, "Bernd")
            .nameIn("Basket1");
        List<WorkbasketSummary> result1 = query1.list();

        Assert.assertEquals(1, result1.size());
        String workbasketId = result1.get(0).getId();
        Workbasket workBasket = workBasketService.getWorkbasket(workbasketId);
        Assert.assertEquals(THREE, workBasket.getDistributionTargets().size());

        WorkbasketQuery query2 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.OPEN, "Bernd",
            "Konstantin");
        List<WorkbasketSummary> result2 = query2.list();
        Assert.assertEquals(1, result2.size());

        WorkbasketQuery query3 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.CUSTOM_5,
            "Bernd", "Konstantin");
        List<WorkbasketSummary> result3 = query3.list();
        Assert.assertEquals(0, result3.size());

        WorkbasketQuery query4 = workBasketService.createWorkbasketQuery().access(WorkbasketAuthorization.CUSTOM_1,
            "Bernd");
        List<WorkbasketSummary> result4 = query4.list();
        Assert.assertEquals(0, result4.size());

        WorkbasketQuery query0 = workBasketService.createWorkbasketQuery()
            .createdBefore(tomorrow)
            .createdAfter(yesterday)
            .nameIn("Basket1", "Basket2", "Basket3");
        List<WorkbasketSummary> result0 = query0.list();
        assertTrue(result0.size() == THREE);
        for (WorkbasketSummary workbasket : result0) {
            String name = workbasket.getName();
            assertTrue("Basket1".equals(name) || "Basket2".equals(name) || "Basket3".equals(name));
        }

        WorkbasketQuery query5 = workBasketService.createWorkbasketQuery()
            .modifiedAfter(thirtyDaysAgo)
            .modifiedBefore(tenDaysAgo);
        List<WorkbasketSummary> result5 = query5.list();
        assertTrue(result5.size() == 2);
        for (WorkbasketSummary workbasket : result5) {
            String name = workbasket.getName();
            assertTrue("Basket2".equals(name) || "Basket3".equals(name));
        }

        WorkbasketQuery query6 = workBasketService.createWorkbasketQuery()
            .modifiedAfter(twentyDaysAgo)
            .domainIn("novatec", "consulting");
        List<WorkbasketSummary> result6 = query6.list();
        assertTrue(result6.size() == 1);
        assertTrue("Basket2".equals(result6.get(0).getName()));

        WorkbasketQuery query7 = workBasketService.createWorkbasketQuery()
            .typeIn(WorkbasketType.GROUP, WorkbasketType.CLEARANCE);
        List<WorkbasketSummary> result7 = query7.list();
        assertTrue(result7.size() == 2);
        for (WorkbasketSummary workbasket : result7) {
            String name = workbasket.getName();
            assertTrue("Basket2".equals(name) || "Basket3".equals(name));
        }

    }

    @Test
    public void testGetWorkbasketsForCurrentUserAndPermission()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketNotFoundException {
        generateSampleDataForQuery();

        String userName = "Bernd";
        String[] groupNames = {"group2", "group3"};
        List<WorkbasketAuthorization> authorizations = new ArrayList<WorkbasketAuthorization>();

        authorizations.add(WorkbasketAuthorization.OPEN);
        Assert.assertTrue(2 == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

        authorizations.add(WorkbasketAuthorization.CUSTOM_4);
        Assert.assertTrue(0 == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

        userName = "Holger";
        authorizations = new ArrayList<WorkbasketAuthorization>();
        authorizations.add(WorkbasketAuthorization.APPEND);
        Assert.assertTrue(1 == getWorkbasketsForPrincipalesAndPermissions(userName, groupNames, authorizations));

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

                @Override
                public Integer run() throws TaskNotFoundException, FileNotFoundException, NotAuthorizedException,
                    SQLException, WorkbasketNotFoundException, ClassificationNotFoundException {
                    List<WorkbasketSummary> wbsResult = workBasketService.getWorkbaskets(authorizations);
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

    public void generateSampleDataForQuery() throws InvalidWorkbasketException, WorkbasketNotFoundException {
        workBasketsCreated = new Date();

        WorkbasketImpl basket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket2.setId("2");
        basket2.setKey("k2");
        basket2.setName("Basket2");
        basket2.setOwner("Eberhardt");
        basket2.setType(WorkbasketType.GROUP);
        basket2.setDomain("novatec");
        basket2 = (WorkbasketImpl) workBasketService.createWorkbasket(basket2);

        WorkbasketImpl basket3 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket3.setId("3");
        basket3.setKey("k3");
        basket3.setName("Basket3");
        basket3.setOwner("Konstantin");
        basket3.setType(WorkbasketType.CLEARANCE);
        basket3.setDomain("consulting");
        basket3 = (WorkbasketImpl) workBasketService.createWorkbasket(basket3);

        WorkbasketImpl basket4 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket4.setId("4");
        basket4.setKey("k4");
        basket4.setName("Basket4");
        basket4.setOwner("Holger");
        basket4.setType(WorkbasketType.TOPIC);
        basket4.setDomain("develop");
        basket4 = (WorkbasketImpl) workBasketService.createWorkbasket(basket4);

        WorkbasketImpl basket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket1.setId("1");
        basket1.setKey("k1");
        basket1.setName("Basket1");
        basket1.setOwner("Holger");
        basket1.setType(WorkbasketType.PERSONAL);
        basket1.setDomain("");
        List<WorkbasketSummary> distTargets = new ArrayList<WorkbasketSummary>();
        distTargets.add(basket2.asSummary());
        distTargets.add(basket3.asSummary());
        distTargets.add(basket4.asSummary());
        basket1.setDistributionTargets(distTargets);
        basket1 = (WorkbasketImpl) workBasketService.createWorkbasket(basket1);

        updateModifiedTimestamps(basket2, basket3, basket4, basket1);

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("k1");
        accessItem.setAccessId("Bernd");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketKey("k2");
        accessItem2.setAccessId("Eberhardt");
        accessItem2.setPermTransfer(true);
        accessItem2.setPermCustom1(true);
        workBasketService.createWorkbasketAuthorization(accessItem2);

        WorkbasketAccessItem accessItem3 = new WorkbasketAccessItem();
        accessItem3.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem3.setWorkbasketKey("k3");
        accessItem3.setAccessId("group2");
        accessItem3.setPermCustom4(true);
        accessItem3.setPermCustom1(true);
        workBasketService.createWorkbasketAuthorization(accessItem3);

        WorkbasketAccessItem accessItem4 = new WorkbasketAccessItem();
        accessItem4.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem4.setWorkbasketKey("k4");
        accessItem4.setAccessId("group3");
        accessItem4.setPermOpen(true);
        accessItem4.setPermRead(true);
        accessItem4.setPermAppend(true);
        workBasketService.createWorkbasketAuthorization(accessItem4);

    }

    private void updateModifiedTimestamps(Workbasket basket2, Workbasket basket3, Workbasket basket4,
        Workbasket basket1) {
        // created and modified timestamps are set by WorkbasketServiceImpl to 'now' when the workbasket is created
        // in order to create timestamps distict from the current time, we must use the mapper directly to bypass
        // WorkbasketServiceImpl
        TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngineImpl);
        SqlSession session = engineProxy.getSqlSession();
        WorkbasketMapper mapper = session.getMapper(WorkbasketMapper.class);

        WorkbasketImpl wb1 = (WorkbasketImpl) basket1;
        WorkbasketImpl wb2 = (WorkbasketImpl) basket2;
        WorkbasketImpl wb3 = (WorkbasketImpl) basket3;
        WorkbasketImpl wb4 = (WorkbasketImpl) basket4;
        engineProxy.openConnection();
        wb1.setModified(new Timestamp(workBasketsCreated.getTime() - TEN_DAYS));
        mapper.update(wb1);
        wb2.setModified(new Timestamp(workBasketsCreated.getTime() - FIFTEEN_DAYS));
        mapper.update(wb2);
        wb3.setModified(new Timestamp(workBasketsCreated.getTime() - TWENTY_DAYS));
        mapper.update(wb3);
        wb4.setModified(new Timestamp(workBasketsCreated.getTime() - (2 * FIFTEEN_DAYS)));
        mapper.update(wb4);
        engineProxy.returnConnection();
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
