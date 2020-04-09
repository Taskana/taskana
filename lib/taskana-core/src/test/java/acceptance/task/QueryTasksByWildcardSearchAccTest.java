package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.WildcardSearchFields;
import pro.taskana.task.api.models.TaskSummary;

@ExtendWith(JaasExtension.class)
public class QueryTasksByWildcardSearchAccTest extends AbstractAccTest {

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    TaskService taskService = taskanaEngine.getTaskService();

    WildcardSearchFields[] wildcards = {
      WildcardSearchFields.CUSTOM_3, WildcardSearchFields.CUSTOM_4, WildcardSearchFields.NAME
    };

    List<TaskSummary> foundTasks =
        taskService
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%99%")
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(foundTasks).hasSize(4);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void should_ReturnAllTasks_When_ProvidingNoSearchFieldsOrValue() {

    TaskService taskService = taskanaEngine.getTaskService();

    WildcardSearchFields[] wildcards = {
      WildcardSearchFields.CUSTOM_3, WildcardSearchFields.CUSTOM_4, WildcardSearchFields.NAME
    };

    List<TaskSummary> foundTasks =
        taskService
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(foundTasks).hasSize(83);

    foundTasks =
        taskService
            .createTaskQuery()
            .wildcardSearchValueLike("%99%")
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(foundTasks).hasSize(83);
  }
}
