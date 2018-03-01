package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.KeyDomain;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.TaskanaEngineProxyForTest;
import pro.taskana.mappings.TaskTestMapper;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks with sorting" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryTasksAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryForOwnerLike()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .ownerLike("%a%", "%u%")
            .orderByCreated(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getCreated().isAfter(taskSummary.getCreated()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryForParentBusinessProcessId()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .parentBusinessProcessIdLike("%PBPI%", "doc%3%")
            .list();
        assertThat(results.size(), equalTo(24));

        String[] parentIds = results.stream()
            .map(TaskSummary::getParentBusinessProcessId)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .parentBusinessProcessIdIn(parentIds)
            .list();
        assertThat(result2.size(), equalTo(24));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryForName()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .nameLike("task%")
            .list();
        assertThat(results.size(), equalTo(6));

        String[] ids = results.stream()
            .map(TaskSummary::getName)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .nameIn(ids)
            .list();
        assertThat(result2.size(), equalTo(6));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryForClassificationKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .classificationKeyLike("L10%")
            .list();
        assertThat(results.size(), equalTo(65));

        String[] ids = results.stream()
            .map(t -> t.getClassificationSummary().getKey())
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .classificationKeyIn(ids)
            .list();
        assertThat(result2.size(), equalTo(65));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForWorkbasketKeyDomain()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<KeyDomain> workbasketIdentifiers = Arrays.asList(new KeyDomain("GPK_KSC", "DOMAIN_A"),
            new KeyDomain("USER_1_2", "DOMAIN_A"));

        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(workbasketIdentifiers.toArray(new KeyDomain[0]))
            .list();
        assertThat(results.size(), equalTo(42));

        String[] ids = results.stream()
            .map(t -> t.getWorkbasketSummary().getId())
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .workbasketIdIn(ids)
            .list();
        assertThat(result2.size(), equalTo(42));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom1()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom1Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(2));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom1)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom1In(ids)
            .list();
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom2()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom2Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom2)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom2In(ids)
            .list();
        assertThat(result2.size(), equalTo(1));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom3()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom3Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom3)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom3In(ids)
            .list();
        assertThat(result2.size(), equalTo(1));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom4()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom4Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom4)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom4In(ids)
            .list();
        assertThat(result2.size(), equalTo(1));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom5()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom5Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom5)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom5In(ids)
            .list();
        assertThat(result2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom6()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom6Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(2));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom6)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom6In(ids)
            .list();
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom7()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom7Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom7)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom7In(ids)
            .list();
        assertThat(result2.size(), equalTo(1));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom8()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom8Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom8)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom8In(ids)
            .list();
        assertThat(result2.size(), equalTo(1));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom9()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom9Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom9)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom9In(ids)
            .list();
        assertThat(result2.size(), equalTo(1));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom10()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .custom10Like("%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%", "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(2));

        String[] ids = results.stream()
            .map(TaskSummary::getCustom10)
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .custom10In(ids)
            .list();
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryTaskByCustomAttributes()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setClassificationKey("T2100");
        Map<String, String> customAttributesForCreate = createSimpleCustomProperties(20000); // about 1 Meg
        newTask.setCustomAttributes(customAttributesForCreate);
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        // query the task by custom attributes
        TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest((TaskanaEngineImpl) taskanaEngine);
        try {
            SqlSession session = engineProxy.getSqlSession();
            Configuration config = session.getConfiguration();
            if (!config.hasMapper(TaskTestMapper.class)) {
                config.addMapper(TaskTestMapper.class);
            }

            TaskTestMapper mapper = session.getMapper(TaskTestMapper.class);
            engineProxy.openConnection();
            List<TaskImpl> queryResult = mapper.selectTasksByCustomAttributeLike("%Property Value of Property_1339%");

            assertTrue(queryResult.size() == 1);
            Task retrievedTask = queryResult.get(0);

            assertTrue(createdTask.getId().equals(retrievedTask.getId()));

            // verify that the map is correctly retrieved from the database
            Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributes();
            assertNotNull(customAttributesFromDb);
            assertTrue(customAttributesForCreate.equals(customAttributesFromDb));

        } finally {
            engineProxy.returnConnection();
        }
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
