package pro.taskana.simplehistory.rest;

import java.beans.ConstructorProperties;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.QueryPagingParameter;
import pro.taskana.common.rest.QuerySortBy;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.util.QueryParamsValidator;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.simplehistory.rest.assembler.TaskHistoryEventRepresentationModelAssembler;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventPagedRepresentationModel;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventRepresentationModel;
import pro.taskana.spi.history.api.events.task.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;

/** Controller for all TaskHistoryEvent related endpoints. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class TaskHistoryEventController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskHistoryEventController.class);

  private final SimpleHistoryServiceImpl simpleHistoryService;
  private final TaskHistoryEventRepresentationModelAssembler assembler;

  @Autowired
  public TaskHistoryEventController(
      TaskanaEngineConfiguration taskanaEngineConfiguration,
      SimpleHistoryServiceImpl simpleHistoryServiceImpl,
      TaskHistoryEventRepresentationModelAssembler assembler)
      throws SQLException {

    this.simpleHistoryService = simpleHistoryServiceImpl;
    this.simpleHistoryService.initialize(taskanaEngineConfiguration.buildTaskanaEngine());
    this.assembler = assembler;
  }

  /**
   * This endpoint retrieves a list of existing Task History Events. Filters can be applied.
   *
   * @title Get a list of all Task History Events
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the Task History Events with the given filter, sort and paging options.
   */
  @GetMapping(path = HistoryRestEndpoints.URL_HISTORY_EVENTS, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskHistoryEventPagedRepresentationModel> getTaskHistoryEvents(
      HttpServletRequest request,
      TaskHistoryQueryFilterParameter filterParameter,
      TaskHistoryQuerySortParameter sortParameter,
      QueryPagingParameter<TaskHistoryEvent, TaskHistoryQuery> pagingParameter) {

    QueryParamsValidator.validateParams(
        request,
        TaskHistoryQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    TaskHistoryQuery query = simpleHistoryService.createTaskHistoryQuery();
    filterParameter.applyToQuery(query);
    sortParameter.applyToQuery(query);

    List<TaskHistoryEvent> historyEvents = pagingParameter.applyToQuery(query);

    TaskHistoryEventPagedRepresentationModel pagedResources =
        assembler.toPagedModel(historyEvents, pagingParameter.getPageMetadata());

    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        ResponseEntity.ok(pagedResources);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTaskHistoryEvents(), returning {}", response);
    }

    return response;
  }

  /**
   * This endpoint retrieves a single Task History Event.
   *
   * @title Get a single Task History Event
   * @param historyEventId the Id of the requested Task History Event.
   * @return the requested Task History Event
   * @throws TaskanaHistoryEventNotFoundException If a Task History Event can't be found by the
   *     provided historyEventId
   */
  @GetMapping(path = HistoryRestEndpoints.URL_HISTORY_EVENTS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskHistoryEventRepresentationModel> getTaskHistoryEvent(
      @PathVariable String historyEventId) throws TaskanaHistoryEventNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskHistoryEvent(historyEventId= {})", historyEventId);
    }

    TaskHistoryEvent resultEvent = simpleHistoryService.getTaskHistoryEvent(historyEventId);

    TaskHistoryEventRepresentationModel taskEventResource = assembler.toModel(resultEvent);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Exit from getTaskHistoryEvent, returning {}",
          new ResponseEntity<>(taskEventResource, HttpStatus.OK));
    }

    return new ResponseEntity<>(taskEventResource, HttpStatus.OK);
  }

  public enum TaskHistoryQuerySortBy implements QuerySortBy<TaskHistoryQuery> {
    TASK_HISTORY_EVENT_ID(TaskHistoryQuery::orderByTaskHistoryEventId),
    BUSINESS_PROCESS_ID(TaskHistoryQuery::orderByBusinessProcessId),
    PARENT_BUSINESS_PROCESS_ID(TaskHistoryQuery::orderByParentBusinessProcessId),
    TASK_ID(TaskHistoryQuery::orderByTaskId),
    EVENT_TYPE(TaskHistoryQuery::orderByEventType),
    CREATED(TaskHistoryQuery::orderByCreated),
    USER_ID(TaskHistoryQuery::orderByUserId),
    DOMAIN(TaskHistoryQuery::orderByDomain),
    WORKBASKET_KEY(TaskHistoryQuery::orderByWorkbasketKey),
    POR_COMPANY(TaskHistoryQuery::orderByPorCompany),
    POR_SYSTEM(TaskHistoryQuery::orderByPorSystem),
    POR_INSTANCE(TaskHistoryQuery::orderByPorInstance),
    POR_TYPE(TaskHistoryQuery::orderByPorType),
    POR_VALUE(TaskHistoryQuery::orderByPorValue),
    TASK_CLASSIFICATION_KEY(TaskHistoryQuery::orderByTaskClassificationKey),
    TASK_CLASSIFICATION_CATEGORY(TaskHistoryQuery::orderByTaskClassificationCategory),
    ATTACHMENT_CLASSIFICATION_KEY(TaskHistoryQuery::orderByAttachmentClassificationKey),
    CUSTOM_1((query, sort) -> query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_1, sort)),
    CUSTOM_2((query, sort) -> query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_2, sort)),
    CUSTOM_3((query, sort) -> query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_3, sort)),
    CUSTOM_4((query, sort) -> query.orderByCustomAttribute(TaskHistoryCustomField.CUSTOM_4, sort));

    private final BiConsumer<TaskHistoryQuery, SortDirection> consumer;

    TaskHistoryQuerySortBy(BiConsumer<TaskHistoryQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(TaskHistoryQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the generic 'sort-by'
  // parameter from the super class.
  public static class TaskHistoryQuerySortParameter
      extends QuerySortParameter<TaskHistoryQuery, TaskHistoryQuerySortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public TaskHistoryQuerySortParameter(
        List<TaskHistoryQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<TaskHistoryQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
