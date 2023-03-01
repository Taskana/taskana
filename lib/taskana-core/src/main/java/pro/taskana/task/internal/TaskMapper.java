/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.task.internal;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.common.internal.persistence.InstantTypeHandler;
import pro.taskana.common.internal.persistence.MapTypeHandler;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.MinimalTaskSummary;
import pro.taskana.task.internal.models.TaskImpl;

/** This class is the mybatis mapping of task. */
@SuppressWarnings("checkstyle:LineLength")
public interface TaskMapper {

  @Select(
      "<script>SELECT ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, RECEIVED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, MANUAL_PRIORITY, STATE, CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CALLBACK_INFO, CALLBACK_STATE, CUSTOM_ATTRIBUTES, "
          + "CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16, "
          + "CUSTOM_INT_1, CUSTOM_INT_2, CUSTOM_INT_3, CUSTOM_INT_4, CUSTOM_INT_5, CUSTOM_INT_6, CUSTOM_INT_7, CUSTOM_INT_8 "
          + "FROM TASK "
          + "WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "externalId", column = "EXTERNAL_ID")
  @Result(property = "created", column = "CREATED")
  @Result(property = "claimed", column = "CLAIMED")
  @Result(property = "completed", column = "COMPLETED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "planned", column = "PLANNED")
  @Result(property = "received", column = "RECEIVED")
  @Result(property = "due", column = "DUE")
  @Result(property = "name", column = "NAME")
  @Result(property = "creator", column = "CREATOR")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "note", column = "NOTE")
  @Result(property = "priority", column = "PRIORITY")
  @Result(property = "manualPriority", column = "MANUAL_PRIORITY")
  @Result(property = "state", column = "STATE")
  @Result(property = "workbasketSummaryImpl.id", column = "WORKBASKET_ID")
  @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY")
  @Result(property = "classificationSummaryImpl.category", column = "CLASSIFICATION_CATEGORY")
  @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID")
  @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID")
  @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "primaryObjRefImpl.company", column = "POR_COMPANY")
  @Result(property = "primaryObjRefImpl.system", column = "POR_SYSTEM")
  @Result(property = "primaryObjRefImpl.systemInstance", column = "POR_INSTANCE")
  @Result(property = "primaryObjRefImpl.type", column = "POR_TYPE")
  @Result(property = "primaryObjRefImpl.value", column = "POR_VALUE")
  @Result(property = "isRead", column = "IS_READ")
  @Result(property = "isTransferred", column = "IS_TRANSFERRED")
  @Result(
      property = "callbackInfo",
      column = "CALLBACK_INFO",
      javaType = Map.class,
      typeHandler = MapTypeHandler.class)
  @Result(property = "callbackState", column = "CALLBACK_STATE")
  @Result(
      property = "customAttributes",
      column = "CUSTOM_ATTRIBUTES",
      javaType = Map.class,
      typeHandler = MapTypeHandler.class)
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  @Result(property = "custom9", column = "CUSTOM_9")
  @Result(property = "custom10", column = "CUSTOM_10")
  @Result(property = "custom11", column = "CUSTOM_11")
  @Result(property = "custom12", column = "CUSTOM_12")
  @Result(property = "custom13", column = "CUSTOM_13")
  @Result(property = "custom14", column = "CUSTOM_14")
  @Result(property = "custom15", column = "CUSTOM_15")
  @Result(property = "custom16", column = "CUSTOM_16")
  @Result(property = "customInt1", column = "CUSTOM_INT_1")
  @Result(property = "customInt2", column = "CUSTOM_INT_2")
  @Result(property = "customInt3", column = "CUSTOM_INT_3")
  @Result(property = "customInt4", column = "CUSTOM_INT_4")
  @Result(property = "customInt5", column = "CUSTOM_INT_5")
  @Result(property = "customInt6", column = "CUSTOM_INT_6")
  @Result(property = "customInt7", column = "CUSTOM_INT_7")
  @Result(property = "customInt8", column = "CUSTOM_INT_8")
  TaskImpl findById(@Param("id") String id);

