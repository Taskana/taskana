package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ObjectReference;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.impl.util.LoggerUtils;

/**
 * Implementation of ObjectReferenceQuery interface.
 *
 * @author EH
 */
public class ObjectReferenceQueryImpl implements ObjectReferenceQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.mappings.QueryMapper.queryObjectReferences";
    private static final String LINK_TO_COUNTER = "pro.taskana.mappings.QueryMapper.countQueryObjectReferences";
    private static final String LINK_TO_VALUEMAPPER = "pro.taskana.mappings.QueryMapper.queryObjectReferenceColumnValues";
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectReferenceQueryImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private String columnName;
    private String[] company;
    private String[] system;
    private String[] systemInstance;
    private String[] type;
    private String[] value;
    private List<String> orderBy;

    ObjectReferenceQueryImpl(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.orderBy = new ArrayList<>();
    }

    @Override
    public ObjectReferenceQuery companyIn(String... companies) {
        this.company = companies;
        return this;
    }

    @Override
    public ObjectReferenceQuery systemIn(String... systems) {
        this.system = systems;
        return this;
    }

    @Override
    public ObjectReferenceQuery systemInstanceIn(String... systemInstances) {
        this.systemInstance = systemInstances;
        return this;
    }

    @Override
    public ObjectReferenceQuery typeIn(String... types) {
        this.type = types;
        return this;
    }

    @Override
    public ObjectReferenceQuery valueIn(String... values) {
        this.value = values;
        return this;
    }

    @Override
    public List<ObjectReference> list() {
        LOGGER.debug("entry to list(), this = {}", this);
        List<ObjectReference> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this);
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
    public List<ObjectReference> list(int offset, int limit) {
        LOGGER.debug("entry to list(offset = {}, limit = {}), this = {}", offset, limit, this);
        List<ObjectReference> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            RowBounds rowBounds = new RowBounds(offset, limit);
            result = taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
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
    public ObjectReference single() {
        LOGGER.debug("entry to single(), this = {}", this);
        ObjectReference result = null;
        try {
            taskanaEngine.openConnection();
            result = taskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from single(). Returning result {} ", result);
        }
    }

    public String[] getCompany() {
        return company;
    }

    public void setCompany(String[] company) {
        this.company = company;
    }

    public String[] getSystem() {
        return system;
    }

    public void setSystem(String[] system) {
        this.system = system;
    }

    public String[] getSystemInstance() {
        return systemInstance;
    }

    public void setSystemInstance(String[] systemInstance) {
        this.systemInstance = systemInstance;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
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

    private ObjectReferenceQuery addOrderCriteria(String colName, SortDirection sortDirection) {
        String orderByDirection = " ASC";
        if (sortDirection != null && SortDirection.DESCENDING.equals(sortDirection)) {
            orderByDirection = " DESC";
        }
        orderBy.add(colName + orderByDirection);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ObjectReferenceQueryImpl [taskanaEngineImpl=");
        builder.append(taskanaEngine);
        builder.append(", company=");
        builder.append(Arrays.toString(company));
        builder.append(", system=");
        builder.append(Arrays.toString(system));
        builder.append(", systemInstance=");
        builder.append(Arrays.toString(systemInstance));
        builder.append(", type=");
        builder.append(Arrays.toString(type));
        builder.append(", value=");
        builder.append(Arrays.toString(value));
        builder.append("]");
        return builder.toString();
    }
}
