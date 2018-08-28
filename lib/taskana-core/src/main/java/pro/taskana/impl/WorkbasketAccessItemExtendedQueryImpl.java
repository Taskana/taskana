package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketAccessItemExtended;
import pro.taskana.WorkbasketAccessItemExtendedQuery;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;

/**
 * WorkbasketAccessItemQuery for generating dynamic SQL.
 *
 * @author mmr
 */
public class WorkbasketAccessItemExtendedQueryImpl implements
    WorkbasketAccessItemExtendedQuery {

    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryWorkbasketAccessItems";
    private static final String LINK_TO_MAPPER_EXTENDED = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItemsExtended";
    private static final String LINK_TO_VALUEMAPPER_EXTENDED = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItemExtendedColumnValues";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketAccessItemExtended.class);
    private String[] workbasketKeyIn;
    private String[] workbasketKeyLike;
    private String columnName;
    private String[] accessIdIn;
    private String[] accessIdLike;
    private String[] workbasketIdIn;
    private String[] idIn;

    private TaskanaEngineImpl taskanaEngine;
    private List<String> orderBy;
    private List<String> orderColumns;

    WorkbasketAccessItemExtendedQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        orderBy = new ArrayList<>();
        orderColumns = new ArrayList<>();
    }

    @Override
    public WorkbasketAccessItemExtendedQuery workbasketKeyIn(String... keys) {
        this.workbasketKeyIn = keys;
        return this;
    }

    @Override
    public WorkbasketAccessItemExtendedQuery orderByWorkbasketKey(SortDirection sortDirection) {
        return addOrderCriteria("WB.KEY", sortDirection);
    }

    @Override
    public WorkbasketAccessItemExtendedQuery idIn(String... ids) {
        this.idIn = ids;
        return this;
    }

    @Override
    public WorkbasketAccessItemExtendedQuery workbasketIdIn(String... workbasketId) {
        this.workbasketIdIn = workbasketId;
        return this;
    }

    @Override
    public WorkbasketAccessItemExtendedQuery accessIdIn(String... accessId) {
        this.accessIdIn = accessId;
        WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
        return this;
    }

    @Override
    public WorkbasketAccessItemExtendedQuery orderByWorkbasketId(SortDirection sortDirection) {
        return addOrderCriteria("WORKBASKET_ID", sortDirection);
    }

    @Override
    public WorkbasketAccessItemExtendedQuery orderByAccessId(SortDirection sortDirection) {
        return addOrderCriteria("ACCESS_ID", sortDirection);
    }

    @Override
    public WorkbasketAccessItemExtendedQuery orderById(SortDirection sortDirection) {
        return addOrderCriteria("ID", sortDirection);
    }

    @Override
    public WorkbasketAccessItemExtendedQuery workbasketKeyLike(String... key) {
        this.workbasketKeyLike = toUpperCopy(key);
        return this;
    }

    @Override
    public WorkbasketAccessItemExtendedQuery accessIdLike(String... ids) {
        this.accessIdLike = toUpperCopy(ids);
        return this;
    }

    public String[] getWorkbasketKeyIn() {
        return workbasketKeyIn;
    }

    public void setWorkbasketKeyIn(String[] workbasketKeyIn) {
        this.workbasketKeyIn = workbasketKeyIn;
    }

    public String[] getAccessIdLike() {
        return accessIdLike;
    }

    public void setAccessIdLike(String[] accessIdLike) {
        this.accessIdLike = accessIdLike;
    }

    public String[] getWorkbasketKeyLike() {
        return workbasketKeyLike;
    }

    public void setWorkbasketKeyLike(String[] workbasketKeyLike) {
        this.workbasketKeyLike = workbasketKeyLike;
    }

    @Override
    public List<WorkbasketAccessItemExtended> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<WorkbasketAccessItemExtended> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            List<WorkbasketAccessItemExtendedImpl> foundAccessItems = taskanaEngine.getSqlSession()
                .selectList(LINK_TO_MAPPER_EXTENDED, this);
            result.addAll(foundAccessItems);
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
    public List<WorkbasketAccessItemExtended> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<WorkbasketAccessItemExtended> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<WorkbasketAccessItemExtended> foundAccessItems = taskanaEngine.getSqlSession()
                .selectList(LINK_TO_MAPPER_EXTENDED, this, rowBounds);
            result.addAll(foundAccessItems);
            return result;
        } catch (PersistenceException e) {
            if (e.getMessage().contains("ERRORCODE=-4470")) {
                TaskanaRuntimeException ex = new TaskanaRuntimeException(
                    "The offset beginning was set over the amount of result-rows.", e.getCause());
                ex.setStackTrace(e.getStackTrace());
                throw ex;
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
        List<String> result = null;
        try {
            taskanaEngine.openConnection();
            this.columnName = columnName;
            this.orderBy.clear();
            this.addOrderCriteria(columnName, sortDirection);
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_VALUEMAPPER_EXTENDED, this);
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
    public WorkbasketAccessItemExtended single() {
        LOGGER.debug("entry to single(), this = {}", this);
        WorkbasketAccessItemExtended accessItem = null;
        try {
            taskanaEngine.openConnection();
            accessItem = taskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER_EXTENDED, this);
            return accessItem;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", accessItem);
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

    public String getColumnName() {
        return columnName;
    }

    public String[] getAccessIdIn() {
        return accessIdIn;
    }

    public String[] getWorkbasketIdIn() {
        return workbasketIdIn;
    }

    public String[] getIdIn() {
        return idIn;
    }

    public List<String> getOrderColumns() {
        return orderColumns;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    private WorkbasketAccessItemExtendedQuery addOrderCriteria(String colName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(colName + orderByDirection);
        orderColumns.add(colName);
        return this;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbasketAccessItemQueryImpl [idIn=");
        builder.append(Arrays.toString(idIn));
        builder.append(", accessIdIn=");
        builder.append(Arrays.toString(accessIdIn));
        builder.append(", workbasketIdIn=");
        builder.append(Arrays.toString(workbasketIdIn));
        builder.append(", workbasketKeyIn=");
        builder.append(Arrays.toString(workbasketKeyIn));
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }
}
