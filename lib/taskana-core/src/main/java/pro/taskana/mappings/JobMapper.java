package pro.taskana.mappings;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.impl.Job;
import pro.taskana.impl.persistence.MapTypeHandler;

/**
 * This class is the mybatis mapping of the JOB table.
 */
public interface JobMapper {

    @Insert("<script>"
        + "INSERT INTO TASKANA.JOB (JOB_ID, CREATED, STARTED, COMPLETED, STATE, TYPE, RETRY_COUNT, EXECUTOR, ERRORS, ARGUMENTS) "
        + "VALUES ("
        + "<choose>"
        + "<when test=\"_databaseId == 'db2'\">"
        + "TASKANA.JOB_SEQ.NEXTVAL"
        + "</when>"
        + "<otherwise>"
        + "nextval('TASKANA.JOB_SEQ')"
        + "</otherwise>"
        + "</choose>"
        + ", #{job.created}, #{job.started}, #{job.completed}, #{job.state}, #{job.type}, #{job.retryCount}, #{job.executor}, #{job.errors}, #{job.arguments,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} )"
        + "</script>")
    void insertJob(@Param("job") Job job);

    @Select("<script> SELECT   JOB_ID, CREATED, STARTED, COMPLETED, STATE, TYPE, RETRY_COUNT, EXECUTOR, ERRORS, ARGUMENTS "
        + "FROM TASKANA.JOB "
        + "WHERE STATE IN ( 'READY') "
        + "ORDER BY JOB_ID "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results(value = {
        @Result(property = "jobId", column = "JOB_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "started", column = "STARTED"),
        @Result(property = "completed", column = "COMPLETED"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "retryCount", column = "RETRY_COUNT"),
        @Result(property = "executor", column = "EXECUTOR"),
        @Result(property = "errors", column = "ERRORS"),
        @Result(property = "arguments", column = "ARGUMENTS",
            javaType = Map.class, typeHandler = MapTypeHandler.class)
    })
    List<Job> findJobsToRun();

    @Update(
        value = "UPDATE TASKANA.JOB SET CREATED = #{created}, STARTED = #{started}, COMPLETED = #{completed}, STATE = #{state}, "
            + "TYPE = #{type}, RETRY_COUNT = #{retryCount},  EXECUTOR = #{executor}, "
            + "ERRORS = #{errors}, "
            + "ARGUMENTS = #{arguments,jdbcType=CLOB ,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} "
            + "where JOB_ID = #{jobId}")
    void update(Job job);

    @Delete(
        value = "DELETE FROM TASKANA.JOB WHERE JOB_ID = #{jobId}")
    void delete(Job job);
}
