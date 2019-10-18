package pro.taskana.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationService;
import pro.taskana.JobService;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.AutocommitFailedException;
import pro.taskana.exceptions.ConnectionNotSetException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.UnsupportedDatabaseException;
import pro.taskana.history.HistoryEventProducer;
import pro.taskana.impl.persistence.MapTypeHandler;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.DistributionTargetMapper;
import pro.taskana.mappings.JobMapper;
import pro.taskana.mappings.ObjectReferenceMapper;
import pro.taskana.mappings.QueryMapper;
import pro.taskana.mappings.TaskMapper;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.mappings.WorkbasketAccessMapper;
import pro.taskana.mappings.WorkbasketMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * This is the implementation of TaskanaEngine.
 */
public class TaskanaEngineImpl implements TaskanaEngine {

    private static final String DEFAULT = "default";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineImpl.class);
    private static ThreadLocal<Deque<SqlSessionManager>> sessionStack = new ThreadLocal<>();
    protected TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected TransactionFactory transactionFactory;
    protected SqlSessionManager sessionManager;
    protected ConnectionManagementMode mode = ConnectionManagementMode.PARTICIPATE;
    protected java.sql.Connection connection = null;
    private HistoryEventProducer historyEventProducer;
    private Internal internal;

    protected TaskanaEngineImpl(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;
        createTransactionFactory(taskanaEngineConfiguration.getUseManagedTransactions());
        this.sessionManager = createSqlSessionManager();
        historyEventProducer = HistoryEventProducer.getInstance(taskanaEngineConfiguration);
        this.internal = new Internal();
    }

    public static TaskanaEngine createTaskanaEngine(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        return new TaskanaEngineImpl(taskanaEngineConfiguration);
    }

    /**
     * With sessionStack, we maintain a Stack of SqlSessionManager objects on a per thread basis. SqlSessionManager is
     * the MyBatis object that wraps database connections. The purpose of this stack is to keep track of nested calls.
     * Each external API call is wrapped into taskanaEngineImpl.openConnection(); .....
     * taskanaEngineImpl.returnConnection(); calls. In order to avoid duplicate opening / closing of connections, we use
     * the sessionStack in the following way: Each time, an openConnection call is received, we push the current
     * sessionManager onto the stack. On the first call to openConnection, we call sessionManager.startManagedSession()
     * to open a database connection. On each call to returnConnection() we pop one instance of sessionManager from the
     * stack. When the stack becomes empty, we close the database connection by calling sessionManager.close()
     *
     * @return Stack of SqlSessionManager
     */
    private static Deque<SqlSessionManager> getSessionStack() {
        Deque<SqlSessionManager> stack = sessionStack.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            sessionStack.set(stack);
        }
        return stack;
    }

    private static SqlSessionManager getSessionFromStack() {
        Deque<SqlSessionManager> stack = getSessionStack();
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    private static void pushSessionToStack(SqlSessionManager session) {
        getSessionStack().push(session);
    }

    private static void popSessionFromStack() {
        Deque<SqlSessionManager> stack = getSessionStack();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public static boolean isDb2(String dbProductName) {
        return dbProductName.contains("DB2");
    }

    public static boolean isH2(String databaseProductName) {
        return databaseProductName.contains("H2");
    }

    public static boolean isPostgreSQL(String databaseProductName) {
        return "PostgreSQL".equals(databaseProductName);
    }

    @Override
    public TaskService getTaskService() {
        SqlSession session = this.sessionManager;
        return new TaskServiceImpl(internal, session.getMapper(TaskMapper.class),
            session.getMapper(AttachmentMapper.class));
    }

    @Override
    public TaskMonitorService getTaskMonitorService() {
        SqlSession session = this.sessionManager;
        return new TaskMonitorServiceImpl(internal,
            session.getMapper(TaskMonitorMapper.class));
    }

    @Override
    public WorkbasketService getWorkbasketService() {
        SqlSession session = this.sessionManager;
        return new WorkbasketServiceImpl(internal,
            session.getMapper(WorkbasketMapper.class),
            session.getMapper(DistributionTargetMapper.class),
            session.getMapper(WorkbasketAccessMapper.class));
    }

    @Override
    public ClassificationService getClassificationService() {
        SqlSession session = this.sessionManager;
        return new ClassificationServiceImpl(internal, session.getMapper(ClassificationMapper.class),
            session.getMapper(TaskMapper.class));
    }

    @Override
    public JobService getJobService() {
        SqlSession session = this.sessionManager;
        return new JobServiceImpl(internal, session.getMapper(JobMapper.class));
    }

    @Override
    public TaskanaEngineConfiguration getConfiguration() {
        return this.taskanaEngineConfiguration;
    }

    @Override
    public boolean isHistoryEnabled() {
        return HistoryEventProducer.isHistoryEnabled();
    }

    @Override
    public void setConnectionManagementMode(ConnectionManagementMode mode) {
        if (this.mode == ConnectionManagementMode.EXPLICIT && connection != null
            && mode != ConnectionManagementMode.EXPLICIT) {
            if (sessionManager.isManagedSessionStarted()) {
                sessionManager.close();
            }
            connection = null;
        }
        this.mode = mode;
    }

    @Override
    public void setConnection(java.sql.Connection connection) throws SQLException {
        if (connection != null) {
            this.connection = connection;
            // disabling auto commit for passed connection in order to gain full control over the connection management
            connection.setAutoCommit(false);
            connection.setSchema(taskanaEngineConfiguration.getSchemaName());
            mode = ConnectionManagementMode.EXPLICIT;
            sessionManager.startManagedSession(connection);
        } else if (this.connection != null) {
            closeConnection();
        }
    }

    @Override
    public void closeConnection() {
        if (this.mode == ConnectionManagementMode.EXPLICIT) {
            this.connection = null;
            if (sessionManager.isManagedSessionStarted()) {
                sessionManager.close();
            }
            mode = ConnectionManagementMode.PARTICIPATE;
        }
    }

    @Override
    public void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException {
        if (!isUserInRole(roles)) {
            if (LOGGER.isDebugEnabled()) {
                String accessIds = LoggerUtils.listToString(CurrentUserContext.getAccessIds());
                String rolesAsString = Arrays.toString(roles);
                LOGGER.debug("Throwing NotAuthorizedException because accessIds {} are not member of roles {}",
                    accessIds,
                    rolesAsString);
            }
            throw new NotAuthorizedException("current user is not member of role(s) " + Arrays.toString(roles));
        }
    }

    @Override
    public boolean isUserInRole(TaskanaRole... roles) {
        if (!getConfiguration().isSecurityEnabled()) {
            return true;
        }

        List<String> accessIds = CurrentUserContext.getAccessIds();
        Set<String> rolesMembers = new HashSet<>();
        for (TaskanaRole role : roles) {
            rolesMembers.addAll(getConfiguration().getRoleMap().get(role));
        }
        for (String accessId : accessIds) {
            if (rolesMembers.contains(accessId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method creates the sqlSessionManager of myBatis. It integrates all the SQL mappers and sets the databaseId
     * attribute.
     *
     * @return a {@link SqlSessionFactory}
     */
    protected SqlSessionManager createSqlSessionManager() {
        Environment environment = new Environment(DEFAULT, this.transactionFactory,
            taskanaEngineConfiguration.getDatasource());
        Configuration configuration = new Configuration(environment);

        // set databaseId
        String databaseProductName;
        try (Connection con = taskanaEngineConfiguration.getDatasource().getConnection()) {
            databaseProductName = con.getMetaData().getDatabaseProductName();
            if (isDb2(databaseProductName)) {
                configuration.setDatabaseId("db2");
            } else if (isH2(databaseProductName)) {
                configuration.setDatabaseId("h2");
            } else if (isPostgreSQL(databaseProductName)) {
                configuration.setDatabaseId("postgres");
            } else {
                throw new UnsupportedDatabaseException(databaseProductName);
            }

        } catch (SQLException e) {
            throw new SystemException(
                "Method createSqlSessionManager() could not open a connection to the database. No databaseId has been set.",
                e.getCause());
        }

        // add mappers
        configuration.addMapper(TaskMapper.class);
        configuration.addMapper(TaskMonitorMapper.class);
        configuration.addMapper(WorkbasketMapper.class);
        configuration.addMapper(DistributionTargetMapper.class);
        configuration.addMapper(ClassificationMapper.class);
        configuration.addMapper(WorkbasketAccessMapper.class);
        configuration.addMapper(ObjectReferenceMapper.class);
        configuration.addMapper(QueryMapper.class);
        configuration.addMapper(AttachmentMapper.class);
        configuration.addMapper(JobMapper.class);
        configuration.getTypeHandlerRegistry().register(MapTypeHandler.class);
        SqlSessionFactory localSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        return SqlSessionManager.newInstance(localSessionFactory);
    }

    /**
     * creates the MyBatis transaction factory.
     *
     * @param useManagedTransactions true, if managed transations should be used. Otherwise false.
     */
    private void createTransactionFactory(boolean useManagedTransactions) {
        if (useManagedTransactions) {
            this.transactionFactory = new ManagedTransactionFactory();
        } else {
            this.transactionFactory = new JdbcTransactionFactory();
        }
    }

    /**
     * Internal Engine for internal operations.
     */
    private class Internal implements TaskanaEngine.Internal {

        @Override
        public void openConnection() {
            initSqlSession();
            try {
                sessionManager.getConnection().setSchema(taskanaEngineConfiguration.getSchemaName());
            } catch (SQLException e) {
                throw new SystemException(
                    "Method openConnection() could not open a connection to the database. No schema has been created.",
                    e.getCause());
            }
            if (mode != ConnectionManagementMode.EXPLICIT) {
                pushSessionToStack(sessionManager);
            }
        }

        @Override
        public void initSqlSession() {
            if (mode == ConnectionManagementMode.EXPLICIT && connection == null) {
                throw new ConnectionNotSetException();
            } else if (mode != ConnectionManagementMode.EXPLICIT && !sessionManager.isManagedSessionStarted()) {
                sessionManager.startManagedSession();
            }
        }

        @Override
        public void returnConnection() {
            if (mode != ConnectionManagementMode.EXPLICIT) {
                popSessionFromStack();
                if (getSessionStack().isEmpty()
                    && sessionManager != null && sessionManager.isManagedSessionStarted()) {
                    if (mode == ConnectionManagementMode.AUTOCOMMIT) {
                        try {
                            sessionManager.commit();
                        } catch (Exception e) {
                            throw new AutocommitFailedException(e.getCause());
                        }
                    }
                    sessionManager.close();
                }
            }
        }

        @Override
        public boolean domainExists(String domain) {
            return getConfiguration().getDomains().contains(domain);
        }

        @Override
        public SqlSession getSqlSession() {
            return sessionManager;
        }

        @Override
        public TaskanaEngine getEngine() {
            return TaskanaEngineImpl.this;
        }

        @Override
        public HistoryEventProducer getHistoryEventProducer() {
            return historyEventProducer;
        }

    }
}
