package pro.taskana.impl;

import java.util.ArrayList;
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
        List<Task> result = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            List<TaskImpl> tasks = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
            tasks.stream().forEach(t -> {
                TaskServiceImpl.setPrimaryObjRef(t);
                result.add(t);
            });
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
        List<Task> result = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            checkAuthorization();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<TaskImpl> tasks = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
            tasks.stream().forEach(t -> {
                TaskServiceImpl.setPrimaryObjRef(t);
                result.add(t);
            });
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
            TaskServiceImpl.setPrimaryObjRef(result);
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

    public String[] getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String[] customFields) {
        this.customFields = customFields;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsTransferred() {
        return isTransferred;
    }

    public void setIsTransferred(Boolean isTransferred) {
        this.isTransferred = isTransferred;
    }

    public String[] getPorCompanyIn() {
        return porCompanyIn;
    }

    public void setPorCompanyIn(String[] porCompanyIn) {
        this.porCompanyIn = porCompanyIn;
    }

    public String getPorCompanyLike() {
        return porCompanyLike;
    }

    public void setPorCompanyLike(String porCompanyLike) {
        this.porCompanyLike = porCompanyLike;
    }

    public String[] getPorSystemIn() {
        return porSystemIn;
    }

    public void setPorSystemIn(String[] porSystemIn) {
        this.porSystemIn = porSystemIn;
    }

    public String getPorSystemLike() {
        return porSystemLike;
    }

    public void setPorSystemLike(String porSystemLike) {
        this.porSystemLike = porSystemLike;
    }

    public String[] getPorSystemInstanceIn() {
        return porSystemInstanceIn;
    }

    public void setPorSystemInstanceIn(String[] porSystemInstanceIn) {
        this.porSystemInstanceIn = porSystemInstanceIn;
    }

    public String getPorSystemInstanceLike() {
        return porSystemInstanceLike;
    }

    public void setPorSystemInstanceLike(String porSystemInstanceLike) {
        this.porSystemInstanceLike = porSystemInstanceLike;
    }

    public String[] getPorTypeIn() {
        return porTypeIn;
    }

    public void setPorTypeIn(String[] porTypeIn) {
        this.porTypeIn = porTypeIn;
    }

    public String getPorTypeLike() {
        return porTypeLike;
    }

    public void setPorTypeLike(String porTypeLike) {
        this.porTypeLike = porTypeLike;
    }

    public String[] getPorValueIn() {
        return porValueIn;
    }

    public void setPorValueIn(String[] porValueIn) {
        this.porValueIn = porValueIn;
    }

    public String getPorValueLike() {
        return porValueLike;
    }

    public void setPorValueLike(String porValueLike) {
        this.porValueLike = porValueLike;
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
