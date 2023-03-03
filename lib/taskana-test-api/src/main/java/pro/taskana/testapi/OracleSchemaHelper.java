/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.sql.DataSource;

import pro.taskana.common.api.exceptions.SystemException;

public final class OracleSchemaHelper {

  private static final String DEFAULT_PASSWORD = "testPassword";

  private OracleSchemaHelper() {
    // hide implicitpublic one
  }

  public static void initOracleSchema(DataSource dataSource, String schemaName)
      throws SystemException {
    try (Connection connection = dataSource.getConnection();
        // connect as SYSTEM user to create schemas
        Connection conn =
            DriverManager.getConnection(
                connection.getMetaData().getURL(), "SYSTEM", DEFAULT_PASSWORD);
        Statement stmt = conn.createStatement()) {
      stmt.execute("GRANT ALL PRIVILEGES TO TEST_USER");

      stmt.addBatch(
          String.format(
              "create tablespace %s datafile '%s.dat' size 5M autoextend "
                  + "on NEXT 5M MAXSIZE UNLIMITED",
              schemaName, schemaName));
      stmt.addBatch(
          String.format(
              "create temporary tablespace %s_TMP tempfile '%s_tmp.dat' size 5M autoextend "
                  + "on NEXT 5M MAXSIZE UNLIMITED",
              schemaName, schemaName));
      stmt.addBatch(
          String.format(
              "create user %s identified by %s default tablespace %s "
                  + "temporary tablespace %s_TMP",
              schemaName, DEFAULT_PASSWORD, schemaName, schemaName));
      stmt.addBatch(String.format("ALTER USER %s quota unlimited on %s", schemaName, schemaName));
      stmt.addBatch(String.format("GRANT UNLIMITED TABLESPACE TO %s", schemaName));
      stmt.executeBatch();
    } catch (Exception e) {
      throw new SystemException("Failed to setup ORACLE Schema", e);
    }
  }
}
