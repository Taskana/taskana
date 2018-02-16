package pro.taskana.impl.integration;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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

import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.TimeInterval;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
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
import pro.taskana.impl.WorkbasketAuthorization;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketType;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.mappings.WorkbasketMapper;
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
    static int counter = 0;
    private DataSource dataSource;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private WorkbasketService workBasketService;
    private Instant now;

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
        now = Instant.now();
    }

    @Test
    public void testInsertWorkbasket()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketNotFoundException {
        int before = workBasketService.getWorkbaskets().size();
        WorkbasketImpl workbasket = (WorkbasketImpl) workBasketService.newWorkbasket("key", "novatec");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id1);
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workBasketService.createWorkbasket(workbasket);
        Assert.assertEquals(before + 1, workBasketService.getWorkbaskets().size());
    }

    @Test
    public void testSelectAllWorkbaskets()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketNotFoundException {
        int before = workBasketService.getWorkbaskets().size();
        WorkbasketImpl workbasket0 = (WorkbasketImpl) workBasketService.newWorkbasket("key0", "novatec");
        String id0 = IdGenerator.generateWithPrefix("TWB");
        workbasket0.setId(id0);
        workbasket0.setName("Superbasket");
        workbasket0.setType(WorkbasketType.PERSONAL);
        workBasketService.createWorkbasket(workbasket0);
        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket("key1", "novatec");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket1.setId(id1);
        workbasket1.setName("Megabasket");
        workbasket1.setType(WorkbasketType.GROUP);
        workBasketService.createWorkbasket(workbasket1);
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket("key2", "novatec");
        String id2 = IdGenerator.generateWithPrefix("TWB");
        workbasket2.setId(id2);
        workbasket2.setName("Hyperbasket");
        workbasket2.setType(WorkbasketType.GROUP);
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
        workbasket = workBasketService.createWorkbasket(workbasket);
        List<String> distributionTargets = new ArrayList<>(Arrays.asList(wbDist1.getId(), wbDist2.getId()));
        createWorkbasketWithSecurity(workbasket, "Elena", true, true, false, false);
        workBasketService.setDistributionTargets(workbasket.getId(), distributionTargets);

        Workbasket foundWorkbasket = workBasketService.getWorkbasket(workbasket.getId());
        Assert.assertEquals(id, foundWorkbasket.getId());
        Assert.assertEquals(2, workBasketService.getDistributionTargets(foundWorkbasket.getId()).size());

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
        workbasket2 = workBasketService.createWorkbasket(workbasket2);
        createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);
        List<String> distTargets = new ArrayList<>(Arrays.asList(workbasket0.getId(), workbasket1.getId()));
        Thread.sleep(20L);
        workBasketService.setDistributionTargets(workbasket2.getId(), distTargets);

        String id3 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket3 = createTestWorkbasket(id3, "key3", "novatec", "hm ... irgend ein basket",
            WorkbasketType.GROUP);
        workbasket3 = workBasketService.createWorkbasket(workbasket3);
        createWorkbasketWithSecurity(workbasket3, "Elena", true, true, false, false);

        List<String> newDistTargets = new ArrayList<>(Arrays.asList(workbasket3.getId()));
        Thread.sleep(20L);
        workBasketService.setDistributionTargets(workbasket2.getId(), newDistTargets);

        Workbasket foundBasket = workBasketService.getWorkbasket(workbasket2.getId());

        List<WorkbasketSummary> distributionTargets = workBasketService.getDistributionTargets(foundBasket.getId());

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
        WorkbasketAccessItem accessItem = workBasketService
            .newWorkbasketAccessItem("k100000000000000000000000000000000000000", "Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1,
            workBasketService.getWorkbasketAuthorizations("k100000000000000000000000000000000000000").size());
    }

    @Test
    public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException, InvalidArgumentException {
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(
            "k200000000000000000000000000000000000000",
            "Zaphod Beeblebrox");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        Assert.assertEquals(1,
            workBasketService.getWorkbasketAuthorizations("k200000000000000000000000000000000000000").size());

        accessItem.setPermAppend(true);
        workBasketService.updateWorkbasketAuthorization(accessItem);

        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            Assert.assertEquals("zaphod beeblebrox", accessItem.getAccessId());
        } else {
            Assert.assertEquals("Zaphod Beeblebrox", accessItem.getAccessId());
        }
    }

    @WithAccessId(userName = "Bernd", groupNames = {"group1", "group2", "group3", "group4"})
    @Test
    public void testWorkbasketQuery()
        throws NotAuthorizedException, InvalidArgumentException, InvalidWorkbasketException,
        WorkbasketNotFoundException, InterruptedException {

        generateSampleDataForQuery();

        WorkbasketQuery query1 = workBasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketAuthorization.OPEN, "Bernd")
            .nameIn("Basket4");
        List<WorkbasketSummary> result1 = query1.list();

        Assert.assertEquals(1, result1.size());
        String workbasketId = result1.get(0).getId();
        Workbasket workBasket = workBasketService.getWorkbasket(workbasketId);
        Assert.assertEquals(THREE, workBasketService.getDistributionTargets(workBasket.getId()).size());

        WorkbasketQuery query2 = workBasketService.createWorkbasketQuery().accessIdsHavePermission(
            WorkbasketAuthorization.OPEN, "Bernd",
            "Konstantin");
        List<WorkbasketSummary> result2 = query2.list();
        Assert.assertEquals(2, result2.size());

        WorkbasketQuery query3 = workBasketService.createWorkbasketQuery().accessIdsHavePermission(
            WorkbasketAuthorization.CUSTOM_5,
            "Bernd", "Konstantin");
        List<WorkbasketSummary> result3 = query3.list();
        Assert.assertEquals(0, result3.size());

        WorkbasketQuery query4 = workBasketService.createWorkbasketQuery().accessIdsHavePermission(
            WorkbasketAuthorization.CUSTOM_1,
            "Bernd");
        List<WorkbasketSummary> result4 = query4.list();
        Assert.assertEquals(1, result4.size());

        WorkbasketQuery query0 = workBasketService.createWorkbasketQuery()
            .createdWithin(today())
            .nameIn("Basket1", "Basket2", "Basket3");
        List<WorkbasketSummary> result0 = query0.list();
        assertTrue(result0.size() == THREE);
        for (WorkbasketSummary workbasket : result0) {
            String name = workbasket.getName();
            assertTrue("Basket1".equals(name) || "Basket2".equals(name) || "Basket3".equals(name));
        }

        Thread.sleep(20L);
        WorkbasketQuery query5 = workBasketService.createWorkbasketQuery()
            .modifiedWithin(
                new TimeInterval(Instant.now().minus(Duration.ofDays(31)), Instant.now().minus(Duration.ofDays(14))));
        List<WorkbasketSummary> result5 = query5.list();
        assertTrue(result5.size() == 3);
        for (WorkbasketSummary workbasket : result5) {
            String name = workbasket.getName();
            assertTrue(
                "Basket1".equals(name) || "Basket2".equals(name) || "Basket3".equals(name) || "Basket4".equals(name));
        }

        WorkbasketQuery query6 = workBasketService.createWorkbasketQuery()
            .modifiedWithin(new TimeInterval(now.minus(Duration.ofDays(21L)), null))
            .domainIn("novatec", "consulting")
            .orderByName(SortDirection.ASCENDING);
        List<WorkbasketSummary> result6 = query6.list();
        assertTrue(result6.size() == 2);
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
        WorkbasketImpl basket1 = (WorkbasketImpl) workBasketService.newWorkbasket("k1", "novatec");
        basket1.setId("1000000000000000000000000000000000000000");
        basket1.setName("Basket1");
        basket1.setOwner("Eberhardt");
        basket1.setType(WorkbasketType.GROUP);
        basket1 = (WorkbasketImpl) workBasketService.createWorkbasket(basket1);
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(basket1.getId(), "Bernd");
        accessItem.setPermTransfer(true);
        accessItem.setPermCustom1(true);
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketImpl basket2 = (WorkbasketImpl) workBasketService.newWorkbasket("k2", "consulting");
        basket2.setId("2000000000000000000000000000000000000000");
        basket2.setName("Basket2");
        basket2.setOwner("Konstantin");
        basket2.setType(WorkbasketType.CLEARANCE);
        basket2 = (WorkbasketImpl) workBasketService.createWorkbasket(basket2);
        WorkbasketAccessItem accessItem2 = workBasketService.newWorkbasketAccessItem(basket2.getId(), "group2");
        accessItem2.setPermTransfer(true);
        accessItem2.setPermRead(true);
        accessItem2.setPermCustom4(true);
        accessItem2.setPermCustom1(true);
        accessItem2.setPermOpen(true);
        workBasketService.createWorkbasketAuthorization(accessItem2);

        WorkbasketImpl basket3 = (WorkbasketImpl) workBasketService.newWorkbasket("k3", "develop");
        basket3.setId("3000000000000000000000000000000000000000");
        basket3.setName("Basket3");
        basket3.setOwner("Holger");
        basket3.setType(WorkbasketType.TOPIC);
        basket3 = (WorkbasketImpl) workBasketService.createWorkbasket(basket3);
        WorkbasketAccessItem accessItem3 = workBasketService.newWorkbasketAccessItem(basket3.getId(), "group3");
        accessItem3.setPermOpen(true);
        accessItem3.setPermRead(true);
        accessItem3.setPermAppend(true);
        workBasketService.createWorkbasketAuthorization(accessItem3);

        WorkbasketImpl basket4 = (WorkbasketImpl) workBasketService.newWorkbasket("k4", "");
        basket4.setId("4000000000000000000000000000000000000000");
        basket4.setName("Basket4");
        basket4.setOwner("Holger");
        basket4.setType(WorkbasketType.PERSONAL);
        List<String> distTargets = new ArrayList<>(Arrays.asList(basket1.getId(), basket2.getId(), basket3.getId()));
        basket4 = (WorkbasketImpl) workBasketService.createWorkbasket(basket4);
        WorkbasketAccessItem accessItem4 = workBasketService.newWorkbasketAccessItem(basket4.getId(), "Bernd");
        accessItem4.setPermOpen(true);
        accessItem4.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem4);
        workBasketService.setDistributionTargets(basket4.getId(), distTargets);

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

        engineProxy.openConnection();
        wb1.setModified(now.minus(Duration.ofDays(10L)));
        mapper.update(wb1);
        wb2.setModified(now.minus(Duration.ofDays(15L)));
        mapper.update(wb2);
        wb3.setModified(now.minus(Duration.ofDays(20L)));
        mapper.update(wb3);
        wb4.setModified(now.minus(Duration.ofDays(30L)));
        mapper.update(wb4);
        engineProxy.returnConnection();
    }

    private void createWorkbasketWithSecurity(Workbasket wb, String accessId, boolean permOpen,
        boolean permRead, boolean permAppend, boolean permTransfer) {
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(wb.getId(), accessId);
        accessItem.setPermOpen(permOpen);
        accessItem.setPermRead(permRead);
        accessItem.setPermAppend(permAppend);
        accessItem.setPermTransfer(permTransfer);
        workBasketService.createWorkbasketAuthorization(accessItem);
    }

    private Workbasket createTestWorkbasket(String id, String key, String domain, String name, WorkbasketType type) {
        WorkbasketImpl wb = (WorkbasketImpl) workBasketService.newWorkbasket(key, domain);
        wb.setId(id);
        wb.setName(name);
        wb.setDescription("Description of a Workbasket...");
        wb.setType(type);
        return wb;
    }

    private TimeInterval today() {
        Instant begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
        Instant end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
        return new TimeInterval(begin, end);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
