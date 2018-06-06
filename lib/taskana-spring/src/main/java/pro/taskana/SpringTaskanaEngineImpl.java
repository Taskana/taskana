package pro.taskana;

import javax.annotation.PostConstruct;

import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import pro.taskana.configuration.SpringTaskanaEngineConfiguration;
import pro.taskana.impl.TaskanaEngineImpl;

/**
 * This class configures the TaskanaEngine for spring
 */
public class SpringTaskanaEngineImpl extends TaskanaEngineImpl {

    public SpringTaskanaEngineImpl(SpringTaskanaEngineConfiguration taskanaEngineConfiguration) {
        super(taskanaEngineConfiguration);
    }

    @PostConstruct
    public void init() {
        this.transactionFactory = new SpringManagedTransactionFactory();
        this.sessionManager = createSqlSessionManager();
    }
}
