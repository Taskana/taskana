package pro.taskana.mappings;

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
import org.apache.ibatis.type.ClobTypeHandler;
import org.apache.ibatis.type.JdbcType;

import pro.taskana.impl.MinimalTaskSummary;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskState;
import pro.taskana.impl.TaskSummaryImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.impl.persistence.MapTypeHandler;

/**
 * This class is the mybatis mapping of task.
 */
public interface TaskMapper {

    String OBJECTREFERENCEMAPPER_FINDBYID = "pro.taskana.mappings.ObjectReferenceMapper.findById";
    String WORKBASKET_FINDSUMMARYBYID = "pro.taskana.mappings.WorkbasketMapper.findSummaryById";
    String CLASSIFICATION_FINDBYID = "pro.taskana.mappings.ClassificationMapper.findById";

    @Select("SELECT ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE, CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
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
        @Result(property = "creator", column = "CREATOR"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "note", column = "NOTE"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "workbasketSummary", column = "WORKBASKET_ID", javaType = WorkbasketSummaryImpl.class,
            one = @One(select = WORKBASKET_FINDSUMMARYBYID)),
        @Result(property = "classificationSummaryImpl.category", column = "CLASSIFICATION_CATEGORY"),
        @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
        @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "primaryObjRef.company", column = "POR_COMPANY"),
        @Result(property = "primaryObjRef.system", column = "POR_SYSTEM"),
        @Result(property = "primaryObjRef.systemInstance", column = "POR_INSTANCE"),
        @Result(property = "primaryObjRef.type", column = "POR_TYPE"),
        @Result(property = "primaryObjRef.value", column = "POR_VALUE"),
        @Result(property = "isRead", column = "IS_READ"),
        @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.CLOB,
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
        @Result(property = "custom10", column = "CUSTOM_10")
    })
    TaskImpl findById(@Param("id") String id);

    @Insert("INSERT INTO TASK(ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE,  CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, "
        + "POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10) "
        + "VALUES(#{id}, #{created}, #{claimed}, #{completed}, #{modified}, #{planned}, #{due}, #{name}, #{creator}, #{description}, #{note}, #{priority}, #{state}, #{classificationSummary.category}, "
        + "#{classificationSummary.key}, #{classificationSummary.id}, #{workbasketSummary.id}, #{workbasketSummary.key}, #{workbasketSummary.domain}, #{businessProcessId}, "
        + "#{parentBusinessProcessId}, #{owner}, #{primaryObjRef.company}, #{primaryObjRef.system}, #{primaryObjRef.systemInstance}, #{primaryObjRef.type}, #{primaryObjRef.value}, "
        + "#{isRead}, #{isTransferred}, #{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}, "
        + "#{custom1}, #{custom2}, #{custom3}, #{custom4}, #{custom5}, #{custom6}, #{custom7}, #{custom8}, #{custom9}, #{custom10})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(TaskImpl task);

    @Update("UPDATE TASK SET CLAIMED = #{claimed}, COMPLETED = #{completed}, MODIFIED = #{modified}, PLANNED = #{planned}, DUE = #{due}, NAME = #{name}, DESCRIPTION = #{description}, NOTE = #{note}, "
        + " PRIORITY = #{priority}, STATE = #{state}, CLASSIFICATION_CATEGORY = #{classificationSummary.category}, CLASSIFICATION_KEY = #{classificationSummary.key}, CLASSIFICATION_ID = #{classificationSummary.id}, "
        + "WORKBASKET_ID = #{workbasketSummary.id}, WORKBASKET_KEY = #{workbasketSummary.key}, DOMAIN = #{workbasketSummary.domain}, "
        + "BUSINESS_PROCESS_ID = #{businessProcessId}, PARENT_BUSINESS_PROCESS_ID = #{parentBusinessProcessId}, OWNER = #{owner}, POR_COMPANY = #{primaryObjRef.company}, POR_SYSTEM = #{primaryObjRef.system}, "
        + "POR_INSTANCE = #{primaryObjRef.systemInstance}, POR_TYPE = #{primaryObjRef.type}, POR_VALUE = #{primaryObjRef.value}, IS_READ = #{isRead}, IS_TRANSFERRED = #{isTransferred}, "
        + "CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}, CUSTOM_1 = #{custom1}, CUSTOM_2 = #{custom2}, "
        + "CUSTOM_3 = #{custom3}, CUSTOM_4 = #{custom4}, CUSTOM_5 = #{custom5}, CUSTOM_6 = #{custom6}, CUSTOM_7 = #{custom7}, CUSTOM_8 = #{custom8}, CUSTOM_9 = #{custom9}, CUSTOM_10 = #{custom10} "
        + "WHERE ID = #{id}")
    void update(TaskImpl task);

    @Delete("DELETE FROM TASK WHERE ID = #{id}")
    void delete(String id);

    @Delete("<script>DELETE FROM TASK WHERE ID IN(<foreach item='item' collection='ids' separator=',' >#{item}</foreach>)</script>")
    void deleteMultiple(@Param("ids") List<String> ids);

    @Select("SELECT ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY = #{workbasketSummaryImpl.key} and DOMAIN = #{workbasketSummaryImpl.domain "
        + "AND STATE = #{taskState}")
    @Results(value = {
        @Result(property = "taskId", column = "ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "claimed", column = "CLAIMED"),
        @Result(property = "completed", column = "COMPLETED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "planned", column = "PLANNED"),
        @Result(property = "due", column = "DUE"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "creator", column = "CREATOR"),
        @Result(property = "note", column = "NOTE"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "classificationSummaryImpl.category", column = "CLASSIFICATION_CATEGORY"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID"),
        @Result(property = "workbasketSummaryImpl.id", column = "WORKBASKET_ID"),
        @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY"),
        @Result(property = "workbasketSummaryImpl.domain", column = "DOMAIN"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
        @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "primaryObjRef.company", column = "POR_COMPANY"),
        @Result(property = "primaryObjRef.system", column = "POR_SYSTEM"),
        @Result(property = "primaryObjRef.systemInstance", column = "POR_INSTANCE"),
        @Result(property = "primaryObjRef.type", column = "POR_TYPE"),
        @Result(property = "primaryObjRef.value", column = "POR_VALUE"),
        @Result(property = "isRead", column = "IS_READ"),
        @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
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
    List<TaskSummaryImpl> findTasksByWorkbasketIdAndState(@Param("workbasketKey") String workbasketKey,
        @Param("taskState") TaskState taskState);

    @Select("SELECT ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, PRIORITY, STATE, CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10 "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY = #{workbasketKey} ")
    @Results(value = {
        @Result(property = "taskId", column = "ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "claimed", column = "CLAIMED"),
        @Result(property = "completed", column = "COMPLETED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "planned", column = "PLANNED"),
        @Result(property = "due", column = "DUE"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "creator", column = "CREATOR"),
        @Result(property = "note", column = "NOTE"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "classificationSummaryImpl.category", column = "CLASSIFICATION_CATEGORY"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
        @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "primaryObjRef.company", column = "POR_COMPANY"),
        @Result(property = "primaryObjRef.system", column = "POR_SYSTEM"),
        @Result(property = "primaryObjRef.systemInstance", column = "POR_INSTANCE"),
        @Result(property = "primaryObjRef.type", column = "POR_TYPE"),
        @Result(property = "primaryObjRef.value", column = "POR_VALUE"),
        @Result(property = "isRead", column = "IS_READ"),
        @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
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
    List<TaskSummaryImpl> findTaskSummariesByWorkbasketKey(@Param("workbasketKey") String workbasketKey);

    @Update("<script>"
        + " UPDATE TASK SET MODIFIED = #{referencetask.modified}, STATE = #{referencetask.state}, WORKBASKET_KEY = #{referencetask.workbasketSummary.key}, WORKBASKET_ID= #{referencetask.workbasketSummary.id}, "
        + " DOMAIN = #{referencetask.domain}, OWNER = #{referencetask.owner}, IS_READ = #{referencetask.isRead}, IS_TRANSFERRED = #{referencetask.isTransferred}"
        + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
        + "</script>")
    void updateTransfered(@Param("taskIds") List<String> taskIds,
        @Param("referencetask") TaskSummaryImpl referencetask);

    @Update("<script>"
        + " UPDATE TASK SET COMPLETED = #{referencetask.completed}, MODIFIED = #{referencetask.modified}, STATE = #{referencetask.state}"
        + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
        + "</script>")
    void updateCompleted(@Param("taskIds") List<String> taskIds,
        @Param("referencetask") TaskSummaryImpl referencetask);

    @Select("<script>SELECT ID, STATE, WORKBASKET_KEY FROM TASK "
        + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
        + "</script>")
    @Results(value = {
        @Result(property = "taskId", column = "ID"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "taskState", column = "STATE")})
    List<MinimalTaskSummary> findExistingTasks(@Param("taskIds") List<String> taskIds);

    @Update("<script>"
        + " UPDATE TASK SET CLASSIFICATION_CATEGORY = #{newCategory} "
        + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
        + "</script>")
    void updateClassificationCategoryOnChange(@Param("taskIds") List<String> taskIds,
        @Param("newCategory") String newCategory);

    @Select("select CUSTOM_ATTRIBUTES from task where id = #{taskId}")
    @Results(value = {
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.CLOB,
            javaType = String.class, typeHandler = ClobTypeHandler.class)
    })
    String getCustomAttributesAsString(@Param("taskId") String taskId);

}
