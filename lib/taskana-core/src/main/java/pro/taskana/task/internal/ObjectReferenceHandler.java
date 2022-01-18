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

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.task.internal.models.TaskImpl;

/** Handles all operations on secondary {@link ObjectReference}s. */
public class ObjectReferenceHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectReferenceHandler.class);
  private final ObjectReferenceMapper objectReferenceMapper;

  ObjectReferenceHandler(ObjectReferenceMapper objectReferenceMapper) {
    this.objectReferenceMapper = objectReferenceMapper;
  }

  void insertNewSecondaryObjectReferencesOnTaskCreation(TaskImpl task)
      throws ObjectReferencePersistenceException, InvalidArgumentException {
    List<ObjectReference> objectReferences = task.getSecondaryObjectReferences();

    if (objectReferences != null) {
      for (ObjectReference objectReference : objectReferences) {
        ObjectReferenceImpl objectReferenceImpl = (ObjectReferenceImpl) objectReference;
        initObjectReference(objectReferenceImpl, task);
        ObjectReferenceImpl.validate(objectReferenceImpl, "ObjectReference", "Task");
        try {
          objectReferenceMapper.insert(objectReferenceImpl);
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "TaskService.createTask() for TaskId={} INSERTED an object reference={}.",
                task.getId(),
                objectReference);
          }
        } catch (PersistenceException e) {
          throw new ObjectReferencePersistenceException(objectReference.getId(), task.getId(), e);
        }
      }
    }
  }

  void insertAndDeleteObjectReferencesOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws ObjectReferencePersistenceException, InvalidArgumentException {
    List<ObjectReference> newObjectReferences =
        newTaskImpl.getSecondaryObjectReferences().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    newTaskImpl.setSecondaryObjectReferences(newObjectReferences);

    for (ObjectReference objectReference : newObjectReferences) {
      ObjectReferenceImpl.validate(objectReference, "Object Reference", "Task");
      initObjectReference((ObjectReferenceImpl) objectReference, newTaskImpl);
    }

    deleteRemovedObjectReferencesOnTaskUpdate(newTaskImpl, oldTaskImpl);
    insertNewObjectReferencesOnTaskUpdate(newTaskImpl, oldTaskImpl);
    updateModifiedObjectReferencesOnTaskUpdate(newTaskImpl, oldTaskImpl);
  }

  private void insertNewObjectReferencesOnTaskUpdate(TaskImpl newTaskImpl, TaskImpl oldTaskImpl)
      throws ObjectReferencePersistenceException {
    Set<String> oldObjectReferencesIds =
        oldTaskImpl.getSecondaryObjectReferences().stream()
            .map(ObjectReference::getId)
            .collect(Collectors.toSet());

    List<ObjectReference> newObjectReferences =
        newTaskImpl.getSecondaryObjectReferences().stream()
            .filter(o -> !oldObjectReferencesIds.contains(o.getId()))
            .collect(Collectors.toList());

    for (ObjectReference objectReference : newObjectReferences) {
      insertNewObjectReferenceOnTaskUpdate(newTaskImpl, objectReference);
    }
  }

  private void updateModifiedObjectReferencesOnTaskUpdate(
      TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {
    List<ObjectReference> newObjectReferences = newTaskImpl.getSecondaryObjectReferences();
    List<ObjectReference> oldObjectReferences = oldTaskImpl.getSecondaryObjectReferences();
    if (newObjectReferences != null
        && !newObjectReferences.isEmpty()
        && oldObjectReferences != null
        && !oldObjectReferences.isEmpty()) {
      final Map<String, ObjectReference> oldObjectReferencesMap =
          oldObjectReferences.stream()
              .collect(Collectors.toMap(ObjectReference::getId, Function.identity()));
      newObjectReferences.forEach(
          o -> {
            if (oldObjectReferencesMap.containsKey(o.getId())
                && !o.equals(oldObjectReferencesMap.get(o.getId()))) {
              objectReferenceMapper.update((ObjectReferenceImpl) o);
            }
          });
    }
  }

  private void deleteRemovedObjectReferencesOnTaskUpdate(
      TaskImpl newTaskImpl, TaskImpl oldTaskImpl) {

    final List<ObjectReference> newObjectReferences = newTaskImpl.getSecondaryObjectReferences();
    List<String> newObjectReferencesIds = new ArrayList<>();
    if (newObjectReferences != null && !newObjectReferences.isEmpty()) {
      newObjectReferencesIds =
          newObjectReferences.stream().map(ObjectReference::getId).collect(Collectors.toList());
    }
    List<ObjectReference> oldObjectReferences = oldTaskImpl.getSecondaryObjectReferences();
    if (oldObjectReferences != null && !oldObjectReferences.isEmpty()) {
      final List<String> newObjRefIds = newObjectReferencesIds;
      oldObjectReferences.forEach(
          o -> {
            if (!newObjRefIds.contains(o.getId())) {
              objectReferenceMapper.delete(o.getId());
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                    "TaskService.updateTask() for TaskId={} DELETED an ObjectReference={}.",
                    newTaskImpl.getId(),
                    o);
              }
            }
          });
    }
  }

  private void insertNewObjectReferenceOnTaskUpdate(
      TaskImpl newTaskImpl, ObjectReference objectReference)
      throws ObjectReferencePersistenceException {
    ObjectReferenceImpl objectReferenceImpl = (ObjectReferenceImpl) objectReference;
    try {
      objectReferenceMapper.insert(objectReferenceImpl);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "TaskService.updateTask() for TaskId={} INSERTED an ObjectReference={}.",
            newTaskImpl.getId(),
            objectReferenceImpl);
      }
    } catch (PersistenceException e) {
      throw new ObjectReferencePersistenceException(
          objectReferenceImpl.getId(), newTaskImpl.getId(), e);
    }
  }

  private void initObjectReference(ObjectReferenceImpl objectReference, Task newTask) {
    if (objectReference.getTaskId() == null) {
      objectReference.setTaskId(newTask.getId());
    }
    if (objectReference.getId() == null) {
      objectReference.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_OBJECT_REFERENCE));
    }
  }
}
