/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.internal.configuration.DB;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.sampledata.SampleDataGenerator;

/** Test that the schema name can be customized. */
@TaskanaSpringBootTest
class SchemaNameCustomizableTest {

  String schemaName = "CUSTOMSCHEMANAME";
  boolean isPostgres = false;

  @Autowired private DataSource dataSource;

  void resetDb() throws SQLException {
    SampleDataGenerator sampleDataGenerator;
    try (Connection connection = dataSource.getConnection()) {
      String databaseProductId = DB.getDatabaseProductId(connection);

      if (DB.isPostgres(databaseProductId)) {
        schemaName = schemaName.toLowerCase(Locale.ENGLISH);
      }
    }
    sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
  }

  @BeforeEach
  void setup() throws Exception {
    resetDb();
  }

  @Disabled("sampledatagenerator cannot handle a different schema")
  @Test
  void checkCustomSchemaNameIsDefined_Postgres() throws Exception {
    assumeThat(isPostgres).isTrue();
    try (Connection connection = dataSource.getConnection()) {

      try (PreparedStatement preparedStatement =
          connection.prepareStatement(
              "SELECT tablename FROM pg_catalog.pg_tables where schemaname = ?")) {
        preparedStatement.setString(1, schemaName);
        ResultSet rs = preparedStatement.executeQuery();

        boolean tablefound = false;
        while (rs.next() && !tablefound) {
          String tableName = rs.getString("tablename");
          tablefound = tableName.equals("workbasket");
        }
        assertThat(tablefound).as("Table workbasket should be there ...").isTrue();
      }
    }
  }

  @Disabled("sampledatagenerator cannot handle a different schema")
  @Test
  void checkCustomSchemaNameIsDefined_OtherDb() throws Exception {
    assumeThat(isPostgres).isTrue();
    try (Connection connection = dataSource.getConnection()) {

      try (PreparedStatement preparedStatement =
          connection.prepareStatement(
              "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?")) {
        preparedStatement.setString(1, schemaName);
        ResultSet rs = preparedStatement.executeQuery();
        boolean tablefound = false;
        while (rs.next() && !tablefound) {
          String tableName = rs.getString("TABLE_NAME");
          tablefound = tableName.equals("WORKBASKET");
        }
        assertThat(tablefound).as("Table WORKBASKET should be there ...").isTrue();
      }
    }
  }
}
