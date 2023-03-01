/*-
 * #%L
 * pro.taskana:taskana-spring
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
package pro.taskana.common.internal;

import java.sql.SQLException;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import pro.taskana.TaskanaConfiguration;

/** This class configures the TaskanaEngine for spring. */
public class SpringTaskanaEngineImpl extends TaskanaEngineImpl implements SpringTaskanaEngine {

  public SpringTaskanaEngineImpl(
      TaskanaConfiguration taskanaConfiguration, ConnectionManagementMode mode)
      throws SQLException {
    super(taskanaConfiguration, mode);
    this.transactionFactory = new SpringManagedTransactionFactory();
    this.sessionManager = createSqlSessionManager();
  }

  public static SpringTaskanaEngine createTaskanaEngine(
      TaskanaConfiguration taskanaConfiguration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return new SpringTaskanaEngineImpl(taskanaConfiguration, connectionManagementMode);
  }
}
