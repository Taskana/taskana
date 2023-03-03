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
package pro.taskana.common.internal.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

public class PlainJavaTransactionProvider implements TaskanaTransactionProvider {

  private final TaskanaEngine taskanaEngine;
  private final DataSource dataSource;
  private final ConnectionManagementMode defaultConnectionManagementMode;

  public PlainJavaTransactionProvider(TaskanaEngine taskanaEngine, DataSource dataSource) {
    this.taskanaEngine = taskanaEngine;
    this.dataSource = dataSource;
    defaultConnectionManagementMode = taskanaEngine.getConnectionManagementMode();
  }

  @Override
  public <T> T executeInTransaction(Supplier<T> supplier) {
    if (((TaskanaEngineImpl) taskanaEngine).getConnection() != null) {
      return supplier.get();
    }
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngine.setConnection(connection);
      final T t = supplier.get();
      connection.commit();
      taskanaEngine.closeConnection();
      return t;
    } catch (SQLException ex) {
      throw new SystemException("caught exception", ex);
    } finally {
      taskanaEngine.setConnectionManagementMode(defaultConnectionManagementMode);
    }
  }
}
