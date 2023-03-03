/*-
 * #%L
 * pro.taskana:taskana-core
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
package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.task.api.ObjectReferenceQueryColumnName.COMPANY;
import static pro.taskana.task.api.ObjectReferenceQueryColumnName.SYSTEM;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.models.ObjectReference;

/** Acceptance test for all "get classification" scenarios. */
class QueryObjectReferenceAccTest extends AbstractAccTest {

  @Test
  void testQueryObjectReferenceValuesForColumnName() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();
    List<String> columnValues = taskQuery.createObjectReferenceQuery().listValues(COMPANY, null);
    assertThat(columnValues).hasSize(4);

    columnValues = taskQuery.createObjectReferenceQuery().listValues(SYSTEM, null);
    assertThat(columnValues).hasSize(4);

    columnValues =
        taskQuery.createObjectReferenceQuery().systemIn("System1").listValues(SYSTEM, null);
    assertThat(columnValues).hasSize(1);
  }

  @Test
  void testFindObjectReferenceByCompany() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().companyIn("Company1", "Company2").list();

    assertThat(objectReferenceList).hasSize(2);
  }

  @Test
  void testFindObjectReferenceBySystem() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery
            .createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .systemIn("System2")
            .list();

    assertThat(objectReferenceList).hasSize(1);
  }

  @Test
  void testFindObjectReferenceBySystemInstance() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery
            .createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .systemInstanceIn("Instance1")
            .list();

    assertThat(objectReferenceList).hasSize(1);
  }

  @Test
  void testFindObjectReferenceByType() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().typeIn("Type2", "Type3").list();

    assertThat(objectReferenceList).hasSize(2);
  }

  @Test
  void testFindObjectReferenceByValue() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().valueIn("Value1", "Value3").list();

    assertThat(objectReferenceList).hasSize(2);
  }
}
