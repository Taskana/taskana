package org.taskana.impl;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.taskana.ClassificationService;
import org.taskana.TaskService;
import org.taskana.TaskanaEngine;
import org.taskana.WorkbasketService;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.model.mappings.ClassificationMapper;
import org.taskana.model.mappings.DistributionTargetMapper;
import org.taskana.model.mappings.ObjectReferenceMapper;
import org.taskana.model.mappings.TaskMapper;
import org.taskana.model.mappings.WorkbasketAccessMapper;
import org.taskana.model.mappings.WorkbasketMapper;

/**
 * This is the implementation of TaskanaEngine.
 */
public class TaskanaEngineImpl implements TaskanaEngine {

    private static final String DEFAULT = "default";

    protected TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected SqlSession session;
    protected TransactionFactory transactionFactory;

    private TaskMapper taskMapper;
    private WorkbasketMapper workbasketMapper;
    private DistributionTargetMapper distributionTargetMapper;
    private ClassificationMapper classificationMapper;
    private WorkbasketAccessMapper workbasketAccessMapper;
    private ObjectReferenceMapper objectReferenceMapper;

    private TaskServiceImpl taskServiceImpl;
    private WorkbasketServiceImpl workbasketServiceImpl;

    public TaskanaEngineImpl(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;

        createTransactionFactory(taskanaEngineConfiguration.getUseContainerManagedTransactions());

        this.session = createSqlSessionFactory().openSession();
        this.taskMapper = session.getMapper(TaskMapper.class);
        this.workbasketMapper = session.getMapper(WorkbasketMapper.class);
        this.distributionTargetMapper = session.getMapper(DistributionTargetMapper.class);
        this.classificationMapper = session.getMapper(ClassificationMapper.class);
        this.workbasketAccessMapper = session.getMapper(WorkbasketAccessMapper.class);
        this.objectReferenceMapper = session.getMapper(ObjectReferenceMapper.class);
    }

    @Override
    public TaskService getTaskService() {
        this.taskServiceImpl = new TaskServiceImpl(this, this.taskMapper, this.objectReferenceMapper);
        return taskServiceImpl;
    }

    @Override
    public WorkbasketService getWorkbasketService() {
        this.workbasketServiceImpl = new WorkbasketServiceImpl(this, this.workbasketMapper,
                this.distributionTargetMapper, this.workbasketAccessMapper);
        return workbasketServiceImpl;
    }

    @Override
    public ClassificationService getClassificationService() {
        return new ClassificationServiceImpl(this.classificationMapper);
    }

    @Override
    public TaskanaEngineConfiguration getConfiguration() {
        return this.taskanaEngineConfiguration;
    }

    /**
     * Close session manually, to be done, if a JdbcTransactionFactory is used.
     * Perhaps it is better to separate the commit and the closing mechanism ...
     */
    public void closeSession() {
        this.session.commit();
        this.session.close();
    }

    /**
     * This method creates the sqlSessionFactory of myBatis. It integrates all the
     * SQL mappers
     * @return a {@link SqlSessionFactory}
     */
    private SqlSessionFactory createSqlSessionFactory() {
        Environment environment = new Environment(DEFAULT, this.transactionFactory,
                taskanaEngineConfiguration.getDatasource());
        Configuration configuration = new Configuration(environment);
        // add mappers
        configuration.addMapper(TaskMapper.class);
        configuration.addMapper(WorkbasketMapper.class);
        configuration.addMapper(DistributionTargetMapper.class);
        configuration.addMapper(ClassificationMapper.class);
        configuration.addMapper(WorkbasketAccessMapper.class);
        configuration.addMapper(ObjectReferenceMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private void createTransactionFactory(boolean useContainerManagedTransactions) {
        if (useContainerManagedTransactions) {
            this.transactionFactory = new ManagedTransactionFactory();
        } else {
            this.transactionFactory = new JdbcTransactionFactory();
        }
    }

}
