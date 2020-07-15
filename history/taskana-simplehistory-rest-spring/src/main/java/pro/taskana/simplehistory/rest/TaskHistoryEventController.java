package pro.taskana.simplehistory.rest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.AbstractPagingController;
import pro.taskana.common.rest.QueryHelper;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.simplehistory.rest.assembler.TaskHistoryEventListResourceAssembler;
import pro.taskana.simplehistory.rest.assembler.TaskHistoryEventRepresentationModelAssembler;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventListResource;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventRepresentationModel;
import pro.taskana.spi.history.api.events.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;

/** Controller for all TaskHistoryEvent related endpoints. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@RequestMapping(path = "/api/v1/task-history-event", produces = "application/hal+json")
public class TaskHistoryEventController extends AbstractPagingController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskHistoryEventController.class);

  private static final String LIKE = "%";
  private static final String BUSINESS_PROCESS_ID = "business-process-id";
  private static final String BUSINESS_PROCESS_ID_LIKE = "business-process-id-like";
  private static final String PARENT_BUSINESS_PROCESS_ID = "parent-business-process-id";
  private static final String PARENT_BUSINESS_PROCESS_ID_LIKE = "parent-business-process-id-like";
  private static final String TASK_ID = "task-id";
  private static final String TASK_ID_LIKE = "task-id-like";
  private static final String EVENT_TYPE = "event-type";
  private static final String EVENT_TYPE_LIKE = "event-type-like";
  private static final String CREATED = "created";
  private static final String USER_ID = "user-id";
  private static final String USER_ID_LIKE = "user-id-like";
  private static final String DOMAIN = "domain";
  private static final String WORKBASKET_KEY = "workbasket-key";
  private static final String WORKBASKET_KEY_LIKE = "workbasket-key-like";
  private static final String POR_COMPANY = "por-company";
  private static final String POR_COMPANY_LIKE = "por-company-like";
  private static final String POR_SYSTEM = "por-system";
  private static final String POR_SYSTEM_LIKE = "por-system-like";
  private static final String POR_INSTANCE = "por-instance";
  private static final String POR_INSTANCE_LIKE = "por-instance-like";
  private static final String POR_TYPE = "por-type";
  private static final String POR_TYPE_LIKE = "por-type-like";
  private static final String POR_VALUE = "por-value";
  private static final String POR_VALUE_LIKE = "por-value-like";
  private static final String TASK_CLASSIFICATION_KEY = "task-classification-key";
  private static final String TASK_CLASSIFICATION_KEY_LIKE = "task-classification-key-like";
  private static final String TASK_CLASSIFICATION_CATEGORY = "task-classification-category";
  private static final String TASK_CLASSIFICATION_CATEGORY_LIKE =
      "task-classification-category-like";
  private static final String ATTACHMENT_CLASSIFICATION_KEY = "attachment-classification-key";
  private static final String ATTACHMENT_CLASSIFICATION_KEY_LIKE =
      "attachment-classification-key-like";
  private static final String CUSTOM_1 = "custom-1";
  private static final String CUSTOM_2 = "custom-2";
  private static final String CUSTOM_3 = "custom-3";
  private static final String CUSTOM_4 = "custom-4";
  private static final String PAGING_PAGE = "page";
  private static final String PAGING_PAGE_SIZE = "page-size";

  private final SimpleHistoryServiceImpl simpleHistoryService;
  private final TaskHistoryEventRepresentationModelAssembler
      taskHistoryEventRepresentationModelAssembler;

  @Autowired
  public TaskHistoryEventController(
      TaskanaEngineConfiguration taskanaEngineConfiguration,
      SimpleHistoryServiceImpl simpleHistoryServiceImpl,
      TaskHistoryEventRepresentationModelAssembler taskHistoryEventRepresentationModelAssembler) {

    this.simpleHistoryService = simpleHistoryServiceImpl;
    this.simpleHistoryService.initialize(taskanaEngineConfiguration.buildTaskanaEngine());
    this.taskHistoryEventRepresentationModelAssembler =
        taskHistoryEventRepresentationModelAssembler;
  }

  @GetMapping
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskHistoryEventListResource> getTaskHistoryEvents(
      @RequestParam MultiValueMap<String, String> params) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskHistoryEvents(params= {})", params);
    }

    TaskHistoryQuery query = simpleHistoryService.createTaskHistoryQuery();
    applySortingParams(query, params);
    applyFilterParams(query, params);

    PageMetadata pageMetadata = null;
    List<TaskHistoryEvent> historyEvents;
    final String page = params.getFirst(PAGING_PAGE);
    final String pageSize = params.getFirst(PAGING_PAGE_SIZE);
    params.remove(PAGING_PAGE);
    params.remove(PAGING_PAGE_SIZE);
    validateNoInvalidParameterIsLeft(params);
    if (page != null && pageSize != null) {
      long totalElements = query.count();
      pageMetadata = initPageMetadata(pageSize, page, totalElements);
      historyEvents = query.listPage((int) pageMetadata.getNumber(), (int) pageMetadata.getSize());
    } else if (page == null && pageSize == null) {
      historyEvents = query.list();
    } else {
      throw new InvalidArgumentException("Paging information is incomplete.");
    }

    TaskHistoryEventListResourceAssembler assembler = new TaskHistoryEventListResourceAssembler();
    TaskHistoryEventListResource pagedResources =
        assembler.toResources(historyEvents, pageMetadata);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Exit from getTaskHistoryEvents(), returning {}",
          new ResponseEntity<>(pagedResources, HttpStatus.OK));
    }

    return new ResponseEntity<>(pagedResources, HttpStatus.OK);
  }

  @GetMapping(path = "/{historyEventId}", produces = "application/hal+json")
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskHistoryEventRepresentationModel> getTaskHistoryEvent(
      @PathVariable String historyEventId) throws TaskanaHistoryEventNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskHistoryEvent(historyEventId= {})", historyEventId);
    }

    TaskHistoryEvent resultEvent = simpleHistoryService.getTaskHistoryEvent(historyEventId);

    TaskHistoryEventRepresentationModel taskEventResource =
        taskHistoryEventRepresentationModelAssembler.toModel(resultEvent);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Exit from getTaskHistoryEvent, returning {}",
          new ResponseEntity<>(taskEventResource, HttpStatus.OK));
    }

    return new ResponseEntity<>(taskEventResource, HttpStatus.OK);
  }

  private void applySortingParams(TaskHistoryQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(params= {})", params);
    }

    QueryHelper.applyAndRemoveSortingParams(
        params,
        (sortBy, sortDirection) -> {
          switch (sortBy) {
            case BUSINESS_PROCESS_ID:
              query.orderByBusinessProcessId(sortDirection);
              break;
            case PARENT_BUSINESS_PROCESS_ID:
              query.orderByParentBusinessProcessId(sortDirection);
              break;
            case TASK_ID:
              query.orderByTaskId(sortDirection);
              break;
            case EVENT_TYPE:
              query.orderByEventType(sortDirection);
              break;
            case CREATED:
              query.orderByCreated(sortDirection);
              break;
            case USER_ID:
              query.orderByUserId(sortDirection);
              break;
            case DOMAIN:
              query.orderByDomain(sortDirection);
              break;
            case WORKBASKET_KEY:
              query.orderByWorkbasketKey(sortDirection);
              break;
            case POR_COMPANY:
              query.orderByPorCompany(sortDirection);
              break;
            case POR_SYSTEM:
              query.orderByPorSystem(sortDirection);
              break;
            case POR_INSTANCE:
              query.orderByPorInstance(sortDirection);
              break;
            case POR_TYPE:
              query.orderByPorType(sortDirection);
              break;
            case POR_VALUE:
              query.orderByPorValue(sortDirection);
              break;
            case TASK_CLASSIFICATION_KEY:
              query.orderByTaskClassificationKey(sortDirection);
              break;
            case TASK_CLASSIFICATION_CATEGORY:
              query.orderByTaskClassificationCategory(sortDirection);
              break;
            case ATTACHMENT_CLASSIFICATION_KEY:
              query.orderByAttachmentClassificationKey(sortDirection);
              break;
            case CUSTOM_1:
              query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_1, sortDirection);
              break;
            case CUSTOM_2:
              query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_2, sortDirection);
              break;
            case CUSTOM_3:
              query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_3, sortDirection);
              break;
            case CUSTOM_4:
              query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_4, sortDirection);
              break;
            default:
              throw new IllegalArgumentException("Unknown order '" + sortBy + "'");
          }
        });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning: {}", query);
    }
  }

  private void applyFilterParams(TaskHistoryQuery query, MultiValueMap<String, String> params) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applyFilterParams(query= {}, params= {})", query, params);
    }

    if (params.containsKey(BUSINESS_PROCESS_ID)) {
      String[] businessProcessId = extractCommaSeparatedFields(params.get(BUSINESS_PROCESS_ID));
      query.businessProcessIdIn(businessProcessId);
      params.remove(BUSINESS_PROCESS_ID);
    }
    if (params.containsKey(BUSINESS_PROCESS_ID_LIKE)) {
      query.businessProcessIdLike(LIKE + params.get(BUSINESS_PROCESS_ID_LIKE).get(0) + LIKE);
      params.remove(BUSINESS_PROCESS_ID_LIKE);
    }
    if (params.containsKey(PARENT_BUSINESS_PROCESS_ID)) {
      String[] parentBusinessProcessId =
          extractCommaSeparatedFields(params.get(PARENT_BUSINESS_PROCESS_ID));
      query.parentBusinessProcessIdIn(parentBusinessProcessId);
      params.remove(PARENT_BUSINESS_PROCESS_ID);
    }
    if (params.containsKey(PARENT_BUSINESS_PROCESS_ID_LIKE)) {
      query.parentBusinessProcessIdLike(
          LIKE + params.get(PARENT_BUSINESS_PROCESS_ID_LIKE).get(0) + LIKE);
      params.remove(PARENT_BUSINESS_PROCESS_ID_LIKE);
    }
    if (params.containsKey(TASK_ID)) {
      String[] taskId = extractCommaSeparatedFields(params.get(TASK_ID));
      query.taskIdIn(taskId);
      params.remove(TASK_ID);
    }
    if (params.containsKey(TASK_ID_LIKE)) {
      query.taskIdLike(LIKE + params.get(TASK_ID_LIKE).get(0) + LIKE);
      params.remove(TASK_ID_LIKE);
    }
    if (params.containsKey(EVENT_TYPE)) {
      String[] eventType = extractCommaSeparatedFields(params.get(EVENT_TYPE));
      query.eventTypeIn(eventType);
      params.remove(EVENT_TYPE);
    }
    if (params.containsKey(EVENT_TYPE_LIKE)) {
      query.eventTypeLike(LIKE + params.get(EVENT_TYPE_LIKE).get(0) + LIKE);
      params.remove(EVENT_TYPE_LIKE);
    }
    if (params.containsKey(CREATED)) {
      String[] created = extractCommaSeparatedFields(params.get(CREATED));
      TimeInterval timeInterval = getTimeIntervalOf(created);
      query.createdWithin(timeInterval);
      params.remove(CREATED);
    }
    if (params.containsKey(USER_ID)) {
      String[] userId = extractCommaSeparatedFields(params.get(USER_ID));
      query.userIdIn(userId);
      params.remove(USER_ID);
    }
    if (params.containsKey(USER_ID_LIKE)) {
      query.userIdLike(LIKE + params.get(USER_ID_LIKE).get(0) + LIKE);
      params.remove(USER_ID_LIKE);
    }
    if (params.containsKey(DOMAIN)) {
      query.domainIn(extractCommaSeparatedFields(params.get(DOMAIN)));
      params.remove(DOMAIN);
    }
    if (params.containsKey(WORKBASKET_KEY)) {
      String[] workbasketKey = extractCommaSeparatedFields(params.get(WORKBASKET_KEY));
      query.workbasketKeyIn(workbasketKey);
      params.remove(WORKBASKET_KEY);
    }
    if (params.containsKey(WORKBASKET_KEY_LIKE)) {
      query.workbasketKeyLike(LIKE + params.get(WORKBASKET_KEY_LIKE).get(0) + LIKE);
      params.remove(WORKBASKET_KEY_LIKE);
    }
    if (params.containsKey(POR_COMPANY)) {
      String[] porCompany = extractCommaSeparatedFields(params.get(POR_COMPANY));
      query.porCompanyIn(porCompany);
      params.remove(POR_COMPANY);
    }
    if (params.containsKey(POR_COMPANY_LIKE)) {
      query.porCompanyLike(LIKE + params.get(POR_COMPANY_LIKE).get(0) + LIKE);
      params.remove(POR_COMPANY_LIKE);
    }
    if (params.containsKey(POR_SYSTEM)) {
      String[] porSystem = extractCommaSeparatedFields(params.get(POR_SYSTEM));
      query.porSystemIn(porSystem);
      params.remove(POR_SYSTEM);
    }
    if (params.containsKey(POR_SYSTEM_LIKE)) {
      query.porSystemLike(LIKE + params.get(POR_SYSTEM_LIKE).get(0) + LIKE);
      params.remove(POR_SYSTEM_LIKE);
    }
    if (params.containsKey(POR_INSTANCE)) {
      String[] porInstance = extractCommaSeparatedFields(params.get(POR_INSTANCE));
      query.porInstanceIn(porInstance);
      params.remove(POR_INSTANCE);
    }
    if (params.containsKey(POR_INSTANCE_LIKE)) {
      query.porInstanceLike(LIKE + params.get(POR_INSTANCE_LIKE).get(0) + LIKE);
      params.remove(POR_INSTANCE_LIKE);
    }
    if (params.containsKey(POR_TYPE)) {
      String[] porType = extractCommaSeparatedFields(params.get(POR_TYPE));
      query.porTypeIn(porType);
      params.remove(POR_TYPE);
    }
    if (params.containsKey(POR_TYPE_LIKE)) {
      query.porTypeLike(LIKE + params.get(POR_TYPE_LIKE).get(0) + LIKE);
      params.remove(POR_TYPE_LIKE);
    }
    if (params.containsKey(POR_VALUE)) {
      String[] porValue = extractCommaSeparatedFields(params.get(POR_VALUE));
      query.porValueIn(porValue);
      params.remove(POR_VALUE);
    }
    if (params.containsKey(POR_VALUE_LIKE)) {
      query.porValueLike(LIKE + params.get(POR_VALUE_LIKE).get(0) + LIKE);
      params.remove(POR_VALUE_LIKE);
    }
    if (params.containsKey(TASK_CLASSIFICATION_KEY)) {
      String[] taskClassificationKey =
          extractCommaSeparatedFields(params.get(TASK_CLASSIFICATION_KEY));
      query.taskClassificationKeyIn(taskClassificationKey);
      params.remove(TASK_CLASSIFICATION_KEY);
    }
    if (params.containsKey(TASK_CLASSIFICATION_KEY_LIKE)) {
      query.taskClassificationKeyLike(
          LIKE + params.get(TASK_CLASSIFICATION_KEY_LIKE).get(0) + LIKE);
      params.remove(TASK_CLASSIFICATION_KEY_LIKE);
    }
    if (params.containsKey(TASK_CLASSIFICATION_CATEGORY)) {
      String[] taskClassificationCategory =
          extractCommaSeparatedFields(params.get(TASK_CLASSIFICATION_CATEGORY));
      query.taskClassificationCategoryIn(taskClassificationCategory);
      params.remove(TASK_CLASSIFICATION_CATEGORY);
    }
    if (params.containsKey(TASK_CLASSIFICATION_CATEGORY_LIKE)) {
      query.taskClassificationCategoryLike(
          LIKE + params.get(TASK_CLASSIFICATION_CATEGORY_LIKE).get(0) + LIKE);
      params.remove(TASK_CLASSIFICATION_CATEGORY_LIKE);
    }
    if (params.containsKey(ATTACHMENT_CLASSIFICATION_KEY)) {
      String[] attachmentClassificationKey =
          extractCommaSeparatedFields(params.get(ATTACHMENT_CLASSIFICATION_KEY));
      query.attachmentClassificationKeyIn(attachmentClassificationKey);
      params.remove(ATTACHMENT_CLASSIFICATION_KEY);
    }
    if (params.containsKey(ATTACHMENT_CLASSIFICATION_KEY_LIKE)) {
      query.attachmentClassificationKeyLike(
          LIKE + params.get(ATTACHMENT_CLASSIFICATION_KEY_LIKE).get(0) + LIKE);
      params.remove(ATTACHMENT_CLASSIFICATION_KEY_LIKE);
    }
    for (TaskHistoryCustomField customField : TaskHistoryCustomField.values()) {
      List<String> list = params.remove(customField.name().replace("_", "-").toLowerCase());
      if (list != null) {
        query.customAttributeIn(customField, extractCommaSeparatedFields(list));
      }
      list = params.remove(customField.name().replace("_", "-").toLowerCase() + "-like");
      if (list != null) {
        String[] values = extractCommaSeparatedFields(list);
        for (int i = 0; i < values.length; i++) {
          values[i] = LIKE + values[i] + LIKE;
        }
        query.customAttributeLike(customField, values);
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", query);
    }
  }

  private TimeInterval getTimeIntervalOf(String[] created) {
    LocalDate begin;
    LocalDate end;
    try {
      begin = LocalDate.parse(created[0]);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Cannot parse String '"
              + created[0]
              + "'. Expected a String of the Format 'yyyy-MM-dd'.");
    }
    if (created.length < 2) {
      end = begin.plusDays(1);
    } else {
      end = LocalDate.parse(created[1]);
    }
    Instant beginInst = begin.atStartOfDay(ZoneId.systemDefault()).toInstant();
    Instant endInst = end.atStartOfDay(ZoneId.systemDefault()).toInstant();
    return new TimeInterval(beginInst, endInst);
  }
}
