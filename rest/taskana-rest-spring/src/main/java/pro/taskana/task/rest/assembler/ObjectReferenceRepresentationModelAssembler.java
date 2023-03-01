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
package pro.taskana.task.rest.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;

@Controller
public class ObjectReferenceRepresentationModelAssembler
    implements RepresentationModelAssembler<ObjectReference, ObjectReferenceRepresentationModel> {

  @Override
  @NonNull
  public ObjectReferenceRepresentationModel toModel(@NonNull ObjectReference entity) {
    ObjectReferenceRepresentationModel repModel = new ObjectReferenceRepresentationModel();
    repModel.setId(entity.getId());
    repModel.setTaskId(entity.getTaskId());
    repModel.setCompany(entity.getCompany());
    repModel.setSystem(entity.getSystem());
    repModel.setSystemInstance(entity.getSystemInstance());
    repModel.setType(entity.getType());
    repModel.setValue(entity.getValue());
    return repModel;
  }

  public ObjectReference toEntity(ObjectReferenceRepresentationModel repModel) {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setId(repModel.getId());
    objectReference.setTaskId(repModel.getTaskId());
    objectReference.setCompany(repModel.getCompany());
    objectReference.setSystem(repModel.getSystem());
    objectReference.setSystemInstance(repModel.getSystemInstance());
    objectReference.setType(repModel.getType());
    objectReference.setValue(repModel.getValue());
    return objectReference;
  }
}
