package pro.taskana.model.mappings;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import pro.taskana.impl.persistence.MapTypeHandler;
import pro.taskana.model.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * This class is the mybatis mapping of task.
 */
public interface TaskMapper {

    String OBJECTREFERENCEMAPPER_FINDBYID = "pro.taskana.model.mappings.ObjectReferenceMapper.findById";
    String CLASSIFICATION_FINDBYID = "pro.taskana.model.mappings.ClassificationMapper.findByIdAndDomain";

    @Select("SELECT ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_ID, WORKBASKETID, OWNER, PRIMARY_OBJ_REF_ID, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
            + "FROM TASK "
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
            @Result(property = "classification", column = "CLASSIFICATION_ID", javaType = Classification.class, one = @One(select = CLASSIFICATION_FINDBYID)),
            @Result(property = "workbasketId", column = "WORKBASKETID"),
            @Result(property = "owner", column = "OWNER"),
            @Result(property = "primaryObjRef", column = "PRIMARY_OBJ_REF_ID", javaType = ObjectReference.class, one = @One(select = OBJECTREFERENCEMAPPER_FINDBYID)),
            @Result(property = "isRead", column = "IS_READ"),
            @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
            @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.BLOB, javaType = Map.class, typeHandler = MapTypeHandler.class),
            @Result(property = "custom1", column = "CUSTOM_1"),
            @Result(property = "custom2", column = "CUSTOM_2"),
            @Result(property = "custom3", column = "CUSTOM_3"),
            @Result(property = "custom4", column = "CUSTOM_4"),
            @Result(property = "custom5", column = "CUSTOM_5"),
            @Result(property = "custom6", column = "CUSTOM_6"),
            @Result(property = "custom7", column = "CUSTOM_7"),
            @Result(property = "custom8", column = "CUSTOM_8"),
            @Result(property = "custom9", column = "CUSTOM_9"),
            @Result(property = "custom10", column = "CUSTOM_10")
    })
    Task findById(@Param("id") String id);

    @Select("<script>"
            + "SELECT STATE, COUNT (STATE) as counter "
            + "FROM TASK "
            + "WHERE STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>) "
            + "GROUP BY STATE"
            + "</script>")
    @Results({ @Result(column = "STATE", property = "state"), @Result(column = "counter", property = "counter") })
    List<TaskStateCounter> getTaskCountForState(@Param("status") List<TaskState> status);

    @Select("<script>"
            + "SELECT COUNT (*) "
            + "FROM TASK "
            + "WHERE WORKBASKETID = #{workbasketId} "
            + "AND DUE >= #{fromDate} "
            + "AND STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>)"
            + "</script>")
    long getTaskCountForWorkbasketByDaysInPastAndState(@Param("workbasketId") String workbasketId, @Param("fromDate") Date fromDate, @Param("status") List<TaskState> states);

    @Select("<script>"
            + "SELECT CAST(DUE AS DATE) as DUE_DATE, WORKBASKETID, COUNT (*) as counter "
            + "FROM TASK "
            + "WHERE DUE >= #{fromDate} "
            + "AND STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>) "
            + "GROUP BY DUE_DATE, WORKBASKETID"
            + "</script>")
    @Results({ @Result(column = "DUE_DATE", property = "due"),
            @Result(column = "WORKBASKETID", property = "workbasketId"),
            @Result(column = "counter", property = "taskCounter") })
    List<DueWorkbasketCounter> getTaskCountByWorkbasketIdAndDaysInPastAndState(@Param("fromDate") Date fromDate, @Param("status") List<TaskState> states);

    @Insert("INSERT INTO TASK(ID, TENANT_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_ID, WORKBASKETID, OWNER, PRIMARY_OBJ_REF_ID, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10) "
            + "VALUES(#{id}, #{tenantId}, #{created}, #{claimed}, #{completed}, #{modified}, #{planned}, #{due}, #{name}, #{description}, #{priority}, #{state}, #{classification.id}, #{workbasketId}, #{owner}, #{primaryObjRef.id}, #{isRead}, #{isTransferred}, #{customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}, #{custom1}, #{custom2}, #{custom3}, #{custom4}, #{custom5}, #{custom6}, #{custom7}, #{custom8}, #{custom9}, #{custom10})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(Task task);

    @Update("UPDATE TASK SET TENANT_ID = #{tenantId}, CLAIMED = #{claimed}, COMPLETED = #{completed}, MODIFIED = #{modified}, PLANNED = #{planned}, DUE = #{due}, NAME = #{name}, DESCRIPTION = #{description}, PRIORITY = #{priority}, STATE = #{state}, CLASSIFICATION_ID = #{classification.id}, WORKBASKETID = #{workbasketId}, OWNER = #{owner}, PRIMARY_OBJ_REF_ID = #{primaryObjRef.id}, IS_READ = #{isRead}, IS_TRANSFERRED = #{isTransferred}, CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}, CUSTOM_1 = #{custom1}, CUSTOM_2 = #{custom2}, CUSTOM_3 = #{custom3}, CUSTOM_4 = #{custom4}, CUSTOM_5 = #{custom5}, CUSTOM_6 = #{custom6}, CUSTOM_7 = #{custom7}, CUSTOM_8 = #{custom8}, CUSTOM_9 = #{custom9}, CUSTOM_10 = #{custom10} "
            + "WHERE ID = #{id}")
    void update(Task task);

    @Delete("DELETE FROM TASK WHERE ID = #{id}")
    void delete(String id);

}
