package pro.taskana.impl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Attachment;
import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.AttachmentMapper;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * This is the implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static final String ID_PREFIX_ATTACHMENT = "TAI";
    private static final String ID_PREFIX_TASK = "TKI";
    private static final String ID_PREFIX_BUSINESS_PROCESS = "BPI";
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private WorkbasketService workbasketService;
    private ClassificationServiceImpl classificationService;
    private TaskMapper taskMapper;
    private AttachmentMapper attachmentMapper;

    public TaskServiceImpl(TaskanaEngine taskanaEngine, TaskMapper taskMapper,
        ObjectReferenceMapper objectReferenceMapper, AttachmentMapper attachmentMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMapper = taskMapper;
        this.workbasketService = taskanaEngineImpl.getWorkbasketService();
        this.attachmentMapper = attachmentMapper;
        this.classificationService = (ClassificationServiceImpl) taskanaEngineImpl.getClassificationService();
    }

    @Override
    public Task claim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException {
        return claim(taskId, false);
    }

    @Override
    public Task claim(String taskId, boolean forceClaim)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to claim(id = {}, forceClaim = {}, userId = {})", taskId, forceClaim, userId);
        TaskImpl task = null;
        try {
            taskanaEngineImpl.openConnection();
            task = (TaskImpl) getTask(taskId);
            TaskState state = task.getState();
            if (state == TaskState.COMPLETED) {
                LOGGER.warn("Method claim() found that task {} is already completed. Throwing InvalidStateException",
                    taskId);
                throw new InvalidStateException("Task is already completed");
            }
            if (state == TaskState.CLAIMED && !forceClaim) {
                LOGGER.warn(
                    "Method claim() found that task {} is claimed by {} and forceClaim is false. Throwing InvalidOwnerException",
                    taskId, task.getOwner());
                throw new InvalidOwnerException("Task is already claimed by user " + task.getOwner());
            }
            Timestamp now = new Timestamp(System.currentTimeMillis());
            task.setOwner(userId);
            task.setModified(now);
            task.setClaimed(now);
            task.setRead(true);
            task.setState(TaskState.CLAIMED);
            taskMapper.update(task);
            LOGGER.debug("Method claim() claimed task '{}' for user '{}'.", taskId, userId);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from claim()");
        }
        return task;
    }

    @Override
    public Task completeTask(String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException {
        return completeTask(taskId, false);
    }

    @Override
    public Task completeTask(String taskId, boolean isForced)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException {
        LOGGER.debug("entry to completeTask(id = {}, isForced {})", taskId, isForced);
        TaskImpl task = null;
        try {
            taskanaEngineImpl.openConnection();
            task = (TaskImpl) this.getTask(taskId);

            // check pre-conditions for non-forced invocation
            if (!isForced) {
                if (task.getClaimed() == null || task.getState() != TaskState.CLAIMED) {
                    LOGGER.warn("Method completeTask() does expect a task which need to be CLAIMED before. TaskId={}",
                        taskId);
                    throw new InvalidStateException(taskId);
                } else if (CurrentUserContext.getUserid() != task.getOwner()) {
                    LOGGER.warn(
                        "Method completeTask() does expect to be invoced by the task-owner or a administrator. TaskId={}, TaskOwner={}, CurrentUser={}",
                        taskId, task.getOwner(), CurrentUserContext.getUserid());
                    throw new InvalidOwnerException(
                        "TaskOwner is" + task.getOwner() + ", but current User is " + CurrentUserContext.getUserid());
                }
            } else {
                // CLAIM-forced, if task was not already claimed before.
                if (task.getClaimed() == null || task.getState() != TaskState.CLAIMED) {
                    task = (TaskImpl) this.claim(taskId, true);
                }
            }
            Timestamp now = new Timestamp(System.currentTimeMillis());
            task.setCompleted(now);
            task.setModified(now);
            task.setState(TaskState.COMPLETED);
            taskMapper.update(task);
            LOGGER.debug("Method completeTask() completed Task '{}'.", taskId);
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from completeTask()");
        }
        return task;
    }

    @Override
    public Task createTask(Task taskToCreate)
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        LOGGER.debug("entry to createTask(task = {})", taskToCreate);
        try {
            taskanaEngineImpl.openConnection();
            TaskImpl task = (TaskImpl) taskToCreate;
            if (task.getId() != "" && task.getId() != null) {
                throw new TaskAlreadyExistException(taskToCreate.getId());
            } else {
                LOGGER.debug("Task {} cannot be be found, so it can be created.", taskToCreate.getId());
                Workbasket workbasket = workbasketService.getWorkbasketByKey(task.getWorkbasketKey());
                workbasketService.checkAuthorization(task.getWorkbasketKey(), WorkbasketAuthorization.APPEND);
                String classificationKey = task.getClassificationKey();
                if (classificationKey == null || classificationKey.length() == 0) {
                    throw new InvalidArgumentException("classificationKey of task must not be empty");
                }
                Classification classification = this.classificationService.getClassification(classificationKey,
                    workbasket.getDomain());
                task.setClassification(classification);
                validateObjectReference(task.getPrimaryObjRef(), "primary ObjectReference", "Task");
                validateAttachments(task);
                task.setDomain(workbasket.getDomain());
                standardSettings(task);
                this.taskMapper.insert(task);
                LOGGER.debug("Method createTask() created Task '{}'.", task.getId());
            }
            return task;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from createTask(task = {})");
        }
    }

    @Override
    public Task getTask(String id) throws TaskNotFoundException, SystemException {
        LOGGER.debug("entry to getTaskById(id = {})", id);
        TaskImpl resultTask = null;
        try {
            taskanaEngineImpl.openConnection();
            resultTask = taskMapper.findById(id);
            if (resultTask != null) {
                organizeAttachments(resultTask);

                // TODO - DELETE getting task-Classification and enable it in
                // organizeTaskAttachmentClassificationSummaries();
                try {
                    Classification classification = this.classificationService.getClassificationByTask(resultTask);
                    resultTask.setClassification(classification);
                } catch (ClassificationNotFoundException e) {
                    LOGGER.debug(
                        "getTask(taskId = {}) caught a ClassificationNotFoundException when attemptin to get "
                            + "the classification for the task. Throwing a SystemException ",
                        id);
                    throw new SystemException(
                        "TaskService.getTask could not find the classification associated to " + id);
                }
                organizeTaskAttachmentClassificationSummaries(resultTask);
                return resultTask;
            } else {
                LOGGER.warn("Method getTaskById() didn't find task with id {}. Throwing TaskNotFoundException", id);
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskById(). Returning result {} ", resultTask);
        }
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketKey)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        LOGGER.debug("entry to transfer(taskId = {}, destinationWorkbasketKey = {})", taskId, destinationWorkbasketKey);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            TaskImpl task = (TaskImpl) getTask(taskId);

            // transfer requires TRANSFER in source and APPEND on destination workbasket
            workbasketService.checkAuthorization(destinationWorkbasketKey, WorkbasketAuthorization.APPEND);
            workbasketService.checkAuthorization(task.getWorkbasketKey(), WorkbasketAuthorization.TRANSFER);

            // if security is disabled, the implicit existance check on the
            // destination workbasket has been skipped and needs to be performed
            if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
                workbasketService.getWorkbasketByKey(destinationWorkbasketKey);
            }

            // reset read flag and set transferred flag
            task.setRead(false);
            task.setTransferred(true);

            // transfer task from source to destination workbasket
            task.setWorkbasketKey(destinationWorkbasketKey);
            task.setDomain(workbasketService.getWorkbasketByKey(destinationWorkbasketKey).getDomain());
            task.setModified(Timestamp.valueOf(LocalDateTime.now()));
            taskMapper.update(task);

            result = getTask(taskId);
            LOGGER.debug("Method transfer() transferred Task '{}' to destination workbasket {}", taskId,
                destinationWorkbasketKey);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from transfer(). Returning result {} ", result);
        }
    }

    @Override
    public Task setTaskRead(String taskId, boolean isRead)
        throws TaskNotFoundException {
        LOGGER.debug("entry to setTaskRead(taskId = {}, isRead = {})", taskId, isRead);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            TaskImpl task = (TaskImpl) getTask(taskId);
            task.setRead(true);
            task.setModified(Timestamp.valueOf(LocalDateTime.now()));
            taskMapper.update(task);
            result = getTask(taskId);
            LOGGER.debug("Method setTaskRead() set read property of Task '{}' to {} ", result, isRead);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from setTaskRead(taskId, isRead). Returning result {} ", result);
        }
    }

    @Override
    public TaskQuery createTaskQuery() {
        return new TaskQueryImpl(taskanaEngine);
    }

    @Override
    public List<Task> getTasksByWorkbasketKeyAndState(String workbasketKey, TaskState taskState)
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {
        LOGGER.debug("entry to getTasksByWorkbasketKeyAndState(workbasketKey = {}, taskState = {})", workbasketKey,
            taskState);
        List<Task> results = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            workbasketService.checkAuthorization(workbasketKey, WorkbasketAuthorization.READ);
            List<TaskImpl> tasks = taskMapper.findTasksByWorkbasketIdAndState(workbasketKey, taskState);
            for (TaskImpl taskImpl : tasks) {
                Classification classification = classificationService.getClassification(taskImpl.getClassificationKey(),
                    taskImpl.getDomain());
                taskImpl.setClassification(classification);
                results.add(taskImpl);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = results == null ? 0 : results.size();
                LOGGER.debug(
                    "exit from getTasksByWorkbasketIdAndState(workbasketId, taskState). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(results));
            }
        }
        return (results == null) ? new ArrayList<>() : results;
    }

    @Override
    public Task updateTask(Task task)
        throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException, WorkbasketNotFoundException,
        ClassificationNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to updateTask(task = {}, userId = {})", task, userId);
        TaskImpl newTaskImpl = (TaskImpl) task;
        TaskImpl oldTaskImpl = null;
        try {
            taskanaEngineImpl.openConnection();
            oldTaskImpl = (TaskImpl) getTask(newTaskImpl.getId());
            standardUpdateActions(oldTaskImpl, newTaskImpl);

            taskMapper.update(newTaskImpl);
            LOGGER.debug("Method updateTask() updated task '{}' for user '{}'.", task.getId(), userId);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from claim()");
        }
        return task;
    }

    private void standardSettings(TaskImpl task) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        task.setId(IdGenerator.generateWithPrefix(ID_PREFIX_TASK));
        task.setState(TaskState.READY);
        task.setCreated(now);
        task.setModified(now);
        task.setRead(false);
        task.setTransferred(false);

        if (task.getPlanned() == null) {
            task.setPlanned(now);
        }

        // if no business process id is provided, a unique id is created.
        if (task.getBusinessProcessId() == null) {
            task.setBusinessProcessId(IdGenerator.generateWithPrefix(ID_PREFIX_BUSINESS_PROCESS));
        }

        // insert Classification specifications if Classification is given.
        Classification classification = task.getClassification();
        if (classification != null) {
            if (classification.getServiceLevel() != null) {
                Duration serviceLevel = Duration.parse(task.getClassification().getServiceLevel());
                LocalDateTime due = task.getPlanned().toLocalDateTime().plus(serviceLevel);
                task.setDue(Timestamp.valueOf(due));
            }

            if (task.getName() == null) {
                task.setName(classification.getName());
            }

            if (task.getDescription() == null) {
                task.setDescription(classification.getDescription());
            }

            if (task.getPriority() == 0) {
                task.setPriority(classification.getPriority());
            }
        }

        // insert Attachments if needed
        List<Attachment> attachments = task.getAttachments();
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                AttachmentImpl attImpl = (AttachmentImpl) attachment;
                attImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
                attImpl.setTaskId(task.getId());
                attImpl.setCreated(now);
                attImpl.setModified(now);
                attachmentMapper.insert(attImpl);
            }
        }

        // insert ObjectReference if needed. Comment this out for the scope of tsk-123
        // if (task.getPrimaryObjRef() != null) {
        // ObjectReference objectReference = this.objectReferenceMapper.findByObjectReference(task.getPrimaryObjRef());
        // if (objectReference == null) {
        // objectReference = task.getPrimaryObjRef();
        // objectReference.setId(IdGenerator.generateWithPrefix(ID_PREFIX_OBJECT_REFERENCE));
        // this.objectReferenceMapper.insert(objectReference);
        // }
        // // task.setPrimaryObjRef(objectReference);
        // }
    }

    @Override
    public List<TaskSummary> getTaskSummariesByWorkbasketKey(String workbasketKey)
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        LOGGER.debug("entry to getTaskSummariesByWorkbasketId(workbasketId = {}", workbasketKey);
        List<TaskSummary> taskSummaries = new ArrayList<>();
        workbasketService.getWorkbasketByKey(workbasketKey);
        try {
            taskanaEngineImpl.openConnection();
            taskSummaries = taskMapper.findTaskSummariesByWorkbasketKey(workbasketKey);
        } catch (Exception ex) {
            LOGGER.error("Getting TASKSUMMARY failed internally.", ex);
        } finally {
            if (taskSummaries == null) {
                taskSummaries = new ArrayList<>();
            }
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = taskSummaries.size();
                LOGGER.debug(
                    "exit from getTaskSummariesByWorkbasketId(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(taskSummaries));
            }
        }
        return taskSummaries;
    }

    @Override
    public Task newTask() {
        return new TaskImpl();
    }

    @Override
    public Attachment newAttachment() {
        return new AttachmentImpl();
    }

    private void organizeTaskAttachmentClassificationSummaries(TaskImpl taskImpl) {
        List<Attachment> attachments = taskImpl.getAttachments();
        List<ClassificationSummary> classificationSummaries;
        List<String> classificationKeyList = new ArrayList<>();
        String[] classificationKeys;
        String domain = taskImpl.getDomain();

        if (attachments != null && !attachments.isEmpty()) {
            for (Attachment attachment : attachments) {
                if (!classificationKeyList.contains(attachment.getClassificationKey())) {
                    classificationKeyList.add(attachment.getClassificationKey());
                }
            }
        } else {
            attachments = new ArrayList<>();
        }
        classificationKeyList.add(taskImpl.getClassificationKey());
        classificationKeys = classificationKeyList.toArray(new String[0]);

        try {
            classificationSummaries = classificationService.createClassificationQuery()
                .domain(domain)
                .key(classificationKeys)
                .list();
            for (Attachment attachment : attachments) {
                Optional<ClassificationSummary> summary = classificationSummaries.stream()
                    .filter(cs -> cs.getKey().equals(attachment.getClassificationKey()))
                    .findFirst();
                if (summary.isPresent()) {
                    ((AttachmentImpl) attachment).setClassificationSummary(summary.get());
                }
            }

            // TODO - Change Task.Classification to ClassificationSummary and enable this snippet.
            // Optional<ClassificationSummary> summary = classificationSummaries.stream()
            // .filter(cs -> cs.getKey().equals(taskImpl.getClassificationKey()))
            // .findFirst();
            // if (summary.isPresent()) {
            // taskImpl.setClassification(summary.get());
            // }
        } catch (NotAuthorizedException e) {
            LOGGER.warn(
                "Throwing NotAuthorizedException when getting Classifications of the Task-Attachments. TaskId={}",
                taskImpl.getId());
        }
    }

    private void organizeAttachments(TaskImpl taskImpl) {
        List<Attachment> attachments = new ArrayList<>();
        List<AttachmentImpl> attachmentImpls;

        attachmentImpls = attachmentMapper.findAttachmentsByTaskId(taskImpl.getId());
        if (attachmentImpls != null && !attachmentImpls.isEmpty()) {
            attachments.addAll(attachmentImpls);
        }
        taskImpl.setAttachments(attachments);
    }

    private void validateObjectReference(ObjectReference objRef, String objRefType, String objName)
        throws InvalidArgumentException {
        // check that all values in the ObjectReference are set correctly
        if (objRef == null) {
            throw new InvalidArgumentException(objRefType + " of " + objName + " must not be null");
        } else if (objRef.getCompany() == null || objRef.getCompany().length() == 0) {
            throw new InvalidArgumentException(
                "Company of " + objRefType + " of " + objName + " must not be empty");
        } else if (objRef.getSystem() == null || objRef.getSystem().length() == 0) {
            throw new InvalidArgumentException(
                "System of " + objRefType + " of " + objName + " must not be empty");
        } else if (objRef.getSystemInstance() == null || objRef.getSystemInstance().length() == 0) {
            throw new InvalidArgumentException(
                "SystemInstance of " + objRefType + " of " + objName + " must not be empty");
        } else if (objRef.getType() == null || objRef.getType().length() == 0) {
            throw new InvalidArgumentException("Type of " + objRefType + " of " + objName + " must not be empty");
        } else if (objRef.getValue() == null || objRef.getValue().length() == 0) {
            throw new InvalidArgumentException("Value of" + objRefType + " of " + objName + " must not be empty");
        }
    }

    private void validateAttachments(TaskImpl task) throws InvalidArgumentException {
        List<Attachment> attachments = task.getAttachments();
        if (attachments == null || attachments.size() == 0) {
            return;
        }

        for (Attachment attachment : attachments) {
            ObjectReference objRef = attachment.getObjectReference();
            validateObjectReference(objRef, "ObjectReference", "Attachment");
            if (attachment.getClassificationSummary() == null) {
                throw new InvalidArgumentException("Classification of attachment " + attachment + " must not be null");
            }
        }
    }

    private void standardUpdateActions(TaskImpl oldTaskImpl, TaskImpl newTaskImpl)
        throws InvalidArgumentException, ConcurrencyException, WorkbasketNotFoundException, InvalidWorkbasketException,
        ClassificationNotFoundException, NotAuthorizedException {
        validateObjectReference(newTaskImpl.getPrimaryObjRef(), "primary ObjectReference", "Task");
        if (oldTaskImpl.getModified() != null && !oldTaskImpl.getModified().equals(newTaskImpl.getModified())
            || oldTaskImpl.getClaimed() != null && !oldTaskImpl.getClaimed().equals(newTaskImpl.getClaimed())
            || oldTaskImpl.getState() != null && !oldTaskImpl.getState().equals(newTaskImpl.getState())) {
            throw new ConcurrencyException("The task has already been updated by another user");
        }

        String newWorkbasketKey = newTaskImpl.getWorkbasketKey();
        if (newWorkbasketKey != null && !newWorkbasketKey.equals(oldTaskImpl.getWorkbasketKey())) {
            throw new InvalidArgumentException("A task's Workbasket cannot be changed via update of the task");
        }

        if (newTaskImpl.getPlanned() == null) {
            newTaskImpl.setPlanned(oldTaskImpl.getPlanned());
        }

        // if no business process id is provided, use the id of the old task.
        if (newTaskImpl.getBusinessProcessId() == null) {
            newTaskImpl.setBusinessProcessId(oldTaskImpl.getBusinessProcessId());
        }

        // insert Classification specifications if Classification is given.
        Classification oldClassification = oldTaskImpl.getClassification();
        Classification newClassification = oldClassification;
        String newClassificationKey = newTaskImpl.getClassificationKey();

        if (newClassificationKey != null && !newClassificationKey.equals(oldClassification.getKey())) {
            Workbasket workbasket = workbasketService.getWorkbasketByKey(newTaskImpl.getWorkbasketKey());
            // set new classification
            newClassification = this.classificationService.getClassification(newClassificationKey,
                workbasket.getDomain());
        }

        newTaskImpl.setClassification(newClassification);

        // if (newClassification != null) {
        if (newClassification.getServiceLevel() != null) {
            Duration serviceLevel = Duration.parse(newClassification.getServiceLevel());
            LocalDateTime due = newTaskImpl.getPlanned().toLocalDateTime().plus(serviceLevel);
            newTaskImpl.setDue(Timestamp.valueOf(due));
        }

        if (newTaskImpl.getName() == null) {
            newTaskImpl.setName(newClassification.getName());
        }

        if (newTaskImpl.getDescription() == null) {
            newTaskImpl.setDescription(newClassification.getDescription());
        }

        if (newTaskImpl.getPriority() == 0) {
            newTaskImpl.setPriority(newClassification.getPriority());
        }
        // }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        newTaskImpl.setModified(now);
    }
}
