package pro.taskana.impl;

import org.apache.ibatis.session.RowBounds;

import pro.taskana.ClassificationQuery;
import pro.taskana.TaskanaEngine;
import pro.taskana.model.Classification;

import java.util.Date;
import java.util.List;

/**
 * Implementation of ClassificationQuery interface.
 * @author EH
 */
public class ClassificationQueryImpl implements ClassificationQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryClassification";
    private TaskanaEngineImpl taskanaEngineImpl;
    private String[] parentClassificationId;
    private String[] category;
    private String[] type;
    private String[] domain;
    private Boolean validInDomain;
    private Date[] created;
    private String[] name;
    private String description;
    private int[] priority;
    private String[] serviceLevel;
    private String[] customFields;
    private Date[] validFrom;
    private Date[] validUntil;

    public ClassificationQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
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
    public ClassificationQuery domain(String... domain) {
        this.domain = domain;
        return this;
    }

    @Override
    public ClassificationQuery validInDomain(Boolean validInDomain) {
        this.validInDomain = validInDomain;
        return this;
    }

    @Override
    public ClassificationQuery created(Date... created) {
        this.created = created;
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
    public ClassificationQuery customFields(String... customFields) {
        this.customFields = customFields;
        return this;
    }

    @Override
    public ClassificationQuery validFrom(Date... validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    @Override
    public ClassificationQuery validUntil(Date... validUntil) {
        this.validUntil = validUntil;
        return this;
    }

    @Override
    public List<Classification> list() {
        try {
            taskanaEngineImpl.openConnection();
            return taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public List<Classification> list(int offset, int limit) {
        try {
            taskanaEngineImpl.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            return taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Classification single() {
        try {
            taskanaEngineImpl.openConnection();
            return taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
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

    public String[] getDomain() {
        return domain;
    }

    public void setDomain(String[] domain) {
        this.domain = domain;
    }

    public Boolean getValidInDomain() {
        return validInDomain;
    }

    public void setValidInDomain(Boolean validInDomain) {
        this.validInDomain = validInDomain;
    }

    public Date[] getCreated() {
        return created;
    }

    public void setCreated(Date[] created) {
        this.created = created;
    }

    public String[] getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String[] customFields) {
        this.customFields = customFields;
    }

    public Date[] getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date[] validFrom) {
        this.validFrom = validFrom;
    }

    public Date[] getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date[] validUntil) {
        this.validUntil = validUntil;
    }
}
