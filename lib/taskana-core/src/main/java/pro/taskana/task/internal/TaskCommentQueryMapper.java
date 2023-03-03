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
package pro.taskana.task.internal;

import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;

import pro.taskana.task.internal.models.TaskCommentImpl;

/** This class provides a mapper for all task comment queries. */
public interface TaskCommentQueryMapper {

  @SelectProvider(type = TaskCommentQuerySqlProvider.class, method = "queryTaskComments")
  @Result(property = "id", column = "ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "textField", column = "TEXT_FIELD")
  @Result(property = "creator", column = "CREATOR")
  @Result(property = "creatorFullName", column = "FULL_NAME")
  @Result(property = "created", column = "CREATED")
  @Result(property = "modified", column = "MODIFIED")
  List<TaskCommentImpl> queryTaskComments(
      TaskCommentQueryImpl taskCommentQuery, RowBounds rowBounds);

  @SelectProvider(type = TaskCommentQuerySqlProvider.class, method = "countQueryTaskComments")
  Long countQueryTaskComments(TaskCommentQueryImpl taskCommentQuery);

  @SelectProvider(type = TaskCommentQuerySqlProvider.class, method = "queryTaskCommentColumnValues")
  List<String> queryTaskCommentColumnValues(TaskCommentQueryImpl taskCommentQuery);
}
