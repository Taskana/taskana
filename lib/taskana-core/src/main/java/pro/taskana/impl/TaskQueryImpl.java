package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskQuery;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAuthorization;

/**
 * TaskQuery for generating dynamic sql.
 */
public class TaskQueryImpl implements TaskQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryTasks";
    private static final String LINK_TO_COUNTER = "pro.taskana.model.mappings.QueryMapper.countQueryTasks";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueryImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private TaskServiceImpl taskService;
    private String[] nameIn;
    private String[] nameLike;
    private String[] taskIds;
    private String[] description;
    private String[] note;
    private int[] priority;
    private TaskState[] states;
    private String[] classificationKeyIn;
    private String[] classificationKeyLike;
    private String[] workbasketKeyIn;
    private String[] workbasketKeyLike;
    private String[] domainIn;
    private String[] domainLike;
    private String[] ownerIn;
    private String[] ownerLike;
    private Boolean isRead;
    private Boolean isTransferred;
    private String[] customFields;
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
    private TimeInterval[] createdIn;
    private TimeInterval[] claimedIn;
    private TimeInterval[] completedIn;
    private TimeInterval[] modifiedIn;
    private TimeInterval[] plannedIn;
    private TimeInterval[] dueIn;
    private List<String> orderBy;

    TaskQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
        this.orderBy = new ArrayList<>();
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
    public TaskQuery createdWithin(TimeInterval... intervals) {
        this.createdIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public TaskQuery claimedWithin(TimeInterval... intervals) {
        this.claimedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public TaskQuery completedWithin(TimeInterval... intervals) {
        this.completedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public TaskQuery modifiedWithin(TimeInterval... intervals) {
        this.modifiedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public TaskQuery plannedWithin(TimeInterval... intervals) {
        this.plannedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public TaskQuery dueWithin(TimeInterval... intervals) {
        this.dueIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
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
        this.note = toUpperCopy(note);
        return this;
    }

    @Override
    public TaskQuery priorityIn(int... priorities) {
        this.priority = priorities;
        return this;
    }

    @Override
    public TaskQuery stateIn(TaskState... states) {
        this.states = states;
        return this;
    }

    @Override
    public TaskQuery classificationKeyIn(String... classificationKey) {
        this.classificationKeyIn = classificationKey;
        return this;
    }

    @Override
    public TaskQuery classificationKeyLike(String... classificationKeys) {
        this.classificationKeyLike = toUpperCopy(classificationKeys);
        return this;
    }

    @Override
    public TaskQuery workbasketKeyIn(String... workbasketKeys) {
        this.workbasketKeyIn = workbasketKeys;
        return this;
    }

    @Override
    public TaskQuery workbasketKeyLike(String... workbasketKeys) {
        this.workbasketKeyLike = toUpperCopy(workbasketKeys);
        return this;
    }

    @Override
    public TaskQuery domainIn(String... domain) {
        this.domainIn = domain;
        return this;
    }

    @Override
    public TaskQuery domainLike(String... domains) {
        this.domainLike = toUpperCopy(domains);
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
    public TaskQuery customFieldsIn(String... customFields) {
        this.customFields = customFields;
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
    public TaskQuery custom1In(String... strings) {
        this.custom1In = strings;
        return this;
    }

    @Override
    public TaskQuery custom1Like(String... strings) {
        this.custom1Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom2In(String... strings) {
        this.custom2In = strings;
        return this;
    }

    @Override
    public TaskQuery custom2Like(String... strings) {
        this.custom2Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom3In(String... strings) {
        this.custom3In = strings;
        return this;
    }

    @Override
    public TaskQuery custom3Like(String... strings) {
        this.custom3Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom4In(String... strings) {
        this.custom4In = strings;
        return this;
    }

    @Override
    public TaskQuery custom4Like(String... strings) {
        this.custom4Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom5In(String... strings) {
        this.custom5In = strings;
        return this;
    }

    @Override
    public TaskQuery custom5Like(String... strings) {
        this.custom5Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom6In(String... strings) {
        this.custom6In = strings;
        return this;
    }

    @Override
    public TaskQuery custom6Like(String... strings) {
        this.custom6Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom7In(String... strings) {
        this.custom7In = strings;
        return this;
    }

    @Override
    public TaskQuery custom7Like(String... strings) {
        this.custom7Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom8In(String... strings) {
        this.custom8In = strings;
        return this;
    }

    @Override
    public TaskQuery custom8Like(String... strings) {
        this.custom8Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom9In(String... strings) {
        this.custom9In = strings;
        return this;
    }

    @Override
    public TaskQuery custom9Like(String... strings) {
        this.custom9Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery custom10In(String... strings) {
        this.custom10In = strings;
        return this;
    }

    @Override
    public TaskQuery custom10Like(String... strings) {
        this.custom10Like = toUpperCopy(strings);
        return this;
    }

    @Override
    public TaskQuery orderByClassificationKey(SortDirection sortDirection) {
        return addOrderCriteria("CLASSIFICATION_KEY", sortDirection);
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
    public TaskQuery orderByNote(SortDirection sortDirection) {
        return addOrderCriteria("NOTE", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom1(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_1", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom2(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_2", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom3(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_3", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom4(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_4", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom5(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_5", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom6(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_6", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom7(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_7", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom8(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_8", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom9(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_9", sortDirection);
    }

    @Override
    public TaskQuery orderByCustom10(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_10", sortDirection);
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
        return new ObjectReferenceQueryImpl(taskanaEngineImpl);
    }

    @Override
    public List<TaskSummary> list() throws NotAuthorizedException {
        List<TaskSummary> result = new ArrayList<>();
        try {
            LOGGER.debug("entry to list(), this = {}", this);
            taskanaEngineImpl.openConnection();
            checkOpenPermissionForWorkbasketKey();
            List<TaskSummaryImpl> tasks = new ArrayList<>();
            tasks = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
            result = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<TaskSummary> list(int offset, int limit) throws NotAuthorizedException {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<TaskSummary> result = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            checkOpenPermissionForWorkbasketKey();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<TaskSummaryImpl> tasks = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
            result = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            return result;
        } catch (Exception e) {
            if (e instanceof PersistenceException) {
                if (e.getMessage().contains("ERRORCODE=-4470")) {
                    TaskanaRuntimeException ex = new TaskanaRuntimeException(
                        "The offset beginning was set over the amount of result-rows.", e.getCause());
                    ex.setStackTrace(e.getStackTrace());
                    throw ex;
                }
            }
            throw e;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(offset,limit). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public TaskSummary single() throws NotAuthorizedException {
        LOGGER.debug("entry to single(), this = {}", this);
        TaskSummary result = null;
        try {
            taskanaEngineImpl.openConnection();
            checkOpenPermissionForWorkbasketKey();
            TaskSummaryImpl taskSummaryImpl = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            if (taskSummaryImpl == null) {
                return null;
            }
            List<TaskSummaryImpl> tasks = new ArrayList<>();
            tasks.add(taskSummaryImpl);
            List<TaskSummary> augmentedList = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            result = augmentedList.get(0);

            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    @Override
    public long count() throws NotAuthorizedException {
        LOGGER.debug("entry to count(), this = {}", this);
        Long rowCount = null;
        try {
            taskanaEngineImpl.openConnection();
            checkOpenPermissionForWorkbasketKey();
            rowCount = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_COUNTER, this);
            return (rowCount == null) ? 0L : rowCount;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from count(). Returning result {} ", rowCount);
        }
    }

    private void checkOpenPermissionForWorkbasketKey() throws NotAuthorizedException {
        if (this.workbasketKeyIn != null && this.workbasketKeyIn.length > 0) {
            for (String wbKey : this.workbasketKeyIn) {
                taskanaEngineImpl.getWorkbasketService().checkAuthorization(wbKey, WorkbasketAuthorization.OPEN);
            }
        }
    }

    public TaskanaEngineImpl getTaskanaEngine() {
        return taskanaEngineImpl;
    }

    public String[] getTaskIds() {
        return taskIds;
    }

    public String[] getNameIn() {
        return nameIn;
    }

    public String[] getDescription() {
        return description;
    }

    public int[] getPriority() {
        return priority;
    }

    public TaskState[] getStates() {
        return states;
    }

    public String[] getOwnerIn() {
        return ownerIn;
    }

    public String[] getOwnerLike() {
        return ownerLike;
    }

    public String[] getCustomFields() {
        return customFields;
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

    public TimeInterval[] getCreatedIn() {
        return createdIn;
    }

    public TaskServiceImpl getTaskService() {
        return taskService;
    }

    public String[] getNote() {
        return note;
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

    public String[] getClassificationKeyLike() {
        return classificationKeyLike;
    }

    public String[] getWorkbasketKeyIn() {
        return workbasketKeyIn;
    }

    public String[] getWorkbasketKeyLike() {
        return workbasketKeyLike;
    }

    public String[] getDomainIn() {
        return domainIn;
    }

    public String[] getDomainLike() {
        return domainLike;
    }

    private TaskQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(columnName + orderByDirection);
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
        builder.append("TaskQueryImpl [nameIn=");
        builder.append(Arrays.toString(nameIn));
        builder.append(", nameLike=");
        builder.append(Arrays.toString(nameLike));
        builder.append(", taskIds=");
        builder.append(Arrays.toString(taskIds));
        builder.append(", description=");
        builder.append(Arrays.toString(description));
        builder.append(", note=");
        builder.append(Arrays.toString(note));
        builder.append(", priority=");
        builder.append(Arrays.toString(priority));
        builder.append(", states=");
        builder.append(Arrays.toString(states));
        builder.append(", classificationKeyIn=");
        builder.append(Arrays.toString(classificationKeyIn));
        builder.append(", classificationKeyLike=");
        builder.append(Arrays.toString(classificationKeyLike));
        builder.append(", workbasketKeyIn=");
        builder.append(Arrays.toString(workbasketKeyIn));
        builder.append(", workbasketKeyLike=");
        builder.append(Arrays.toString(workbasketKeyLike));
        builder.append(", domainIn=");
        builder.append(Arrays.toString(domainIn));
        builder.append(", domainLike=");
        builder.append(Arrays.toString(domainLike));
        builder.append(", ownerIn=");
        builder.append(Arrays.toString(ownerIn));
        builder.append(", ownerLike=");
        builder.append(Arrays.toString(ownerLike));
        builder.append(", isRead=");
        builder.append(isRead);
        builder.append(", isTransferred=");
        builder.append(isTransferred);
        builder.append(", customFields=");
        builder.append(Arrays.toString(customFields));
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
