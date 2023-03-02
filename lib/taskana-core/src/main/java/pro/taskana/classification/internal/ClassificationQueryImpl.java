package pro.taskana.classification.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationQuery;
import pro.taskana.classification.api.ClassificationQueryColumnName;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.InternalTaskanaEngine;

/** Implementation of ClassificationQuery interface. */
public class ClassificationQueryImpl implements ClassificationQuery {

  private static final String LINK_TO_SUMMARYMAPPER =
      "pro.taskana.classification.internal.ClassificationQueryMapper.queryClassificationSummaries";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.classification.internal.ClassificationQueryMapper.countQueryClassifications";
  private static final String LINK_TO_VALUEMAPPER =
      "pro.taskana.classification.internal.ClassificationQueryMapper."
          + "queryClassificationColumnValues";

  private final InternalTaskanaEngine taskanaEngine;
  private final List<String> orderBy;
  private final List<String> orderColumns;
  private ClassificationQueryColumnName columnName;
  @Getter private String[] key;
  @Getter private String[] idIn;
  @Getter private String[] parentId;
  @Getter private String[] parentKey;
  @Getter private String[] category;
  @Getter private String[] type;
  @Getter private String[] domain;
  @Getter private Boolean validInDomain;
  @Getter private TimeInterval[] createdIn;
  @Getter private TimeInterval[] modifiedIn;
  @Getter private String[] nameIn;
  @Getter private String[] nameLike;
  @Getter private String descriptionLike;
  @Getter private int[] priority;
  @Getter private String[] serviceLevelIn;
  @Getter private String[] serviceLevelLike;
  @Getter private String[] applicationEntryPointIn;
  @Getter private String[] applicationEntryPointLike;
  @Getter private String[] custom1In;
  @Getter private String[] custom1Like;
  @Getter private String[] custom2In;
  @Getter private String[] custom2Like;
  @Getter private String[] custom3In;
  @Getter private String[] custom3Like;
  @Getter private String[] custom4In;
  @Getter private String[] custom4Like;
  @Getter private String[] custom5In;
  @Getter private String[] custom5Like;
  @Getter private String[] custom6In;
  @Getter private String[] custom6Like;
  @Getter private String[] custom7In;
  @Getter private String[] custom7Like;
  @Getter private String[] custom8In;
  @Getter private String[] custom8Like;

  ClassificationQueryImpl(InternalTaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    this.orderBy = new ArrayList<>();
    this.orderColumns = new ArrayList<>();
  }

  @Override
  public ClassificationQuery keyIn(String... keys) {
    this.key = keys;
    return this;
  }

  @Override
  public ClassificationQuery idIn(String... ids) {
    this.idIn = ids;
    return this;
  }

  @Override
  public ClassificationQuery parentIdIn(String... parentIds) {
    this.parentId = parentIds;
    return this;
  }

  @Override
  public ClassificationQuery parentKeyIn(String... parentKeys) {
    this.parentKey = parentKeys;
    return this;
  }

  @Override
  public ClassificationQuery categoryIn(String... categories) {
    this.category = categories;
    return this;
  }

  @Override
  public ClassificationQuery typeIn(String... types) {
    this.type = types;
    return this;
  }

  @Override
  public ClassificationQuery domainIn(String... domains) {
    this.domain = domains;
    return this;
  }

  @Override
  public ClassificationQuery validInDomainEquals(Boolean validInDomain) {
    this.validInDomain = validInDomain;
    return this;
  }

  @Override
  public ClassificationQuery createdWithin(TimeInterval... createdIn) {
    validateAllTimeIntervals(createdIn);
    this.createdIn = createdIn;
    return this;
  }

  @Override
  public ClassificationQuery modifiedWithin(TimeInterval... modifiedIn) {
    validateAllTimeIntervals(modifiedIn);
    this.modifiedIn = modifiedIn;
    return this;
  }

  @Override
  public ClassificationQuery nameIn(String... names) {
    this.nameIn = names;
    return this;
  }

