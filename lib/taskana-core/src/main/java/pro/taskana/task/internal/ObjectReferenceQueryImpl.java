package pro.taskana.task.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.task.api.ObjectReferenceQuery;
import pro.taskana.task.api.ObjectReferenceQueryColumnName;
import pro.taskana.task.api.models.ObjectReference;

/** Implementation of ObjectReferenceQuery interface. */
public class ObjectReferenceQueryImpl implements ObjectReferenceQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.task.internal.ObjectReferenceMapper.queryObjectReferences";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.task.internal.ObjectReferenceMapper.countQueryObjectReferences";
  private static final String LINK_TO_VALUEMAPPER =
      "pro.taskana.task.internal.ObjectReferenceMapper.queryObjectReferenceColumnValues";
  private final InternalTaskanaEngine taskanaEngine;
  private final List<String> orderBy;
  private ObjectReferenceQueryColumnName columnName;
  private String[] company;
  private String[] system;
  private String[] systemInstance;
  private String[] type;
  private String[] value;

  ObjectReferenceQueryImpl(InternalTaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
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
    return taskanaEngine.executeInDatabaseConnection(
        () -> taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this));
  }

  @Override
  public List<ObjectReference> list(int offset, int limit) {
    List<ObjectReference> result;
    try {
      taskanaEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      result = taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
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
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<String> listValues(
      ObjectReferenceQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result;
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
  public ObjectReference single() {
    ObjectReference result = null;
    try {
      taskanaEngine.openConnection();
      result = taskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
      return result;
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
    return "ObjectReferenceQueryImpl [taskanaEngine="
        + taskanaEngine
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
