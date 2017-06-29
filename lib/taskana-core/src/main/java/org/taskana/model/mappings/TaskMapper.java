package org.taskana.model.mappings;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.taskana.model.DueWorkbasketCounter;
import org.taskana.model.Task;
import org.taskana.model.TaskStateCounter;
import org.taskana.model.TaskState;

public interface TaskMapper {

    @Select("SELECT ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, TYPE, WORKBASKETID, OWNER "
            + "FROM task "
            + "WHERE ID = #{id}")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"), 
            @Result(property = "claimed", column = "CLAIMED"),
            @Result(property = "completed", column = "COMPLETED"), 
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "planned", column = "PLANNED"),
            @Result(property = "due", column = "DUE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "priority", column = "PRIORITY"),
            @Result(property = "state", column = "STATE"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "workbasketId", column = "WORKBASKETID"),
            @Result(property = "owner", column = "OWNER")})
	Task findById(String id);

    @Select("SELECT ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, TYPE, WORKBASKETID, OWNER "
            + "FROM task "
            + "WHERE WORKBASKETID = #{workbasketId} "
            + "ORDER BY ID")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"), 
            @Result(property = "claimed", column = "CLAIMED"),
            @Result(property = "completed", column = "COMPLETED"), 
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "planned", column = "PLANNED"),
            @Result(property = "due", column = "DUE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "priority", column = "PRIORITY"),
            @Result(property = "state", column = "STATE"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "workbasketId", column = "WORKBASKETID"),
            @Result(property = "owner", column = "OWNER")})
	List<Task> findByWorkBasketId(String workbasketId);

    @Select("<script>"
            + "SELECT ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, TYPE, WORKBASKETID, OWNER "
            + "FROM task "
            + "WHERE WORKBASKETID IN (<foreach item='item' collection='workbasketIds' separator=','>#{item}</foreach>) "
            + "AND STATE IN (<foreach item='item' collection='states' separator=',' >#{item}</foreach>) "
            + "ORDER BY ID"
            + "</script>")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"), 
            @Result(property = "claimed", column = "CLAIMED"),
            @Result(property = "completed", column = "COMPLETED"), 
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "planned", column = "PLANNED"),
            @Result(property = "due", column = "DUE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "priority", column = "PRIORITY"),
            @Result(property = "state", column = "STATE"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "workbasketId", column = "WORKBASKETID"),
            @Result(property = "owner", column = "OWNER")})
	List<Task> findByWorkbasketIdsAndStates(@Param("workbasketIds") List<String> workbasketIds, @Param("states") List<TaskState> states);
	
    @Select("<script>"
            + "SELECT ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, TYPE, WORKBASKETID, OWNER "
            + "FROM task "
            + "WHERE STATE IN (<foreach item='item' collection='states' separator=',' >#{item}</foreach>) "
            + "ORDER BY ID"
            + "</script>")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"), 
            @Result(property = "claimed", column = "CLAIMED"),
            @Result(property = "completed", column = "COMPLETED"), 
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "planned", column = "PLANNED"),
            @Result(property = "due", column = "DUE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "priority", column = "PRIORITY"),
            @Result(property = "state", column = "STATE"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "workbasketId", column = "WORKBASKETID"),
            @Result(property = "owner", column = "OWNER")})
	List<Task> findByStates(@Param("states") List<TaskState> states);
    
	@Select("SELECT * FROM TASK")
	List<Task> findAll();

	@Select("<script>"
	        + "SELECT STATE, COUNT (STATE) as counter "
	        + "FROM TASK "
	        + "WHERE STATE IN (<foreach collection='status' item='state' separator=','>#{item}</foreach>) "
	        + "GROUP BY STATE"
	        + "</script>")
	@Results({
		@Result(column="STATE", property="state"),
		@Result(column="counter", property="counter")
	})
	List<TaskStateCounter> getTaskCountForState(@Param("status") List<TaskState> status);

	@Select("<script>"
	        + "SELECT COUNT (*) "
	        + "FROM TASK "
	        + "WHERE WORKBASKETID = #{workbasketId} "
	        + "AND DUE >= #{fromDate} "
	        + "AND STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>)"
	        + "</script>")
	long getTaskCountForWorkbasketByDaysInPastAndState(@Param("workbasketId")String workbasketId, @Param("fromDate") Date fromDate, @Param("status") List<TaskState> states);
	
	@Select("<script>"
	        + "SELECT CAST(DUE AS DATE) as DUE_DATE, WORKBASKETID, COUNT (*) as counter "
	        + "FROM TASK "
	        + "WHERE DUE >= #{fromDate} "
	        + "AND STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>) "
	        + "GROUP BY DUE_DATE, WORKBASKETID"
	        + "</script>")
	@Results({
		@Result(column="DUE_DATE", property="due"),
		@Result(column="WORKBASKETID", property="workbasketId"),
		@Result(column="counter", property="taskCounter")
	})
	List<DueWorkbasketCounter> getTaskCountByWorkbasketIdAndDaysInPastAndState(@Param("fromDate") Date fromDate, @Param("status") List<TaskState> states);
	
	@Insert("INSERT INTO TASK(ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, TYPE, WORKBASKETID, OWNER) VALUES(#{id}, #{tenantId}, #{created}, #{claimed}, #{completed}, #{modified}, #{planned}, #{due}, #{name}, #{description}, #{priority}, #{state}, #{type}, #{workbasketId}, #{owner})")
	@Options(keyProperty = "id", keyColumn="ID")
	void insert(Task task);

	@Update("UPDATE TASK SET TENANT_ID = #{tenantId}, CLAIMED = #{claimed}, COMPLETED = #{completed}, MODIFIED = #{modified}, PLANNED = #{planned}, DUE = #{due}, NAME = #{name}, DESCRIPTION = #{description}, PRIORITY = #{priority}, STATE = #{state}, TYPE = #{type}, WORKBASKETID = #{workbasketId}, OWNER = #{owner} WHERE ID = #{id}")
	void update(Task task);

	@Delete("DELETE FROM TASK WHERE ID = #{id}")
	void delete(String id);

}
