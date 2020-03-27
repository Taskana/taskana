package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;
import pro.taskana.simplehistory.query.HistoryQuery;
import pro.taskana.simplehistory.query.HistoryQueryColumnName;

/** Implementation for generating dynamic sql. */
public class HistoryQueryImpl implements HistoryQuery {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryQueryImpl.class);

  private TaskanaHistoryEngineImpl taskanaHistoryEngine;
  private HistoryQueryMapper historyQueryMapper;

  private HistoryQueryColumnName columnName;
  private List<String> orderBy;
  private List<String> orderColumns;
  private int
      maxRows; // limit for rows. used to make list(offset, limit) and single() more efficient.

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

  public HistoryQueryImpl(
      TaskanaHistoryEngineImpl taskanaHistoryEngineImpl, HistoryQueryMapper historyQueryMapper) {
    this.taskanaHistoryEngine = taskanaHistoryEngineImpl;
    this.historyQueryMapper = historyQueryMapper;
    this.orderBy = new ArrayList<>();
    this.orderColumns = new ArrayList<>();
    this.maxRows = -1;
  }

  @Override
  public HistoryQuery idIn(String... idIn) {
    this.idIn = toUpperCopy(idIn);
    return this;
  }

  @Override
  public HistoryQuery businessProcessIdIn(String... businessProcessId) {
    this.businessProcessIdIn = toUpperCopy(businessProcessId);
    return this;
  }

  @Override
  public HistoryQuery parentBusinessProcessIdIn(String... parentBusinessProcessId) {
    this.parentBusinessProcessIdIn = toUpperCopy(parentBusinessProcessId);
    return this;
  }

  @Override
  public HistoryQuery taskIdIn(String... taskId) {
    this.taskIdIn = toUpperCopy(taskId);
    return this;
  }

  @Override
  public HistoryQuery eventTypeIn(String... eventType) {
    this.eventTypeIn = toUpperCopy(eventType);
    return this;
  }

  @Override
  public HistoryQuery createdWithin(TimeInterval... createdIn) {
    this.createdIn = createdIn;
    return this;
  }

  @Override
  public HistoryQuery userIdIn(String... userId) {
    this.userIdIn = toUpperCopy(userId);
    return this;
  }

  @Override
  public HistoryQuery domainIn(String... domain) {
    this.domainIn = toUpperCopy(domain);
    return this;
  }

  @Override
  public HistoryQuery workbasketKeyIn(String... workbasketKey) {
    this.workbasketKeyIn = toUpperCopy(workbasketKey);
    return this;
  }

  @Override
  public HistoryQuery porCompanyIn(String... porCompany) {
    this.porCompanyIn = toUpperCopy(porCompany);
    return this;
  }

  @Override
  public HistoryQuery porSystemIn(String... porSystem) {
    this.porSystemIn = toUpperCopy(porSystem);
    return this;
  }

  @Override
  public HistoryQuery porInstanceIn(String... porInstance) {
    this.porInstanceIn = toUpperCopy(porInstance);
    return this;
  }

  @Override
  public HistoryQuery porTypeIn(String... porType) {
    this.porTypeIn = toUpperCopy(porType);
    return this;
  }

  @Override
  public HistoryQuery porValueIn(String... porValue) {
    this.porValueIn = toUpperCopy(porValue);
    return this;
  }

  @Override
  public HistoryQuery taskClassificationKeyIn(String... taskClassificationKey) {
    this.taskClassificationKeyIn = toUpperCopy(taskClassificationKey);
    return this;
  }

  @Override
  public HistoryQuery taskClassificationCategoryIn(String... taskClassificationCategory) {
    this.taskClassificationCategoryIn = toUpperCopy(taskClassificationCategory);
    return this;
  }

  @Override
  public HistoryQuery attachmentClassificationKeyIn(String... attachmentClassificationKey) {
    this.attachmentClassificationKeyIn = toUpperCopy(attachmentClassificationKey);
    return this;
  }

  @Override
  public HistoryQuery oldValueIn(String... oldValueIn) {
    this.oldValueIn = toUpperCopy(oldValueIn);
    return this;
  }

  @Override
  public HistoryQuery newValueIn(String... newValueIn) {
    this.newValueIn = toUpperCopy(newValueIn);
    return this;
  }

  @Override
  public HistoryQuery custom1In(String... custom1) {
    this.custom1In = toUpperCopy(custom1);
    return this;
  }

  @Override
  public HistoryQuery custom2In(String... custom2) {
    this.custom2In = toUpperCopy(custom2);
    return this;
  }

  @Override
  public HistoryQuery custom3In(String... custom3) {
    this.custom3In = toUpperCopy(custom3);
    return this;
  }

  @Override
  public HistoryQuery custom4In(String... custom4) {
    this.custom4In = toUpperCopy(custom4);
    return this;
  }

  @Override
  public HistoryQuery businessProcessIdLike(String... businessProcessId) {
    this.businessProcessIdLike = toUpperCopy(businessProcessId);
    return this;
  }

  @Override
  public HistoryQuery parentBusinessProcessIdLike(String... parentBusinessProcessId) {
    this.parentBusinessProcessIdLike = toUpperCopy(parentBusinessProcessId);
    return this;
  }

  @Override
  public HistoryQuery taskIdLike(String... taskId) {
    this.taskIdLike = toUpperCopy(taskId);
    return this;
  }

  @Override
  public HistoryQuery eventTypeLike(String... eventType) {
    this.eventTypeLike = toUpperCopy(eventType);
    return this;
  }

  @Override
  public HistoryQuery userIdLike(String... userId) {
    this.userIdLike = toUpperCopy(userId);
    return this;
  }

  @Override
  public HistoryQuery domainLike(String... domain) {
    this.domainLike = toUpperCopy(domain);
    return this;
  }

  @Override
  public HistoryQuery workbasketKeyLike(String... workbasketKey) {
    this.workbasketKeyLike = toUpperCopy(workbasketKey);
    return this;
  }

  @Override
  public HistoryQuery porCompanyLike(String... porCompany) {
    this.porCompanyLike = toUpperCopy(porCompany);
    return this;
  }

  @Override
  public HistoryQuery porSystemLike(String... porSystem) {
    this.porSystemLike = toUpperCopy(porSystem);
    return this;
  }

  @Override
  public HistoryQuery porInstanceLike(String... porInstance) {
    this.porInstanceLike = toUpperCopy(porInstance);
    return this;
  }

  @Override
  public HistoryQuery porTypeLike(String... porType) {
    this.porTypeLike = toUpperCopy(porType);
    return this;
  }

  @Override
  public HistoryQuery porValueLike(String... porValue) {
    this.porValueLike = toUpperCopy(porValue);
    return this;
  }

  @Override
  public HistoryQuery taskClassificationKeyLike(String... taskClassificationKey) {
    this.taskClassificationKeyLike = toUpperCopy(taskClassificationKey);
    return this;
  }

  @Override
  public HistoryQuery taskClassificationCategoryLike(String... taskClassificationCategory) {
    this.taskClassificationCategoryLike = toUpperCopy(taskClassificationCategory);
    return this;
  }

  @Override
  public HistoryQuery attachmentClassificationKeyLike(String... attachmentClassificationKey) {
    this.attachmentClassificationKeyLike = toUpperCopy(attachmentClassificationKey);
    return this;
  }

  @Override
  public HistoryQuery oldValueLike(String... oldValue) {
    this.oldValueLike = toUpperCopy(oldValue);
    return this;
  }

  @Override
  public HistoryQuery newValueLike(String... newValue) {
    this.newValueLike = toUpperCopy(newValue);
    return this;
  }

  @Override
  public HistoryQuery custom1Like(String... custom1) {
    this.custom1Like = toUpperCopy(custom1);
    return this;
  }

  @Override
  public HistoryQuery custom2Like(String... custom2) {
    this.custom2Like = toUpperCopy(custom2);
    return this;
  }

  @Override
  public HistoryQuery custom3Like(String... custom3) {
    this.custom3Like = toUpperCopy(custom3);
    return this;
  }

  @Override
  public HistoryQuery custom4Like(String... custom4) {
    this.custom4Like = toUpperCopy(custom4);
    return this;
  }

  @Override
  public HistoryQuery orderByBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public HistoryQuery orderByParentBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public HistoryQuery orderByTaskId(SortDirection sortDirection) {
    return addOrderCriteria("TASK_ID", sortDirection);
  }

  @Override
  public HistoryQuery orderByEventType(SortDirection sortDirection) {
    return addOrderCriteria("EVENT_TYPE", sortDirection);
  }

  @Override
  public HistoryQuery orderByCreated(SortDirection sortDirection) {
    return addOrderCriteria("CREATED", sortDirection);
  }

  @Override
  public HistoryQuery orderByUserId(SortDirection sortDirection) {
    return addOrderCriteria("USER_ID", sortDirection);
  }

  @Override
  public HistoryQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public HistoryQuery orderByWorkbasketKey(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_KEY", sortDirection);
  }

  @Override
  public HistoryQuery orderByPorCompany(SortDirection sortDirection) {
    return addOrderCriteria("POR_COMPANY", sortDirection);
  }

  @Override
  public HistoryQuery orderByPorSystem(SortDirection sortDirection) {
    return addOrderCriteria("POR_SYSTEM", sortDirection);
  }

  @Override
  public HistoryQuery orderByPorInstance(SortDirection sortDirection) {
    return addOrderCriteria("POR_INSTANCE", sortDirection);
  }

  @Override
  public HistoryQuery orderByPorType(SortDirection sortDirection) {
    return addOrderCriteria("POR_TYPE", sortDirection);
  }

  @Override
  public HistoryQuery orderByPorValue(SortDirection sortDirection) {
    return addOrderCriteria("POR_VALUE", sortDirection);
  }

  @Override
  public HistoryQuery orderByTaskClassificationKey(SortDirection sortDirection) {
    return addOrderCriteria("TASK_CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public HistoryQuery orderByTaskClassificationCategory(SortDirection sortDirection) {
    return addOrderCriteria("TASK_CLASSIFICATION_CATEGORY", sortDirection);
  }

  @Override
  public HistoryQuery orderByAttachmentClassificationKey(SortDirection sortDirection) {
    return addOrderCriteria("ATTACHMENT_CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public HistoryQuery orderByOldValue(SortDirection sortDirection) {
    return addOrderCriteria("OLD_VALUE", sortDirection);
  }

  @Override
  public HistoryQuery orderByNewValue(SortDirection sortDirection) {
    return addOrderCriteria("NEW_VALUE", sortDirection);
  }

  @Override
  public HistoryQuery orderByCustomAttribute(int num, SortDirection sortDirection)
      throws InvalidArgumentException {

    switch (num) {
      case 1:
        return addOrderCriteria("CUSTOM_1", sortDirection);
      case 2:
        return addOrderCriteria("CUSTOM_2", sortDirection);
      case 3:
        return addOrderCriteria("CUSTOM_3", sortDirection);
      case 4:
        return addOrderCriteria("CUSTOM_4", sortDirection);
      default:
        throw new InvalidArgumentException(
            "Custom number has to be between 1 and 4, but this is: " + num);
    }
  }
  
  @Override
  public List<HistoryEventImpl> list() {
    LOGGER.debug("entry to list(), this = {}", this);
    List<HistoryEventImpl> result = new ArrayList<>();
    try {
      taskanaHistoryEngine.openConnection();
      result = historyQueryMapper.queryHistoryEvent(this);
      LOGGER.debug("transaction was successful. Result: {}", result.toString());
      return result;
    } catch (SQLException e) {
      LOGGER.error(
          "Method openConnection() could not open a connection to the database.", e.getCause());
      return result;
    } catch (NullPointerException npe) {
      LOGGER.error("No History Event found.");
      return result;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public List<HistoryEventImpl> list(int offset, int limit) {
    LOGGER.debug("entry to list({},{}), this = {}", offset, limit, this);
    List<HistoryEventImpl> result = new ArrayList<>();
    try {
      taskanaHistoryEngine.openConnection();
      this.maxRows = offset + limit;
      result = historyQueryMapper.queryHistoryEvent(this);
      LOGGER.debug("transaction was successful. Result: {}", result.toString());
      limit = Math.min(result.size() - offset, limit);
      if (result.size() > offset) {
        return result.subList(offset, offset + limit);
      } else {
        return new ArrayList<>();
      }
    } catch (SQLException e) {
      LOGGER.error(
          "Method openConnection() could not open a connection to the database.", e.getCause());
      return result;
    } catch (NullPointerException npe) {
      LOGGER.error("No History Event found.");
      return result;
    } finally {
      taskanaHistoryEngine.returnConnection();
      this.maxRows = -1;
    }
  }

  @Override
  public List<String> listValues(HistoryQueryColumnName dbColumnName, SortDirection sortDirection) {
    LOGGER.debug(
        "entry to listValues() of column {} with sortDirection {}, this {}",
        dbColumnName,
        sortDirection,
        this);
    List<String> result = new ArrayList<>();
    this.columnName = dbColumnName;
    List<String> cacheOrderBy = this.orderBy;
    this.orderBy.clear();
    this.addOrderCriteria(columnName.toString(), sortDirection);

    try {
      taskanaHistoryEngine.openConnection();
      result = historyQueryMapper.queryHistoryColumnValues(this);
      LOGGER.debug("transaction was successful. Result: {}", result.toString());
      return result;
    } catch (SQLException e) {
      LOGGER.error(
          "Method openConnection() could not open a connection to the database.", e.getCause());
      return result;
    } catch (NullPointerException npe) {
      LOGGER.error("No History Event found.");
      return result;
    } finally {
      this.orderBy = cacheOrderBy;
      this.columnName = null;
      this.orderColumns.remove(orderColumns.size() - 1);
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public HistoryEventImpl single() {
    LOGGER.debug("entry to list(), this = {}", this);
    HistoryEventImpl result = null;
    try {
      taskanaHistoryEngine.openConnection();
      this.maxRows = 1;
      result = historyQueryMapper.queryHistoryEvent(this).get(0);
      LOGGER.debug("transaction was successful. Result: {}", result.toString());
      return result;
    } catch (SQLException e) {
      LOGGER.error(
          "Method openConnection() could not open a connection to the database.", e.getCause());
      return result;
    } catch (NullPointerException npe) {
      LOGGER.error("No History Event found.");
      return result;
    } finally {
      taskanaHistoryEngine.returnConnection();
      this.maxRows = -1;
    }
  }

  @Override
  public long count() {
    LOGGER.debug("entry to count(), this = {}", this);
    try {
      taskanaHistoryEngine.openConnection();
      long result = historyQueryMapper.countHistoryEvent(this);
      LOGGER.debug("transaction was successful. Result: {}", result);
      return result;
    } catch (SQLException e) {
      LOGGER.error(
          "Method openConnection() could not open a connection to the database.", e.getCause());
      return -1;
    } catch (NullPointerException npe) {
      LOGGER.error("No History Event found.");
      return -1;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  private HistoryQueryImpl addOrderCriteria(String columnName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(columnName + orderByDirection);
    orderColumns.add(columnName);
    return this;
  }
}
