package pro.taskana.impl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Classification;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * This is the implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String ID_PREFIX_OBJECT_REFERENCE = "ORI";

    private static final String ID_PREFIX_TASK = "TKI";

    private static final String ID_PREFIX_BUSINESS_PROCESS = "BPI";

    private TaskanaEngine taskanaEngine;

    private TaskanaEngineImpl taskanaEngineImpl;

    private WorkbasketService workbasketService;

    private TaskMapper taskMapper;

    private ObjectReferenceMapper objectReferenceMapper;

    public TaskServiceImpl(TaskanaEngine taskanaEngine, TaskMapper taskMapper,
        ObjectReferenceMapper objectReferenceMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMapper = taskMapper;
        this.objectReferenceMapper = objectReferenceMapper;
        this.workbasketService = taskanaEngineImpl.getWorkbasketService();
    }

    @Override
    public Task claim(String taskId) throws TaskNotFoundException, InvalidStateException, InvalidOwnerException {
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
            task = (TaskImpl) getTaskById(taskId);
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
    public Task completeTask(String taskId) throws TaskNotFoundException, InvalidOwnerException, InvalidStateException {
        return completeTask(taskId, false);
    }

    @Override
    public Task completeTask(String taskId, boolean isForced)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException {
        LOGGER.debug("entry to completeTask(id = {}, isForced {})", taskId, isForced);
        TaskImpl task = null;
        try {
            taskanaEngineImpl.openConnection();
            task = (TaskImpl) this.getTaskById(taskId);
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
        TaskAlreadyExistException {
        LOGGER.debug("entry to createTask(task = {})", taskToCreate);
        try {
            taskanaEngineImpl.openConnection();
            TaskImpl task = (TaskImpl) taskToCreate;
            if (task.getId() != "" && task.getId() != null) {
                throw new TaskAlreadyExistException(taskToCreate.getId());
            } else {
                LOGGER.debug("Task {} can´t be be found, so it can be created.", taskToCreate.getId());
                workbasketService.getWorkbasket(task.getWorkbasketId());
                workbasketService.checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.APPEND);
                Classification classification = task.getClassification();
                if (classification == null) {
                    throw new ClassificationNotFoundException(null);
                }
                taskanaEngine.getClassificationService().getClassification(classification.getKey(),
                    classification.getDomain());

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
    public Task getTaskById(String id) throws TaskNotFoundException {
        LOGGER.debug("entry to getTaskById(id = {})", id);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = taskMapper.findById(id);
            if (result != null) {
                return result;
            } else {
                LOGGER.warn("Method getTaskById() didn't find task with id {}. Throwing TaskNotFoundException", id);
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskById(). Returning result {} ", result);
        }
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketId)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to transfer(taskId = {}, destinationWorkbasketId = {})", taskId, destinationWorkbasketId);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            TaskImpl task = (TaskImpl) getTaskById(taskId);

            // transfer requires TRANSFER in source and APPEND on destination workbasket
            workbasketService.checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
            workbasketService.checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.TRANSFER);

            // if security is disabled, the implicit existance check on the
            // destination workbasket has been skipped and needs to be performed
            if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
                workbasketService.getWorkbasket(destinationWorkbasketId);
            }

            // reset read flag and set transferred flag
            task.setRead(false);
            task.setTransferred(true);

            // transfer task from source to destination workbasket
            task.setWorkbasketId(destinationWorkbasketId);
            task.setModified(Timestamp.valueOf(LocalDateTime.now()));
            taskMapper.update(task);

            result = getTaskById(taskId);
            LOGGER.debug("Method transfer() transferred Task '{}' to destination workbasket {}", taskId,
                destinationWorkbasketId);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from transfer(). Returning result {} ", result);
        }
    }

    @Override
    public Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException {
        LOGGER.debug("entry to setTaskRead(taskId = {}, isRead = {})", taskId, isRead);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            TaskImpl task = (TaskImpl) getTaskById(taskId);
            task.setRead(true);
            task.setModified(Timestamp.valueOf(LocalDateTime.now()));
            taskMapper.update(task);
            result = getTaskById(taskId);
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
    public List<Task> getTasksByWorkbasketIdAndState(String workbasketId, TaskState taskState)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to getTasksByWorkbasketIdAndState(workbasketId = {}, taskState = {})", workbasketId,
            taskState);
        List<Task> results = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            workbasketService.checkAuthorization(workbasketId, WorkbasketAuthorization.READ);
            List<TaskImpl> tasks = taskMapper.findTasksByWorkbasketIdAndState(workbasketId, taskState);
            tasks.stream().forEach(t -> results.add(t));
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

        // insert ObjectReference if needed.
        if (task.getPrimaryObjRef() != null) {
            ObjectReference objectReference = this.objectReferenceMapper.findByObjectReference(task.getPrimaryObjRef());
            if (objectReference == null) {
                objectReference = task.getPrimaryObjRef();
                objectReference.setId(IdGenerator.generateWithPrefix(ID_PREFIX_OBJECT_REFERENCE));
                this.objectReferenceMapper.insert(objectReference);
            }
            task.setPrimaryObjRef(objectReference);
        }
    }

    @Override
    public List<TaskSummary> getTaskSummariesByWorkbasketId(String workbasketId) throws WorkbasketNotFoundException {
        LOGGER.debug("entry to getTaskSummariesByWorkbasketId(workbasketId = {}", workbasketId);
        List<TaskSummary> taskSummaries = new ArrayList<>();
        workbasketService.getWorkbasket(workbasketId);
        try {
            taskanaEngineImpl.openConnection();
            taskSummaries = taskMapper.findTaskSummariesByWorkbasketId(workbasketId);
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
}
