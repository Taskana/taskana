package pro.taskana.task.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.ObjectReferenceQuery;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;

/** TaskQuery for generating dynamic sql. */
public class TaskQueryImpl implements TaskQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskSummaries";
  private static final String LINK_TO_MAPPER_DB2 =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskSummariesDb2";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.task.internal.TaskQueryMapper.countQueryTasks";
  private static final String LINK_TO_COUNTER_DB2 =
      "pro.taskana.task.internal.TaskQueryMapper.countQueryTasksDb2";
  private static final String LINK_TO_VALUE_MAPPER =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskColumnValues";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueryImpl.class);
  private final InternalTaskanaEngine taskanaEngine;
  private final TaskServiceImpl taskService;
  private final List<String> orderBy;
  private final List<String> orderColumns;
  private TaskQueryColumnName columnName;
  private String[] nameIn;
  private String[] nameLike;
  private String[] externalIdIn;
  private String[] externalIdLike;
  private String[] creatorIn;
  private String[] creatorLike;
  private String[] taskIds;
  private String[] description;
  private String[] note;
  private String[] noteLike;
  private int[] priority;
  private KeyDomain[] workbasketKeyDomainIn;
  private String[] workbasketIdIn;
  private TaskState[] stateIn;
  private String[] classificationIdIn;
  private String[] classificationKeyIn;
  private String[] classificationKeyLike;
  private String[] classificationKeyNotIn;
  private String[] classificationCategoryIn;
  private String[] classificationCategoryLike;
  private String[] classificationNameIn;
  private String[] classificationNameLike;
  private String[] ownerIn;
  private String[] ownerNotIn;
  private String[] ownerLike;
  private String[] ownerLongNameIn;
  private String[] ownerLongNameNotIn;
  private String[] ownerLongNameLike;
  private String[] ownerLongNameNotLike;
  private Boolean isRead;
  private Boolean isTransferred;
  private ObjectReference[] objectReferences;
  private String[] porCompanyIn;
  private String[] porCompanyLike;
  private String[] porSystemIn;
  private String[] porSystemLike;
  private String[] porSystemInstanceIn;
  private String[] porSystemInstanceLike;
  private String[] porTypeIn;
  private String[] porTypeLike;
  private String[] porValueIn;
  private String[] porValueLike;
  private String[] parentBusinessProcessIdIn;
  private String[] parentBusinessProcessIdLike;
  private String[] businessProcessIdIn;
  private String[] businessProcessIdLike;
  private CallbackState[] callbackStateIn;
  private String[] custom1In;
  private String[] custom1NotIn;
  private String[] custom1Like;
  private String[] custom2In;
  private String[] custom2NotIn;
  private String[] custom2Like;
  private String[] custom3In;
  private String[] custom3NotIn;
  private String[] custom3Like;
  private String[] custom4In;
  private String[] custom4NotIn;
  private String[] custom4Like;
  private String[] custom5In;
  private String[] custom5NotIn;
  private String[] custom5Like;
  private String[] custom6In;
  private String[] custom6NotIn;
  private String[] custom6Like;
  private String[] custom7In;
  private String[] custom7NotIn;
  private String[] custom7Like;
  private String[] custom8In;
  private String[] custom8NotIn;
  private String[] custom8Like;
  private String[] custom9In;
  private String[] custom9NotIn;
  private String[] custom9Like;
  private String[] custom10In;
  private String[] custom10NotIn;
  private String[] custom10Like;
  private String[] custom11In;
  private String[] custom11NotIn;
  private String[] custom11Like;
  private String[] custom12In;
  private String[] custom12NotIn;
  private String[] custom12Like;
  private String[] custom13In;
  private String[] custom13NotIn;
  private String[] custom13Like;
  private String[] custom14In;
  private String[] custom14NotIn;
  private String[] custom14Like;
  private String[] custom15In;
  private String[] custom15NotIn;
  private String[] custom15Like;
  private String[] custom16In;
  private String[] custom16NotIn;
  private String[] custom16Like;
  private String[] attachmentClassificationKeyIn;
  private String[] attachmentClassificationKeyLike;
  private String[] attachmentClassificationIdIn;
  private String[] attachmentClassificationIdLike;
  private String[] attachmentClassificationNameIn;
  private String[] attachmentClassificationNameLike;
  private String[] attachmentChannelIn;
  private String[] attachmentChannelLike;
  private String[] attachmentReferenceIn;
  private String[] attachmentReferenceLike;
  private TimeInterval[] attachmentReceivedIn;
  private String[] accessIdIn;
  private boolean filterByAccessIdIn;
  private TimeInterval[] createdIn;
  private TimeInterval[] claimedIn;
  private TimeInterval[] completedIn;
  private TimeInterval[] modifiedIn;
  private TimeInterval[] plannedIn;
  private TimeInterval[] receivedIn;
  private TimeInterval[] dueIn;
  private WildcardSearchField[] wildcardSearchFieldIn;
  private String wildcardSearchValueLike;
  private boolean selectAndClaim;
  private boolean useDistinctKeyword = false;
  private boolean joinWithAttachments = false;
  private boolean joinWithClassifications = false;
  private boolean joinWithAttachmentClassifications = false;
  private boolean joinWithWorkbaskets = false;
  private boolean addAttachmentColumnsToSelectClauseForOrdering = false;
  private boolean addClassificationNameToSelectClauseForOrdering = false;
  private boolean addAttachmentClassificationNameToSelectClauseForOrdering = false;
  private boolean addWorkbasketNameToSelectClauseForOrdering = false;
  private boolean includeLongName = false;

  TaskQueryImpl(
      InternalTaskanaEngine taskanaEngine,
      TaskServiceImpl taskService,
      boolean includeLongName) {
    this.taskanaEngine = taskanaEngine;
    this.taskService = taskService;
    this.orderBy = new ArrayList<>();
    this.orderColumns = new ArrayList<>();
    this.filterByAccessIdIn = true;
    this.includeLongName = includeLongName;
  }

  @Override
  public TaskQuery nameIn(String... names) {
    this.nameIn = names;
    return this;
  }

  @Override
  public TaskQuery nameLike(String... names) {
    this.nameLike = toUpperCopy(names);
    return this;
  }

  @Override
  public TaskQuery externalIdIn(String... externalIds) {
    this.externalIdIn = externalIds;
    return this;
  }

  @Override
  public TaskQuery externalIdLike(String... externalIds) {
    this.externalIdLike = toUpperCopy(externalIds);
    return this;
  }

  @Override
  public TaskQuery creatorIn(String... creators) {
    this.creatorIn = creators;
    return this;
  }

  @Override
  public TaskQuery creatorLike(String... creators) {
    this.creatorLike = toUpperCopy(creators);
    return this;
  }

  @Override
  public TaskQuery descriptionLike(String... description) {
    this.description = toUpperCopy(description);
    return this;
  }

  @Override
  public TaskQuery noteLike(String... note) {
    this.noteLike = toUpperCopy(note);
    return this;
  }

  @Override
  public TaskQuery priorityIn(int... priorities) {
    this.priority = priorities;
    return this;
  }

  @Override
  public TaskQuery stateIn(TaskState... states) {
    this.stateIn = states;
    return this;
  }

  @Override
  public TaskQuery stateNotIn(TaskState... states) {
    this.stateIn = EnumUtil.allValuesExceptFor(states);
    return this;
  }

  @Override
  public TaskQuery classificationKeyIn(String... classificationKey) {
    this.classificationKeyIn = classificationKey;
    return this;
  }

  @Override
  public TaskQuery classificationKeyNotIn(String... classificationKeys) {
    this.classificationKeyNotIn = classificationKeys;
    return this;
  }

  @Override
  public TaskQuery classificationKeyLike(String... classificationKeys) {
    this.classificationKeyLike = toUpperCopy(classificationKeys);
    return this;
  }

  @Override
  public TaskQuery classificationIdIn(String... classificationId) {
    this.classificationIdIn = classificationId;
    return this;
  }

  @Override
  public TaskQuery classificationCategoryIn(String... classificationCategories) {
    this.classificationCategoryIn = classificationCategories;
    return this;
  }

  @Override
  public TaskQuery classificationCategoryLike(String... classificationCategories) {
    this.classificationCategoryLike = toUpperCopy(classificationCategories);
    return this;
  }

  @Override
  public TaskQuery classificationNameIn(String... classificationNames) {
    joinWithClassifications = true;
    this.classificationNameIn = classificationNames;
    return this;
  }

  @Override
  public TaskQuery classificationNameLike(String... classificationNames) {
    joinWithClassifications = true;
    this.classificationNameLike = toUpperCopy(classificationNames);
    return this;
  }

  @Override
  public TaskQuery workbasketKeyDomainIn(KeyDomain... workbasketIdentifiers) {
    this.workbasketKeyDomainIn = workbasketIdentifiers;
    return this;
  }

  @Override
  public TaskQuery workbasketIdIn(String... workbasketIds) {
    this.workbasketIdIn = workbasketIds;
    return this;
  }

  @Override
  public TaskQuery ownerIn(String... owners) {
    this.ownerIn = owners;
    return this;
  }

  @Override
  public TaskQuery ownerNotIn(String... owners) {
    this.ownerNotIn = owners;
    return this;
  }

  @Override
  public TaskQuery ownerLike(String... owners) {
    this.ownerLike = toUpperCopy(owners);
    return this;
  }

  @Override
  public TaskQuery ownerLongNameIn(String... longNames) {
    includeLongName = true;
    this.ownerLongNameIn = longNames;
    return this;
  }

  @Override
  public TaskQuery ownerLongNameNotIn(String... longNames) {
    includeLongName = true;
    this.ownerLongNameNotIn = longNames;
    return this;
  }

  @Override
  public TaskQuery ownerLongNameLike(String... longNames) {
    includeLongName = true;
    this.ownerLongNameLike = toUpperCopy(longNames);
    return this;
  }

  @Override
  public TaskQuery ownerLongNameNotLike(String... longNames) {
    includeLongName = true;
    this.ownerLongNameNotLike = toUpperCopy(longNames);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceIn(ObjectReference... objectReferences) {
    this.objectReferences = objectReferences;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceCompanyIn(String... companies) {
    this.porCompanyIn = companies;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceCompanyLike(String... company) {
    this.porCompanyLike = toUpperCopy(company);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemIn(String... systems) {
    this.porSystemIn = systems;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemLike(String... system) {
    this.porSystemLike = toUpperCopy(system);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances) {
    this.porSystemInstanceIn = systemInstances;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemInstanceLike(String... systemInstance) {
    this.porSystemInstanceLike = toUpperCopy(systemInstance);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceTypeIn(String... types) {
    this.porTypeIn = types;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceTypeLike(String... type) {
    this.porTypeLike = toUpperCopy(type);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceValueLike(String... value) {
    this.porValueLike = toUpperCopy(value);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceValueIn(String... values) {
    this.porValueIn = values;
    return this;
  }

  @Override
  public TaskQuery createdWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.createdIn = intervals;
    return this;
  }

  @Override
  public TaskQuery claimedWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.claimedIn = intervals;
    return this;
  }

  @Override
  public TaskQuery completedWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.completedIn = intervals;
    return this;
  }

  @Override
  public TaskQuery modifiedWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.modifiedIn = intervals;
    return this;
  }

  @Override
  public TaskQuery plannedWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.plannedIn = intervals;
    return this;
  }

  @Override
  public TaskQuery receivedWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.receivedIn = intervals;
    return this;
  }

  @Override
  public TaskQuery dueWithin(TimeInterval... intervals) {
    validateAllIntervals(intervals);
    this.dueIn = intervals;
    return this;
  }

  @Override
  public TaskQuery readEquals(Boolean isRead) {
    this.isRead = isRead;
    return this;
  }

  @Override
  public TaskQuery transferredEquals(Boolean isTransferred) {
    this.isTransferred = isTransferred;
    return this;
  }

  @Override
  public TaskQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds) {
    this.parentBusinessProcessIdIn = parentBusinessProcessIds;
    return this;
  }

  @Override
  public TaskQuery parentBusinessProcessIdLike(String... parentBusinessProcessId) {
    this.parentBusinessProcessIdLike = toUpperCopy(parentBusinessProcessId);
    return this;
  }

  @Override
  public TaskQuery businessProcessIdIn(String... businessProcessIds) {
    this.businessProcessIdIn = businessProcessIds;
    return this;
  }

  @Override
  public TaskQuery businessProcessIdLike(String... businessProcessIds) {
    this.businessProcessIdLike = toUpperCopy(businessProcessIds);
    return this;
  }

  @Override
  public TaskQuery customAttributeIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }
    switch (customField) {
      case CUSTOM_1:
        this.custom1In = strings;
        break;
      case CUSTOM_2:
        this.custom2In = strings;
        break;
      case CUSTOM_3:
        this.custom3In = strings;
        break;
      case CUSTOM_4:
        this.custom4In = strings;
        break;
      case CUSTOM_5:
        this.custom5In = strings;
        break;
      case CUSTOM_6:
        this.custom6In = strings;
        break;
      case CUSTOM_7:
        this.custom7In = strings;
        break;
      case CUSTOM_8:
        this.custom8In = strings;
        break;
      case CUSTOM_9:
        this.custom9In = strings;
        break;
      case CUSTOM_10:
        this.custom10In = strings;
        break;
      case CUSTOM_11:
        this.custom11In = strings;
        break;
      case CUSTOM_12:
        this.custom12In = strings;
        break;
      case CUSTOM_13:
        this.custom13In = strings;
        break;
      case CUSTOM_14:
        this.custom14In = strings;
        break;
      case CUSTOM_15:
        this.custom15In = strings;
        break;
      case CUSTOM_16:
        this.custom16In = strings;
        break;
      default:
        throw new SystemException("Unknown custom attribute '" + customField + "'");
    }

    return this;
  }

  @Override
  public TaskQuery customAttributeNotIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }
    switch (customField) {
      case CUSTOM_1:
        this.custom1NotIn = strings;
        break;
      case CUSTOM_2:
        this.custom2NotIn = strings;
        break;
      case CUSTOM_3:
        this.custom3NotIn = strings;
        break;
      case CUSTOM_4:
        this.custom4NotIn = strings;
        break;
      case CUSTOM_5:
        this.custom5NotIn = strings;
        break;
      case CUSTOM_6:
        this.custom6NotIn = strings;
        break;
      case CUSTOM_7:
        this.custom7NotIn = strings;
        break;
      case CUSTOM_8:
        this.custom8NotIn = strings;
        break;
      case CUSTOM_9:
        this.custom9NotIn = strings;
        break;
      case CUSTOM_10:
        this.custom10NotIn = strings;
        break;
      case CUSTOM_11:
        this.custom11NotIn = strings;
        break;
      case CUSTOM_12:
        this.custom12NotIn = strings;
        break;
      case CUSTOM_13:
        this.custom13NotIn = strings;
        break;
      case CUSTOM_14:
        this.custom14NotIn = strings;
        break;
      case CUSTOM_15:
        this.custom15NotIn = strings;
        break;
      case CUSTOM_16:
        this.custom16NotIn = strings;
        break;
      default:
        throw new SystemException("Unknown custom attribute '" + customField + "'");
    }

    return this;
  }

  @Override
  public TaskQuery customAttributeLike(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }

    switch (customField) {
      case CUSTOM_1:
        this.custom1Like = toUpperCopy(strings);
        break;
      case CUSTOM_2:
        this.custom2Like = toUpperCopy(strings);
        break;
      case CUSTOM_3:
        this.custom3Like = toUpperCopy(strings);
        break;
      case CUSTOM_4:
        this.custom4Like = toUpperCopy(strings);
        break;
      case CUSTOM_5:
        this.custom5Like = toUpperCopy(strings);
        break;
      case CUSTOM_6:
        this.custom6Like = toUpperCopy(strings);
        break;
      case CUSTOM_7:
        this.custom7Like = toUpperCopy(strings);
        break;
      case CUSTOM_8:
        this.custom8Like = toUpperCopy(strings);
        break;
      case CUSTOM_9:
        this.custom9Like = toUpperCopy(strings);
        break;
      case CUSTOM_10:
        this.custom10Like = toUpperCopy(strings);
        break;
      case CUSTOM_11:
        this.custom11Like = toUpperCopy(strings);
        break;
      case CUSTOM_12:
        this.custom12Like = toUpperCopy(strings);
        break;
      case CUSTOM_13:
        this.custom13Like = toUpperCopy(strings);
        break;
      case CUSTOM_14:
        this.custom14Like = toUpperCopy(strings);
        break;
      case CUSTOM_15:
        this.custom15Like = toUpperCopy(strings);
        break;
      case CUSTOM_16:
        this.custom16Like = toUpperCopy(strings);
        break;
      default:
        throw new SystemException("Unknown custom field '" + customField + "'");
    }

    return this;
  }

  @Override
  public TaskQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys) {
    joinWithAttachments = true;
    this.attachmentClassificationKeyIn = attachmentClassificationKeys;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationKeyLike(String... attachmentClassificationKey) {
    joinWithAttachments = true;
    this.attachmentClassificationKeyLike = toUpperCopy(attachmentClassificationKey);
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationIdIn(String... attachmentClassificationId) {
    joinWithAttachments = true;
    this.attachmentClassificationIdIn = attachmentClassificationId;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationIdLike(String... attachmentClassificationId) {
    joinWithAttachments = true;
    this.attachmentClassificationIdLike = toUpperCopy(attachmentClassificationId);
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationNameIn(String... attachmentClassificationName) {
    joinWithAttachmentClassifications = true;
    this.attachmentClassificationNameIn = attachmentClassificationName;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationNameLike(String... attachmentClassificationName) {
    joinWithAttachmentClassifications = true;
    this.attachmentClassificationNameLike = toUpperCopy(attachmentClassificationName);
    return this;
  }

  @Override
  public TaskQuery attachmentChannelIn(String... attachmentChannel) {
    joinWithAttachments = true;
    this.attachmentChannelIn = attachmentChannel;
    return this;
  }

  @Override
  public TaskQuery attachmentChannelLike(String... attachmentChannel) {
    joinWithAttachments = true;
    this.attachmentChannelLike = toUpperCopy(attachmentChannel);
    return this;
  }

  @Override
  public TaskQuery attachmentReferenceValueIn(String... referenceValue) {
    joinWithAttachments = true;
    this.attachmentReferenceIn = referenceValue;
    return this;
  }

  @Override
  public TaskQuery attachmentReferenceValueLike(String... referenceValue) {
    joinWithAttachments = true;
    this.attachmentReferenceLike = toUpperCopy(referenceValue);
    return this;
  }

  @Override
  public TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn) {
    validateAllIntervals(receivedIn);
    joinWithAttachments = true;
    this.attachmentReceivedIn = receivedIn;
    return this;
  }

  @Override
  public TaskQuery callbackStateIn(CallbackState... states) {
    this.callbackStateIn = states;
    return this;
  }

  @Override
  public ObjectReferenceQuery createObjectReferenceQuery() {
    return new ObjectReferenceQueryImpl(taskanaEngine);
  }

  @Override
  public TaskQuery orderByBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public TaskQuery orderByClaimed(SortDirection sortDirection) {
    return addOrderCriteria("CLAIMED", sortDirection);
  }

  @Override
  public TaskQuery orderByClassificationKey(SortDirection sortDirection) {
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("TCLASSIFICATION_KEY", sortDirection)
        : addOrderCriteria("t.CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public TaskQuery orderByClassificationName(SortDirection sortDirection) {
    joinWithClassifications = true;
    addClassificationNameToSelectClauseForOrdering = true;
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("CNAME", sortDirection)
        : addOrderCriteria("c.NAME", sortDirection);
  }

  @Override
  public TaskQuery wildcardSearchValueLike(String wildcardSearchValue) {
    this.wildcardSearchValueLike = wildcardSearchValue.toUpperCase();
    return this;
  }

  @Override
  public TaskQuery wildcardSearchFieldsIn(WildcardSearchField... wildcardSearchFields) {
    this.wildcardSearchFieldIn = wildcardSearchFields;
    return this;
  }

  @Override
  public TaskQuery orderByCompleted(SortDirection sortDirection) {
    return addOrderCriteria("COMPLETED", sortDirection);
  }

  @Override
  public TaskQuery orderByCreated(SortDirection sortDirection) {
    return addOrderCriteria("CREATED", sortDirection);
  }

  @Override
  public TaskQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public TaskQuery orderByDue(SortDirection sortDirection) {
    return addOrderCriteria("DUE", sortDirection);
  }

  @Override
  public TaskQuery orderByTaskId(SortDirection sortDirection) {
    return addOrderCriteria("ID", sortDirection);
  }

  @Override
  public TaskQuery orderByModified(SortDirection sortDirection) {
    return addOrderCriteria("MODIFIED", sortDirection);
  }

  @Override
  public TaskQuery orderByName(SortDirection sortDirection) {
    return addOrderCriteria("NAME", sortDirection);
  }

  @Override
  public TaskQuery orderByCreator(SortDirection sortDirection) {
    return addOrderCriteria("CREATOR", sortDirection);
  }

  @Override
  public TaskQuery orderByNote(SortDirection sortDirection) {
    return addOrderCriteria("NOTE", sortDirection);
  }

  @Override
  public TaskQuery orderByOwner(SortDirection sortDirection) {
    return addOrderCriteria("OWNER", sortDirection);
  }

  @Override
  public TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public TaskQuery orderByPlanned(SortDirection sortDirection) {
    return addOrderCriteria("PLANNED", sortDirection);
  }

  @Override
  public TaskQuery orderByReceived(SortDirection sortDirection) {
    return addOrderCriteria("RECEIVED", sortDirection);
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceCompany(SortDirection sortDirection) {
    return addOrderCriteria("POR_COMPANY", sortDirection);
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceSystem(SortDirection sortDirection) {
    return addOrderCriteria("POR_SYSTEM", sortDirection);
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceSystemInstance(SortDirection sortDirection) {
    return addOrderCriteria("POR_INSTANCE", sortDirection);
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceType(SortDirection sortDirection) {
    return addOrderCriteria("POR_TYPE", sortDirection);
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceValue(SortDirection sortDirection) {
    return addOrderCriteria("POR_VALUE", sortDirection);
  }

  @Override
  public TaskQuery orderByPriority(SortDirection sortDirection) {
    return addOrderCriteria("PRIORITY", sortDirection);
  }

  @Override
  public TaskQuery orderByState(SortDirection sortDirection) {
    return addOrderCriteria("STATE", sortDirection);
  }

  @Override
  public TaskQuery orderByWorkbasketKey(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_KEY", sortDirection);
  }

  @Override
  public TaskQuery orderByCustomAttribute(
      TaskCustomField customField, SortDirection sortDirection) {
    return addOrderCriteria(customField.name(), sortDirection);
  }

  @Override
  public TaskQuery idIn(String... taskIds) {
    this.taskIds = taskIds;
    return this;
  }

  @Override
  public TaskQuery orderByWorkbasketId(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_ID", sortDirection);
  }

  @Override
  public TaskQuery orderByWorkbasketName(SortDirection sortDirection) {
    joinWithWorkbaskets = true;
    addWorkbasketNameToSelectClauseForOrdering = true;
    return DB.DB2.dbProductId.equals(getDatabaseId())
        ? addOrderCriteria("WNAME", sortDirection)
        : addOrderCriteria("w.NAME", sortDirection);
  }

  @Override
  public TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("ACLASSIFICATION_KEY", sortDirection)
        : addOrderCriteria("a.CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public TaskQuery orderByAttachmentClassificationName(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentClassificationNameToSelectClauseForOrdering = true;
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("ACNAME", sortDirection)
        : addOrderCriteria("ac.NAME", sortDirection);
  }

  @Override
  public TaskQuery orderByAttachmentClassificationId(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("ACLASSIFICATION_ID", sortDirection)
        : addOrderCriteria("a.CLASSIFICATION_ID", sortDirection);
  }

  @Override
  public TaskQuery orderByAttachmentChannel(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return addOrderCriteria("CHANNEL", sortDirection);
  }

  @Override
  public TaskQuery orderByAttachmentReference(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return addOrderCriteria("REF_VALUE", sortDirection);
  }

  @Override
  public TaskQuery orderByAttachmentReceived(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("ARECEIVED", sortDirection)
        : addOrderCriteria("a.RECEIVED", sortDirection);
  }

  @Override
  public TaskQuery orderByOwnerLongName(SortDirection sortDirection) {
    includeLongName = true;
    return DB.isDb2(getDatabaseId())
        ? addOrderCriteria("ULONG_NAME", sortDirection)
        : addOrderCriteria("u.LONG_NAME", sortDirection);
  }

  public TaskQuery selectAndClaimEquals(boolean selectAndClaim) {
    this.selectAndClaim = selectAndClaim;
    return this;
  }

  @Override
  public List<TaskSummary> list() {
    return taskanaEngine.executeInDatabaseConnection(
        () -> {
          checkForIllegalParamCombinations();
          checkOpenAndReadPermissionForSpecifiedWorkbaskets();
          setupJoinAndOrderParameters();
          setupAccessIds();
          List<TaskSummaryImpl> tasks =
              taskanaEngine.getSqlSession().selectList(getLinkToMapperScript(), this);

          return taskService.augmentTaskSummariesByContainedSummariesWithPartitioning(tasks);
        });
  }

  @Override
  public List<TaskSummary> list(int offset, int limit) {
    List<TaskSummary> result;
    try {
      taskanaEngine.openConnection();
      checkForIllegalParamCombinations();
      checkOpenAndReadPermissionForSpecifiedWorkbaskets();
      setupAccessIds();
      setupJoinAndOrderParameters();
      RowBounds rowBounds = new RowBounds(offset, limit);
      List<TaskSummaryImpl> tasks =
          taskanaEngine.getSqlSession().selectList(getLinkToMapperScript(), this, rowBounds);
      result = taskService.augmentTaskSummariesByContainedSummariesWithPartitioning(tasks);
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
  public List<String> listValues(TaskQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result;
    try {
      taskanaEngine.openConnection();
      this.columnName = columnName;
      this.orderBy.clear();
      this.addOrderCriteria(columnName.toString(), sortDirection);
      checkForIllegalParamCombinations();
      checkOpenAndReadPermissionForSpecifiedWorkbaskets();
      setupAccessIds();

      if (columnName == TaskQueryColumnName.CLASSIFICATION_NAME) {
        joinWithClassifications = true;
      }

      if (columnName == TaskQueryColumnName.A_CLASSIFICATION_NAME) {
        joinWithAttachmentClassifications = true;
      }

      if (columnName.isAttachmentColumn()) {
        joinWithAttachments = true;
      }

      if (columnName == TaskQueryColumnName.OWNER_LONG_NAME) {
        includeLongName = true;
      }

      setupJoinAndOrderParameters();
      result = taskanaEngine.getSqlSession().selectList(LINK_TO_VALUE_MAPPER, this);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public TaskSummary single() {
    TaskSummary result;
    try {
      taskanaEngine.openConnection();
      checkOpenAndReadPermissionForSpecifiedWorkbaskets();
      setupAccessIds();
      setupJoinAndOrderParameters();
      TaskSummaryImpl taskSummaryImpl =
          taskanaEngine.getSqlSession().selectOne(getLinkToMapperScript(), this);
      if (taskSummaryImpl == null) {
        return null;
      }
      List<TaskSummaryImpl> tasks = new ArrayList<>();
      tasks.add(taskSummaryImpl);
      List<TaskSummary> augmentedList =
          taskService.augmentTaskSummariesByContainedSummariesWithPartitioning(tasks);
      result = augmentedList.get(0);

      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public long count() {
    Long rowCount;
    try {
      taskanaEngine.openConnection();
      checkOpenAndReadPermissionForSpecifiedWorkbaskets();
      setupAccessIds();
      setupJoinAndOrderParameters();
      rowCount = taskanaEngine.getSqlSession().selectOne(getLinkToCounterTaskScript(), this);
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  // optimized query for db2 can't be used for now in case of selectAndClaim because of temporary
  // tables and the "for update" clause clashing in db2
  public String getLinkToMapperScript() {
    if (DB.isDb2(getDatabaseId()) && !selectAndClaim) {
      return LINK_TO_MAPPER_DB2;
    } else {
      return LINK_TO_MAPPER;
    }
  }

  public String getLinkToCounterTaskScript() {
    return DB.isDb2(getDatabaseId()) ? LINK_TO_COUNTER_DB2 : LINK_TO_COUNTER;
  }

  public boolean isUseDistinctKeyword() {
    return useDistinctKeyword;
  }

  public void setUseDistinctKeyword(boolean useDistinctKeyword) {
    this.useDistinctKeyword = useDistinctKeyword;
  }

  public boolean isJoinWithAttachments() {
    return joinWithAttachments;
  }

  public void setJoinWithAttachments(boolean joinWithAttachments) {
    this.joinWithAttachments = joinWithAttachments;
  }

  public boolean isJoinWithClassifications() {
    return joinWithClassifications;
  }

  public void setJoinWithClassifications(boolean joinWithClassifications) {
    this.joinWithClassifications = joinWithClassifications;
  }

  public boolean isJoinWithAttachmentsClassifications() {
    return joinWithAttachmentClassifications;
  }

  public void setJoinWithAttachmentsClassifications(boolean joinWithAttachmentsClassifications) {
    this.joinWithAttachmentClassifications = joinWithAttachmentsClassifications;
  }

  public boolean isAddAttachmentColumnsToSelectClauseForOrdering() {
    return addAttachmentColumnsToSelectClauseForOrdering;
  }

  public void setAddAttachmentColumnsToSelectClauseForOrdering(
      boolean addAttachmentColumnsToSelectClauseForOrdering) {
    this.addAttachmentColumnsToSelectClauseForOrdering =
        addAttachmentColumnsToSelectClauseForOrdering;
  }

  public boolean isIncludeLongName() {
    return includeLongName;
  }

  public void setIncludeLongName(boolean includeLongName) {
    this.includeLongName = includeLongName;
  }

  public String[] getTaskIds() {
    return taskIds;
  }

  public String[] getNameIn() {
    return nameIn;
  }

  public String[] getExternalIdIn() {
    return externalIdIn;
  }

  public String[] getExternalIdLike() {
    return externalIdLike;
  }

  public String[] getCreatorIn() {
    return creatorIn;
  }

  public String[] getCreatorLike() {
    return creatorLike;
  }

  public String[] getDescription() {
    return description;
  }

  public int[] getPriority() {
    return priority;
  }

  public TaskState[] getStateIn() {
    return stateIn;
  }

  public String[] getOwnerIn() {
    return ownerIn;
  }

  public String[] getOwnerNotIn() {
    return ownerNotIn;
  }

  public String[] getOwnerLike() {
    return ownerLike;
  }

  public String[] getOwnerLongNameIn() {
    return ownerLongNameIn;
  }

  public String[] getOwnerLongNameNotIn() {
    return ownerLongNameNotIn;
  }

  public String[] getOwnerLongNameLike() {
    return ownerLongNameLike;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public Boolean getIsTransferred() {
    return isTransferred;
  }

  public boolean getIsSelectAndClaim() {
    return selectAndClaim;
  }

  public String[] getPorCompanyIn() {
    return porCompanyIn;
  }

  public String[] getPorCompanyLike() {
    return porCompanyLike;
  }

  public String[] getPorSystemIn() {
    return porSystemIn;
  }

  public String[] getPorSystemLike() {
    return porSystemLike;
  }

  public String[] getPorSystemInstanceIn() {
    return porSystemInstanceIn;
  }

  public String[] getPorSystemInstanceLike() {
    return porSystemInstanceLike;
  }

  public String[] getPorTypeIn() {
    return porTypeIn;
  }

  public String[] getPorTypeLike() {
    return porTypeLike;
  }

  public String[] getPorValueIn() {
    return porValueIn;
  }

  public String[] getPorValueLike() {
    return porValueLike;
  }

  public List<String> getOrderBy() {
    return orderBy;
  }

  public List<String> getOrderColumns() {
    return orderColumns;
  }

  public TimeInterval[] getCreatedIn() {
    return createdIn;
  }

  public TaskServiceImpl getTaskService() {
    return taskService;
  }

  public String[] getNote() {
    return note;
  }

  public String[] getNoteLike() {
    return noteLike;
  }

  public String[] getParentBusinessProcessIdIn() {
    return parentBusinessProcessIdIn;
  }

  public String[] getParentBusinessProcessIdLike() {
    return parentBusinessProcessIdLike;
  }

  public String[] getBusinessProcessIdIn() {
    return businessProcessIdIn;
  }

  public String[] getBusinessProcessIdLike() {
    return businessProcessIdLike;
  }

  public String[] getCustom1In() {
    return custom1In;
  }

  public String[] getCustom1NotIn() {
    return custom1NotIn;
  }

  public String[] getCustom1Like() {
    return custom1Like;
  }

  public String[] getCustom2In() {
    return custom2In;
  }

  public String[] getCustom2NotIn() {
    return custom2NotIn;
  }

  public String[] getCustom2Like() {
    return custom2Like;
  }

  public String[] getCustom3In() {
    return custom3In;
  }

  public String[] getCustom3NotIn() {
    return custom3NotIn;
  }

  public String[] getCustom3Like() {
    return custom3Like;
  }

  public String[] getCustom4In() {
    return custom4In;
  }

  public String[] getCustom4NotIn() {
    return custom4NotIn;
  }

  public String[] getCustom4Like() {
    return custom4Like;
  }

  public String[] getCustom5In() {
    return custom5In;
  }

  public String[] getCustom5NotIn() {
    return custom5NotIn;
  }

  public String[] getCustom5Like() {
    return custom5Like;
  }

  public String[] getCustom6In() {
    return custom6In;
  }

  public String[] getCustom6NotIn() {
    return custom6NotIn;
  }

  public String[] getCustom6Like() {
    return custom6Like;
  }

  public String[] getCustom7In() {
    return custom7In;
  }

  public String[] getCustom7NotIn() {
    return custom7NotIn;
  }

  public String[] getCustom7Like() {
    return custom7Like;
  }

  public String[] getCustom8In() {
    return custom8In;
  }

  public String[] getCustom8NotIn() {
    return custom8NotIn;
  }

  public String[] getCustom8Like() {
    return custom8Like;
  }

  public String[] getCustom9In() {
    return custom9In;
  }

  public String[] getCustom9NotIn() {
    return custom9NotIn;
  }

  public String[] getCustom9Like() {
    return custom9Like;
  }

  public String[] getCustom10In() {
    return custom10In;
  }

  public String[] getCustom10NotIn() {
    return custom10NotIn;
  }

  public String[] getCustom10Like() {
    return custom10Like;
  }

  public String[] getCustom11In() {
    return custom11In;
  }

  public String[] getCustom11NotIn() {
    return custom11NotIn;
  }

  public String[] getCustom11Like() {
    return custom11Like;
  }

  public String[] getCustom12In() {
    return custom12In;
  }

  public String[] getCustom12NotIn() {
    return custom12NotIn;
  }

  public String[] getCustom12Like() {
    return custom12Like;
  }

  public String[] getCustom13In() {
    return custom13In;
  }

  public String[] getCustom13NotIn() {
    return custom13NotIn;
  }

  public String[] getCustom13Like() {
    return custom13Like;
  }

  public String[] getCustom14In() {
    return custom14In;
  }

  public String[] getCustom14NotIn() {
    return custom14NotIn;
  }

  public String[] getCustom14Like() {
    return custom14Like;
  }

  public String[] getCustom15In() {
    return custom15In;
  }

  public String[] getCustom15NotIn() {
    return custom15NotIn;
  }

  public String[] getCustom15Like() {
    return custom15Like;
  }

  public String[] getCustom16In() {
    return custom16In;
  }

  public String[] getCustom16NotIn() {
    return custom16NotIn;
  }

  public String[] getCustom16Like() {
    return custom16Like;
  }

  public String[] getClassificationCategoryIn() {
    return classificationCategoryIn;
  }

  public String[] getClassificationCategoryLike() {
    return classificationCategoryLike;
  }

  public TimeInterval[] getClaimedIn() {
    return claimedIn;
  }

  public TimeInterval[] getCompletedIn() {
    return completedIn;
  }

  public TimeInterval[] getModifiedIn() {
    return modifiedIn;
  }

  public TimeInterval[] getPlannedIn() {
    return plannedIn;
  }

  public TimeInterval[] getReceivedIn() {
    return receivedIn;
  }

  public TimeInterval[] getDueIn() {
    return dueIn;
  }

  public String[] getNameLike() {
    return nameLike;
  }

  public String[] getClassificationKeyIn() {
    return classificationKeyIn;
  }

  public String[] getClassificationKeyNotIn() {
    return classificationKeyNotIn;
  }

  public String[] getClassificationKeyLike() {
    return classificationKeyLike;
  }

  public String[] getClassificationIdIn() {
    return classificationIdIn;
  }

  public KeyDomain[] getWorkbasketKeyDomainIn() {
    return workbasketKeyDomainIn;
  }

  public String[] getWorkbasketIdIn() {
    return workbasketIdIn;
  }

  public TaskQueryColumnName getColumnName() {
    return columnName;
  }

  public String[] getAttachmentClassificationKeyIn() {
    return attachmentClassificationKeyIn;
  }

  public void setAttachmentClassificationKeyIn(String[] attachmentClassificationKeyIn) {
    this.attachmentClassificationKeyIn = attachmentClassificationKeyIn;
  }

  public String[] getAttachmentClassificationKeyLike() {
    return attachmentClassificationKeyLike;
  }

  public void setAttachmentClassificationKeyLike(String[] attachmentClassificationKeyLike) {
    this.attachmentClassificationKeyLike = attachmentClassificationKeyLike;
  }

  public String[] getAttachmentClassificationIdIn() {
    return attachmentClassificationIdIn;
  }

  public void setAttachmentClassificationIdIn(String[] attachmentClassificationIdIn) {
    this.attachmentClassificationIdIn = attachmentClassificationIdIn;
  }

  public String[] getAttachmentClassificationIdLike() {
    return attachmentClassificationIdLike;
  }

  public void setAttachmentClassificationIdLike(String[] attachmentclassificationIdLike) {
    this.attachmentClassificationIdLike = attachmentclassificationIdLike;
  }

  public String[] getAttachmentChannelIn() {
    return attachmentChannelIn;
  }

  public void setAttachmentChannelIn(String[] attachmentChannelIn) {
    this.attachmentChannelIn = attachmentChannelIn;
  }

  public String[] getAttachmentChannelLike() {
    return attachmentChannelLike;
  }

  public void setAttachmentChannelLike(String[] attachmentChannelLike) {
    this.attachmentChannelLike = attachmentChannelLike;
  }

  public String[] getAttachmentReferenceIn() {
    return attachmentReferenceIn;
  }

  public void setAttachmentReferenceIn(String[] attachmentReferenceIn) {
    this.attachmentReferenceIn = attachmentReferenceIn;
  }

  public String[] getAttachmentReferenceLike() {
    return attachmentReferenceLike;
  }

  public void setAttachmentReferenceLike(String[] attachmentReferenceLike) {
    this.attachmentReferenceLike = attachmentReferenceLike;
  }

  public TimeInterval[] getAttachmentReceivedIn() {
    return attachmentReceivedIn;
  }

  public void setAttachmentReceivedIn(TimeInterval[] attachmentReceivedIn) {
    this.attachmentReceivedIn = attachmentReceivedIn;
  }

  public String[] getClassificationNameIn() {
    return classificationNameIn;
  }

  public void setClassificationNameIn(String[] classificationNameIn) {
    this.classificationNameIn = classificationNameIn;
  }

  public String[] getClassificationNameLike() {
    return classificationNameLike;
  }

  public void setClassificationNameLike(String[] classificationNameLike) {
    this.classificationNameLike = classificationNameLike;
  }

  public String[] getAttachmentClassificationNameIn() {
    return attachmentClassificationNameIn;
  }

  public void setAttachmentClassificationNameIn(String[] attachmentClassificationNameIn) {
    this.attachmentClassificationNameIn = attachmentClassificationNameIn;
  }

  public String[] getAttachmentClassificationNameLike() {
    return attachmentClassificationNameLike;
  }

  public void setAttachmentClassificationNameLike(String[] attachmentClassificationNameLike) {
    this.attachmentClassificationNameLike = attachmentClassificationNameLike;
  }

  public boolean isAddClassificationNameToSelectClauseForOrdering() {
    return addClassificationNameToSelectClauseForOrdering;
  }

  public void setAddClassificationNameToSelectClauseForOrdering(
      boolean addClassificationNameToSelectClauseForOrdering) {
    this.addClassificationNameToSelectClauseForOrdering =
        addClassificationNameToSelectClauseForOrdering;
  }

  public boolean isAddAttachmentClassificationNameToSelectClauseForOrdering() {
    return addAttachmentClassificationNameToSelectClauseForOrdering;
  }

  public void setAddAttachmentClassificationNameToSelectClauseForOrdering(
      boolean addAttachmentClassificationNameToSelectClauseForOrdering) {
    this.addAttachmentClassificationNameToSelectClauseForOrdering =
        addAttachmentClassificationNameToSelectClauseForOrdering;
  }

  public WildcardSearchField[] getWildcardSearchFieldIn() {
    return wildcardSearchFieldIn;
  }

  public String getWildcardSearchValueLike() {
    return wildcardSearchValueLike;
  }

  private void validateAllIntervals(TimeInterval[] intervals) {
    for (TimeInterval ti : intervals) {
      if (!ti.isValid()) {
        throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
      }
    }
  }

  private void checkForIllegalParamCombinations() {

    if ((wildcardSearchValueLike != null && wildcardSearchFieldIn == null)
        || (wildcardSearchValueLike == null && wildcardSearchFieldIn != null)) {
      throw new IllegalArgumentException(
          "The params \"wildcardSearchFieldIn\" and \"wildcardSearchValueLike\""
              + " must be used together!");
    }
  }

  private String getDatabaseId() {
    return this.taskanaEngine.getSqlSession().getConfiguration().getDatabaseId();
  }

  private void setupJoinAndOrderParameters() {
    // if classificationName or attachmentClassificationName are added to the result set, and
    // multiple
    // attachments exist, the addition of these attribute may increase the result set.
    // in order to have the same result set independent of sorting yes or no,
    // we add the add... flags whenever we join with classification or attachmentClassification
    if (joinWithAttachmentClassifications) {
      joinWithAttachments = true;
      addAttachmentClassificationNameToSelectClauseForOrdering = true;
    }
    if (joinWithClassifications) {
      addClassificationNameToSelectClauseForOrdering = true;
    }

    if (addClassificationNameToSelectClauseForOrdering) {
      joinWithClassifications = true;
    }
    if (addAttachmentClassificationNameToSelectClauseForOrdering) {
      joinWithAttachments = true;
      joinWithAttachmentClassifications = true;
    }
    if (joinWithAttachments || joinWithClassifications) {
      useDistinctKeyword = true;
    }
  }

  private void setupAccessIds() {
    if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN)
        || !filterByAccessIdIn) {
      this.accessIdIn = null;
    } else if (this.accessIdIn == null) {
      String[] accessIds = new String[0];
      List<String> ucAccessIds = taskanaEngine.getEngine().getCurrentUserContext().getAccessIds();
      if (!ucAccessIds.isEmpty()) {
        accessIds = new String[ucAccessIds.size()];
        accessIds = ucAccessIds.toArray(accessIds);
      }
      this.accessIdIn = accessIds;
      WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
    }
  }

  private void checkOpenAndReadPermissionForSpecifiedWorkbaskets() {
    if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Skipping permissions check since user is in role ADMIN or TASK_ADMIN.");
      }
      return;
    }
    try {
      if (this.workbasketIdIn != null && this.workbasketIdIn.length > 0) {
        filterByAccessIdIn = false;
        for (String workbasketId : workbasketIdIn) {
          checkOpenAndReadPermissionById(workbasketId);
        }
      }
      if (workbasketKeyDomainIn != null && workbasketKeyDomainIn.length > 0) {
        filterByAccessIdIn = false;
        for (KeyDomain keyDomain : workbasketKeyDomainIn) {
          checkOpenAndReadPermissionByKeyDomain(keyDomain);
        }
      }
    } catch (NotAuthorizedException e) {
      throw new NotAuthorizedToQueryWorkbasketException(e.getMessage(), e.getErrorCode(), e);
    }
  }

  private void checkOpenAndReadPermissionById(String workbasketId) throws NotAuthorizedException {
    try {
      taskanaEngine
          .getEngine()
          .getWorkbasketService()
          .checkAuthorization(workbasketId, WorkbasketPermission.OPEN, WorkbasketPermission.READ);
    } catch (WorkbasketNotFoundException e) {
      LOGGER.warn(
          String.format("The workbasket with the ID ' %s ' does not exist.", workbasketId), e);
    }
  }

  private void checkOpenAndReadPermissionByKeyDomain(KeyDomain keyDomain)
      throws NotAuthorizedException {
    try {
      taskanaEngine
          .getEngine()
          .getWorkbasketService()
          .checkAuthorization(
              keyDomain.getKey(),
              keyDomain.getDomain(),
              WorkbasketPermission.OPEN,
              WorkbasketPermission.READ);
    } catch (WorkbasketNotFoundException e) {
      LOGGER.warn(
          String.format(
              "The workbasket with the KEY ' %s ' and DOMAIN ' %s ' does not exist.",
              keyDomain.getKey(), keyDomain.getDomain()),
          e);
    }
  }

  private TaskQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(columnName + orderByDirection);
    orderColumns.add(columnName);
    return this;
  }

  @Override
  public String toString() {
    return "TaskQueryImpl [taskanaEngine="
        + taskanaEngine
        + ", taskService="
        + taskService
        + ", orderBy="
        + orderBy
        + ", orderColumns="
        + orderColumns
        + ", columnName="
        + columnName
        + ", nameIn="
        + Arrays.toString(nameIn)
        + ", nameLike="
        + Arrays.toString(nameLike)
        + ", externalIdIn="
        + Arrays.toString(externalIdIn)
        + ", externalIdLike="
        + Arrays.toString(externalIdLike)
        + ", creatorIn="
        + Arrays.toString(creatorIn)
        + ", creatorLike="
        + Arrays.toString(creatorLike)
        + ", taskIds="
        + Arrays.toString(taskIds)
        + ", description="
        + Arrays.toString(description)
        + ", note="
        + Arrays.toString(note)
        + ", noteLike="
        + Arrays.toString(noteLike)
        + ", priority="
        + Arrays.toString(priority)
        + ", workbasketKeyDomainIn="
        + Arrays.toString(workbasketKeyDomainIn)
        + ", workbasketIdIn="
        + Arrays.toString(workbasketIdIn)
        + ", stateIn="
        + Arrays.toString(stateIn)
        + ", classificationIdIn="
        + Arrays.toString(classificationIdIn)
        + ", classificationKeyIn="
        + Arrays.toString(classificationKeyIn)
        + ", classificationKeyLike="
        + Arrays.toString(classificationKeyLike)
        + ", classificationKeyNotIn="
        + Arrays.toString(classificationKeyNotIn)
        + ", classificationCategoryIn="
        + Arrays.toString(classificationCategoryIn)
        + ", classificationCategoryLike="
        + Arrays.toString(classificationCategoryLike)
        + ", classificationNameIn="
        + Arrays.toString(classificationNameIn)
        + ", classificationNameLike="
        + Arrays.toString(classificationNameLike)
        + ", ownerIn="
        + Arrays.toString(ownerIn)
        + ", ownerLike="
        + Arrays.toString(ownerLike)
        + ", isRead="
        + isRead
        + ", isTransferred="
        + isTransferred
        + ", objectReferences="
        + Arrays.toString(objectReferences)
        + ", porCompanyIn="
        + Arrays.toString(porCompanyIn)
        + ", porCompanyLike="
        + Arrays.toString(porCompanyLike)
        + ", porSystemIn="
        + Arrays.toString(porSystemIn)
        + ", porSystemLike="
        + Arrays.toString(porSystemLike)
        + ", porSystemInstanceIn="
        + Arrays.toString(porSystemInstanceIn)
        + ", porSystemInstanceLike="
        + Arrays.toString(porSystemInstanceLike)
        + ", porTypeIn="
        + Arrays.toString(porTypeIn)
        + ", porTypeLike="
        + Arrays.toString(porTypeLike)
        + ", porValueIn="
        + Arrays.toString(porValueIn)
        + ", porValueLike="
        + Arrays.toString(porValueLike)
        + ", parentBusinessProcessIdIn="
        + Arrays.toString(parentBusinessProcessIdIn)
        + ", parentBusinessProcessIdLike="
        + Arrays.toString(parentBusinessProcessIdLike)
        + ", businessProcessIdIn="
        + Arrays.toString(businessProcessIdIn)
        + ", businessProcessIdLike="
        + Arrays.toString(businessProcessIdLike)
        + ", callbackStateIn="
        + Arrays.toString(callbackStateIn)
        + ", custom1In="
        + Arrays.toString(custom1In)
        + ", custom1NotIn="
        + Arrays.toString(custom1NotIn)
        + ", custom1Like="
        + Arrays.toString(custom1Like)
        + ", custom2In="
        + Arrays.toString(custom2In)
        + ", custom2NotIn="
        + Arrays.toString(custom2NotIn)
        + ", custom2Like="
        + Arrays.toString(custom2Like)
        + ", custom3In="
        + Arrays.toString(custom3In)
        + ", custom3NotIn="
        + Arrays.toString(custom3NotIn)
        + ", custom3Like="
        + Arrays.toString(custom3Like)
        + ", custom4In="
        + Arrays.toString(custom4In)
        + ", custom4NotIn="
        + Arrays.toString(custom4NotIn)
        + ", custom4Like="
        + Arrays.toString(custom4Like)
        + ", custom5In="
        + Arrays.toString(custom5In)
        + ", custom5NotIn="
        + Arrays.toString(custom5NotIn)
        + ", custom5Like="
        + Arrays.toString(custom5Like)
        + ", custom6In="
        + Arrays.toString(custom6In)
        + ", custom6NotIn="
        + Arrays.toString(custom6NotIn)
        + ", custom6Like="
        + Arrays.toString(custom6Like)
        + ", custom7In="
        + Arrays.toString(custom7In)
        + ", custom7NotIn="
        + Arrays.toString(custom7NotIn)
        + ", custom7Like="
        + Arrays.toString(custom7Like)
        + ", custom8In="
        + Arrays.toString(custom8In)
        + ", custom8NotIn="
        + Arrays.toString(custom8NotIn)
        + ", custom8Like="
        + Arrays.toString(custom8Like)
        + ", custom9In="
        + Arrays.toString(custom9In)
        + ", custom9NotIn="
        + Arrays.toString(custom9NotIn)
        + ", custom9Like="
        + Arrays.toString(custom9Like)
        + ", custom10In="
        + Arrays.toString(custom10In)
        + ", custom10NotIn="
        + Arrays.toString(custom10NotIn)
        + ", custom10Like="
        + Arrays.toString(custom10Like)
        + ", custom11In="
        + Arrays.toString(custom11In)
        + ", custom11NotIn="
        + Arrays.toString(custom11NotIn)
        + ", custom11Like="
        + Arrays.toString(custom11Like)
        + ", custom12In="
        + Arrays.toString(custom12In)
        + ", custom12NotIn="
        + Arrays.toString(custom12NotIn)
        + ", custom12Like="
        + Arrays.toString(custom12Like)
        + ", custom13In="
        + Arrays.toString(custom13In)
        + ", custom13NotIn="
        + Arrays.toString(custom13NotIn)
        + ", custom13Like="
        + Arrays.toString(custom13Like)
        + ", custom14In="
        + Arrays.toString(custom14In)
        + ", custom14NotIn="
        + Arrays.toString(custom14NotIn)
        + ", custom14Like="
        + Arrays.toString(custom14Like)
        + ", custom15In="
        + Arrays.toString(custom15In)
        + ", custom15NotIn="
        + Arrays.toString(custom15NotIn)
        + ", custom15Like="
        + Arrays.toString(custom15Like)
        + ", custom16In="
        + Arrays.toString(custom16In)
        + ", custom16NotIn="
        + Arrays.toString(custom16NotIn)
        + ", custom16Like="
        + Arrays.toString(custom16Like)
        + ", attachmentClassificationKeyIn="
        + Arrays.toString(attachmentClassificationKeyIn)
        + ", attachmentClassificationKeyLike="
        + Arrays.toString(attachmentClassificationKeyLike)
        + ", attachmentClassificationIdIn="
        + Arrays.toString(attachmentClassificationIdIn)
        + ", attachmentClassificationIdLike="
        + Arrays.toString(attachmentClassificationIdLike)
        + ", attachmentClassificationNameIn="
        + Arrays.toString(attachmentClassificationNameIn)
        + ", attachmentClassificationNameLike="
        + Arrays.toString(attachmentClassificationNameLike)
        + ", attachmentChannelIn="
        + Arrays.toString(attachmentChannelIn)
        + ", attachmentChannelLike="
        + Arrays.toString(attachmentChannelLike)
        + ", attachmentReferenceIn="
        + Arrays.toString(attachmentReferenceIn)
        + ", attachmentReferenceLike="
        + Arrays.toString(attachmentReferenceLike)
        + ", attachmentReceivedIn="
        + Arrays.toString(attachmentReceivedIn)
        + ", accessIdIn="
        + Arrays.toString(accessIdIn)
        + ", filterByAccessIdIn="
        + filterByAccessIdIn
        + ", createdIn="
        + Arrays.toString(createdIn)
        + ", claimedIn="
        + Arrays.toString(claimedIn)
        + ", completedIn="
        + Arrays.toString(completedIn)
        + ", modifiedIn="
        + Arrays.toString(modifiedIn)
        + ", plannedIn="
        + Arrays.toString(plannedIn)
        + ", receivedIn="
        + Arrays.toString(receivedIn)
        + ", dueIn="
        + Arrays.toString(dueIn)
        + ", wildcardSearchFieldIn="
        + Arrays.toString(wildcardSearchFieldIn)
        + ", wildcardSearchValueLike="
        + wildcardSearchValueLike
        + ", selectAndClaim="
        + selectAndClaim
        + ", useDistinctKeyword="
        + useDistinctKeyword
        + ", joinWithAttachments="
        + joinWithAttachments
        + ", joinWithClassifications="
        + joinWithClassifications
        + ", joinWithAttachmentClassifications="
        + joinWithAttachmentClassifications
        + ", joinWithWorkbaskets="
        + joinWithWorkbaskets
        + ", addAttachmentColumnsToSelectClauseForOrdering="
        + addAttachmentColumnsToSelectClauseForOrdering
        + ", addClassificationNameToSelectClauseForOrdering="
        + addClassificationNameToSelectClauseForOrdering
        + ", addAttachmentClassificationNameToSelectClauseForOrdering="
        + addAttachmentClassificationNameToSelectClauseForOrdering
        + ", addWorkbasketNameToSelectClauseForOrdering="
        + addWorkbasketNameToSelectClauseForOrdering
        + "]";
  }
}
