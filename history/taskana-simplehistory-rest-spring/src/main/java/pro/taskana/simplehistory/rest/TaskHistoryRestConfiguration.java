package pro.taskana.simplehistory.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;

/** Configuration for Taskana history REST service. */
@Configuration
@ComponentScan("pro.taskana")
@EnableTransactionManagement
public class TaskHistoryRestConfiguration {

  @Bean
  public SimpleHistoryServiceImpl getSimpleHistoryService() {
    return new SimpleHistoryServiceImpl();
  }
}
