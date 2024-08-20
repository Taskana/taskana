package io.kadai.task.internal;

import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.task.api.ObjectReferenceQuery;
import io.kadai.task.api.ObjectReferenceQueryColumnName;
import io.kadai.task.api.models.ObjectReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;

/** Implementation of ObjectReferenceQuery interface. */
public class ObjectReferenceQueryImpl implements ObjectReferenceQuery {

  private static final String LINK_TO_MAPPER =
      "io.kadai.task.internal.ObjectReferenceMapper.queryObjectReferences";
  private static final String LINK_TO_COUNTER =
      "io.kadai.task.internal.ObjectReferenceMapper.countQueryObjectReferences";
  private static final String LINK_TO_VALUEMAPPER =
      "io.kadai.task.internal.ObjectReferenceMapper.queryObjectReferenceColumnValues";
  private final InternalKadaiEngine kadaiEngine;
  private final List<String> orderBy;
  private ObjectReferenceQueryColumnName columnName;
  private String[] company;
  private String[] system;
  private String[] systemInstance;
  private String[] type;
  private String[] value;

  ObjectReferenceQueryImpl(InternalKadaiEngine kadaiEngine) {
    this.kadaiEngine = kadaiEngine;
    this.orderBy = new ArrayList<>();
  }

  @Override
  public ObjectReferenceQuery companyIn(String... companies) {
    this.company = companies;
    return this;
  }

  @Override
  public ObjectReferenceQuery systemIn(String... systems) {
    this.system = systems;
    return this;
  }

  @Override
  public ObjectReferenceQuery systemInstanceIn(String... systemInstances) {
    this.systemInstance = systemInstances;
    return this;
  }

  @Override
  public ObjectReferenceQuery typeIn(String... types) {
    this.type = types;
    return this;
  }

  @Override
  public ObjectReferenceQuery valueIn(String... values) {
    this.value = values;
    return this;
  }

  @Override
  public List<ObjectReference> list() {
    return kadaiEngine.executeInDatabaseConnection(
        () -> kadaiEngine.getSqlSession().selectList(LINK_TO_MAPPER, this));
  }

  @Override
  public List<ObjectReference> list(int offset, int limit) {
    List<ObjectReference> result;
    try {
      kadaiEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      result = kadaiEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
      return result;
    } catch (PersistenceException e) {
      if (e.getMessage().contains("ERRORCODE=-4470")) {
        SystemException ex =
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
      ObjectReferenceQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result;
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
  public ObjectReference single() {
    ObjectReference result = null;
    try {
      kadaiEngine.openConnection();
      result = kadaiEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
      return result;
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

  public String[] getCompany() {
    return company;
  }

  public void setCompany(String[] company) {
    this.company = company;
  }

  public String[] getSystem() {
    return system;
  }

  public void setSystem(String[] system) {
    this.system = system;
  }

  public String[] getSystemInstance() {
    return systemInstance;
  }

  public void setSystemInstance(String[] systemInstance) {
    this.systemInstance = systemInstance;
  }

  public String[] getType() {
    return type;
  }

  public void setType(String[] type) {
    this.type = type;
  }

  public String[] getValue() {
    return value;
  }

  public void setValue(String[] value) {
    this.value = value;
  }

  public ObjectReferenceQueryColumnName getColumnName() {
    return columnName;
  }

  private ObjectReferenceQuery addOrderCriteria(String colName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(colName + orderByDirection);
    return this;
  }

  @Override
  public String toString() {
    return "ObjectReferenceQueryImpl [kadaiEngine="
        + kadaiEngine
        + ", columnName="
        + columnName
        + ", company="
        + Arrays.toString(company)
        + ", system="
        + Arrays.toString(system)
        + ", systemInstance="
        + Arrays.toString(systemInstance)
        + ", type="
        + Arrays.toString(type)
        + ", value="
        + Arrays.toString(value)
        + ", orderBy="
        + orderBy
        + "]";
  }
}
