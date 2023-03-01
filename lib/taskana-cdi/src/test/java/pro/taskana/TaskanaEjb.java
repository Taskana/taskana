/*-
 * #%L
 * pro.taskana:taskana-cdi
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

import javax.ejb.Stateless;
import javax.inject.Inject;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.WorkbasketService;

@Stateless
public class TaskanaEjb {

  @Inject private TaskService taskService;

  @Inject private ClassificationService classificationService;

  @Inject private WorkbasketService workbasketService;

  public TaskService getTaskService() {
    return taskService;
  }

  public ClassificationService getClassificationService() {
    return classificationService;
  }

  public WorkbasketService getWorkbasketService() {
    return workbasketService;
  }

  public void triggerRollback(String workbasketId, String classificationKey) throws Exception {

    final Task task = taskService.newTask(workbasketId);
    task.setClassificationKey(classificationKey);
    task.setName("triggerRollback");
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);

    taskService.createTask(task);
    System.out.println("---------------->" + task.getId());
    throw new RuntimeException("Expected Test Exception");
  }
}
