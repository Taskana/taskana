package io.kadai.workbasket.rest;

import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.rest.QueryPagingParameter;
import io.kadai.common.rest.QuerySortBy;
import io.kadai.common.rest.QuerySortParameter;
import io.kadai.common.rest.RestEndpoints;
import io.kadai.common.rest.ldap.LdapClient;
import io.kadai.common.rest.util.QueryParamsValidator;
import io.kadai.workbasket.api.WorkbasketAccessItemQuery;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import io.kadai.workbasket.rest.assembler.WorkbasketAccessItemRepresentationModelAssembler;
import io.kadai.workbasket.rest.models.WorkbasketAccessItemPagedRepresentationModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for Workbasket access. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class WorkbasketAccessItemController {

  private final LdapClient ldapClient;
  private final WorkbasketService workbasketService;
  private final WorkbasketAccessItemRepresentationModelAssembler modelAssembler;

  @Autowired
  public WorkbasketAccessItemController(
      LdapClient ldapClient,
      WorkbasketService workbasketService,
      WorkbasketAccessItemRepresentationModelAssembler modelAssembler) {
    this.ldapClient = ldapClient;
    this.workbasketService = workbasketService;
    this.modelAssembler = modelAssembler;
  }

  /**
   * This endpoint retrieves a list of existing Workbasket Access Items. Filters can be applied.
   *
   * @title Get a list of all Workbasket Access Items
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the Workbasket Access Items with the given filter, sort and paging options.
   * @throws NotAuthorizedException if the user is not authorized.
   */
  @Operation(
      summary = "Get a list of all Workbasket Access Items",
      description =
          "This endpoint retrieves a list of existing Workbasket Access Items. Filters can be "
              + "applied.",
      parameters = {
        @Parameter(name = "sort-by", example = "WORKBASKET_KEY"),
        @Parameter(name = "order", example = "ASCENDING"),
        @Parameter(name = "access-id", example = "user-2-2")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "the Workbasket Access Items with the given filter, sort and paging options.",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema =
                      @Schema(implementation = WorkbasketAccessItemPagedRepresentationModel.class))
            })
      })
  @GetMapping(path = RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
  public ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> getWorkbasketAccessItems(
      HttpServletRequest request,
      @ParameterObject WorkbasketAccessItemQueryFilterParameter filterParameter,
      @ParameterObject WorkbasketAccessItemQuerySortParameter sortParameter,
      @ParameterObject
          QueryPagingParameter<WorkbasketAccessItem, WorkbasketAccessItemQuery> pagingParameter)
      throws NotAuthorizedException {

    QueryParamsValidator.validateParams(
        request,
        WorkbasketAccessItemQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    WorkbasketAccessItemQuery query = workbasketService.createWorkbasketAccessItemQuery();
    filterParameter.apply(query);
    sortParameter.apply(query);

    List<WorkbasketAccessItem> workbasketAccessItems = pagingParameter.apply(query);

    WorkbasketAccessItemPagedRepresentationModel pagedResources =
        modelAssembler.toPagedModel(workbasketAccessItems, pagingParameter.getPageMetadata());

    return ResponseEntity.ok(pagedResources);
  }

  /**
   * This endpoint deletes all Workbasket Access Items for a provided Access Id.
   *
   * @title Delete a Workbasket Access Item
   * @param accessId the Access Id whose Workbasket Access Items should be removed
   * @return no content
   * @throws NotAuthorizedException if the user is not authorized.
   * @throws InvalidArgumentException if some argument is invalid.
   */
  @Operation(
      summary = "Delete a Workbasket Access Item",
      description = "This endpoint deletes all Workbasket Access Items for a provided Access Id.",
      parameters = {
        @Parameter(
            name = "accessId",
            description = "the Access Id whose Workbasket Access Items should be removed",
            example = "user-2-1",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "204",
            content = {@Content(schema = @Schema())})
      })
  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
  public ResponseEntity<Void> removeWorkbasketAccessItems(
      @RequestParam("access-id") String accessId)
      throws NotAuthorizedException, InvalidArgumentException {
    if (ldapClient.isUser(accessId)) {
      List<WorkbasketAccessItem> workbasketAccessItemList =
          workbasketService.createWorkbasketAccessItemQuery().accessIdIn(accessId).list();

      if (workbasketAccessItemList != null && !workbasketAccessItemList.isEmpty()) {
        workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);
      }
    } else {
      throw new InvalidArgumentException(
          String.format(
              "AccessId '%s' is not a user. " + "You can remove all access items for users only.",
              accessId));
    }

    return ResponseEntity.noContent().build();
  }

  public enum WorkbasketAccessItemSortBy implements QuerySortBy<WorkbasketAccessItemQuery> {
    WORKBASKET_KEY(WorkbasketAccessItemQuery::orderByWorkbasketKey),
    ACCESS_ID(WorkbasketAccessItemQuery::orderByAccessId);

    private final BiConsumer<WorkbasketAccessItemQuery, SortDirection> consumer;

    WorkbasketAccessItemSortBy(BiConsumer<WorkbasketAccessItemQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(WorkbasketAccessItemQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the generic 'sort-by'
  // parameter from the super class.
  public static class WorkbasketAccessItemQuerySortParameter
      extends QuerySortParameter<WorkbasketAccessItemQuery, WorkbasketAccessItemSortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public WorkbasketAccessItemQuerySortParameter(
        List<WorkbasketAccessItemSortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<WorkbasketAccessItemSortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
