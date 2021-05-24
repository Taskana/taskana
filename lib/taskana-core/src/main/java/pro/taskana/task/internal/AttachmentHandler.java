package pro.taskana.task.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
  private final AttachmentMapper attachmentMapper;
  private final ClassificationService classificationService;

  AttachmentHandler(
      AttachmentMapper attachmentMapper, ClassificationService classificationService) {
    this.attachmentMapper = attachmentMapper;
    this.classificationService = classificationService;
  }

  List<Attachment> augmentAttachmentsByClassification(
      List<AttachmentImpl> attachmentImpls, BulkOperationResults<String, Exception> bulkLog) {
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

    return result;
  }

  void insertAndDeleteAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws AttachmentPersistenceException, InvalidArgumentException,
          ClassificationNotFoundException {
    List<Attachment> newAttachments =
        newTaskImpl.getAttachments().stream().filter(Objects::nonNull).collect(Collectors.toList());
    newTaskImpl.setAttachments(newAttachments);

    for (Attachment attachment : newAttachments) {
      verifyAttachment((AttachmentImpl) attachment, newTaskImpl.getDomain());
      initAttachment((AttachmentImpl) attachment, newTaskImpl);
    }

    deleteRemovedAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
    insertNewAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
    updateModifiedAttachmentsOnTaskUpdate(newTaskImpl, oldTaskImpl);
  }

  void insertNewAttachmentsOnTaskCreation(TaskImpl task)
      throws InvalidArgumentException, AttachmentPersistenceException,
          ClassificationNotFoundException {
    List<Attachment> attachments = task.getAttachments();

    if (attachments != null) {
      for (Attachment attachment : attachments) {
        AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
        verifyAttachment(attachmentImpl, task.getDomain());
        initAttachment(attachmentImpl, task);

        try {
          attachmentMapper.insert(attachmentImpl);
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "TaskService.createTask() for TaskId={} INSERTED an Attachment={}.",
                task.getId(),
                attachmentImpl);
          }
        } catch (PersistenceException e) {
          throw new AttachmentPersistenceException(
              String.format(
                  "Cannot insert the Attachement %s for Task %s  because it already exists.",
                  attachmentImpl.getId(), task.getId()),
              e.getCause());
        }
      }
    }
  }

  private void insertNewAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws AttachmentPersistenceException {
    Set<String> oldAttachmentIds =
        oldTaskImpl.getAttachments().stream()
            .map(AttachmentSummary::getId)
            .collect(Collectors.toSet());

    List<Attachment> newAttachments =
        newTaskImpl.getAttachments().stream()
            .filter(a -> !oldAttachmentIds.contains(a.getId()))
            .collect(Collectors.toList());

    for (Attachment attachment : newAttachments) {
      insertNewAttachmentOnTaskUpdate(newTaskImpl, attachment);
    }
  }

  private void updateModifiedAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
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

  private void deleteRemovedAttachmentsOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {

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
              attachmentMapper.delete(a.getId());
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                    "TaskService.updateTask() for TaskId={} DELETED an Attachment={}.",
                    newTaskImpl.getId(),
                    a);
              }
            }
          });
    }
  }

  private void insertNewAttachmentOnTaskUpdate(TaskImpl newTaskImpl, Attachment attachment)
      throws AttachmentPersistenceException {
    AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;

    try {
      attachmentMapper.insert(attachmentImpl);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "TaskService.updateTask() for TaskId={} INSERTED an Attachment={}.",
            newTaskImpl.getId(),
            attachmentImpl);
      }
    } catch (PersistenceException e) {
      throw new AttachmentPersistenceException(
          String.format(
              "Cannot insert the Attachement %s for Task %s  because it already exists.",
              attachmentImpl.getId(), newTaskImpl.getId()),
          e.getCause());
    }
  }

  private void initAttachment(AttachmentImpl attachment, Task newTask) {
    if (attachment.getId() == null) {
      attachment.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_ATTACHMENT));
    }
    if (attachment.getCreated() == null) {
      attachment.setCreated(newTask.getModified());
    }
    if (attachment.getModified() == null) {
      attachment.setModified(attachment.getCreated());
    }
    if (attachment.getTaskId() == null) {
      attachment.setTaskId(newTask.getId());
    }
  }

  private void verifyAttachment(AttachmentImpl attachment, String domain)
      throws InvalidArgumentException, ClassificationNotFoundException {
    ClassificationSummary classification = attachment.getClassificationSummary();
    if (classification == null) {
      throw new InvalidArgumentException("Classification of Attachment must not be null.");
    }
    if (classification.getKey() == null || classification.getKey().length() == 0) {
      throw new InvalidArgumentException("ClassificationKey of Attachment must not be empty.");
    }

    ObjectReference.validate(attachment.getObjectReference(), "ObjectReference", "Attachment");

    classification =
        classificationService
            .getClassification(attachment.getClassificationSummary().getKey(), domain)
            .asSummary();
    attachment.setClassificationSummary(classification);
  }
}
