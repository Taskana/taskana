package pro.taskana.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskanaTransactionProvider;

/**
 * Configuration class for Spring sample application.
 */
@Import(RestConfiguration.class)
public class SampleConfiguration {

    @Bean
    public TaskanaTransactionProvider<BulkOperationResults<String, Exception>> springTransactionProvider() {
        return new SpringTransactionProvider();
    }

}
