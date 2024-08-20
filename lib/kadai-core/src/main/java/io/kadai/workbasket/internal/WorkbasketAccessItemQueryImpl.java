package io.kadai.workbasket.internal;

import static io.kadai.common.api.BaseQuery.toLowerCopy;

import io.kadai.common.api.exceptions.KadaiRuntimeException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.workbasket.api.AccessItemQueryColumnName;
import io.kadai.workbasket.api.WorkbasketAccessItemQuery;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import io.kadai.workbasket.internal.models.WorkbasketAccessItemImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;

/** WorkbasketAccessItemQueryImpl for generating dynamic SQL. */
public class WorkbasketAccessItemQueryImpl implements WorkbasketAccessItemQuery {

  private static final String LINK_TO_MAPPER =
      "io.kadai.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketAccessItems";
  private static final String LINK_TO_COUNTER =
      "io.kadai.workbasket.internal.WorkbasketQueryMapper.countQueryWorkbasketAccessItems";
  private static final String LINK_TO_VALUEMAPPER =
      "io.kadai.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketAccessItemColumnValues";
  private final InternalKadaiEngine kadaiEngine;
  private final List<String> orderBy;
  private final List<String> orderColumns;
  private AccessItemQueryColumnName columnName;
  private String[] accessIdIn;
  private String[] accessIdLike;
  private String[] workbasketIdIn;
  private String[] workbasketKeyIn;
  private String[] workbasketKeyLike;
  private String[] idIn;

  WorkbasketAccessItemQueryImpl(InternalKadaiEngine kadaiEngine) {
    this.kadaiEngine = kadaiEngine;
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
    this.workbasketKeyLike = toLowerCopy(key);
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
    this.accessIdLike = toLowerCopy(ids);
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
    return kadaiEngine.executeInDatabaseConnection(
        () -> kadaiEngine.getSqlSession().selectList(LINK_TO_MAPPER, this));
  }

  @Override
  public List<WorkbasketAccessItem> list(int offset, int limit) {
    List<WorkbasketAccessItem> result = new ArrayList<>();
    try {
      kadaiEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      List<WorkbasketAccessItemImpl> foundAccessItms =
          kadaiEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
      result.addAll(foundAccessItms);
      return result;
    } catch (PersistenceException e) {
      if (e.getMessage().contains("ERRORCODE=-4470")) {
        KadaiRuntimeException ex =
            new SystemException(
                "The offset beginning was set over the amount of result-rows.", e.getCause());
        ex.setStackTrace(e.getStackTrace());
        throw ex;
      }
      throw e;
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public List<String> listValues(
      AccessItemQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result = null;
    try {
      kadaiEngine.openConnection();
      this.columnName = columnName;
      this.orderBy.clear();
      this.addOrderCriteria(columnName.toString(), sortDirection);
      result = kadaiEngine.getSqlSession().selectList(LINK_TO_VALUEMAPPER, this);
      return result;
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketAccessItem single() {
    WorkbasketAccessItem accessItm = null;
    try {
      kadaiEngine.openConnection();
      accessItm = kadaiEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
      return accessItm;
    } finally {
      kadaiEngine.returnConnection();
    }
  }

  @Override
  public long count() {
    Long rowCount = null;
    try {
      kadaiEngine.openConnection();
      rowCount = kadaiEngine.getSqlSession().selectOne(LINK_TO_COUNTER, this);
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      kadaiEngine.returnConnection();
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
