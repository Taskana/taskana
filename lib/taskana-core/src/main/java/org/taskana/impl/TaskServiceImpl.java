package org.taskana.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taskana.TaskService;
import org.taskana.TaskanaEngine;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.TaskNotFoundException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.impl.util.IdGenerator;
import org.taskana.model.DueWorkbasketCounter;
import org.taskana.model.ObjectReference;
import org.taskana.model.Task;
import org.taskana.model.TaskState;
import org.taskana.model.TaskStateCounter;
import org.taskana.model.WorkbasketAuthorization;
import org.taskana.model.mappings.ObjectReferenceMapper;
import org.taskana.model.mappings.TaskMapper;
/**
 * This is the implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String ID_PREFIX_OBJECTR_EFERENCE = "ORI";
    private static final String ID_PREFIX_TASK = "TKI";

    private TaskanaEngine taskanaEngine;
    private TaskMapper taskMapper;
    private ObjectReferenceMapper objectReferenceMapper;

    public TaskServiceImpl(TaskanaEngine taskanaEngine, TaskMapper taskMapper,
            ObjectReferenceMapper objectReferenceMapper) {
        super();
        this.taskanaEngine = taskanaEngine;
        this.taskMapper = taskMapper;
        this.objectReferenceMapper = objectReferenceMapper;
    }

    @Override
    public void claim(String id, String userName) throws TaskNotFoundException {
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
    }

    @Override
    public void complete(String id) throws TaskNotFoundException {
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
    }

    @Override
    public Task create(Task task) throws NotAuthorizedException {
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
    }

    @Override
    public Task getTaskById(String id) throws TaskNotFoundException {
        Task task = taskMapper.findById(id);
        if (task != null) {
            return task;
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Override
    public List<Task> getTasksForWorkbasket(String workbasketId) throws NotAuthorizedException {
        taskanaEngine.getWorkbasketService().checkAuthorization(workbasketId, WorkbasketAuthorization.OPEN);

        return taskMapper.findByWorkBasketId(workbasketId);
    }

    @Override
    public List<Task> findTasks(List<TaskState> states) {
        return taskMapper.findByStates(states);
    }

    @Override
    public List<Task> getTasksForWorkbasket(List<String> workbasketIds, List<TaskState> states)
            throws NotAuthorizedException {

        for (String workbasket : workbasketIds) {
            taskanaEngine.getWorkbasketService().checkAuthorization(workbasket, WorkbasketAuthorization.OPEN);
        }

        return taskMapper.findByWorkbasketIdsAndStates(workbasketIds, states);
    }

    @Override
    public List<Task> getTasks() {
        return taskMapper.findAll();
    }

    @Override
    public List<TaskStateCounter> getTaskCountForState(List<TaskState> states) {
        return taskMapper.getTaskCountForState(states);
    }

    @Override
    public long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast,
            List<TaskState> states) {
        LocalDate time = LocalDate.now();
        time = time.minusDays(daysInPast);
        Date fromDate = Date.valueOf(time);
        return taskMapper.getTaskCountForWorkbasketByDaysInPastAndState(workbasketId, fromDate, states);
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketId)
            throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException {
        Task task = getTaskById(taskId);

        // transfer requires TRANSFER in source and APPEND on destination
        // workbasket
        taskanaEngine.getWorkbasketService().checkAuthorization(destinationWorkbasketId,
                WorkbasketAuthorization.APPEND);
        taskanaEngine.getWorkbasketService().checkAuthorization(task.getWorkbasketId(),
                WorkbasketAuthorization.TRANSFER);

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
    }

    @Override
    public List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast,
            List<TaskState> states) {
        LocalDate time = LocalDate.now();
        time = time.minusDays(daysInPast);
        Date fromDate = Date.valueOf(time);
        return taskMapper.getTaskCountByWorkbasketIdAndDaysInPastAndState(fromDate, states);
    }

    @Override
    public Task setTaskRead(String taskId, boolean isRead) throws TaskNotFoundException {
        Task task = getTaskById(taskId);
        task.setRead(true);
        task.setModified(Timestamp.valueOf(LocalDateTime.now()));
        taskMapper.update(task);
        return getTaskById(taskId);
    }

}
