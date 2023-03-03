/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.TaskSummary;

@ExtendWith(JaasExtension.class)
class QueryTasksByWildcardSearchAccTest extends AbstractAccTest {

  @Nested
  class WildcardSearchTest {

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
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

    @WithAccessId(user = "admin")
    @Test
    void should_CountAllTasksByWildcardSearch_For_ProvidedSearchValue() {
      WildcardSearchField[] wildcards = {
        WildcardSearchField.CUSTOM_3, WildcardSearchField.CUSTOM_4, WildcardSearchField.NAME
      };

      long foundTasks =
          taskService
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
          taskService
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
          taskService
              .createTaskQuery()
              .wildcardSearchFieldsIn(wildcards)
              .wildcardSearchValueLike("%Wid%")
              .list();

      List<TaskSummary> foundTasksCaseInsensitive =
          taskService
              .createTaskQuery()
              .wildcardSearchFieldsIn(wildcards)
              .wildcardSearchValueLike("%wid%")
              .list();

      assertThat(foundTasksCaseSensitive).hasSize(90);
      assertThat(foundTasksCaseInsensitive).containsExactlyElementsOf(foundTasksCaseSensitive);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ThrowException_When_NotUsingSearchFieldsAndValueParamsTogether() {

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
}
