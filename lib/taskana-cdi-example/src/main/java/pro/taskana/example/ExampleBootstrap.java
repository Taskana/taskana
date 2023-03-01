/*-
 * #%L
 * pro.taskana:taskana-cdi-example
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
package pro.taskana.example;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Example Bootstrap Application. */
@ApplicationScoped
public class ExampleBootstrap {

  @EJB private TaskanaEjb taskanaEjb;

  @PostConstruct
  public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
      throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
          InvalidOwnerException, TaskAlreadyExistException, InvalidArgumentException,
          AttachmentPersistenceException, ObjectReferencePersistenceException,
          MismatchedWorkbasketPermissionException, InvalidTaskStateException {
    System.out.println("---------------------------> Start App");
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    Task task = taskanaEjb.getTaskService().newTask(null);
    task.setPrimaryObjRef(objRef);
    task = taskanaEjb.getTaskService().createTask(task);
    System.out.println("---------------------------> Task started: " + task.getId());
    taskanaEjb.getTaskService().claim(task.getId());
    System.out.println(
        "---------------------------> Task claimed: "
            + taskanaEjb.getTaskService().getTask(task.getId()).getOwner());
    taskanaEjb.getTaskService().completeTask(task.getId());
    System.out.println("---------------------------> Task completed");
  }
}
