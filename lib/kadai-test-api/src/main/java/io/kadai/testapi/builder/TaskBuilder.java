package io.kadai.testapi.builder;

import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.task.api.CallbackState;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskCustomIntField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.AttachmentPersistenceException;
import io.kadai.task.api.exceptions.ObjectReferencePersistenceException;
import io.kadai.task.api.exceptions.TaskAlreadyExistException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.testapi.builder.EntityBuilder.SummaryEntityBuilder;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

public class TaskBuilder implements SummaryEntityBuilder<TaskSummary, Task, TaskService> {

  private final TaskTestImpl testTask = new TaskTestImpl();

  public static TaskBuilder newTask() {
    return new TaskBuilder();
  }

  public TaskBuilder externalId(String externalId) {
    testTask.setExternalId(externalId);
    return this;
  }

  public TaskBuilder created(Instant created) {
    testTask.setCreatedIgnoreFreeze(created);
    if (created != null) {
      testTask.freezeCreated();
    } else {
      testTask.unfreezeCreated();
    }
    return this;
  }

  public TaskBuilder claimed(Instant claimed) {
    testTask.setClaimed(claimed);
    return this;
  }

  public TaskBuilder completed(Instant completed) {
    testTask.setCompleted(completed);
    return this;
  }

  public TaskBuilder modified(Instant modified) {
    testTask.setModifiedIgnoreFreeze(modified);
    if (modified != null) {
      testTask.freezeModified();
    } else {
      testTask.unfreezeModified();
    }
    return this;
  }

  public TaskBuilder received(Instant received) {
    testTask.setReceived(received);
    return this;
  }

  public TaskBuilder planned(Instant planned) {
    testTask.setPlanned(planned);
    return this;
  }

  public TaskBuilder due(Instant due) {
    testTask.setDue(due);
    return this;
  }

  public TaskBuilder name(String name) {
    testTask.setName(name);
    return this;
  }

  public TaskBuilder note(String note) {
    testTask.setNote(note);
    return this;
  }

  public TaskBuilder description(String description) {
    testTask.setDescription(description);
    return this;
  }

  public TaskBuilder state(TaskState state) {
    testTask.setStateIgnoreFreeze(state);
    if (state != null) {
      testTask.freezeState();
    } else {
      testTask.unfreezeState();
    }
    return this;
  }

  public TaskBuilder classificationSummary(ClassificationSummary classificationSummary) {
    testTask.setClassificationSummary(classificationSummary);
    return this;
  }

  public TaskBuilder workbasketSummary(WorkbasketSummary workbasketSummary) {
    testTask.setWorkbasketSummary(workbasketSummary);
    return this;
  }

  public TaskBuilder businessProcessId(String businessProcessId) {
    testTask.setBusinessProcessId(businessProcessId);
    return this;
  }

  public TaskBuilder parentBusinessProcessId(String parentBusinessProcessId) {
    testTask.setParentBusinessProcessId(parentBusinessProcessId);
    return this;
  }

  public TaskBuilder owner(String owner) {
    testTask.setOwner(owner);
    return this;
  }

  public TaskBuilder ownerLongName(String ownerLongName) {
    testTask.setOwnerLongName(ownerLongName);
    return this;
  }

  public TaskBuilder primaryObjRef(ObjectReference primaryObjRef) {
    testTask.setPrimaryObjRef(primaryObjRef);
    return this;
  }

  public TaskBuilder read(Boolean read) {
    if (read != null) {
      testTask.setReadIgnoreFreeze(read);
      if (read) {
        testTask.freezeRead();
      }
    } else {
      testTask.unfreezeRead();
    }
    return this;
  }

  public TaskBuilder transferred(Boolean transferred) {
    if (transferred != null) {
      testTask.setTransferredIgnoreFreeze(transferred);
      if (transferred) {
        testTask.freezeTransferred();
      }
    } else {
      testTask.unfreezeTransferred();
    }
    return this;
  }

  public TaskBuilder numberOfComments(Integer numberOfComments) {
    testTask.setNumberOfComments(numberOfComments);
    return this;
  }

  public TaskBuilder groupByCount(Integer count) {
    testTask.setGroupByCount(count);
    return this;
  }

  public TaskBuilder attachments(Attachment... attachments) {
    testTask.setAttachments(Arrays.asList(attachments));
    return this;
  }

  public TaskBuilder objectReferences(ObjectReference... objectReferences) {
    testTask.setSecondaryObjectReferences(Arrays.asList(objectReferences));
    return this;
  }

  public TaskBuilder customAttribute(TaskCustomField customField, String value) {
    testTask.setCustomField(customField, value);
    return this;
  }

  public TaskBuilder customIntField(TaskCustomIntField customIntField, Integer value) {
    testTask.setCustomIntField(customIntField, value);
    return this;
  }

  public TaskBuilder callbackInfo(Map<String, String> callbackInfo) {
    testTask.setCallbackInfo(callbackInfo);
    return this;
  }

  public TaskBuilder callbackState(CallbackState callbackState) {
    testTask.setCallbackState(callbackState);
    return this;
  }

  public TaskBuilder priority(Integer priority) {
    if (priority != null) {
      testTask.setPriorityIgnoreFreeze(priority);
      testTask.freezePriority();
    } else {
      testTask.unfreezePriority();
    }
    return this;
  }

  public TaskBuilder manualPriority(Integer manualPriority) {
    testTask.setManualPriority(manualPriority);
    return this;
  }

  @Override
  public TaskSummary entityToSummary(Task entity) {
    return entity.asSummary();
  }

  @Override
  public Task buildAndStore(TaskService taskService)
      throws TaskAlreadyExistException,
          InvalidArgumentException,
          WorkbasketNotFoundException,
          ClassificationNotFoundException,
          AttachmentPersistenceException,
          ObjectReferencePersistenceException,
          TaskNotFoundException,
          NotAuthorizedOnWorkbasketException {
    try {
      Task task = taskService.createTask(testTask);
      return taskService.getTask(task.getId());
    } finally {
      testTask.setId(null);
      testTask.setExternalId(null);
    }
  }
}
