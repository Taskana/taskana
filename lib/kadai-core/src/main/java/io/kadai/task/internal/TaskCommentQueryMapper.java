package io.kadai.task.internal;

import io.kadai.task.internal.models.TaskCommentImpl;
import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;

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
