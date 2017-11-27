package pro.taskana.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;

import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.persistence.ClassificationQuery;
import pro.taskana.persistence.ObjectReferenceQuery;
import pro.taskana.persistence.TaskQuery;

/**
 * TaskQuery for generating dynamic sql.
 */
public class TaskQueryImpl implements TaskQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryTasks";

    private TaskanaEngineImpl taskanaEngineImpl;

    private String[] name;
    private String description;
    private int[] priority;
    private TaskState[] states;
    private ClassificationQuery classificationQuery;
    private String[] workbasketId;
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
    public TaskQuery workbasketId(String... workbasketIds) {
        this.workbasketId = workbasketIds;
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
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            return taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public List<Task> list(int offset, int limit) throws NotAuthorizedException {
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            RowBounds rowBounds = new RowBounds(offset, limit);
            return taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Task single() throws NotAuthorizedException {
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            return taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    private void checkAuthorization() throws NotAuthorizedException {
        if (this.workbasketId != null && this.workbasketId.length > 0) {
            for (String workbasket : this.workbasketId) {
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

    public String[] getWorkbasketId() {
        return workbasketId;
    }

    public void setWorkbasketId(String[] workbasketId) {
        this.workbasketId = workbasketId;
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
}
