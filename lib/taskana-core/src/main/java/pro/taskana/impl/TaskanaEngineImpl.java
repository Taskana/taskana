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
    protected static ThreadLocal<Deque<SqlSessionManager>> sessionStack = new ThreadLocal<>();
    protected TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected TransactionFactory transactionFactory;
    protected SqlSessionManager sessionManager;
    protected ConnectionManagementMode mode = ConnectionManagementMode.PARTICIPATE;
    protected java.sql.Connection connection = null;

    protected TaskanaEngineImpl(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;
        createTransactionFactory(taskanaEngineConfiguration.getUseManagedTransactions());
        this.sessionManager = createSqlSessionManager();
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
    protected static Deque<SqlSessionManager> getSessionStack() {
        Deque<SqlSessionManager> stack = sessionStack.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            sessionStack.set(stack);
        }
        return stack;
    }

    protected static SqlSessionManager getSessionFromStack() {
        Deque<SqlSessionManager> stack = getSessionStack();
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    protected static void pushSessionToStack(SqlSessionManager session) {
        getSessionStack().push(session);
    }

    protected static void popSessionFromStack() {
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
        return databaseProductName.equals("PostgreSQL");
    }

    @Override
    public TaskService getTaskService() {
        SqlSession session = this.sessionManager;
        return new TaskServiceImpl(this, session.getMapper(TaskMapper.class),
            session.getMapper(AttachmentMapper.class));
    }

    @Override
    public TaskMonitorService getTaskMonitorService() {
        SqlSession session = this.sessionManager;
        return new TaskMonitorServiceImpl(this,
            session.getMapper(TaskMonitorMapper.class));
    }

    @Override
    public WorkbasketService getWorkbasketService() {
        SqlSession session = this.sessionManager;
        return new WorkbasketServiceImpl(this,
            session.getMapper(WorkbasketMapper.class),
            session.getMapper(DistributionTargetMapper.class),
            session.getMapper(WorkbasketAccessMapper.class));
    }

    @Override
    public ClassificationService getClassificationService() {
        SqlSession session = this.sessionManager;
        return new ClassificationServiceImpl(this, session.getMapper(ClassificationMapper.class),
            session.getMapper(TaskMapper.class));
    }

    @Override
    public JobService getJobService() {
        SqlSession session = this.sessionManager;
        return new JobServiceImpl(this, session.getMapper(JobMapper.class));
    }

    @Override
    public TaskanaEngineConfiguration getConfiguration() {
        return this.taskanaEngineConfiguration;
    }

    /**
     * sets the connection management mode.
     *
     * @param mode
     *            - the connection management mode Valid values are:
     *            <ul>
     *            <li>PARTICIPATE - taskana participates in global transaction. This is the default mode.</li>
     *            <li>AUTOCOMMIT - taskana commits each API call separately</li>
     *            <li>EXPLICIT - commit processing is managed explicitly by the client</li>
     *            </ul>
     */
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

    /**
     * Set the database connection to be used by taskana. If this Api is called, taskana uses the connection passed by
     * the client for database access in all subsequent API calls until the client resets this connection. Control over
     * commit and rollback is the responsibility of the client. In order to close the connection, the client can call
     * TaskanaEngine.closeConnection() or TaskanaEngine.setConnection(null). Both calls have the same effect.
     *
     * @param connection
     *            The connection that passed into TaskanaEngine
     */
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

    /**
     * closes the connection to the database in mode EXPLICIT. In mode EXPLICIT, closes the client's connection, sets it
     * to null and switches to mode PARTICIPATE Has the same effect as setConnection(null)
     */
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

    /**
     * Open the connection to the database. to be called at the begin of each Api call that accesses the database
     *
     */
    void openConnection() {
        initSqlSession();
        try {
            this.sessionManager.getConnection().setSchema(taskanaEngineConfiguration.getSchemaName());
        } catch (SQLException e) {
            throw new SystemException(
                "Method openConnection() could not open a connection to the database. No schema has been created.",
                e.getCause());
        }
        if (mode != ConnectionManagementMode.EXPLICIT) {
            pushSessionToStack(this.sessionManager);
        }
    }

    /**
     * Initializes the SqlSessionManager.
     */
    void initSqlSession() {
        if (mode == ConnectionManagementMode.EXPLICIT && this.connection == null) {
            throw new ConnectionNotSetException();
        } else if (mode != ConnectionManagementMode.EXPLICIT && !this.sessionManager.isManagedSessionStarted()) {
            this.sessionManager.startManagedSession();
        }
    }

    /**
     * Returns the database connection into the pool. In the case of nested calls, simply pops the latest session from
     * the session stack. Closes the connection if the session stack is empty. In mode AUTOCOMMIT commits before the
     * connection is closed. To be called at the end of each Api call that accesses the database
     */
    void returnConnection() {
        if (this.mode != ConnectionManagementMode.EXPLICIT) {
            popSessionFromStack();
            if (getSessionStack().isEmpty()
                && this.sessionManager != null && this.sessionManager.isManagedSessionStarted()) {
                if (this.mode == ConnectionManagementMode.AUTOCOMMIT) {
                    try {
                        this.sessionManager.commit();
                    } catch (Exception e) {
                        throw new AutocommitFailedException(e.getCause());
                    }
                }
                this.sessionManager.close();
            }
        }
    }

    /**
     * retrieve the SqlSession used by taskana.
     *
     * @return the myBatis SqlSession object used by taskana
     */
    SqlSession getSqlSession() {
        return this.sessionManager;
    }

    /**
     * Checks whether current user is member of any of the specified roles.
     *
     * @param roles
     *            The roles that are checked for membership of the current user
     * @throws NotAuthorizedException
     *             If the current user is not member of any specified role
     */
    @Override
    public void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException {
        if (!isUserInRole(roles)) {
            if (LOGGER.isErrorEnabled()) {
                String accessIds = LoggerUtils.listToString(CurrentUserContext.getAccessIds());
                String rolesAsString = Arrays.toString(roles);
                LOGGER.error("Throwing NotAuthorizedException because accessIds {} are not member of roles {}",
                    accessIds,
                    rolesAsString);
            }
            throw new NotAuthorizedException("current user is not member of role(s) " + Arrays.toString(roles));
        }
    }

    /**
     * check whether the current user is member of one of the roles specified.
     *
     * @param roles
     *            The roles that are checked for membership of the current user
     * @return true if the current user is a member of at least one of the specified groups
     */
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
     * @param useManagedTransactions
     */
    private void createTransactionFactory(boolean useManagedTransactions) {
        if (useManagedTransactions) {
            this.transactionFactory = new ManagedTransactionFactory();
        } else {
            this.transactionFactory = new JdbcTransactionFactory();
        }
    }

    /**
     * Returns true if the given domain does exist in the configuration.
     *
     * @param domain
     *            the domain specified in the configuration
     * @return <code>true</code> if the domain exists
     */
    public boolean domainExists(String domain) {
        return getConfiguration().getDomains().contains(domain);
    }

}
