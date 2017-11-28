package pro.taskana.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;

import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskanaEngine;
import pro.taskana.model.ObjectReference;

/**
 * Implementation of ObjectReferenceQuery interface.
 * @author EH
 */
public class ObjectReferenceQueryImpl implements ObjectReferenceQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryObjectReference";

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
        try {
            taskanaEngineImpl.openConnection();
            return taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public List<ObjectReference> list(int offset, int limit) {
        try {
            taskanaEngineImpl.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            return taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public ObjectReference single() {
        try {
            taskanaEngineImpl.openConnection();
            return taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
        } finally {
            taskanaEngineImpl.returnConnection();
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
}
