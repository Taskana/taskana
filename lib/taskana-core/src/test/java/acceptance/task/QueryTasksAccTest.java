package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.KeyDomain;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
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
        groupNames = {"admin"})
    @Test
    public void testQueryTaskValuesForColumnName() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> columnValueList = taskService.createTaskQuery()
            .ownerLike("%user%")
            .orderByOwner(desc)
            .listValues("OWNER", null);
        assertNotNull(columnValueList);
        assertEquals(3, columnValueList.size());

        columnValueList = taskService.createTaskQuery()
            .listValues("STATE", null);
        assertNotNull(columnValueList);
        assertEquals(3, columnValueList.size());
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
        assertThat(results.size(), equalTo(66));

        String[] ids = results.stream()
            .map(t -> t.getClassificationSummary().getKey())
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .classificationKeyIn(ids)
            .list();
        assertThat(result2.size(), equalTo(66));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForAttachmentInSummary()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        TaskNotFoundException, WorkbasketNotFoundException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        TaskService taskService = taskanaEngine.getTaskService();

        Attachment attachment = createAttachment("DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3));

        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.addAttachment(attachment);
        taskService.updateTask(task);

        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000000")
            .list();
        assertThat(results.size(), equalTo(1));
        assertThat(results.get(0).getAttachmentSummaries().size(), equalTo(3));
        AttachmentSummary att = results.get(0).getAttachmentSummaries().get(0);
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
            .customAttributeLike("1", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(2));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("1");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("1", ids)
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
            .customAttributeLike("2", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("2");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("2", ids)
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
            .customAttributeLike("3", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("3");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("3", ids)
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
            .customAttributeLike("4", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("4");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("4", ids)
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
            .customAttributeLike("5", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("5");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("5", ids)
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
            .customAttributeLike("6", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(2));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("6");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("6", ids)
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
            .customAttributeLike("7", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("7");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("7", ids)
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
            .customAttributeLike("8", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("8");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("8", ids)
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
            .customAttributeLike("9", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(1));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("9");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("9", ids)
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
            .customAttributeLike("10", "%a%", "%b%", "%c%", "%d%", "%e%", "%f%", "%g%", "%h%", "%i%", "%j%", "%k%",
                "%l%", "%m%",
                "%n%", "%o%", "%p%",
                "%q%", "%r%", "%s%", "%w%")
            .list();
        assertThat(results.size(), equalTo(2));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("10");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .customAttributeIn("10", ids)
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

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryAndCountMatch() {
        TaskService taskService = taskanaEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<TaskSummary> tasks = taskQuery
            .nameIn("Task99", "Task01", "Widerruf")
            .list();
        long numberOfTasks = taskQuery
            .nameIn("Task99", "Task01", "Widerruf")
            .count();
        Assert.assertEquals(numberOfTasks, tasks.size());

    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllPaged() {
        TaskService taskService = taskanaEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        long numberOfTasks = taskQuery.count();
        Assert.assertEquals(24, numberOfTasks);
        List<TaskSummary> tasks = taskQuery
            .orderByDue(SortDirection.DESCENDING)
            .list();
        List<TaskSummary> tasksp = taskQuery
            .orderByDue(SortDirection.DESCENDING)
            .listPage(4, 5);
        Assert.assertEquals(5, tasksp.size());
        tasksp = taskQuery
            .orderByDue(SortDirection.DESCENDING)
            .listPage(5, 5);
        Assert.assertEquals(4, tasksp.size());
    }

}
