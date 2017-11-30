package pro.taskana.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskanaEngine;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.ObjectReference;

/**
 * Implementation of ObjectReferenceQuery interface.
 * @author EH
 */
public class ObjectReferenceQueryImpl implements ObjectReferenceQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryObjectReference";
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectReferenceQueryImpl.class);

    private TaskanaEngineImpl taskanaEngineImpl;
    private String[] company;
    private String[] system;
    private String[] systemInstance;
    private String[] type;
    private String[] value;

    public ObjectReferenceQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public ObjectReferenceQuery company(String... companies) {
        this.company = companies;
        return this;
    }

    @Override
    public ObjectReferenceQuery system(String... systems) {
        this.system = systems;
        return this;
    }

    @Override
    public ObjectReferenceQuery systemInstance(String... systemInstances) {
        this.systemInstance = systemInstances;
        return this;
    }

    @Override
    public ObjectReferenceQuery type(String... types) {
        this.type = types;
        return this;
    }

    @Override
    public ObjectReferenceQuery value(String... values) {
        this.value = values;
        return this;
    }

    @Override
    public List<ObjectReference> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<ObjectReference> result = null;
        try {
            taskanaEngineImpl.openConnection();
            result =  taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(). Returning {} resulting Objects: {} ", numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<ObjectReference> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<ObjectReference> result = null;
        try {
            taskanaEngineImpl.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            result = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(offset,limit). Returning {} resulting Objects: {} ", numberOfResultObjects, LoggerUtils.listToString(result));
            }
       }
    }

    @Override
    public ObjectReference single() {
        LOGGER.debug("entry to single(), this = {}", this);
        ObjectReference result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    public String[] getCompany() {
        return company;
    }

    public void setCompany(String[] company) {
        this.company = company;
    }

    public String[] getSystem() {
        return system;
    }

    public void setSystem(String[] system) {
        this.system = system;
    }

    public String[] getSystemInstance() {
        return systemInstance;
    }

    public void setSystemInstance(String[] systemInstance) {
        this.systemInstance = systemInstance;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ObjectReferenceQueryImpl [taskanaEngineImpl=");
        builder.append(taskanaEngineImpl);
        builder.append(", company=");
        builder.append(Arrays.toString(company));
        builder.append(", system=");
        builder.append(Arrays.toString(system));
        builder.append(", systemInstance=");
        builder.append(Arrays.toString(systemInstance));
        builder.append(", type=");
        builder.append(Arrays.toString(type));
        builder.append(", value=");
        builder.append(Arrays.toString(value));
        builder.append("]");
        return builder.toString();
    }
}
