package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;

/**
 * WorkbasketAccessItemQuery for generating dynamic SQL.
 *
 * @author bbr
 */
public class WorkbasketAccessItemQueryImpl implements WorkbasketAccessItemQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItems";
    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryWorkbasketAccessItems";
    private static final String LINK_TO_VALUEMAPPER = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItemColumnValues";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketQueryImpl.class);
    private String columnName;
    private String[] accessIdIn;
    private String[] workbasketIdIn;
    private String[] idIn;

    private TaskanaEngineImpl taskanaEngine;
    private List<String> orderBy;
    private List<String> orderColumns;

    WorkbasketAccessItemQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        orderBy = new ArrayList<>();
        orderColumns = new ArrayList<>();
    }

    @Override
    public WorkbasketAccessItemQuery idIn(String... ids) {
        this.idIn = ids;
        return this;
    }

    @Override
    public WorkbasketAccessItemQuery workbasketIdIn(String... id) {
        this.workbasketIdIn = id;
        return this;
    }

    @Override
    public WorkbasketAccessItemQuery accessIdIn(String... accessId) {
        this.accessIdIn = accessId;
        WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
        return this;
    }

    @Override
    public WorkbasketAccessItemQuery orderById(SortDirection sortDirection) {
        return addOrderCriteria("ID", sortDirection);
    }

    @Override
    public WorkbasketAccessItemQuery orderByWorkbasketId(SortDirection sortDirection) {
        return addOrderCriteria("WORKBASKET_ID", sortDirection);
    }

    @Override
    public WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection) {
        return addOrderCriteria("ACCESS_ID", sortDirection);
    }

    @Override
    public List<WorkbasketAccessItem> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<WorkbasketAccessItem> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            List<WorkbasketAccessItemImpl> foundAccessItms = taskanaEngine.getSqlSession()
                .selectList(LINK_TO_MAPPER, this);
            result.addAll(foundAccessItms);
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
    public List<String> listValues(String columnName, SortDirection sortDirection) {
        LOGGER.debug("Entry to listValues(dbColumnName={}) this = {}", columnName, this);
        List<String> result = null;
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
    public List<WorkbasketAccessItem> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<WorkbasketAccessItem> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<WorkbasketAccessItemImpl> foundAccessItms = taskanaEngine.getSqlSession()
                .selectList(LINK_TO_MAPPER, this, rowBounds);
            result.addAll(foundAccessItms);
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
    public WorkbasketAccessItem single() {
        LOGGER.debug("entry to single(), this = {}", this);
        WorkbasketAccessItem accessItm = null;
        try {
            taskanaEngine.openConnection();
            accessItm = taskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return accessItm;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", accessItm);
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

    private WorkbasketAccessItemQuery addOrderCriteria(String colName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(colName + orderByDirection);
        orderColumns.add(colName);
        return this;
    }

    public String[] getIdIn() {
        return this.idIn;
    }

    public String[] getAccessIdIn() {
        return accessIdIn;
    }

    public String[] getWorkbasketIdIn() {
        return workbasketIdIn;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public List<String> getOrderColumns() {
        return orderColumns;
    }

    public String getColumnName() {
        return columnName;
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
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }

}
