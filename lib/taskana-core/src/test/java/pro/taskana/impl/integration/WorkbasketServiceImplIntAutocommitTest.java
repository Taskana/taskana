package pro.taskana.impl.integration;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.TaskanaEngineProxyForTest;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketType;
import pro.taskana.model.mappings.WorkbasketMapper;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for workbasketServiceImpl with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
@RunWith(JAASRunner.class)
public class WorkbasketServiceImplIntAutocommitTest {

    private static final int SLEEP_TIME = 100;
    private static final int THREE = 3;
    private static final Duration ONE_DAY = Duration.ofDays(1L);
    private static final Duration TEN_DAYS = Duration.ofDays(10L);
    private static final Duration FIFTEEN_DAYS = Duration.ofDays(15L);
    private static final Duration TWENTY_DAYS = Duration.ofDays(20L);
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

    @WithAccessId(userName = "Elena")
    @Test
    public void testSelectWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        String id = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket = createTestWorkbasket(id, "key0", "novatec", "Superbasket", WorkbasketType.GROUP);
        workbasket = workBasketService.createWorkbasket(workbasket);
        createWorkbasketWithSecurity(workbasket, CurrentUserContext.getUserid(), true, true, false, false);
        Workbasket foundWorkbasket = workBasketService.getWorkbasket(id);
        Assert.assertEquals(id, foundWorkbasket.getId());
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail()
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        workBasketService.getWorkbasket("fail");
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testSelectWorkbasketWithDistribution()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        String id = IdGenerator.generateWithPrefix("TWB");
        Workbasket wbDist1 = createTestWorkbasket(id, "key0", "novatec", "Superbasket", WorkbasketType.GROUP);
        wbDist1 = workBasketService.createWorkbasket(wbDist1);
        createWorkbasketWithSecurity(wbDist1, CurrentUserContext.getUserid(), true, true, false, false);

        id = IdGenerator.generateWithPrefix("TWB");
        Workbasket wbDist2 = createTestWorkbasket(id, "key1", "novatec", "Megabasket", WorkbasketType.GROUP);
        wbDist2 = workBasketService.createWorkbasket(wbDist2);
        createWorkbasketWithSecurity(wbDist2, "Elena", true, true, false, false);

        id = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket = createTestWorkbasket(id, "key2", "novatec", "Hyperbasket", WorkbasketType.GROUP);
        workbasket.setDistributionTargets(new ArrayList<>());
        workbasket.getDistributionTargets().add(wbDist1.asSummary());
        workbasket.getDistributionTargets().add(wbDist2.asSummary());
        workbasket = workBasketService.createWorkbasket(workbasket);
        createWorkbasketWithSecurity(workbasket, "Elena", true, true, false, false);

        Workbasket foundWorkbasket = workBasketService.getWorkbasket(workbasket.getId());
        Assert.assertEquals(id, foundWorkbasket.getId());
        Assert.assertEquals(2, foundWorkbasket.getDistributionTargets().size());

    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testUpdateWorkbasket() throws Exception {
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
        workbasket3 = workBasketService.createWorkbasket(workbasket3);
        createWorkbasketWithSecurity(workbasket3, "Elena", true, true, false, false);

        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3.asSummary());
        Thread.sleep(SLEEP_TIME);
        workbasket2 = workBasketService.updateWorkbasket(workbasket2);

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

    @WithAccessId(userName = "Bernd", groupNames = {"group1", "group2", "group3", "group4"})
    @Test
    public void testWorkbasketQuery()
        throws NotAuthorizedException, InvalidArgumentException, InvalidWorkbasketException,
        WorkbasketNotFoundException {

        generateSampleDataForQuery();

        Instant now = Instant.now();
        Instant tomorrow = now.plus(ONE_DAY);
        Instant yesterday = now.minus(ONE_DAY);
        Instant tenDaysAgo = now.minus(TEN_DAYS);
        Instant twentyDaysAgo = now.minus(TWENTY_DAYS);
        Instant thirtyDaysAgo = now.minus(TWENTY_DAYS).minus(TEN_DAYS);

        WorkbasketQuery query1 = workBasketService.createWorkbasketQuery()
            .accessIdsHavePersmission(WorkbasketAuthorization.OPEN, "Bernd")
            .nameIn("Basket4");
        List<WorkbasketSummary> result1 = query1.list();

        Assert.assertEquals(1, result1.size());
        String workbasketId = result1.get(0).getId();
        Workbasket workBasket = workBasketService.getWorkbasket(workbasketId);
        Assert.assertEquals(THREE, workBasket.getDistributionTargets().size());

        WorkbasketQuery query2 = workBasketService.createWorkbasketQuery().accessIdsHavePersmission(
            WorkbasketAuthorization.OPEN, "Bernd",
            "Konstantin");
        List<WorkbasketSummary> result2 = query2.list();
        Assert.assertEquals(2, result2.size());

        WorkbasketQuery query3 = workBasketService.createWorkbasketQuery().accessIdsHavePersmission(
            WorkbasketAuthorization.CUSTOM_5,
            "Bernd", "Konstantin");
        List<WorkbasketSummary> result3 = query3.list();
        Assert.assertEquals(0, result3.size());

        WorkbasketQuery query4 = workBasketService.createWorkbasketQuery().accessIdsHavePersmission(
            WorkbasketAuthorization.CUSTOM_1,
            "Bernd");
        List<WorkbasketSummary> result4 = query4.list();
        Assert.assertEquals(1, result4.size());

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
            .modifiedAfter(now.minus(Duration.ofDays(31L)))
            .modifiedBefore(now.minus(Duration.ofDays(9)));
        List<WorkbasketSummary> result5 = query5.list();
        assertTrue(result5.size() == 4);
        for (WorkbasketSummary workbasket : result5) {
            String name = workbasket.getName();
            assertTrue(
                "Basket1".equals(name) || "Basket2".equals(name) || "Basket3".equals(name) || "Basket4".equals(name));
        }

        WorkbasketQuery query6 = workBasketService.createWorkbasketQuery()
            .modifiedAfter(twentyDaysAgo)
            .domainIn("novatec", "consulting");
        List<WorkbasketSummary> result6 = query6.list();
        assertTrue(result6.size() == 1);
        assertTrue("Basket1".equals(result6.get(0).getName()));

        WorkbasketQuery query7 = workBasketService.createWorkbasketQuery()
            .typeIn(WorkbasketType.GROUP, WorkbasketType.CLEARANCE);
        List<WorkbasketSummary> result7 = query7.list();
        assertTrue(result7.size() == 2);
        for (WorkbasketSummary workbasket : result7) {
            String name = workbasket.getName();
            assertTrue("Basket1".equals(name) || "Basket2".equals(name));
        }

    }

