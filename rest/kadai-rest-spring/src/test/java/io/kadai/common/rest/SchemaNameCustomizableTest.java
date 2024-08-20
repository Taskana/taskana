package io.kadai.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import io.kadai.common.internal.configuration.DB;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.sampledata.SampleDataGenerator;
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

/** Test that the schema name can be customized. */
@KadaiSpringBootTest
class SchemaNameCustomizableTest {

  String schemaName = "CUSTOMSCHEMANAME";
  boolean isPostgres = false;

  @Autowired private DataSource dataSource;

  void resetDb() throws SQLException {
    SampleDataGenerator sampleDataGenerator;
    try (Connection connection = dataSource.getConnection()) {
      DB db = DB.getDB(connection);

      if (DB.POSTGRES == db) {
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
