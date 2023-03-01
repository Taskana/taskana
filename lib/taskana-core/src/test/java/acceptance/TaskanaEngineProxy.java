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
package acceptance;

import java.lang.reflect.Field;
import org.apache.ibatis.session.SqlSession;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;

/** Utility class to enable unit tests to access mappers directly. */
public class TaskanaEngineProxy {

  private final InternalTaskanaEngine engine;

  public TaskanaEngineProxy(TaskanaEngine taskanaEngine) throws Exception {
    Field internal = TaskanaEngineImpl.class.getDeclaredField("internalTaskanaEngineImpl");
    internal.setAccessible(true);
    engine = (InternalTaskanaEngine) internal.get(taskanaEngine);
  }

  public InternalTaskanaEngine getEngine() {
    return engine;
  }

  public SqlSession getSqlSession() {
    return engine.getSqlSession();
  }

  public void openConnection() {
    engine.openConnection();
  }

  public void returnConnection() {
    engine.returnConnection();
  }
}
