/*-
 * #%L
 * pro.taskana:taskana-rest-spring-example-wildfly
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.example.wildfly;

import java.io.InputStream;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.sampledata.SampleDataGenerator;

@Configuration
public class TaskanaWildflyConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaWildflyConfiguration.class);

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("getTaskanaEngine") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(
      DataSource dataSource,
      @Value("${taskana.schemaName:TASKANA}") String schemaName,
      @Value("${generateSampleData:true}") boolean generateSampleData) {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    if (generateSampleData) {
      sampleDataGenerator.generateSampleData();
    }
    return sampleDataGenerator;
  }

  @Bean
  public DataSource dataSource() throws Exception {
    // First try to load Properties and get Datasource via jndi lookup
    Context ctx;
    DataSource dataSource;
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    try (InputStream propertyStream = classloader.getResourceAsStream("application.properties")) {
      Properties properties = new Properties();
      ctx = new InitialContext();
      properties.load(propertyStream);
      LOGGER.debug(
          "TASKANA is using datasource '{}' from application.properties.",
          properties.getProperty("datasource.jndi"));
      dataSource = (DataSource) ctx.lookup(properties.getProperty("datasource.jndi"));
      return dataSource;
    } catch (Exception e) {
      LOGGER.error(
          "Caught exception when attempting to start Taskana with Datasource "
              + "from Jndi. Using default H2 datasource. ",
          e);
      throw e;
    }
  }
}
