package pro.taskana.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Attachment;
import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.AttachmentPersistenceException;
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
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.AttachmentMapper;
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
    private static final String MUST_NOT_BE_EMPTY = " must not be empty";
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private WorkbasketService workbasketService;
    private ClassificationServiceImpl classificationService;
    private TaskMapper taskMapper;
    private AttachmentMapper attachmentMapper;

    public TaskServiceImpl(TaskanaEngine taskanaEngine, TaskMapper taskMapper,
        AttachmentMapper attachmentMapper) {
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
            Instant now = Instant.now();
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
            Instant now = Instant.now();
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
                workbasketService.checkAuthorization(task.getWorkbasketKey(),
                    WorkbasketAuthorization.APPEND);
                String classificationKey = task.getClassificationKey();
                if (classificationKey == null || classificationKey.length() == 0) {
                    throw new InvalidArgumentException("classificationKey of task must not be empty");
                }
                Classification classification = this.classificationService.getClassification(classificationKey,
                    workbasket.getDomain());
                task.setClassificationSummary(classification.asSummary());
                task.setWorkbasketSummary(workbasket.asSummary());
                validateObjectReference(task.getPrimaryObjRef(), "primary ObjectReference", "Task");
                validateAttachments(task);
                task.setDomain(workbasket.getDomain());
                standardSettings(task, classification);
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
    public Task getTask(String id) throws TaskNotFoundException {
        LOGGER.debug("entry to getTaskById(id = {})", id);
        TaskImpl resultTask = null;
        try {
            taskanaEngineImpl.openConnection();

            resultTask = taskMapper.findById(id);
            if (resultTask != null) {
                List<AttachmentImpl> attachmentImpls = attachmentMapper.findAttachmentsByTaskId(resultTask.getId());
                if (attachmentImpls == null) {
                    attachmentImpls = new ArrayList<>();
                }

                List<ClassificationSummary> classifications;
                try {
                    classifications = findClassificationForTaskImplAndAttachments(resultTask, attachmentImpls);
                } catch (NotAuthorizedException e1) {
                    LOGGER.error(
                        "ClassificationQuery unexpectedly returned NotauthorizedException. Throwing SystemException ");
                    throw new SystemException("ClassificationQuery unexpectedly returned NotauthorizedException.");
                }

                List<Attachment> attachments = addClassificationSummariesToAttachments(resultTask, attachmentImpls,
                    classifications);
                resultTask.setAttachments(attachments);

                ClassificationSummary classification = getMatchingClassificationFromList(classifications,
                    resultTask.getClassificationSummary().getKey(), resultTask.getDomain());

                resultTask.setClassificationSummary(classification);
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

            Workbasket destinationWorkbasket = workbasketService.getWorkbasketByKey(destinationWorkbasketKey);

            // reset read flag and set transferred flag
            task.setRead(false);
            task.setTransferred(true);

            // transfer task from source to destination workbasket
            task.setWorkbasketKey(destinationWorkbasketKey);
            task.setWorkbasketSummary(destinationWorkbasket.asSummary());
            task.setDomain(destinationWorkbasket.getDomain());
            task.setModified(Instant.now());
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
            task.setModified(Instant.now());
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
    public List<TaskSummary> getTasksByWorkbasketKeyAndState(String workbasketKey, TaskState taskState)
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {
        LOGGER.debug("entry to getTasksByWorkbasketKeyAndState(workbasketKey = {}, taskState = {})", workbasketKey,
            taskState);
        List<TaskSummary> results = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            workbasketService.checkAuthorization(workbasketKey, WorkbasketAuthorization.READ);
            List<TaskSummaryImpl> tasks = taskMapper.findTasksByWorkbasketIdAndState(workbasketKey, taskState);
            // postprocessing: augment each tasksummary by classificationSummary, workbasketSummary and
            // list<attachmentsummary>
            results = augmentTaskSummariesByContainedSummaries(tasks);
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
        ClassificationNotFoundException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        String userId = CurrentUserContext.getUserid();
        LOGGER.debug("entry to updateTask(task = {}, userId = {})", task, userId);
        TaskImpl newTaskImpl = (TaskImpl) task;
        TaskImpl oldTaskImpl = null;
        try {
            taskanaEngineImpl.openConnection();
            oldTaskImpl = (TaskImpl) getTask(newTaskImpl.getId());
            standardUpdateActions(oldTaskImpl, newTaskImpl);
            handleAttachmentsOnTaskUpdate(oldTaskImpl, newTaskImpl);
            newTaskImpl.setModified(Instant.now());

            taskMapper.update(newTaskImpl);
            LOGGER.debug("Method updateTask() updated task '{}' for user '{}'.", task.getId(), userId);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from claim()");
        }
        return task;
    }

    private void standardSettings(TaskImpl task, Classification classification) {
        Instant now = Instant.now();
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

        if (classification != null) {
            if (classification.getServiceLevel() != null) {
                Duration serviceLevel = Duration.parse(classification.getServiceLevel());
                Instant due = task.getPlanned().plus(serviceLevel);
                task.setDue(due);
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
                AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
                attachmentImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_ATTACHMENT));
                attachmentImpl.setTaskId(task.getId());
                attachmentImpl.setCreated(now);
                attachmentImpl.setModified(now);
                attachmentMapper.insert(attachmentImpl);
            }
        }
    }

    @Override
    public List<TaskSummary> getTaskSummariesByWorkbasketKey(String workbasketKey)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to getTaskSummariesByWorkbasketId(workbasketId = {}", workbasketKey);
        List<TaskSummary> results = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            workbasketService.getWorkbasketByKey(workbasketKey);  // make sure that the workbasket exists
            List<TaskSummaryImpl> taskSummaries = taskMapper.findTaskSummariesByWorkbasketKey(workbasketKey);
            // postprocessing: augment each tasksummary by classificationSummary, workbasketSummary and
            // list<attachmentsummary>
            results = augmentTaskSummariesByContainedSummaries(taskSummaries);

        } catch (WorkbasketNotFoundException | NotAuthorizedException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("Getting TASKSUMMARY failed internally.", ex);
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = results.size();
                LOGGER.debug(
                    "exit from getTaskSummariesByWorkbasketId(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(results));
            }
        }
        return results;
    }

    List<TaskSummary> augmentTaskSummariesByContainedSummaries(List<TaskSummaryImpl> taskSummaries)
        throws NotAuthorizedException {
        LOGGER.debug("entry to augmentTaskSummariesByContainedSummaries()");
        List<TaskSummary> result = new ArrayList<>();
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return result;
        }

        Set<String> taskIdSet = taskSummaries.stream().map(TaskSummaryImpl::getTaskId).collect(Collectors.toSet());
        String[] taskIdArray = taskIdSet.toArray(new String[0]);

        LOGGER.debug("augmentTaskSummariesByContainedSummaries() about to query for attachments ");
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
            ClassificationSummary aClassification = getMatchingClassificationFromList(classifications,
                task.getClassificationSummary().getKey(),
                task.getDomain());
            // set the classification on the task object
            task.setClassificationSummary(aClassification);
        }
    }

    private ClassificationSummary getMatchingClassificationFromList(List<ClassificationSummary> classifications,
        String taskClassKey, String taskDomain) {
        ClassificationSummary aClassification = classifications.stream()
            .filter(x -> taskClassKey != null && taskClassKey.equals(x.getKey()) && taskDomain != null
                && taskDomain.equals(x.getDomain()))
            .findFirst()
            .orElse(null);
        if (aClassification == null) {
            // search in "" domain
            aClassification = classifications.stream()
                .filter(x -> taskClassKey != null && taskClassKey.equals(x.getKey()) && "".equals(x.getDomain()))
                .findFirst()
                .orElse(null);
            if (aClassification == null) {
                LOGGER.error("Could not find a Classification for task ");
                throw new SystemException("Could not find a Classification for task ");
            }
        }
        return aClassification;
    }

    private List<ClassificationSummary> findClassificationsForTasksAndAttachments(
        List<TaskSummaryImpl> taskSummaries, List<AttachmentSummaryImpl> attachmentSummaries)
        throws NotAuthorizedException {
        LOGGER.debug("entry to getClassificationsForTasksAndAttachments()");
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> classificationDomainSet = taskSummaries.stream().map(TaskSummaryImpl::getDomain).collect(
            Collectors.toSet());
        // add "" domain in case the classification exists only there (fallback for tasks)
        classificationDomainSet.add("");

        Set<String> classificationKeySet = taskSummaries.stream()
            .map(t -> t.getClassificationSummary().getKey())
            .collect(Collectors.toSet());

        if (attachmentSummaries != null && !attachmentSummaries.isEmpty()) {
            Set<String> classificationKeysFromAttachments = attachmentSummaries.stream()
                .map(t -> t.getClassificationSummary().getKey())
                .collect(Collectors.toSet());
            classificationKeySet.addAll(classificationKeysFromAttachments);
        }

        return queryClassificationsForTasksAndAttachments(classificationDomainSet, classificationKeySet);
    }

    private List<ClassificationSummary> findClassificationForTaskImplAndAttachments(TaskImpl task,
        List<AttachmentImpl> attachmentImpls) throws NotAuthorizedException {

        Set<String> classificationDomainSet = new HashSet<>(Arrays.asList(task.getDomain(), ""));
        Set<String> classificationKeySet = new HashSet<>(Arrays.asList(task.getClassificationKey()));

        if (attachmentImpls != null && !attachmentImpls.isEmpty()) {
            Set<String> classificationKeysFromAttachments = attachmentImpls.stream()
                .map(t -> t.getClassificationSummary().getKey())
                .collect(Collectors.toSet());
            classificationKeySet.addAll(classificationKeysFromAttachments);
        }

        return queryClassificationsForTasksAndAttachments(classificationDomainSet, classificationKeySet);

    }

    private List<ClassificationSummary> queryClassificationsForTasksAndAttachments(Set<String> classificationDomainSet,
        Set<String> classificationKeySet) throws NotAuthorizedException {

        String[] classificationDomainArray = classificationDomainSet.toArray(new String[0]);
        String[] classificationKeyArray = classificationKeySet.toArray(new String[0]);

        LOGGER.debug("getClassificationsForTasksAndAttachments() about to query classifications and exit");
        // perform classification query
        return this.classificationService.createClassificationQuery()
            .domain(classificationDomainArray)
            .key(classificationKeyArray)
            .list();
    }

    private void addWorkbasketSummariesToTaskSummaries(List<TaskSummaryImpl> taskSummaries)
        throws NotAuthorizedException {
        LOGGER.debug("entry to addWorkbasketSummariesToTaskSummaries()");
        if (taskSummaries == null || taskSummaries.isEmpty()) {
            return;
        }
        // calculate parameters for workbasket query: workbasket keys
        Set<String> workbasketKeySet = taskSummaries.stream().map(t -> t.getWorkbasketSummary().getKey()).collect(
            Collectors.toSet());
        String[] workbasketKeyArray = workbasketKeySet.toArray(new String[0]);
        // perform workbasket query
        LOGGER.debug("addWorkbasketSummariesToTaskSummaries() about to query workbaskets");
        List<WorkbasketSummary> workbaskets = this.workbasketService.createWorkbasketQuery()
            .keyIn(workbasketKeyArray)
            .list();
        // assign query results to appropriate tasks.
        for (TaskSummaryImpl task : taskSummaries) {
            String workbasketKey = task.getWorkbasketSummaryImpl().getKey();

            // find the appropriate workbasket from the query result
            WorkbasketSummary aWorkbasket = workbaskets.stream()
                .filter(x -> workbasketKey != null && workbasketKey.equals(x.getKey()))
                .findFirst()
                .orElse(null);
            if (aWorkbasket == null) {
                LOGGER.error("Could not find a Workbasket for task {}.", task.getTaskId());
                throw new SystemException("Could not find a Workbasket for task " + task.getTaskId());
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
            // find the associated task to use the correct domain
            TaskSummaryImpl aTaskSummary = taskSummaries.stream()
                .filter(x -> x.getTaskId().equals(att.getTaskId()))
                .findFirst()
                .orElse(null);
            if (aTaskSummary == null) {
                LOGGER.error("Could not find a Task associated to attachment {}.", att);
                throw new SystemException("Could not find a Task associated to attachment " + att);
            }
            String domain = aTaskSummary.getDomain();
            String classificationKey = att.getClassificationSummary().getKey();
            ClassificationSummary aClassification = classifications.stream()
                .filter(x -> classificationKey != null && classificationKey.equals(x.getKey()) && domain != null
                    && domain.equals(x.getDomain()))
                .findFirst()
                .orElse(null);
            if (aClassification == null) {
                LOGGER.error("Could not find a Classification for attachment {}.", att);
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
            String domain = task.getDomain();
            String classificationKey = att.getClassificationSummary().getKey();
            ClassificationSummary aClassification = classifications.stream()
                .filter(x -> classificationKey != null && classificationKey.equals(x.getKey()) && domain != null
                    && domain.equals(x.getDomain()))
                .findFirst()
                .orElse(null);
            if (aClassification == null) {
                LOGGER.error("Could not find a Classification for attachment {}.", att);
                throw new SystemException("Could not find a Classification for attachment " + att);
            }
            att.setClassificationSummary(aClassification);
            result.add(att);
        }
        return result;
    }

    @Override
    public Task newTask(String workbasketKey) {
        TaskImpl task = new TaskImpl();
        task.setWorkbasketKey(workbasketKey);
        return task;
    }

    @Override
    public Attachment newAttachment() {
        return new AttachmentImpl();
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

    private void validateAttachments(TaskImpl task) throws InvalidArgumentException {
        List<Attachment> attachments = task.getAttachments();
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
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
                }
            }
        }
    }

    private void standardUpdateActions(TaskImpl oldTaskImpl, TaskImpl newTaskImpl)
        throws InvalidArgumentException, ConcurrencyException, WorkbasketNotFoundException,
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

        updateClassificationRelatedProperties(oldTaskImpl, newTaskImpl);

        newTaskImpl.setModified(Instant.now());
    }

    private void updateClassificationRelatedProperties(TaskImpl oldTaskImpl, TaskImpl newTaskImpl)
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {
        // insert Classification specifications if Classification is given.
        ClassificationSummary oldClassificationSummary = oldTaskImpl.getClassificationSummary();
        ClassificationSummary newClassificationSummary = oldClassificationSummary;
        String newClassificationKey = newTaskImpl.getClassificationKey();

        Classification newClassification = null;
        if (newClassificationKey != null && !newClassificationKey.equals(oldClassificationSummary.getKey())) {
            Workbasket workbasket = workbasketService.getWorkbasketByKey(newTaskImpl.getWorkbasketSummary().getKey());
            // set new classification
            newClassification = this.classificationService.getClassification(newClassificationKey,
                workbasket.getDomain());
            newClassificationSummary = newClassification.asSummary();
        }

        newTaskImpl.setClassificationSummary(newClassificationSummary);

        if (newClassification != null) {
            if (newClassification.getServiceLevel() != null) {
                Duration serviceLevel = Duration.parse(newClassification.getServiceLevel());
                Instant due = newTaskImpl.getPlanned().plus(serviceLevel);
                newTaskImpl.setDue(due);
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
        }
    }

    private void handleAttachmentsOnTaskUpdate(TaskImpl oldTaskImpl, TaskImpl newTaskImpl)
        throws AttachmentPersistenceException {
        // Iterator for removing invalid current values directly. OldAttachments can be ignored.
        Iterator<Attachment> i = newTaskImpl.getAttachments().iterator();
        while (i.hasNext()) {
            Attachment attachment = i.next();
            if (attachment != null) {
                boolean wasAlreadyRepresented = false;
                if (attachment.getId() != null) {
                    for (Attachment oldAttachment : oldTaskImpl.getAttachments()) {
                        if (oldAttachment != null && attachment.getId().equals(oldAttachment.getId())) {
                            wasAlreadyRepresented = true;
                            if (!attachment.equals(oldAttachment)) {
                                AttachmentImpl temp = (AttachmentImpl) attachment;
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
                if (!wasAlreadyRepresented) {
                    AttachmentImpl attachmentImpl = (AttachmentImpl) attachment;
                    initAttachment(attachmentImpl, newTaskImpl);
                    try {
                        attachmentMapper.insert(attachmentImpl);
                        LOGGER.debug("TaskService.updateTask() for TaskId={} INSERTED an Attachment={}.",
                            newTaskImpl.getId(),
                            attachmentImpl);
                    } catch (PersistenceException e) {
                        LOGGER.error(
                            "TaskService.updateTask() for TaskId={} can NOT INSERT the current Attachment, because it was added fored multiple times and wasn´t persisted before. ID={}",
                            newTaskImpl.getId(), attachmentImpl.getId());
                        throw new AttachmentPersistenceException(attachmentImpl.getId());
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
}