    private void generateSampleDataForQuery()
        throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException {
        WorkbasketImpl basket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket1.setId("1");
        basket1.setKey("k1");
        basket1.setName("Basket1");
        basket1.setOwner("Eberhardt");
        basket1.setType(WorkbasketType.GROUP);
        basket1.setDomain("novatec");
        basket1 = (WorkbasketImpl) workBasketService.createWorkbasket(basket1);
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey(basket1.getKey());
        accessItem.setAccessId("Bernd");
        accessItem.setPermTransfer(true);
        accessItem.setPermCustom1(true);
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketImpl basket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket2.setId("2");
        basket2.setKey("k2");
        basket2.setName("Basket2");
        basket2.setOwner("Konstantin");
        basket2.setType(WorkbasketType.CLEARANCE);
        basket2.setDomain("consulting");
        basket2 = (WorkbasketImpl) workBasketService.createWorkbasket(basket2);
        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketKey(basket2.getKey());
        accessItem2.setAccessId("group2");
        accessItem2.setPermTransfer(true);
        accessItem2.setPermRead(true);
        accessItem2.setPermCustom4(true);
        accessItem2.setPermCustom1(true);
        accessItem2.setPermOpen(true);
        workBasketService.createWorkbasketAuthorization(accessItem2);

        WorkbasketImpl basket3 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket3.setId("3");
        basket3.setKey("k3");
        basket3.setName("Basket3");
        basket3.setOwner("Holger");
        basket3.setType(WorkbasketType.TOPIC);
        basket3.setDomain("develop");
        basket3 = (WorkbasketImpl) workBasketService.createWorkbasket(basket3);
        WorkbasketAccessItem accessItem3 = new WorkbasketAccessItem();
        accessItem3.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem3.setWorkbasketKey(basket3.getKey());
        accessItem3.setAccessId("group3");
        accessItem3.setPermOpen(true);
        accessItem3.setPermRead(true);
        accessItem3.setPermAppend(true);
        workBasketService.createWorkbasketAuthorization(accessItem3);

        WorkbasketImpl basket4 = (WorkbasketImpl) workBasketService.newWorkbasket();
        basket4.setId("4");
        basket4.setKey("k4");
        basket4.setName("Basket4");
        basket4.setOwner("Holger");
        basket4.setType(WorkbasketType.PERSONAL);
        basket4.setDomain("");
        List<WorkbasketSummary> distTargets = new ArrayList<WorkbasketSummary>();
        distTargets.add(basket1.asSummary());
        distTargets.add(basket2.asSummary());
        distTargets.add(basket3.asSummary());
        basket4.setDistributionTargets(distTargets);
        basket4 = (WorkbasketImpl) workBasketService.createWorkbasket(basket4);
        WorkbasketAccessItem accessItem4 = new WorkbasketAccessItem();
        accessItem4.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem4.setWorkbasketKey(basket4.getKey());
        accessItem4.setAccessId("Bernd");
        accessItem4.setPermOpen(true);
        accessItem4.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem4);

        updateModifiedTimestamps(basket1, basket2, basket3, basket4);
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
        Instant now = Instant.now();

        engineProxy.openConnection();
        wb1.setModified(now.minus(TEN_DAYS));
        mapper.update(wb1);
        wb2.setModified(now.minus(FIFTEEN_DAYS));
        mapper.update(wb2);
        wb3.setModified(now.minus(TWENTY_DAYS));
        mapper.update(wb3);
        wb4.setModified(now.minus(TWENTY_DAYS).minus(TEN_DAYS));
        mapper.update(wb4);
        engineProxy.returnConnection();
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

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
