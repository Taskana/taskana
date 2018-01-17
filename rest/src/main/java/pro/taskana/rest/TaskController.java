package pro.taskana.rest;

import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.TaskState;
import pro.taskana.rest.query.TaskFilter;

@RestController
@RequestMapping(path = "/v1/tasks", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskFilter taskLogic;

    @RequestMapping
    public ResponseEntity<List<TaskSummary>> getTasks(@RequestParam MultiValueMap<String, String> params)
        throws LoginException, InvalidArgumentException {
        try {
            if (params.keySet().size() == 0) {
                // get all
                return ResponseEntity.status(HttpStatus.OK).body(taskLogic.getAll());
            }
            return ResponseEntity.status(HttpStatus.OK).body(taskLogic.inspectPrams(params));
        } catch (NotAuthorizedException e) {
            logger.error("Somthing went wrong whith the Authorisation, while getting all Tasks.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @RequestMapping(value = "/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable(value = "taskId") String taskId)
        throws ClassificationNotFoundException {
        try {
            Task task = taskService.getTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (TaskNotFoundException e) {
            logger.error("The searched Task couldn´t be found or does not exist.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/workbasket/{workbasketKey}/state/{taskState}")
    public ResponseEntity<List<TaskSummary>> getTasksByWorkbasketIdAndState(
        @PathVariable(value = "workbasketKey") String workbasketKey,
        @PathVariable(value = "taskState") TaskState taskState) {
        try {
            List<TaskSummary> taskList = taskService.getTasksByWorkbasketKeyAndState(workbasketKey, taskState);
            return ResponseEntity.status(HttpStatus.OK).body(taskList);
        } catch (WorkbasketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NotAuthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/claim")
    public ResponseEntity<Task> claimTask(@PathVariable String taskId, @RequestBody String userName)
        throws ClassificationNotFoundException {
        // TODO verify user
        try {
            taskService.claim(taskId);
            Task updatedTask = taskService.getTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (TaskNotFoundException e) {
            logger.error("The given Task coundn´t be found/claimd or does not Exist.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidStateException e) {
            logger.error("The given Task could not be claimed. Reason: {}", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (InvalidOwnerException e) {
            logger.error("The given Task could not be claimed. Reason: {}", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable String taskId) throws ClassificationNotFoundException {
        try {
            taskService.completeTask(taskId, true);
            Task updatedTask = taskService.getTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidStateException | InvalidOwnerException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            logger.error("Something went wrong: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/transfer/{workbasketKey}")
    public ResponseEntity<Task> transferTask(@PathVariable String taskId, @PathVariable String workbasketKey) {
        try {
            Task updatedTask = taskService.transfer(taskId, workbasketKey);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedTask);
        } catch (Exception e) {
            logger.error("Something went wrong: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/workbasket/{workbasketKey}", method = RequestMethod.GET)
    public ResponseEntity<List<TaskSummary>> getTasksummariesByWorkbasketId(
        @PathVariable(value = "workbasketKey") String workbasketKey) {
        List<TaskSummary> taskSummaries = null;
        try {
            taskSummaries = taskService.getTaskSummariesByWorkbasketKey(workbasketKey);
            return ResponseEntity.status(HttpStatus.OK).body(taskSummaries);
        } catch (Exception ex) {
            if (taskSummaries == null) {
                taskSummaries = Collections.emptyList();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
