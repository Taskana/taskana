package pro.taskana.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketQuery;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketSummary;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.CurrentUserContext;

/**
 * WorkbasketQuery for generating dynamic SQL.
 *
 * @author bbr
 */
public class WorkbasketQueryImpl implements WorkbasketQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryWorkbasket";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketQueryImpl.class);

    private String[] accessId;
    private WorkbasketAuthorization authorization;
    private String[] name;
    private String[] key;
    private String[] domain;
    private WorkbasketType[] type;
    private Date createdAfter;
    private Date createdBefore;
    private Date modifiedAfter;
    private Date modifiedBefore;
    private String description;
    private String[] owner;
    private TaskanaEngineImpl taskanaEngineImpl;

    public WorkbasketQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public WorkbasketQuery keyIn(String... key) {
        this.key = key;
        return this;
    }

    @Override
    public WorkbasketQuery domainIn(String... domain) {
        this.domain = domain;
        return this;
    }

    @Override
    public WorkbasketQuery typeIn(WorkbasketType... type) {
        this.type = type;
        return this;
    }

    @Override
    public WorkbasketQuery nameIn(String... names) {
        this.name = names;
        return this;
    }

    @Override
    public WorkbasketQuery createdAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
        return this;
    }

    @Override
    public WorkbasketQuery createdBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
        return this;
    }

    @Override
    public WorkbasketQuery modifiedAfter(Date modifiedAfter) {
        this.modifiedAfter = modifiedAfter;
        return this;
    }

    @Override
    public WorkbasketQuery modifiedBefore(Date modifiedBefore) {
        this.modifiedBefore = modifiedBefore;
        return this;
    }

    @Override
    public WorkbasketQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    @Override
    public WorkbasketQuery ownerIn(String... owners) {
        this.owner = owners;
        return this;
    }

    @Override
    public WorkbasketQuery access(WorkbasketAuthorization permission, String... accessIds)
        throws InvalidArgumentException {
        if (permission == null) {
            throw new InvalidArgumentException("permission must not be null");
        }

        String[] tempAccessIds = null;

        if (accessIds == null || accessIds.length == 0) {
            List<String> userCtxAccessIds = CurrentUserContext.getAccessIds();
            tempAccessIds = new String[userCtxAccessIds.size()];
            userCtxAccessIds.toArray(tempAccessIds);
        } else {
            tempAccessIds = accessIds;
        }

        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            for (int i = 0; i < tempAccessIds.length; i++) {
                String id = tempAccessIds[i];
                if (id != null) {
                    tempAccessIds[i] = id.toLowerCase();
                } else {
                    tempAccessIds[i] = null;
                }
            }
        }

        this.authorization = permission;
        this.accessId = tempAccessIds;

        return this;
    }

    @Override
    public List<WorkbasketSummary> list() throws NotAuthorizedException {
        LOGGER.debug("entry to list(), this = {}", this);
        List<WorkbasketSummary> result = null;
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
    public List<WorkbasketSummary> list(int offset, int limit) throws NotAuthorizedException {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<WorkbasketSummary> result = null;
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
    public WorkbasketSummary single() throws NotAuthorizedException {
        LOGGER.debug("entry to single(), this = {}", this);
        WorkbasketSummary result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    public String[] getAccessId() {
        return accessId;
    }

    public WorkbasketAuthorization getAuthorization() {
        return authorization;
    }

    public String[] getName() {
        return name;
    }

    public String[] getKey() {
        return key;
    }

    public String[] getDomain() {
        return domain;
    }

    public WorkbasketType[] getType() {
        return type;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public Date getModifiedAfter() {
        return modifiedAfter;
    }

    public Date getModifiedBefore() {
        return modifiedBefore;
    }

    public String getDescription() {
        return description;
    }

    public String[] getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbasketQueryImpl [accessId=");
        builder.append(Arrays.toString(accessId));
        builder.append(", authorization=");
        builder.append(authorization);
        builder.append(", name=");
        builder.append(Arrays.toString(name));
        builder.append(", key=");
        builder.append(Arrays.toString(key));
        builder.append(", domain=");
        builder.append(Arrays.toString(domain));
        builder.append(", type=");
        builder.append(Arrays.toString(type));
        builder.append(", createdAfter=");
        builder.append(createdAfter);
        builder.append(", createdBefore=");
        builder.append(createdBefore);
        builder.append(", modifiedAfter=");
        builder.append(modifiedAfter);
        builder.append(", modifiedBefore=");
        builder.append(modifiedBefore);
        builder.append(", description=");
        builder.append(description);
        builder.append(", owner=");
        builder.append(Arrays.toString(owner));
        builder.append("]");
        return builder.toString();
    }

}
