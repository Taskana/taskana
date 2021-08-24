package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryColumnName;
import pro.taskana.spi.history.api.events.task.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** Implementation for generating dynamic sql. */
public class TaskHistoryQueryImpl implements TaskHistoryQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper.queryHistoryEvents";
  private static final String LINK_TO_VALUE_MAPPER =
      "pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper.queryHistoryColumnValues";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper.countHistoryEvents";

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskHistoryQueryImpl.class);

  private static final String SQL_EXCEPTION_MESSAGE =
      "Method openConnection() could not open a connection to the database.";

  private final TaskanaHistoryEngineImpl taskanaHistoryEngine;
  private final List<String> orderBy;
  private final List<String> orderColumns;

  @SuppressWarnings("unused")
  private TaskHistoryQueryColumnName columnName;

  private String[] idIn;
  private String[] businessProcessIdIn;
  private String[] parentBusinessProcessIdIn;
  private String[] taskIdIn;
  private String[] eventTypeIn;
  private TimeInterval[] createdIn;
  private String[] userIdIn;
  private String[] domainIn;
  private String[] workbasketKeyIn;
  private String[] porCompanyIn;
  private String[] porSystemIn;
  private String[] porInstanceIn;
  private String[] porTypeIn;
  private String[] porValueIn;
  private String[] taskClassificationKeyIn;
  private String[] taskClassificationCategoryIn;
  private String[] attachmentClassificationKeyIn;
  private String[] oldValueIn;
  private String[] newValueIn;
  private String[] custom1In;
  private String[] custom2In;
  private String[] custom3In;
  private String[] custom4In;

  private String[] businessProcessIdLike;
  private String[] parentBusinessProcessIdLike;
  private String[] taskIdLike;
  private String[] eventTypeLike;
  private String[] userIdLike;
  private String[] domainLike;
  private String[] workbasketKeyLike;
  private String[] porCompanyLike;
  private String[] porSystemLike;
  private String[] porInstanceLike;
  private String[] porTypeLike;
  private String[] porValueLike;
  private String[] taskClassificationKeyLike;
  private String[] taskClassificationCategoryLike;
  private String[] attachmentClassificationKeyLike;
  private String[] oldValueLike;
  private String[] newValueLike;
  private String[] custom1Like;
  private String[] custom2Like;
  private String[] custom3Like;
  private String[] custom4Like;

  public TaskHistoryQueryImpl(TaskanaHistoryEngineImpl taskanaHistoryEngine) {
    this.taskanaHistoryEngine = taskanaHistoryEngine;
    this.orderBy = new ArrayList<>();
    this.orderColumns = new ArrayList<>();
  }

  public String[] getIdIn() {
    return idIn;
  }

  public String[] getBusinessProcessIdIn() {
    return businessProcessIdIn;
  }

  public String[] getParentBusinessProcessIdIn() {
    return parentBusinessProcessIdIn;
  }

  public String[] getTaskIdIn() {
    return taskIdIn;
  }

  public String[] getEventTypeIn() {
    return eventTypeIn;
  }

  public TimeInterval[] getCreatedIn() {
    return createdIn;
  }

  public String[] getUserIdIn() {
    return userIdIn;
  }

  public String[] getDomainIn() {
    return domainIn;
  }

  public String[] getWorkbasketKeyIn() {
    return workbasketKeyIn;
  }

  public String[] getPorCompanyIn() {
    return porCompanyIn;
  }

  public String[] getPorSystemIn() {
    return porSystemIn;
  }

  public String[] getPorInstanceIn() {
    return porInstanceIn;
  }

  public String[] getPorTypeIn() {
    return porTypeIn;
  }

  public String[] getPorValueIn() {
    return porValueIn;
  }

  public String[] getTaskClassificationKeyIn() {
    return taskClassificationKeyIn;
  }

  public String[] getTaskClassificationCategoryIn() {
    return taskClassificationCategoryIn;
  }

  public String[] getAttachmentClassificationKeyIn() {
    return attachmentClassificationKeyIn;
  }

  public String[] getOldValueIn() {
    return oldValueIn;
  }

  public String[] getNewValueIn() {
    return newValueIn;
  }

  public String[] getCustom1In() {
    return custom1In;
  }

  public String[] getCustom2In() {
    return custom2In;
  }

  public String[] getCustom3In() {
    return custom3In;
  }

  public String[] getCustom4In() {
    return custom4In;
  }

  public String[] getBusinessProcessIdLike() {
    return businessProcessIdLike;
  }

  public String[] getParentBusinessProcessIdLike() {
    return parentBusinessProcessIdLike;
  }

  public String[] getTaskIdLike() {
    return taskIdLike;
  }

  public String[] getEventTypeLike() {
    return eventTypeLike;
  }

  public String[] getUserIdLike() {
    return userIdLike;
  }

  public String[] getDomainLike() {
    return domainLike;
  }

  public String[] getWorkbasketKeyLike() {
    return workbasketKeyLike;
  }

  public String[] getPorCompanyLike() {
    return porCompanyLike;
  }

  public String[] getPorSystemLike() {
    return porSystemLike;
  }

  public String[] getPorInstanceLike() {
    return porInstanceLike;
  }

  public String[] getPorTypeLike() {
    return porTypeLike;
  }

  public String[] getPorValueLike() {
    return porValueLike;
  }

  public String[] getTaskClassificationKeyLike() {
    return taskClassificationKeyLike;
  }

  public String[] getTaskClassificationCategoryLike() {
    return taskClassificationCategoryLike;
  }

  public String[] getAttachmentClassificationKeyLike() {
    return attachmentClassificationKeyLike;
  }

  public String[] getOldValueLike() {
    return oldValueLike;
  }

  public String[] getNewValueLike() {
    return newValueLike;
  }

  public String[] getCustom1Like() {
    return custom1Like;
  }

  public String[] getCustom2Like() {
    return custom2Like;
  }

  public String[] getCustom3Like() {
    return custom3Like;
  }

  public String[] getCustom4Like() {
    return custom4Like;
  }

  @Override
  public TaskHistoryQuery idIn(String... idIn) {
    this.idIn = toUpperCopy(idIn);
    return this;
  }

  @Override
  public TaskHistoryQuery businessProcessIdIn(String... businessProcessId) {
    this.businessProcessIdIn = toUpperCopy(businessProcessId);
    return this;
  }

  @Override
  public TaskHistoryQuery parentBusinessProcessIdIn(String... parentBusinessProcessId) {
    this.parentBusinessProcessIdIn = toUpperCopy(parentBusinessProcessId);
    return this;
  }

  @Override
  public TaskHistoryQuery taskIdIn(String... taskId) {
    this.taskIdIn = toUpperCopy(taskId);
    return this;
  }

  @Override
  public TaskHistoryQuery eventTypeIn(String... eventType) {
    this.eventTypeIn = toUpperCopy(eventType);
    return this;
  }

  @Override
  public TaskHistoryQuery createdWithin(TimeInterval... createdIn) {
    this.createdIn = createdIn;
    return this;
  }

  @Override
  public TaskHistoryQuery userIdIn(String... userId) {
    this.userIdIn = toUpperCopy(userId);
    return this;
  }

  @Override
  public TaskHistoryQuery domainIn(String... domain) {
    this.domainIn = toUpperCopy(domain);
    return this;
  }

  @Override
  public TaskHistoryQuery workbasketKeyIn(String... workbasketKey) {
    this.workbasketKeyIn = toUpperCopy(workbasketKey);
    return this;
  }

  @Override
  public TaskHistoryQuery porCompanyIn(String... porCompany) {
    this.porCompanyIn = toUpperCopy(porCompany);
    return this;
  }

  @Override
  public TaskHistoryQuery porSystemIn(String... porSystem) {
    this.porSystemIn = toUpperCopy(porSystem);
    return this;
  }

  @Override
  public TaskHistoryQuery porInstanceIn(String... porInstance) {
    this.porInstanceIn = toUpperCopy(porInstance);
    return this;
  }

  @Override
  public TaskHistoryQuery porTypeIn(String... porType) {
    this.porTypeIn = toUpperCopy(porType);
    return this;
  }

  @Override
  public TaskHistoryQuery porValueIn(String... porValue) {
    this.porValueIn = toUpperCopy(porValue);
    return this;
  }

  @Override
  public TaskHistoryQuery taskClassificationKeyIn(String... taskClassificationKey) {
    this.taskClassificationKeyIn = toUpperCopy(taskClassificationKey);
    return this;
  }

  @Override
  public TaskHistoryQuery taskClassificationCategoryIn(String... taskClassificationCategory) {
    this.taskClassificationCategoryIn = toUpperCopy(taskClassificationCategory);
    return this;
  }

  @Override
  public TaskHistoryQuery attachmentClassificationKeyIn(String... attachmentClassificationKey) {
    this.attachmentClassificationKeyIn = toUpperCopy(attachmentClassificationKey);
    return this;
  }

  @Override
  public TaskHistoryQuery oldValueIn(String... oldValueIn) {
    this.oldValueIn = toUpperCopy(oldValueIn);
    return this;
  }

  @Override
  public TaskHistoryQuery newValueIn(String... newValueIn) {
    this.newValueIn = toUpperCopy(newValueIn);
    return this;
  }

  @Override
  public TaskHistoryQuery businessProcessIdLike(String... businessProcessId) {
    this.businessProcessIdLike = toUpperCopy(businessProcessId);
    return this;
  }

  @Override
  public TaskHistoryQuery parentBusinessProcessIdLike(String... parentBusinessProcessId) {
    this.parentBusinessProcessIdLike = toUpperCopy(parentBusinessProcessId);
    return this;
  }

  @Override
  public TaskHistoryQuery taskIdLike(String... taskId) {
    this.taskIdLike = toUpperCopy(taskId);
    return this;
  }

  @Override
  public TaskHistoryQuery eventTypeLike(String... eventType) {
    this.eventTypeLike = toUpperCopy(eventType);
    return this;
  }

  @Override
  public TaskHistoryQuery userIdLike(String... userId) {
    this.userIdLike = toUpperCopy(userId);
    return this;
  }

  @Override
  public TaskHistoryQuery domainLike(String... domain) {
    this.domainLike = toUpperCopy(domain);
    return this;
  }

  @Override
  public TaskHistoryQuery workbasketKeyLike(String... workbasketKey) {
    this.workbasketKeyLike = toUpperCopy(workbasketKey);
    return this;
  }

  @Override
  public TaskHistoryQuery porCompanyLike(String... porCompany) {
    this.porCompanyLike = toUpperCopy(porCompany);
    return this;
  }

  @Override
  public TaskHistoryQuery porSystemLike(String... porSystem) {
    this.porSystemLike = toUpperCopy(porSystem);
    return this;
  }

  @Override
  public TaskHistoryQuery porInstanceLike(String... porInstance) {
    this.porInstanceLike = toUpperCopy(porInstance);
    return this;
  }

  @Override
  public TaskHistoryQuery porTypeLike(String... porType) {
    this.porTypeLike = toUpperCopy(porType);
    return this;
  }

  @Override
  public TaskHistoryQuery porValueLike(String... porValue) {
    this.porValueLike = toUpperCopy(porValue);
    return this;
  }

  @Override
  public TaskHistoryQuery taskClassificationKeyLike(String... taskClassificationKey) {
    this.taskClassificationKeyLike = toUpperCopy(taskClassificationKey);
    return this;
  }

  @Override
  public TaskHistoryQuery taskClassificationCategoryLike(String... taskClassificationCategory) {
    this.taskClassificationCategoryLike = toUpperCopy(taskClassificationCategory);
    return this;
  }

  @Override
  public TaskHistoryQuery attachmentClassificationKeyLike(String... attachmentClassificationKey) {
    this.attachmentClassificationKeyLike = toUpperCopy(attachmentClassificationKey);
    return this;
  }

  @Override
  public TaskHistoryQuery oldValueLike(String... oldValue) {
    this.oldValueLike = toUpperCopy(oldValue);
    return this;
  }

  @Override
  public TaskHistoryQuery newValueLike(String... newValue) {
    this.newValueLike = toUpperCopy(newValue);
    return this;
  }

  @Override
  public TaskHistoryQuery customAttributeIn(
      TaskHistoryCustomField customField, String... searchArguments) {
    switch (customField) {
      case CUSTOM_1:
        custom1In = toUpperCopy(searchArguments);
        break;
      case CUSTOM_2:
        custom2In = toUpperCopy(searchArguments);
        break;
      case CUSTOM_3:
        custom3In = toUpperCopy(searchArguments);
        break;
      case CUSTOM_4:
        custom4In = toUpperCopy(searchArguments);
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
    return this;
  }

  @Override
  public TaskHistoryQuery customAttributeLike(
      TaskHistoryCustomField customField, String... searchArguments) {
    switch (customField) {
      case CUSTOM_1:
        custom1Like = toUpperCopy(searchArguments);
        break;
      case CUSTOM_2:
        custom2Like = toUpperCopy(searchArguments);
        break;
      case CUSTOM_3:
        custom3Like = toUpperCopy(searchArguments);
        break;
      case CUSTOM_4:
        custom4Like = toUpperCopy(searchArguments);
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
    return this;
  }

  @Override
  public TaskHistoryQuery orderByTaskHistoryEventId(SortDirection sortDirection) {
    return addOrderCriteria("ID", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByParentBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByTaskId(SortDirection sortDirection) {
    return addOrderCriteria("TASK_ID", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByEventType(SortDirection sortDirection) {
    return addOrderCriteria("EVENT_TYPE", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByCreated(SortDirection sortDirection) {
    return addOrderCriteria("CREATED", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByUserId(SortDirection sortDirection) {
    return addOrderCriteria("USER_ID", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByWorkbasketKey(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_KEY", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByPorCompany(SortDirection sortDirection) {
    return addOrderCriteria("POR_COMPANY", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByPorSystem(SortDirection sortDirection) {
    return addOrderCriteria("POR_SYSTEM", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByPorInstance(SortDirection sortDirection) {
    return addOrderCriteria("POR_INSTANCE", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByPorType(SortDirection sortDirection) {
    return addOrderCriteria("POR_TYPE", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByPorValue(SortDirection sortDirection) {
    return addOrderCriteria("POR_VALUE", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByTaskClassificationKey(SortDirection sortDirection) {
    return addOrderCriteria("TASK_CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByTaskClassificationCategory(SortDirection sortDirection) {
    return addOrderCriteria("TASK_CLASSIFICATION_CATEGORY", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByAttachmentClassificationKey(SortDirection sortDirection) {
    return addOrderCriteria("ATTACHMENT_CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByOldValue(SortDirection sortDirection) {
    return addOrderCriteria("OLD_VALUE", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByNewValue(SortDirection sortDirection) {
    return addOrderCriteria("NEW_VALUE", sortDirection);
  }

  @Override
  public TaskHistoryQuery orderByCustomAttribute(
      TaskHistoryCustomField customField, SortDirection sortDirection) {
    return addOrderCriteria(customField.name(), sortDirection);
  }

  @Override
  public List<TaskHistoryEvent> list() {
    List<TaskHistoryEvent> result = new ArrayList<>();
    try {
      taskanaHistoryEngine.openConnection();
      result = taskanaHistoryEngine.getSqlSession().selectList(LINK_TO_MAPPER, this);
      return result;
    } catch (SQLException e) {
      LOGGER.error(SQL_EXCEPTION_MESSAGE, e.getCause());
      return result;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public List<TaskHistoryEvent> list(int offset, int limit) {
    List<TaskHistoryEvent> result = new ArrayList<>();
    try {
      taskanaHistoryEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      result = taskanaHistoryEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
      return result;
    } catch (SQLException e) {
      LOGGER.error(SQL_EXCEPTION_MESSAGE, e.getCause());
      return result;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public List<String> listValues(
      TaskHistoryQueryColumnName dbColumnName, SortDirection sortDirection) {
    List<String> result = new ArrayList<>();
    this.columnName = dbColumnName;
    this.orderBy.clear();
    this.addOrderCriteria(columnName.toString(), sortDirection);

    try {
      taskanaHistoryEngine.openConnection();
      result = taskanaHistoryEngine.getSqlSession().selectList(LINK_TO_VALUE_MAPPER, this);
      return result;
    } catch (SQLException e) {
      LOGGER.error(SQL_EXCEPTION_MESSAGE, e.getCause());
      return result;
    } finally {
      this.orderColumns.remove(orderColumns.size() - 1);
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public TaskHistoryEvent single() {
    TaskHistoryEvent result = null;
    try {
      taskanaHistoryEngine.openConnection();
      result = taskanaHistoryEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);

      return result;

    } catch (SQLException e) {
      LOGGER.error(SQL_EXCEPTION_MESSAGE, e.getCause());
      return result;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public long count() {
    Long rowCount;
    try {
      taskanaHistoryEngine.openConnection();
      rowCount = taskanaHistoryEngine.getSqlSession().selectOne(LINK_TO_COUNTER, this);
      return (rowCount == null) ? 0L : rowCount;
    } catch (SQLException e) {
      LOGGER.error(SQL_EXCEPTION_MESSAGE, e.getCause());
      return -1;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  private TaskHistoryQueryImpl addOrderCriteria(String columnName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(columnName + orderByDirection);
    orderColumns.add(columnName);
    return this;
  }
}
