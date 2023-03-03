/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-provider
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
package pro.taskana.simplehistory.impl.task;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** This class is the mybatis mapping of task history events. */
@SuppressWarnings("checkstyle:LineLength")
public interface TaskHistoryEventMapper {

  @Insert(
      "<script>INSERT INTO TASK_HISTORY_EVENT (ID,BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID,"
          + " EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, POR_COMPANY, POR_SYSTEM, POR_INSTANCE,"
          + " POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY, ATTACHMENT_CLASSIFICATION_KEY, "
          + " OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, DETAILS)"
          + " VALUES ( #{historyEvent.id}, #{historyEvent.businessProcessId}, #{historyEvent.parentBusinessProcessId}, #{historyEvent.taskId},"
          + " #{historyEvent.eventType}, #{historyEvent.created}, #{historyEvent.userId}, #{historyEvent.domain}, #{historyEvent.workbasketKey},"
          + " #{historyEvent.porCompany}, #{historyEvent.porSystem}, #{historyEvent.porInstance}, #{historyEvent.porType},"
          + " #{historyEvent.porValue}, #{historyEvent.taskClassificationKey}, #{historyEvent.taskClassificationCategory},"
          + " #{historyEvent.attachmentClassificationKey}, #{historyEvent.oldValue}, #{historyEvent.newValue},"
          + " #{historyEvent.custom1}, #{historyEvent.custom2}, #{historyEvent.custom3}, #{historyEvent.custom4},"
          + " #{historyEvent.details}) "
          + "</script>")
  void insert(@Param("historyEvent") TaskHistoryEvent historyEvent);

  @Select(
      "<script>"
          + "SELECT ID, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, "
          + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY,"
          + "ATTACHMENT_CLASSIFICATION_KEY, OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, DETAILS "
          + "FROM TASK_HISTORY_EVENT WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID")
  @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "eventType", column = "EVENT_TYPE")
  @Result(property = "created", column = "CREATED")
  @Result(property = "userId", column = "USER_ID")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "workbasketKey", column = "WORKBASKET_KEY")
  @Result(property = "porCompany", column = "POR_COMPANY")
  @Result(property = "porSystem", column = "POR_SYSTEM")
  @Result(property = "porInstance", column = "POR_INSTANCE")
  @Result(property = "porType", column = "POR_TYPE")
  @Result(property = "porValue", column = "POR_VALUE")
  @Result(property = "taskClassificationKey", column = "TASK_CLASSIFICATION_KEY")
  @Result(property = "taskClassificationCategory", column = "TASK_CLASSIFICATION_CATEGORY")
  @Result(property = "attachmentClassificationKey", column = "ATTACHMENT_CLASSIFICATION_KEY")
  @Result(property = "oldValue", column = "OLD_VALUE")
  @Result(property = "newValue", column = "NEW_VALUE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "details", column = "DETAILS")
  TaskHistoryEvent findById(@Param("id") String id);

  @Delete(
      "<script>DELETE FROM TASK_HISTORY_EVENT WHERE TASK_ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</script>")
  void deleteMultipleByTaskIds(@Param("taskIds") List<String> taskIds);
}
