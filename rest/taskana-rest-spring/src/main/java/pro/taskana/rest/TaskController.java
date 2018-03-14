package pro.taskana.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.query.TaskFilter;

/**
 * Controller for all {@link Task} related endpoints.
 */
@RestController
@RequestMapping(path = "/v1/tasks", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskFilter taskLogic;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<TaskSummary>> getTasks(@RequestParam MultiValueMap<String, String> params)
        throws NotAuthorizedException, InvalidArgumentException {
        if (params.keySet().size() == 0) {
            // get all
            return ResponseEntity.status(HttpStatus.OK).body(taskLogic.getAll());
        }
        return ResponseEntity.status(HttpStatus.OK).body(taskLogic.inspectPrams(params));
    }

    @GetMapping(path = "/{taskId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<Task> getTask(@PathVariable String taskId)
        throws TaskNotFoundException, NotAuthorizedException {
        Task task = taskService.getTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping(path = "/workbasket/{workbasketId}/state/{taskState}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<TaskSummary>> getTasksByWorkbasketIdAndState(@PathVariable String workbasketId,
        @PathVariable TaskState taskState) {
        List<TaskSummary> taskList = taskService.createTaskQuery()
            .workbasketIdIn(workbasketId)
            .stateIn(taskState)
            .list();
        return ResponseEntity.status(HttpStatus.OK).body(taskList);
    }

    @PostMapping(path = "/{taskId}/claim")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> claimTask(@PathVariable String taskId, @RequestBody String userName)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        // TODO verify user
        taskService.claim(taskId);
        Task updatedTask = taskService.getTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/complete")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> completeTask(@PathVariable String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException {
        taskService.completeTask(taskId, true);
        Task updatedTask = taskService.getTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> createTask(@RequestBody Task task)
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @RequestMapping(path = "/{taskId}/transfer/{workbasketKey}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Task> transferTask(@PathVariable String taskId, @PathVariable String workbasketKey)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        Task updatedTask = taskService.transfer(taskId, workbasketKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedTask);
    }

    @GetMapping(path = "/workbasket/{workbasketId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<TaskSummary>> getTasksummariesByWorkbasketId(@PathVariable String workbasketId) {
        List<TaskSummary> taskSummaries = null;
        taskSummaries = taskService.createTaskQuery().workbasketIdIn(workbasketId).list();
        return ResponseEntity.status(HttpStatus.OK).body(taskSummaries);

    }
}
