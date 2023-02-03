package pro.taskana.properties;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan("pro.taskana")
public class MyTaskanaTestConfiguration {

  @Bean
  @Primary
  public String taskanaPropertiesFileName() {
    return "/mytaskana.properties";
  }

  @Bean
  @Primary
  public String taskanaPropertiesDelimiter() {
    return ";";
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
