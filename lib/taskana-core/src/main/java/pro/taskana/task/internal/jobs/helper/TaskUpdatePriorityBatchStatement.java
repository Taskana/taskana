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
package pro.taskana.task.internal.jobs.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Update a lot of priorities for tasks in a performant way without the need to write SQL. */
public class TaskUpdatePriorityBatchStatement {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskUpdatePriorityBatchStatement.class);
  private final PreparedStatement preparedStatement;

  public TaskUpdatePriorityBatchStatement(Connection connection) throws SQLException {
    preparedStatement = connection.prepareStatement("update TASK set PRIORITY = ? where ID = ?");
  }

  public void addPriorityUpdate(String taskId, int priority) throws SQLException {
    preparedStatement.setInt(1, priority);
    preparedStatement.setString(2, taskId);
    LOGGER.debug("Job update priority to {} for task {}.", priority, taskId);
    preparedStatement.addBatch();
  }

  public void executeBatch() throws SQLException {
    preparedStatement.executeBatch();
  }
}
