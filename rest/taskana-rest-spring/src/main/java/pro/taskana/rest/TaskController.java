package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.KeyDomain;
import pro.taskana.Task;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskSummaryResource;
import pro.taskana.rest.resource.assembler.TaskResourceAssembler;
import pro.taskana.rest.resource.assembler.TaskSummaryResourcesAssembler;

/**
 * Controller for all {@link Task} related endpoints.
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequestMapping(path = "/v1/tasks", produces = "application/hal+json")
public class TaskController extends AbstractPagingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private static final String STATE = "state";
    private static final String STATE_VALUE_CLAIMED = "CLAIMED";
    private static final String STATE_VALUE_COMPLETED = "COMPLETED";
    private static final String STATE_VALUE_READY = "READY";
    private static final String PRIORITY = "priority";
    private static final String NAME = "name";
    private static final String OWNER = "owner";
    private static final String DOMAIN = "domain";
    private static final String WORKBASKET_ID = "workbasket-id";
    private static final String WORKBASKET_KEY = "workbasket-key";
    private static final String CLASSIFICATION_KEY = "classification.key";
    private static final String POR_VALUE = "por.value";
    private static final String POR_TYPE = "por.type";
    private static final String POR_SYSTEM_INSTANCE = "por.instance";
    private static final String POR_SYSTEM = "por.system";
    private static final String POR_COMPANY = "por.company";
    private static final String DUE = "due";
    private static final String PLANNED = "planned";

    private static final String SORT_BY = "sortBy";
    private static final String SORT_DIRECTION = "order";

    private static final String PAGING_PAGE = "page";
    private static final String PAGING_PAGE_SIZE = "page-size";

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskResourceAssembler taskResourceAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<PagedResources<TaskSummaryResource>> getTasks(
        @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {

        TaskQuery query = taskService.createTaskQuery();
        query = applyFilterParams(query, params);
        query = applySortingParams(query, params);

        PageMetadata pageMetadata = null;
        List<TaskSummary> taskSummaries = null;
        String page = params.getFirst(PAGING_PAGE);
        String pageSize = params.getFirst(PAGING_PAGE_SIZE);
        params.remove(PAGING_PAGE);
        params.remove(PAGING_PAGE_SIZE);
        validateNoInvalidParameterIsLeft(params);
        if (page != null && pageSize != null) {
            // paging
            long totalElements = query.count();
            pageMetadata = initPageMetadata(pageSize, page,
                totalElements);
            taskSummaries = query.listPage((int) pageMetadata.getNumber(),
                (int) pageMetadata.getSize());
        } else if (page == null && pageSize == null) {
            // not paging
            taskSummaries = query.list();
        } else {
            throw new InvalidArgumentException("Paging information is incomplete.");
        }

        TaskSummaryResourcesAssembler taskSummaryResourcesAssembler = new TaskSummaryResourcesAssembler();
        PagedResources<TaskSummaryResource> pagedResources = taskSummaryResourcesAssembler.toResources(taskSummaries,
            pageMetadata);

        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }

    @GetMapping(path = "/{taskId}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> getTask(@PathVariable String taskId)
        throws TaskNotFoundException, NotAuthorizedException {
        Task task = taskService.getTask(taskId);
        ResponseEntity<TaskResource> result = new ResponseEntity<>(taskResourceAssembler.toResource(task),
            HttpStatus.OK);
        return result;
    }

    @PostMapping(path = "/{taskId}/claim")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> claimTask(@PathVariable String taskId, @RequestBody String userName)
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        // TODO verify user
        taskService.claim(taskId);
        Task updatedTask = taskService.getTask(taskId);
        ResponseEntity<TaskResource> result = new ResponseEntity<>(taskResourceAssembler.toResource(updatedTask),
            HttpStatus.OK);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{taskId}/complete")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> completeTask(@PathVariable String taskId)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException {
        taskService.forceCompleteTask(taskId);
        Task updatedTask = taskService.getTask(taskId);
        ResponseEntity<TaskResource> result = new ResponseEntity<>(taskResourceAssembler.toResource(updatedTask),
            HttpStatus.OK);
        return result;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{taskId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> deleteTask(@PathVariable String taskId)
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        taskService.forceDeleteTask(taskId);
        ResponseEntity<TaskResource> result = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> createTask(@RequestBody TaskResource taskResource)
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException {
        Task createdTask = taskService.createTask(taskResourceAssembler.toModel(taskResource));
        ResponseEntity<TaskResource> result = new ResponseEntity<>(taskResourceAssembler.toResource(createdTask),
            HttpStatus.CREATED);
        return result;
    }

    @RequestMapping(path = "/{taskId}/transfer/{workbasketId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> transferTask(@PathVariable String taskId, @PathVariable String workbasketId)
        throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException, InvalidStateException {
        Task updatedTask = taskService.transfer(taskId, workbasketId);
        ResponseEntity<TaskResource> result = new ResponseEntity<>(taskResourceAssembler.toResource(updatedTask),
            HttpStatus.OK);
        return result;
    }

    @PutMapping(path = "/{taskId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TaskResource> updateTask(
        @PathVariable(value = "taskId") String taskId,
        @RequestBody TaskResource taskResource)
        throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
        ResponseEntity<TaskResource> result;
        if (taskId.equals(taskResource.getTaskId())) {
            Task task = taskResourceAssembler.toModel(taskResource);
            task = taskService.updateTask(task);
            result = ResponseEntity.ok(taskResourceAssembler.toResource(task));
        } else {
            throw new InvalidArgumentException(
                "TaskId ('" + taskId
                    + "') is not identical with the taskId of to object in the payload which should be updated. ID=('"
                    + taskResource.getTaskId() + "')");
        }

        return result;
    }

    private TaskQuery applyFilterParams(TaskQuery taskQuery, MultiValueMap<String, String> params)
        throws InvalidArgumentException {

        // apply filters
        if (params.containsKey(NAME)) {
            String[] names = extractCommaSeparatedFields(params.get(NAME));
            taskQuery.nameIn(names);
            params.remove(NAME);
        }
        if (params.containsKey(PRIORITY)) {
            String[] prioritiesInString = extractCommaSeparatedFields(params.get(PRIORITY));
            int[] priorities = extractPriorities(prioritiesInString);
            taskQuery.priorityIn(priorities);
            params.remove(PRIORITY);
        }
        if (params.containsKey(STATE)) {
            TaskState[] states = extractStates(params);
            taskQuery.stateIn(states);
            params.remove(STATE);
        }
        if (params.containsKey(CLASSIFICATION_KEY)) {
            String[] classificationKeys = extractCommaSeparatedFields(params.get(CLASSIFICATION_KEY));
            taskQuery.classificationKeyIn(classificationKeys);
            params.remove(CLASSIFICATION_KEY);
        }
        if (params.containsKey(WORKBASKET_ID)) {
            String[] workbaskets = extractCommaSeparatedFields(params.get(WORKBASKET_ID));
            taskQuery.workbasketIdIn(workbaskets);
            params.remove(WORKBASKET_ID);
        }
        if (params.containsKey(WORKBASKET_KEY)) {
            String[] domains = null;
            if (params.get(DOMAIN) != null) {
                domains = extractCommaSeparatedFields(params.get(DOMAIN));
            }
            if (domains == null || domains.length != 1) {
                throw new InvalidArgumentException("workbasket-key requires excactly one domain as second parameter.");
            }
            String[] workbasketKeys = extractCommaSeparatedFields(params.get(WORKBASKET_KEY));
            KeyDomain[] keyDomains = new KeyDomain[workbasketKeys.length];
            for (int i = 0; i < workbasketKeys.length; i++) {
                keyDomains[i] = new KeyDomain(workbasketKeys[i], domains[0]);
            }
            taskQuery.workbasketKeyDomainIn(keyDomains);
            params.remove(WORKBASKET_KEY);
            params.remove(DOMAIN);
        }
        if (params.containsKey(OWNER)) {
            String[] owners = extractCommaSeparatedFields(params.get(OWNER));
            taskQuery.ownerIn(owners);
            params.remove(OWNER);
        }
        if (params.containsKey(POR_COMPANY)) {
            String[] companies = extractCommaSeparatedFields(params.get(POR_COMPANY));
            taskQuery.primaryObjectReferenceCompanyIn(companies);
            params.remove(POR_COMPANY);
        }
        if (params.containsKey(POR_SYSTEM)) {
            String[] systems = extractCommaSeparatedFields(params.get(POR_SYSTEM));
            taskQuery.primaryObjectReferenceSystemIn(systems);
            params.remove(POR_SYSTEM);
        }
        if (params.containsKey(POR_SYSTEM_INSTANCE)) {
            String[] systemInstances = extractCommaSeparatedFields(params.get(POR_SYSTEM_INSTANCE));
            taskQuery.primaryObjectReferenceSystemInstanceIn(systemInstances);
            params.remove(POR_SYSTEM_INSTANCE);
        }
        if (params.containsKey(POR_TYPE)) {
            String[] types = extractCommaSeparatedFields(params.get(POR_TYPE));
            taskQuery.primaryObjectReferenceTypeIn(types);
            params.remove(POR_TYPE);
        }
        if (params.containsKey(POR_VALUE)) {
            String[] values = extractCommaSeparatedFields(params.get(POR_VALUE));
            taskQuery.primaryObjectReferenceValueIn(values);
            params.remove(POR_VALUE);
        }
        return taskQuery;
    }

    private TaskQuery applySortingParams(TaskQuery taskQuery, MultiValueMap<String, String> params)
        throws InvalidArgumentException {

        // sorting
        String sortBy = params.getFirst(SORT_BY);
        if (sortBy != null) {
            SortDirection sortDirection;
            if (params.getFirst(SORT_DIRECTION) != null && "desc".equals(params.getFirst(SORT_DIRECTION))) {
                sortDirection = SortDirection.DESCENDING;
            } else {
                sortDirection = SortDirection.ASCENDING;
            }
            switch (sortBy) {
                case (CLASSIFICATION_KEY):
                    taskQuery = taskQuery.orderByClassificationKey(sortDirection);
                    break;
                case (POR_TYPE):
                    taskQuery = taskQuery.orderByPrimaryObjectReferenceType(sortDirection);
                    break;
                case (POR_VALUE):
                    taskQuery = taskQuery.orderByPrimaryObjectReferenceValue(sortDirection);
                    break;
                case (STATE):
                    taskQuery = taskQuery.orderByState(sortDirection);
                    break;
                case (NAME):
                    taskQuery = taskQuery.orderByName(sortDirection);
                    break;
                case (DUE):
                    taskQuery = taskQuery.orderByDue(sortDirection);
                    break;
                case (PLANNED):
                    taskQuery = taskQuery.orderByPlanned(sortDirection);
                    break;
                case (PRIORITY):
                    taskQuery = taskQuery.orderByPriority(sortDirection);
                    break;
                default:
                    throw new InvalidArgumentException("Unknown filter attribute: " + sortBy);
            }
        }
        params.remove(SORT_BY);
        params.remove(SORT_DIRECTION);
        return taskQuery;
    }

    private int[] extractPriorities(String[] prioritiesInString) {
        int[] priorities = new int[prioritiesInString.length];
        for (int i = 0; i < prioritiesInString.length; i++) {
            priorities[i] = Integer.valueOf(prioritiesInString[i]);
        }
        return priorities;
    }

    private TaskState[] extractStates(MultiValueMap<String, String> params) throws InvalidArgumentException {
        List<TaskState> states = new ArrayList<>();
        for (String item : params.get(STATE)) {
            for (String state : item.split(",")) {
                switch (state) {
                    case STATE_VALUE_READY:
                        states.add(TaskState.READY);
                        break;
                    case STATE_VALUE_COMPLETED:
                        states.add(TaskState.COMPLETED);
                        break;
                    case STATE_VALUE_CLAIMED:
                        states.add(TaskState.CLAIMED);
                        break;
                    default:
                        throw new InvalidArgumentException("Unknown status '" + state + "'");
                }
            }
        }
        return states.toArray(new TaskState[0]);
    }
}
