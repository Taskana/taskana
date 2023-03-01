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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** Test for {@link TaskCommentRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class TaskCommentRepresentationModelAssemblerTest {

  private final TaskCommentRepresentationModelAssembler assembler;
  private final TaskService taskService;

  @Autowired
  TaskCommentRepresentationModelAssemblerTest(
      TaskCommentRepresentationModelAssembler assembler, TaskService taskService) {
    this.assembler = assembler;
    this.taskService = taskService;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {

    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment("TKI:000000000000000000000000000000000000");

    taskComment.setId("taskCommentId");
    taskComment.setCreator("user-1-1");
    taskComment.setCreatorFullName("fullName");
    taskComment.setTextField("this is a task comment");
    taskComment.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    taskComment.setModified(Instant.parse("2011-11-11T11:00:00Z"));

    TaskCommentRepresentationModel repModel = assembler.toModel(taskComment);

    testEquality(taskComment, repModel);
    testLinks(repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {

    TaskCommentRepresentationModel repModel = new TaskCommentRepresentationModel();
    repModel.setTaskId("TKI:000000000000000000000000000000000000");
    repModel.setTaskCommentId("TCI:000000000000000000000000000000000000");
    repModel.setCreator("user-1-1");
    repModel.setCreatorFullName("fullName");
    repModel.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    repModel.setModified(Instant.parse("2011-11-11T11:00:00Z"));
    repModel.setTextField("textField");

    TaskComment taskComment = assembler.toEntityModel(repModel);

    testEquality(taskComment, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment("TKI:000000000000000000000000000000000000");
    taskComment.setId("taskCommentId");
    taskComment.setCreator("user-1-1");
    taskComment.setCreatorFullName("fullName");
    taskComment.setTextField("this is a task comment");
    taskComment.setCreated(Instant.parse("2010-01-01T12:00:00Z"));
    taskComment.setModified(Instant.parse("2011-11-11T11:00:00Z"));

    TaskCommentRepresentationModel repModel = assembler.toModel(taskComment);
    TaskComment taskComment2 = assembler.toEntityModel(repModel);

    assertThat(taskComment)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(taskComment2)
        .isEqualTo(taskComment2);
  }

  private void testEquality(TaskComment taskComment, TaskCommentRepresentationModel repModel) {
    assertThat(taskComment).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();
    assertThat(taskComment.getId()).isEqualTo(repModel.getTaskCommentId());
    assertThat(taskComment.getTaskId()).isEqualTo(repModel.getTaskId());
    assertThat(taskComment.getTextField()).isEqualTo(repModel.getTextField());
    assertThat(taskComment.getCreator()).isEqualTo(repModel.getCreator());
    assertThat(taskComment.getCreatorFullName()).isEqualTo(repModel.getCreatorFullName());
    assertThat(taskComment.getCreated()).isEqualTo(repModel.getCreated());
    assertThat(taskComment.getModified()).isEqualTo(repModel.getModified());
  }

  private void testLinks(TaskCommentRepresentationModel repModel) {}
}
