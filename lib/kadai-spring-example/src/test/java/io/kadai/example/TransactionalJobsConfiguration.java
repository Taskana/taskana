package io.kadai.example;

import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.internal.transaction.SpringTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for Spring sample application. */
@Configuration
public class TransactionalJobsConfiguration {

  @Bean
  public KadaiTransactionProvider springTransactionProvider() {
    return new SpringTransactionProvider();
  }
}
