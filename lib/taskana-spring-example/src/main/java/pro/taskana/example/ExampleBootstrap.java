/*-
 * #%L
 * pro.taskana:taskana-spring-example
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

/** TODO. */
@Component
@Transactional
public class ExampleBootstrap {

  @Autowired private TaskService taskService;

  @Autowired private TaskanaEngine taskanaEngine;

  @PostConstruct
  public void test() throws Exception {
    System.out.println("---------------------------> Start App");

    Workbasket wb = taskanaEngine.getWorkbasketService().newWorkbasket("workbasket", "DOMAIN_A");
    wb.setName("workbasket");
    wb.setType(WorkbasketType.GROUP);
    taskanaEngine.getWorkbasketService().createWorkbasket(wb);
    Classification classification =
        taskanaEngine.getClassificationService().newClassification("TEST", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    taskanaEngine.getClassificationService().createClassification(classification);

    Task task = taskanaEngine.getTaskService().newTask(wb.getId());
    task.setName("Spring example task");
    task.setClassificationKey(classification.getKey());
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);
    task = taskService.createTask(task);
    System.out.println("---------------------------> Task started: " + task.getId());
    taskService.claim(task.getId());
    System.out.println(
        "---------------------------> Task claimed: "
            + taskService.getTask(task.getId()).getOwner());
    taskService.forceCompleteTask(task.getId());
    System.out.println("---------------------------> Task completed");
  }
}
