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

    private static final String LINK_TO_MAPPER = "pro.taskana.model.mappings.QueryMapper.queryWorkbasketAccessItem";
    private static final String LINK_TO_COUNTER = "pro.taskana.model.mappings.QueryMapper.countQueryWorkbasketAccessItems";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketQueryImpl.class);
    private String[] accessIdIn;
    private String[] workbasketKeyIn;

    private TaskanaEngineImpl taskanaEngineImpl;
    private List<String> orderBy;

    WorkbasketAccessItemQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        orderBy = new ArrayList<>();
    }

    @Override
    public WorkbasketAccessItemQuery workbasketKeyIn(String... key) {
        this.workbasketKeyIn = key;
        return this;
    }

    @Override
    public WorkbasketAccessItemQuery accessIdIn(String... accessId) {
        this.accessIdIn = accessId;
        WorkbasketQueryImpl.lowercaseAccessIds(this.accessIdIn);
        return this;
    }

    @Override
    public WorkbasketAccessItemQuery orderByWorkbasketKey(SortDirection sortDirection) {
        return addOrderCriteria("WORKBASKET_KEY", sortDirection);
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
            taskanaEngineImpl.openConnection();
            List<WorkbasketAccessItemImpl> foundAccessItms = taskanaEngineImpl.getSqlSession()
                .selectList(LINK_TO_MAPPER, this);
            result.addAll(foundAccessItms);
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
    public List<WorkbasketAccessItem> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<WorkbasketAccessItem> result = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            List<WorkbasketAccessItemImpl> foundAccessItms = taskanaEngineImpl.getSqlSession()
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
            taskanaEngineImpl.returnConnection();
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
            taskanaEngineImpl.openConnection();
            accessItm = taskanaEngineImpl.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return accessItm;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", accessItm);
        }
    }

    @Override
    public long count() {
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

    private WorkbasketAccessItemQuery addOrderCriteria(String colName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(colName + orderByDirection);
        return this;
    }

    public String[] getAccessIdIn() {
        return accessIdIn;
    }

    public String[] getWorkbasketKeyIn() {
        return workbasketKeyIn;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbasketAccessItemQueryImpl [accessIdIn=");
        builder.append(Arrays.toString(accessIdIn));
        builder.append(", workbasketKeyIn=");
        builder.append(Arrays.toString(workbasketKeyIn));
        builder.append(", orderBy=");
        builder.append(orderBy);
        builder.append("]");
        return builder.toString();
    }

}
