package io.kadai.monitor.internal;

import io.kadai.monitor.api.CombinedClassificationFilter;
import io.kadai.monitor.api.SelectedItem;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.TimeIntervalReportBuilder;
import io.kadai.monitor.api.reports.item.DetailedMonitorQueryItem;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.api.reports.item.PriorityQueryItem;
import io.kadai.monitor.api.reports.item.TaskQueryItem;
import io.kadai.monitor.api.reports.item.TimestampQueryItem;
import io.kadai.monitor.internal.reports.WorkbasketPriorityReportBuilderImpl;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskState;
import java.time.Instant;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;

/** This class is the mybatis mapping of task monitoring. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface MonitorMapper {

  @SelectProvider(type = MonitorMapperSqlProvider.class, method = "getTaskCountOfWorkbaskets")
  @Result(column = "WORKBASKET_KEY", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfWorkbaskets(
      @Param("now") Instant now,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report);

  @SelectProvider(type = MonitorMapperSqlProvider.class, method = "getTaskCountOfCategories")
  @Result(column = "CLASSIFICATION_CATEGORY", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfCategories(
      @Param("now") Instant now,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report);

  @SelectProvider(type = MonitorMapperSqlProvider.class, method = "getTaskCountOfClassifications")
  @Result(column = "CLASSIFICATION_KEY", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfClassifications(
      @Param("now") Instant now,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report);

  @SelectProvider(
      type = MonitorMapperSqlProvider.class,
      method = "getTaskCountOfDetailedClassifications")
  @Result(column = "TASK_CLASSIFICATION_KEY", property = "key")
  @Result(column = "ATTACHMENT_CLASSIFICATION_KEY", property = "attachmentKey")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<DetailedMonitorQueryItem> getTaskCountOfDetailedClassifications(
      @Param("now") Instant now,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report);

  @SelectProvider(
      type = MonitorMapperSqlProvider.class,
      method = "getTaskCountOfTaskCustomFieldValues")
  @Result(column = "CUSTOM_FIELD", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfTaskCustomFieldValues(
      @Param("now") Instant now,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report);

  @SelectProvider(type = MonitorMapperSqlProvider.class, method = "getTaskIdsForSelectedItems")
  List<String> getTaskIdsForSelectedItems(
      @Param("now") Instant now,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report,
      @Param("combinedClassificationFilter")
          List<CombinedClassificationFilter> combinedClassificationFilter,
      @Param("groupedBy") String groupedBy,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("selectedItems") List<SelectedItem> selectedItems,
      @Param("joinWithAttachments") boolean joinWithAttachments);

  @SelectProvider(type = MonitorMapperSqlProvider.class, method = "getTasksCountByState")
  @Result(column = "WORKBASKET_KEY", property = "workbasketKey")
  @Result(column = "STATE", property = "state")
  @Result(column = "COUNT", property = "count")
  List<TaskQueryItem> getTasksCountByState(
      @Param("domains") String[] domains,
      @Param("states") TaskState[] states,
      @Param("workbasketIds") String[] workbasketIds,
      @Param("priorityMinimum") Integer priorityMinimum);

  @SelectProvider(
      type = MonitorMapperSqlProvider.class,
      method = "getCustomAttributeValuesForReport")
  List<String> getCustomAttributeValuesForReport(
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report,
      @Param("combinedClassificationFilter")
          List<CombinedClassificationFilter> combinedClassificationFilter,
      @Param("customField") TaskCustomField taskCustomField);

  @SelectProvider(
      type = MonitorMapperSqlProvider.class,
      method = "getTasksCountForStatusGroupedByOrgLevel")
  @Result(column = "STATUS", property = "status")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "COUNT", property = "count")
  @Result(column = "ORG_LEVEL_1", property = "orgLevel1")
  @Result(column = "ORG_LEVEL_2", property = "orgLevel2")
  @Result(column = "ORG_LEVEL_3", property = "orgLevel3")
  @Result(column = "ORG_LEVEL_4", property = "orgLevel4")
  List<TimestampQueryItem> getTasksCountForStatusGroupedByOrgLevel(
      @Param("now") Instant now,
      @Param("status") TaskTimestamp status,
      @Param("report") TimeIntervalReportBuilder<?, ?, ?> report);

  @SelectProvider(type = MonitorMapperSqlProvider.class, method = "getTaskCountByPriority")
  @Result(column = "WORKBASKET_KEY", property = "workbasketKey")
  @Result(column = "COUNT", property = "count")
  @Result(column = "PRIORITY", property = "priority")
  List<PriorityQueryItem> getTaskCountByPriority(
      @Param("report") WorkbasketPriorityReportBuilderImpl report);
}
