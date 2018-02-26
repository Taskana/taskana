package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.TimeInterval;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
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
    private String[] domainIn;
    private String[] domainLike;
    private WorkbasketType[] type;
    private TimeInterval[] createdIn;
    private TimeInterval[] modifiedIn;
    private String[] descriptionLike;
    private String[] ownerIn;
    private String[] ownerLike;
    private String[] custom1In;
    private String[] custom1Like;
    private String[] custom2In;
    private String[] custom2Like;
    private String[] custom3In;
    private String[] custom3Like;
    private String[] custom4In;
    private String[] custom4Like;
    private String[] orgLevel1In;
    private String[] orgLevel1Like;
    private String[] orgLevel2In;
    private String[] orgLevel2Like;
    private String[] orgLevel3In;
    private String[] orgLevel3Like;
    private String[] orgLevel4In;
    private String[] orgLevel4Like;
    private TaskanaEngineImpl taskanaEngineImpl;
    private List<String> orderBy;

    WorkbasketQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.orderBy = new ArrayList<>();
    }

    @Override
    public WorkbasketQuery keyIn(String... key) {
        this.keyIn = toUpperCopy(key);
        return this;
    }

    @Override
    public WorkbasketQuery keyLike(String... keys) {
        this.keyLike = toUpperCopy(keys);
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
    public WorkbasketQuery keyOrNameLike(String... keysOrNames) {
        this.keyOrNameLike = toUpperCopy(keysOrNames);
        return this;
    }

    @Override
    public WorkbasketQuery domainIn(String... domain) {
        this.domainIn = domain;
        return this;
    }

    @Override
    public WorkbasketQuery domainLike(String... domain) {
        this.domainLike = domain;
        return this;
    }

    @Override
    public WorkbasketQuery typeIn(WorkbasketType... type) {
        this.type = type;
        return this;
    }

    @Override
    public WorkbasketQuery createdWithin(TimeInterval... intervals) {
        this.createdIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public WorkbasketQuery modifiedWithin(TimeInterval... intervals) {
        this.modifiedIn = intervals;
        for (TimeInterval ti : intervals) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
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
    public WorkbasketQuery custom1In(String... custom1) {
        this.custom1In = custom1;
        return this;
    }

    @Override
    public WorkbasketQuery custom1Like(String... custom1) {
        this.custom1Like = toUpperCopy(custom1);
        return this;
    }

    @Override
    public WorkbasketQuery custom2In(String... custom2) {
        this.custom2In = custom2;
        return this;
    }

    @Override
    public WorkbasketQuery custom2Like(String... custom2) {
        this.custom2Like = toUpperCopy(custom2);
        return this;
    }

    @Override
    public WorkbasketQuery custom3In(String... custom3) {
        this.custom3In = custom3;
        return this;
    }

    @Override
    public WorkbasketQuery custom3Like(String... custom3) {
        this.custom3Like = toUpperCopy(custom3);
        return this;
    }

    @Override
    public WorkbasketQuery custom4In(String... custom4) {
        this.custom4In = custom4;
        return this;
    }

    @Override
    public WorkbasketQuery custom4Like(String... custom4) {
        this.custom4Like = toUpperCopy(custom4);
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel1In(String... orgLevel1) {
        this.orgLevel1In = orgLevel1;
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel1Like(String... orgLevel1) {
        this.orgLevel1Like = toUpperCopy(orgLevel1);
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel2In(String... orgLevel2) {
        this.orgLevel2In = orgLevel2;
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel2Like(String... orgLevel2) {
        this.orgLevel2Like = toUpperCopy(orgLevel2);
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel3In(String... orgLevel3) {
        this.orgLevel3In = orgLevel3;
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel3Like(String... orgLevel3) {
        this.orgLevel3Like = toUpperCopy(orgLevel3);
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel4In(String... orgLevel4) {
        this.orgLevel4In = orgLevel4;
        return this;
    }

    @Override
    public WorkbasketQuery orgLevel4Like(String... orgLevel4) {
        this.orgLevel4Like = toUpperCopy(orgLevel4);
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
    public WorkbasketQuery orderByDomain(SortDirection sortDirection) {
        return addOrderCriteria("DOMAIN", sortDirection);
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
    public WorkbasketQuery orderByCustom1(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_1", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByCustom2(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_2", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByCustom3(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_3", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByCustom4(SortDirection sortDirection) {
        return addOrderCriteria("CUSTOM_4", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByOrgLevel1(SortDirection sortDirection) {
        return addOrderCriteria("ORG_LEVEL_1", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByOrgLevel2(SortDirection sortDirection) {
        return addOrderCriteria("ORG_LEVEL_2", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByOrgLevel3(SortDirection sortDirection) {
        return addOrderCriteria("ORG_LEVEL_3", sortDirection);
    }

    @Override
    public WorkbasketQuery orderByOrgLevel4(SortDirection sortDirection) {
        return addOrderCriteria("ORG_LEVEL_4", sortDirection);
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
        this.authorization = permission;
        return this;
    }

    @Override
    public List<WorkbasketSummary> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<WorkbasketSummary> workbaskets = null;
        try {
            taskanaEngineImpl.openConnection();
            addAccessIdsOfCallerToQuery();
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
            addAccessIdsOfCallerToQuery();
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
            addAccessIdsOfCallerToQuery();
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

    public WorkbasketType[] getType() {
        return type;
    }

    public TimeInterval[] getCreatedIn() {
        return createdIn;
    }

    public TimeInterval[] getModifiedIn() {
        return modifiedIn;
    }

    public String[] getDescriptionLike() {
        return descriptionLike;
    }

    public String[] getOwnerIn() {
        return ownerIn;
    }

    public String[] getDomainIn() {
        return domainIn;
    }

    public String[] getDomainLike() {
        return domainLike;
    }

    public String[] getCustom1In() {
        return custom1In;
    }

    public String[] getCustom1Like() {
        return custom1Like;
    }

    public String[] getCustom2In() {
        return custom2In;
    }

    public String[] getCustom2Like() {
        return custom2Like;
    }

    public String[] getCustom3In() {
        return custom3In;
    }

    public String[] getCustom3Like() {
        return custom3Like;
    }

    public String[] getCustom4In() {
        return custom4In;
    }

    public String[] getCustom4Like() {
        return custom4Like;
    }

    public String[] getOrgLevel1In() {
        return orgLevel1In;
    }

    public String[] getOrgLevel1Like() {
        return orgLevel1Like;
    }

    public String[] getOrgLevel2In() {
        return orgLevel2In;
    }

    public String[] getOrgLevel2Like() {
        return orgLevel2Like;
    }

    public String[] getOrgLevel3In() {
        return orgLevel3In;
    }

    public String[] getOrgLevel3Like() {
        return orgLevel3Like;
    }

    public String[] getOrgLevel4In() {
        return orgLevel4In;
    }

    public String[] getOrgLevel4Like() {
        return orgLevel4Like;
    }

    public String[] getOwnerLike() {
        return ownerLike;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    @Override
    public long count() {
        LOGGER.debug("entry to count(), this = {}", this);
        Long rowCount = null;
        try {
            taskanaEngineImpl.openConnection();
            addAccessIdsOfCallerToQuery();
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
        builder.append(", domainIn=");
        builder.append(Arrays.toString(domainIn));
        builder.append(", domainLike=");
        builder.append(Arrays.toString(domainLike));
        builder.append(", type=");
        builder.append(Arrays.toString(type));
        builder.append(", createdIn=");
        builder.append(Arrays.toString(createdIn));
        builder.append(", modifiedIn=");
        builder.append(Arrays.toString(modifiedIn));
        builder.append(", descriptionLike=");
        builder.append(Arrays.toString(descriptionLike));
        builder.append(", ownerIn=");
        builder.append(Arrays.toString(ownerIn));
        builder.append(", ownerLike=");
        builder.append(Arrays.toString(ownerLike));
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
        builder.append(", orgLevel1In=");
        builder.append(Arrays.toString(orgLevel1In));
        builder.append(", orgLevel1Like=");
        builder.append(Arrays.toString(orgLevel1Like));
        builder.append(", orgLevel2In=");
        builder.append(Arrays.toString(orgLevel2In));
        builder.append(", orgLevel2Like=");
        builder.append(Arrays.toString(orgLevel2Like));
        builder.append(", orgLevel3In=");
        builder.append(Arrays.toString(orgLevel3In));
        builder.append(", orgLevel3Like=");
        builder.append(Arrays.toString(orgLevel3Like));
        builder.append(", orgLevel4In=");
        builder.append(Arrays.toString(orgLevel4In));
        builder.append(", orgLevel4Like=");
        builder.append(Arrays.toString(orgLevel4Like));
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }

    private void addAccessIdsOfCallerToQuery() {
        // might already be set by accessIdsHavePermission
        if (this.accessId == null) {
            String[] accessIds = new String[0];
            List<String> ucAccessIds = CurrentUserContext.getAccessIds();
            if (ucAccessIds != null && !ucAccessIds.isEmpty()) {
                accessIds = new String[ucAccessIds.size()];
                accessIds = ucAccessIds.toArray(accessIds);
            }
            this.accessId = accessIds;
            lowercaseAccessIds(this.accessId);
        }
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
