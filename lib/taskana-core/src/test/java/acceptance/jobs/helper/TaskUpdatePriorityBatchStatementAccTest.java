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
package acceptance.jobs.helper;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.jobs.helper.SqlConnectionRunner;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityBatchStatement;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskUpdatePriorityBatchStatementAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(true);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_updatePriority() throws Exception {
    // given
    SqlConnectionRunner runner = new SqlConnectionRunner(taskanaEngine);
    String taskId = "TKI:000000000000000000000000000000000050";
    final int priorityUpdate = 25;

    // when
    runner.runWithConnection(
        connection -> {
          final TaskUpdatePriorityBatchStatement batchStatement =
              new TaskUpdatePriorityBatchStatement(connection);
          batchStatement.addPriorityUpdate(taskId, priorityUpdate);
          batchStatement.executeBatch();
          if (!connection.getAutoCommit()) {
            connection.commit();
          }
        });

    // then
    final Task actual = taskanaEngine.getTaskService().getTask(taskId);
    assertThat(actual).extracting(Task::getPriority).isEqualTo(priorityUpdate);
  }
}
