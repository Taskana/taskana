package pro.taskana.simplehistory.impl;

import static pro.taskana.common.api.BaseQuery.toLowerCopy;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryQuery;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryQueryColumnName;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;

public class ClassificationHistoryQueryImpl implements ClassificationHistoryQuery {

  private static final String CLASSIFICATION_PACKAGE_PATH =
      "pro.taskana.simplehistory.impl.classification.";
  private static final String LINK_TO_MAPPER =
      CLASSIFICATION_PACKAGE_PATH + "ClassificationHistoryQueryMapper.queryHistoryEvents";
  private static final String LINK_TO_VALUE_MAPPER =
      CLASSIFICATION_PACKAGE_PATH + "ClassificationHistoryQueryMapper.queryHistoryColumnValues";
  private static final String LINK_TO_COUNTER =
      CLASSIFICATION_PACKAGE_PATH + "ClassificationHistoryQueryMapper.countHistoryEvents";

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClassificationHistoryQueryImpl.class);

  private static final String SQL_EXCEPTION_MESSAGE =
      "Method openConnection() could not open a connection to the database.";

  private final InternalTaskanaEngine internalTaskanaEngine;

  private final List<String> orderBy = new ArrayList<>();
  private final List<String> orderColumns = new ArrayList<>();
  private ClassificationHistoryQueryColumnName columnName;
  private String[] idIn;
  private String[] eventTypeIn;
  private TimeInterval[] createdIn;
  private String[] userIdIn;
  private String[] classificationIdIn;
  private String[] applicationEntryPointIn;
  private String[] categoryIn;
  private String[] domainIn;
  private String[] keyIn;
  private String[] nameIn;
  private String[] parentIdIn;
  private String[] parentKeyIn;
  private int[] priorityIn;
  private String[] serviceLevelIn;
  private String[] typeIn;
  private String[] custom1In;
  private String[] custom2In;
  private String[] custom3In;
  private String[] custom4In;
  private String[] custom5In;
  private String[] custom6In;
  private String[] custom7In;
  private String[] custom8In;
  private String[] eventTypeLike;
  private String[] userIdLike;
  private String[] classificationIdLike;
  private String[] applicationEntryPointLike;
  private String[] categoryLike;
  private String[] domainLike;
  private String[] keyLike;
  private String[] nameLike;
  private String[] parentIdLike;
  private String[] parentKeyLike;
  private String[] serviceLevelLike;
  private String[] typeLike;
  private String[] custom1Like;
  private String[] custom2Like;
  private String[] custom3Like;
  private String[] custom4Like;
  private String[] custom5Like;
  private String[] custom6Like;
  private String[] custom7Like;
  private String[] custom8Like;

  public ClassificationHistoryQueryImpl(InternalTaskanaEngine internalTaskanaEngine) {
    this.internalTaskanaEngine = internalTaskanaEngine;
  }

  @Override
  public ClassificationHistoryQuery idIn(String... idIn) {
    this.idIn = idIn;
    return this;
  }

  @Override
  public ClassificationHistoryQuery eventTypeIn(String... eventType) {
    this.eventTypeIn = eventType;
    return this;
  }

  @Override
  public ClassificationHistoryQuery createdWithin(TimeInterval... createdWithin) {
    this.createdIn = createdWithin;
    return this;
  }

  @Override
  public ClassificationHistoryQuery userIdIn(String... userId) {
    this.userIdIn = userId;
    return this;
  }

  @Override
  public ClassificationHistoryQuery classificationIdIn(String... classificationId) {
    this.classificationIdIn = classificationId;
    return this;
  }

  @Override
  public ClassificationHistoryQuery applicationEntryPointIn(String... applicationEntryPoint) {
    this.applicationEntryPointIn = applicationEntryPoint;
    return this;
  }

  @Override
  public ClassificationHistoryQuery categoryIn(String... category) {
    this.categoryIn = category;
    return this;
  }

  @Override
  public ClassificationHistoryQuery domainIn(String... domain) {
    this.domainIn = domain;
    return this;
  }

  @Override
  public ClassificationHistoryQuery keyIn(String... key) {
    this.keyIn = key;
    return this;
  }

  @Override
  public ClassificationHistoryQuery nameIn(String... name) {
    this.nameIn = name;
    return this;
  }

  @Override
  public ClassificationHistoryQuery parentIdIn(String... parentId) {
    this.parentIdIn = parentId;
    return this;
  }

  @Override
  public ClassificationHistoryQuery parentKeyIn(String... parentKey) {
    this.parentKeyIn = parentKey;
    return this;
  }

  @Override
  public ClassificationHistoryQuery priorityIn(int... priorities) {
    this.priorityIn = priorities;
    return this;
  }

  @Override
  public ClassificationHistoryQuery serviceLevelIn(String... serviceLevelIn) {
    this.serviceLevelIn = serviceLevelIn;
    return this;
  }

  @Override
  public ClassificationHistoryQuery typeIn(String... type) {
    this.typeIn = type;
    return this;
  }

  @Override
  public ClassificationHistoryQuery customAttributeIn(
      ClassificationCustomField customField, String... searchArguments) {
    switch (customField) {
      case CUSTOM_1:
        custom1In = searchArguments;
        break;
      case CUSTOM_2:
        custom2In = searchArguments;
        break;
      case CUSTOM_3:
        custom3In = searchArguments;
        break;
      case CUSTOM_4:
        custom4In = searchArguments;
        break;
      case CUSTOM_5:
        custom5In = searchArguments;
        break;
      case CUSTOM_6:
        custom6In = searchArguments;
        break;
      case CUSTOM_7:
        custom7In = searchArguments;
        break;
      case CUSTOM_8:
        custom8In = searchArguments;
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
    return this;
  }

  @Override
  public ClassificationHistoryQuery eventTypeLike(String... eventType) {
    this.eventTypeLike = toLowerCopy(eventType);
    return this;
  }

  @Override
  public ClassificationHistoryQuery userIdLike(String... userId) {
    this.userIdLike = toLowerCopy(userId);
    return this;
  }

  @Override
  public ClassificationHistoryQuery classificationIdLike(String... classificationId) {
    this.classificationIdLike = toLowerCopy(classificationId);
    return this;
  }

  @Override
  public ClassificationHistoryQuery applicationEntryPointLike(String... applicationEntryPointLike) {
    this.applicationEntryPointLike = toLowerCopy(applicationEntryPointLike);
    return this;
  }

  @Override
  public ClassificationHistoryQuery categoryLike(String... category) {
    this.categoryLike = toLowerCopy(category);
    return this;
  }

  @Override
  public ClassificationHistoryQuery domainLike(String... domain) {
    this.domainLike = toLowerCopy(domain);
    return this;
  }

  @Override
  public ClassificationHistoryQuery keyLike(String... key) {
    this.keyLike = toLowerCopy(key);
    return this;
  }

  @Override
  public ClassificationHistoryQuery nameLike(String... name) {
    this.nameLike = toLowerCopy(name);
    return this;
  }

  @Override
  public ClassificationHistoryQuery parentIdLike(String... parentId) {
    this.parentIdLike = toLowerCopy(parentId);
    return this;
  }

  @Override
  public ClassificationHistoryQuery parentKeyLike(String... parentKey) {
    this.parentKeyLike = toLowerCopy(parentKey);
    return this;
  }

  @Override
  public ClassificationHistoryQuery serviceLevelLike(String... serviceLevel) {
    this.serviceLevelLike = toLowerCopy(serviceLevel);
    return this;
  }

  @Override
  public ClassificationHistoryQuery typeLike(String... type) {
    this.typeLike = toLowerCopy(type);
    return this;
  }

  @Override
  public ClassificationHistoryQuery customAttributeLike(
      ClassificationCustomField customField, String... searchArguments) {
    switch (customField) {
      case CUSTOM_1:
        custom1Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_2:
        custom2Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_3:
        custom3Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_4:
        custom4Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_5:
        custom5Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_6:
        custom6Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_7:
        custom7Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_8:
        custom8Like = toLowerCopy(searchArguments);
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
    return this;
  }

  @Override
  public ClassificationHistoryQuery orderByEventType(SortDirection sortDirection) {
    return addOrderCriteria("EVENT_TYPE", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByCreated(SortDirection sortDirection) {
    return addOrderCriteria("CREATED", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByUserId(SortDirection sortDirection) {
    return addOrderCriteria("USER_ID", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByClassificationId(SortDirection sortDirection) {
    return addOrderCriteria("CLASSIFICATION_ID", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByApplicationEntryPoint(SortDirection sortDirection) {
    return addOrderCriteria("APPLICATION_ENTRY_POINT", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByCategory(SortDirection sortDirection) {
    return addOrderCriteria("CATEGORY", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByKey(SortDirection sortDirection) {
    return addOrderCriteria("KEY", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByName(SortDirection sortDirection) {
    return addOrderCriteria("NAME", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByParentId(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_ID", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByParentKey(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_KEY", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByPriority(SortDirection sortDirection) {
    return addOrderCriteria("PRIORITY", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByServiceLevel(SortDirection sortDirection) {
    return addOrderCriteria("SERVICE_LEVEL", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByType(SortDirection sortDirection) {
    return addOrderCriteria("TYPE", sortDirection);
  }

  @Override
  public ClassificationHistoryQuery orderByCustomAttribute(int num, SortDirection sortDirection)
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
      case 5:
        return addOrderCriteria("CUSTOM_5", sortDirection);
      case 6:
        return addOrderCriteria("CUSTOM_6", sortDirection);
      case 7:
        return addOrderCriteria("CUSTOM_7", sortDirection);
      case 8:
        return addOrderCriteria("CUSTOM_8", sortDirection);
      default:
        throw new InvalidArgumentException(
            "Custom number has to be between 1 and 8, but this is: " + num);
    }
  }

  @Override
  public List<ClassificationHistoryEvent> list() {
    List<ClassificationHistoryEvent> result;
    try {
      internalTaskanaEngine.openConnection();
      result = internalTaskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this);
      return result;
    } finally {
      internalTaskanaEngine.returnConnection();
    }
  }

  @Override
  public List<ClassificationHistoryEvent> list(int offset, int limit) {
    List<ClassificationHistoryEvent> result = new ArrayList<>();
    try {
      internalTaskanaEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      result = internalTaskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
      return result;
    } finally {
      internalTaskanaEngine.returnConnection();
    }
  }

  @Override
  public List<String> listValues(
      ClassificationHistoryQueryColumnName dbColumnName, SortDirection sortDirection) {
    List<String> result = new ArrayList<>();
    this.columnName = dbColumnName;
    List<String> cacheOrderBy = new ArrayList<>(this.orderBy);
    this.orderBy.clear();
    this.addOrderCriteria(columnName.toString(), sortDirection);

    try {
      internalTaskanaEngine.openConnection();
      result = internalTaskanaEngine.getSqlSession().selectList(LINK_TO_VALUE_MAPPER, this);
      return result;
    } finally {
      this.orderBy.addAll(cacheOrderBy);
      this.columnName = null;
      this.orderColumns.remove(orderColumns.size() - 1);
      internalTaskanaEngine.returnConnection();
    }
  }

  @Override
  public ClassificationHistoryEvent single() {
    ClassificationHistoryEvent result = null;
    try {

      internalTaskanaEngine.openConnection();
      result = internalTaskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);

      return result;
    } finally {
      internalTaskanaEngine.returnConnection();
    }
  }

  @Override
  public long count() {
    Long rowCount = null;
    try {
      internalTaskanaEngine.openConnection();
      rowCount = internalTaskanaEngine.getSqlSession().selectOne(LINK_TO_COUNTER, this);
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      internalTaskanaEngine.returnConnection();
    }
  }

  public String[] getIdIn() {
    return idIn;
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

  public String[] getClassificationIdIn() {
    return classificationIdIn;
  }

  public String[] getApplicationEntryPointIn() {
    return applicationEntryPointIn;
  }

  public String[] getCategoryIn() {
    return categoryIn;
  }

  public String[] getDomainIn() {
    return domainIn;
  }

  public String[] getKeyIn() {
    return keyIn;
  }

  public String[] getNameIn() {
    return nameIn;
  }

  public String[] getParentIdIn() {
    return parentIdIn;
  }

  public String[] getParentKeyIn() {
    return parentKeyIn;
  }

  public int[] getPriorityIn() {
    return priorityIn;
  }

  public String[] getServiceLevelIn() {
    return serviceLevelIn;
  }

  public String[] getTypeIn() {
    return typeIn;
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

  public String[] getCustom5In() {
    return custom5In;
  }

  public String[] getCustom6In() {
    return custom6In;
  }

  public String[] getCustom7In() {
    return custom7In;
  }

  public String[] getCustom8In() {
    return custom8In;
  }

  public String[] getEventTypeLike() {
    return eventTypeLike;
  }

  public String[] getUserIdLike() {
    return userIdLike;
  }

  public String[] getClassificationIdLike() {
    return classificationIdLike;
  }

  public String[] getApplicationEntryPointLike() {
    return applicationEntryPointLike;
  }

  public String[] getCategoryLike() {
    return categoryLike;
  }

  public String[] getDomainLike() {
    return domainLike;
  }

  public String[] getKeyLike() {
    return keyLike;
  }

  public String[] getNameLike() {
    return nameLike;
  }

  public String[] getParentIdLike() {
    return parentIdLike;
  }

  public String[] getParentKeyLike() {
    return parentKeyLike;
  }

  public String[] getServiceLevelLike() {
    return serviceLevelLike;
  }

  public String[] getTypeLike() {
    return typeLike;
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

  public String[] getCustom5Like() {
    return custom5Like;
  }

  public String[] getCustom6Like() {
    return custom6Like;
  }

  public String[] getCustom7Like() {
    return custom7Like;
  }

  public String[] getCustom8Like() {
    return custom8Like;
  }

  public ClassificationHistoryQueryColumnName getColumnName() {
    return columnName;
  }

  public List<String> getOrderBy() {
    return orderBy;
  }

  public List<String> getOrderColumns() {
    return orderColumns;
  }

  private ClassificationHistoryQueryImpl addOrderCriteria(
      String columnName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(columnName + orderByDirection);
    orderColumns.add(columnName);
    return this;
  }
}
