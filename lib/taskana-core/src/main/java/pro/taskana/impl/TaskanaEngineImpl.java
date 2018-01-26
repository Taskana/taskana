package pro.taskana.impl;

import java.util.Stack;

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
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.AutocommitFailedException;
import pro.taskana.exceptions.ConnectionNotSetException;
import pro.taskana.impl.persistence.MapTypeHandler;
import pro.taskana.model.mappings.AttachmentMapper;
import pro.taskana.model.mappings.ClassificationMapper;
import pro.taskana.model.mappings.DistributionTargetMapper;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.QueryMapper;
import pro.taskana.model.mappings.TaskMapper;
import pro.taskana.model.mappings.TaskMonitorMapper;
import pro.taskana.model.mappings.WorkbasketAccessMapper;
import pro.taskana.model.mappings.WorkbasketMapper;

/**
 * This is the implementation of TaskanaEngine.
 */
public class TaskanaEngineImpl implements TaskanaEngine {

    private static final String DEFAULT = "default";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineImpl.class);
    protected static ThreadLocal<Stack<SqlSessionManager>> sessionStack = new ThreadLocal<Stack<SqlSessionManager>>();
    protected TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected TransactionFactory transactionFactory;
    protected SqlSessionManager sessionManager;
    protected SqlSessionFactory sessionFactory;
    protected ConnectionManagementMode mode = ConnectionManagementMode.PARTICIPATE;
    protected java.sql.Connection connection = null;

    public TaskanaEngineImpl(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;
        createTransactionFactory(taskanaEngineConfiguration.getUseManagedTransactions());
        this.sessionManager = createSqlSessionManager();
    }

    @Override
    public TaskService getTaskService() {
        SqlSession session = this.sessionManager;
        TaskServiceImpl taskServiceImpl = new TaskServiceImpl(this, session.getMapper(TaskMapper.class),
            session.getMapper(ObjectReferenceMapper.class), session.getMapper(AttachmentMapper.class));
        return taskServiceImpl;
    }

    @Override
    public TaskMonitorService getTaskMonitorService() {
        SqlSession session = this.sessionManager;
        TaskMonitorServiceImpl taskMonitorServiceImpl = new TaskMonitorServiceImpl(this,
            session.getMapper(TaskMonitorMapper.class));
        return taskMonitorServiceImpl;
    }

    @Override
    public WorkbasketService getWorkbasketService() {
        SqlSession session = this.sessionManager;
        WorkbasketServiceImpl workbasketServiceImpl = new WorkbasketServiceImpl(this,
            session.getMapper(WorkbasketMapper.class),
            session.getMapper(DistributionTargetMapper.class),
            session.getMapper(WorkbasketAccessMapper.class));
        return workbasketServiceImpl;
    }

    @Override
    public ClassificationService getClassificationService() {
        SqlSession session = this.sessionManager;
        return new ClassificationServiceImpl(this, session.getMapper(ClassificationMapper.class));
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
     *            TODO
     */
    @Override
    public void setConnection(java.sql.Connection connection) {
        if (connection != null) {
            this.connection = connection;
            mode = ConnectionManagementMode.EXPLICIT;
            sessionManager.startManagedSession(connection);
        } else if (this.connection != null) {
            this.connection = null;
            if (sessionManager.isManagedSessionStarted()) {
                sessionManager.close();
            }
            mode = ConnectionManagementMode.PARTICIPATE;
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
     */
    void openConnection() {
        initSqlSession();
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
        } else if (mode != ConnectionManagementMode.EXPLICIT) {
            if (!this.sessionManager.isManagedSessionStarted()) {
                this.sessionManager.startManagedSession();
            }
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
                        LOGGER.error("closeSession(): Tried to Autocommit and caught exception" + e);
                        throw new AutocommitFailedException(e);
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
     * This method creates the sqlSessionManager of myBatis. It integrates all the SQL mappers
     *
     * @return a {@link SqlSessionFactory}
     */
    private SqlSessionManager createSqlSessionManager() {
        Environment environment = new Environment(DEFAULT, this.transactionFactory,
            taskanaEngineConfiguration.getDatasource());
        Configuration configuration = new Configuration(environment);
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
        configuration.getTypeHandlerRegistry().register(MapTypeHandler.class);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSessionManager sessionManager = SqlSessionManager.newInstance(sessionFactory);
        return sessionManager;
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
    protected static Stack<SqlSessionManager> getSessionStack() {
        Stack<SqlSessionManager> stack = sessionStack.get();
        if (stack == null) {
            stack = new Stack<SqlSessionManager>();
            sessionStack.set(stack);
        }
        return stack;
    }

    protected static SqlSessionManager getSessionFromStack() {
        Stack<SqlSessionManager> stack = getSessionStack();
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    protected static void pushSessionToStack(SqlSessionManager session) {
        getSessionStack().push(session);
    }

    protected static void popSessionFromStack() {
        Stack<SqlSessionManager> stack = getSessionStack();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }
}
