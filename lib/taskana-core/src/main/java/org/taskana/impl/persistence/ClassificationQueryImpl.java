package org.taskana.impl.persistence;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.taskana.TaskanaEngine;
import org.taskana.impl.TaskanaEngineImpl;
import org.taskana.model.Classification;
import org.taskana.persistence.ClassificationQuery;

/**
 * Implementation of ClassificationQuery interface.
 * @author EH
 */
public class ClassificationQueryImpl implements ClassificationQuery {

    private static final String LINK_TO_MAPPER = "org.taskana.model.mappings.QueryMapper.queryClassification";
    private TaskanaEngineImpl taskanaEngine;
    private String tenantId;
    private String[] parentClassificationId;
    private String[] category;
    private String[] type;
    private String[] name;
    private String description;
    private int[] priority;
    private String[] serviceLevel;

    public ClassificationQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public ClassificationQuery tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public ClassificationQuery parentClassification(String... parentClassificationId) {
        this.parentClassificationId = parentClassificationId;
        return this;
    }

    @Override
    public ClassificationQuery category(String... category) {
        this.category = category;
        return this;
    }

    @Override
    public ClassificationQuery type(String... type) {
        this.type = type;
        return this;
    }

    @Override
    public ClassificationQuery name(String... name) {
        this.name = name;
        return this;
    }

    @Override
    public ClassificationQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ClassificationQuery priority(int... priorities) {
        this.priority = priorities;
        return this;
    }

    @Override
    public ClassificationQuery serviceLevel(String... serviceLevel) {
        this.serviceLevel = serviceLevel;
        return this;
    }

    @Override
    public List<Classification> list() {
        return taskanaEngine.getSession().selectList(LINK_TO_MAPPER, this);
    }

    @Override
    public List<Classification> list(int offset, int limit) {
        RowBounds rowBounds = new RowBounds(offset, limit);
        return taskanaEngine.getSession().selectList(LINK_TO_MAPPER, this, rowBounds);
    }

    @Override
    public Classification single() {
        return taskanaEngine.getSession().selectOne(LINK_TO_MAPPER, this);
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String[] getParentClassificationId() {
        return parentClassificationId;
    }

    public void setParentClassificationId(String[] parentClassificationId) {
        this.parentClassificationId = parentClassificationId;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
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

    public String[] getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String[] serviceLevel) {
        this.serviceLevel = serviceLevel;
    }
}
