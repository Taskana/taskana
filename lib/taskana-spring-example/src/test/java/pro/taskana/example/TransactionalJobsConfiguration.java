package pro.taskana.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pro.taskana.common.internal.transaction.SpringTransactionProvider;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** Configuration class for Spring sample application. */
@Configuration
public class TransactionalJobsConfiguration {

  @Bean
  public TaskanaTransactionProvider<Object> springTransactionProvider() {
    return new SpringTransactionProvider();
  }
}
