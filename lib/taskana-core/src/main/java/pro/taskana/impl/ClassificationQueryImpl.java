package pro.taskana.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;

/**
 * Implementation of ClassificationQuery interface.
 *
 * @author EH
 */
public class ClassificationQueryImpl implements ClassificationQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.mappings.QueryMapper.queryClassification";
    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryClassifications";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationQueryImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private String[] key;
    private String[] parentClassificationKey;
    private String[] category;
    private String[] type;
    private String[] domain;
    private Boolean validInDomain;
    private Instant[] created;
    private String[] nameIn;
    private String[] nameLike;
    private String description;
    private int[] priority;
    private String[] serviceLevelIn;
    private String[] serviceLevelLike;
    private String[] applicationEntryPointIn;
    private String[] applicationEntryPointLike;
    private String[] custom1In;
    private String[] custom1Like;
    private String[] custom2In;
    private String[] custom2Like;
    private String[] custom3In;
    private String[] custom3Like;
    private String[] custom4In;
    private String[] custom4Like;
    private String[] custom5In;
    private String[] custom5Like;
    private String[] custom6In;
    private String[] custom6Like;
    private String[] custom7In;
    private String[] custom7Like;
    private String[] custom8In;
    private String[] custom8Like;

    ClassificationQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public ClassificationQuery keyIn(String... key) {
        this.key = key;
        return this;
    }

    @Override
    public ClassificationQuery parentClassificationKeyIn(String... parentClassificationKey) {
        this.parentClassificationKey = parentClassificationKey;
        return this;
    }

    @Override
    public ClassificationQuery categoryIn(String... category) {
        this.category = category;
        return this;
    }

    @Override
    public ClassificationQuery typeIn(String... type) {
        this.type = type;
        return this;
    }

    @Override
    public ClassificationQuery domainIn(String... domain) {
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
    public ClassificationQuery nameIn(String... nameIn) {
        this.nameIn = nameIn;
        return this;
    }

    @Override
    public ClassificationQuery nameLike(String... nameLike) {
        this.nameLike = nameLike;
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
    public ClassificationQuery serviceLevelIn(String... serviceLevelIn) {
        this.serviceLevelIn = serviceLevelIn;
        return this;
    }

    @Override
    public ClassificationQuery serviceLevelLike(String... serviceLevelLike) {
        this.serviceLevelLike = serviceLevelLike;
        return this;
    }

    @Override
    public ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn) {
        this.applicationEntryPointIn = applicationEntryPointIn;
        return this;
    }

    @Override
    public ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike) {
        this.applicationEntryPointLike = applicationEntryPointLike;
        return this;
    }

    @Override
    public ClassificationQuery custom1In(String... custom1In) {
        this.custom1In = custom1In;
        return this;
    }

    @Override
    public ClassificationQuery custom1Like(String... custom1Like) {
        this.custom1Like = custom1Like;
        return this;
    }

    @Override
    public ClassificationQuery custom2In(String... custom2In) {
        this.custom2In = custom2In;
        return this;
    }

    @Override
    public ClassificationQuery custom2Like(String... custom2Like) {
        this.custom2Like = custom2Like;
        return this;
    }

    @Override
    public ClassificationQuery custom3In(String... custom3In) {
        this.custom3In = custom3In;
        return this;
    }

    @Override
    public ClassificationQuery custom3Like(String... custom3Like) {
        this.custom3Like = custom3Like;
        return this;
    }

    @Override
    public ClassificationQuery custom4In(String... custom4In) {
        this.custom4In = custom4In;
        return this;
    }

    @Override
    public ClassificationQuery custom4Like(String... custom4Like) {
        this.custom4Like = custom4Like;
        return this;
    }

    @Override
    public ClassificationQuery custom5In(String... custom5In) {
        this.custom5In = custom5In;
        return this;
    }

    @Override
    public ClassificationQuery custom5Like(String... custom5Like) {
        this.custom5Like = custom5Like;
        return this;
    }

    @Override
    public ClassificationQuery custom6In(String... custom6In) {
        this.custom6In = custom6In;
        return this;
    }

    @Override
    public ClassificationQuery custom6Like(String... custom6Like) {
        this.custom6Like = custom6Like;
        return this;
    }

    @Override
    public ClassificationQuery custom7In(String... custom7In) {
        this.custom7In = custom7In;
        return this;
    }

    @Override
    public ClassificationQuery custom7Like(String... custom7Like) {
        this.custom7Like = custom7Like;
        return this;
    }

    @Override
    public ClassificationQuery custom8In(String... custom8In) {
        this.custom8In = custom8In;
        return this;
    }

    @Override
    public ClassificationQuery custom8Like(String... custom8Like) {
        this.custom8Like = custom8Like;
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
        } catch (Exception e) {
            if (e instanceof PersistenceException) {
                if (e.getMessage().contains("ERRORCODE=-4470")) {
                    TaskanaRuntimeException ex = new TaskanaRuntimeException(
                        "The offset beginning was set over the amount of result-rows.", e.getCause());
                    ex.setStackTrace(e.getStackTrace());
                    throw ex;
                }
            }
            throw e;
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

    public String[] getNameIn() {
        return nameIn;
    }

    public void setNameIn(String[] nameIn) {
        this.nameIn = nameIn;
    }

    public String[] getNameLike() {
        return nameLike;
    }

    public void setNameLike(String[] nameLike) {
        this.nameLike = nameLike;
    }

    public String getDescriptionLike() {
        return description;
    }

    public void setDescriptionLike(String description) {
        this.description = description;
    }

    public int[] getPriority() {
        return priority;
    }

    public void setPriority(int[] priority) {
        this.priority = priority;
    }

    public String[] getServiceLevelIn() {
        return serviceLevelIn;
    }

    public void setServiceLevelIn(String[] serviceLevel) {
        this.serviceLevelIn = serviceLevel;
    }

    public String[] getServiceLevelLike() {
        return serviceLevelLike;
    }

    public void setServiceLevelLike(String[] serviceLevelLike) {
        this.serviceLevelLike = serviceLevelLike;
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

    public String[] getApplicationEntryPointIn() {
        return applicationEntryPointIn;
    }

    public void setApplicationEntryPointIn(String[] applicationEntryPoint) {
        this.applicationEntryPointIn = applicationEntryPoint;
    }

    public String[] getApplicationEntryPointLike() {
        return applicationEntryPointLike;
    }

    public void setApplicationEntryPointLike(String[] applicationEntryPoint) {
        this.applicationEntryPointLike = applicationEntryPoint;
    }

    public String[] getCustom1In() {
        return custom1In;
    }

    public void setCustom1In(String[] custom1In) {
        this.custom1In = custom1In;
    }

    public String[] getCustom1Like() {
        return custom1Like;
    }

    public void setCustom1Like(String[] custom1Like) {
        this.custom1Like = custom1Like;
    }

    public String[] getCustom2In() {
        return custom2In;
    }

    public void setCustom2In(String[] custom2In) {
        this.custom2In = custom2In;
    }

    public String[] getCustom2Like() {
        return custom2Like;
    }

    public void setCustom2Like(String[] custom2Like) {
        this.custom2Like = custom2Like;
    }

    public String[] getCustom3In() {
        return custom3In;
    }

    public void setCustom3In(String[] custom3In) {
        this.custom3In = custom3In;
    }

    public String[] getCustom3Like() {
        return custom3Like;
    }

    public void setCustom3Like(String[] custom3Like) {
        this.custom3Like = custom3Like;
    }

    public String[] getCustom4In() {
        return custom4In;
    }

    public void setCustom4In(String[] custom4In) {
        this.custom4In = custom4In;
    }

    public String[] getCustom4Like() {
        return custom4Like;
    }

    public void setCustom4Like(String[] custom4Like) {
        this.custom4Like = custom4Like;
    }

    public String[] getCustom5In() {
        return custom5In;
    }

    public void setCustom5In(String[] custom5In) {
        this.custom5In = custom5In;
    }

    public String[] getCustom5Like() {
        return custom5Like;
    }

    public void setCustom5Like(String[] custom5Like) {
        this.custom5Like = custom5Like;
    }

    public String[] getCustom6In() {
        return custom6In;
    }

    public void setCustom6In(String[] custom6In) {
        this.custom6In = custom6In;
    }

    public String[] getCustom6Like() {
        return custom6Like;
    }

    public void setCustom6Like(String[] custom6Like) {
        this.custom6Like = custom6Like;
    }

    public String[] getCustom7In() {
        return custom7In;
    }

    public void setCustom7In(String[] custom7In) {
        this.custom7In = custom7In;
    }

    public String[] getCustom7Like() {
        return custom7Like;
    }

    public void setCustom7Like(String[] custom7Like) {
        this.custom7Like = custom7Like;
    }

    public String[] getCustom8In() {
        return custom8In;
    }

    public void setCustom8In(String[] custom8In) {
        this.custom8In = custom8In;
    }

    public String[] getCustom8Like() {
        return custom8Like;
    }

    public void setCustom8Like(String[] custom8Like) {
        this.custom8Like = custom8Like;
    }

    @Override
    public long count() throws NotAuthorizedException {
        LOGGER.debug("entry to count(), this = {}", this);
        Long rowCount = null;
        try {
            taskanaEngineImpl.openConnection();
            rowCount = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_COUNTER, this);
            return (rowCount == null) ? 0L : rowCount;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from count(). Returning result {} ", rowCount);
        }
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
        builder.append(", nameIn=");
        builder.append(Arrays.toString(nameIn));
        builder.append(", nameLike=");
        builder.append(Arrays.toString(nameLike));
        builder.append(", descriptionLike=");
        builder.append(description);
        builder.append(", priority=");
        builder.append(Arrays.toString(priority));
        builder.append(", serviceLevelIn=");
        builder.append(Arrays.toString(serviceLevelIn));
        builder.append(", serviceLevelLike=");
        builder.append(Arrays.toString(serviceLevelLike));
        builder.append(", applicationEntryPointIn=");
        builder.append(Arrays.toString(applicationEntryPointIn));
        builder.append(", applicationEntryPointLike=");
        builder.append(Arrays.toString(applicationEntryPointLike));
        builder.append(", custom1In=");
        builder.append(Arrays.toString(custom1In));
        builder.append(", custom1Like=");
        builder.append(Arrays.toString(custom1Like));
        builder.append(", custom2In=");
        builder.append(Arrays.toString(custom2In));
        builder.append(", custom2Like=");
        builder.append(Arrays.toString(custom2Like));
        builder.append(", custom3In=");
        builder.append(Arrays.toString(custom3In));
        builder.append(", custom3Like=");
        builder.append(Arrays.toString(custom3Like));
        builder.append(", custom4In=");
        builder.append(Arrays.toString(custom4In));
        builder.append(", custom4Like=");
        builder.append(Arrays.toString(custom4Like));
        builder.append(", custom5In=");
        builder.append(Arrays.toString(custom5In));
        builder.append(", custom5Like=");
        builder.append(Arrays.toString(custom5Like));
        builder.append(", custom6In=");
        builder.append(Arrays.toString(custom6In));
        builder.append(", custom6Like=");
        builder.append(Arrays.toString(custom6Like));
        builder.append(", custom7In=");
        builder.append(Arrays.toString(custom7In));
        builder.append(", custom7Like=");
        builder.append(Arrays.toString(custom7Like));
        builder.append(", custom8In=");
        builder.append(Arrays.toString(custom8In));
        builder.append(", custom8Like=");
        builder.append(Arrays.toString(custom8Like));
        builder.append("]");
        return builder.toString();
    }
}
