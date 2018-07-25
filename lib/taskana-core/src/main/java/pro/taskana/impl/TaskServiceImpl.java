package pro.taskana.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Attachment;
import pro.taskana.BulkOperationResults;
import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.CustomPropertySelector;
import pro.taskana.mappings.TaskMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * This is the implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static final String ID_PREFIX_ATTACHMENT = "TAI";
    private static final String ID_PREFIX_TASK = "TKI";
    private static final String ID_PREFIX_BUSINESS_PROCESS = "BPI";
    private static final String MUST_NOT_BE_EMPTY = " must not be empty";
    private static final Duration MAX_DURATION = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
    private DaysToWorkingDaysConverter converter;
    private TaskanaEngineImpl taskanaEngine;
    private WorkbasketService workbasketService;
    private ClassificationServiceImpl classificationService;
    private TaskMapper taskMapper;
    private AttachmentMapper attachmentMapper;

    TaskServiceImpl(TaskanaEngine taskanaEngine, TaskMapper taskMapper,
        AttachmentMapper attachmentMapper) {
        super();
        try {
            this.converter = DaysToWorkingDaysConverter
                .initialize(Collections.singletonList(new TimeIntervalColumnHeader(0)), Instant.now());
        } catch (InvalidArgumentException e) {
            throw new SystemException("Internal error. Cannot initialize DaysToWorkingDaysConverter", e.getCause());
        }
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.taskMapper = taskMapper;
        this.workbasketService = taskanaEngine.getWorkbasketService();
        this.attachmentMapper = attachmentMapper;
        this.classificationService = (ClassificationServiceImpl) taskanaEngine.getClassificationService();
    }

    @Override
    public Task claim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        return claim(taskId, false);
    }

    @Override
    public Task forceClaim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        return claim(taskId, true);
    }

    private Task claim(String taskId, boolean forceClaim)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to claim(id = {}, userId = {}, forceClaim = {})", taskId, userId, forceClaim);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) getTask(taskId);
            TaskState state = task.getState();
            if (state == TaskState.COMPLETED) {
                throw new InvalidStateException("Task with id " + taskId + " is already completed.");
            }
            if (state == TaskState.CLAIMED && !forceClaim && !task.getOwner().equals(userId)) {
                throw new InvalidOwnerException(
                    "Task with id " + taskId + " is already claimed by " + task.getOwner() + ".");
            }
            Instant now = Instant.now();
            task.setOwner(userId);
            task.setModified(now);
            task.setClaimed(now);
            task.setRead(true);
            task.setState(TaskState.CLAIMED);
            taskMapper.update(task);
            LOGGER.debug("Task '{}' claimed by user '{}'.", taskId, userId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from claim()");
        }
        return task;
    }

    @Override
    public Task cancelClaim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        return this.cancelClaim(taskId, false);
    }

    @Override
    public Task forceCancelClaim(String taskId)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        return this.cancelClaim(taskId, true);
    }

    private Task cancelClaim(String taskId, boolean forceUnclaim)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to cancelClaim(taskId = {}), userId = {}, forceUnclaim = {})", taskId, userId,
            forceUnclaim);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) getTask(taskId);
            TaskState state = task.getState();
            if (state == TaskState.COMPLETED) {
                throw new InvalidStateException("Task with id " + taskId + " is already completed.");
            }
            if (state == TaskState.CLAIMED && !forceUnclaim && !userId.equals(task.getOwner())) {
                throw new InvalidOwnerException(
                    "Task with id " + taskId + " is already claimed by " + task.getOwner() + ".");
            }
            Instant now = Instant.now();
            task.setOwner(null);
            task.setModified(now);
            task.setClaimed(null);
            task.setRead(true);
            task.setState(TaskState.READY);
            taskMapper.update(task);
            LOGGER.debug("Task '{}' unclaimed by user '{}'.", taskId, userId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from cancelClaim()");
        }
        return task;
    }

    @Override
    public Task completeTask(String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException {
        return completeTask(taskId, false);
    }

    @Override
    public Task forceCompleteTask(String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException {
        return completeTask(taskId, true);
    }

    private Task completeTask(String taskId, boolean isForced)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to completeTask(id = {}, userId = {}, isForced = {})", taskId, userId, isForced);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) this.getTask(taskId);

            if (task.getState() == TaskState.COMPLETED) {
                return task;
            }

            // check pre-conditions for non-forced invocation
            if (!isForced) {
                if (task.getClaimed() == null || task.getState() != TaskState.CLAIMED) {
                    throw new InvalidStateException("Task with id " + taskId + " has to be claimed before.");
                } else if (!CurrentUserContext.getAccessIds().contains(task.getOwner())) {
                    throw new InvalidOwnerException(
                        "Owner of task " + taskId + " is " + task.getOwner() + ", but current User is " + userId);
                }
            } else {
                // CLAIM-forced, if task was not already claimed before.
                if (task.getClaimed() == null || task.getState() != TaskState.CLAIMED) {
                    task = (TaskImpl) this.forceClaim(taskId);
                }
            }
            Instant now = Instant.now();
            task.setCompleted(now);
            task.setModified(now);
            task.setState(TaskState.COMPLETED);
            task.setOwner(userId);
            taskMapper.update(task);
            LOGGER.debug("Task '{}' completed by user '{}'.", taskId, userId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from completeTask()");
        }
        return task;
    }

    @Override
    public BulkOperationResults<String, TaskanaException> completeTasks(List<String> taskIds)
        throws InvalidArgumentException {
        try {
            LOGGER.debug("entry to completeTasks(taskIds = {})", taskIds);
            taskanaEngine.openConnection();

            // Check pre-conditions with throwing Exceptions
            if (taskIds == null) {
                throw new InvalidArgumentException(
                    "TaskIds can´t be used as NULL-Parameter.");
            }

            // process bulk-complete
            BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
            if (!taskIds.isEmpty()) {
                // remove null/empty taskIds with message
                Iterator<String> taskIdIterator = taskIds.iterator();
                while (taskIdIterator.hasNext()) {
                    String currentTaskId = taskIdIterator.next();
                    if (currentTaskId == null || currentTaskId.isEmpty()) {
                        bulkLog.addError("",
                            new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed and invalid."));
                        taskIdIterator.remove();
                    }
                }

                // query for existing tasks, modify values and LOG missing ones.
                List<TaskSummary> taskSummaries = this.createTaskQuery().idIn(taskIds.toArray(new String[0])).list();
                Instant now = Instant.now();
                taskIdIterator = taskIds.iterator();
                while (taskIdIterator.hasNext()) {
                    String currentTaskId = taskIdIterator.next();
                    TaskSummaryImpl taskSummary = (TaskSummaryImpl) taskSummaries.stream()
                        .filter(ts -> currentTaskId.equals(ts.getTaskId()))
                        .findFirst()
                        .orElse(null);
                    if (taskSummary == null) {
                        bulkLog.addError(currentTaskId, new TaskNotFoundException(currentTaskId, "task with id "
                            + currentTaskId + " was not found."));
                        taskIdIterator.remove();
                    } else if (taskSummary.getClaimed() == null || taskSummary.getState() != TaskState.CLAIMED) {
                        bulkLog.addError(currentTaskId, new InvalidStateException(currentTaskId));
                        taskIdIterator.remove();
                    } else if (!CurrentUserContext.getAccessIds().contains(taskSummary.getOwner())) {
                        bulkLog.addError(currentTaskId, new InvalidOwnerException(
                            "TaskOwner is" + taskSummary.getOwner() + ", but current User is "
                                + CurrentUserContext.getUserid()));
                        taskIdIterator.remove();
                    } else {
                        taskSummary.setCompleted(now);
                        taskSummary.setModified(now);
                        taskSummary.setState(TaskState.COMPLETED);
                    }
                }

                if (!taskIds.isEmpty() && !taskSummaries.isEmpty()) {
                    taskMapper.updateCompleted(taskIds, (TaskSummaryImpl) taskSummaries.get(0));
                }
            }
            return bulkLog;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from to completeTasks(taskIds = {})", taskIds);
        }
    }

    @Override
    public Task createTask(Task taskToCreate)
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, InvalidArgumentException {
        LOGGER.debug("entry to createTask(task = {})", taskToCreate);
        TaskImpl task = (TaskImpl) taskToCreate;
        try {
            taskanaEngine.openConnection();
            if (task.getId() != null && !"".equals(task.getId())) {
                throw new TaskAlreadyExistException(task.getId());
            } else {
                LOGGER.debug("Task {} cannot be be found, so it can be created.", task.getId());
                Workbasket workbasket;

                if (task.getWorkbasketSummary().getId() != null) {
                    workbasket = workbasketService.getWorkbasket(task.getWorkbasketSummary().getId());
                } else if (task.getWorkbasketKey() != null) {
                    workbasket = workbasketService.getWorkbasket(task.getWorkbasketKey(), task.getDomain());
                } else {
                    throw new InvalidArgumentException("Cannot create a task outside a workbasket");
                }

                task.setWorkbasketSummary(workbasket.asSummary());
                task.setDomain(workbasket.getDomain());

                workbasketService.checkAuthorization(task.getWorkbasketSummary().getId(),
                    WorkbasketPermission.APPEND);

                // we do use the key and not the ID to make sure that we use the classification from the right domain.
                // otherwise we would have to check the classification and its domain for validity.
                String classificationKey = task.getClassificationKey();
                if (classificationKey == null || classificationKey.length() == 0) {
                    throw new InvalidArgumentException("classificationKey of task must not be empty");
                }

                Classification classification = this.classificationService.getClassification(classificationKey,
                    workbasket.getDomain());
                task.setClassificationSummary(classification.asSummary());
                validateObjectReference(task.getPrimaryObjRef(), "primary ObjectReference", "Task");
                PrioDurationHolder prioDurationFromAttachments = handleAttachments(task);
                standardSettings(task, classification, prioDurationFromAttachments);
                this.taskMapper.insert(task);
                LOGGER.debug("Method createTask() created Task '{}'.", task.getId());
            }
            return task;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createTask(task = {})", task);
        }
    }

    @Override
    public Task getTask(String id) throws TaskNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to getTaskById(id = {})", id);
        TaskImpl resultTask = null;
        try {
            taskanaEngine.openConnection();

            resultTask = taskMapper.findById(id);
            if (resultTask != null) {
                WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
                query.setUsedToAugmentTasks(true);
                String workbasketId = resultTask.getWorkbasketSummary().getId();
                List<WorkbasketSummary> workbaskets = query
                    .idIn(workbasketId)
                    .list();
                if (workbaskets.isEmpty()) {
                    String currentUser = CurrentUserContext.getUserid();
                    throw new NotAuthorizedException(
                        "The current user " + currentUser + " has no read permission for workbasket " + workbasketId);
                } else {
                    resultTask.setWorkbasketSummary(workbaskets.get(0));
                }

                List<AttachmentImpl> attachmentImpls = attachmentMapper.findAttachmentsByTaskId(resultTask.getId());
                if (attachmentImpls == null) {
                    attachmentImpls = new ArrayList<>();
                }

                List<ClassificationSummary> classifications;
                classifications = findClassificationForTaskImplAndAttachments(resultTask, attachmentImpls);
                List<Attachment> attachments = addClassificationSummariesToAttachments(resultTask, attachmentImpls,
                    classifications);
                resultTask.setAttachments(attachments);

                String classificationId = resultTask.getClassificationSummary().getId();
                ClassificationSummary classification = classifications.stream()
                    .filter(c -> c.getId().equals(classificationId))
                    .findFirst()
                    .orElse(null);
                if (classification == null) {
                    throw new SystemException(
                        "Could not find a Classification for task " + resultTask.getId());
                }

                resultTask.setClassificationSummary(classification);
                return resultTask;
            } else {
                throw new TaskNotFoundException(id, "Task with id " + id + " was not found");
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from getTaskById(). Returning result {} ", resultTask);
        }
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketId)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidStateException {
        LOGGER.debug("entry to transfer(taskId = {}, destinationWorkbasketId = {})", taskId, destinationWorkbasketId);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) getTask(taskId);

            if (task.getState() == TaskState.COMPLETED) {
                throw new InvalidStateException("Completed task with id " + task.getId() + " cannot be transferred.");
            }

            // transfer requires TRANSFER in source and APPEND on destination workbasket
            workbasketService.checkAuthorization(destinationWorkbasketId, WorkbasketPermission.APPEND);
            workbasketService.checkAuthorization(task.getWorkbasketSummary().getId(),
                WorkbasketPermission.TRANSFER);

            Workbasket destinationWorkbasket = workbasketService.getWorkbasket(destinationWorkbasketId);

            // reset read flag and set transferred flag
            task.setRead(false);
            task.setTransferred(true);

            // transfer task from source to destination workbasket
            task.setWorkbasketSummary(destinationWorkbasket.asSummary());
            task.setModified(Instant.now());
            task.setState(TaskState.READY);
            task.setOwner(null);
            taskMapper.update(task);
            LOGGER.debug("Method transfer() transferred Task '{}' to destination workbasket {}", taskId,
                destinationWorkbasketId);
            return task;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from transfer(). Returning result {} ", task);
        }
    }

    @Override
    public Task transfer(String taskId, String destinationWorkbasketKey, String domain)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidStateException {
        LOGGER.debug("entry to transfer(taskId = {}, destinationWorkbasketKey = {}, domain = {})", taskId,
            destinationWorkbasketKey, domain);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) getTask(taskId);

            if (task.getState() == TaskState.COMPLETED) {
                throw new InvalidStateException("Completed task with id " + task.getId() + " cannot be transferred.");
            }

            // transfer requires TRANSFER in source and APPEND on destination workbasket
            workbasketService.checkAuthorization(destinationWorkbasketKey, domain, WorkbasketPermission.APPEND);
            workbasketService.checkAuthorization(task.getWorkbasketSummary().getId(),
                WorkbasketPermission.TRANSFER);

            Workbasket destinationWorkbasket = workbasketService.getWorkbasket(destinationWorkbasketKey, domain);

            // reset read flag and set transferred flag
            task.setRead(false);
            task.setTransferred(true);

            // transfer task from source to destination workbasket
            task.setWorkbasketSummary(destinationWorkbasket.asSummary());
            task.setModified(Instant.now());
            task.setState(TaskState.READY);
            task.setOwner(null);
            taskMapper.update(task);
            LOGGER.debug("Method transfer() transferred Task '{}' to destination workbasket {}", taskId,
                destinationWorkbasket.getId());
            return task;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from transfer(). Returning result {} ", task);
        }
    }

    @Override
    public BulkOperationResults<String, TaskanaException> transferTasks(String destinationWorkbasketId,
        List<String> taskIds) throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
        try {
            taskanaEngine.openConnection();
            LOGGER.debug("entry to transferBulk(targetWbId = {}, taskIds = {})", destinationWorkbasketId, taskIds);
            // Check pre-conditions with trowing Exceptions
            if (destinationWorkbasketId == null || destinationWorkbasketId.isEmpty()) {
                throw new InvalidArgumentException(
                    "DestinationWorkbasketId must not be null or empty.");
            }
            Workbasket destinationWorkbasket = workbasketService.getWorkbasket(destinationWorkbasketId);

            return transferTasks(taskIds, destinationWorkbasket);
        } finally {
            LOGGER.debug("exit from transferBulk(targetWbKey = {}, taskIds = {})", destinationWorkbasketId, taskIds);
            taskanaEngine.returnConnection();
        }
    }

    @Override
    public BulkOperationResults<String, TaskanaException> transferTasks(String destinationWorkbasketKey,
        String destinationWorkbasketDomain, List<String> taskIds)
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
        try {
            taskanaEngine.openConnection();
            LOGGER.debug("entry to transferBulk(targetWbKey = {}, domain = {}, taskIds = {})", destinationWorkbasketKey,
                destinationWorkbasketDomain, taskIds);
            // Check pre-conditions with trowing Exceptions
            if (destinationWorkbasketKey == null || destinationWorkbasketDomain == null) {
                throw new InvalidArgumentException(
                    "DestinationWorkbasketKey or domain can´t be used as NULL-Parameter.");
            }
            Workbasket destinationWorkbasket = workbasketService.getWorkbasket(destinationWorkbasketKey,
                destinationWorkbasketDomain);

            return transferTasks(taskIds, destinationWorkbasket);
        } finally {
            LOGGER.debug("exit from transferBulk(targetWbKey = {}, taskIds = {})", destinationWorkbasketKey,
                destinationWorkbasketDomain, taskIds);
            taskanaEngine.returnConnection();
        }
    }

    private BulkOperationResults<String, TaskanaException> transferTasks(List<String> taskIdsToBeTransferred,
        Workbasket destinationWorkbasket)
        throws InvalidArgumentException, WorkbasketNotFoundException, NotAuthorizedException {

        workbasketService.checkAuthorization(destinationWorkbasket.getId(), WorkbasketPermission.APPEND);

        // Check pre-conditions with trowing Exceptions
        if (taskIdsToBeTransferred == null) {
            throw new InvalidArgumentException("TaskIds must not be null.");
        }
        BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

        // convert to ArrayList<String> if necessary to prevent a UnsupportedOperationException while removing
        List<String> taskIds = new ArrayList<>(taskIdsToBeTransferred);

        // check tasks Ids exist and not empty - log and remove
        Iterator<String> taskIdIterator = taskIds.iterator();
        while (taskIdIterator.hasNext()) {
            String currentTaskId = taskIdIterator.next();
            if (currentTaskId == null || currentTaskId.equals("")) {
                bulkLog.addError("",
                    new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed."));
                taskIdIterator.remove();
            }
        }

        // Check pre-conditions with trowing Exceptions after removing invalid invalid arguments.
        if (taskIds.isEmpty()) {
            throw new InvalidArgumentException("TaskIds must not contain only invalid arguments.");
        }

        // query for existing tasks. use taskMapper.findExistingTasks because this method
        // returns only the required information.
        List<MinimalTaskSummary> taskSummaries;
        if (taskIds.isEmpty()) {
            taskSummaries = new ArrayList<>();
        } else {
            taskSummaries = taskMapper.findExistingTasks(taskIds);
        }
        // check source WB (read)+transfer
        Set<String> workbasketIds = new HashSet<>();
        taskSummaries.forEach(t -> workbasketIds.add(t.getWorkbasketId()));
        WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
        query.setUsedToAugmentTasks(true);
        List<WorkbasketSummary> sourceWorkbaskets;
        if (taskSummaries.isEmpty()) {
            sourceWorkbaskets = new ArrayList<>();
        } else {
            sourceWorkbaskets = query
                .callerHasPermission(WorkbasketPermission.TRANSFER)
                .idIn(workbasketIds.toArray(new String[0]))
                .list();
        }
        taskIdIterator = taskIds.iterator();
        while (taskIdIterator.hasNext()) {
            String currentTaskId = taskIdIterator.next();
            MinimalTaskSummary taskSummary = taskSummaries.stream()
                .filter(t -> currentTaskId.equals(t.getTaskId()))
                .findFirst()
                .orElse(null);
            if (taskSummary == null) {
                bulkLog.addError(currentTaskId,
                    new TaskNotFoundException(currentTaskId, "Task with id " + currentTaskId + " was not found."));
                taskIdIterator.remove();
            } else if (taskSummary.getTaskState() == TaskState.COMPLETED) {
                bulkLog.addError(currentTaskId,
                    new InvalidStateException("Completed task with id " + currentTaskId + " cannot be transferred."));
                taskIdIterator.remove();
            } else if (sourceWorkbaskets.stream()
                .noneMatch(wb -> taskSummary.getWorkbasketId().equals(wb.getId()))) {
                bulkLog.addError(currentTaskId,
                    new NotAuthorizedException(
                        "The workbasket of this task got not TRANSFER permissions. TaskId=" + currentTaskId));
                taskIdIterator.remove();
            }
        }

        // filter taskSummaries and update values
        taskSummaries = taskSummaries.stream().filter(ts -> taskIds.contains(ts.getTaskId())).collect(
            Collectors.toList());
        if (!taskSummaries.isEmpty()) {
            Instant now = Instant.now();
            TaskSummaryImpl updateObject = new TaskSummaryImpl();
            updateObject.setRead(false);
            updateObject.setTransferred(true);
            updateObject.setWorkbasketSummary(destinationWorkbasket.asSummary());
            updateObject.setDomain(destinationWorkbasket.getDomain());
            updateObject.setModified(now);
            updateObject.setState(TaskState.READY);
            updateObject.setOwner(null);
            taskMapper.updateTransfered(taskIds, updateObject);
        }
        return bulkLog;
    }

    @Override
    public Task setTaskRead(String taskId, boolean isRead)
        throws TaskNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to setTaskRead(taskId = {}, isRead = {})", taskId, isRead);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) getTask(taskId);
            task.setRead(isRead);
            task.setModified(Instant.now());
            taskMapper.update(task);
            LOGGER.debug("Method setTaskRead() set read property of Task '{}' to {} ", task, isRead);
            return task;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from setTaskRead(taskId, isRead). Returning result {} ", task);
        }
    }

    @Override
    public TaskQuery createTaskQuery() {
        return new TaskQueryImpl(taskanaEngine);
    }

    @Override
    public Task updateTask(Task task)
        throws InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
        ClassificationNotFoundException, NotAuthorizedException, AttachmentPersistenceException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to updateTask(task = {}, userId = {})", task, userId);
        TaskImpl newTaskImpl = (TaskImpl) task;
        TaskImpl oldTaskImpl = null;
        try {
            taskanaEngine.openConnection();
            oldTaskImpl = (TaskImpl) getTask(newTaskImpl.getId());
            PrioDurationHolder prioDurationFromAttachments = handleAttachmentsOnTaskUpdate(oldTaskImpl, newTaskImpl);
            standardUpdateActions(oldTaskImpl, newTaskImpl, prioDurationFromAttachments);

            taskMapper.update(newTaskImpl);
            LOGGER.debug("Method updateTask() updated task '{}' for user '{}'.", task.getId(), userId);

        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from claim()");
        }
        return task;
    }

    private void standardSettings(TaskImpl task, Classification classification,
        PrioDurationHolder prioDurationFromAttachments) {
        Instant now = Instant.now();
        task.setId(IdGenerator.generateWithPrefix(ID_PREFIX_TASK));
        task.setState(TaskState.READY);
        task.setCreated(now);
        task.setModified(now);
        task.setRead(false);
        task.setTransferred(false);

        String creator = CurrentUserContext.getUserid();
        if (taskanaEngine.getConfiguration().isSecurityEnabled()) {
            if (creator == null) {
                throw new SystemException(
                    "TaskanaSecurity is enabled, but the current UserId is NULL while creating a Task.");
            }
        }
        task.setCreator(creator);

        if (task.getPlanned() == null) {
            task.setPlanned(now);
        }

        // if no business process id is provided, a unique id is created.
        if (task.getBusinessProcessId() == null) {
            task.setBusinessProcessId(IdGenerator.generateWithPrefix(ID_PREFIX_BUSINESS_PROCESS));
        }

        // insert Classification specifications if Classification is given.

        if (classification != null) {
            PrioDurationHolder finalPrioDuration = getNewPrioDuration(prioDurationFromAttachments.getPrio(),
                prioDurationFromAttachments.getDuration(),
                classification.getPriority(), classification.getServiceLevel());
            Duration finalDuration = finalPrioDuration.getDuration();
            if (finalDuration != null && !MAX_DURATION.equals(finalDuration)) {
                long days = converter.convertWorkingDaysToDays(task.getPlanned(), finalDuration.toDays());
                Instant due = task.getPlanned().plus(Duration.ofDays(days));
                task.setDue(due);
            }
            task.setPriority(finalPrioDuration.getPrio());

        }

        if (task.getName() == null) {
            task.setName(classification.getName());
        }

        if (task.getDescription() == null) {
            task.setDescription(classification.getDescription());
        }

        // insert Attachments if needed
        List<Attachment> attachments = task.getAttachments();
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
                attachmentImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
                attachmentImpl.setTaskId(task.getId());
                attachmentImpl.setCreated(now);
                attachmentImpl.setModified(now);
                attachmentMapper.insert(attachmentImpl);
            }
        }
    }

    List<TaskSummary> augmentTaskSummariesByContainedSummaries(List<TaskSummaryImpl> taskSummaries) {
        LOGGER.debug("entry to augmentTaskSummariesByContainedSummaries()");
        List<TaskSummary> result = new ArrayList<>();
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return result;
        }

        Set<String> taskIdSet = taskSummaries.stream().map(TaskSummaryImpl::getTaskId).collect(Collectors.toSet());
        String[] taskIdArray = taskIdSet.toArray(new String[0]);

        LOGGER.debug("augmentTaskSummariesByContainedSummaries() about to query for attachmentSummaries ");
        List<AttachmentSummaryImpl> attachmentSummaries = attachmentMapper
            .findAttachmentSummariesByTaskIds(taskIdArray);

        List<ClassificationSummary> classifications = findClassificationsForTasksAndAttachments(taskSummaries,
            attachmentSummaries);

        addClassificationSummariesToTaskSummaries(taskSummaries, classifications);
        addWorkbasketSummariesToTaskSummaries(taskSummaries);
        addAttachmentSummariesToTaskSummaries(taskSummaries, attachmentSummaries, classifications);
        result.addAll(taskSummaries);
        LOGGER.debug("exit from to augmentTaskSummariesByContainedSummaries()");
        return result;
    }

    private void addClassificationSummariesToTaskSummaries(List<TaskSummaryImpl> tasks,
        List<ClassificationSummary> classifications) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        // assign query results to appropriate tasks.
        for (TaskSummaryImpl task : tasks) {
            String classificationId = task.getClassificationSummary().getId();
            ClassificationSummary aClassification = classifications.stream()
                .filter(c -> c.getId().equals(classificationId))
                .findFirst()
                .orElse(null);
            if (aClassification == null) {
                throw new SystemException(
                    "Did not find a Classification for task (Id=" + task.getTaskId() + ",classification="
                        + task.getClassificationSummary().getId() + ")");
            }
            // set the classification on the task object
            task.setClassificationSummary(aClassification);
        }
    }

    private List<ClassificationSummary> findClassificationsForTasksAndAttachments(
        List<TaskSummaryImpl> taskSummaries, List<AttachmentSummaryImpl> attachmentSummaries) {
        LOGGER.debug("entry to getClassificationsForTasksAndAttachments()");
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> classificationIdSet = taskSummaries.stream().map(t -> t.getClassificationSummary().getId()).collect(
            Collectors.toSet());

        if (attachmentSummaries != null && !attachmentSummaries.isEmpty()) {
            for (AttachmentSummaryImpl att : attachmentSummaries) {
                classificationIdSet.add(att.getClassificationSummary().getId());
            }
        }
        return queryClassificationsForTasksAndAttachments(classificationIdSet);
    }

    private List<ClassificationSummary> findClassificationForTaskImplAndAttachments(TaskImpl task,
        List<AttachmentImpl> attachmentImpls) {

        Set<String> classificationIdSet = new HashSet<>(Arrays.asList(task.getClassificationSummary().getId()));
        if (attachmentImpls != null && !attachmentImpls.isEmpty()) {
            for (AttachmentImpl att : attachmentImpls) {
                classificationIdSet.add(att.getClassificationSummary().getId());
            }
        }

        return queryClassificationsForTasksAndAttachments(classificationIdSet);

    }

    private List<ClassificationSummary> queryClassificationsForTasksAndAttachments(Set<String> classificationIdSet) {

        String[] classificationIdArray = classificationIdSet.toArray(new String[0]);

        LOGGER.debug("getClassificationsForTasksAndAttachments() about to query classifications and exit");
        // perform classification query
        return this.classificationService.createClassificationQuery()
            .idIn(classificationIdArray)
            .list();
    }

    private void addWorkbasketSummariesToTaskSummaries(List<TaskSummaryImpl> taskSummaries) {
        LOGGER.debug("entry to addWorkbasketSummariesToTaskSummaries()");
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return;
        }
        // calculate parameters for workbasket query: workbasket keys
        Set<String> workbasketIdSet = taskSummaries.stream().map(t -> t.getWorkbasketSummary().getId()).collect(
            Collectors.toSet());
        String[] workbasketIdArray = workbasketIdSet.toArray(new String[0]);
        // perform workbasket query
        LOGGER.debug("addWorkbasketSummariesToTaskSummaries() about to query workbaskets");
        WorkbasketQueryImpl query = (WorkbasketQueryImpl) workbasketService.createWorkbasketQuery();
        query.setUsedToAugmentTasks(true);

        List<WorkbasketSummary> workbaskets = query
            .idIn(workbasketIdArray)
            .list();
        // assign query results to appropriate tasks.
        Iterator<TaskSummaryImpl> taskIterator = taskSummaries.iterator();
        while (taskIterator.hasNext()) {
            TaskSummaryImpl task = taskIterator.next();
            String workbasketId = task.getWorkbasketSummaryImpl().getId();

            // find the appropriate workbasket from the query result
            WorkbasketSummary aWorkbasket = workbaskets.stream()
                .filter(x -> workbasketId != null && workbasketId.equals(x.getId()))
                .findFirst()
                .orElse(null);
            if (aWorkbasket == null) {
                LOGGER.warn("Could not find a Workbasket for task {}.", task.getTaskId());
                taskIterator.remove();
                continue;
            }
            // set the classification on the task object
            task.setWorkbasketSummary(aWorkbasket);
        }
        LOGGER.debug("exit from addWorkbasketSummariesToTaskSummaries()");
    }

    private void addAttachmentSummariesToTaskSummaries(List<TaskSummaryImpl> taskSummaries,
        List<AttachmentSummaryImpl> attachmentSummaries, List<ClassificationSummary> classifications) {
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return;
        }

        // augment attachment summaries by classification summaries
        // Note:
        // the mapper sets for each Attachment summary the property classificationSummary.key from the
        // CLASSIFICATION_KEY property in the DB
        addClassificationSummariesToAttachmentSummaries(attachmentSummaries, taskSummaries, classifications);
        // assign attachment summaries to task summaries
        for (TaskSummaryImpl task : taskSummaries) {
            for (AttachmentSummaryImpl attachment : attachmentSummaries) {
                if (attachment.getTaskId() != null && attachment.getTaskId().equals(task.getTaskId())) {
                    task.addAttachmentSummary(attachment);
                }
            }
        }

    }

    private void addClassificationSummariesToAttachmentSummaries(List<AttachmentSummaryImpl> attachmentSummaries,
        List<TaskSummaryImpl> taskSummaries, List<ClassificationSummary> classifications) {
        // prereq: in each attachmentSummary, the classificationSummary.key property is set.
        if (attachmentSummaries == null || attachmentSummaries.isEmpty() || taskSummaries == null
            || taskSummaries.isEmpty()) {
            return;
        }
        // iterate over all attachment summaries an add the appropriate classification summary to each
        for (AttachmentSummaryImpl att : attachmentSummaries) {
            String classificationId = att.getClassificationSummary().getId();
            ClassificationSummary aClassification = classifications.stream()
                .filter(x -> classificationId != null && classificationId.equals(x.getId()))
                .findFirst()
                .orElse(null);
            if (aClassification == null) {
                throw new SystemException("Could not find a Classification for attachment " + att);
            }
            att.setClassificationSummary(aClassification);
        }
    }

    private List<Attachment> addClassificationSummariesToAttachments(TaskImpl task,
        List<AttachmentImpl> attachmentImpls, List<ClassificationSummary> classifications) {
        if (attachmentImpls == null || attachmentImpls.isEmpty()) {
            return new ArrayList<>();
        }

        List<Attachment> result = new ArrayList<>();
        for (AttachmentImpl att : attachmentImpls) {
            // find the associated task to use the correct domain
            ClassificationSummary aClassification = classifications.stream()
                .filter(c -> c != null & c.getId().equals(att.getClassificationSummary().getId()))
                .findFirst()
                .orElse(null);

            if (aClassification == null) {
                throw new SystemException("Could not find a Classification for attachment " + att);
            }
            att.setClassificationSummary(aClassification);
            result.add(att);
        }
        return result;
    }

    @Override
    public Task newTask(String workbasketId) {
        TaskImpl task = new TaskImpl();
        WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
        wb.setId(workbasketId);
        task.setWorkbasketSummary(wb);
        return task;
    }

    @Override
    public Task newTask(String workbasketKey, String domain) {
        TaskImpl task = new TaskImpl();
        WorkbasketSummaryImpl wb = new WorkbasketSummaryImpl();
        wb.setKey(workbasketKey);
        wb.setDomain(domain);
        task.setWorkbasketSummary(wb);
        return task;
    }

    @Override
    public Attachment newAttachment() {
        return new AttachmentImpl();
    }

    @Override
    public void deleteTask(String taskId) throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        deleteTask(taskId, false);
    }

    @Override
    public void forceDeleteTask(String taskId)
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        deleteTask(taskId, true);
    }

    private void deleteTask(String taskId, boolean forceDelete)
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        LOGGER.debug("entry to deleteTask(taskId = {} , forceDelete = {})", taskId, forceDelete);
        taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN);
        TaskImpl task = null;
        try {
            taskanaEngine.openConnection();
            task = (TaskImpl) getTask(taskId);

            if (!TaskState.COMPLETED.equals(task.getState()) && !forceDelete) {
                throw new InvalidStateException("Cannot delete Task " + taskId + " because it is not completed.");
            }
            taskMapper.delete(taskId);
            LOGGER.debug("Task {} deleted.", taskId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteTask().");
        }
    }

    @Override
    public BulkOperationResults<String, TaskanaException> deleteTasks(List<String> taskIds)
        throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to deleteTasks(tasks = {})", LoggerUtils.listToString(taskIds));
        }
        try {
            taskanaEngine.openConnection();
            if (taskIds == null) {
                throw new InvalidArgumentException("List of TaskIds must not be null.");
            }

            BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

            if (taskIds.isEmpty()) {
                return bulkLog;
            }

            List<MinimalTaskSummary> taskSummaries = taskMapper.findExistingTasks(taskIds);

            Iterator<String> taskIdIterator = taskIds.iterator();
            while (taskIdIterator.hasNext()) {
                String currentTaskId = taskIdIterator.next();
                if (currentTaskId == null || currentTaskId.equals("")) {
                    bulkLog.addError("",
                        new InvalidArgumentException("IDs with EMPTY or NULL value are not allowed."));
                    taskIdIterator.remove();
                } else {
                    MinimalTaskSummary foundSummary = taskSummaries.stream()
                        .filter(taskState -> currentTaskId.equals(taskState.getTaskId()))
                        .findFirst()
                        .orElse(null);
                    if (foundSummary == null) {
                        bulkLog.addError(currentTaskId, new TaskNotFoundException(currentTaskId,
                            "Task with id " + currentTaskId + " was not found."));
                        taskIdIterator.remove();
                    } else {
                        if (!TaskState.COMPLETED.equals(foundSummary.getTaskState())) {
                            bulkLog.addError(currentTaskId, new InvalidStateException(currentTaskId));
                            taskIdIterator.remove();
                        }
                    }
                }
            }
            if (!taskIds.isEmpty()) {
                taskMapper.deleteMultiple(taskIds);
            }
            return bulkLog;
        } finally {
            LOGGER.debug("exit from deleteTasks()");
            taskanaEngine.returnConnection();
        }
    }

    @Override
    public List<String> updateTasks(ObjectReference selectionCriteria,
        Map<String, String> customFieldsToUpdate) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to updateTasks(selectionCriteria = {}, customFieldsToUpdate = {})", selectionCriteria,
                customFieldsToUpdate);
        }

        if (customFieldsToUpdate == null || customFieldsToUpdate.isEmpty()) {
            throw new InvalidArgumentException("The customFieldsToUpdate argument to updateTasks must not be empty.");
        }
        validateObjectReference(selectionCriteria, "ObjectReference", "updateTasks call");

        Set<String> allowedKeys = new HashSet<>(
            Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"));

        try {
            taskanaEngine.openConnection();

            CustomPropertySelector fieldSelector = new CustomPropertySelector();
            TaskImpl newTask = new TaskImpl();
            newTask.setModified(Instant.now());
            for (Map.Entry<String, String> entry : customFieldsToUpdate.entrySet()) {
                String key = entry.getKey();
                if (!allowedKeys.contains(key)) {
                    throw new InvalidArgumentException(
                        "The customFieldsToUpdate argument to updateTasks contains invalid key " + key);
                } else {
                    fieldSelector.setCustomProperty(key, true);
                    newTask.setCustomAttribute(key, entry.getValue());
                }
            }

            // use query in order to find only those tasks that are visible to the current user
            List<TaskSummary> taskSummaries = createTaskQuery()
                .primaryObjectReferenceCompanyIn(selectionCriteria.getCompany())
                .primaryObjectReferenceSystemIn(selectionCriteria.getSystem())
                .primaryObjectReferenceSystemInstanceIn(selectionCriteria.getSystemInstance())
                .primaryObjectReferenceTypeIn(selectionCriteria.getType())
                .primaryObjectReferenceValueIn(selectionCriteria.getValue())
                .list();

            List<String> taskIds = new ArrayList<>();
            if (!taskSummaries.isEmpty()) {
                taskIds = taskSummaries.stream().map(TaskSummary::getTaskId).collect(Collectors.toList());
                taskMapper.updateTasks(taskIds, newTask, fieldSelector);
                LOGGER.debug("updateTasks() updated the following tasks: {} ",
                    LoggerUtils.listToString(taskIds));
            } else {
                LOGGER.debug("updateTasks() found no tasks for update ");
            }
            return taskIds;
        } finally {
            LOGGER.debug("exit from deleteTasks().");
            taskanaEngine.returnConnection();
        }

    }

    private void validateObjectReference(ObjectReference objRef, String objRefType, String objName)
        throws InvalidArgumentException {
        // check that all values in the ObjectReference are set correctly
        if (objRef == null) {
            throw new InvalidArgumentException(objRefType + " of " + objName + " must not be null");
        } else if (objRef.getCompany() == null || objRef.getCompany().length() == 0) {
            throw new InvalidArgumentException(
                "Company of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
        } else if (objRef.getSystem() == null || objRef.getSystem().length() == 0) {
            throw new InvalidArgumentException(
                "System of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
        } else if (objRef.getSystemInstance() == null || objRef.getSystemInstance().length() == 0) {
            throw new InvalidArgumentException(
                "SystemInstance of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
        } else if (objRef.getType() == null || objRef.getType().length() == 0) {
            throw new InvalidArgumentException("Type of " + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
        } else if (objRef.getValue() == null || objRef.getValue().length() == 0) {
            throw new InvalidArgumentException("Value of" + objRefType + " of " + objName + MUST_NOT_BE_EMPTY);
        }
    }

    private PrioDurationHolder handleAttachments(TaskImpl task) throws InvalidArgumentException {
        List<Attachment> attachments = task.getAttachments();
        if (attachments == null || attachments.isEmpty()) {
            return new PrioDurationHolder(null, Integer.MIN_VALUE);
        }
        Duration minDuration = MAX_DURATION;
        int maxPrio = Integer.MIN_VALUE;

        Iterator<Attachment> i = attachments.iterator();
        while (i.hasNext()) {
            Attachment attachment = i.next();
            if (attachment == null) {
                i.remove();
            } else {
                ObjectReference objRef = attachment.getObjectReference();
                validateObjectReference(objRef, "ObjectReference", "Attachment");
                if (attachment.getClassificationSummary() == null) {
                    throw new InvalidArgumentException(
                        "Classification of attachment " + attachment + " must not be null");
                } else {
                    ClassificationSummary classificationSummary = attachment.getClassificationSummary();
                    if (classificationSummary != null) {
                        PrioDurationHolder newPrioDuraton = getNewPrioDuration(maxPrio, minDuration,
                            classificationSummary.getPriority(), classificationSummary.getServiceLevel());
                        maxPrio = newPrioDuraton.getPrio();
                        minDuration = newPrioDuraton.getDuration();
                    }
                }
            }
        }
        if (minDuration != null && MAX_DURATION.equals(minDuration)) {
            minDuration = null;
        }

        return new PrioDurationHolder(minDuration, maxPrio);
    }

    private void standardUpdateActions(TaskImpl oldTaskImpl, TaskImpl newTaskImpl,
        PrioDurationHolder prioDurationFromAttachments)
        throws InvalidArgumentException, ConcurrencyException, ClassificationNotFoundException {
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

        updateClassificationRelatedProperties(oldTaskImpl, newTaskImpl, prioDurationFromAttachments);

        newTaskImpl.setModified(Instant.now());
    }

    private void updateClassificationRelatedProperties(TaskImpl oldTaskImpl, TaskImpl newTaskImpl,
        PrioDurationHolder prioDurationFromAttachments)
        throws ClassificationNotFoundException {
        // insert Classification specifications if Classification is given.
        ClassificationSummary oldClassificationSummary = oldTaskImpl.getClassificationSummary();
        ClassificationSummary newClassificationSummary = newTaskImpl.getClassificationSummary();
        if (newClassificationSummary == null) {
            newClassificationSummary = oldClassificationSummary;
        }

        if (newClassificationSummary == null) { // newClassification is null -> take prio and duration from attachments
            updateTaskPrioDurationFromAttachments(newTaskImpl, prioDurationFromAttachments);
        } else {
            Classification newClassification = null;
            if (!oldClassificationSummary.getKey().equals(newClassificationSummary.getKey())) {
                newClassification = this.classificationService
                    .getClassification(newClassificationSummary.getKey(),
                        newTaskImpl.getWorkbasketSummary().getDomain());
                newClassificationSummary = newClassification.asSummary();
                newTaskImpl.setClassificationSummary(newClassificationSummary);
            }

            if (newClassificationSummary.getServiceLevel() != null) {
                Duration durationFromClassification = Duration.parse(newClassificationSummary.getServiceLevel());
                Duration minDuration = prioDurationFromAttachments.getDuration();
                if (minDuration != null) {
                    if (minDuration.compareTo(durationFromClassification) > 0) {
                        minDuration = durationFromClassification;
                    }
                } else {
                    minDuration = durationFromClassification;
                }

                long days = converter.convertWorkingDaysToDays(newTaskImpl.getPlanned(), minDuration.toDays());
                Instant due = newTaskImpl.getPlanned().plus(Duration.ofDays(days));

                newTaskImpl.setDue(due);
            }

            if (newTaskImpl.getName() == null) {
                newTaskImpl.setName(newClassificationSummary.getName());
            }

            if (newTaskImpl.getDescription() == null && newClassification != null) {
                newTaskImpl.setDescription(newClassification.getDescription());
            }

            int newPriority = Math.max(newClassificationSummary.getPriority(), prioDurationFromAttachments.getPrio());
            newTaskImpl.setPriority(newPriority);

        }

    }

    private PrioDurationHolder handleAttachmentsOnTaskUpdate(TaskImpl oldTaskImpl, TaskImpl newTaskImpl)
        throws AttachmentPersistenceException {

        Duration minDuration = MAX_DURATION;
        int maxPrio = Integer.MIN_VALUE;

        // Iterator for removing invalid current values directly. OldAttachments can be ignored.
        Iterator<Attachment> i = newTaskImpl.getAttachments().iterator();
        while (i.hasNext()) {
            Attachment attachment = i.next();
            if (attachment != null) {
                boolean wasAlreadyPresent = false;
                if (attachment.getId() != null) {
                    for (Attachment oldAttachment : oldTaskImpl.getAttachments()) {
                        if (oldAttachment != null && attachment.getId().equals(oldAttachment.getId())) {
                            wasAlreadyPresent = true;
                            if (!attachment.equals(oldAttachment)) {
                                AttachmentImpl temp = (AttachmentImpl) attachment;

                                ClassificationSummary classification = attachment.getClassificationSummary();
                                if (classification != null) {
                                    PrioDurationHolder newPrioDuration = getNewPrioDuration(maxPrio, minDuration,
                                        classification.getPriority(), classification.getServiceLevel());
                                    maxPrio = newPrioDuration.getPrio();
                                    minDuration = newPrioDuration.getDuration();
                                }

                                temp.setModified(Instant.now());
                                attachmentMapper.update(temp);
                                LOGGER.debug("TaskService.updateTask() for TaskId={} UPDATED an Attachment={}.",
                                    newTaskImpl.getId(),
                                    attachment);
                                break;
                            }

                        }
                    }
                }

                // ADD, when ID not set or not found in elements
                if (!wasAlreadyPresent) {
                    AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
                    initAttachment(attachmentImpl, newTaskImpl);
                    ClassificationSummary classification = attachment.getClassificationSummary();
                    if (classification != null) {
                        PrioDurationHolder newPrioDuration = getNewPrioDuration(maxPrio, minDuration,
                            classification.getPriority(), classification.getServiceLevel());
                        maxPrio = newPrioDuration.getPrio();
                        minDuration = newPrioDuration.getDuration();
                    }

                    try {
                        attachmentMapper.insert(attachmentImpl);
                        LOGGER.debug("TaskService.updateTask() for TaskId={} INSERTED an Attachment={}.",
                            newTaskImpl.getId(),
                            attachmentImpl);
                    } catch (PersistenceException e) {
                        throw new AttachmentPersistenceException(
                            "Cannot insert the Attachement " + attachmentImpl.getId() + " for Task "
                                + newTaskImpl.getId() + " because it already exists.",
                            e.getCause());
                    }

                }
            } else {
                i.remove();
            }
        }

        // DELETE, when an Attachment was only represented before
        for (Attachment oldAttachment : oldTaskImpl.getAttachments()) {
            if (oldAttachment != null) {
                boolean isRepresented = false;
                for (Attachment newAttachment : newTaskImpl.getAttachments()) {
                    if (newAttachment != null && oldAttachment.getId().equals(newAttachment.getId())) {
                        isRepresented = true;
                        break;
                    }
                }
                if (!isRepresented) {
                    attachmentMapper.deleteAttachment(oldAttachment.getId());
                    LOGGER.debug("TaskService.updateTask() for TaskId={} DELETED an Attachment={}.",
                        newTaskImpl.getId(),
                        oldAttachment);
                }
            }
        }
        if (minDuration != null && MAX_DURATION.equals(minDuration)) {
            minDuration = null;
        }
        return new PrioDurationHolder(minDuration, maxPrio);
    }

    private PrioDurationHolder handleAttachmentsOnClassificationUpdate(Task task) {

        Duration minDuration = MAX_DURATION;
        int maxPrio = Integer.MIN_VALUE;

        // Iterator for removing invalid current values directly. OldAttachments can be ignored.
        Iterator<Attachment> i = task.getAttachments().iterator();
        while (i.hasNext()) {
            Attachment attachment = i.next();
            if (attachment != null) {
                ClassificationSummary classification = attachment.getClassificationSummary();
                if (classification != null) {
                    PrioDurationHolder newPrioDuration = getNewPrioDuration(maxPrio, minDuration,
                        classification.getPriority(), classification.getServiceLevel());
                    maxPrio = newPrioDuration.getPrio();
                    minDuration = newPrioDuration.getDuration();
                }

            }
        }
        if (minDuration != null && MAX_DURATION.equals(minDuration)) {
            minDuration = null;
        }
        return new PrioDurationHolder(minDuration, maxPrio);
    }

    private PrioDurationHolder getNewPrioDuration(int prio, Duration duration, int prioFromClassification,
        String serviceLevelFromClassification) {
        Duration minDuration = duration;
        int maxPrio = prio;

        if (serviceLevelFromClassification != null) {
            Duration currentDuration = Duration.parse(serviceLevelFromClassification);
            if (duration != null) {
                if (duration.compareTo(currentDuration) > 0) {
                    minDuration = currentDuration;
                }
            } else {
                minDuration = currentDuration;
            }
        }
        if (prioFromClassification > maxPrio) {
            maxPrio = prioFromClassification;
        }

        return new PrioDurationHolder(minDuration, maxPrio);
    }

    private void initAttachment(AttachmentImpl attachment, Task newTask) {
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
    }

    public void refreshPriorityAndDueDate(String taskId)
        throws ClassificationNotFoundException {
        LOGGER.debug("entry to refreshPriorityAndDueDate(taskId = {})", taskId);
        TaskImpl task = null;
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        try {
            taskanaEngine.openConnection();
            if (taskId == null || taskId.isEmpty()) {
                return;
            }

            task = taskMapper.findById(taskId);

            List<AttachmentImpl> attachmentImpls = attachmentMapper.findAttachmentsByTaskId(task.getId());
            if (attachmentImpls == null) {
                attachmentImpls = new ArrayList<>();
            }
            List<Attachment> attachments = augmentAttachmentsByClassification(attachmentImpls, bulkLog);
            task.setAttachments(attachments);

            Classification classification = classificationService
                .getClassification(task.getClassificationSummary().getId());
            task.setClassificationSummary(classification.asSummary());
            PrioDurationHolder prioDurationFromAttachments = handleAttachmentsOnClassificationUpdate(task);

            updatePrioDueDateOnClassificationUpdate(task, prioDurationFromAttachments);

            task.setModified(Instant.now());
            taskMapper.update(task);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from refreshPriorityAndDueDate(). ");
        }

    }

    private void updatePrioDueDateOnClassificationUpdate(TaskImpl task,
        PrioDurationHolder prioDurationFromAttachments) {
        ClassificationSummary classificationSummary = task.getClassificationSummary();

        if (classificationSummary == null) { // classification is null -> take prio and duration from attachments
            updateTaskPrioDurationFromAttachments(task, prioDurationFromAttachments);
        } else {
            updateTaskPrioDurationFromClassificationAndAttachments(task, prioDurationFromAttachments,
                classificationSummary);
        }

    }

    private void updateTaskPrioDurationFromClassificationAndAttachments(TaskImpl task,
        PrioDurationHolder prioDurationFromAttachments, ClassificationSummary classificationSummary) {
        if (classificationSummary.getServiceLevel() != null) {
            Duration durationFromClassification = Duration.parse(classificationSummary.getServiceLevel());
            Duration minDuration = prioDurationFromAttachments.getDuration();
            if (minDuration != null) {
                if (minDuration.compareTo(durationFromClassification) > 0) {
                    minDuration = durationFromClassification;
                }
            } else {
                minDuration = durationFromClassification;
            }

            long days = converter.convertWorkingDaysToDays(task.getPlanned(), minDuration.toDays());
            Instant due = task.getPlanned().plus(Duration.ofDays(days));

            task.setDue(due);
        }

        int newPriority = Math.max(classificationSummary.getPriority(), prioDurationFromAttachments.getPrio());
        task.setPriority(newPriority);
    }

    private void updateTaskPrioDurationFromAttachments(TaskImpl task, PrioDurationHolder prioDurationFromAttachments) {
        if (prioDurationFromAttachments.getDuration() != null) {
            long days = converter.convertWorkingDaysToDays(task.getPlanned(),
                prioDurationFromAttachments.getDuration().toDays());
            Instant due = task.getPlanned().plus(Duration.ofDays(days));
            task.setDue(due);
        }
        if (prioDurationFromAttachments.getPrio() > Integer.MIN_VALUE) {
            task.setPriority(prioDurationFromAttachments.getPrio());
        }
    }

    private List<Attachment> augmentAttachmentsByClassification(List<AttachmentImpl> attachmentImpls,
        BulkOperationResults<String, Exception> bulkLog) {
        List<Attachment> result = new ArrayList<>();
        if (attachmentImpls == null || attachmentImpls.isEmpty()) {
            return result;
        }
        Set<String> classificationIds = attachmentImpls.stream().map(t -> t.getClassificationSummary().getId()).collect(
            Collectors.toSet());
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .idIn(classificationIds.toArray(new String[0]))
            .list();
        for (AttachmentImpl att : attachmentImpls) {
            ClassificationSummary classificationSummary = classifications.stream()
                .filter(cl -> cl.getId().equals(att.getClassificationSummary().getId()))
                .findFirst()
                .orElse(null);
            if (classificationSummary == null) {
                String id = att.getClassificationSummary().getId();
                bulkLog.addError(att.getClassificationSummary().getId(), new ClassificationNotFoundException(id,
                    "When processing task updates due to change of classification, the classification with id " + id
                        + " was not found."));
            } else {
                att.setClassificationSummary(classificationSummary);
                result.add(att);
            }
        }

        return result;
    }

    /**
     * hold a pair of priority and Duration.
     *
     * @author bbr
     */
    static class PrioDurationHolder {

        private Duration duration;

        private int prio;

        PrioDurationHolder(Duration duration, int prio) {
            super();
            this.duration = duration;
            this.prio = prio;
        }

        public Duration getDuration() {
            return duration;
        }

        public int getPrio() {
            return prio;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PrioDurationHolder [duration=");
            builder.append(duration);
            builder.append(", prio=");
            builder.append(prio);
            builder.append("]");
            return builder.toString();
        }
    }

}
