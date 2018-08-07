package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
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
        groupNames = {"admin"})
    @Test
    public void testQueryTaskValuesForColumnNameOnAttachments() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> columnValueList = taskService.createTaskQuery()
            .attachmentReferenceValueIn("val4")
            .listValues("CHANNEL", null);
        assertNotNull(columnValueList);
        assertEquals(2, columnValueList.size());

        columnValueList = taskService.createTaskQuery()
            .listValues("REF_VALUE", null);
        assertNotNull(columnValueList);
        assertEquals(6, columnValueList.size());

        columnValueList = taskService.createTaskQuery()
            .orderByAttachmentClassificationId(desc)
            .listValues("a.CLASSIFICATION_ID", null);
        assertNotNull(columnValueList);
        assertEquals(11, columnValueList.size());

        columnValueList = taskService.createTaskQuery()
            .orderByClassificationKey(desc)
            .listValues("t.CLASSIFICATION_KEY", null);
        assertNotNull(columnValueList);
        assertEquals(7, columnValueList.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryForOwnerLike() {
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
    public void testQueryForParentBusinessProcessId() {
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
    public void testQueryForName() {
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
    public void testQueryForClassificationKey() {
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

        List<TaskSummary> result3 = taskService.createTaskQuery()
            .classificationKeyNotIn("T2100", "T2000")
            .list();
        assertThat(result3.size(), equalTo(70));

        List<TaskSummary> result4 = taskService.createTaskQuery()
            .classificationKeyNotIn("L1050", "L1060", "T2100")
            .list();
        assertThat(result4.size(), equalTo(6));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForAttachmentInSummary()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException {
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
        assertNotNull(results.get(0).getAttachmentSummaries().get(0));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom1()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("1", "custom%", "p%", "%xyz%", "efg")
            .list();
        assertThat(results.size(), equalTo(3));

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
        assertThat(result2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom2()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("2", "custom%", "a%")
            .list();
        assertThat(results.size(), equalTo(2));

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
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom3()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("3", "ffg")
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
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("4", "%ust%", "%ty")
            .list();
        assertThat(results.size(), equalTo(2));

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
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom5()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("5", "ew", "al")
            .list();
        assertThat(results.size(), equalTo(2));

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
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom6()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("6", "%custom6%", "%vvg%", "11%")
            .list();
        assertThat(results.size(), equalTo(3));

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
        assertThat(result2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidArgumentException.class)
    public void testQueryForCustom7WithExceptionInLike()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("7")
            .list();
        assertThat(results.size(), equalTo(0));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidArgumentException.class)
    public void testQueryForCustom7WithExceptionInIn()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("7", "fsdhfshk%")
            .list();
        assertThat(results.size(), equalTo(0));

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
        assertThat(result2.size(), equalTo(0));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom7WithException()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("7", "%")
            .list();
        assertThat(results.size(), equalTo(2));

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
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom8()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("8", "%")
            .list();
        assertThat(results.size(), equalTo(2));

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
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom9()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("9", "%")
            .list();
        assertThat(results.size(), equalTo(2));

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
        assertThat(result2.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom10()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("10", "%")
            .list();
        assertThat(results.size(), equalTo(3));

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
        assertThat(result2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom11()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("11", "%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("11");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);
        List<TaskSummary> results2 = taskService.createTaskQuery()
            .customAttributeIn("11", ids)
            .list();
        assertThat(results2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom12()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("12", "%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("12");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);
        List<TaskSummary> results2 = taskService.createTaskQuery()
            .customAttributeIn("12", ids)
            .list();
        assertThat(results2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom13()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("13", "%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("13");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);
        List<TaskSummary> results2 = taskService.createTaskQuery()
            .customAttributeIn("13", ids)
            .list();
        assertThat(results2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom14()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("14", "%")
            .list();
        assertThat(results.size(), equalTo(47));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("14");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);
        List<TaskSummary> results2 = taskService.createTaskQuery()
            .customAttributeIn("14", ids)
            .list();
        assertThat(results2.size(), equalTo(47));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom15()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("15", "%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("15");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);
        List<TaskSummary> results2 = taskService.createTaskQuery()
            .customAttributeIn("15", ids)
            .list();
        assertThat(results2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForCustom16()
        throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .customAttributeLike("16", "%")
            .list();
        assertThat(results.size(), equalTo(3));

        String[] ids = results.stream()
            .map(t -> {
                try {
                    return t.getCustomAttribute("16");
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    return "";
                }
            })
            .collect(Collectors.toList())
            .toArray(new String[0]);
        List<TaskSummary> results2 = taskService.createTaskQuery()
            .customAttributeIn("16", ids)
            .list();
        assertThat(results2.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryTaskByCustomAttributes()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

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
        assertEquals(24, numberOfTasks);
        List<TaskSummary> tasks = taskQuery
            .orderByDue(SortDirection.DESCENDING)
            .list();
        assertEquals(24, tasks.size());
        List<TaskSummary> tasksp = taskQuery
            .orderByDue(SortDirection.DESCENDING)
            .listPage(4, 5);
        assertEquals(5, tasksp.size());
        tasksp = taskQuery
            .orderByDue(SortDirection.DESCENDING)
            .listPage(5, 5);
        assertEquals(4, tasksp.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForCreatorIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .creatorIn("creator_user_id2", "creator_user_id3")
            .list();
        assertEquals(4, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForCreatorLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .creatorLike("ersTeLlEr%")
            .list();
        assertEquals(3, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForNoteLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .noteLike("Some%")
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForClassificationCategoryIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .classificationCategoryIn("MANUAL", "AUTOMATIC")
            .list();
        assertEquals(4, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForClassificationCategoryLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .classificationCategoryLike("AUTO%")
            .list();
        assertEquals(1, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForPrimaryObjectReferenceCompanyLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceCompanyLike("My%")
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForPrimaryObjectReferenceSystemLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceSystemLike("My%")
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForPrimaryObjectReferenceSystemInstanceLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceSystemInstanceLike("My%")
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForPrimaryObjectReferenceTypeLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceTypeLike("My%")
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForReadEquals() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .readEquals(true)
            .list();
        assertEquals(25, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForTransferredEquals() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .transferredEquals(true)
            .list();
        assertEquals(2, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForBusinessProcessIdIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .businessProcessIdIn("PI_0000000000003", "BPI21")
            .list();
        assertEquals(8, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForBusinessProcessIdLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .businessProcessIdLike("pI_%")
            .list();
        assertEquals(66, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentClassificationKeyIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentClassificationKeyIn("L110102")
            .list();
        assertEquals(1, results.size());
        assertEquals("TKI:000000000000000000000000000000000002",
            results.get(0).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentClassificationKeyLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentClassificationKeyLike("%10102")
            .list();
        assertEquals(1, results.size());
        assertEquals("TKI:000000000000000000000000000000000002",
            results.get(0).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentclassificationIdIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentClassificationIdIn("CLI:000000000000000000000000000000000002")
            .list();
        assertEquals(1, results.size());
        assertEquals("TKI:000000000000000000000000000000000001",
            results.get(0).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentChannelLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentChannelLike("%6")
            .list();
        assertEquals(2, results.size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentReferenceIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentReferenceValueIn("val4")
            .list();
        assertEquals(6, results.size());
        assertEquals(1, results.get(5).getAttachmentSummaries().size());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentReceivedIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        TimeInterval interval = new TimeInterval(
            getInstant("2018-01-30T12:00:00"),
            getInstant("2018-01-31T12:00:00"));
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentReceivedWithin(interval)
            .orderByWorkbasketId(desc)
            .list();
        assertEquals(2, results.size());
        assertEquals("TKI:000000000000000000000000000000000001", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000011", results.get(1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCreatorDesc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCreator(desc)
            .list();
        assertEquals("user_1_1", results.get(0).getCreator());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByWorkbasketIdDesc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByWorkbasketId(desc)
            .list();
        assertEquals("WBI:100000000000000000000000000000000015",
            results.get(0).getWorkbasketSummary().getId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom1Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("1", asc)
            .list();
        assertEquals("custom1", results.get(0).getCustomAttribute("1"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom2Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("2", desc)
            .list();
        assertEquals("custom2", results.get(0).getCustomAttribute("2"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom3Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("3", asc)
            .list();
        assertEquals("custom3", results.get(0).getCustomAttribute("3"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom4Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("4", desc)
            .list();
        assertEquals("rty", results.get(0).getCustomAttribute("4"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom5Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("5", asc)
            .list();
        assertEquals("al", results.get(0).getCustomAttribute("5"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom6Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("6", desc)
            .list();
        assertEquals("vvg", results.get(0).getCustomAttribute("6"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom7Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("7", asc)
            .list();
        assertEquals("custom7", results.get(0).getCustomAttribute("7"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom8Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("8", desc)
            .list();
        assertEquals("lnp", results.get(0).getCustomAttribute("8"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom9Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("9", asc)
            .list();
        assertEquals("bbq", results.get(0).getCustomAttribute("9"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom10Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("10", desc)
            .list();
        assertEquals("ert", results.get(0).getCustomAttribute("10"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom11Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("11", desc)
            .list();
        assertEquals("ert", results.get(0).getCustomAttribute("11"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom12Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("12", asc)
            .list();
        assertEquals("custom12", results.get(0).getCustomAttribute("12"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom13Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("13", desc)
            .list();
        assertEquals("ert", results.get(0).getCustomAttribute("13"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom14Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("14", asc)
            .list();
        assertEquals("abc", results.get(0).getCustomAttribute("14"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom15Desc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("15", desc)
            .list();
        assertEquals("ert", results.get(0).getCustomAttribute("15"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByCustom16Asc() throws InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByCustomAttribute("16", asc)
            .list();
        assertEquals("custom16", results.get(0).getCustomAttribute("16"));
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderWithDirectionNull() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .orderByPrimaryObjectReferenceSystemInstance(null)
            .list();
        assertEquals("00", results.get(0).getPrimaryObjRef().getSystemInstance());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByAttachmentClassificationIdAsc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationId(asc)
            .list();
        assertEquals("TKI:000000000000000000000000000000000011", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByAttachmentClassificationIdDesc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationId(desc)
            .list();
        assertEquals("TKI:000000000000000000000000000000000010", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000011", results.get(results.size() - 1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByAttachmentClassificationKeyAsc() {

        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationKey(asc)
            .list();

        assertEquals("TKI:000000000000000000000000000000000010", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000012", results.get(results.size() - 1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByAttachmentClassificationKeyDesc() {

        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentClassificationKey(desc)
            .list();

        assertEquals("TKI:000000000000000000000000000000000012", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByAttachmentRefValueDesc() {

        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentReference(desc)
            .list();

        assertEquals("TKI:000000000000000000000000000000000012", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForOrderByAttachmentChannelAscAndReferenceDesc() {

        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .idIn("TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000012")
            .orderByAttachmentChannel(asc)
            .orderByAttachmentReference(desc)
            .list();

        assertEquals("TKI:000000000000000000000000000000000012", results.get(0).getTaskId());
        assertEquals("TKI:000000000000000000000000000000000010", results.get(results.size() - 1).getTaskId());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testQueryForAttachmentLikeCHAndOrderByClassificationKeyDescAndAsc() {

        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .attachmentChannelLike("CH%")
            .orderByClassificationKey(desc)
            .list();

        assertEquals("T2000", results.get(0).getClassificationSummary().getKey());
        assertEquals("L1050", results.get(results.size() - 1).getClassificationSummary().getKey());

        results = taskService.createTaskQuery()
            .attachmentChannelLike("CH%")
            .orderByClassificationKey(asc)
            .list();

        assertEquals("L1050", results.get(0).getClassificationSummary().getKey());
        assertEquals("T2000", results.get(results.size() - 1).getClassificationSummary().getKey());
    }

}
