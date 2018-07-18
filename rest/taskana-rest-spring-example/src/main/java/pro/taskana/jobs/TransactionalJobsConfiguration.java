package pro.taskana.jobs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * Configuration class for Spring sample application.
 */
@Configuration
public class TransactionalJobsConfiguration {

    @Bean
    public TaskanaTransactionProvider<Object> springTransactionProvider() {
        return new pro.taskana.transaction.SpringTransactionProvider();
    }

}
