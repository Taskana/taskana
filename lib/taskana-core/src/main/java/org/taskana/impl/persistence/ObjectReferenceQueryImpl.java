package org.taskana.impl.persistence;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.taskana.TaskanaEngine;
import org.taskana.impl.TaskanaEngineImpl;
import org.taskana.model.ObjectReference;
import org.taskana.persistence.ObjectReferenceQuery;

/**
 * Implementation of ObjectReferenceQuery interface.
 * @author EH
 */
public class ObjectReferenceQueryImpl implements ObjectReferenceQuery {

    private static final String LINK_TO_MAPPER = "org.taskana.model.mappings.QueryMapper.queryObjectReference";

    private TaskanaEngineImpl taskanaEngine;
    private String tenantId;
    private String[] company;
    private String[] system;
    private String[] systemInstance;
    private String[] type;
    private String[] value;

    public ObjectReferenceQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public ObjectReferenceQuery tenantId(String tenantIds) {
        this.tenantId = tenantIds;
        return this;
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
        return taskanaEngine.getSession().selectList(LINK_TO_MAPPER, this);
    }

    @Override
    public List<ObjectReference> list(int offset, int limit) {
        RowBounds rowBounds = new RowBounds(offset, limit);
        return taskanaEngine.getSession().selectList(LINK_TO_MAPPER, this, rowBounds);
    }

    @Override
    public ObjectReference single() {
        return taskanaEngine.getSession().selectOne(LINK_TO_MAPPER, this);
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
