package pro.taskana.task.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;

public class AttachmentHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentHandler.class);
  private static final String ID_PREFIX_ATTACHMENT = "TAI";
  private final AttachmentMapper attachmentMapper;
  private final ClassificationService classificationService;

  AttachmentHandler(
      AttachmentMapper attachmentMapper, ClassificationService classificationService) {
    this.attachmentMapper = attachmentMapper;
    this.classificationService = classificationService;
  }

  public List<Attachment> augmentAttachmentsByClassification(
      List<AttachmentImpl> attachmentImpls,
      BulkOperationResults<String, Exception> bulkLog) {
    LOGGER.debug("entry to augmentAttachmentsByClassification()");
    List<Attachment> result = new ArrayList<>();
    if (attachmentImpls == null || attachmentImpls.isEmpty()) {
      return result;
    }
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .idIn(
                attachmentImpls.stream()
                    .map(t -> t.getClassificationSummary().getId())
                    .distinct()
                    .toArray(String[]::new))
            .list();
    for (AttachmentImpl att : attachmentImpls) {
      ClassificationSummary classificationSummary =
          classifications.stream()
              .filter(cl -> cl.getId().equals(att.getClassificationSummary().getId()))
              .findFirst()
              .orElse(null);
      if (classificationSummary == null) {
        String id = att.getClassificationSummary().getId();
        bulkLog.addError(
            att.getClassificationSummary().getId(),
            new ClassificationNotFoundException(
                id,
                String.format(
                    "When processing task updates due to change "
                        + "of classification, the classification with id %s was not found",
                    id)));
      } else {
        att.setClassificationSummary(classificationSummary);
        result.add(att);
      }
    }

    LOGGER.debug("exit from augmentAttachmentsByClassification()");
    return result;
  }

  void insertAndDeleteAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws AttachmentPersistenceException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to insertAndDeleteAttachmentsOnTaskUpdate(oldTaskImpl = {}, newTaskImpl = {})",
          oldTaskImpl,
          newTaskImpl);
    }
    List<Attachment> newAttachments =
        newTaskImpl.getAttachments().stream().filter(Objects::nonNull).collect(Collectors.toList());
    newTaskImpl.setAttachments(newAttachments);

    deleteRemovedAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
    insertNewAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
    updateModifiedAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "exit from insertAndDeleteAttachmentsOnTaskUpdate(oldTaskImpl = {}, newTaskImpl = {})",
          oldTaskImpl,
          newTaskImpl);
    }
  }

  void updateModifiedAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
    List<Attachment> newAttachments = newTaskImpl.getAttachments();
    List<Attachment> oldAttachments = oldTaskImpl.getAttachments();
    if (newAttachments != null
        && !newAttachments.isEmpty()
        && oldAttachments != null
        && !oldAttachments.isEmpty()) {
      final Map<String, Attachment> oldAttachmentMap =
          oldAttachments.stream()
              .collect(Collectors.toMap(AttachmentSummary::getId, Function.identity()));
      newAttachments.forEach(
          a -> {
            if (oldAttachmentMap.containsKey(a.getId())
                && !a.equals(oldAttachmentMap.get(a.getId()))) {
              attachmentMapper.update((AttachmentImpl) a);
            }
          });
    }
  }

  void insertNewAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws AttachmentPersistenceException {
    List<String> oldAttachmentIds =
        oldTaskImpl.getAttachments().stream()
            .map(AttachmentSummary::getId)
            .collect(Collectors.toList());
    List<AttachmentPersistenceException> exceptions = new ArrayList<>();
    newTaskImpl
        .getAttachments()
        .forEach(
            a -> {
              if (!oldAttachmentIds.contains(a.getId())) {
                try {
                  insertNewAttachmentOnTaskUpdate(newTaskImpl, a);
                } catch (AttachmentPersistenceException excpt) {
                  exceptions.add(excpt);
                  LOGGER.warn("attempted to insert attachment {} and caught exception", a, excpt);
                }
              }
            });
    if (!exceptions.isEmpty()) {
      throw exceptions.get(0);
    }
  }

  void insertNewAttachmentsOnTaskCreation(TaskImpl task, Instant now)
      throws InvalidArgumentException {
    List<Attachment> attachments = task.getAttachments();
    if (attachments != null) {
      for (Attachment attachment : attachments) {
        AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
        attachmentImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
        attachmentImpl.setTaskId(task.getId());
        attachmentImpl.setCreated(now);
        attachmentImpl.setModified(now);
        ObjectReference objRef = attachmentImpl.getObjectReference();
        validateObjectReference(objRef, "ObjectReference", "Attachment");
        attachmentMapper.insert(attachmentImpl);
      }
    }
  }

  void deleteRemovedAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteRemovedAttachmentsOnTaskUpdate(oldTaskImpl = {}, newTaskImpl = {})",
          oldTaskImpl,
          newTaskImpl);
    }

    final List<Attachment> newAttachments = newTaskImpl.getAttachments();
    List<String> newAttachmentIds = new ArrayList<>();
    if (newAttachments != null && !newAttachments.isEmpty()) {
      newAttachmentIds =
          newAttachments.stream().map(Attachment::getId).collect(Collectors.toList());
    }
    List<Attachment> oldAttachments = oldTaskImpl.getAttachments();
    if (oldAttachments != null && !oldAttachments.isEmpty()) {
      final List<String> newAttIds = newAttachmentIds;
      oldAttachments.forEach(
          a -> {
            if (!newAttIds.contains(a.getId())) {
              attachmentMapper.deleteAttachment(a.getId());
              LOGGER.debug(
                  "TaskService.updateTask() for TaskId={} DELETED an Attachment={}.",
                  newTaskImpl.getId(),
                  a);
            }
          });
    }
    LOGGER.debug("exit from deleteRemovedAttachmentsOnTaskUpdate()");
  }

  void insertNewAttachmentOnTaskUpdate(TaskImpl newTaskImpl, Attachment attachment)
      throws AttachmentPersistenceException {
    LOGGER.debug("entry to insertNewAttachmentOnTaskUpdate()");
    AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
    initAttachment(attachmentImpl, newTaskImpl);

    try {
      attachmentMapper.insert(attachmentImpl);
      LOGGER.debug(
          "TaskService.updateTask() for TaskId={} INSERTED an Attachment={}.",
          newTaskImpl.getId(),
          attachmentImpl);
    } catch (PersistenceException e) {
      throw new AttachmentPersistenceException(
          "Cannot insert the Attachement "
              + attachmentImpl.getId()
              + " for Task "
              + newTaskImpl.getId()
              + " because it already exists.",
          e.getCause());
    }
    LOGGER.debug("exit from insertNewAttachmentOnTaskUpdate(), returning");
  }

  void initAttachment(AttachmentImpl attachment, Task newTask) {
    LOGGER.debug("entry to initAttachment()");
    if (attachment.getId() == null) {
      attachment.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
    }
    if (attachment.getCreated() == null) {
      attachment.setCreated(Instant.now());
    }
    if (attachment.getModified() == null) {
      attachment.setModified(attachment.getCreated());
    }
    if (attachment.getTaskId() == null) {
      attachment.setTaskId(newTask.getId());
    }
    LOGGER.debug("exit from initAttachment()");
  }

  void validateObjectReference(ObjectReference objRef, String objRefType, String objName)
      throws InvalidArgumentException {
    LOGGER.debug("entry to validateObjectReference()");
    // check that all values in the ObjectReference are set correctly
    if (objRef == null) {
      throw new InvalidArgumentException(objRefType + " of " + objName + " must not be null");
    } else if (objRef.getCompany() == null || objRef.getCompany().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Company of %s of %s must not be empty", objRefType, objName));
    } else if (objRef.getSystem() == null || objRef.getSystem().length() == 0) {
      throw new InvalidArgumentException(
          String.format("System of %s of %s must not be empty", objRefType, objName));
    } else if (objRef.getSystemInstance() == null || objRef.getSystemInstance().length() == 0) {
      throw new InvalidArgumentException(
          String.format("SystemInstance of %s of %s must not be empty", objRefType, objName));
    } else if (objRef.getType() == null || objRef.getType().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Type of %s of %s must not be empty", objRefType, objName));
    } else if (objRef.getValue() == null || objRef.getValue().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Value of %s of %s must not be empty", objRefType, objName));
    }
    LOGGER.debug("exit from validateObjectReference()");
  }
}
