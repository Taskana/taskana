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

import java.util.UUID;

import pro.taskana.testapi.builder.ClassificationBuilder;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.WorkbasketBuilder;
import pro.taskana.workbasket.api.WorkbasketType;

public class DefaultTestEntities {

  public static ClassificationBuilder defaultTestClassification() {
    return ClassificationBuilder.newClassification()
        .key(UUID.randomUUID().toString().replace("-", ""))
        .domain("DOMAIN_A");
  }

  public static WorkbasketBuilder defaultTestWorkbasket() {
    return WorkbasketBuilder.newWorkbasket()
        .key(UUID.randomUUID().toString())
        .domain("DOMAIN_A")
        .name("Megabasket")
        .type(WorkbasketType.GROUP)
        .orgLevel1("company");
  }

  public static ObjectReferenceBuilder defaultTestObjectReference() {
    return ObjectReferenceBuilder.newObjectReference()
        .company("Company1")
        .system("System1")
        .systemInstance("Instance1")
        .type("Type1")
        .value("Value1");
  }
}
