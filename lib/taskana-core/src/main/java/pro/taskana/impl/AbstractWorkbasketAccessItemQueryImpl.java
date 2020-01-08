package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.AbstractWorkbasketAccessItemQuery;
import pro.taskana.AccessItemQueryColumnName;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;

/**
 * AbstractWorkbasketAccessItemQueryImpl for generating dynamic SQL.
 *
 * @param <Q> the actual WorkbasketAccessItemQuery behind this abstract class
 * @param <T> the workbasket access item
 */
abstract class AbstractWorkbasketAccessItemQueryImpl<
        Q extends AbstractWorkbasketAccessItemQuery<Q, T>, T extends WorkbasketAccessItem>
    implements AbstractWorkbasketAccessItemQuery<Q, T> {

  private static final String LINK_TO_COUNTER =
      "pro.taskana.mappings.QueryMapper.countQueryWorkbasketAccessItems";

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractWorkbasketAccessItemQueryImpl.class);
  private AccessItemQueryColumnName columnName;
  private String[] accessIdIn;
  private String[] workbasketIdIn;
  private String[] idIn;

  private InternalTaskanaEngine taskanaEngine;
  private List<String> orderBy;
  private List<String> orderColumns;

  AbstractWorkbasketAccessItemQueryImpl(InternalTaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    orderBy = new ArrayList<>();
    orderColumns = new ArrayList<>();
  }

  @Override
  public Q idIn(String... ids) {
    this.idIn = ids;
    return _this();
  }

  @Override
  public Q workbasketIdIn(String... id) {
    this.workbasketIdIn = id;
    return _this();
  }

  @Override
  public Q accessIdIn(String... accessId) {
    this.accessIdIn = accessId;
    WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
    return _this();
  }

  @Override
  public Q orderByWorkbasketId(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_ID", sortDirection);
  }

  @Override
  public Q orderByAccessId(SortDirection sortDirection) {
    return addOrderCriteria("ACCESS_ID", sortDirection);
  }

  @Override
  public Q orderById(SortDirection sortDirection) {
    return addOrderCriteria("ID", sortDirection);
  }

  @Override
  public List<T> list() {
    LOGGER.debug("entry to list(), this = {}", _this());
    List<T> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      List<T> foundAccessItms =
          taskanaEngine.getSqlSession().selectList(getLinkToMapper(), _this());
      result.addAll(foundAccessItms);
      return result;
    } finally {
      taskanaEngine.returnConnection();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "exit from list(). Returning {} resulting Objects: {} ",
            result.size(),
            LoggerUtils.listToString(result));
      }
    }
  }

  @Override
  public List<T> list(int offset, int limit) {
    LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, _this());
    List<T> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      List<T> foundAccessItms =
          taskanaEngine.getSqlSession().selectList(getLinkToMapper(), _this(), rowBounds);
      result.addAll(foundAccessItms);
      return result;
    } catch (PersistenceException e) {
      if (e.getMessage().contains("ERRORCODE=-4470")) {
        TaskanaRuntimeException ex =
            new TaskanaRuntimeException(
                "The offset beginning was set over the amount of result-rows.", e.getCause());
        ex.setStackTrace(e.getStackTrace());
        throw ex;
      }
      throw e;
    } finally {
      taskanaEngine.returnConnection();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "exit from list(offset,limit). Returning {} resulting Objects: {} ",
            result.size(),
            LoggerUtils.listToString(result));
      }
    }
  }

  @Override
  public List<String> listValues(
      AccessItemQueryColumnName columnName, SortDirection sortDirection) {
    LOGGER.debug("Entry to listValues(dbColumnName={}) this = {}", columnName, _this());
    List<String> result = null;
    try {
      taskanaEngine.openConnection();
      this.columnName = columnName;
      this.orderBy.clear();
      this.addOrderCriteria(columnName.toString(), sortDirection);
      result = taskanaEngine.getSqlSession().selectList(getLinkToValueMapper(), _this());
      return result;
    } finally {
      taskanaEngine.returnConnection();
      if (LOGGER.isDebugEnabled()) {
        int numberOfResultObjects = result == null ? 0 : result.size();
        LOGGER.debug(
            "Exit from listValues. Returning {} resulting Objects: {} ",
            numberOfResultObjects,
            LoggerUtils.listToString(result));
      }
    }
  }

  @Override
  public T single() {
    LOGGER.debug("entry to single(), this = {}", _this());
    T accessItem = null;
    try {
      taskanaEngine.openConnection();
      accessItem = taskanaEngine.getSqlSession().selectOne(getLinkToMapper(), _this());
      return accessItem;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from single(). Returning result {} ", accessItem);
    }
  }

  @Override
  public long count() {
    LOGGER.debug("entry to count(), this = {}", _this());
    Long rowCount = null;
    try {
      taskanaEngine.openConnection();
      rowCount = taskanaEngine.getSqlSession().selectOne(LINK_TO_COUNTER, _this());
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from count(). Returning result {} ", rowCount);
    }
  }

  public String[] getIdIn() {
    return this.idIn;
  }

  public String[] getAccessIdIn() {
    return accessIdIn;
  }

  public String[] getWorkbasketIdIn() {
    return workbasketIdIn;
  }

  public List<String> getOrderBy() {
    return orderBy;
  }

  public List<String> getOrderColumns() {
    return orderColumns;
  }

  public AccessItemQueryColumnName getColumnName() {
    return columnName;
  }

  protected Q addOrderCriteria(String colName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(colName + orderByDirection);
    orderColumns.add(colName);
    return _this();
  }

  @SuppressWarnings("checkstyle:MethodName")
  abstract Q _this();

  abstract String getLinkToMapper();

  abstract String getLinkToValueMapper();

  @Override
  public String toString() {
    return "AbstractWorkbasketAccessItemQueryImpl ["
        + "idIn="
        + Arrays.toString(this.idIn)
        + ", accessIdIn="
        + Arrays.toString(this.accessIdIn)
        + ", workbasketIdIn="
        + Arrays.toString(this.workbasketIdIn)
        + ", orderBy="
        + this.orderBy
        + "]";
  }
}
