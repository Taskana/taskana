package pro.taskana.impl.integration;

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

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.TimeInterval;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.TaskanaEngineProxyForTest;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.mappings.WorkbasketMapper;
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
    public static void resetDb() {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
    }

    @Before
    public void setup() throws SQLException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        workBasketService = taskanaEngine.getWorkbasketService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
        now = Instant.now();
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketFail()
        throws WorkbasketNotFoundException, NotAuthorizedException {
        workBasketService.getWorkbasket("fail");
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testUpdateWorkbasket() throws Exception {
        String id0 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket0 = createTestWorkbasket(id0, "key0", "DOMAIN_A", "Superbasket", WorkbasketType.GROUP);
        workbasket0 = workBasketService.createWorkbasket(workbasket0);
        createWorkbasketWithSecurity(workbasket0, "Elena", true, true, false, false);

        String id1 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket1 = createTestWorkbasket(id1, "key1", "DOMAIN_A", "Megabasket", WorkbasketType.GROUP);
        workbasket1 = workBasketService.createWorkbasket(workbasket1);
        createWorkbasketWithSecurity(workbasket1, "Elena", true, true, false, false);

        String id2 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket2 = createTestWorkbasket(id2, "key2", "DOMAIN_A", "Hyperbasket", WorkbasketType.GROUP);
        workbasket2 = workBasketService.createWorkbasket(workbasket2);
        createWorkbasketWithSecurity(workbasket2, "Elena", true, true, false, false);
        List<String> distTargets = new ArrayList<>(Arrays.asList(workbasket0.getId(), workbasket1.getId()));
        Thread.sleep(SLEEP_TIME);
        workBasketService.setDistributionTargets(workbasket2.getId(), distTargets);

        String id3 = IdGenerator.generateWithPrefix("TWB");
        Workbasket workbasket3 = createTestWorkbasket(id3, "key3", "DOMAIN_A", "hm ... irgend ein basket",
            WorkbasketType.GROUP);
        workbasket3 = workBasketService.createWorkbasket(workbasket3);
        createWorkbasketWithSecurity(workbasket3, "Elena", true, true, false, false);

        List<String> newDistTargets = new ArrayList<>(Arrays.asList(workbasket3.getId()));
        Thread.sleep(SLEEP_TIME);
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

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testInsertWorkbasketAccessUser() throws NotAuthorizedException, InvalidArgumentException,
        DomainNotFoundException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
        WorkbasketNotFoundException {

        Workbasket wb = createTestWorkbasket("k100000000000000000000000000000000000000", "key1", "DOMAIN_A", "name",
            WorkbasketType.PERSONAL);
        workBasketService.createWorkbasket(wb);
        WorkbasketAccessItem accessItem = workBasketService
            .newWorkbasketAccessItem("k100000000000000000000000000000000000000", "Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAccessItem(accessItem);

        Assert.assertEquals(1,
            workBasketService.getWorkbasketAccessItems("k100000000000000000000000000000000000000").size());
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testUpdateWorkbasketAccessUser()
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException, DomainNotFoundException,
        InvalidWorkbasketException, WorkbasketAlreadyExistException {
        WorkbasketImpl wb = (WorkbasketImpl) workBasketService.newWorkbasket("key", "DOMAIN_A");
        wb.setId("k200000000000000000000000000000000000000");
        wb.setName("name");
        wb.setDescription("Description of a Workbasket...");
        wb.setType(WorkbasketType.GROUP);
        workBasketService.createWorkbasket(wb);

        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(
            "k200000000000000000000000000000000000000",
            "Zaphod Beeblebrox");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAccessItem(accessItem);

        Assert.assertEquals(1,
            workBasketService.getWorkbasketAccessItems("k200000000000000000000000000000000000000").size());

        accessItem.setPermAppend(true);
        workBasketService.updateWorkbasketAccessItem(accessItem);

        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            Assert.assertEquals("zaphod beeblebrox", accessItem.getAccessId());
        } else {
            Assert.assertEquals("Zaphod Beeblebrox", accessItem.getAccessId());
        }
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
        boolean permRead, boolean permAppend, boolean permTransfer)
        throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketAccessItem accessItem = workBasketService.newWorkbasketAccessItem(wb.getId(), accessId);
        accessItem.setPermOpen(permOpen);
        accessItem.setPermRead(permRead);
        accessItem.setPermAppend(permAppend);
        accessItem.setPermTransfer(permTransfer);
        workBasketService.createWorkbasketAccessItem(accessItem);
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

}
