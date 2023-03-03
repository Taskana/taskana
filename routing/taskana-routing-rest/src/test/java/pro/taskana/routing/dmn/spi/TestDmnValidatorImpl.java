/*-
 * #%L
 * pro.taskana:taskana-routing-rest
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
package pro.taskana.routing.dmn.spi;

import org.camunda.bpm.model.dmn.DmnModelInstance;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.routing.dmn.spi.api.DmnValidator;

public class TestDmnValidatorImpl implements DmnValidator {

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {}

  @Override
  public void validate(DmnModelInstance dmnModelInstance) {
    // custom validation logic
  }
}
