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
    private String[] name;
    private String[] taskIds;
    private String description;
    private String note;
    private int[] priority;
    private TaskState[] states;
    private String[] classificationKey;
    private String[] workbasketKey;
    private String[] domain;
    private String[] owner;
    private Boolean isRead;
    private Boolean isTransferred;
    private String[] customFields;
    private String[] porCompanyIn;
    private String porCompanyLike;
    private String[] porSystemIn;
    private String porSystemLike;
    private String[] porSystemInstanceIn;
    private String porSystemInstanceLike;
    private String[] porTypeIn;
    private String porTypeLike;
    private String[] porValueIn;
    private String porValueLike;
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
        this.name = names;
        return this;
    }

    @Override
    public TaskQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    @Override
    public TaskQuery noteLike(String note) {
        this.note = note;
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
        this.classificationKey = classificationKey;
        return this;
    }

    @Override
    public TaskQuery workbasketKeyIn(String... workbasketKeys) {
        this.workbasketKey = workbasketKeys;
        return this;
    }

    @Override
    public TaskQuery domainIn(String... domain) {
        this.domain = domain;
        return this;
    }

    @Override
    public TaskQuery ownerIn(String... owners) {
        this.owner = owners;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceCompanyIn(String... companies) {
        this.porCompanyIn = companies;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceCompanyLike(String company) {
        this.porCompanyLike = company;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceSystemIn(String... systems) {
        this.porSystemIn = systems;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceSystemLike(String system) {
        this.porSystemLike = system;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances) {
        this.porSystemInstanceIn = systemInstances;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceSystemInstanceLike(String systemInstance) {
        this.porSystemInstanceLike = systemInstance;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceTypeIn(String... types) {
        this.porTypeIn = types;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceTypeLike(String type) {
        this.porTypeLike = type;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceValueIn(String... values) {
        this.porValueIn = values;
        return this;
    }

    @Override
    public TaskQuery primaryObjectReferenceValueLike(String value) {
        this.porValueLike = value;
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
        if (this.workbasketKey != null && this.workbasketKey.length > 0) {
            for (String wbKey : this.workbasketKey) {
                taskanaEngineImpl.getWorkbasketService().checkAuthorization(wbKey, WorkbasketAuthorization.OPEN);
            }
        }
    }

    public TaskanaEngineImpl getTaskanaEngine() {
        return taskanaEngineImpl;
    }

    public void setTaskanaEngine(TaskanaEngineImpl taskanaEngine) {
        this.taskanaEngineImpl = taskanaEngine;
    }

    public String[] getTaskIds() {
        return taskIds;
    }

    public String[] getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int[] getPriority() {
        return priority;
    }

    public TaskState[] getStates() {
        return states;
    }

    public String[] getClassificationKey() {
        return classificationKey;
    }

    public String[] getWorkbasketKey() {
        return workbasketKey;
    }

    public String[] getDomain() {
        return domain;
    }

    public String[] getOwner() {
        return owner;
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

    public String getPorCompanyLike() {
        return porCompanyLike;
    }

    public String[] getPorSystemIn() {
        return porSystemIn;
    }

    public String getPorSystemLike() {
        return porSystemLike;
    }

    public String[] getPorSystemInstanceIn() {
        return porSystemInstanceIn;
    }

    public String getPorSystemInstanceLike() {
        return porSystemInstanceLike;
    }

    public String[] getPorTypeIn() {
        return porTypeIn;
    }

    public String getPorTypeLike() {
        return porTypeLike;
    }

    public String[] getPorValueIn() {
        return porValueIn;
    }

    public String getPorValueLike() {
        return porValueLike;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskQueryImpl [taskanaEngineImpl=");
        builder.append(taskanaEngineImpl);
        builder.append(", taskService=");
        builder.append(taskService);
        builder.append(", name=");
        builder.append(Arrays.toString(name));
        builder.append(", description=");
        builder.append(description);
        builder.append(", note=");
        builder.append(note);
        builder.append(", priority=");
        builder.append(Arrays.toString(priority));
        builder.append(", states=");
        builder.append(Arrays.toString(states));
        builder.append(", classificationKey=");
        builder.append(Arrays.toString(classificationKey));
        builder.append(", workbasketKey=");
        builder.append(Arrays.toString(workbasketKey));
        builder.append(", domain=");
        builder.append(Arrays.toString(domain));
        builder.append(", owner=");
        builder.append(Arrays.toString(owner));
        builder.append(", isRead=");
        builder.append(isRead);
        builder.append(", isTransferred=");
        builder.append(isTransferred);
        builder.append(", customFields=");
        builder.append(Arrays.toString(customFields));
        builder.append(", porCompanyIn=");
        builder.append(Arrays.toString(porCompanyIn));
        builder.append(", porCompanyLike=");
        builder.append(porCompanyLike);
        builder.append(", porSystemIn=");
        builder.append(Arrays.toString(porSystemIn));
        builder.append(", porSystemLike=");
        builder.append(porSystemLike);
        builder.append(", porSystemInstanceIn=");
        builder.append(Arrays.toString(porSystemInstanceIn));
        builder.append(", porSystemInstanceLike=");
        builder.append(porSystemInstanceLike);
        builder.append(", porTypeIn=");
        builder.append(Arrays.toString(porTypeIn));
        builder.append(", porTypeLike=");
        builder.append(porTypeLike);
        builder.append(", porValueIn=");
        builder.append(Arrays.toString(porValueIn));
        builder.append(", porValueLike=");
        builder.append(porValueLike);
        builder.append("]");
        return builder.toString();
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

    private TaskQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(columnName + orderByDirection);
        return this;
    }
}
