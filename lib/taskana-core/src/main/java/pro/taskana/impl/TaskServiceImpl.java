package pro.taskana.impl;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.*;
import pro.taskana.model.mappings.ObjectReferenceMapper;
import pro.taskana.model.mappings.TaskMapper;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public Task claim(String id, String userName) throws TaskNotFoundException {
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
                LOGGER.debug("User '{}' claimed task '{}'.", userName, id);
            } else {
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
        }
        return task;
    }

    @Override
    public Task complete(String id) throws TaskNotFoundException {
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
                LOGGER.debug("Task '{}' completed.", id);
            } else {
                throw new TaskNotFoundException(id);
            }
        } finally {
            taskanaEngineImpl.returnConnection();
        }
        return task;
    }

    @Override
    public Task create(Task task) throws NotAuthorizedException, WorkbasketNotFoundException {
        try {
            taskanaEngineImpl.openConnection();
            taskanaEngine.getWorkbasketService().checkAuthorization(task.getWorkbasketId(), WorkbasketAuthorization.APPEND);

            standardSettings(task);

            this.taskMapper.insert(task);

            LOGGER.debug("Task '{}' created.", task.getId());
            return task;
        } finally {
            taskanaEngineImpl.returnConnection();
        }
    }

    @Override
    public Task createManualTask(String workbasketId, String classificationId, String domain, Timestamp planned, String name, String description, ObjectReference primaryObjectReference, Map<String, Object> customAttributes) throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
        try {
            taskanaEngineImpl.openConnection();
            taskanaEngine.getWorkbasketService().checkAuthorization(workbasketId, WorkbasketAuthorization.APPEND);

            taskanaEngine.getWorkbasketService().getWorkbasket(workbasketId);
            Classification classification = taskanaEngine.getClassificationService().getClassification(classificationId, domain);

            if (classification.getCategory() != "MANUAL") {
                throw new NotAuthorizedException("You're not allowed to add a task manually to a '" + classification.getCategory() + "'- Classification!");
            }

            Task task = new Task();

            task.setWorkbasketId(workbasketId);
            task.setClassification(classification);
            task.setPlanned(planned);
            task.setPrimaryObjRef(primaryObjectReference);
            task.setCustomAttributes(customAttributes);
            task.setName(name);
            task.setDescription(description);

            this.standardSettings(task);
            this.setCustomers(task);

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
    @Override
    public List<Task> getTasksByWorkbasketIdAndState(String workbasketId, TaskState taskState) throws WorkbasketNotFoundException, NotAuthorizedException, Exception {
        List<Task> resultList = null;
        try {
            taskanaEngineImpl.openConnection();
            taskanaEngine.getWorkbasketService().checkAuthorization(workbasketId, WorkbasketAuthorization.READ);
            resultList = taskMapper.findTasksByWorkbasketIdAndState(workbasketId, taskState);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
        return (resultList == null) ? new ArrayList<>() : resultList;
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

    private void setCustomers(Task task) {
        if (task.getCustomAttributes() != null) {
            for (String custom : task.getCustomAttributes().keySet()) {
                if (task.getCustom1() == null) {
                    task.setCustom1(custom);
                } else if (task.getCustom2() == null) {
                    task.setCustom2(custom);
                } else if (task.getCustom3() == null) {
                    task.setCustom3(custom);
                } else if (task.getCustom4() == null) {
                    task.setCustom4(custom);
                } else if (task.getCustom5() == null) {
                    task.setCustom5(custom);
                } else if (task.getCustom6() == null) {
                    task.setCustom6(custom);
                } else if (task.getCustom7() == null) {
                    task.setCustom7(custom);
                } else if (task.getCustom8() == null) {
                    task.setCustom8(custom);
                } else if (task.getCustom9() == null) {
                    task.setCustom9(custom);
                } else if (task.getCustom10() == null) {
                    task.setCustom10(custom);
                } else {
                    break;
                }
            }
        }
    }
}
