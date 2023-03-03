/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-provider
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
package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.TaskImpl;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCreationAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @Test
  @WithAccessId(user = "admin")
  void should_CreateCreatedHistoryEvent_When_TaskIsCreated() throws Exception {

    TaskImpl newTask = (TaskImpl) taskService.newTask("WBI:100000000000000000000000000000000006");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectRef("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    taskService.createTask(newTask);

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(newTask.getId()));

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.CREATED.getName());
  }
}
