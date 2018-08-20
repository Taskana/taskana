package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.KeyDomain;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskQuery;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.TimeInterval;
import pro.taskana.WorkbasketPermission;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.security.CurrentUserContext;

/**
 * TaskQuery for generating dynamic sql.
 */
public class TaskQueryImpl implements TaskQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.mappings.QueryMapper.queryTaskSummaries";
    private static final String LINK_TO_MAPPER_DB2 = "pro.taskana.mappings.QueryMapper.queryTaskSummariesDb2";
    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryTasks";
    private static final String LINK_TO_COUNTER_DB2 = "pro.taskana.mappings.QueryMapper.countQueryTasksDb2";
    private static final String LINK_TO_VALUEMAPPER = "pro.taskana.mappings.QueryMapper.queryTaskColumnValues";
    private static final String TIME_INTERVAL = "TimeInterval ";
    private static final String IS_INVALID = " is invalid.";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueryImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private TaskServiceImpl taskService;
    private String columnName;
    private String[] nameIn;
    private String[] nameLike;
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
    private String[] ownerIn;
    private String[] ownerLike;
    private Boolean isRead;
    private Boolean isTransferred;
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
    private String[] custom1In;
    private String[] custom1Like;
    private String[] custom2In;
    private String[] custom2Like;
    private String[] custom3In;
    private String[] custom3Like;
    private String[] custom4In;
    private String[] custom4Like;
    private String[] custom5In;
    private String[] custom5Like;
    private String[] custom6In;
    private String[] custom6Like;
    private String[] custom7In;
    private String[] custom7Like;
    private String[] custom8In;
    private String[] custom8Like;
    private String[] custom9In;
    private String[] custom9Like;
    private String[] custom10In;
    private String[] custom10Like;
    private String[] custom11In;
    private String[] custom11Like;
    private String[] custom12In;
    private String[] custom12Like;
    private String[] custom13In;
    private String[] custom13Like;
    private String[] custom14In;
    private String[] custom14Like;
    private String[] custom15In;
    private String[] custom15Like;
    private String[] custom16In;
    private String[] custom16Like;
    private String[] attachmentClassificationKeyIn;
    private String[] attachmentClassificationKeyLike;
    private String[] attachmentClassificationIdIn;
    private String[] attachmentClassificationIdLike;
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
    private TimeInterval[] dueIn;
    private List<String> orderBy;
    private List<String> orderColumns;

    private boolean joinWithAttachments = false;
    private boolean addAttachmentColumnsToSelectClauseForOrdering = false;

    TaskQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.taskService = (TaskServiceImpl) taskanaEngine.getTaskService();
        this.orderBy = new ArrayList<>();
        this.orderColumns = new ArrayList<>();
        this.filterByAccessIdIn = true;
    }

    @Override
    public TaskQuery idIn(String... taskIds) {
        this.taskIds = taskIds;
        return this;
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
    public TaskQuery createdWithin(TimeInterval... intervals) {
        this.createdIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
        return this;
    }

    @Override
    public TaskQuery claimedWithin(TimeInterval... intervals) {
        this.claimedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
        return this;
    }

    @Override
    public TaskQuery completedWithin(TimeInterval... intervals) {
        this.completedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
        return this;
    }

    @Override
    public TaskQuery modifiedWithin(TimeInterval... intervals) {
        this.modifiedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
        return this;
    }

    @Override
    public TaskQuery plannedWithin(TimeInterval... intervals) {
        this.plannedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
        return this;
    }

    @Override
    public TaskQuery dueWithin(TimeInterval... intervals) {
        this.dueIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
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
    public TaskQuery ownerIn(String... owners) {
        this.ownerIn = owners;
        return this;
    }

    @Override
    public TaskQuery ownerLike(String... owners) {
        this.ownerLike = toUpperCopy(owners);
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
    public TaskQuery primaryObjectReferenceValueIn(String... values) {
        this.porValueIn = values;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceValueLike(String... value) {
        this.porValueLike = toUpperCopy(value);
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
    public TaskQuery stateIn(TaskState... states) {
        this.stateIn = states;
        return this;
    }

    @Override
    public TaskQuery stateNotIn(TaskState... states) {
        // No benefit in introducing a new variable
        List<TaskState> stateIn = Arrays.asList(TaskState.values());
        for (TaskState state : states) {
            stateIn.remove(state);
        }
        this.stateIn = stateIn.toArray(new TaskState[0]);
        return this;
    }

    @Override
    public TaskQuery customAttributeIn(String number, String... strings) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }
        if (strings.length == 0) {
            throw new InvalidArgumentException(
                "At least one string has to be provided as a search parameter");
        }

        switch (num) {
            case 1:
                this.custom1In = strings;
                break;
            case 2:
                this.custom2In = strings;
                break;
            case 3:
                this.custom3In = strings;
                break;
            case 4:
                this.custom4In = strings;
                break;
            case 5:
                this.custom5In = strings;
                break;
            case 6:
                this.custom6In = strings;
                break;
            case 7:
                this.custom7In = strings;
                break;
            case 8:
                this.custom8In = strings;
                break;
            case 9:
                this.custom9In = strings;
                break;
            case 10:
                this.custom10In = strings;
                break;
            case 11:
                this.custom11In = strings;
                break;
            case 12:
                this.custom12In = strings;
                break;
            case 13:
                this.custom13In = strings;
                break;
            case 14:
                this.custom14In = strings;
                break;
            case 15:
                this.custom15In = strings;
                break;
            case 16:
                this.custom16In = strings;
                break;
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
        }

        return this;
    }

    @Override
    public TaskQuery customAttributeLike(String number, String... strings) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }
        if (strings.length == 0) {
            throw new InvalidArgumentException(
                "At least one string has to be provided as a search parameter");
        }

        switch (num) {
            case 1:
                this.custom1Like = toUpperCopy(strings);
                break;
            case 2:
                this.custom2Like = toUpperCopy(strings);
                break;
            case 3:
                this.custom3Like = toUpperCopy(strings);
                break;
            case 4:
                this.custom4Like = toUpperCopy(strings);
                break;
            case 5:
                this.custom5Like = toUpperCopy(strings);
                break;
            case 6:
                this.custom6Like = toUpperCopy(strings);
                break;
            case 7:
                this.custom7Like = toUpperCopy(strings);
                break;
            case 8:
                this.custom8Like = toUpperCopy(strings);
                break;
            case 9:
                this.custom9Like = toUpperCopy(strings);
                break;
            case 10:
                this.custom10Like = toUpperCopy(strings);
                break;
            case 11:
                this.custom11Like = toUpperCopy(strings);
                break;
            case 12:
                this.custom12Like = toUpperCopy(strings);
                break;
            case 13:
                this.custom13Like = toUpperCopy(strings);
                break;
            case 14:
                this.custom14Like = toUpperCopy(strings);
                break;
            case 15:
                this.custom15Like = toUpperCopy(strings);
                break;
            case 16:
                this.custom16Like = toUpperCopy(strings);
                break;
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
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
        this.attachmentClassificationKeyLike = attachmentClassificationKey;
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
        this.attachmentClassificationIdLike = attachmentClassificationId;
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
        this.attachmentChannelLike = attachmentChannel;
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
        this.attachmentReferenceLike = referenceValue;
        return this;
    }

    @Override
    public TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn) {
        joinWithAttachments = true;
        this.attachmentReceivedIn = receivedIn;
        for (TimeInterval ti : receivedIn) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException(TIME_INTERVAL + ti + IS_INVALID);
            }
        }
        return this;
    }

    @Override
    public TaskQuery orderByClassificationKey(SortDirection sortDirection) {
        return this.taskanaEngine.sessionManager.getConfiguration().getDatabaseId().equals("db2")
            ? addOrderCriteria("TCLASSIFICATION_KEY", sortDirection)
            : addOrderCriteria("t.CLASSIFICATION_KEY", sortDirection);
    }

    @Override
    public TaskQuery orderByDomain(SortDirection sortDirection) {
        return addOrderCriteria("DOMAIN", sortDirection);
    }

    @Override
    public TaskQuery orderByPlanned(SortDirection sortDirection) {
        return addOrderCriteria("PLANNED", sortDirection);
    }

    @Override
    public TaskQuery orderByDue(SortDirection sortDirection) {
        return addOrderCriteria("DUE", sortDirection);
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
    public TaskQuery orderByOwner(SortDirection sortDirection) {
        return addOrderCriteria("OWNER", sortDirection);
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
    public TaskQuery orderByWorkbasketId(SortDirection sortDirection) {
        return addOrderCriteria("WORKBASKET_ID", sortDirection);
    }

    @Override
    public TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection) {
        joinWithAttachments = true;
        addAttachmentColumnsToSelectClauseForOrdering = true;
        return this.taskanaEngine.sessionManager.getConfiguration().getDatabaseId().equals("db2")
            ? addOrderCriteria("ACLASSIFICATION_KEY", sortDirection)
            : addOrderCriteria("a.CLASSIFICATION_KEY", sortDirection);
    }

    @Override
    public TaskQuery orderByAttachmentClassificationId(SortDirection sortDirection) {
        joinWithAttachments = true;
        addAttachmentColumnsToSelectClauseForOrdering = true;
        return this.taskanaEngine.sessionManager.getConfiguration().getDatabaseId().equals("db2")
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
        return addOrderCriteria("RECEIVED", sortDirection);
    }

    @Override
    public TaskQuery orderByNote(SortDirection sortDirection) {
        return addOrderCriteria("NOTE", sortDirection);
    }

    @Override
    public TaskQuery orderByCustomAttribute(String number, SortDirection sortDirection)
        throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }

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
            case 9:
                return addOrderCriteria("CUSTOM_9", sortDirection);
            case 10:
                return addOrderCriteria("CUSTOM_10", sortDirection);
            case 11:
                return addOrderCriteria("CUSTOM_11", sortDirection);
            case 12:
                return addOrderCriteria("CUSTOM_12", sortDirection);
            case 13:
                return addOrderCriteria("CUSTOM_13", sortDirection);
            case 14:
                return addOrderCriteria("CUSTOM_14", sortDirection);
            case 15:
                return addOrderCriteria("CUSTOM_15", sortDirection);
            case 16:
                return addOrderCriteria("CUSTOM_16", sortDirection);
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
        }
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
    public TaskQuery orderByCompleted(SortDirection sortDirection) {
        return addOrderCriteria("COMPLETED", sortDirection);
    }

    @Override
    public TaskQuery orderByCreated(SortDirection sortDirection) {
        return addOrderCriteria("CREATED", sortDirection);
    }

    @Override
    public TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection) {
        return addOrderCriteria("PARENT_BUSINESS_PROCESS_ID", sortDirection);
    }

    @Override
    public ObjectReferenceQuery createObjectReferenceQuery() {
        return new ObjectReferenceQueryImpl(taskanaEngine);
    }

    @Override
    public List<TaskSummary> list() {
        List<TaskSummary> result = new ArrayList<>();
        try {
            LOGGER.debug("entry to list(), this = {}", this);
            taskanaEngine.openConnection();
            checkOpenAndReadPermissionForSpecifiedWorkbaskets();
            List<TaskSummaryImpl> tasks = new ArrayList<>();
            setupAccessIds();
            tasks = taskanaEngine.getSqlSession().selectList(getLinkToMapperScript(), this);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("mapper returned {} resulting Objects: {} ", tasks.size(),
                    LoggerUtils.listToString(tasks));
            }
            result = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    public String getLinkToMapperScript() {
        return this.taskanaEngine.sessionManager.getConfiguration().getDatabaseId().equals("db2")
            ? LINK_TO_MAPPER_DB2
            : LINK_TO_MAPPER;
    }

    public String getLinkToCounterTaskScript() {
        return this.taskanaEngine.sessionManager.getConfiguration().getDatabaseId().equals("db2")
            ? LINK_TO_COUNTER_DB2
            : LINK_TO_COUNTER;
    }

    private void setupAccessIds() {
        if (taskanaEngine.isUserInRole(TaskanaRole.ADMIN) || !filterByAccessIdIn) {
            this.accessIdIn = null;
        } else if (this.accessIdIn == null) {
            String[] accessIds = new String[0];
            List<String> ucAccessIds = CurrentUserContext.getAccessIds();
            if (ucAccessIds != null && !ucAccessIds.isEmpty()) {
                accessIds = new String[ucAccessIds.size()];
                accessIds = ucAccessIds.toArray(accessIds);
            }
            this.accessIdIn = accessIds;
            WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
        }

    }

    @Override
    public List<String> listValues(String columnName, SortDirection sortDirection) {
        LOGGER.debug("Entry to listValues(dbColumnName={}) this = {}", columnName, this);
        List<String> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            this.columnName = columnName;
            this.orderBy.clear();
            this.addOrderCriteria(columnName, sortDirection);
            checkOpenAndReadPermissionForSpecifiedWorkbaskets();
            setupAccessIds();
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_VALUEMAPPER, this);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("Exit from listValues. Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<TaskSummary> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<TaskSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            checkOpenAndReadPermissionForSpecifiedWorkbaskets();
            setupAccessIds();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<TaskSummaryImpl> tasks = taskanaEngine.getSqlSession()
                .selectList(getLinkToMapperScript(), this, rowBounds);
            result = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            return result;
        } catch (PersistenceException e) {
            if (e.getMessage().contains("ERRORCODE=-4470")) {
                TaskanaRuntimeException ex = new TaskanaRuntimeException(
                    "The offset beginning was set over the amount of result-rows.", e.getCause());
                ex.setStackTrace(e.getStackTrace());
                throw ex;
            }
            throw e;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(offset,limit). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public TaskSummary single() {
        LOGGER.debug("entry to single(), this = {}", this);
        TaskSummary result = null;
        try {
            taskanaEngine.openConnection();
            checkOpenAndReadPermissionForSpecifiedWorkbaskets();
            setupAccessIds();
            TaskSummaryImpl taskSummaryImpl = taskanaEngine.getSqlSession().selectOne(getLinkToMapperScript(), this);
            if (taskSummaryImpl == null) {
                return null;
            }
            List<TaskSummaryImpl> tasks = new ArrayList<>();
            tasks.add(taskSummaryImpl);
            List<TaskSummary> augmentedList = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            result = augmentedList.get(0);

            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    @Override
    public long count() {
        LOGGER.debug("entry to count(), this = {}", this);
        Long rowCount = null;
        try {
            taskanaEngine.openConnection();
            checkOpenAndReadPermissionForSpecifiedWorkbaskets();
            setupAccessIds();
            rowCount = taskanaEngine.getSqlSession().selectOne(getLinkToCounterTaskScript(), this);
            return (rowCount == null) ? 0L : rowCount;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from count(). Returning result {} ", rowCount);
        }
    }

    public boolean isJoinWithAttachments() {
        return joinWithAttachments;
    }

    public void setJoinWithAttachments(boolean joinWithAttachments) {
        this.joinWithAttachments = joinWithAttachments;
    }

    public boolean isAddAttachmentColumnsToSelectClauseForOrdering() {
        return addAttachmentColumnsToSelectClauseForOrdering;
    }

    public void setAddAttachmentColumnsToSelectClauseForOrdering(
        boolean addAttachmentColumnsToSelectClauseForOrdering) {
        this.addAttachmentColumnsToSelectClauseForOrdering = addAttachmentColumnsToSelectClauseForOrdering;
    }

    private void checkOpenAndReadPermissionForSpecifiedWorkbaskets() {
        if (taskanaEngine.isUserInRole(TaskanaRole.ADMIN)) {
            LOGGER.debug("Skipping permissions check since user is in role ADMIN.");
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
            throw new NotAuthorizedToQueryWorkbasketException(e.getMessage(), e.getCause());
        }
    }

    private void checkOpenAndReadPermissionById(String workbasketId) throws NotAuthorizedException {
        try {
            taskanaEngine.getWorkbasketService().checkAuthorization(workbasketId,
                WorkbasketPermission.OPEN, WorkbasketPermission.READ);
        } catch (WorkbasketNotFoundException e) {
            LOGGER.warn("The workbasket with the ID '" + workbasketId + "' does not exist.", e);
        }
    }

    private void checkOpenAndReadPermissionByKeyDomain(KeyDomain keyDomain) throws NotAuthorizedException {
        try {
            taskanaEngine.getWorkbasketService().checkAuthorization(keyDomain.getKey(),
                keyDomain.getDomain(), WorkbasketPermission.OPEN, WorkbasketPermission.READ);
        } catch (WorkbasketNotFoundException e) {
            LOGGER.warn("The workbasket with the KEY '" + keyDomain.getKey() + "' and DOMAIN '"
                + keyDomain.getDomain() + "'does not exist.", e);
        }
    }

    public TaskanaEngineImpl getTaskanaEngine() {
        return taskanaEngine;
    }

    public String[] getTaskIds() {
        return taskIds;
    }

    public String[] getNameIn() {
        return nameIn;
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

    public String[] getOwnerLike() {
        return ownerLike;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public Boolean getIsTransferred() {
        return isTransferred;
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

    public String[] getCustom1Like() {
        return custom1Like;
    }

    public String[] getCustom2In() {
        return custom2In;
    }

    public String[] getCustom2Like() {
        return custom2Like;
    }

    public String[] getCustom3In() {
        return custom3In;
    }

    public String[] getCustom3Like() {
        return custom3Like;
    }

    public String[] getCustom4In() {
        return custom4In;
    }

    public String[] getCustom4Like() {
        return custom4Like;
    }

    public String[] getCustom5In() {
        return custom5In;
    }

    public String[] getCustom5Like() {
        return custom5Like;
    }

    public String[] getCustom6In() {
        return custom6In;
    }

    public String[] getCustom6Like() {
        return custom6Like;
    }

    public String[] getCustom7In() {
        return custom7In;
    }

    public String[] getCustom7Like() {
        return custom7Like;
    }

    public String[] getCustom8In() {
        return custom8In;
    }

    public String[] getCustom8Like() {
        return custom8Like;
    }

    public String[] getCustom9In() {
        return custom9In;
    }

    public String[] getCustom9Like() {
        return custom9Like;
    }

    public String[] getCustom10In() {
        return custom10In;
    }

    public String[] getCustom10Like() {
        return custom10Like;
    }

    public String[] getCustom11In() {
        return custom11In;
    }

    public String[] getCustom11Like() {
        return custom11Like;
    }

    public String[] getCustom12In() {
        return custom12In;
    }

    public String[] getCustom12Like() {
        return custom12Like;
    }

    public String[] getCustom13In() {
        return custom13In;
    }

    public String[] getCustom13Like() {
        return custom13Like;
    }

    public String[] getCustom14In() {
        return custom14In;
    }

    public String[] getCustom14Like() {
        return custom14Like;
    }

    public String[] getCustom15In() {
        return custom15In;
    }

    public String[] getCustom15Like() {
        return custom15Like;
    }

    public String[] getCustom16In() {
        return custom16In;
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

    public String getColumnName() {
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

    private TaskQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(columnName + orderByDirection);
        orderColumns.add(columnName);
        return this;
    }

    private String[] toUpperCopy(String... source) {
        String[] target = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i].toUpperCase();
        }
        return target;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskQueryImpl [columnName=");
        builder.append(columnName);
        builder.append(", nameIn=");
        builder.append(Arrays.toString(nameIn));
        builder.append(", nameLike=");
        builder.append(Arrays.toString(nameLike));
        builder.append(", creatorIn=");
        builder.append(Arrays.toString(creatorIn));
        builder.append(", creatorLike=");
        builder.append(Arrays.toString(creatorLike));
        builder.append(", taskIds=");
        builder.append(Arrays.toString(taskIds));
        builder.append(", description=");
        builder.append(Arrays.toString(description));
        builder.append(", note=");
        builder.append(Arrays.toString(note));
        builder.append(", priority=");
        builder.append(Arrays.toString(priority));
        builder.append(", workbasketKeyDomainIn=");
        builder.append(Arrays.toString(workbasketKeyDomainIn));
        builder.append(", workbasketIdIn=");
        builder.append(Arrays.toString(workbasketIdIn));
        builder.append(", stateIn=");
        builder.append(Arrays.toString(stateIn));
        builder.append(", classificationIdIn=");
        builder.append(Arrays.toString(classificationIdIn));
        builder.append(", classificationKeyIn=");
        builder.append(Arrays.toString(classificationKeyIn));
        builder.append(", classificationKeyNotIn=");
        builder.append(Arrays.toString(classificationKeyNotIn));
        builder.append(", classificationKeyLike=");
        builder.append(Arrays.toString(classificationKeyLike));
        builder.append(", classificationCategoryIn=");
        builder.append(Arrays.toString(classificationCategoryIn));
        builder.append(", classificationCategoryLike=");
        builder.append(Arrays.toString(classificationCategoryLike));
        builder.append(", ownerIn=");
        builder.append(Arrays.toString(ownerIn));
        builder.append(", ownerLike=");
        builder.append(Arrays.toString(ownerLike));
        builder.append(", isRead=");
        builder.append(isRead);
        builder.append(", isTransferred=");
        builder.append(isTransferred);
        builder.append(", porCompanyIn=");
        builder.append(Arrays.toString(porCompanyIn));
        builder.append(", porCompanyLike=");
        builder.append(Arrays.toString(porCompanyLike));
        builder.append(", porSystemIn=");
        builder.append(Arrays.toString(porSystemIn));
        builder.append(", porSystemLike=");
        builder.append(Arrays.toString(porSystemLike));
        builder.append(", porSystemInstanceIn=");
        builder.append(Arrays.toString(porSystemInstanceIn));
        builder.append(", porSystemInstanceLike=");
        builder.append(Arrays.toString(porSystemInstanceLike));
        builder.append(", porTypeIn=");
        builder.append(Arrays.toString(porTypeIn));
        builder.append(", porTypeLike=");
        builder.append(Arrays.toString(porTypeLike));
        builder.append(", porValueIn=");
        builder.append(Arrays.toString(porValueIn));
        builder.append(", porValueLike=");
        builder.append(Arrays.toString(porValueLike));
        builder.append(", parentBusinessProcessIdIn=");
        builder.append(Arrays.toString(parentBusinessProcessIdIn));
        builder.append(", parentBusinessProcessIdLike=");
        builder.append(Arrays.toString(parentBusinessProcessIdLike));
        builder.append(", businessProcessIdIn=");
        builder.append(Arrays.toString(businessProcessIdIn));
        builder.append(", businessProcessIdLike=");
        builder.append(Arrays.toString(businessProcessIdLike));
        builder.append(", custom1In=");
        builder.append(Arrays.toString(custom1In));
        builder.append(", custom1Like=");
        builder.append(Arrays.toString(custom1Like));
        builder.append(", custom2In=");
        builder.append(Arrays.toString(custom2In));
        builder.append(", custom2Like=");
        builder.append(Arrays.toString(custom2Like));
        builder.append(", custom3In=");
        builder.append(Arrays.toString(custom3In));
        builder.append(", custom3Like=");
        builder.append(Arrays.toString(custom3Like));
        builder.append(", custom4In=");
        builder.append(Arrays.toString(custom4In));
        builder.append(", custom4Like=");
        builder.append(Arrays.toString(custom4Like));
        builder.append(", custom5In=");
        builder.append(Arrays.toString(custom5In));
        builder.append(", custom5Like=");
        builder.append(Arrays.toString(custom5Like));
        builder.append(", custom6In=");
        builder.append(Arrays.toString(custom6In));
        builder.append(", custom6Like=");
        builder.append(Arrays.toString(custom6Like));
        builder.append(", custom7In=");
        builder.append(Arrays.toString(custom7In));
        builder.append(", custom7Like=");
        builder.append(Arrays.toString(custom7Like));
        builder.append(", custom8In=");
        builder.append(Arrays.toString(custom8In));
        builder.append(", custom8Like=");
        builder.append(Arrays.toString(custom8Like));
        builder.append(", custom9In=");
        builder.append(Arrays.toString(custom9In));
        builder.append(", custom9Like=");
        builder.append(Arrays.toString(custom9Like));
        builder.append(", custom10In=");
        builder.append(Arrays.toString(custom10In));
        builder.append(", custom10Like=");
        builder.append(Arrays.toString(custom10Like));
        builder.append(", custom11In=");
        builder.append(Arrays.toString(custom11In));
        builder.append(", custom11Like=");
        builder.append(Arrays.toString(custom11Like));
        builder.append(", custom12In=");
        builder.append(Arrays.toString(custom12In));
        builder.append(", custom12Like=");
        builder.append(Arrays.toString(custom12Like));
        builder.append(", custom13In=");
        builder.append(Arrays.toString(custom13In));
        builder.append(", custom13Like=");
        builder.append(Arrays.toString(custom13Like));
        builder.append(", custom14In=");
        builder.append(Arrays.toString(custom14In));
        builder.append(", custom14Like=");
        builder.append(Arrays.toString(custom14Like));
        builder.append(", custom15In=");
        builder.append(Arrays.toString(custom15In));
        builder.append(", custom15Like=");
        builder.append(Arrays.toString(custom15Like));
        builder.append(", custom16In=");
        builder.append(Arrays.toString(custom16In));
        builder.append(", custom16Like=");
        builder.append(Arrays.toString(custom16Like));
        builder.append(", accessIdIn=");
        builder.append(Arrays.toString(accessIdIn));
        builder.append(", filterByAccessIdIn=");
        builder.append(filterByAccessIdIn);
        builder.append(", createdIn=");
        builder.append(Arrays.toString(createdIn));
        builder.append(", claimedIn=");
        builder.append(Arrays.toString(claimedIn));
        builder.append(", completedIn=");
        builder.append(Arrays.toString(completedIn));
        builder.append(", modifiedIn=");
        builder.append(Arrays.toString(modifiedIn));
        builder.append(", plannedIn=");
        builder.append(Arrays.toString(plannedIn));
        builder.append(", dueIn=");
        builder.append(Arrays.toString(dueIn));
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }

}
