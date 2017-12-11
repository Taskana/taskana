package pro.taskana.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Classification;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;

/**
 * This is the implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String ID_PREFIX_OBJECTR_EFERENCE = "ORI";
    private static final String ID_PREFIX_TASK = "TKI";
    private static final String ID_PREFIX_BUSINESS_PROCESS = "BPI";

    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private TaskMapper taskMapper;
    private ObjectReferenceMapper objectReferenceMapper;

    public TaskServiceImpl(TaskanaEngine taskanaEngine, TaskMapper taskMapper,
            ObjectReferenceMapper objectReferenceMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMapper = taskMapper;
        this.objectReferenceMapper = objectReferenceMapper;
    }

    @Override
    public Task claim(String id, String userName) throws TaskNotFoundException {
        LOGGER.debug("entry to claim(id = {}, userName = {})", id, userName);
        Task task = null;
        try {
            taskanaEngineImpl.openConnection();
            task = taskMapper.findById(id);
            if (task != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                task.setOwner(userName);
                task.setModified(now);
                task.setClaimed(now);
                task.setState(TaskState.CLAIMED);
                taskMapper.update(task);
                LOGGER.debug("Method claim() claimed task '{}' for user '{}'.", id, userName);
            } else {
                LOGGER.warn("Method claim() didn't find task with id {}. Throwing TaskNotFoundException", id);
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from claim()");
        }
        return task;
    }

    @Override
    public Task complete(String id) throws TaskNotFoundException {
        LOGGER.debug("entry to complete(id = {})", id);
        Task task = null;
        try {
            taskanaEngineImpl.openConnection();
            task = taskMapper.findById(id);
            if (task != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                task.setCompleted(now);
                task.setModified(now);
                task.setState(TaskState.COMPLETED);
                taskMapper.update(task);
                LOGGER.debug("Method complete() completed Task '{}'.", id);
            } else {
                LOGGER.warn("Method complete() didn't find task with id {}. Throwing TaskNotFoundException", id);
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from complete()");
        }
        return task;
    }

    @Override
    public Task createTask(Task task) throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        LOGGER.debug("entry to createTask(task = {})", task);
        try {
            taskanaEngineImpl.openConnection();
            taskanaEngine.getWorkbasketService().getWorkbasket(task.getWorkbasketId());
            taskanaEngine.getWorkbasketService().checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.APPEND);
            Classification classification = task.getClassification();
            if (classification == null) {
                throw new ClassificationNotFoundException(null);
            }
            taskanaEngine.getClassificationService().getClassification(classification.getId(), "");

            standardSettings(task);

            this.taskMapper.insert(task);

            LOGGER.debug("Method createTask() created Task '{}'.", task.getId());
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
    public List<TaskStateCounter> getTaskCountForState(List<TaskState> states) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskCountForState(states = {})", LoggerUtils.listToString(states));
        }
        List<TaskStateCounter> result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = taskMapper.getTaskCountForState(states);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getTaskCountForState(). Returning {} resulting Objects: {} ", numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast, List<TaskState> states) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskCountForWorkbasketByDaysInPastAndState(workbasketId {}, daysInPast={}, states = {})",
                                                                    workbasketId, daysInPast, LoggerUtils.listToString(states));
        }
        long result = -1;
        try {
            taskanaEngineImpl.openConnection();
            LocalDate time = LocalDate.now();
            time = time.minusDays(daysInPast);
            Date fromDate = Date.valueOf(time);
            result = taskMapper.getTaskCountForWorkbasketByDaysInPastAndState(workbasketId, fromDate, states);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskCountForWorkbasketByDaysInPastAndState(). Returning result {} ", result);
       }
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketId)
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to transfer(taskId = {}, destinationWorkbasketId = {})", taskId, destinationWorkbasketId);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            Task task = getTaskById(taskId);

            // transfer requires TRANSFER in source and APPEND on destination workbasket
            taskanaEngine.getWorkbasketService().checkAuthorization(destinationWorkbasketId, WorkbasketAuthorization.APPEND);
            taskanaEngine.getWorkbasketService().checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.TRANSFER);

            // if security is disabled, the implicit existance check on the
            // destination workbasket has been skipped and needs to be performed
            if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
                taskanaEngine.getWorkbasketService().getWorkbasket(destinationWorkbasketId);
            }

            // reset read flag and set transferred flag
            task.setRead(false);
            task.setTransferred(true);

            // transfer task from source to destination workbasket
            task.setWorkbasketId(destinationWorkbasketId);
            task.setModified(Timestamp.valueOf(LocalDateTime.now()));
            taskMapper.update(task);

            result = getTaskById(taskId);
            LOGGER.debug("Method transfer() transferred Task '{}' to destination workbasket {}", taskId, destinationWorkbasketId);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from transfer(). Returning result {} ", result);
        }
    }

    @Override
    public List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast,
            List<TaskState> states) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast = {}, states = {})", daysInPast, LoggerUtils.listToString(states));
        }
        List<DueWorkbasketCounter> result = null;
        try {
            taskanaEngineImpl.openConnection();
            LocalDate time = LocalDate.now();
            time = time.minusDays(daysInPast);
            Date fromDate = Date.valueOf(time);
            result = taskMapper.getTaskCountByWorkbasketIdAndDaysInPastAndState(fromDate, states);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast,states). Returning {} resulting Objects: {} ",
                                                                numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException {
        LOGGER.debug("entry to setTaskRead(taskId = {}, isRead = {})", taskId, isRead);
        Task result = null;
        try {
            taskanaEngineImpl.openConnection();
            Task task = getTaskById(taskId);
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
    public List<Task> getTasksByWorkbasketIdAndState(String workbasketId, TaskState taskState) throws WorkbasketNotFoundException, NotAuthorizedException, Exception {
        LOGGER.debug("entry to getTasksByWorkbasketIdAndState(workbasketId = {}, taskState = {})", workbasketId, taskState);
        List<Task> result = null;
        try {
            taskanaEngineImpl.openConnection();
            taskanaEngine.getWorkbasketService().checkAuthorization(workbasketId, WorkbasketAuthorization.READ);
            result = taskMapper.findTasksByWorkbasketIdAndState(workbasketId, taskState);
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getTasksByWorkbasketIdAndState(workbasketId, taskState). Returning {} resulting Objects: {} ",
                                                                numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
        return (result == null) ? new ArrayList<>() : result;
    }

    private void standardSettings(Task task) {
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
                objectReference.setId(IdGenerator.generateWithPrefix(ID_PREFIX_OBJECTR_EFERENCE));
                this.objectReferenceMapper.insert(objectReference);
            }
            task.setPrimaryObjRef(objectReference);
        }
    }

    @Override
    public List<TaskSummary> getTaskSummariesByWorkbasketId(String workbasketId) throws WorkbasketNotFoundException {
        LOGGER.debug("entry to getTaskSummariesByWorkbasketId(workbasketId = {}", workbasketId);
        List<TaskSummary> taskSummaries = new ArrayList<>();
        taskanaEngineImpl.getWorkbasketService().getWorkbasket(workbasketId);
        try {
            taskanaEngineImpl.openConnection();
            taskSummaries = taskMapper.findTaskSummariesByWorkbasketId(workbasketId);
        } catch (Exception ex) {
            LOGGER.error("Getting TASKSUMMARY failed internally.", ex);
        }  finally {
            if (taskSummaries == null) {
                taskSummaries = new ArrayList<>();
            }
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = taskSummaries.size();
                LOGGER.debug("exit from getTaskSummariesByWorkbasketId(workbasketId). Returning {} resulting Objects: {} ",
                                                        numberOfResultObjects, LoggerUtils.listToString(taskSummaries));
            }
        }
        return taskSummaries;
    }
}