  @Insert(
      "INSERT INTO TASK(ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, RECEIVED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, MANUAL_PRIORITY, STATE,  CLASSIFICATION_CATEGORY, CLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, "
          + "POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CALLBACK_INFO, CALLBACK_STATE, CUSTOM_ATTRIBUTES, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, "
          + "CUSTOM_9, CUSTOM_10, CUSTOM_11,  CUSTOM_12,  CUSTOM_13,  CUSTOM_14,  CUSTOM_15,  CUSTOM_16, CUSTOM_INT_1, CUSTOM_INT_2, CUSTOM_INT_3, CUSTOM_INT_4, CUSTOM_INT_5, CUSTOM_INT_6, CUSTOM_INT_7, CUSTOM_INT_8 ) "
          + "VALUES(#{id},#{externalId}, #{created}, #{claimed}, #{completed}, #{modified}, #{planned}, #{received}, #{due}, #{name}, #{creator}, #{description}, #{note}, #{priority}, #{manualPriority}, #{state}, #{classificationSummary.category}, "
          + "#{classificationSummary.key}, #{classificationSummary.id}, #{workbasketSummary.id}, #{workbasketSummary.key}, #{workbasketSummary.domain}, #{businessProcessId}, "
          + "#{parentBusinessProcessId}, #{owner}, #{primaryObjRef.company}, #{primaryObjRef.system}, #{primaryObjRef.systemInstance}, #{primaryObjRef.type}, #{primaryObjRef.value}, "
          + "#{isRead}, #{isTransferred}, #{callbackInfo,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, #{callbackState}, "
          + "#{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, "
          + "#{custom1}, #{custom2}, #{custom3}, #{custom4}, #{custom5}, #{custom6}, #{custom7}, #{custom8}, #{custom9}, #{custom10}, "
          + "#{custom11}, #{custom12}, #{custom13}, #{custom14}, #{custom15},  #{custom16}, #{customInt1}, #{customInt2}, #{customInt3}, #{customInt4}, #{customInt5}, #{customInt6}, #{customInt7}, #{customInt8})")
  @Options(keyProperty = "id", keyColumn = "ID")
  void insert(TaskImpl task);

  @Update(
      "UPDATE TASK SET CLAIMED = #{claimed}, COMPLETED = #{completed}, MODIFIED = #{modified}, PLANNED = #{planned}, RECEIVED = #{received}, DUE = #{due}, NAME = #{name}, DESCRIPTION = #{description}, NOTE = #{note}, "
          + " PRIORITY = #{priority}, MANUAL_PRIORITY = #{manualPriority}, STATE = #{state}, CLASSIFICATION_CATEGORY = #{classificationSummary.category}, CLASSIFICATION_KEY = #{classificationSummary.key}, CLASSIFICATION_ID = #{classificationSummary.id}, "
          + "WORKBASKET_ID = #{workbasketSummary.id}, WORKBASKET_KEY = #{workbasketSummary.key}, DOMAIN = #{workbasketSummary.domain}, "
          + "BUSINESS_PROCESS_ID = #{businessProcessId}, PARENT_BUSINESS_PROCESS_ID = #{parentBusinessProcessId}, OWNER = #{owner}, POR_COMPANY = #{primaryObjRef.company}, POR_SYSTEM = #{primaryObjRef.system}, "
          + "POR_INSTANCE = #{primaryObjRef.systemInstance}, POR_TYPE = #{primaryObjRef.type}, POR_VALUE = #{primaryObjRef.value}, IS_READ = #{isRead}, IS_TRANSFERRED = #{isTransferred}, "
          + "CALLBACK_INFO = #{callbackInfo,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, "
          + "CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}, CUSTOM_1 = #{custom1}, CUSTOM_2 = #{custom2}, "
          + "CUSTOM_3 = #{custom3}, CUSTOM_4 = #{custom4}, CUSTOM_5 = #{custom5}, CUSTOM_6 = #{custom6}, CUSTOM_7 = #{custom7}, CUSTOM_8 = #{custom8}, "
          + "CUSTOM_9 = #{custom9}, CUSTOM_10 = #{custom10}, CUSTOM_11 = #{custom11}, CUSTOM_12 = #{custom12}, CUSTOM_13 = #{custom13}, CUSTOM_14 = #{custom14}, CUSTOM_15 = #{custom15}, CUSTOM_16 = #{custom16}, "
          + "CUSTOM_INT_1 = #{customInt1}, CUSTOM_INT_2 = #{customInt2}, CUSTOM_INT_3 = #{customInt3}, CUSTOM_INT_4 = #{customInt4}, CUSTOM_INT_5 = #{customInt5}, CUSTOM_INT_6 = #{customInt6}, CUSTOM_INT_7 = #{customInt7}, CUSTOM_INT_8 = #{customInt8} "
          + "WHERE ID = #{id}")
  void update(TaskImpl task);

