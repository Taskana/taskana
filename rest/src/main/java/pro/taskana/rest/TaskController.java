package pro.taskana.rest;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.rest.query.TaskFilter;

@RestController
@RequestMapping(path = "/v1/tasks", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskFilter taskLogic;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<TaskSummary>> getTasks(@RequestParam MultiValueMap<String, String> params)
        throws InvalidArgumentException {
        try {
            if (params.keySet().size() == 0) {
                // get all
                return ResponseEntity.status(HttpStatus.OK).body(taskLogic.getAll());
            }
            return ResponseEntity.status(HttpStatus.OK).body(taskLogic.inspectPrams(params));
        } catch (NotAuthorizedException e) {
            logger.error("Something went wrong with the Authorisation, while getting all Tasks.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(path = "/{taskId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        try {
            Task task = taskService.getTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (TaskNotFoundException e) {
            logger.error("The searched Task couldn´t be found or does not exist.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "/workbasket/{workbasketId}/state/{taskState}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<TaskSummary>> getTasksByWorkbasketIdAndState(@PathVariable String workbasketId,
        @PathVariable TaskState taskState) {
        try {
            List<TaskSummary> taskList = taskService.createTaskQuery()
                .workbasketIdIn(workbasketId)
                .stateIn(taskState)
                .list();
            return ResponseEntity.status(HttpStatus.OK).body(taskList);
        } catch (NotAuthorizedToQueryWorkbasketException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(path = "/{taskId}/claim")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> claimTask(@PathVariable String taskId, @RequestBody String userName) {
        // TODO verify user
        try {
            taskService.claim(taskId);
            Task updatedTask = taskService.getTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (TaskNotFoundException e) {
            logger.error("The given Task coundn´t be found/claimd or does not Exist.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidStateException | InvalidOwnerException e) {
            logger.error("The given Task could not be claimed. Reason: {}", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/complete")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> completeTask(@PathVariable String taskId) {
        try {
            taskService.completeTask(taskId, true);
            Task updatedTask = taskService.getTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (TaskNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidStateException | InvalidOwnerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            logger.error("Something went wrong: ", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(path = "/{taskId}/transfer/{workbasketKey}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> transferTask(@PathVariable String taskId, @PathVariable String workbasketKey) {
        try {
            Task updatedTask = taskService.transfer(taskId, workbasketKey);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedTask);
        } catch (Exception e) {
            logger.error("Something went wrong: ", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/workbasket/{workbasketId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<TaskSummary>> getTasksummariesByWorkbasketId(@PathVariable String workbasketId) {
        List<TaskSummary> taskSummaries = null;
        try {
            taskSummaries = taskService.createTaskQuery().workbasketIdIn(workbasketId).list();
            return ResponseEntity.status(HttpStatus.OK).body(taskSummaries);
        } catch (NotAuthorizedToQueryWorkbasketException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception ex) {
            if (taskSummaries == null) {
                taskSummaries = Collections.emptyList();
            }
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
