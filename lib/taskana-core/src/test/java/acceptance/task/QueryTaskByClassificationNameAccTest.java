package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.AttachmentSummary;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.TaskQuery;
import pro.taskana.TaskQueryColumnName;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result correctly.
 */
@ExtendWith(JAASExtension.class)
public class QueryTaskByClassificationNameAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryTaskByClassificationNameAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"admin"})
    @Test
    public void testQueryTaskValuesForAttachmentClassificationName() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> columnValueList = taskService.createTaskQuery()
            .ownerLike("%user%")
            .orderByOwner(desc)
            .listValues(TaskQueryColumnName.A_CLASSIFICATION_NAME, null);
        assertNotNull(columnValueList);
        assertEquals(8, columnValueList.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"admin"})
    @Test
    public void testQueryTaskValuesForClassificationName() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> columnValueList = taskService.createTaskQuery()
            .ownerLike("%user%")
            .orderByClassificationName(asc)
            .listValues(TaskQueryColumnName.CLASSIFICATION_NAME, null);
        assertNotNull(columnValueList);
        assertEquals(5, columnValueList.size());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryByClassificationNameIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .classificationNameIn("Dynamik-Ablehnung")
            .list();
        assertEquals(1, tasks.size());

        List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
        assertNotNull(attachmentSummaries);
        assertEquals(2, attachmentSummaries.size());

        tasks = taskService.createTaskQuery()
            .classificationNameIn("Dynamik-Ablehnung")
            .orderByClassificationName(SortDirection.ASCENDING)
            .list();
        assertEquals(1, tasks.size());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryByClassificationNameLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .classificationNameLike("Dynamik%", "Widerruf")
            .orderByClassificationName(SortDirection.ASCENDING)
            .list();
        assertEquals(22, tasks.size());

        // without sort, the same number of tasks should be returned
        tasks = taskService.createTaskQuery()
            .classificationNameLike("Dynamik%", "Widerruf")
            .list();
        assertEquals(22, tasks.size());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testSelectByAttachmentClassificationNameLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        // find Task with attachment classification names
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .attachmentClassificationNameLike("Widerruf", "Beratungsprotokoll", "Dynamik%")
            .orderByAttachmentClassificationName(SortDirection.ASCENDING)
            .list();
        assertEquals(7, tasks.size());
        // make sure that unordered query returns the same number of objects
        tasks = taskService.createTaskQuery()
            .attachmentClassificationNameLike("Widerruf", "Beratungsprotokoll", "Dynamik%")
            .list();
        assertEquals(7, tasks.size());

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testSelectByAttachmentClassificationNameIn() {
        TaskService taskService = taskanaEngine.getTaskService();
        // find Task with attachment classification names
        List<TaskSummary> tasks = taskService.createTaskQuery()
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .orderByAttachmentClassificationName(SortDirection.ASCENDING)
            .list();
        assertEquals(4, tasks.size());
        // make sure that unordered query returns the same number of objects
        tasks = taskService.createTaskQuery()
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .list();
        assertEquals(4, tasks.size());

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryAndCountMatchForClassificationName() {
        TaskService taskService = taskanaEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<TaskSummary> tasks = taskQuery
            .classificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .list();
        long numberOfTasks = taskQuery
            .classificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .count();
        Assert.assertEquals(numberOfTasks, tasks.size());

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryAndCountForAttachmentClassificationName() {
        TaskService taskService = taskanaEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<TaskSummary> tasks = taskQuery
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .list();
        // we expect 4 result objects in this case, because task TKI:000000000000000000000000000000000001  has 2 attachments with different Classifications
        // therefore task TKI:000000000000000000000000000000000001 occurs twice in the result set
        Assert.assertEquals(4, tasks.size());
        long numberOfTasks = taskQuery
            .attachmentClassificationNameIn("Widerruf", "Beratungsprotokoll", "Dynamikänderung")
            .count();
        Assert.assertEquals(3, numberOfTasks);
        // the count returns only the number of tasks that have an attachment with the specified classification name.
        // therefore, task 001 is counted only once.
    }

}

