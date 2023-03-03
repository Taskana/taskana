/*-
 * #%L
 * pro.taskana:taskana-spi-routing-dmn-router
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
package pro.taskana;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

public abstract class AbstractAccTest {

  protected static TaskanaConfiguration taskanaConfiguration;
  protected static TaskanaEngine taskanaEngine;

  @BeforeAll
  protected static void setupTest() throws Exception {
    resetDb(false);
  }

  protected static void resetDb(boolean dropTables) throws Exception {

    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    dataSource = DataSourceGenerator.getDataSource();
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(dataSource, false, schemaName)
            .initTaskanaProperties()
            .germanPublicHolidaysEnabled(true)
            .build();
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(dataSource, taskanaConfiguration.getSchemaName());
    dbSchemaCreator.run();
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
    taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(
            taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
  }

  protected ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setCompany(company);
    objectReference.setSystem(system);
    objectReference.setSystemInstance(systemInstance);
    objectReference.setType(type);
    objectReference.setValue(value);
    return objectReference;
  }
}
