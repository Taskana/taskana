package pro.taskana.model.mappings;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

import pro.taskana.Classification;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.persistence.MapTypeHandler;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskSummary;

/**
 * This class is the mybatis mapping of task.
 */
public interface TaskMapper {

    String OBJECTREFERENCEMAPPER_FINDBYID = "pro.taskana.model.mappings.ObjectReferenceMapper.findById";
    String CLASSIFICATION_FINDBYKEYANDDOMAIN = "pro.taskana.model.mappings.ClassificationMapper.findByKeyAndDomain";
    String CLASSIFICATION_FINDBYID = "pro.taskana.model.mappings.ClassificationMapper.findById";

    @Select("SELECT ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_KEY, WORKBASKET_KEY, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
        + "FROM TASK "
        + "WHERE ID = #{id}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
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
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "classification", column = "CLASSIFICATION_KEY", javaType = ClassificationImpl.class,
            one = @One(select = CLASSIFICATION_FINDBYID)),
        @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
        @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "porCompany", column = "POR_COMPANY"),
        @Result(property = "porSystem", column = "POR_SYSTEM"),
        @Result(property = "porSystemInstance", column = "POR_INSTANCE"),
        @Result(property = "porType", column = "POR_TYPE"),
        @Result(property = "porValue", column = "POR_VALUE"),
        @Result(property = "isRead", column = "IS_READ"),
        @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.BLOB,
            javaType = Map.class, typeHandler = MapTypeHandler.class),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "custom5", column = "CUSTOM_5"),
        @Result(property = "custom6", column = "CUSTOM_6"),
        @Result(property = "custom7", column = "CUSTOM_7"),
        @Result(property = "custom8", column = "CUSTOM_8"),
        @Result(property = "custom9", column = "CUSTOM_9"),
        @Result(property = "custom10", column = "CUSTOM_10")})

    TaskImpl findById(@Param("id") String id);

    @Results({@Result(column = "DUE_DATE", property = "due"),
        @Result(column = "WORKBASKET_KEY", property = "workbasketKey"),
        @Result(column = "counter", property = "taskCounter")})
    List<DueWorkbasketCounter> getTaskCountByWorkbasketIdAndDaysInPastAndState(@Param("fromDate") Date fromDate,
        @Param("status") List<TaskState> states);

    @Insert("INSERT INTO TASK(ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_KEY, WORKBASKET_KEY, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10) "
        + "VALUES(#{id}, #{created}, #{claimed}, #{completed}, #{modified}, #{planned}, #{due}, #{name}, #{description}, #{priority}, #{state}, #{classification.key}, #{workbasketKey}, #{businessProcessId}, #{parentBusinessProcessId}, #{owner}, #{primaryObjRef.company},#{primaryObjRef.system},#{primaryObjRef.systemInstance},#{primaryObjRef.type},#{primaryObjRef.value},  #{isRead}, #{isTransferred}, #{customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}, #{custom1}, #{custom2}, #{custom3}, #{custom4}, #{custom5}, #{custom6}, #{custom7}, #{custom8}, #{custom9}, #{custom10})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(TaskImpl task);

    @Update("UPDATE TASK SET CLAIMED = #{claimed}, COMPLETED = #{completed}, MODIFIED = #{modified}, PLANNED = #{planned}, DUE = #{due}, NAME = #{name}, DESCRIPTION = #{description}, PRIORITY = #{priority}, STATE = #{state}, CLASSIFICATION_KEY = #{classification.key}, WORKBASKET_KEY = #{workbasketKey}, BUSINESS_PROCESS_ID = #{businessProcessId}, PARENT_BUSINESS_PROCESS_ID = #{parentBusinessProcessId}, OWNER = #{owner}, POR_COMPANY = #{primaryObjRef.company}, POR_SYSTEM = #{primaryObjRef.system}, POR_INSTANCE = #{primaryObjRef.systemInstance}, POR_TYPE = #{primaryObjRef.type}, POR_VALUE = #{primaryObjRef.value}, IS_READ = #{isRead}, IS_TRANSFERRED = #{isTransferred}, CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}, CUSTOM_1 = #{custom1}, CUSTOM_2 = #{custom2}, CUSTOM_3 = #{custom3}, CUSTOM_4 = #{custom4}, CUSTOM_5 = #{custom5}, CUSTOM_6 = #{custom6}, CUSTOM_7 = #{custom7}, CUSTOM_8 = #{custom8}, CUSTOM_9 = #{custom9}, CUSTOM_10 = #{custom10} "
        + "WHERE ID = #{id}")
    void update(TaskImpl task);

    @Delete("DELETE FROM TASK WHERE ID = #{id}")
    void delete(String id);

    @Select("SELECT ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_KEY, WORKBASKET_KEY, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY = #{workbasketKey} "
        + "AND STATE = #{taskState}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
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
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "classification", column = "CLASSIFICATION_KEY", javaType = Classification.class,
            one = @One(select = CLASSIFICATION_FINDBYKEYANDDOMAIN)),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "porCompany", column = "POR_COMPANY"),
        @Result(property = "porSystem", column = "POR_SYSTEM"),
        @Result(property = "porSystemInstance", column = "POR_INSTANCE"),
        @Result(property = "porType", column = "POR_TYPE"),
        @Result(property = "porValue", column = "POR_VALUE"),
        @Result(property = "isRead", column = "IS_READ"),
        @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.BLOB,
            javaType = Map.class, typeHandler = MapTypeHandler.class),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "custom5", column = "CUSTOM_5"),
        @Result(property = "custom6", column = "CUSTOM_6"),
        @Result(property = "custom7", column = "CUSTOM_7"),
        @Result(property = "custom8", column = "CUSTOM_8"),
        @Result(property = "custom9", column = "CUSTOM_9"),
        @Result(property = "custom10", column = "CUSTOM_10")})

    List<TaskImpl> findTasksByWorkbasketIdAndState(@Param("workbasketKey") String workbasketKey,
        @Param("taskState") TaskState taskState);

    @Select("SELECT TASK.ID AS taskId, TASK.NAME AS taskName, TASK.WORKBASKET_KEY AS workId, TASK.CLASSIFICATION_KEY AS classificationKey, "
        + "WORKBASKET.NAME AS workName, CLASSIFICATION.NAME AS classificationName "
        + "FROM TASK "
        + "LEFT JOIN WORKBASKET ON WORKBASKET.KEY = TASK.WORKBASKET_KEY "
        + "LEFT JOIN CLASSIFICATION ON CLASSIFICATION.KEY = TASK.CLASSIFICATION_KEY "
        + "WHERE TASK.WORKBASKET_KEY = #{workbasketKey}")
    @Results({
        @Result(property = "taskId", column = "taskId"),
        @Result(property = "taskName", column = "taskName"),
        @Result(property = "workbasketKey", column = "workKey"),
        @Result(property = "workbasketName", column = "workName"),
        @Result(property = "classificationKey", column = "classificationKey"),
        @Result(property = "classificationName", column = "classificationName")
    })
    List<TaskSummary> findTaskSummariesByWorkbasketKey(@Param("workbasketKey") String workbasketKey);

}
