package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;

/**
 * Implementation of ClassificationQuery interface.
 *
 * @author EH
 */
public class ClassificationQueryImpl implements ClassificationQuery {

    private static final String LINK_TO_SUMMARYMAPPER = "pro.taskana.mappings.QueryMapper.queryClassificationSummaries";
    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryClassifications";
    private static final String LINK_TO_VALUEMAPPER = "pro.taskana.mappings.QueryMapper.queryClassificationColumnValues";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationQueryImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private String columnName;
    private String[] key;
    private String[] idIn;
    private String[] parentId;
    private String[] parentKey;
    private String[] category;
    private String[] type;
    private String[] domain;
    private Boolean validInDomain;
    private TimeInterval[] createdIn;
    private TimeInterval[] modifiedIn;
    private String[] nameIn;
    private String[] nameLike;
    private String descriptionLike;
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
    private List<String> orderBy;
    private List<String> orderColumns;

    ClassificationQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.orderBy = new ArrayList<>();
        this.orderColumns = new ArrayList<>();
    }

    @Override
    public ClassificationQuery keyIn(String... key) {
        this.key = key;
        return this;
    }

    @Override
    public ClassificationQuery idIn(String... id) {
        this.idIn = id;
        return this;
    }

    @Override
    public ClassificationQuery parentIdIn(String... parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public ClassificationQuery parentKeyIn(String... parentKey) {
        this.parentKey = parentKey;
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
    public ClassificationQuery validInDomainEquals(Boolean validInDomain) {
        this.validInDomain = validInDomain;
        return this;
    }

    @Override
    public ClassificationQuery createdWithin(TimeInterval... createdIn) {
        this.createdIn = createdIn;
        for (TimeInterval ti : createdIn) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public ClassificationQuery modifiedWithin(TimeInterval... modifiedIn) {
        this.modifiedIn = modifiedIn;
        for (TimeInterval ti : modifiedIn) {
            if (!ti.isValid()) {
                throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
            }
        }
        return this;
    }

    @Override
    public ClassificationQuery nameIn(String... nameIn) {
        this.nameIn = nameIn;
        return this;
    }

    @Override
    public ClassificationQuery nameLike(String... nameLike) {
        this.nameLike = toUpperCopy(nameLike);
        return this;
    }

    @Override
    public ClassificationQuery descriptionLike(String description) {
        this.descriptionLike = description.toUpperCase();
        return this;
    }

    @Override
    public ClassificationQuery priorityIn(int... priorities) {
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
        this.serviceLevelLike = toUpperCopy(serviceLevelLike);
        return this;
    }

    @Override
    public ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn) {
        this.applicationEntryPointIn = applicationEntryPointIn;
        return this;
    }

    @Override
    public ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike) {
        this.applicationEntryPointLike = toUpperCopy(applicationEntryPointLike);
        return this;
    }

    @Override
    public ClassificationQuery customAttributeIn(String number, String... customIn) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }
        if (customIn.length == 0) {
            throw new InvalidArgumentException(
                "At least one string has to be provided as a search parameter");
        }

        switch (num) {
            case 1:
                this.custom1In = customIn;
                break;
            case 2:
                this.custom2In = customIn;
                break;
            case 3:
                this.custom3In = customIn;
                break;
            case 4:
                this.custom4In = customIn;
                break;
            case 5:
                this.custom5In = customIn;
                break;
            case 6:
                this.custom6In = customIn;
                break;
            case 7:
                this.custom7In = customIn;
                break;
            case 8:
                this.custom8In = customIn;
                break;
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 8");
        }

        return this;
    }

    @Override
    public ClassificationQuery customAttributeLike(String number, String... customLike) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }
        if (customLike.length == 0) {
            throw new InvalidArgumentException(
                "At least one string has to be provided as a search parameter");
        }

        switch (num) {
            case 1:
                this.custom1Like = toUpperCopy(customLike);
                break;
            case 2:
                this.custom2Like = toUpperCopy(customLike);
                break;
            case 3:
                this.custom3Like = toUpperCopy(customLike);
                break;
            case 4:
                this.custom4Like = toUpperCopy(customLike);
                break;
            case 5:
                this.custom5Like = toUpperCopy(customLike);
                break;
            case 6:
                this.custom6Like = toUpperCopy(customLike);
                break;
            case 7:
                this.custom7Like = toUpperCopy(customLike);
                break;
            case 8:
                this.custom8Like = toUpperCopy(customLike);
                break;
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
        }

        return this;
    }

    @Override
    public ClassificationQuery orderByKey(SortDirection sortDirection) {
        return addOrderCriteria("KEY", sortDirection);
    }

    @Override
    public ClassificationQuery orderByParentId(SortDirection sortDirection) {
        return addOrderCriteria("PARENT_ID", sortDirection);
    }

    @Override
    public ClassificationQuery orderByParentKey(SortDirection sortDirection) {
        return addOrderCriteria("PARENT_KEY", sortDirection);
    }

    @Override
    public ClassificationQuery orderByCategory(SortDirection sortDirection) {
        return addOrderCriteria("CATEGORY", sortDirection);
    }

    @Override
    public ClassificationQuery orderByDomain(SortDirection sortDirection) {
        return addOrderCriteria("DOMAIN", sortDirection);
    }

    @Override
    public ClassificationQuery orderByPriority(SortDirection sortDirection) {
        return addOrderCriteria("PRIORITY", sortDirection);
    }

    @Override
    public ClassificationQuery orderByName(SortDirection sortDirection) {
        return addOrderCriteria("NAME", sortDirection);
    }

    @Override
    public ClassificationQuery orderByServiceLevel(SortDirection sortDirection) {
        return addOrderCriteria("SERVICE_LEVEL", sortDirection);
    }

    @Override
    public ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection) {
        return addOrderCriteria("APPLICATION_ENTRY_POINT", sortDirection);
    }

    @Override
    public ClassificationQuery orderByCustomAttribute(String number, SortDirection sortDirection) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }

        switch (num) {
            case 1:
                return addOrderCriteria("CUSTOM_1", sortDirection);
            case 2:
                return addOrderCriteria("CUSTOM_2", sortDirection);
            case 3:
                return addOrderCriteria("CUSTOM_3", sortDirection);
            case 4:
                return addOrderCriteria("CUSTOM_4", sortDirection);
            case 5:
                return addOrderCriteria("CUSTOM_5", sortDirection);
            case 6:
                return addOrderCriteria("CUSTOM_6", sortDirection);
            case 7:
                return addOrderCriteria("CUSTOM_7", sortDirection);
            case 8:
                return addOrderCriteria("CUSTOM_8", sortDirection);
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
        }
    }

    @Override
    public List<ClassificationSummary> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<ClassificationSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_SUMMARYMAPPER, this);
            return result;
        } finally {
            taskanaEngine.returnConnection();
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
        List<ClassificationSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_SUMMARYMAPPER, this, rowBounds);
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
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from list(offset,limit). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<String> listValues(String columnName, SortDirection sortDirection) {
        LOGGER.debug("Entry to listValues(dbColumnName={}) this = {}", columnName, this);
        List<String> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            this.columnName = columnName;
            this.orderBy.clear();
            this.addOrderCriteria(columnName, sortDirection);
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_VALUEMAPPER, this);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("Exit from listValues. Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public ClassificationSummary single() {
        LOGGER.debug("entry to single(), this = {}", this);
        ClassificationSummary result = null;
        try {
            taskanaEngine.openConnection();
            result = taskanaEngine.getSqlSession().selectOne(LINK_TO_SUMMARYMAPPER, this);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    @Override
    public long count() {
        LOGGER.debug("entry to count(), this = {}", this);
        Long rowCount = null;
        try {
            taskanaEngine.openConnection();
            rowCount = taskanaEngine.getSqlSession().selectOne(LINK_TO_COUNTER, this);
            return (rowCount == null) ? 0L : rowCount;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from count(). Returning result {} ", rowCount);
        }
    }

    private ClassificationQuery addOrderCriteria(String columnName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(columnName + orderByDirection);
        orderColumns.add(columnName);
        return this;
    }

    public String[] getKey() {
        return key;
    }

    public String[] getIdIn() {
        return idIn;
    }

    public String[] getparentId() {
        return parentId;
    }

    public String[] getparentKey() {
        return parentKey;
    }

    public String[] getCategory() {
        return category;
    }

    public String[] getType() {
        return type;
    }

    public String[] getNameIn() {
        return nameIn;
    }

    public String[] getNameLike() {
        return nameLike;
    }

    public String getDescriptionLike() {
        return descriptionLike;
    }

    public int[] getPriority() {
        return priority;
    }

    public String[] getServiceLevelIn() {
        return serviceLevelIn;
    }

    public String[] getServiceLevelLike() {
        return serviceLevelLike;
    }

    public String[] getDomain() {
        return domain;
    }

    public Boolean getValidInDomain() {
        return validInDomain;
    }

    public TimeInterval[] getCreatedIn() {
        return createdIn;
    }

    public TimeInterval[] getModifiedIn() {
        return modifiedIn;
    }

    public String[] getApplicationEntryPointIn() {
        return applicationEntryPointIn;
    }

    public String[] getApplicationEntryPointLike() {
        return applicationEntryPointLike;
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

    public String[] getCustom5In() {
        return custom5In;
    }

    public String[] getCustom5Like() {
        return custom5Like;
    }

    public String[] getCustom6In() {
        return custom6In;
    }

    public String[] getCustom6Like() {
        return custom6Like;
    }

    public String[] getCustom7In() {
        return custom7In;
    }

    public String[] getCustom7Like() {
        return custom7Like;
    }

    public String[] getCustom8In() {
        return custom8In;
    }

    public String[] getCustom8Like() {
        return custom8Like;
    }

    public String getColumnName() {
        return columnName;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public List<String> getOrderColumns() {
        return orderColumns;
    }

    private String[] toUpperCopy(String... source) {
        String[] target = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i].toUpperCase();
        }
        return target;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassificationQueryImpl [columnName=");
        builder.append(columnName);
        builder.append(", key=");
        builder.append(Arrays.toString(key));
        builder.append(", idIn=");
        builder.append(Arrays.toString(idIn));
        builder.append(", parentId=");
        builder.append(Arrays.toString(parentId));
        builder.append(", parentKey=");
        builder.append(Arrays.toString(parentKey));
        builder.append(", category=");
        builder.append(Arrays.toString(category));
        builder.append(", type=");
        builder.append(Arrays.toString(type));
        builder.append(", domain=");
        builder.append(Arrays.toString(domain));
        builder.append(", validInDomain=");
        builder.append(validInDomain);
        builder.append(", createdIn=");
        builder.append(Arrays.toString(createdIn));
        builder.append(", modifiedIn=");
        builder.append(Arrays.toString(modifiedIn));
        builder.append(", nameIn=");
        builder.append(Arrays.toString(nameIn));
        builder.append(", nameLike=");
        builder.append(Arrays.toString(nameLike));
        builder.append(", descriptionLike=");
        builder.append(descriptionLike);
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
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }

}
