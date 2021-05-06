package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQuery;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryColumnName;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.workbasket.api.WorkbasketCustomField;

public class WorkbasketHistoryQueryImpl implements WorkbasketHistoryQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper.queryHistoryEvents";
  private static final String LINK_TO_VALUE_MAPPER =
      "pro.taskana.simplehistory.impl.workbasket."
          + "WorkbasketHistoryQueryMapper.queryHistoryColumnValues";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper.countHistoryEvents";
  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketHistoryQueryImpl.class);

  private static final String SQL_EXCEPTION_MESSAGE =
      "Method openConnection() could not open a connection to the database.";

  private TaskanaHistoryEngineImpl taskanaHistoryEngine;

  private WorkbasketHistoryQueryColumnName columnName;
  private List<String> orderBy;
  private List<String> orderColumns;

  private String[] idIn;
  private String[] workbasketIdIn;
  private String[] eventTypeIn;
  private TimeInterval[] createdIn;
  private String[] userIdIn;
  private String[] domainIn;
  private String[] keyIn;
  private String[] typeIn;
  private String[] ownerIn;
  private String[] custom1In;
  private String[] custom2In;
  private String[] custom3In;
  private String[] custom4In;
  private String[] orgLevel1In;
  private String[] orgLevel2In;
  private String[] orgLevel3In;
  private String[] orgLevel4In;

  private String[] workbasketIdLike;
  private String[] eventTypeLike;
  private String[] userIdLike;
  private String[] domainLike;
  private String[] keyLike;
  private String[] typeLike;
  private String[] ownerLike;
  private String[] custom1Like;
  private String[] custom2Like;
  private String[] custom3Like;
  private String[] custom4Like;
  private String[] orgLevel1Like;
  private String[] orgLevel2Like;
  private String[] orgLevel3Like;
  private String[] orgLevel4Like;

  public WorkbasketHistoryQueryImpl(TaskanaHistoryEngineImpl internalTaskanaHistoryEngine) {
    this.taskanaHistoryEngine = internalTaskanaHistoryEngine;
    this.orderBy = new ArrayList<>();
    this.orderColumns = new ArrayList<>();
  }

  public String[] getIdIn() {
    return idIn;
  }

  public String[] getWorkbasketIdIn() {
    return workbasketIdIn;
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

  public String[] getKeyIn() {
    return keyIn;
  }

  public String[] getTypeIn() {
    return typeIn;
  }

  public String[] getOwnerIn() {
    return ownerIn;
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

  public String[] getOrgLevel1In() {
    return orgLevel1In;
  }

  public String[] getOrgLevel2In() {
    return orgLevel2In;
  }

  public String[] getOrgLevel3In() {
    return orgLevel3In;
  }

  public String[] getOrgLevel4In() {
    return orgLevel4In;
  }

  public String[] getWorkbasketIdLike() {
    return workbasketIdLike;
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

  public String[] getKeyLike() {
    return keyLike;
  }

  public String[] getTypeLike() {
    return typeLike;
  }

  public String[] getOwnerLike() {
    return ownerLike;
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

  public String[] getOrgLevel1Like() {
    return orgLevel1Like;
  }

  public String[] getOrgLevel2Like() {
    return orgLevel2Like;
  }

  public String[] getOrgLevel3Like() {
    return orgLevel3Like;
  }

  public String[] getOrgLevel4Like() {
    return orgLevel4Like;
  }

  @Override
  public WorkbasketHistoryQuery idIn(String... idIn) {
    this.idIn = toUpperCopy(idIn);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery workbasketIdIn(String... workbasketId) {
    this.workbasketIdIn = toUpperCopy(workbasketId);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery eventTypeIn(String... eventType) {
    this.eventTypeIn = toUpperCopy(eventType);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery createdWithin(TimeInterval... createdIn) {
    this.createdIn = createdIn;
    return this;
  }

  @Override
  public WorkbasketHistoryQuery userIdIn(String... userId) {
    this.userIdIn = toUpperCopy(userId);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery domainIn(String... domain) {
    this.domainIn = toUpperCopy(domain);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery keyIn(String... workbasketKey) {
    this.keyIn = toUpperCopy(workbasketKey);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery typeIn(String... workbasketType) {
    this.typeIn = toUpperCopy(workbasketType);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery ownerIn(String... oownerIn) {
    this.ownerIn = toUpperCopy(ownerIn);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel1In(String... orgLevel1) {
    this.orgLevel1In = toUpperCopy(orgLevel1);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel2In(String... orgLevel2) {
    this.orgLevel2In = toUpperCopy(orgLevel2);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel3In(String... orgLevel3) {
    this.orgLevel3In = toUpperCopy(orgLevel3);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel4In(String... orgLevel4) {
    this.orgLevel4In = toUpperCopy(orgLevel4);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery customAttributeIn(
      WorkbasketCustomField customField, String... searchArguments) {
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
  public WorkbasketHistoryQuery customAttributeLike(
      WorkbasketCustomField customField, String... searchArguments) {
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
  public WorkbasketHistoryQuery workbasketIdLike(String... workbasketId) {
    this.workbasketIdLike = toUpperCopy(workbasketId);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery eventTypeLike(String... eventType) {
    this.eventTypeLike = toUpperCopy(eventType);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery userIdLike(String... userId) {
    this.userIdLike = toUpperCopy(userId);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery domainLike(String... domain) {
    this.domainLike = toUpperCopy(domain);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery workbasketKeyLike(String... workbasketKey) {
    this.keyLike = toUpperCopy(workbasketKey);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery workbasketTypeLike(String... workbasketType) {
    this.typeLike = toUpperCopy(workbasketType);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery ownerLike(String... ownerLike) {
    this.ownerLike = toUpperCopy(ownerLike);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel1Like(String... orgLevel1) {
    this.orgLevel1Like = toUpperCopy(orgLevel1);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel2Like(String... orgLevel2) {
    this.orgLevel2Like = toUpperCopy(orgLevel2);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel3Like(String... orgLevel3) {
    this.orgLevel3Like = toUpperCopy(orgLevel3);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orgLevel4Like(String... orgLevel4) {
    this.orgLevel4Like = toUpperCopy(orgLevel4);
    return this;
  }

  @Override
  public WorkbasketHistoryQuery orderByWorkbasketId(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_ID", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByEventType(SortDirection sortDirection) {
    return addOrderCriteria("EVENT_TYPE", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByCreated(SortDirection sortDirection) {
    return addOrderCriteria("CREATED", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByUserId(SortDirection sortDirection) {
    return addOrderCriteria("USER_ID", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByKey(SortDirection sortDirection) {
    return addOrderCriteria("KEY", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByType(SortDirection sortDirection) {
    return addOrderCriteria("TYPE", sortDirection);
  }

  @Override
  public WorkbasketHistoryQuery orderByCustomAttribute(int num, SortDirection sortDirection)
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
  public WorkbasketHistoryQuery orderByOrgLevel(int num, SortDirection sortDirection)
      throws InvalidArgumentException {

    switch (num) {
      case 1:
        return addOrderCriteria("ORGLEVEL_1", sortDirection);
      case 2:
        return addOrderCriteria("ORGLEVEL_2", sortDirection);
      case 3:
        return addOrderCriteria("ORGLEVEL_3", sortDirection);
      case 4:
        return addOrderCriteria("ORGLEVEL_4", sortDirection);
      default:
        throw new InvalidArgumentException(
            "Org number has to be between 1 and 4, but this is: " + num);
    }
  }

  @Override
  public List<WorkbasketHistoryEvent> list() {
    List<WorkbasketHistoryEvent> result = new ArrayList<>();
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
  public List<WorkbasketHistoryEvent> list(int offset, int limit) {
    List<WorkbasketHistoryEvent> result = new ArrayList<>();
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
      WorkbasketHistoryQueryColumnName dbColumnName, SortDirection sortDirection) {
    List<String> result = new ArrayList<>();
    this.columnName = dbColumnName;
    List<String> cacheOrderBy = this.orderBy;
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
      this.orderBy = cacheOrderBy;
      this.columnName = null;
      this.orderColumns.remove(orderColumns.size() - 1);
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketHistoryEvent single() {
    WorkbasketHistoryEvent result = null;
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
    Long rowCount = null;
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

  private WorkbasketHistoryQueryImpl addOrderCriteria(
      String columnName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(columnName + orderByDirection);
    orderColumns.add(columnName);
    return this;
  }
}
