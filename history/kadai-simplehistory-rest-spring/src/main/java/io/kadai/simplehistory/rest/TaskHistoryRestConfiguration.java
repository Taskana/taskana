package io.kadai.simplehistory.rest;

import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Configuration for Kadai history REST service. */
@Configuration
@ComponentScan("io.kadai")
@EnableTransactionManagement
public class TaskHistoryRestConfiguration {

  @Bean
  public SimpleHistoryServiceImpl getSimpleHistoryService() {
    return new SimpleHistoryServiceImpl();
  }
}
