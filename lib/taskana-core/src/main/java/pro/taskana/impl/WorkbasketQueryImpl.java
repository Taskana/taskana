package pro.taskana.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BaseQuery;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketType;
import pro.taskana.model.mappings.WorkbasketAccessMapper;
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
    private String[] nameIn;
    private String[] nameLike;
    private String[] keyIn;
    private String[] keyLike;
    private String[] keyOrNameLike;
    private String[] domain;
    private WorkbasketType[] type;
    private Instant createdAfter;
    private Instant createdBefore;
    private Instant modifiedAfter;
    private Instant modifiedBefore;
    private String descriptionLike;
    private String[] owner;
    private TaskanaEngineImpl taskanaEngineImpl;

    public WorkbasketQueryImpl(TaskanaEngine taskanaEngine, WorkbasketAccessMapper workbasketAccessMapper) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    }

    @Override
    public WorkbasketQuery keyIn(String... key) {
        this.keyIn = toUpperCopy(key);
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
        this.nameIn = toUpperCopy(names);
        return this;
    }

    @Override
    public BaseQuery<WorkbasketSummary> nameLike(String... names) {
        this.nameLike = toUpperCopy(names);
        return this;
    }

    @Override
    public BaseQuery<WorkbasketSummary> keyLike(String... keys) {
        this.keyLike = toUpperCopy(keys);
        return this;
    }

    @Override
    public BaseQuery<WorkbasketSummary> keyOrNameLike(String... keysOrNames) {
        this.keyOrNameLike = toUpperCopy(keysOrNames);
        return this;
    }

    @Override
    public WorkbasketQuery createdAfter(Instant createdAfter) {
        this.createdAfter = createdAfter;
        return this;
    }

    @Override
    public WorkbasketQuery createdBefore(Instant createdBefore) {
        this.createdBefore = createdBefore;
        return this;
    }

    @Override
    public WorkbasketQuery modifiedAfter(Instant modifiedAfter) {
        this.modifiedAfter = modifiedAfter;
        return this;
    }

    @Override
    public WorkbasketQuery modifiedBefore(Instant modifiedBefore) {
        this.modifiedBefore = modifiedBefore;
        return this;
    }

    @Override
    public WorkbasketQuery descriptionLike(String description) {
        this.descriptionLike = description.toUpperCase();
        return this;
    }

    @Override
    public WorkbasketQuery ownerIn(String... owners) {
        this.owner = owners;
        return this;
    }

    @Override
    public WorkbasketQuery accessIdsHavePersmission(WorkbasketAuthorization permission, String... accessIds)
        throws InvalidArgumentException {
        // Checking pre-conditions
        if (permission == null) {
            throw new InvalidArgumentException("Permission can´t be null.");
        }
        if (accessIds == null || accessIds.length == 0) {
            throw new InvalidArgumentException("accessIds can´t be NULL or empty.");
        }

        // set up permissions and ids
        this.authorization = permission;
        this.accessId = accessIds;
        lowercaseAccessIds();

        return this;
    }

    @Override
    public WorkbasketQuery callerHasPermission(WorkbasketAuthorization permission) throws InvalidArgumentException {
        String[] accessIds;
        // Check pre-conditions
        if (permission == null) {
            throw new InvalidArgumentException("Permission cannot be null.");
        }
        List<String> ucAccessIds = CurrentUserContext.getAccessIds();
        if (ucAccessIds != null && ucAccessIds.size() > 0) {
            accessIds = new String[ucAccessIds.size()];
            accessIds = ucAccessIds.toArray(accessIds);
        } else {
            throw new InvalidArgumentException("CurrentUserContext need to have at least one accessId.");
        }

        // set up permissions and ids
        this.authorization = permission;
        this.accessId = accessIds;
        lowercaseAccessIds();

        return this;
    }

    @Override
    public List<WorkbasketSummary> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<WorkbasketSummary> workbaskets = null;
        try {
            taskanaEngineImpl.openConnection();
            workbaskets = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this);
            return workbaskets;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = workbaskets == null ? 0 : workbaskets.size();
                LOGGER.debug("exit from list(). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(workbaskets));
            }
        }
    }

    @Override
    public List<WorkbasketSummary> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<WorkbasketSummary> workbaskets = null;
        try {
            taskanaEngineImpl.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            workbaskets = taskanaEngineImpl.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
            return workbaskets;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = workbaskets == null ? 0 : workbaskets.size();
                LOGGER.debug("exit from list(offset,limit). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(workbaskets));
            }
        }
    }

    @Override
    public WorkbasketSummary single() {
        LOGGER.debug("entry to single(), this = {}", this);
        WorkbasketSummary workbasket = null;
        try {
            taskanaEngineImpl.openConnection();
            workbasket = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return workbasket;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", workbasket);
        }
    }

    public String[] getAccessId() {
        return accessId;
    }

    public WorkbasketAuthorization getAuthorization() {
        return authorization;
    }

    public String[] getNameIn() {
        return nameIn;
    }

    public String[] getNameLike() {
        return nameLike;
    }

    public String[] getKeyIn() {
        return keyIn;
    }

    public String[] getKeyLike() {
        return keyLike;
    }

    public String[] getKeyOrNameLike() {
        return keyOrNameLike;
    }

    public String[] getDomain() {
        return domain;
    }

    public WorkbasketType[] getType() {
        return type;
    }

    public Instant getCreatedAfter() {
        return createdAfter;
    }

    public Instant getCreatedBefore() {
        return createdBefore;
    }

    public Instant getModifiedAfter() {
        return modifiedAfter;
    }

    public Instant getModifiedBefore() {
        return modifiedBefore;
    }

    public String getDescriptionLike() {
        return descriptionLike;
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
        builder.append(", nameIn=");
        builder.append(Arrays.toString(nameIn));
        builder.append(", nameLike=");
        builder.append(Arrays.toString(nameLike));
        builder.append(", keyIn=");
        builder.append(Arrays.toString(keyIn));
        builder.append(", keyLike=");
        builder.append(Arrays.toString(keyLike));
        builder.append(", keyOrNameLike=");
        builder.append(Arrays.toString(keyOrNameLike));
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
        builder.append(", descriptionLike=");
        builder.append(descriptionLike);
        builder.append(", owner=");
        builder.append(Arrays.toString(owner));
        builder.append(", taskanaEngineImpl=");
        builder.append(taskanaEngineImpl);
        builder.append("]");
        return builder.toString();
    }

    private void lowercaseAccessIds() {
        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            for (int i = 0; i < this.accessId.length; i++) {
                String id = this.accessId[i];
                if (id != null) {
                    this.accessId[i] = id.toLowerCase();
                }
            }
        }
    }

    private String[] toUpperCopy(String... source) {
        String[] target = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i].toUpperCase();
        }
        return target;
    }

}
