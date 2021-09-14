package pro.taskana.task.internal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.task.internal.models.TaskCommentImpl;

public interface TaskCommentMapper {

  @Insert(
      "INSERT INTO TASK_COMMENT (ID, TASK_ID, TEXT_FIELD, CREATOR, CREATED, MODIFIED) "
          + "VALUES (#{taskComment.id}, #{taskComment.taskId}, #{taskComment.textField},"
          + " #{taskComment.creator}, #{taskComment.created}, #{taskComment.modified})")
  void insert(@Param("taskComment") TaskCommentImpl taskComment);

  @Update(
      "UPDATE TASK_COMMENT SET MODIFIED = #{modified}, TEXT_FIELD = #{textField}  "
          + "WHERE ID = #{id}")
  void update(TaskCommentImpl taskCommentImpl);

  @Delete("DELETE FROM TASK_COMMENT WHERE ID = #{taskCommentId}")
  void delete(String taskCommentId);

  @Select(
      "<script> SELECT ID, TASK_ID, TEXT_FIELD, CREATOR, CREATED, MODIFIED"
          + " FROM TASK_COMMENT "
          + "WHERE ID = #{taskCommentId} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "textField", column = "TEXT_FIELD")
  @Result(property = "creator", column = "CREATOR")
  @Result(property = "created", column = "CREATED")
  @Result(property = "modified", column = "MODIFIED")
  TaskCommentImpl findById(@Param("taskCommentId") String taskCommentId);
}
