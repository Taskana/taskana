package pro.taskana.task.internal;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.common.internal.persistence.InstantTypeHandler;
import pro.taskana.common.internal.persistence.MapTypeHandler;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;

/** This class is the mybatis mapping of task. */
@SuppressWarnings("checkstyle:LineLength")
public interface TaskMapper {

  @Select(
      "<script>SELECT ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE, CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CALLBACK_INFO, CALLBACK_STATE, CUSTOM_ATTRIBUTES, "
          + "CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16 "
          + "FROM TASK "
          + "WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Results(
      value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "externalId", column = "EXTERNAL_ID"),
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
        @Result(property = "workbasketSummaryImpl.id", column = "WORKBASKET_ID"),
        @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY"),
        @Result(
            property = "classificationSummaryImpl.category",
            column = "CLASSIFICATION_CATEGORY"),
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
        @Result(
            property = "callbackInfo",
            column = "CALLBACK_INFO",
            javaType = Map.class,
            typeHandler = MapTypeHandler.class),
        @Result(property = "callbackState", column = "CALLBACK_STATE"),
        @Result(
            property = "customAttributes",
            column = "CUSTOM_ATTRIBUTES",
            javaType = Map.class,
            typeHandler = MapTypeHandler.class),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "custom5", column = "CUSTOM_5"),
        @Result(property = "custom6", column = "CUSTOM_6"),
        @Result(property = "custom7", column = "CUSTOM_7"),
        @Result(property = "custom8", column = "CUSTOM_8"),
        @Result(property = "custom9", column = "CUSTOM_9"),
        @Result(property = "custom10", column = "CUSTOM_10"),
        @Result(property = "custom11", column = "CUSTOM_11"),
        @Result(property = "custom12", column = "CUSTOM_12"),
        @Result(property = "custom13", column = "CUSTOM_13"),
        @Result(property = "custom14", column = "CUSTOM_14"),
        @Result(property = "custom15", column = "CUSTOM_15"),
        @Result(property = "custom16", column = "CUSTOM_16")
      })
  TaskImpl findById(@Param("id") String id);

  @Insert(
      "INSERT INTO TASK(ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE,  CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, "
          + "POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CALLBACK_INFO, CALLBACK_STATE, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, "
          + "CUSTOM_9, CUSTOM_10, CUSTOM_11,  CUSTOM_12,  CUSTOM_13,  CUSTOM_14,  CUSTOM_15,  CUSTOM_16 ) "
          + "VALUES(#{id},#{externalId}, #{created}, #{claimed}, #{completed}, #{modified}, #{planned}, #{due}, #{name}, #{creator}, #{description}, #{note}, #{priority}, #{state}, #{classificationSummary.category}, "
          + "#{classificationSummary.key}, #{classificationSummary.id}, #{workbasketSummary.id}, #{workbasketSummary.key}, #{workbasketSummary.domain}, #{businessProcessId}, "
          + "#{parentBusinessProcessId}, #{owner}, #{primaryObjRef.company}, #{primaryObjRef.system}, #{primaryObjRef.systemInstance}, #{primaryObjRef.type}, #{primaryObjRef.value}, "
          + "#{isRead}, #{isTransferred}, #{callbackInfo,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, #{callbackState}, "
          + "#{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, "
          + "#{custom1}, #{custom2}, #{custom3}, #{custom4}, #{custom5}, #{custom6}, #{custom7}, #{custom8}, #{custom9}, #{custom10}, "
          + "#{custom11}, #{custom12}, #{custom13}, #{custom14}, #{custom15},  #{custom16})")
  @Options(keyProperty = "id", keyColumn = "ID")
  void insert(TaskImpl task);

  @Update(
      "UPDATE TASK SET CLAIMED = #{claimed}, COMPLETED = #{completed}, MODIFIED = #{modified}, PLANNED = #{planned}, DUE = #{due}, NAME = #{name}, DESCRIPTION = #{description}, NOTE = #{note}, "
          + " PRIORITY = #{priority}, STATE = #{state}, CLASSIFICATION_CATEGORY = #{classificationSummary.category}, CLASSIFICATION_KEY = #{classificationSummary.key}, CLASSIFICATION_ID = #{classificationSummary.id}, "
          + "WORKBASKET_ID = #{workbasketSummary.id}, WORKBASKET_KEY = #{workbasketSummary.key}, DOMAIN = #{workbasketSummary.domain}, "
          + "BUSINESS_PROCESS_ID = #{businessProcessId}, PARENT_BUSINESS_PROCESS_ID = #{parentBusinessProcessId}, OWNER = #{owner}, POR_COMPANY = #{primaryObjRef.company}, POR_SYSTEM = #{primaryObjRef.system}, "
          + "POR_INSTANCE = #{primaryObjRef.systemInstance}, POR_TYPE = #{primaryObjRef.type}, POR_VALUE = #{primaryObjRef.value}, IS_READ = #{isRead}, IS_TRANSFERRED = #{isTransferred}, "
          + "CALLBACK_INFO = #{callbackInfo,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, "
          + "CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, CUSTOM_1 = #{custom1}, CUSTOM_2 = #{custom2}, "
          + "CUSTOM_3 = #{custom3}, CUSTOM_4 = #{custom4}, CUSTOM_5 = #{custom5}, CUSTOM_6 = #{custom6}, CUSTOM_7 = #{custom7}, CUSTOM_8 = #{custom8}, "
          + "CUSTOM_9 = #{custom9}, CUSTOM_10 = #{custom10}, CUSTOM_11 = #{custom11}, CUSTOM_12 = #{custom12}, CUSTOM_13 = #{custom13}, CUSTOM_14 = #{custom14}, CUSTOM_15 = #{custom15}, CUSTOM_16 = #{custom16} "
          + "WHERE ID = #{id}")
  void update(TaskImpl task);

  @Delete("DELETE FROM TASK WHERE ID = #{id}")
  void delete(String id);

  @Delete(
      "<script>DELETE FROM TASK WHERE ID IN(<foreach item='item' collection='ids' separator=',' >#{item}</foreach>)</script>")
  void deleteMultiple(@Param("ids") List<String> ids);

  @Update(
      "<script>UPDATE TASK SET CALLBACK_STATE = #{state} WHERE EXTERNAL_ID IN(<foreach item='item' collection='externalIds' separator=',' >#{item}</foreach>)</script>")
  void setCallbackStateMultiple(
      @Param("externalIds") List<String> externalIds, @Param("state") CallbackState state);

  @Update(
      "<script>UPDATE TASK SET OWNER = #{owner}, MODIFIED = #{modified} "
          + "WHERE STATE = 'READY' "
          + "AND ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach> "
          + "</script>")
  int setOwnerOfTasks(
      @Param("owner") String owner,
      @Param("taskIds") List<String> taskIds,
      @Param("modified") Instant modified);

  @Update(
      "<script>"
          + " UPDATE TASK SET MODIFIED = #{referencetask.modified}, STATE = #{referencetask.state}, WORKBASKET_KEY = #{referencetask.workbasketSummary.key}, WORKBASKET_ID= #{referencetask.workbasketSummary.id}, "
          + " DOMAIN = #{referencetask.domain}, OWNER = #{referencetask.owner}, IS_READ = #{referencetask.isRead}, IS_TRANSFERRED = #{referencetask.isTransferred}"
          + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
          + "</script>")
  void updateTransfered(
      @Param("taskIds") List<String> taskIds,
      @Param("referencetask") TaskSummaryImpl referencetask);

  @Update(
      "<script>"
          + " UPDATE TASK SET COMPLETED = #{referenceTask.completed}, MODIFIED = #{referenceTask.modified}, STATE = #{referenceTask.state}, OWNER = #{referenceTask.owner}"
          + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
          + "</script>")
  void updateCompleted(
      @Param("taskIds") List<String> taskIds, @Param("referenceTask") TaskSummary referenceTask);

  @Update(
      "<script>"
          + " UPDATE TASK SET CLAIMED = #{referenceTask.claimed}, MODIFIED = #{referenceTask.modified}, STATE = #{referenceTask.state}, OWNER = #{referenceTask.owner}, IS_READ = #{referenceTask.isRead}"
          + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
          + "</script>")
  void updateClaimed(
      @Param("taskIds") List<String> taskIds, @Param("referenceTask") TaskSummary referenceTask);

  @Select(
      "<script>SELECT ID, EXTERNAL_ID, STATE, WORKBASKET_ID, OWNER, MODIFIED, CLASSIFICATION_ID, "
          + "PLANNED, DUE, CALLBACK_STATE FROM TASK "
          + "<where> "
          + "<if test='taskIds != null'>ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</if> "
          + "<if test='externalIds != null'>EXTERNAL_ID IN(<foreach item='item' collection='externalIds' separator=',' >#{item}</foreach>)</if> "
          + "</where> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Results(
      value = {
        @Result(property = "taskId", column = "ID"),
        @Result(property = "externalId", column = "EXTERNAL_ID"),
        @Result(property = "workbasketId", column = "WORKBASKET_ID"),
        @Result(property = "classificationId", column = "CLASSIFICATION_ID"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "taskState", column = "STATE"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "due", column = "DUE"),
        @Result(property = "planned", column = "PLANNED"),
        @Result(property = "callbackState", column = "CALLBACK_STATE")
      })
  List<MinimalTaskSummary> findExistingTasks(
      @Param("taskIds") List<String> taskIds, @Param("externalIds") List<String> externalIds);

  @Update(
      "<script>"
          + " UPDATE TASK SET CLASSIFICATION_CATEGORY = #{newCategory} "
          + " WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach>"
          + "</script>")
  void updateClassificationCategoryOnChange(
      @Param("taskIds") List<String> taskIds, @Param("newCategory") String newCategory);

  @Update(
      "<script>UPDATE TASK SET  "
          + "<if test='fields.custom1'>CUSTOM_1 = #{task.custom1}, </if> "
          + "<if test='fields.custom2'>CUSTOM_2 = #{task.custom2}, </if> "
          + "<if test='fields.custom3'>CUSTOM_3 = #{task.custom3}, </if> "
          + "<if test='fields.custom4'>CUSTOM_4 = #{task.custom4}, </if> "
          + "<if test='fields.custom5'>CUSTOM_5 = #{task.custom5}, </if> "
          + "<if test='fields.custom6'>CUSTOM_6 = #{task.custom6}, </if> "
          + "<if test='fields.custom7'>CUSTOM_7 = #{task.custom7}, </if> "
          + "<if test='fields.custom8'>CUSTOM_8 = #{task.custom8}, </if> "
          + "<if test='fields.custom9'>CUSTOM_9 = #{task.custom9}, </if> "
          + "<if test='fields.custom10'>CUSTOM_10 = #{task.custom10}, </if> "
          + "<if test='fields.custom11'>CUSTOM_11 = #{task.custom11}, </if> "
          + "<if test='fields.custom12'>CUSTOM_12 = #{task.custom12}, </if> "
          + "<if test='fields.custom13'>CUSTOM_13 = #{task.custom13}, </if> "
          + "<if test='fields.custom14'>CUSTOM_14 = #{task.custom14}, </if> "
          + "<if test='fields.custom15'>CUSTOM_15 = #{task.custom15}, </if> "
          + "<if test='fields.custom16'>CUSTOM_16 = #{task.custom16}, </if> "
          + "MODIFIED = #{task.modified} "
          + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
          + "</script>")
  void updateTasks(
      @Param("taskIds") List<String> taskIds,
      @Param("task") TaskImpl task,
      @Param("fields") TaskCustomPropertySelector fields);

  @Update(
      "<script>"
          + "<if test='taskIds != null'> "
          + "UPDATE TASK SET  MODIFIED = #{referenceTask.modified}, "
          + "PLANNED = #{referenceTask.planned}, DUE = #{referenceTask.due} "
          + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
          + "</if> "
          + "</script>")
  long updateTaskDueDates(
      @Param("taskIds") List<String> taskIds, @Param("referenceTask") TaskImpl referenceTask);

  @Update(
      "<script>"
          + "<if test='taskIds != null'> "
          + "UPDATE TASK SET MODIFIED = #{referenceTask.modified}, "
          + "PRIORITY = #{referenceTask.priority} "
          + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
          + "</if> "
          + "</script>")
  long updatePriorityOfTasks(
      @Param("taskIds") List<String> taskIds, @Param("referenceTask") TaskImpl referenceTask);

  @Select(
      "<script>SELECT ID, PLANNED, STATE FROM TASK "
          + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
          + "AND STATE IN ( 'READY','CLAIMED') "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Results(
      value = {
        @Result(property = "left", column = "ID"),
        @Result(
            property = "right",
            column = "PLANNED",
            javaType = Instant.class,
            typeHandler = InstantTypeHandler.class)
      })
  List<Pair<String, Instant>> filterTaskIdsForReadyAndClaimed(
      @Param("taskIds") List<String> taskIds);

  @Select(
      "<script> "
          + "<choose>"
          + "<when  test='accessIds == null'>"
          + "SELECT t.ID FROM TASK t WHERE 1 = 2 "
          + "</when>"
          + "<otherwise>"
          + "SELECT t.ID FROM TASK t WHERE t.ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)"
          + "AND NOT (t.WORKBASKET_ID IN ( "
          + "<choose>"
          + "<when test=\"_databaseId == 'db2'\">"
          + "SELECT WID from (SELECT WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ FROM WORKBASKET_ACCESS_LIST AS s where "
          + "</when>"
          + "<otherwise>"
          + "SELECT WID from (SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ FROM WORKBASKET_ACCESS_LIST AS s where "
          + "</otherwise>"
          + "</choose>"
          + "ACCESS_ID IN (<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>) "
          + "group by WORKBASKET_ID ) AS f where max_read = 1 ))"
          + "</otherwise>"
          + "</choose>"
          + "</script>")
  @Results(value = {@Result(property = "id", column = "ID")})
  List<String> filterTaskIdsNotAuthorizedFor(
      @Param("taskIds") List<String> taskIds, @Param("accessIds") List<String> accessIds);
}
