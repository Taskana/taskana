package pro.taskana.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;
import pro.taskana.persistence.TaskQuery;

/**
 * This is the implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String ID_PREFIX_OBJECTR_EFERENCE = "ORI";
    private static final String ID_PREFIX_TASK = "TKI";

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
    public void claim(String id, String userName) throws TaskNotFoundException {
        try {
            taskanaEngineImpl.openConnection();
            Task task = taskMapper.findById(id);
            if (task != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                task.setOwner(userName);
                task.setModified(now);
                task.setClaimed(now);
                task.setState(TaskState.CLAIMED);
                taskMapper.update(task);
                LOGGER.debug("User '{}' claimed task '{}'.", userName, id);
            } else {
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public void complete(String id) throws TaskNotFoundException {
        try {
            taskanaEngineImpl.openConnection();
            Task task = taskMapper.findById(id);
            if (task != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                task.setCompleted(now);
                task.setModified(now);
                task.setState(TaskState.COMPLETED);
                taskMapper.update(task);
                LOGGER.debug("Task '{}' completed.", id);
            } else {
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Task create(Task task) throws NotAuthorizedException {
        try {
            taskanaEngineImpl.openConnection();
            taskanaEngine.getWorkbasketService().checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.APPEND);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            task.setId(IdGenerator.generateWithPrefix(ID_PREFIX_TASK));
            task.setState(TaskState.READY);
            task.setCreated(now);
            task.setModified(now);
            task.setRead(false);
            task.setTransferred(false);

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
            this.taskMapper.insert(task);

            LOGGER.debug("Task '{}' created.", task.getId());
            return task;
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Task getTaskById(String id) throws TaskNotFoundException {
        try {
            taskanaEngineImpl.openConnection();
            Task task = taskMapper.findById(id);
            if (task != null) {
                return task;
            } else {
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public List<TaskStateCounter> getTaskCountForState(List<TaskState> states) {
        try {
            taskanaEngineImpl.openConnection();
            return taskMapper.getTaskCountForState(states);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast, List<TaskState> states) {
        try {
            taskanaEngineImpl.openConnection();
            LocalDate time = LocalDate.now();
            time = time.minusDays(daysInPast);
            Date fromDate = Date.valueOf(time);
            return taskMapper.getTaskCountForWorkbasketByDaysInPastAndState(workbasketId, fromDate, states);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketId)
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
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

            return getTaskById(taskId);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast,
            List<TaskState> states) {
        try {
            taskanaEngineImpl.openConnection();
            LocalDate time = LocalDate.now();
            time = time.minusDays(daysInPast);
            Date fromDate = Date.valueOf(time);
            return taskMapper.getTaskCountByWorkbasketIdAndDaysInPastAndState(fromDate, states);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException {
        try {
            taskanaEngineImpl.openConnection();
            Task task = getTaskById(taskId);
            task.setRead(true);
            task.setModified(Timestamp.valueOf(LocalDateTime.now()));
            taskMapper.update(task);
            return getTaskById(taskId);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public TaskQuery createTaskQuery() {
        return new TaskQueryImpl(taskanaEngine);
    }
}
