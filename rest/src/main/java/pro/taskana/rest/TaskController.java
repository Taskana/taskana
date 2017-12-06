package pro.taskana.rest;

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

import pro.taskana.TaskService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;
import pro.taskana.rest.query.TaskFilter;

@RestController
@RequestMapping(path = "/v1/tasks", produces = { MediaType.APPLICATION_JSON_VALUE })
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskFilter taskLogic;

    @RequestMapping
    public ResponseEntity<List<Task>> getTasks(@RequestParam MultiValueMap<String, String> params)
            throws LoginException {
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
    public ResponseEntity<Task> getTask(@PathVariable(value = "taskId") String taskId) {
        try {
            Task task = taskService.getTaskById(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (TaskNotFoundException e) {
            logger.error("The searched Task couldn´t be found or does not exist.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/workbasket/{workbasketId}/state/{taskState}")
    public ResponseEntity<List<Task>> getTasksByWorkbasketIdAndState(
            @PathVariable(value = "workbasketId") String workbasketId, @PathVariable(value = "taskState") TaskState taskState) {
        try {
            List<Task> taskList = taskService.getTasksByWorkbasketIdAndState(workbasketId, taskState);
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
    public ResponseEntity<Task> claimTask(@PathVariable String taskId, @RequestBody String userName) {
        // TODO verify user
        try {
            taskService.claim(taskId, userName);
            Task updatedTask = taskService.getTaskById(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (TaskNotFoundException e) {
            logger.error("The given Task coundn´t be found/claimd or does not Exist.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable String taskId) {
        try {
            taskService.complete(taskId);
            Task updatedTask = taskService.getTaskById(taskId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task createdTask = taskService.create(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            logger.error("Something went wrong: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/transfer/{workbasketId}")
    public ResponseEntity<Task> transferTask(@PathVariable String taskId, @PathVariable String workbasketId) {
        try {
            Task updatedTask = taskService.transfer(taskId, workbasketId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedTask);
        } catch (Exception e) {
            logger.error("Something went wrong: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
