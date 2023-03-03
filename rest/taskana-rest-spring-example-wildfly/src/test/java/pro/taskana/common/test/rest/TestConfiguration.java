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
package pro.taskana.common.test.rest;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.common.internal.configuration.DB;
import pro.taskana.sampledata.SampleDataGenerator;

@SpringBootApplication
@ComponentScan("pro.taskana")
@DependsOn("getTaskanaEngine") // wait for schema to be created BEFORE inserting test data
public class TestConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestConfiguration.class);

  @Autowired
  public TestConfiguration(
      @Value("${taskana.schemaName:TASKANA}") String schemaName, DataSource dataSource) {
    if (LOGGER.isDebugEnabled()) {
      try (Connection connection = dataSource.getConnection()) {
        LOGGER.debug(
            "Using database of type {} with url '{}'",
            DB.getDatabaseProductName(connection),
            connection.getMetaData().getURL());
      } catch (SQLException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
    new SampleDataGenerator(dataSource, schemaName).generateSampleData();
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
