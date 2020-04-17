package pro.taskana.simplehistory.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.rest.resource.TaskHistoryEventResourceAssembler;

/** Configuration for Taskana history REST service. */
@Configuration
@ComponentScan(basePackages = {"pro.taskana.rest", "pro.taskana.simplehistory.rest"})
@EnableTransactionManagement
public class TaskHistoryRestConfiguration {

  @Bean
  public SimpleHistoryServiceImpl getSimpleHistoryService() {
    return new SimpleHistoryServiceImpl();
  }

  @Bean
  public TaskHistoryEventResourceAssembler getTaskHistoryEventResourceAssembler() {
    return new TaskHistoryEventResourceAssembler();
  }
}
