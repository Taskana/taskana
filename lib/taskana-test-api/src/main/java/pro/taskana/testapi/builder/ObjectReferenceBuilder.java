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
package pro.taskana.testapi.builder;

import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

public class ObjectReferenceBuilder {

  private final ObjectReferenceImpl objectReference = new ObjectReferenceImpl();

  private ObjectReferenceBuilder() {}

  public static ObjectReferenceBuilder newObjectReference() {
    return new ObjectReferenceBuilder();
  }

  public ObjectReferenceBuilder company(String company) {
    objectReference.setCompany(company);
    return this;
  }

  public ObjectReferenceBuilder system(String system) {
    objectReference.setSystem(system);
    return this;
  }

  public ObjectReferenceBuilder systemInstance(String systemInstance) {
    objectReference.setSystemInstance(systemInstance);
    return this;
  }

  public ObjectReferenceBuilder type(String type) {
    objectReference.setType(type);
    return this;
  }

  public ObjectReferenceBuilder value(String value) {
    objectReference.setValue(value);
    return this;
  }

  public ObjectReference build() {
    return objectReference.copy();
  }
}
