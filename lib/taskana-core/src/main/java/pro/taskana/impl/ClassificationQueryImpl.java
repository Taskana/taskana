package pro.taskana.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.impl.util.LoggerUtils;

/**
 * Implementation of ClassificationQuery interface.
 *
 * @author EH
 */
public class ClassificationQueryImpl implements ClassificationQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryClassification";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationQueryImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private String[] key;
    private String[] parentClassificationKey;
    private String[] category;
    private String[] type;
    private String[] domain;
    private Boolean validInDomain;
    private Instant[] created;
    private String[] name;
    private String description;
    private int[] priority;
    private String[] serviceLevel;
    private String[] customFields;
    private String[] applicationEntryPoint;

    public ClassificationQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public ClassificationQuery key(String... key) {
        this.key = key;
        return this;
    }

    @Override
    public ClassificationQuery parentClassificationKey(String... parentClassificationKey) {
        this.parentClassificationKey = parentClassificationKey;
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
    public ClassificationQuery created(Instant... created) {
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
    public ClassificationQuery applicationEntryPoint(String... applicationEntryPoint) {
        this.applicationEntryPoint = applicationEntryPoint;
        return this;
    }

    @Override
    public ClassificationQuery customFields(String... customFields) {
        this.customFields = customFields;
        return this;
    }

    @Override
    public List<ClassificationSummary> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<ClassificationSummary> result = null;
        try {
            taskanaEngineImpl.openConnection();
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
    public List<ClassificationSummary> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<ClassificationSummary> result = null;
        try {
            taskanaEngineImpl.openConnection();
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
    public ClassificationSummary single() {
        LOGGER.debug("entry to single(), this = {}", this);
        ClassificationSummary result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    public String[] getKey() {
        return key;
    }

    public void setKey(String[] key) {
        this.key = key;
    }

    public String[] getParentClassificationKey() {
        return parentClassificationKey;
    }

    public void setParentClassificationKey(String[] parentClassificationKey) {
        this.parentClassificationKey = parentClassificationKey;
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

    public Instant[] getCreated() {
        return created;
    }

    public void setCreated(Instant[] created) {
        this.created = created;
    }

    public String[] getApplicationEntryPoint() {
        return applicationEntryPoint;
    }

    public void setApplicationEntryPoint(String[] applicationEntryPoint) {
        this.applicationEntryPoint = applicationEntryPoint;
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
        builder.append("ClassificationQueryImpl [taskanaEngineImpl=");
        builder.append(taskanaEngineImpl);
        builder.append(", parentClassificationKey=");
        builder.append(Arrays.toString(parentClassificationKey));
        builder.append(", category=");
        builder.append(Arrays.toString(category));
        builder.append(", type=");
        builder.append(Arrays.toString(type));
        builder.append(", domain=");
        builder.append(Arrays.toString(domain));
        builder.append(", validInDomain=");
        builder.append(validInDomain);
        builder.append(", created=");
        builder.append(Arrays.toString(created));
        builder.append(", name=");
        builder.append(Arrays.toString(name));
        builder.append(", description=");
        builder.append(description);
        builder.append(", priority=");
        builder.append(Arrays.toString(priority));
        builder.append(", serviceLevel=");
        builder.append(Arrays.toString(serviceLevel));
        builder.append(", customFields=");
        builder.append(Arrays.toString(customFields));
        builder.append("]");
        return builder.toString();
    }
}
