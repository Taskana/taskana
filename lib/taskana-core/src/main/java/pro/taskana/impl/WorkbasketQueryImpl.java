package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.security.CurrentUserContext;

/**
 * WorkbasketQuery for generating dynamic SQL.
 *
 * @author bbr
 */
public class WorkbasketQueryImpl implements WorkbasketQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.mappings.QueryMapper.queryWorkbasket";
    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryWorkbaskets";
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
    private String[] descriptionLike;
    private String[] ownerIn;
    private String[] ownerLike;
    private TaskanaEngineImpl taskanaEngineImpl;
    private List<String> orderBy;

    WorkbasketQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        orderBy = new ArrayList<>();
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
    public WorkbasketQuery nameLike(String... names) {
        this.nameLike = toUpperCopy(names);
        return this;
    }

    @Override
    public WorkbasketQuery keyLike(String... keys) {
        this.keyLike = toUpperCopy(keys);
        return this;
    }

    @Override
    public WorkbasketQuery keyOrNameLike(String... keysOrNames) {
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
    public WorkbasketQuery descriptionLike(String... description) {
        this.descriptionLike = toUpperCopy(description);
        return this;
    }

    @Override
    public WorkbasketQuery ownerIn(String... owners) {
        this.ownerIn = owners;
        return this;
    }

    @Override
    public WorkbasketQuery ownerLike(String... owners) {
        this.ownerLike = toUpperCopy(owners);
        return this;
    }

    @Override
    public WorkbasketQuery orderByName(SortDirection sortDirection) {
        return addOrderCriteria("NAME", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByKey(SortDirection sortDirection) {
        return addOrderCriteria("KEY", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByDescription(SortDirection sortDirection) {
        return addOrderCriteria("DESCRIPTION", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByOwner(SortDirection sortDirection) {
        return addOrderCriteria("OWNER", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByType(SortDirection sortDirection) {
        return addOrderCriteria("TYPE", sortDirection);
    }

    @Override
    public WorkbasketQuery accessIdsHavePermission(WorkbasketAuthorization permission, String... accessIds)
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
        lowercaseAccessIds(this.accessId);

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
        if (ucAccessIds != null && !ucAccessIds.isEmpty()) {
            accessIds = new String[ucAccessIds.size()];
            accessIds = ucAccessIds.toArray(accessIds);
        } else {
            throw new InvalidArgumentException("CurrentUserContext need to have at least one accessId.");
        }

        // set up permissions and ids
        this.authorization = permission;
        this.accessId = accessIds;
        lowercaseAccessIds(this.accessId);

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

    public String[] getDescriptionLike() {
        return descriptionLike;
    }

    public String[] getOwnerIn() {
        return ownerIn;
    }

    public String[] getOwnerLike() {
        return ownerLike;
    }

    public List<String> getOrderBy() {
        return orderBy;
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
        builder.append(Arrays.toString(descriptionLike));
        builder.append(", ownerIn=");
        builder.append(Arrays.toString(ownerIn));
        builder.append(", ownerLike=");
        builder.append(Arrays.toString(ownerLike));
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }

    static void lowercaseAccessIds(String[] accessIdArray) {
        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            for (int i = 0; i < accessIdArray.length; i++) {
                String id = accessIdArray[i];
                if (id != null) {
                    accessIdArray[i] = id.toLowerCase();
                }
            }
        }
    }

    static String[] toUpperCopy(String... source) {
        if (source == null || source.length == 0) {
            return null;
        } else {
            String[] target = new String[source.length];
            for (int i = 0; i < source.length; i++) {
                target[i] = source[i].toUpperCase();
            }
            return target;
        }
    }

    private WorkbasketQuery addOrderCriteria(String colName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(colName + orderByDirection);
        return this;
    }

}
