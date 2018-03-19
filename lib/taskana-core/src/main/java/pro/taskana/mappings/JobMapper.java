package pro.taskana.mappings;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

import pro.taskana.impl.Job;
import pro.taskana.impl.persistence.MapTypeHandler;

/**
 * This class is the mybatis mapping of the JOB table.
 */
public interface JobMapper {

    @Insert("INSERT INTO JOB (JOB_ID, CREATED, STARTED, COMPLETED, STATE, EXECUTOR, ARGUMENTS) "
        + "VALUES (NEXT VALUE FOR JOB_SEQ, #{job.created}, #{job.started}, #{job.completed}, #{job.state}, #{job.executor}, #{job.arguments,jdbcType=CLOB, javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} )")
    void insertJob(@Param("job") Job job);

    @Select("SELECT   JOB_ID, CREATED, STARTED, COMPLETED, STATE, EXECUTOR, ARGUMENTS "
        + "FROM JOB "
        + "WHERE STATE IN ( 'READY') "
        + "ORDER BY JOB_ID ")
    @Results(value = {
        @Result(property = "jobId", column = "JOB_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "started", column = "STARTED"),
        @Result(property = "completed", column = "COMPLETED"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "executor", column = "EXECUTOR"),
        @Result(property = "arguments", column = "ARGUMENTS", jdbcType = JdbcType.CLOB,
            javaType = Map.class, typeHandler = MapTypeHandler.class)
    })
    List<Job> findJobsToRun();

    @Update(
        value = "UPDATE JOB SET CREATED = #{created}, STARTED = #{started}, COMPLETED = #{completed}, STATE = #{state}, EXECUTOR = #{executor}, "
            + "ARGUMENTS = #{arguments,jdbcType=CLOB ,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} "
            + "where JOB_ID = #{jobId}")
    void update(Job job);
}
