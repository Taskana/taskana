package pro.taskana.common.internal;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.internal.persistence.MapTypeHandler;

/** This class is the mybatis mapping of the JOB table. */
@SuppressWarnings("checkstyle:LineLength")
public interface JobMapper {

  @Insert(
      "<script>"
          + "INSERT INTO SCHEDULED_JOB (JOB_ID, PRIORITY, CREATED, DUE, STATE, LOCKED_BY, LOCK_EXPIRES, TYPE, RETRY_COUNT, ARGUMENTS) "
          + "VALUES ("
          + "<choose>"
          + "<when test=\"_databaseId == 'db2'\">"
          + "SCHEDULED_JOB_SEQ.NEXTVAL"
          + "</when>"
          + "<otherwise>"
          + "nextval('SCHEDULED_JOB_SEQ')"
          + "</otherwise>"
          + "</choose>"
          + ", #{job.priority}, #{job.created}, #{job.due}, #{job.state}, #{job.lockedBy}, #{job.lockExpires}, #{job.type}, #{job.retryCount}, #{job.arguments,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler} )"
          + "</script>")
  @Result(property = "jobId", column = "JOB_ID")
  Integer insertJob(@Param("job") ScheduledJob job);

  @Select(
      "<script> SELECT   JOB_ID, PRIORITY, CREATED, DUE, STATE, LOCKED_BY, LOCK_EXPIRES, TYPE, RETRY_COUNT, ARGUMENTS "
          + "FROM SCHEDULED_JOB "
          + "WHERE STATE IN ( 'READY') AND (DUE is null OR DUE &lt; #{now}) AND (LOCK_EXPIRES is null OR LOCK_EXPIRES &lt; CURRENT_TIMESTAMP) AND RETRY_COUNT > 0 "
          + "ORDER BY PRIORITY DESC "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "jobId", column = "JOB_ID")
  @Result(property = "priority", column = "PRIORITY")
  @Result(property = "created", column = "CREATED")
  @Result(property = "due", column = "DUE")
  @Result(property = "state", column = "STATE")
  @Result(property = "lockedBy", column = "LOCKED_BY")
  @Result(property = "lockExpires", column = "LOCK_EXPIRES")
  @Result(property = "type", column = "TYPE")
  @Result(property = "retryCount", column = "RETRY_COUNT")
  @Result(
      property = "arguments",
      column = "ARGUMENTS",
      javaType = Map.class,
      typeHandler = MapTypeHandler.class)
  List<ScheduledJob> findJobsToRun(Instant now);

  @Update(
      value =
          "UPDATE SCHEDULED_JOB SET CREATED = #{created}, PRIORITY = #{priority}, DUE = #{due}, STATE = #{state}, "
              + "LOCKED_BY = #{lockedBy}, LOCK_EXPIRES = #{lockExpires}, TYPE = #{type}, RETRY_COUNT = #{retryCount}, "
              + "ARGUMENTS = #{arguments,jdbcType=CLOB ,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler} "
              + "where JOB_ID = #{jobId}")
  void update(ScheduledJob job);

  @Delete(value = "DELETE FROM SCHEDULED_JOB WHERE JOB_ID = #{jobId}")
  void delete(ScheduledJob job);

  @Delete(value = "DELETE FROM SCHEDULED_JOB WHERE TYPE = #{jobType}")
  void deleteMultiple(Type jobType);
}
