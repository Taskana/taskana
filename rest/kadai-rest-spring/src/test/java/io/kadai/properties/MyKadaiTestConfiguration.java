package io.kadai.properties;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan("io.kadai")
public class MyKadaiTestConfiguration {

  @Bean
  @Primary
  public String kadaiPropertiesFileName() {
    return "/mykadai.properties";
  }

  @Bean
  @Primary
  public String kadaiPropertiesDelimiter() {
    return ";";
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
