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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import pro.taskana.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

@ControllerAdvice
public class InitBinderControllerAdvice {

  private final ObjectMapper objectMapper;

  @Autowired
  public InitBinderControllerAdvice(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @InitBinder
  @SuppressWarnings("ConstantConditions")
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(
        PriorityColumnHeaderRepresentationModel.class,
        new JsonPropertyEditor(objectMapper, PriorityColumnHeaderRepresentationModel.class));
    binder.registerCustomEditor(
        ObjectReference.class, new JsonPropertyEditor(objectMapper, ObjectReferenceImpl.class));

    // @see https://stackoverflow.com/questions/75133732/spring-boot-rest-controller-array-handling
    binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
  }
}