  @Override
  public ClassificationQuery nameLike(String... names) {
    this.nameLike = toLowerCopy(names);
    return this;
  }

  @Override
  public ClassificationQuery descriptionLike(String descriptions) {
    this.descriptionLike = descriptions.toLowerCase();
    return this;
  }

  @Override
  public ClassificationQuery priorityIn(int... priorities) {
    this.priority = priorities;
    return this;
  }

  @Override
  public ClassificationQuery serviceLevelIn(String... serviceLevels) {
    this.serviceLevelIn = serviceLevels;
    return this;
  }

  @Override
  public ClassificationQuery serviceLevelLike(String... serviceLevels) {
    this.serviceLevelLike = toLowerCopy(serviceLevels);
    return this;
  }

  @Override
  public ClassificationQuery applicationEntryPointIn(String... applicationEntryPoints) {
    this.applicationEntryPointIn = applicationEntryPoints;
    return this;
  }

  @Override
  public ClassificationQuery applicationEntryPointLike(String... applicationEntryPoints) {
    this.applicationEntryPointLike = toLowerCopy(applicationEntryPoints);
    return this;
  }

  @Override
  public ClassificationQuery customAttributeIn(
      ClassificationCustomField customField, String... values) throws InvalidArgumentException {
    if (values.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }

    switch (customField) {
      case CUSTOM_1:
        this.custom1In = values;
        break;
      case CUSTOM_2:
        this.custom2In = values;
        break;
      case CUSTOM_3:
        this.custom3In = values;
        break;
      case CUSTOM_4:
        this.custom4In = values;
        break;
      case CUSTOM_5:
        this.custom5In = values;
        break;
      case CUSTOM_6:
        this.custom6In = values;
        break;
      case CUSTOM_7:
        this.custom7In = values;
        break;
      case CUSTOM_8:
        this.custom8In = values;
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }

    return this;
  }

  @Override
  public ClassificationQuery customAttributeLike(
      ClassificationCustomField customField, String... values) throws InvalidArgumentException {
    if (values.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }

    switch (customField) {
      case CUSTOM_1:
        this.custom1Like = toLowerCopy(values);
        break;
      case CUSTOM_2:
        this.custom2Like = toLowerCopy(values);
        break;
      case CUSTOM_3:
        this.custom3Like = toLowerCopy(values);
        break;
      case CUSTOM_4:
        this.custom4Like = toLowerCopy(values);
        break;
      case CUSTOM_5:
        this.custom5Like = toLowerCopy(values);
        break;
      case CUSTOM_6:
        this.custom6Like = toLowerCopy(values);
        break;
      case CUSTOM_7:
        this.custom7Like = toLowerCopy(values);
        break;
      case CUSTOM_8:
        this.custom8Like = toLowerCopy(values);
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }

    return this;
  }

  @Override
  public ClassificationQuery orderByKey(SortDirection sortDirection) {
    return addOrderCriteria("KEY", sortDirection);
  }

  @Override
  public ClassificationQuery orderByParentId(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_ID", sortDirection);
  }

  @Override
  public ClassificationQuery orderByParentKey(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_KEY", sortDirection);
  }

  @Override
  public ClassificationQuery orderByCategory(SortDirection sortDirection) {
    return addOrderCriteria("CATEGORY", sortDirection);
  }

  @Override
  public ClassificationQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public ClassificationQuery orderByName(SortDirection sortDirection) {
    return addOrderCriteria("NAME", sortDirection);
  }

  @Override
  public ClassificationQuery orderByServiceLevel(SortDirection sortDirection) {
    return addOrderCriteria("SERVICE_LEVEL", sortDirection);
  }

  @Override
  public ClassificationQuery orderByPriority(SortDirection sortDirection) {
    return addOrderCriteria("PRIORITY", sortDirection);
  }

