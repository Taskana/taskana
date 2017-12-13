package pro.taskana.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationQuery;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
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

    private String[] name;
    private String description;
    private int[] priority;
    private TaskState[] states;
    private ClassificationQuery classificationQuery;
    private String[] workbasketKey;
    private String[] owner;
    private ObjectReferenceQuery objectReferenceQuery;
    private Boolean isRead;
    private Boolean isTransferred;
    private String[] customFields;

    public TaskQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public TaskQuery name(String... names) {
        this.name = names;
        return this;
    }

    @Override
    public TaskQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    @Override
    public TaskQuery priority(int... priorities) {
        this.priority = priorities;
        return this;
    }

    @Override
    public TaskQuery state(TaskState... states) {
        this.states = states;
        return this;
    }

    @Override
    public TaskQuery classification(ClassificationQuery classificationQuery) {
        this.classificationQuery = classificationQuery;
        return this;
    }

    @Override
    public TaskQuery workbasketKeyIn(String... workbasketKeys) {
        this.workbasketKey = workbasketKeys;
        return this;
    }

    @Override
    public TaskQuery owner(String... owners) {
        this.owner = owners;
        return this;
    }

    @Override
    public TaskQuery objectReference(ObjectReferenceQuery objectReferenceQuery) {
        this.objectReferenceQuery = objectReferenceQuery;
        return this;
    }

    @Override
    public TaskQuery read(Boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    @Override
    public TaskQuery transferred(Boolean isTransferred) {
        this.isTransferred = isTransferred;
        return this;
    }

    @Override
    public TaskQuery customFields(String... customFields) {
        this.customFields = customFields;
        return this;
    }

    @Override
    public ObjectReferenceQuery createObjectReferenceQuery() {
        return new ObjectReferenceQueryImpl(taskanaEngineImpl);
    }

    @Override
    public List<Task> list() throws NotAuthorizedException {
        LOGGER.debug("entry to list(), this = {}", this);
        List<Task> result = null;
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            result = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
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
    public List<Task> list(int offset, int limit) throws NotAuthorizedException {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<Task> result = null;
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            RowBounds rowBounds = new RowBounds(offset, limit);
            result = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
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
    public TaskImpl single() throws NotAuthorizedException {
        LOGGER.debug("entry to single(), this = {}", this);
        TaskImpl result = null;
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            result = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    private void checkAuthorization() throws NotAuthorizedException {
        if (this.workbasketKey != null && this.workbasketKey.length > 0) {
            for (String workbasket : this.workbasketKey) {
                taskanaEngineImpl.getWorkbasketService().checkAuthorization(workbasket, WorkbasketAuthorization.OPEN);
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

    public void setName(String[] name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int[] getPriority() {
        return priority;
    }

    public void setPriority(int[] priority) {
        this.priority = priority;
    }

    public TaskState[] getStates() {
        return states;
    }

    public void setStates(TaskState[] states) {
        this.states = states;
    }

    public ClassificationQuery getClassificationQuery() {
        return classificationQuery;
    }

    public void setClassificationQuery(ClassificationQuery classificationQuery) {
        this.classificationQuery = classificationQuery;
    }

    public String[] getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String[] workbasketKey) {
        this.workbasketKey = workbasketKey;
    }

    public String[] getOwner() {
        return owner;
    }

    public void setOwner(String[] owner) {
        this.owner = owner;
    }

    public ObjectReferenceQuery getObjectReferenceQuery() {
        return objectReferenceQuery;
    }

    public void setObjectReferenceQuery(ObjectReferenceQuery objectReferenceQuery) {
        this.objectReferenceQuery = objectReferenceQuery;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean isTransferred) {
        this.isTransferred = isTransferred;
    }

    public String[] getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String[] customFields) {
        this.customFields = customFields;
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
        builder.append(", priority=");
        builder.append(Arrays.toString(priority));
        builder.append(", states=");
        builder.append(Arrays.toString(states));
        builder.append(", classificationQuery=");
        builder.append(classificationQuery);
        builder.append(", workbasketKey=");
        builder.append(Arrays.toString(workbasketKey));
        builder.append(", owner=");
        builder.append(Arrays.toString(owner));
        builder.append(", objectReferenceQuery=");
        builder.append(objectReferenceQuery);
        builder.append(", isRead=");
        builder.append(isRead);
        builder.append(", isTransferred=");
        builder.append(isTransferred);
        builder.append(", customFields=");
        builder.append(Arrays.toString(customFields));
        builder.append("]");
        return builder.toString();
    }
}
