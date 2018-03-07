package acceptance.security;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for task queries and authorization.
 */
@RunWith(JAASRunner.class)
public class TaskQueryAccTest extends AbstractAccTest {

    public TaskQueryAccTest() {
        super();
    }

    public void testTaskQueryUnauthenticated() {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .ownerLike("%a%", "%u%")
            .list();

        assertThat(results.size(), equalTo(0));

    }

    @WithAccessId(
        userName = "user_1_1") // , groupNames = {"businessadmin"})
    @Test
    public void testTaskQueryUser_1_1() {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .ownerLike("%a%", "%u%")
            .list();

        assertThat(results.size(), equalTo(3));

    }

    @WithAccessId(
        userName = "user_1_1", groupNames = {"businessadmin"})
    @Test
    public void testTaskQueryUser_1_1BusinessAdm() {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .ownerLike("%a%", "%u%")
            .list();

        assertThat(results.size(), equalTo(3));

    }

    @WithAccessId(
        userName = "user_1_1", groupNames = {"admin"})
    @Test
    public void testTaskQueryUser_1_1Admin() {
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> results = taskService.createTaskQuery()
            .ownerLike("%a%", "%u%")
            .list();

        assertThat(results.size(), equalTo(25));

    }

}
