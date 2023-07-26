package pro.taskana.task.internal;

import static pro.taskana.common.api.BaseQuery.toLowerCopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.api.IntInterval;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.ObjectReferenceQuery;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.internal.WorkbasketQueryImpl;

/** TaskQuery for generating dynamic sql. */
public class TaskQueryImpl implements TaskQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskSummaries";
  private static final String LINK_TO_MAPPER_DB2 =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskSummariesDb2";
  private static final String LINK_TO_MAPPER_ORACLE =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskSummariesOracle";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.task.internal.TaskQueryMapper.countQueryTasks";
  private static final String LINK_TO_COUNTER_DB2 =
      "pro.taskana.task.internal.TaskQueryMapper.countQueryTasksDb2";
  private static final String LINK_TO_VALUE_MAPPER =
      "pro.taskana.task.internal.TaskQueryMapper.queryTaskColumnValues";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueryImpl.class);
  private final InternalTaskanaEngine taskanaEngine;
  private final TaskServiceImpl taskService;
  private final List<String> orderByOuter;
  private final List<String> orderByInner;

  private TaskQueryColumnName columnName;
  private String[] accessIdIn;
  private boolean filterByAccessIdIn;
  private boolean selectAndClaim;
  private boolean useDistinctKeyword = false;
  private boolean joinWithAttachments = false;
  private boolean joinWithSecondaryObjectReferences = false;
  private boolean joinWithClassifications = false;
  private boolean joinWithAttachmentClassifications = false;
  private boolean joinWithWorkbaskets = false;
  private boolean addAttachmentColumnsToSelectClauseForOrdering = false;
  private boolean addClassificationNameToSelectClauseForOrdering = false;
  private boolean addAttachmentClassificationNameToSelectClauseForOrdering = false;
  private boolean addWorkbasketNameToSelectClauseForOrdering = false;
  private boolean joinWithUserInfo;
  private boolean groupByPor;
  private String groupBySor;
  private String[] taskId;
  private String[] taskIdNotIn;
  private String[] externalIdIn;
  private String[] externalIdNotIn;
  private TimeInterval[] receivedWithin;
  private TimeInterval[] receivedNotWithin;
  private TimeInterval[] createdWithin;
  private TimeInterval[] createdNotWithin;
  private TimeInterval[] claimedWithin;
  private TimeInterval[] claimedNotWithin;
  private TimeInterval[] modifiedWithin;
  private TimeInterval[] modifiedNotWithin;
  private TimeInterval[] plannedWithin;
  private TimeInterval[] plannedNotWithin;
  private TimeInterval[] dueWithin;
  private TimeInterval[] dueNotWithin;
  private TimeInterval[] completedWithin;
  private TimeInterval[] completedNotWithin;
  private String[] nameIn;
  private String[] nameNotIn;
  private String[] nameLike;
  private String[] nameNotLike;
  private String[] creatorIn;
  private String[] creatorNotIn;
  private String[] creatorLike;
  private String[] creatorNotLike;
  private String[] noteLike;
  private String[] noteNotLike;
  private String[] descriptionLike;
  private String[] descriptionNotLike;
  private int[] priority;
  private int[] priorityNotIn;
  private IntInterval[] priorityWithin;
  private IntInterval[] priorityNotWithin;
  private TaskState[] stateIn;
  private TaskState[] stateNotIn;
  private String[] classificationIdIn;
  private String[] classificationIdNotIn;
  private String[] classificationKeyIn;
  private String[] classificationKeyNotIn;
  private String[] classificationKeyLike;
  private String[] classificationKeyNotLike;
  private String[] classificationParentKeyIn;
  private String[] classificationParentKeyNotIn;
  private String[] classificationParentKeyLike;
  private String[] classificationParentKeyNotLike;
  private String[] classificationCategoryIn;
  private String[] classificationCategoryNotIn;
  private String[] classificationCategoryLike;
  private String[] classificationCategoryNotLike;
  private String[] classificationNameIn;
  private String[] classificationNameNotIn;
  private String[] classificationNameLike;
  private String[] classificationNameNotLike;
  private String[] workbasketIdIn;
  private String[] workbasketIdNotIn;
  private KeyDomain[] workbasketKeyDomainIn;
  private KeyDomain[] workbasketKeyDomainNotIn;
  private String[] businessProcessIdIn;
  private String[] businessProcessIdNotIn;
  private String[] businessProcessIdLike;
  private String[] businessProcessIdNotLike;
  private String[] parentBusinessProcessIdIn;
  private String[] parentBusinessProcessIdNotIn;
  private String[] parentBusinessProcessIdLike;
  private String[] parentBusinessProcessIdNotLike;
  private String[] ownerIn;
  private String[] ownerNotIn;
  private String[] ownerLike;
  private String[] ownerNotLike;
  private String[] ownerLongNameIn;
  private String[] ownerLongNameNotIn;
  private String[] ownerLongNameLike;
  private String[] ownerLongNameNotLike;
  private ObjectReference[] objectReferences;
  private String[] porCompanyIn;
  private String[] porCompanyNotIn;
  private String[] porCompanyLike;
  private String[] porCompanyNotLike;
  private String[] porSystemIn;
  private String[] porSystemNotIn;
  private String[] porSystemLike;
  private String[] porSystemNotLike;
  private String[] porSystemInstanceIn;
  private String[] porSystemInstanceNotIn;
  private String[] porSystemInstanceLike;
  private String[] porSystemInstanceNotLike;
  private String[] porTypeIn;
  private String[] porTypeNotIn;
  private String[] porTypeLike;
  private String[] porTypeNotLike;
  private String[] porValueIn;
  private String[] porValueNotIn;
  private String[] porValueLike;
  private String[] porValueNotLike;
  private Boolean isRead;
  private Boolean isTransferred;
  private String[] attachmentClassificationIdIn;
  private String[] attachmentClassificationIdNotIn;
  private String[] attachmentClassificationNameIn;
  private String[] attachmentClassificationNameNotIn;
  private String[] attachmentClassificationKeyIn;
  private String[] attachmentClassificationKeyNotIn;
  private String[] attachmentClassificationKeyLike;
  private String[] attachmentClassificationKeyNotLike;
  private String[] attachmentClassificationNameLike;
  private String[] attachmentClassificationNameNotLike;
  private String[] attachmentChannelIn;
  private String[] attachmentChannelNotIn;
  private String[] attachmentChannelLike;
  private String[] attachmentChannelNotLike;
  private String[] attachmentReferenceIn;
  private String[] attachmentReferenceNotIn;
  private String[] attachmentReferenceLike;
  private String[] attachmentReferenceNotLike;
  private TimeInterval[] attachmentReceivedWithin;
  private TimeInterval[] attachmentReceivedNotWithin;
  private boolean withoutAttachment;
  private ObjectReference[] secondaryObjectReferences;
  private String[] sorCompanyIn;
  private String[] sorCompanyLike;
  private String[] sorValueIn;
  private String[] sorValueLike;
  private String[] sorSystemIn;
  private String[] sorSystemLike;
  private String[] sorSystemInstanceIn;
  private String[] sorSystemInstanceLike;
  private String[] sorTypeIn;
  private String[] sorTypeLike;
  private String[] custom1In;
  private boolean custom1InContainsNull;
  private String[] custom1NotIn;
  private boolean custom1NotInContainsNull;
  private String[] custom1Like;
  private String[] custom1NotLike;
  private String[] custom2In;
  private boolean custom2InContainsNull;
  private String[] custom2NotIn;
  private boolean custom2NotInContainsNull;
  private String[] custom2Like;
  private String[] custom2NotLike;
  private String[] custom3In;
  private boolean custom3InContainsNull;
  private String[] custom3NotIn;
  private boolean custom3NotInContainsNull;
  private String[] custom3Like;
  private String[] custom3NotLike;
  private String[] custom4In;
  private boolean custom4InContainsNull;
  private String[] custom4NotIn;
  private boolean custom4NotInContainsNull;
  private String[] custom4Like;
  private String[] custom4NotLike;
  private String[] custom5In;
  private boolean custom5InContainsNull;
  private String[] custom5NotIn;
  private boolean custom5NotInContainsNull;
  private String[] custom5Like;
  private String[] custom5NotLike;
  private String[] custom6In;
  private boolean custom6InContainsNull;
  private String[] custom6NotIn;
  private boolean custom6NotInContainsNull;
  private String[] custom6Like;
  private String[] custom6NotLike;
  private String[] custom7In;
  private boolean custom7InContainsNull;
  private String[] custom7NotIn;
  private boolean custom7NotInContainsNull;
  private String[] custom7Like;
  private String[] custom7NotLike;
  private String[] custom8In;
  private boolean custom8InContainsNull;
  private String[] custom8NotIn;
  private boolean custom8NotInContainsNull;
  private String[] custom8Like;
  private String[] custom8NotLike;
  private String[] custom9In;
  private boolean custom9InContainsNull;
  private String[] custom9NotIn;
  private boolean custom9NotInContainsNull;
  private String[] custom9Like;
  private String[] custom9NotLike;
  private String[] custom10In;
  private boolean custom10InContainsNull;
  private String[] custom10NotIn;
  private boolean custom10NotInContainsNull;
  private String[] custom10Like;
  private String[] custom10NotLike;
  private String[] custom11In;
  private boolean custom11InContainsNull;
  private String[] custom11NotIn;
  private boolean custom11NotInContainsNull;
  private String[] custom11Like;
  private String[] custom11NotLike;
  private String[] custom12In;
  private boolean custom12InContainsNull;
  private String[] custom12NotIn;
  private boolean custom12NotInContainsNull;
  private String[] custom12Like;
  private String[] custom12NotLike;
  private String[] custom13In;
  private boolean custom13InContainsNull;
  private String[] custom13NotIn;
  private boolean custom13NotInContainsNull;
  private String[] custom13Like;
  private String[] custom13NotLike;
  private String[] custom14In;
  private boolean custom14InContainsNull;
  private String[] custom14NotIn;
  private boolean custom14NotInContainsNull;
  private String[] custom14Like;
  private String[] custom14NotLike;
  private String[] custom15In;
  private boolean custom15InContainsNull;
  private String[] custom15NotIn;
  private boolean custom15NotInContainsNull;
  private String[] custom15Like;
  private String[] custom15NotLike;
  private String[] custom16In;
  private boolean custom16InContainsNull;
  private String[] custom16NotIn;
  private boolean custom16NotInContainsNull;
  private String[] custom16Like;
  private String[] custom16NotLike;
  private Integer[] customInt1In;
  private Integer[] customInt1NotIn;
  private IntInterval[] customInt1Within;
  private IntInterval[] customInt1NotWithin;
  private Integer[] customInt2In;
  private Integer[] customInt2NotIn;
  private IntInterval[] customInt2Within;
  private IntInterval[] customInt2NotWithin;
  private Integer[] customInt3In;
  private Integer[] customInt3NotIn;
  private IntInterval[] customInt3Within;
  private IntInterval[] customInt3NotWithin;
  private Integer[] customInt4In;
  private Integer[] customInt4NotIn;
  private IntInterval[] customInt4Within;
  private IntInterval[] customInt4NotWithin;
  private Integer[] customInt5In;
  private Integer[] customInt5NotIn;
  private IntInterval[] customInt5Within;
  private IntInterval[] customInt5NotWithin;
  private Integer[] customInt6In;
  private Integer[] customInt6NotIn;
  private IntInterval[] customInt6Within;
  private IntInterval[] customInt6NotWithin;
  private Integer[] customInt7In;
  private Integer[] customInt7NotIn;
  private IntInterval[] customInt7Within;
  private IntInterval[] customInt7NotWithin;
  private Integer[] customInt8In;
  private Integer[] customInt8NotIn;
  private IntInterval[] customInt8Within;
  private IntInterval[] customInt8NotWithin;
  // endregion
  // region callbackState
  private CallbackState[] callbackStateIn;
  private CallbackState[] callbackStateNotIn;
  private WildcardSearchField[] wildcardSearchFieldIn;
  private String wildcardSearchValueLike;

  TaskQueryImpl(InternalTaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    this.taskService = (TaskServiceImpl) taskanaEngine.getEngine().getTaskService();
    this.orderByOuter = new ArrayList<>();
    this.orderByInner = new ArrayList<>();
    this.filterByAccessIdIn = true;
    this.withoutAttachment = false;
    this.joinWithUserInfo = taskanaEngine.getEngine().getConfiguration().isAddAdditionalUserInfo();
  }

  @Override
  public TaskQuery idIn(String... taskIds) {
    this.taskId = taskIds;
    return this;
  }

  @Override
  public TaskQuery idNotIn(String... taskIds) {
    this.taskIdNotIn = taskIds;
    return this;
  }

  @Override
  public TaskQuery orderByTaskId(SortDirection sortDirection) {
    return addOrderCriteria("ID", sortDirection);
  }

  @Override
  public TaskQuery externalIdIn(String... externalIds) {
    this.externalIdIn = externalIds;
    return this;
  }

  @Override
  public TaskQuery externalIdNotIn(String... externalIds) {
    this.externalIdNotIn = externalIds;
    return this;
  }

  @Override
  public TaskQuery receivedWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.receivedWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery receivedNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.receivedNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByReceived(SortDirection sortDirection) {
    return addOrderCriteria("RECEIVED", sortDirection);
  }

  @Override
  public TaskQuery createdWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.createdWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery createdNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.createdNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByCreated(SortDirection sortDirection) {
    return addOrderCriteria("CREATED", sortDirection);
  }

  @Override
  public TaskQuery claimedWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.claimedWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery claimedNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.claimedNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByClaimed(SortDirection sortDirection) {
    return addOrderCriteria("CLAIMED", sortDirection);
  }

  @Override
  public TaskQuery modifiedWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.modifiedWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery modifiedNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.modifiedNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByModified(SortDirection sortDirection) {
    return addOrderCriteria("MODIFIED", sortDirection);
  }

  @Override
  public TaskQuery plannedWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.plannedWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery plannedNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.plannedNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByPlanned(SortDirection sortDirection) {
    return addOrderCriteria("PLANNED", sortDirection);
  }

  @Override
  public TaskQuery dueWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.dueWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery dueNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.dueNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByDue(SortDirection sortDirection) {
    return addOrderCriteria("DUE", sortDirection);
  }

  @Override
  public TaskQuery completedWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.completedWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery completedNotWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.completedNotWithin = intervals;
    return this;
  }

  @Override
  public TaskQuery orderByCompleted(SortDirection sortDirection) {
    return addOrderCriteria("COMPLETED", sortDirection);
  }

  @Override
  public TaskQuery nameIn(String... names) {
    this.nameIn = names;
    return this;
  }

  @Override
  public TaskQuery nameNotIn(String... names) {
    this.nameNotIn = names;
    return this;
  }

  @Override
  public TaskQuery nameLike(String... names) {
    this.nameLike = toLowerCopy(names);
    return this;
  }

  @Override
  public TaskQuery nameNotLike(String... names) {
    this.nameNotLike = toLowerCopy(names);
    return this;
  }

  @Override
  public TaskQuery orderByName(SortDirection sortDirection) {
    return addOrderCriteria("NAME", sortDirection);
  }

  @Override
  public TaskQuery creatorIn(String... creators) {
    this.creatorIn = creators;
    return this;
  }

  @Override
  public TaskQuery creatorNotIn(String... creators) {
    this.creatorNotIn = creators;
    return this;
  }

  @Override
  public TaskQuery creatorLike(String... creators) {
    this.creatorLike = toLowerCopy(creators);
    return this;
  }

  @Override
  public TaskQuery creatorNotLike(String... creators) {
    this.creatorNotLike = toLowerCopy(creators);
    return this;
  }

  @Override
  public TaskQuery orderByCreator(SortDirection sortDirection) {
    return addOrderCriteria("CREATOR", sortDirection);
  }

  @Override
  public TaskQuery noteLike(String... notes) {
    this.noteLike = toLowerCopy(notes);
    return this;
  }

  @Override
  public TaskQuery noteNotLike(String... notes) {
    this.noteNotLike = toLowerCopy(notes);
    return this;
  }

  @Override
  public TaskQuery orderByNote(SortDirection sortDirection) {
    return addOrderCriteria("NOTE", sortDirection);
  }

  @Override
  public TaskQuery descriptionLike(String... descriptions) {
    this.descriptionLike = toLowerCopy(descriptions);
    return this;
  }

  @Override
  public TaskQuery descriptionNotLike(String... descriptions) {
    this.descriptionNotLike = toLowerCopy(descriptions);
    return this;
  }

  @Override
  public TaskQuery priorityIn(int... priorities) {
    this.priority = priorities;
    return this;
  }

  @Override
  public TaskQuery priorityNotIn(int... priorities) {
    this.priorityNotIn = priorities;
    return this;
  }

  @Override
  public TaskQuery priorityWithin(IntInterval... priorities) {
    this.priorityWithin = priorities;
    return this;
  }

  @Override
  public TaskQuery priorityNotWithin(IntInterval... priorities) {
    this.priorityNotWithin = priorities;
    return this;
  }

  @Override
  public TaskQuery orderByPriority(SortDirection sortDirection) {
    return addOrderCriteria("PRIORITY", sortDirection);
  }

  @Override
  public TaskQuery stateIn(TaskState... states) {
    this.stateIn = states;
    return this;
  }

  @Override
  public TaskQuery stateNotIn(TaskState... states) {
    this.stateNotIn = states;
    return this;
  }

  @Override
  public TaskQuery orderByState(SortDirection sortDirection) {
    return addOrderCriteria("STATE", sortDirection);
  }

  @Override
  public TaskQuery classificationIdIn(String... classificationIds) {
    this.classificationIdIn = classificationIds;
    return this;
  }

  @Override
  public TaskQuery classificationIdNotIn(String... classificationIds) {
    this.classificationIdNotIn = classificationIds;
    return this;
  }

  @Override
  public TaskQuery classificationKeyIn(String... classificationKeys) {
    this.classificationKeyIn = classificationKeys;
    return this;
  }

  @Override
  public TaskQuery classificationKeyNotIn(String... classificationKeys) {
    this.classificationKeyNotIn = classificationKeys;
    return this;
  }

  @Override
  public TaskQuery classificationKeyLike(String... classificationKeys) {
    this.classificationKeyLike = toLowerCopy(classificationKeys);
    return this;
  }

  @Override
  public TaskQuery classificationKeyNotLike(String... classificationKeys) {
    this.classificationKeyNotLike = toLowerCopy(classificationKeys);
    return this;
  }

  @Override
  public TaskQuery orderByClassificationKey(SortDirection sortDirection) {
    return DB.DB2 == getDB()
        ? addOrderCriteria("TCLASSIFICATION_KEY", sortDirection)
        : addOrderCriteria("t.CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public TaskQuery classificationParentKeyIn(String... classificationParentKeys) {
    this.classificationParentKeyIn = classificationParentKeys;
    this.joinWithClassifications = true;
    return this;
  }

  @Override
  public TaskQuery classificationParentKeyNotIn(String... classificationParentKeys) {
    this.classificationParentKeyNotIn = classificationParentKeys;
    this.joinWithClassifications = true;
    return this;
  }

  @Override
  public TaskQuery classificationParentKeyLike(String... classificationParentKeys) {
    this.classificationParentKeyLike = toLowerCopy(classificationParentKeys);
    this.joinWithClassifications = true;
    return this;
  }

  @Override
  public TaskQuery classificationParentKeyNotLike(String... classificationParentKeys) {
    this.classificationParentKeyNotLike = toLowerCopy(classificationParentKeys);
    this.joinWithClassifications = true;
    return this;
  }

  @Override
  public TaskQuery classificationCategoryIn(String... classificationCategories) {
    this.classificationCategoryIn = classificationCategories;
    this.joinWithClassifications = true;
    return this;
  }

  @Override
  public TaskQuery classificationCategoryNotIn(String... classificationCategories) {
    this.classificationCategoryNotIn = classificationCategories;
    return this;
  }

  @Override
  public TaskQuery classificationCategoryLike(String... classificationCategories) {
    this.classificationCategoryLike = toLowerCopy(classificationCategories);
    return this;
  }

  @Override
  public TaskQuery classificationCategoryNotLike(String... classificationCategories) {
    this.classificationCategoryNotLike = toLowerCopy(classificationCategories);
    return this;
  }

  @Override
  public TaskQuery classificationNameIn(String... classificationNames) {
    joinWithClassifications = true;
    this.classificationNameIn = classificationNames;
    return this;
  }

  @Override
  public TaskQuery classificationNameNotIn(String... classificationNames) {
    joinWithClassifications = true;
    this.classificationNameNotIn = classificationNames;
    return this;
  }

  @Override
  public TaskQuery classificationNameLike(String... classificationNames) {
    joinWithClassifications = true;
    this.classificationNameLike = toLowerCopy(classificationNames);
    return this;
  }

  @Override
  public TaskQuery classificationNameNotLike(String... classificationNames) {
    joinWithClassifications = true;
    this.classificationNameNotLike = toLowerCopy(classificationNames);
    return this;
  }

  @Override
  public TaskQuery orderByClassificationName(SortDirection sortDirection) {
    joinWithClassifications = true;
    addClassificationNameToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("CNAME", sortDirection)
        : addOrderCriteria("c.NAME", sortDirection);
  }

  @Override
  public TaskQuery workbasketIdIn(String... workbasketIds) {
    this.workbasketIdIn = workbasketIds;
    return this;
  }

  @Override
  public TaskQuery workbasketIdNotIn(String... workbasketIds) {
    this.workbasketIdNotIn = workbasketIds;
    return this;
  }

  @Override
  public TaskQuery orderByWorkbasketId(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_ID", sortDirection);
  }

  @Override
  public TaskQuery workbasketKeyDomainIn(KeyDomain... workbasketIdentifiers) {
    this.workbasketKeyDomainIn = workbasketIdentifiers;
    return this;
  }

  @Override
  public TaskQuery workbasketKeyDomainNotIn(KeyDomain... workbasketIdentifiers) {
    this.workbasketKeyDomainNotIn = workbasketIdentifiers;
    return this;
  }

  @Override
  public TaskQuery businessProcessIdIn(String... businessProcessIds) {
    this.businessProcessIdIn = businessProcessIds;
    return this;
  }

  @Override
  public TaskQuery businessProcessIdNotIn(String... businessProcessIds) {
    this.businessProcessIdNotIn = businessProcessIds;
    return this;
  }

  @Override
  public TaskQuery businessProcessIdLike(String... businessProcessIds) {
    this.businessProcessIdLike = toLowerCopy(businessProcessIds);
    return this;
  }

  @Override
  public TaskQuery businessProcessIdNotLike(String... businessProcessIds) {
    this.businessProcessIdNotLike = toLowerCopy(businessProcessIds);
    return this;
  }

  @Override
  public TaskQuery orderByBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("BUSINESS_PROCESS_ID", sortDirection);
  }

  @Override
  public TaskQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds) {
    this.parentBusinessProcessIdIn = parentBusinessProcessIds;
    return this;
  }

  @Override
  public TaskQuery parentBusinessProcessIdNotIn(String... parentBusinessProcessIds) {
    this.parentBusinessProcessIdNotIn = parentBusinessProcessIds;
    return this;
  }

  @Override
  public TaskQuery parentBusinessProcessIdLike(String... businessProcessIds) {
    this.parentBusinessProcessIdLike = toLowerCopy(businessProcessIds);
    return this;
  }

  @Override
  public TaskQuery parentBusinessProcessIdNotLike(String... businessProcessIds) {
    this.parentBusinessProcessIdNotLike = toLowerCopy(businessProcessIds);
    return this;
  }

  @Override
  public TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection) {
    return addOrderCriteria("PARENT_BUSINESS_PROCESS_ID", sortDirection);
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
    this.ownerLike = toLowerCopy(owners);
    return this;
  }

  @Override
  public TaskQuery ownerNotLike(String... owners) {
    this.ownerNotLike = toLowerCopy(owners);
    return this;
  }

  @Override
  public TaskQuery orderByOwner(SortDirection sortDirection) {
    return addOrderCriteria("OWNER", sortDirection);
  }

  public TaskQuery ownerLongNameIn(String... longNames) {
    joinWithUserInfo = true;
    this.ownerLongNameIn = longNames;
    return this;
  }

  @Override
  public TaskQuery ownerLongNameNotIn(String... longNames) {
    joinWithUserInfo = true;
    this.ownerLongNameNotIn = longNames;
    return this;
  }

  @Override
  public TaskQuery ownerLongNameLike(String... longNames) {
    joinWithUserInfo = true;
    this.ownerLongNameLike = toLowerCopy(longNames);
    return this;
  }

  @Override
  public TaskQuery ownerLongNameNotLike(String... longNames) {
    joinWithUserInfo = true;
    this.ownerLongNameNotLike = toLowerCopy(longNames);
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
  public TaskQuery primaryObjectReferenceCompanyNotIn(String... companies) {
    this.porCompanyNotIn = companies;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceCompanyLike(String... companies) {
    this.porCompanyLike = toLowerCopy(companies);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceCompanyNotLike(String... companies) {
    this.porCompanyNotLike = toLowerCopy(companies);
    return this;
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceCompany(SortDirection sortDirection) {
    return addOrderCriteria("POR_COMPANY", sortDirection);
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemIn(String... systems) {
    this.porSystemIn = systems;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemNotIn(String... systems) {
    this.porSystemNotIn = systems;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemLike(String... systems) {
    this.porSystemLike = toLowerCopy(systems);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemNotLike(String... systems) {
    this.porSystemNotLike = toLowerCopy(systems);
    return this;
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceSystem(SortDirection sortDirection) {
    return addOrderCriteria("POR_SYSTEM", sortDirection);
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances) {
    this.porSystemInstanceIn = systemInstances;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemInstanceNotIn(String... systemInstances) {
    this.porSystemInstanceNotIn = systemInstances;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemInstanceLike(String... systemInstances) {
    this.porSystemInstanceLike = toLowerCopy(systemInstances);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceSystemInstanceNotLike(String... systemInstances) {
    this.porSystemInstanceNotLike = toLowerCopy(systemInstances);
    return this;
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceSystemInstance(SortDirection sortDirection) {
    return addOrderCriteria("POR_INSTANCE", sortDirection);
  }

  @Override
  public TaskQuery primaryObjectReferenceTypeIn(String... types) {
    this.porTypeIn = types;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceTypeNotIn(String... types) {
    this.porTypeNotIn = types;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceTypeLike(String... types) {
    this.porTypeLike = toLowerCopy(types);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceTypeNotLike(String... types) {
    this.porTypeNotLike = toLowerCopy(types);
    return this;
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceType(SortDirection sortDirection) {
    return addOrderCriteria("POR_TYPE", sortDirection);
  }

  @Override
  public TaskQuery primaryObjectReferenceValueIn(String... values) {
    this.porValueIn = values;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceValueNotIn(String... values) {
    this.porValueNotIn = values;
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceValueLike(String... values) {
    this.porValueLike = toLowerCopy(values);
    return this;
  }

  @Override
  public TaskQuery primaryObjectReferenceValueNotLike(String... values) {
    this.porValueNotLike = toLowerCopy(values);
    return this;
  }

  @Override
  public TaskQuery orderByPrimaryObjectReferenceValue(SortDirection sortDirection) {
    return addOrderCriteria("POR_VALUE", sortDirection);
  }

  @Override
  public TaskQuery groupByPor() {
    if (taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery()
        && getDB().equals(DB.DB2)) {
      throw new SystemException(
          "taskana.feature.useSpecificDb2Taskquery needs to be set to false "
              + "in order to group by por.");
    }
    groupByPor = true;
    return this;
  }

  @Override
  public TaskQuery groupBySor(String type) {
    if (taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery()
        && getDB().equals(DB.DB2)) {
      throw new SystemException(
          "taskana.feature.useSpecificDb2Taskquery needs to be set to false "
              + "in order to group by sor.");
    }
    groupBySor = type;
    return sorTypeIn(type);
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
  public TaskQuery attachmentClassificationIdIn(String... attachmentClassificationIds) {
    joinWithAttachments = true;
    this.attachmentClassificationIdIn = attachmentClassificationIds;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationIdNotIn(String... attachmentClassificationIds) {
    joinWithAttachments = true;
    this.attachmentClassificationIdNotIn = attachmentClassificationIds;
    return this;
  }

  @Override
  public TaskQuery orderByAttachmentClassificationId(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("ACLASSIFICATION_ID", sortDirection)
        : addOrderCriteria("a.CLASSIFICATION_ID", sortDirection);
  }

  @Override
  public TaskQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys) {
    joinWithAttachments = true;
    this.attachmentClassificationKeyIn = attachmentClassificationKeys;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationKeyNotIn(String... attachmentClassificationKeys) {
    joinWithAttachments = true;
    this.attachmentClassificationKeyNotIn = attachmentClassificationKeys;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationKeyLike(String... attachmentClassificationKeys) {
    joinWithAttachments = true;
    this.attachmentClassificationKeyLike = toLowerCopy(attachmentClassificationKeys);
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationKeyNotLike(String... attachmentClassificationKeys) {
    joinWithAttachments = true;
    this.attachmentClassificationKeyNotLike = toLowerCopy(attachmentClassificationKeys);
    return this;
  }

  @Override
  public TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("ACLASSIFICATION_KEY", sortDirection)
        : addOrderCriteria("a.CLASSIFICATION_KEY", sortDirection);
  }

  @Override
  public TaskQuery attachmentClassificationNameIn(String... attachmentClassificationNames) {
    joinWithAttachmentClassifications = true;
    this.attachmentClassificationNameIn = attachmentClassificationNames;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationNameNotIn(String... attachmentClassificationNames) {
    joinWithAttachmentClassifications = true;
    this.attachmentClassificationNameNotIn = attachmentClassificationNames;
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationNameLike(String... attachmentClassificationNames) {
    joinWithAttachmentClassifications = true;
    this.attachmentClassificationNameLike = toLowerCopy(attachmentClassificationNames);
    return this;
  }

  @Override
  public TaskQuery attachmentClassificationNameNotLike(String... attachmentClassificationNames) {
    joinWithAttachmentClassifications = true;
    this.attachmentClassificationNameNotLike = toLowerCopy(attachmentClassificationNames);
    return this;
  }

  @Override
  public TaskQuery orderByAttachmentClassificationName(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentClassificationNameToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("ACNAME", sortDirection)
        : addOrderCriteria("ac.NAME", sortDirection);
  }

  @Override
  public TaskQuery attachmentChannelIn(String... attachmentChannels) {
    joinWithAttachments = true;
    this.attachmentChannelIn = attachmentChannels;
    return this;
  }

  @Override
  public TaskQuery attachmentChannelNotIn(String... attachmentChannels) {
    joinWithAttachments = true;
    this.attachmentChannelNotIn = attachmentChannels;
    return this;
  }

  @Override
  public TaskQuery attachmentChannelLike(String... attachmentChannels) {
    joinWithAttachments = true;
    this.attachmentChannelLike = toLowerCopy(attachmentChannels);
    return this;
  }

  @Override
  public TaskQuery attachmentChannelNotLike(String... attachmentChannels) {
    joinWithAttachments = true;
    this.attachmentChannelNotLike = toLowerCopy(attachmentChannels);
    return this;
  }

  @Override
  public TaskQuery orderByAttachmentChannel(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("CHANNEL", sortDirection)
        : addOrderCriteria("a.CHANNEL", sortDirection);
  }

  @Override
  public TaskQuery attachmentReferenceValueIn(String... referenceValues) {
    joinWithAttachments = true;
    this.attachmentReferenceIn = referenceValues;
    return this;
  }

  @Override
  public TaskQuery attachmentReferenceValueNotIn(String... referenceValues) {
    joinWithAttachments = true;
    this.attachmentReferenceNotIn = referenceValues;
    return this;
  }

  @Override
  public TaskQuery attachmentReferenceValueLike(String... referenceValues) {
    joinWithAttachments = true;
    this.attachmentReferenceLike = toLowerCopy(referenceValues);
    return this;
  }

  @Override
  public TaskQuery attachmentReferenceValueNotLike(String... referenceValues) {
    joinWithAttachments = true;
    this.attachmentReferenceNotLike = toLowerCopy(referenceValues);
    return this;
  }

  @Override
  public TaskQuery orderByAttachmentReference(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("REF_VALUE", sortDirection)
        : addOrderCriteria("a.REF_VALUE", sortDirection);
  }

  @Override
  public TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn) {
    validateAllTimeIntervals(receivedIn);
    joinWithAttachments = true;
    this.attachmentReceivedWithin = receivedIn;
    return this;
  }

  @Override
  public TaskQuery attachmentNotReceivedWithin(TimeInterval... receivedNotIn) {
    validateAllTimeIntervals(receivedNotIn);
    joinWithAttachments = true;
    this.attachmentReceivedNotWithin = receivedNotIn;
    return this;
  }

  @Override
  public TaskQuery orderByAttachmentReceived(SortDirection sortDirection) {
    joinWithAttachments = true;
    addAttachmentColumnsToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("ARECEIVED", sortDirection)
        : addOrderCriteria("a.RECEIVED", sortDirection);
  }

  @Override
  public TaskQuery withoutAttachment() {
    this.joinWithAttachments = true;
    this.withoutAttachment = true;
    return this;
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

  public String[] getOwnerLongNameNotLike() {
    return ownerLongNameNotLike;
  }

  @Override
  public TaskQuery secondaryObjectReferenceIn(ObjectReference... objectReferences) {
    this.joinWithSecondaryObjectReferences = true;
    this.secondaryObjectReferences = objectReferences;
    return this;
  }

  public TaskQuery sorCompanyIn(String... companies) {
    joinWithSecondaryObjectReferences = true;
    sorCompanyIn = companies;
    return this;
  }

  public TaskQuery sorCompanyLike(String... companies) {
    joinWithSecondaryObjectReferences = true;
    sorCompanyLike = toLowerCopy(companies);
    return this;
  }

  public TaskQuery sorSystemIn(String... systems) {
    joinWithSecondaryObjectReferences = true;
    sorSystemIn = systems;
    return this;
  }

  public TaskQuery sorSystemLike(String... systems) {
    joinWithSecondaryObjectReferences = true;
    sorSystemLike = toLowerCopy(systems);
    return this;
  }

  public TaskQuery sorSystemInstanceIn(String... systemInstances) {
    joinWithSecondaryObjectReferences = true;
    sorSystemInstanceIn = systemInstances;
    return this;
  }

  public TaskQuery sorSystemInstanceLike(String... systemInstances) {
    joinWithSecondaryObjectReferences = true;
    sorSystemInstanceLike = toLowerCopy(systemInstances);
    return this;
  }

  public TaskQuery sorTypeIn(String... types) {
    joinWithSecondaryObjectReferences = true;
    sorTypeIn = types;
    return this;
  }

  public TaskQuery sorTypeLike(String... types) {
    joinWithSecondaryObjectReferences = true;
    sorTypeLike = toLowerCopy(types);
    return this;
  }

  @Override
  public TaskQuery sorValueIn(String... values) {
    joinWithSecondaryObjectReferences = true;
    sorValueIn = values;
    return this;
  }

  public TaskQuery sorValueLike(String... values) {
    joinWithSecondaryObjectReferences = true;
    sorValueLike = toLowerCopy(values);
    return this;
  }

  @Override
  public TaskQuery customAttributeIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }

    List<String> conditionList = new ArrayList<>(Arrays.asList(strings));
    boolean containsNull = conditionList.contains(null);
    if (containsNull) {
      conditionList.remove(null);
    }

    switch (customField) {
      case CUSTOM_1:
        this.custom1In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom1InContainsNull = true;
        }
        break;
      case CUSTOM_2:
        this.custom2In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom2InContainsNull = true;
        }
        break;
      case CUSTOM_3:
        this.custom3In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom3InContainsNull = true;
        }
        break;
      case CUSTOM_4:
        this.custom4In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom4InContainsNull = true;
        }
        break;
      case CUSTOM_5:
        this.custom5In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom5InContainsNull = true;
        }
        break;
      case CUSTOM_6:
        this.custom6In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom6InContainsNull = true;
        }
        break;
      case CUSTOM_7:
        this.custom7In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom7InContainsNull = true;
        }
        break;
      case CUSTOM_8:
        this.custom8In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom8InContainsNull = true;
        }
        break;
      case CUSTOM_9:
        this.custom9In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom9InContainsNull = true;
        }
        break;
      case CUSTOM_10:
        this.custom10In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom10InContainsNull = true;
        }
        break;
      case CUSTOM_11:
        this.custom11In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom11InContainsNull = true;
        }
        break;
      case CUSTOM_12:
        this.custom12In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom12InContainsNull = true;
        }
        break;
      case CUSTOM_13:
        this.custom13In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom13InContainsNull = true;
        }
        break;
      case CUSTOM_14:
        this.custom14In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom14InContainsNull = true;
        }
        break;
      case CUSTOM_15:
        this.custom15In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom15InContainsNull = true;
        }
        break;
      case CUSTOM_16:
        this.custom16In = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom16InContainsNull = true;
        }
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
    List<String> conditionList = new ArrayList<>(Arrays.asList(strings));
    boolean containsNull = conditionList.contains(null);
    if (containsNull) {
      conditionList.remove(null);
    }
    switch (customField) {
      case CUSTOM_1:
        this.custom1NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom1NotInContainsNull = true;
        }
        break;
      case CUSTOM_2:
        this.custom2NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom2NotInContainsNull = true;
        }
        break;
      case CUSTOM_3:
        this.custom3NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom3NotInContainsNull = true;
        }
        break;
      case CUSTOM_4:
        this.custom4NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom4NotInContainsNull = true;
        }
        break;
      case CUSTOM_5:
        this.custom5NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom5NotInContainsNull = true;
        }
        break;
      case CUSTOM_6:
        this.custom6NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom6NotInContainsNull = true;
        }
        break;
      case CUSTOM_7:
        this.custom7NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom7NotInContainsNull = true;
        }
        break;
      case CUSTOM_8:
        this.custom8NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom8NotInContainsNull = true;
        }
        break;
      case CUSTOM_9:
        this.custom9NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom9NotInContainsNull = true;
        }
        break;
      case CUSTOM_10:
        this.custom10NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom10NotInContainsNull = true;
        }
        break;
      case CUSTOM_11:
        this.custom11NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom11NotInContainsNull = true;
        }
        break;
      case CUSTOM_12:
        this.custom12NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom12NotInContainsNull = true;
        }
        break;
      case CUSTOM_13:
        this.custom13NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom13NotInContainsNull = true;
        }
        break;
      case CUSTOM_14:
        this.custom14NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom14NotInContainsNull = true;
        }
        break;
      case CUSTOM_15:
        this.custom15NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom15NotInContainsNull = true;
        }
        break;
      case CUSTOM_16:
        this.custom16NotIn = conditionList.toArray(new String[0]);
        if (containsNull) {
          this.custom16NotInContainsNull = true;
        }
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
        this.custom1Like = toLowerCopy(strings);
        break;
      case CUSTOM_2:
        this.custom2Like = toLowerCopy(strings);
        break;
      case CUSTOM_3:
        this.custom3Like = toLowerCopy(strings);
        break;
      case CUSTOM_4:
        this.custom4Like = toLowerCopy(strings);
        break;
      case CUSTOM_5:
        this.custom5Like = toLowerCopy(strings);
        break;
      case CUSTOM_6:
        this.custom6Like = toLowerCopy(strings);
        break;
      case CUSTOM_7:
        this.custom7Like = toLowerCopy(strings);
        break;
      case CUSTOM_8:
        this.custom8Like = toLowerCopy(strings);
        break;
      case CUSTOM_9:
        this.custom9Like = toLowerCopy(strings);
        break;
      case CUSTOM_10:
        this.custom10Like = toLowerCopy(strings);
        break;
      case CUSTOM_11:
        this.custom11Like = toLowerCopy(strings);
        break;
      case CUSTOM_12:
        this.custom12Like = toLowerCopy(strings);
        break;
      case CUSTOM_13:
        this.custom13Like = toLowerCopy(strings);
        break;
      case CUSTOM_14:
        this.custom14Like = toLowerCopy(strings);
        break;
      case CUSTOM_15:
        this.custom15Like = toLowerCopy(strings);
        break;
      case CUSTOM_16:
        this.custom16Like = toLowerCopy(strings);
        break;
      default:
        throw new SystemException("Unknown custom field '" + customField + "'");
    }

    return this;
  }

  @Override
  public TaskQuery customAttributeNotLike(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }

    switch (customField) {
      case CUSTOM_1:
        this.custom1NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_2:
        this.custom2NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_3:
        this.custom3NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_4:
        this.custom4NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_5:
        this.custom5NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_6:
        this.custom6NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_7:
        this.custom7NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_8:
        this.custom8NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_9:
        this.custom9NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_10:
        this.custom10NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_11:
        this.custom11NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_12:
        this.custom12NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_13:
        this.custom13NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_14:
        this.custom14NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_15:
        this.custom15NotLike = toLowerCopy(strings);
        break;
      case CUSTOM_16:
        this.custom16NotLike = toLowerCopy(strings);
        break;
      default:
        throw new SystemException("Unknown custom field '" + customField + "'");
    }

    return this;
  }

  @Override
  public TaskQuery orderByCustomAttribute(
      TaskCustomField customField, SortDirection sortDirection) {
    return addOrderCriteria(customField.name(), sortDirection);
  }

  @Override
  public TaskQuery customIntAttributeNotIn(TaskCustomIntField customIntField, Integer... values)
      throws InvalidArgumentException {
    if (values.length == 0) {
      throw new InvalidArgumentException(
          "At least one Integer has to be provided as a search parameter");
    }
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1NotIn = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2NotIn = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3NotIn = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4NotIn = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5NotIn = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6NotIn = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7NotIn = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8NotIn = values;
        break;
      default:
        throw new SystemException("Unknown custom int field '" + customIntField + "'");
    }
    return this;
  }

  @Override
  public TaskQuery customIntAttributeIn(TaskCustomIntField customIntField, Integer... values)
      throws InvalidArgumentException {
    if (values.length == 0) {
      throw new InvalidArgumentException(
          "At least one Integer has to be provided as a search parameter");
    }

    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1In = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2In = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3In = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4In = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5In = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6In = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7In = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8In = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }

    return this;
  }

  @Override
  public TaskQuery orderByCustomIntAttribute(
      TaskCustomIntField customIntField, SortDirection sortDirection) {
    return addOrderCriteria(customIntField.name(), sortDirection);
  }

  @Override
  public TaskQuery customIntAttributeWithin(
      TaskCustomIntField customIntField, IntInterval... values) {
    validateAllIntIntervals(values);
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1Within = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2Within = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3Within = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4Within = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5Within = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6Within = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7Within = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8Within = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }
    return this;
  }

  @Override
  public TaskQuery customIntAttributeNotWithin(
      TaskCustomIntField customIntField, IntInterval... values) {
    validateAllIntIntervals(values);
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1NotWithin = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2NotWithin = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3NotWithin = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4NotWithin = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5NotWithin = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6NotWithin = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7NotWithin = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8NotWithin = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }
    return this;
  }

  @Override
  public TaskQuery callbackStateIn(CallbackState... states) {
    this.callbackStateIn = states;
    return this;
  }

  @Override
  public TaskQuery callbackStateNotIn(CallbackState... states) {
    this.callbackStateNotIn = states;
    return this;
  }

  @Override
  public TaskQuery wildcardSearchValueLike(String wildcardSearchValue) {
    this.wildcardSearchValueLike = wildcardSearchValue.toLowerCase();
    return this;
  }

  @Override
  public TaskQuery wildcardSearchFieldsIn(WildcardSearchField... wildcardSearchFields) {
    this.wildcardSearchFieldIn = wildcardSearchFields;
    return this;
  }

  @Override
  public ObjectReferenceQuery createObjectReferenceQuery() {
    return new ObjectReferenceQueryImpl(taskanaEngine);
  }

  @Override
  public TaskQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public TaskQuery orderByWorkbasketKey(SortDirection sortDirection) {
    return addOrderCriteria("WORKBASKET_KEY", sortDirection);
  }

  @Override
  public TaskQuery orderByWorkbasketName(SortDirection sortDirection) {
    joinWithWorkbaskets = true;
    addWorkbasketNameToSelectClauseForOrdering = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("WNAME", sortDirection)
        : addOrderCriteria("w.NAME", sortDirection);
  }

  @Override
  public TaskQuery orderByOwnerLongName(SortDirection sortDirection) {
    joinWithUserInfo = true;
    return (DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery())
        ? addOrderCriteria("ULONG_NAME", sortDirection)
        : addOrderCriteria("u.LONG_NAME", sortDirection);
  }

  public List<TaskSummary> list() {
    return taskanaEngine.executeInDatabaseConnection(
        () -> {
          checkForIllegalParamCombinations();
          checkOpenReadAndReadTasksPermissionForSpecifiedWorkbaskets();
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
      checkOpenReadAndReadTasksPermissionForSpecifiedWorkbaskets();
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
      this.orderByOuter.clear();
      this.orderByInner.clear();
      this.addOrderCriteria(columnName.toString(), sortDirection);
      checkForIllegalParamCombinations();
      checkOpenReadAndReadTasksPermissionForSpecifiedWorkbaskets();
      setupAccessIds();

      if (columnName.equals(TaskQueryColumnName.CLASSIFICATION_NAME)) {
        joinWithClassifications = true;
      }

      if (columnName.equals(TaskQueryColumnName.A_CLASSIFICATION_NAME)) {
        joinWithAttachmentClassifications = true;
      }

      if (columnName.isAttachmentColumn()) {
        joinWithAttachments = true;
      }

      if (columnName.isObjectReferenceColumn()) {
        joinWithSecondaryObjectReferences = true;
      }

      if (columnName == TaskQueryColumnName.OWNER_LONG_NAME) {
        joinWithUserInfo = true;
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
      checkOpenReadAndReadTasksPermissionForSpecifiedWorkbaskets();
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
      checkOpenReadAndReadTasksPermissionForSpecifiedWorkbaskets();
      setupAccessIds();
      setupJoinAndOrderParameters();
      rowCount = taskanaEngine.getSqlSession().selectOne(getLinkToCounterTaskScript(), this);
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  public TaskQuery selectAndClaimEquals(boolean selectAndClaim) {
    this.selectAndClaim = selectAndClaim;
    return this;
  }

  // optimized query for db2 can't be used for now in case of selectAndClaim because of temporary
  // tables and the "for update" clause clashing in db2
  private String getLinkToMapperScript() {
    if (DB.DB2 == getDB()
        && !selectAndClaim
        && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery()) {
      return LINK_TO_MAPPER_DB2;
    } else if (selectAndClaim && DB.ORACLE == getDB()) {
      return LINK_TO_MAPPER_ORACLE;
    } else {
      return LINK_TO_MAPPER;
    }
  }

  private String getLinkToCounterTaskScript() {
    return DB.DB2 == getDB()
            && taskanaEngine.getEngine().getConfiguration().isUseSpecificDb2Taskquery()
        ? LINK_TO_COUNTER_DB2
        : LINK_TO_COUNTER;
  }

  private void validateAllTimeIntervals(TimeInterval[] intervals) {
    for (TimeInterval ti : intervals) {
      if (!ti.isValid()) {
        throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
      }
    }
  }

  private void validateAllIntIntervals(IntInterval[] intervals) {
    for (IntInterval ti : intervals) {
      if (!ti.isValid()) {
        throw new IllegalArgumentException("IntInterval " + ti + " is invalid.");
      }
    }
  }

  private void checkForIllegalParamCombinations() {
    if (wildcardSearchValueLike != null ^ wildcardSearchFieldIn != null) {
      throw new IllegalArgumentException(
          "The params \"wildcardSearchFieldIn\" and \"wildcardSearchValueLike\""
              + " must be used together!");
    }
    if (withoutAttachment
        && (attachmentChannelIn != null
            || attachmentChannelLike != null
            || attachmentChannelNotIn != null
            || attachmentChannelNotLike != null
            || attachmentClassificationIdIn != null
            || attachmentClassificationIdNotIn != null
            || attachmentClassificationKeyIn != null
            || attachmentClassificationKeyLike != null
            || attachmentClassificationKeyNotIn != null
            || attachmentClassificationKeyNotLike != null
            || attachmentClassificationNameIn != null
            || attachmentClassificationNameLike != null
            || attachmentClassificationNameNotIn != null
            || attachmentClassificationNameNotLike != null
            || attachmentReceivedWithin != null
            || attachmentReceivedNotWithin != null
            || attachmentReferenceIn != null
            || attachmentReferenceLike != null
            || attachmentReferenceNotIn != null
            || attachmentReferenceNotLike != null)) {
      throw new IllegalArgumentException(
          "The param \"withoutAttachment\" can only be used "
              + "without adding attributes of attachment as filter parameter");
    }
  }

  private DB getDB() {
    return DB.getDB(this.taskanaEngine.getSqlSession().getConfiguration().getDatabaseId());
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
    if (joinWithAttachments || joinWithClassifications || joinWithSecondaryObjectReferences) {
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

  private void checkOpenReadAndReadTasksPermissionForSpecifiedWorkbaskets() {
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
          checkOpenReadAndReadTasksPermissionById(workbasketId);
        }
      }
      if (workbasketKeyDomainIn != null && workbasketKeyDomainIn.length > 0) {
        filterByAccessIdIn = false;
        for (KeyDomain keyDomain : workbasketKeyDomainIn) {
          checkOpenReadAndReadTasksPermissionByKeyDomain(keyDomain);
        }
      }
    } catch (NotAuthorizedOnWorkbasketException e) {
      throw new NotAuthorizedToQueryWorkbasketException(e.getMessage(), e.getErrorCode(), e);
    }
  }

  private void checkOpenReadAndReadTasksPermissionById(String workbasketId)
      throws NotAuthorizedOnWorkbasketException {
    try {
      taskanaEngine
          .getEngine()
          .getWorkbasketService()
          .checkAuthorization(
              workbasketId,
              WorkbasketPermission.OPEN,
              WorkbasketPermission.READ,
              WorkbasketPermission.READTASKS);
    } catch (WorkbasketNotFoundException e) {
      LOGGER.warn(
          String.format("The workbasket with the ID ' %s ' does not exist.", workbasketId), e);
    }
  }

  private void checkOpenReadAndReadTasksPermissionByKeyDomain(KeyDomain keyDomain)
      throws NotAuthorizedOnWorkbasketException {
    try {
      taskanaEngine
          .getEngine()
          .getWorkbasketService()
          .checkAuthorization(
              keyDomain.getKey(),
              keyDomain.getDomain(),
              WorkbasketPermission.OPEN,
              WorkbasketPermission.READ,
              WorkbasketPermission.READTASKS);
    } catch (WorkbasketNotFoundException e) {
      LOGGER.warn(
          String.format(
              "The workbasket with the KEY ' %s ' and DOMAIN ' %s ' does not exist.",
              keyDomain.getKey(), keyDomain.getDomain()),
          e);
    }
  }

  private TaskQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
    if (sortDirection == null) {
      sortDirection = SortDirection.ASCENDING;
    }
    orderByInner.add(columnName + " " + sortDirection);
    if (columnName.startsWith("a") || columnName.startsWith("w") || columnName.startsWith("c")) {
      orderByOuter.add(columnName.replace(".", "").toUpperCase() + " " + sortDirection);
    } else {
      if (columnName.startsWith("u")) {
        orderByOuter.add(columnName.replace(".", "").substring(1) + " " + sortDirection);
      } else {
        orderByOuter.add(columnName + " " + sortDirection);
      }
    }
    return this;
  }

  @Override
  public String toString() {
    return "TaskQueryImpl [taskanaEngine="
        + taskanaEngine
        + ", taskService="
        + taskService
        + ", orderByOuter="
        + orderByOuter
        + ", columnName="
        + columnName
        + ", accessIdIn="
        + Arrays.toString(accessIdIn)
        + ", filterByAccessIdIn="
        + filterByAccessIdIn
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
        + ", joinWithSecondaryObjectReferences="
        + joinWithSecondaryObjectReferences
        + ", addAttachmentColumnsToSelectClauseForOrdering="
        + addAttachmentColumnsToSelectClauseForOrdering
        + ", addClassificationNameToSelectClauseForOrdering="
        + addClassificationNameToSelectClauseForOrdering
        + ", addAttachmentClassificationNameToSelectClauseForOrdering="
        + addAttachmentClassificationNameToSelectClauseForOrdering
        + ", addWorkbasketNameToSelectClauseForOrdering="
        + addWorkbasketNameToSelectClauseForOrdering
        + ", groupByPor="
        + groupByPor
        + ", groupBySor="
        + groupBySor
        + ", taskId="
        + Arrays.toString(taskId)
        + ", taskIdNotIn="
        + Arrays.toString(taskIdNotIn)
        + ", externalIdIn="
        + Arrays.toString(externalIdIn)
        + ", externalIdNotIn="
        + Arrays.toString(externalIdNotIn)
        + ", receivedWithin="
        + Arrays.toString(receivedWithin)
        + ", receivedNotWithin="
        + Arrays.toString(receivedNotWithin)
        + ", createdWithin="
        + Arrays.toString(createdWithin)
        + ", createdNotWithin="
        + Arrays.toString(createdNotWithin)
        + ", claimedWithin="
        + Arrays.toString(claimedWithin)
        + ", claimedNotWithin="
        + Arrays.toString(claimedNotWithin)
        + ", modifiedWithin="
        + Arrays.toString(modifiedWithin)
        + ", modifiedNotWithin="
        + Arrays.toString(modifiedNotWithin)
        + ", plannedWithin="
        + Arrays.toString(plannedWithin)
        + ", plannedNotWithin="
        + Arrays.toString(plannedNotWithin)
        + ", dueWithin="
        + Arrays.toString(dueWithin)
        + ", dueNotWithin="
        + Arrays.toString(dueNotWithin)
        + ", completedWithin="
        + Arrays.toString(completedWithin)
        + ", completedNotWithin="
        + Arrays.toString(completedNotWithin)
        + ", nameIn="
        + Arrays.toString(nameIn)
        + ", nameNotIn="
        + Arrays.toString(nameNotIn)
        + ", nameLike="
        + Arrays.toString(nameLike)
        + ", nameNotLike="
        + Arrays.toString(nameNotLike)
        + ", creatorIn="
        + Arrays.toString(creatorIn)
        + ", creatorNotIn="
        + Arrays.toString(creatorNotIn)
        + ", creatorLike="
        + Arrays.toString(creatorLike)
        + ", creatorNotLike="
        + Arrays.toString(creatorNotLike)
        + ", noteLike="
        + Arrays.toString(noteLike)
        + ", noteNotLike="
        + Arrays.toString(noteNotLike)
        + ", descriptionLike="
        + Arrays.toString(descriptionLike)
        + ", descriptionNotLike="
        + Arrays.toString(descriptionNotLike)
        + ", priority="
        + Arrays.toString(priority)
        + ", priorityNotIn="
        + Arrays.toString(priorityNotIn)
        + ", priorityWithin="
        + Arrays.toString(priorityWithin)
        + ", priorityNotWithin="
        + Arrays.toString(priorityNotWithin)
        + ", stateIn="
        + Arrays.toString(stateIn)
        + ", stateNotIn="
        + Arrays.toString(stateNotIn)
        + ", classificationIdIn="
        + Arrays.toString(classificationIdIn)
        + ", classificationIdNotIn="
        + Arrays.toString(classificationIdNotIn)
        + ", classificationKeyIn="
        + Arrays.toString(classificationKeyIn)
        + ", classificationKeyNotIn="
        + Arrays.toString(classificationKeyNotIn)
        + ", classificationKeyLike="
        + Arrays.toString(classificationKeyLike)
        + ", classificationKeyNotLike="
        + Arrays.toString(classificationKeyNotLike)
        + ", classificationParentKeyIn="
        + Arrays.toString(classificationParentKeyIn)
        + ", classificationParentKeyNotIn="
        + Arrays.toString(classificationParentKeyNotIn)
        + ", classificationParentKeyLike="
        + Arrays.toString(classificationParentKeyLike)
        + ", classificationParentKeyNotLike="
        + Arrays.toString(classificationParentKeyNotLike)
        + ", classificationCategoryIn="
        + Arrays.toString(classificationCategoryIn)
        + ", classificationCategoryNotIn="
        + Arrays.toString(classificationCategoryNotIn)
        + ", classificationCategoryLike="
        + Arrays.toString(classificationCategoryLike)
        + ", classificationCategoryNotLike="
        + Arrays.toString(classificationCategoryNotLike)
        + ", classificationNameIn="
        + Arrays.toString(classificationNameIn)
        + ", classificationNameNotIn="
        + Arrays.toString(classificationNameNotIn)
        + ", classificationNameLike="
        + Arrays.toString(classificationNameLike)
        + ", classificationNameNotLike="
        + Arrays.toString(classificationNameNotLike)
        + ", workbasketIdIn="
        + Arrays.toString(workbasketIdIn)
        + ", workbasketIdNotIn="
        + Arrays.toString(workbasketIdNotIn)
        + ", workbasketKeyDomainIn="
        + Arrays.toString(workbasketKeyDomainIn)
        + ", workbasketKeyDomainNotIn="
        + Arrays.toString(workbasketKeyDomainNotIn)
        + ", businessProcessIdIn="
        + Arrays.toString(businessProcessIdIn)
        + ", businessProcessIdNotIn="
        + Arrays.toString(businessProcessIdNotIn)
        + ", businessProcessIdLike="
        + Arrays.toString(businessProcessIdLike)
        + ", businessProcessIdNotLike="
        + Arrays.toString(businessProcessIdNotLike)
        + ", parentBusinessProcessIdIn="
        + Arrays.toString(parentBusinessProcessIdIn)
        + ", parentBusinessProcessIdNotIn="
        + Arrays.toString(parentBusinessProcessIdNotIn)
        + ", parentBusinessProcessIdLike="
        + Arrays.toString(parentBusinessProcessIdLike)
        + ", parentBusinessProcessIdNotLike="
        + Arrays.toString(parentBusinessProcessIdNotLike)
        + ", ownerIn="
        + Arrays.toString(ownerIn)
        + ", ownerNotIn="
        + Arrays.toString(ownerNotIn)
        + ", ownerLike="
        + Arrays.toString(ownerLike)
        + ", ownerNotLike="
        + Arrays.toString(ownerNotLike)
        + ", ownerLongNameIn="
        + Arrays.toString(ownerLongNameIn)
        + ", ownerLongNameNotIn="
        + Arrays.toString(ownerLongNameNotIn)
        + ", ownerLongNameLike="
        + Arrays.toString(ownerLongNameLike)
        + ", ownerLongNameNotLike="
        + Arrays.toString(ownerLongNameNotLike)
        + ", objectReferences="
        + Arrays.toString(objectReferences)
        + ", porCompanyIn="
        + Arrays.toString(porCompanyIn)
        + ", porCompanyNotIn="
        + Arrays.toString(porCompanyNotIn)
        + ", porCompanyLike="
        + Arrays.toString(porCompanyLike)
        + ", porCompanyNotLike="
        + Arrays.toString(porCompanyNotLike)
        + ", porSystemIn="
        + Arrays.toString(porSystemIn)
        + ", porSystemNotIn="
        + Arrays.toString(porSystemNotIn)
        + ", porSystemLike="
        + Arrays.toString(porSystemLike)
        + ", porSystemNotLike="
        + Arrays.toString(porSystemNotLike)
        + ", porSystemInstanceIn="
        + Arrays.toString(porSystemInstanceIn)
        + ", porSystemInstanceNotIn="
        + Arrays.toString(porSystemInstanceNotIn)
        + ", porSystemInstanceLike="
        + Arrays.toString(porSystemInstanceLike)
        + ", porSystemInstanceNotLike="
        + Arrays.toString(porSystemInstanceNotLike)
        + ", porTypeIn="
        + Arrays.toString(porTypeIn)
        + ", porTypeNotIn="
        + Arrays.toString(porTypeNotIn)
        + ", porTypeLike="
        + Arrays.toString(porTypeLike)
        + ", porTypeNotLike="
        + Arrays.toString(porTypeNotLike)
        + ", porValueIn="
        + Arrays.toString(porValueIn)
        + ", porValueNotIn="
        + Arrays.toString(porValueNotIn)
        + ", porValueLike="
        + Arrays.toString(porValueLike)
        + ", porValueNotLike="
        + Arrays.toString(porValueNotLike)
        + ", isRead="
        + isRead
        + ", isTransferred="
        + isTransferred
        + ", attachmentClassificationIdIn="
        + Arrays.toString(attachmentClassificationIdIn)
        + ", attachmentClassificationIdNotIn="
        + Arrays.toString(attachmentClassificationIdNotIn)
        + ", attachmentClassificationNameIn="
        + Arrays.toString(attachmentClassificationNameIn)
        + ", attachmentClassificationNameNotIn="
        + Arrays.toString(attachmentClassificationNameNotIn)
        + ", attachmentClassificationKeyIn="
        + Arrays.toString(attachmentClassificationKeyIn)
        + ", attachmentClassificationKeyNotIn="
        + Arrays.toString(attachmentClassificationKeyNotIn)
        + ", attachmentClassificationKeyLike="
        + Arrays.toString(attachmentClassificationKeyLike)
        + ", attachmentClassificationKeyNotLike="
        + Arrays.toString(attachmentClassificationKeyNotLike)
        + ", attachmentClassificationNameLike="
        + Arrays.toString(attachmentClassificationNameLike)
        + ", attachmentClassificationNameNotLike="
        + Arrays.toString(attachmentClassificationNameNotLike)
        + ", attachmentChannelIn="
        + Arrays.toString(attachmentChannelIn)
        + ", attachmentChannelNotIn="
        + Arrays.toString(attachmentChannelNotIn)
        + ", attachmentChannelLike="
        + Arrays.toString(attachmentChannelLike)
        + ", attachmentChannelNotLike="
        + Arrays.toString(attachmentChannelNotLike)
        + ", attachmentReferenceIn="
        + Arrays.toString(attachmentReferenceIn)
        + ", attachmentReferenceNotIn="
        + Arrays.toString(attachmentReferenceNotIn)
        + ", attachmentReferenceLike="
        + Arrays.toString(attachmentReferenceLike)
        + ", attachmentReferenceNotLike="
        + Arrays.toString(attachmentReferenceNotLike)
        + ", attachmentReceivedWithin="
        + Arrays.toString(attachmentReceivedWithin)
        + ", attachmentReceivedNotWithin="
        + Arrays.toString(attachmentReceivedNotWithin)
        + ", withoutAttachment="
        + withoutAttachment
        + ", secondaryObjectReferences="
        + Arrays.toString(secondaryObjectReferences)
        + ", sorCompanyIn="
        + Arrays.toString(sorCompanyIn)
        + ", sorCompanyLike="
        + Arrays.toString(sorCompanyLike)
        + ", sorSystemIn="
        + Arrays.toString(sorSystemIn)
        + ", sorSystemNotIn="
        + Arrays.toString(sorSystemLike)
        + ", sorSystemNotLike="
        + Arrays.toString(sorSystemInstanceIn)
        + ", sorSystemInstanceLike="
        + Arrays.toString(sorSystemInstanceLike)
        + ", sorTypeIn="
        + Arrays.toString(sorTypeIn)
        + ", sorTypeLike="
        + Arrays.toString(sorTypeLike)
        + ", sorValueIn="
        + Arrays.toString(sorValueIn)
        + ", sorValueLike="
        + Arrays.toString(sorValueLike)
        + ", custom1In="
        + Arrays.toString(custom1In)
        + ", custom1NotIn="
        + Arrays.toString(custom1NotIn)
        + ", custom1Like="
        + Arrays.toString(custom1Like)
        + ", custom1NotLike="
        + Arrays.toString(custom1NotLike)
        + ", custom2In="
        + Arrays.toString(custom2In)
        + ", custom2NotIn="
        + Arrays.toString(custom2NotIn)
        + ", custom2Like="
        + Arrays.toString(custom2Like)
        + ", custom2NotLike="
        + Arrays.toString(custom2NotLike)
        + ", custom3In="
        + Arrays.toString(custom3In)
        + ", custom3NotIn="
        + Arrays.toString(custom3NotIn)
        + ", custom3Like="
        + Arrays.toString(custom3Like)
        + ", custom3NotLike="
        + Arrays.toString(custom3NotLike)
        + ", custom4In="
        + Arrays.toString(custom4In)
        + ", custom4NotIn="
        + Arrays.toString(custom4NotIn)
        + ", custom4Like="
        + Arrays.toString(custom4Like)
        + ", custom4NotLike="
        + Arrays.toString(custom4NotLike)
        + ", custom5In="
        + Arrays.toString(custom5In)
        + ", custom5NotIn="
        + Arrays.toString(custom5NotIn)
        + ", custom5Like="
        + Arrays.toString(custom5Like)
        + ", custom5NotLike="
        + Arrays.toString(custom5NotLike)
        + ", custom6In="
        + Arrays.toString(custom6In)
        + ", custom6NotIn="
        + Arrays.toString(custom6NotIn)
        + ", custom6Like="
        + Arrays.toString(custom6Like)
        + ", custom6NotLike="
        + Arrays.toString(custom6NotLike)
        + ", custom7In="
        + Arrays.toString(custom7In)
        + ", custom7NotIn="
        + Arrays.toString(custom7NotIn)
        + ", custom7Like="
        + Arrays.toString(custom7Like)
        + ", custom7NotLike="
        + Arrays.toString(custom7NotLike)
        + ", custom8In="
        + Arrays.toString(custom8In)
        + ", custom8NotIn="
        + Arrays.toString(custom8NotIn)
        + ", custom8Like="
        + Arrays.toString(custom8Like)
        + ", custom8NotLike="
        + Arrays.toString(custom8NotLike)
        + ", custom9In="
        + Arrays.toString(custom9In)
        + ", custom9NotIn="
        + Arrays.toString(custom9NotIn)
        + ", custom9Like="
        + Arrays.toString(custom9Like)
        + ", custom9NotLike="
        + Arrays.toString(custom9NotLike)
        + ", custom10In="
        + Arrays.toString(custom10In)
        + ", custom10NotIn="
        + Arrays.toString(custom10NotIn)
        + ", custom10Like="
        + Arrays.toString(custom10Like)
        + ", custom10NotLike="
        + Arrays.toString(custom10NotLike)
        + ", custom11In="
        + Arrays.toString(custom11In)
        + ", custom11NotIn="
        + Arrays.toString(custom11NotIn)
        + ", custom11Like="
        + Arrays.toString(custom11Like)
        + ", custom11NotLike="
        + Arrays.toString(custom11NotLike)
        + ", custom12In="
        + Arrays.toString(custom12In)
        + ", custom12NotIn="
        + Arrays.toString(custom12NotIn)
        + ", custom12Like="
        + Arrays.toString(custom12Like)
        + ", custom12NotLike="
        + Arrays.toString(custom12NotLike)
        + ", custom13In="
        + Arrays.toString(custom13In)
        + ", custom13NotIn="
        + Arrays.toString(custom13NotIn)
        + ", custom13Like="
        + Arrays.toString(custom13Like)
        + ", custom13NotLike="
        + Arrays.toString(custom13NotLike)
        + ", custom14In="
        + Arrays.toString(custom14In)
        + ", custom14NotIn="
        + Arrays.toString(custom14NotIn)
        + ", custom14Like="
        + Arrays.toString(custom14Like)
        + ", custom14NotLike="
        + Arrays.toString(custom14NotLike)
        + ", custom15In="
        + Arrays.toString(custom15In)
        + ", custom15NotIn="
        + Arrays.toString(custom15NotIn)
        + ", custom15Like="
        + Arrays.toString(custom15Like)
        + ", custom15NotLike="
        + Arrays.toString(custom15NotLike)
        + ", custom16In="
        + Arrays.toString(custom16In)
        + ", custom16NotIn="
        + Arrays.toString(custom16NotIn)
        + ", custom16Like="
        + Arrays.toString(custom16Like)
        + ", custom16NotLike="
        + Arrays.toString(custom16NotLike)
        + ", customInt1In="
        + Arrays.toString(customInt1In)
        + ", customInt1NotIn="
        + Arrays.toString(customInt1NotIn)
        + ", customInt1Within="
        + Arrays.toString(customInt1Within)
        + ", customInt1NotWithin="
        + Arrays.toString(customInt1NotWithin)
        + ", customInt2In="
        + Arrays.toString(customInt2In)
        + ", customInt2NotIn="
        + Arrays.toString(customInt2NotIn)
        + ", customInt2Within="
        + Arrays.toString(customInt2Within)
        + ", customInt2NotWithin="
        + Arrays.toString(customInt2NotWithin)
        + ", customInt3In="
        + Arrays.toString(customInt3In)
        + ", customInt3NotIn="
        + Arrays.toString(customInt3NotIn)
        + ", customInt3Within="
        + Arrays.toString(customInt3Within)
        + ", customInt3NotWithin="
        + Arrays.toString(customInt3NotWithin)
        + ", customInt4In="
        + Arrays.toString(customInt4In)
        + ", customInt4NotIn="
        + Arrays.toString(customInt4NotIn)
        + ", customInt4Within="
        + Arrays.toString(customInt4Within)
        + ", customInt4NotWithin="
        + Arrays.toString(customInt4NotWithin)
        + ", customInt5In="
        + Arrays.toString(customInt5In)
        + ", customInt5NotIn="
        + Arrays.toString(customInt5NotIn)
        + ", customInt5Within="
        + Arrays.toString(customInt5Within)
        + ", customInt5NotWithin="
        + Arrays.toString(customInt5NotWithin)
        + ", customInt6In="
        + Arrays.toString(customInt6In)
        + ", customInt6NotIn="
        + Arrays.toString(customInt6NotIn)
        + ", customInt6Within="
        + Arrays.toString(customInt6Within)
        + ", customInt6NotWithin="
        + Arrays.toString(customInt6NotWithin)
        + ", customInt7In="
        + Arrays.toString(customInt7In)
        + ", customInt7NotIn="
        + Arrays.toString(customInt7NotIn)
        + ", customInt7Within="
        + Arrays.toString(customInt7Within)
        + ", customInt7NotWithin="
        + Arrays.toString(customInt7NotWithin)
        + ", customInt8In="
        + Arrays.toString(customInt8In)
        + ", customInt8NotIn="
        + Arrays.toString(customInt8NotIn)
        + ", customInt8Within="
        + Arrays.toString(customInt8Within)
        + ", customInt8NotWithin="
        + Arrays.toString(customInt8NotWithin)
        + ", callbackStateIn="
        + Arrays.toString(callbackStateIn)
        + ", callbackStateNotIn="
        + Arrays.toString(callbackStateNotIn)
        + ", wildcardSearchFieldIn="
        + Arrays.toString(wildcardSearchFieldIn)
        + ", wildcardSearchValueLike="
        + wildcardSearchValueLike
        + ", joinWithUserInfo="
        + joinWithUserInfo
        + "]";
  }
}
