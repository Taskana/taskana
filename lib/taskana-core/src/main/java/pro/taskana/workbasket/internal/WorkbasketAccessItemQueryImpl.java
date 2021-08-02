package pro.taskana.workbasket.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.workbasket.api.AccessItemQueryColumnName;
import pro.taskana.workbasket.api.WorkbasketAccessItemQuery;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

/** WorkbasketAccessItemQueryImpl for generating dynamic SQL. */
public class WorkbasketAccessItemQueryImpl implements WorkbasketAccessItemQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketAccessItems";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.workbasket.internal.WorkbasketQueryMapper.countQueryWorkbasketAccessItems";
  private static final String LINK_TO_VALUEMAPPER =
      "pro.taskana.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketAccessItemColumnValues";
  private AccessItemQueryColumnName columnName;
  private String[] accessIdIn;
  private String[] accessIdLike;
  private String[] workbasketIdIn;
  private String[] workbasketKeyIn;
  private String[] workbasketKeyLike;
  private String[] idIn;

  private final InternalTaskanaEngine taskanaEngine;
  private final List<String> orderBy;
  private final List<String> orderColumns;

  WorkbasketAccessItemQueryImpl(InternalTaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    orderBy = new ArrayList<>();
    orderColumns = new ArrayList<>();
  }

  @Override
  public WorkbasketAccessItemQuery idIn(String... ids) {
    this.idIn = ids;
    return this;
  }

  @Override
  public WorkbasketAccessItemQuery workbasketIdIn(String... id) {
    this.workbasketIdIn = id;
    return this;
  }

  @Override
  public WorkbasketAccessItemQuery workbasketKeyIn(String... keys) {
    this.workbasketKeyIn = keys;
    return this;
  }

  @Override
  public WorkbasketAccessItemQuery workbasketKeyLike(String... key) {
    this.workbasketKeyLike = toUpperCopy(key);
    return this;
  }

  @Override
  public WorkbasketAccessItemQuery accessIdIn(String... accessId) {
    this.accessIdIn = accessId;
    WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
    return this;
  }

  @Override
  public WorkbasketAccessItemQuery accessIdLike(String... ids) {
    this.accessIdLike = toUpperCopy(ids);
    return this;
  }

  @Override
  public WorkbasketAccessItemQuery orderByWorkbasketId(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_ID", sortDirection);
  }

  @Override
  public WorkbasketAccessItemQuery orderByWorkbasketKey(SortDirection sortDirection) {
    return addOrderCriteria("WB.KEY", sortDirection);
  }

  @Override
  public WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection) {
    return addOrderCriteria("ACCESS_ID", sortDirection);
  }

  @Override
  public WorkbasketAccessItemQuery orderById(SortDirection sortDirection) {
    return addOrderCriteria("ID", sortDirection);
  }

  @Override
  public List<WorkbasketAccessItem> list() {
    return taskanaEngine.executeInDatabaseConnection(
        () -> taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this));
  }

  @Override
  public List<WorkbasketAccessItem> list(int offset, int limit) {
    List<WorkbasketAccessItem> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      List<WorkbasketAccessItemImpl> foundAccessItms =
          taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
      result.addAll(foundAccessItms);
      return result;
    } catch (PersistenceException e) {
      if (e.getMessage().contains("ERRORCODE=-4470")) {
        TaskanaRuntimeException ex =
            new SystemException(
                "The offset beginning was set over the amount of result-rows.", e.getCause());
        ex.setStackTrace(e.getStackTrace());
        throw ex;
      }
      throw e;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<String> listValues(
      AccessItemQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result = null;
    try {
      taskanaEngine.openConnection();
      this.columnName = columnName;
      this.orderBy.clear();
      this.addOrderCriteria(columnName.toString(), sortDirection);
      result = taskanaEngine.getSqlSession().selectList(LINK_TO_VALUEMAPPER, this);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketAccessItem single() {
    WorkbasketAccessItem accessItm = null;
    try {
      taskanaEngine.openConnection();
      accessItm = taskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
      return accessItm;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public long count() {
    Long rowCount = null;
    try {
      taskanaEngine.openConnection();
      rowCount = taskanaEngine.getSqlSession().selectOne(LINK_TO_COUNTER, this);
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  public String[] getIdIn() {
    return this.idIn;
  }

  public String[] getAccessIdIn() {
    return accessIdIn;
  }

  public String[] getAccessIdLike() {
    return accessIdLike;
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

  public String[] getWorkbasketKeyIn() {
    return workbasketKeyIn;
  }

  public String[] getWorkbasketKeyLike() {
    return workbasketKeyLike;
  }

  private WorkbasketAccessItemQuery addOrderCriteria(String colName, SortDirection sortDirection) {
    String orderByDirection =
        " "
            + (sortDirection == null
                ? SortDirection.ASCENDING.toString()
                : sortDirection.toString());
    orderBy.add(colName + orderByDirection);
    orderColumns.add(colName);
    return this;
  }

  @Override
  public String toString() {
    return "WorkbasketAccessItemQueryImpl ["
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