  @Update(
      "UPDATE TASK SET MODIFIED = #{modified}, STATE = #{state}, OWNER = #{owner} WHERE ID = #{id}")
  void requestReview(TaskImpl task);

  @Update(
      "UPDATE TASK SET MODIFIED = #{modified}, STATE = #{state}, OWNER = #{owner} WHERE ID = #{id}")
  void requestChanges(TaskImpl task);

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
          + "WHERE ID IN <foreach item='taskId' index='index' separator=',' open='(' close=')' collection='taskIds'>#{taskId}</foreach> "
          + "</script>")
  void setOwnerOfTasks(
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
      @Param("taskIds") Set<String> taskIds, @Param("referencetask") TaskImpl referencetask);

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
          + "PLANNED, DUE, CALLBACK_STATE, MANUAL_PRIORITY FROM TASK "
          + "<where> "
          + "<if test='taskIds != null'>ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</if> "
          + "<if test='externalIds != null'>EXTERNAL_ID IN(<foreach item='item' collection='externalIds' separator=',' >#{item}</foreach>)</if> "
          + "</where> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "taskId", column = "ID")
  @Result(property = "externalId", column = "EXTERNAL_ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "classificationId", column = "CLASSIFICATION_ID")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "taskState", column = "STATE")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "due", column = "DUE")
  @Result(property = "planned", column = "PLANNED")
  @Result(property = "callbackState", column = "CALLBACK_STATE")
  @Result(property = "manualPriority", column = "MANUAL_PRIORITY")
  List<MinimalTaskSummary> findExistingTasks(
      @Param("taskIds") Collection<String> taskIds, @Param("externalIds") List<String> externalIds);

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
          + "<if test='taskSummaries != null'> "
          + "UPDATE TASK SET MODIFIED = #{referenceTask.modified}, "
          + "PLANNED = #{referenceTask.planned}, DUE = #{referenceTask.due} "
          + "WHERE ID IN(<foreach item='taskSummary' collection='taskSummaries' separator=',' >#{taskSummary.taskId}</foreach>) "
          + "</if> "
          + "</script>")
  void updateTaskDueDates(
      @Param("taskSummaries") List<MinimalTaskSummary> taskSummaries,
      @Param("referenceTask") TaskImpl referenceTask);

  @Update(
      "<script>"
          + "<if test='taskIds != null'> "
          + "UPDATE TASK SET MODIFIED = #{referenceTask.modified}, "
          + "PRIORITY = #{referenceTask.priority} "
          + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
          + "</if> "
          + "</script>")
  void updatePriorityOfTasks(
      @Param("taskIds") List<String> taskIds, @Param("referenceTask") TaskImpl referenceTask);

  @Select(
      "<script>SELECT ID, PLANNED, STATE FROM TASK "
          + "WHERE ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>) "
          + "AND STATE IN ( 'READY','CLAIMED') "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "left", column = "ID")
  @Result(
      property = "right",
      column = "PLANNED",
      javaType = Instant.class,
      typeHandler = InstantTypeHandler.class)
  List<Pair<String, Instant>> filterTaskIdsForReadyAndClaimed(
      @Param("taskIds") List<String> taskIds);

  @Select(
      "<script> "
          + "<choose>"
          + "<when  test='accessIds == null'>"
          + "SELECT NULL LIMIT 0 "
          + "</when>"
          + "<otherwise>"
          + "SELECT t.ID, t.WORKBASKET_ID FROM TASK t WHERE t.ID IN(<foreach item='taskSummary' collection='taskSummaries' separator=',' >#{taskSummary.taskId}</foreach>)"
          + "AND NOT (t.WORKBASKET_ID IN ( "
          + "<choose>"
          + "<when test=\"_databaseId == 'db2' || _databaseId == 'oracle'\">"
          + "SELECT WID from (SELECT WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ FROM WORKBASKET_ACCESS_LIST s where "
          + "</when>"
          + "<otherwise>"
          + "SELECT WID from (SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ FROM WORKBASKET_ACCESS_LIST s where "
          + "</otherwise>"
          + "</choose>"
          + "ACCESS_ID IN (<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>) "
          + "group by WORKBASKET_ID ) f WHERE max_read = 1 )) "
          + "</otherwise>"
          + "</choose>"
          + "</script>")
  @Result(property = "left", column = "ID")
  @Result(property = "right", column = "WORKBASKET_ID")
  List<Pair<String, String>> getTaskAndWorkbasketIdsNotAuthorizedFor(
      @Param("taskSummaries") List<MinimalTaskSummary> taskSummaries,
      @Param("accessIds") List<String> accessIds);
}
