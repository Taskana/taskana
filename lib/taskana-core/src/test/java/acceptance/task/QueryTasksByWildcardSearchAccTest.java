package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.TaskSummary;

@ExtendWith(JaasExtension.class)
public class QueryTasksByWildcardSearchAccTest extends AbstractAccTest {

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    TaskService taskService = taskanaEngine.getTaskService();

    WildcardSearchField[] wildcards = {
      WildcardSearchField.CUSTOM_3, WildcardSearchField.CUSTOM_4, WildcardSearchField.NAME
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
  void should_ReturnAllTasks_For_ProvidedSearchValueAndAdditionalParameters() {
    TaskService taskService = taskanaEngine.getTaskService();

    WildcardSearchField[] wildcards = {
      WildcardSearchField.CUSTOM_3, WildcardSearchField.CUSTOM_4, WildcardSearchField.NAME
    };

    List<TaskSummary> foundTasks =
        taskService
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%99%")
            .ownerIn("user_1_1")
            .businessProcessIdLike("%PI2%")
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(foundTasks).hasSize(1);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void should_ThrowException_When_NotUsingSearchFieldsAndValueParamsTogether() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable queryAttempt =
        () ->
            taskService
                .createTaskQuery()
                .wildcardSearchValueLike("%99%")
                .orderByName(SortDirection.ASCENDING)
                .list();

    assertThatThrownBy(queryAttempt).isInstanceOf(IllegalArgumentException.class);

    queryAttempt =
        () ->
            taskService
                .createTaskQuery()
                .wildcardSearchFieldsIn(
                    WildcardSearchField.CUSTOM_1, WildcardSearchField.DESCRIPTION)
                .orderByName(SortDirection.ASCENDING)
                .list();

    assertThatThrownBy(queryAttempt).isInstanceOf(IllegalArgumentException.class);
  }
}
