package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskQuery;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAuthorization;

/**
 * TaskQuery for generating dynamic sql.
 */
public class TaskQueryImpl implements TaskQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryTasks";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueryImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private ClassificationServiceImpl classificationService;
    private TaskServiceImpl taskService;
    private String[] name;
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

    public TaskQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.classificationService = (ClassificationServiceImpl) taskanaEngineImpl.getClassificationService();
        this.taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
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
        LOGGER.debug("entry to list(), this = {}", this);
        List<TaskSummary> result = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            List<TaskSummaryImpl> tasks = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
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
            checkAuthorization();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<TaskSummaryImpl> tasks = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
            result = taskService.augmentTaskSummariesByContainedSummaries(tasks);
            return result;
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
            checkAuthorization();
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

    private void checkAuthorization() throws NotAuthorizedException {
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskQueryImpl [taskanaEngineImpl=");
        builder.append(taskanaEngineImpl);
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
}
