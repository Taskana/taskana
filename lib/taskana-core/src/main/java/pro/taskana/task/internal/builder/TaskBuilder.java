package pro.taskana.task.internal.builder;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import javax.security.auth.Subject;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.UserPrincipal;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

public class TaskBuilder {

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

  public TaskBuilder attachments(Attachment... attachments) {
    testTask.setAttachments(Arrays.asList(attachments));
    return this;
  }

  public TaskBuilder objectReferences(ObjectReference... objectReferences) {
    testTask.setSecondaryObjectReferences(Arrays.asList(objectReferences));
    return this;
  }

  public TaskBuilder customAttribute(TaskCustomField customField, String value) {
    testTask.setCustomAttribute(customField, value);
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

  public Task buildAndStore(TaskService taskService)
      throws TaskAlreadyExistException, InvalidArgumentException, WorkbasketNotFoundException,
          ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException,
          ObjectReferencePersistenceException, TaskNotFoundException {
    try {
      Task task = taskService.createTask(testTask);
      return taskService.getTask(task.getId());
    } finally {
      testTask.setId(null);
      testTask.setExternalId(null);
    }
  }

  public Task buildAndStore(TaskService taskService, String userId)
      throws PrivilegedActionException {
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(userId));
    PrivilegedExceptionAction<Task> performBuildAndStore = () -> buildAndStore(taskService);

    return Subject.doAs(subject, performBuildAndStore);
  }

  public TaskSummary buildAndStoreAsSummary(TaskService taskService)
      throws TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          AttachmentPersistenceException, ObjectReferencePersistenceException {
    return buildAndStore(taskService).asSummary();
  }

  public TaskSummary buildAndStoreAsSummary(TaskService taskService, String userId)
      throws PrivilegedActionException {
    return buildAndStore(taskService, userId).asSummary();
  }
}
