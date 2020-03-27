package pro.taskana.rest.simplehistory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pro.taskana.rest.resource.TaskHistoryEventResourceAssembler;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;

/** Configuration for Taskana history REST service. */
@Configuration
@ComponentScan(basePackages = {"pro.taskana.rest", "pro.taskana.rest.simplehistory"})
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
