package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.TaskSummary;

@ExtendWith(JaasExtension.class)
class QueryTasksByWildcardSearchAccTest extends AbstractAccTest {
  private static final TaskService TASK_SERVICE = taskanaEngine.getTaskService();

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    WildcardSearchField[] wildcards = {
      WildcardSearchField.CUSTOM_3, WildcardSearchField.CUSTOM_4, WildcardSearchField.NAME
    };

    List<TaskSummary> foundTasks =
        TASK_SERVICE
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%99%")
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(foundTasks).hasSize(4);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CountAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    WildcardSearchField[] wildcards = {
      WildcardSearchField.CUSTOM_3, WildcardSearchField.CUSTOM_4, WildcardSearchField.NAME
    };

    long foundTasks =
        TASK_SERVICE
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%99%")
            .orderByName(SortDirection.ASCENDING)
            .count();

    assertThat(foundTasks).isEqualTo(4);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnAllTasks_For_ProvidedSearchValueAndAdditionalParameters() {
    WildcardSearchField[] wildcards = {
      WildcardSearchField.CUSTOM_3, WildcardSearchField.CUSTOM_4, WildcardSearchField.NAME
    };

    List<TaskSummary> foundTasks =
        TASK_SERVICE
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%99%")
            .ownerIn("user-1-1")
            .businessProcessIdLike("%PI2%")
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(foundTasks).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnAllTasksCaseInsensitive_When_PerformingWildcardQuery() {
    WildcardSearchField[] wildcards = {WildcardSearchField.NAME};

    List<TaskSummary> foundTasksCaseSensitive =
        TASK_SERVICE
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%Wid%")
            .list();

    List<TaskSummary> foundTasksCaseInsensitive =
        TASK_SERVICE
            .createTaskQuery()
            .wildcardSearchFieldsIn(wildcards)
            .wildcardSearchValueLike("%wid%")
            .list();

    assertThat(foundTasksCaseSensitive).hasSize(80);
    assertThat(foundTasksCaseInsensitive).containsExactlyElementsOf(foundTasksCaseSensitive);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowException_When_NotUsingSearchFieldsAndValueParamsTogether() {

    ThrowingCallable queryAttempt =
        () ->
            TASK_SERVICE
                .createTaskQuery()
                .wildcardSearchValueLike("%99%")
                .orderByName(SortDirection.ASCENDING)
                .list();

    assertThatThrownBy(queryAttempt).isInstanceOf(IllegalArgumentException.class);

    queryAttempt =
        () ->
            TASK_SERVICE
                .createTaskQuery()
                .wildcardSearchFieldsIn(
                    WildcardSearchField.CUSTOM_1, WildcardSearchField.DESCRIPTION)
                .orderByName(SortDirection.ASCENDING)
                .list();

    assertThatThrownBy(queryAttempt).isInstanceOf(IllegalArgumentException.class);
  }
}