  @Override
  public ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection) {
    return addOrderCriteria("APPLICATION_ENTRY_POINT", sortDirection);
  }

  @Override
  public ClassificationQuery orderByCustomAttribute(
      ClassificationCustomField customField, SortDirection sortDirection) {
    return addOrderCriteria(customField.name(), sortDirection);
  }

  @Override
  public List<ClassificationSummary> list() {
    return taskanaEngine.executeInDatabaseConnection(
        () -> taskanaEngine.getSqlSession().selectList(LINK_TO_SUMMARYMAPPER, this));
  }

  @Override
  public List<ClassificationSummary> list(int offset, int limit) {
    List<ClassificationSummary> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      result = taskanaEngine.getSqlSession().selectList(LINK_TO_SUMMARYMAPPER, this, rowBounds);
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
      ClassificationQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result = new ArrayList<>();
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
  public ClassificationSummary single() {
    ClassificationSummary result = null;
    try {
      taskanaEngine.openConnection();
      result = taskanaEngine.getSqlSession().selectOne(LINK_TO_SUMMARYMAPPER, this);
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

  public ClassificationQueryColumnName getColumnName() {
    return columnName;
  }

  public List<String> getOrderBy() {
    return orderBy;
  }

  public List<String> getOrderColumns() {
    return orderColumns;
  }

  private void validateAllTimeIntervals(TimeInterval[] createdIn) {
    for (TimeInterval ti : createdIn) {
      if (!ti.isValid()) {
        throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
      }
    }
  }

  private ClassificationQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(columnName + orderByDirection);
    orderColumns.add(columnName);
    return this;
  }

  @Override
  public String toString() {
    return "ClassificationQueryImpl ["
        + "columnName= "
        + this.columnName
        + ", key= "
        + Arrays.toString(this.key)
        + ", idIn= "
        + Arrays.toString(this.idIn)
        + ", parentId= "
        + Arrays.toString(this.parentId)
        + ", parentKey= "
        + Arrays.toString(this.parentKey)
        + ", category= "
        + Arrays.toString(this.category)
        + ", type= "
        + Arrays.toString(this.type)
        + ", domain= "
        + Arrays.toString(this.domain)
        + ", validInDomain= "
        + this.validInDomain
        + ", createdIn= "
        + Arrays.toString(this.createdIn)
        + ", modifiedIn= "
        + Arrays.toString(this.modifiedIn)
        + ", nameIn= "
        + Arrays.toString(this.nameIn)
        + ", nameLike= "
        + Arrays.toString(this.nameLike)
        + ", descriptionLike= "
        + this.descriptionLike
        + ", priority= "
        + Arrays.toString(this.priority)
        + ", serviceLevelIn= "
        + Arrays.toString(this.serviceLevelIn)
        + ", serviceLevelLike= "
        + Arrays.toString(this.serviceLevelLike)
        + ", applicationEntryPointIn= "
        + Arrays.toString(this.applicationEntryPointIn)
        + ", applicationEntryPointLike= "
        + Arrays.toString(this.applicationEntryPointLike)
        + ", custom1In= "
        + Arrays.toString(this.custom1In)
        + ", custom1Like= "
        + Arrays.toString(this.custom1Like)
        + ", custom2In= "
        + Arrays.toString(this.custom2In)
        + ", custom2Like= "
        + Arrays.toString(this.custom2Like)
        + ", custom3In= "
        + Arrays.toString(this.custom3In)
        + ", custom3Like= "
        + Arrays.toString(this.custom3Like)
        + ", custom4In= "
        + Arrays.toString(this.custom4In)
        + ", custom4Like= "
        + Arrays.toString(this.custom4Like)
        + ", custom5In= "
        + Arrays.toString(this.custom5In)
        + ", custom5Like= "
        + Arrays.toString(this.custom5Like)
        + ", custom6In= "
        + Arrays.toString(this.custom6In)
        + ", custom6Like= "
        + Arrays.toString(this.custom6Like)
        + ", custom7In= "
        + Arrays.toString(this.custom7In)
        + ", custom7Like= "
        + Arrays.toString(this.custom7Like)
        + ", custom8In= "
        + Arrays.toString(this.custom8In)
        + ", custom8Like= "
        + Arrays.toString(this.custom8Like)
        + ", orderBy= "
        + this.orderBy
        + "]";
  }
}
