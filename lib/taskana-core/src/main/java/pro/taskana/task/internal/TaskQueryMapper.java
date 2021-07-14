package pro.taskana.task.internal;

import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;

import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.TaskSummaryImpl;

/** This class provides a mapper for all task queries. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface TaskQueryMapper {

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "queryTaskSummaries")
  @Result(property = "id", column = "ID")
  @Result(property = "externalId", column = "EXTERNAL_ID")
  @Result(property = "created", column = "CREATED")
  @Result(property = "claimed", column = "CLAIMED")
  @Result(property = "completed", column = "COMPLETED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "planned", column = "PLANNED")
  @Result(property = "due", column = "DUE")
  @Result(property = "name", column = "NAME")
  @Result(property = "creator", column = "CREATOR")
  @Result(property = "note", column = "NOTE")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "priority", column = "PRIORITY")
  @Result(property = "state", column = "STATE")
  @Result(property = "workbasketSummaryImpl.domain", column = "DOMAIN")
  @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY")
  @Result(property = "workbasketSummaryImpl.id", column = "WORKBASKET_ID")
  @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY")
  @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID")
  @Result(property = "classificationSummaryImpl.domain", column = "DOMAIN")
  @Result(property = "classificationSummaryImpl.category", column = "CLASSIFICATION_CATEGORY")
  @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID")
  @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "primaryObjRef.company", column = "POR_COMPANY")
  @Result(property = "primaryObjRef.system", column = "POR_SYSTEM")
  @Result(property = "primaryObjRef.systemInstance", column = "POR_INSTANCE")
  @Result(property = "primaryObjRef.type", column = "POR_TYPE")
  @Result(property = "primaryObjRef.value", column = "POR_VALUE")
  @Result(property = "isRead", column = "IS_READ")
  @Result(property = "isTransferred", column = "IS_TRANSFERRED")
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
  List<TaskSummaryImpl> queryTaskSummaries(TaskQueryImpl taskQuery);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "queryTaskSummariesDb2")
  @Result(property = "id", column = "ID")
  @Result(property = "externalId", column = "EXTERNAL_ID")
  @Result(property = "created", column = "CREATED")
  @Result(property = "claimed", column = "CLAIMED")
  @Result(property = "completed", column = "COMPLETED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "planned", column = "PLANNED")
  @Result(property = "due", column = "DUE")
  @Result(property = "name", column = "NAME")
  @Result(property = "creator", column = "CREATOR")
  @Result(property = "note", column = "NOTE")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "priority", column = "PRIORITY")
  @Result(property = "state", column = "STATE")
  @Result(property = "workbasketSummaryImpl.domain", column = "DOMAIN")
  @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY")
  @Result(property = "workbasketSummaryImpl.id", column = "WORKBASKET_ID")
  @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY")
  @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID")
  @Result(property = "classificationSummaryImpl.domain", column = "DOMAIN")
  @Result(property = "classificationSummaryImpl.category", column = "CLASSIFICATION_CATEGORY")
  @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID")
  @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "primaryObjRef.company", column = "POR_COMPANY")
  @Result(property = "primaryObjRef.system", column = "POR_SYSTEM")
  @Result(property = "primaryObjRef.systemInstance", column = "POR_INSTANCE")
  @Result(property = "primaryObjRef.type", column = "POR_TYPE")
  @Result(property = "primaryObjRef.value", column = "POR_VALUE")
  @Result(property = "isRead", column = "IS_READ")
  @Result(property = "isTransferred", column = "IS_TRANSFERRED")
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
  List<TaskSummaryImpl> queryTaskSummariesDb2(TaskQueryImpl taskQuery);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "queryObjectReferences")
  @Result(property = "id", column = "ID")
  @Result(property = "company", column = "COMPANY")
  @Result(property = "system", column = "SYSTEM")
  @Result(property = "systemInstance", column = "SYSTEM_INSTANCE")
  @Result(property = "type", column = "TYPE")
  @Result(property = "value", column = "VALUE")
  List<ObjectReference> queryObjectReferences(ObjectReferenceQueryImpl objectReference);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "countQueryTasks")
  Long countQueryTasks(TaskQueryImpl taskQuery);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "countQueryTasksDb2")
  Long countQueryTasksDb2(TaskQueryImpl taskQuery);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "countQueryObjectReferences")
  Long countQueryObjectReferences(ObjectReferenceQueryImpl objectReference);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "queryTaskColumnValues")
  List<String> queryTaskColumnValues(TaskQueryImpl taskQuery);

  @SelectProvider(type = TaskQuerySqlProvider.class, method = "queryObjectReferenceColumnValues")
  List<String> queryObjectReferenceColumnValues(ObjectReferenceQueryImpl objectReference);
}
