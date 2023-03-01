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
package pro.taskana.routing.dmn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.AbstractAccTest;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class DmnTaskRouterAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();

  @WithAccessId(user = "taskadmin")
  @Test
  void should_RouteTaskToCorrectWorkbasket_When_DmnTaskRouterFindsRule() throws Exception {

    Task taskToRoute = taskService.newTask();
    taskToRoute.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("company", null, null, "MyType1", "00000001");
    taskToRoute.setPrimaryObjRef(objectReference);

    Task routedTask = taskService.createTask(taskToRoute);
    assertThat(routedTask.getWorkbasketKey()).isEqualTo("GPK_KSC");
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_DmnTaskRouterFindsNoRule() {

    Task taskToRoute = taskService.newTask();
    taskToRoute.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("company", null, null, "MyTeö", "000002");
    taskToRoute.setPrimaryObjRef(objectReference);

    ThrowingCallable call = () -> taskService.createTask(taskToRoute);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .extracting(Throwable::getMessage)
        .isEqualTo("Cannot create a Task outside a Workbasket");
  }
}
