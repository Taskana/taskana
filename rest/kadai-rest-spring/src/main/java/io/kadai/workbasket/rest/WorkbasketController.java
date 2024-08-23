package io.kadai.workbasket.rest;

import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.rest.QueryPagingParameter;
import io.kadai.common.rest.QuerySortBy;
import io.kadai.common.rest.QuerySortParameter;
import io.kadai.common.rest.RestEndpoints;
import io.kadai.common.rest.util.QueryParamsValidator;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketQuery;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import io.kadai.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import io.kadai.workbasket.api.exceptions.WorkbasketInUseException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import io.kadai.workbasket.rest.assembler.WorkbasketAccessItemRepresentationModelAssembler;
import io.kadai.workbasket.rest.assembler.WorkbasketRepresentationModelAssembler;
import io.kadai.workbasket.rest.assembler.WorkbasketSummaryRepresentationModelAssembler;
import io.kadai.workbasket.rest.models.DistributionTargetsCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketAccessItemCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for all {@link Workbasket} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class WorkbasketController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketController.class);

  private final WorkbasketService workbasketService;
  private final WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler;
  private final WorkbasketSummaryRepresentationModelAssembler
      workbasketSummaryRepresentationModelAssembler;
  private final WorkbasketAccessItemRepresentationModelAssembler
      workbasketAccessItemRepresentationModelAssembler;

  @Autowired
  WorkbasketController(
      WorkbasketService workbasketService,
      WorkbasketRepresentationModelAssembler workbasketRepresentationModelAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketSummaryRepresentationModelAssembler,
      WorkbasketAccessItemRepresentationModelAssembler
          workbasketAccessItemRepresentationModelAssembler) {
    this.workbasketService = workbasketService;
    this.workbasketRepresentationModelAssembler = workbasketRepresentationModelAssembler;
    this.workbasketSummaryRepresentationModelAssembler =
        workbasketSummaryRepresentationModelAssembler;

    this.workbasketAccessItemRepresentationModelAssembler =
        workbasketAccessItemRepresentationModelAssembler;
  }

  /**
   * This endpoint retrieves a list of existing Workbaskets. Filters can be applied.
   *
   * @title Get a list of all Workbaskets
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the Workbaskets with the given filter, sort and paging options.
   */
  @Operation(
      summary = "Get a list of all Workbaskets",
      description =
          "This endpoint retrieves a list of existing Workbaskets. Filters can be applied.",
      parameters = {@Parameter(name = "type", example = "PERSONAL")},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Found all Workbaskets",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema =
                      @Schema(implementation = WorkbasketSummaryPagedRepresentationModel.class))
            })
      })
  @GetMapping(path = RestEndpoints.URL_WORKBASKET)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketSummaryPagedRepresentationModel> getWorkbaskets(
      HttpServletRequest request,
      @ParameterObject WorkbasketQueryFilterParameter filterParameter,
      @ParameterObject WorkbasketQuerySortParameter sortParameter,
      @ParameterObject QueryPagingParameter<WorkbasketSummary, WorkbasketQuery> pagingParameter) {

    QueryParamsValidator.validateParams(
        request,
        WorkbasketQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    filterParameter.apply(query);
    sortParameter.apply(query);

    List<WorkbasketSummary> workbasketSummaries = pagingParameter.apply(query);
    WorkbasketSummaryPagedRepresentationModel pagedModels =
        workbasketSummaryRepresentationModelAssembler.toPagedModel(
            workbasketSummaries, pagingParameter.getPageMetadata());

    return ResponseEntity.ok(pagedModels);
  }

  /**
   * This endpoint retrieves a single Workbasket.
   *
   * @title Get a single Workbasket
   * @param workbasketId the Id of the requested Workbasket
   * @return the requested Workbasket
   * @throws WorkbasketNotFoundException if the requested Workbasket is not found
   * @throws NotAuthorizedOnWorkbasketException if the current user has no permissions to access the
   *     requested Workbasket
   */
  @Operation(
      summary = "Get a single Workbasket",
      description = "This endpoint retrieves a single Workbasket.",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the requested Workbasket",
            required = true,
            example = "WBI:100000000000000000000000000000000001")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the requested Workbasket",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = WorkbasketRepresentationModel.class))
            })
      })
  @GetMapping(path = RestEndpoints.URL_WORKBASKET_ID, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> getWorkbasket(
      @PathVariable("workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedOnWorkbasketException {
    Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);

    return ResponseEntity.ok(workbasketRepresentationModelAssembler.toModel(workbasket));
  }

  /**
   * This endpoint deletes an existing Workbasket.
   *
   * <p>Returned HTTP Status codes:
   *
   * <ul>
   *   <li><b>204 NO_CONTENT</b> - Workbasket has been deleted successfully
   *   <li><b>202 ACCEPTED</b> - Workbasket still contains completed Tasks. It has been marked for
   *       deletion and will be deleted automatically as soon as all completed Tasks are deleted.
   *   <li><b>423 LOCKED</b> - Workbasket contains non-completed Tasks and cannot be deleted.
   * </ul>
   *
   * @title Delete a Workbasket
   * @param workbasketId the Id of the Workbasket which should be deleted
   * @return the deleted Workbasket
   * @throws NotAuthorizedOnWorkbasketException if the current user is not authorized to delete this
   *     Workbasket.
   * @throws InvalidArgumentException if the requested Workbasket Id is null or empty
   * @throws WorkbasketNotFoundException if the requested Workbasket is not found
   * @throws WorkbasketInUseException if the Workbasket contains tasks.
   * @throws NotAuthorizedException if the current user has not correct permissions
   */
  @Operation(
      summary = "Delete a Workbasket",
      description = "This endpoint deletes an existing Workbasket",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the requested Workbasket",
            required = true,
            example = "WBI:100000000000000000000000000000000002")
      },
      responses = {
        @ApiResponse(
            responseCode = "204",
            description = "<b>204 NO_CONTENT</b> - Workbasket has been deleted successfully",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "202",
            description =
                "<b>202 ACCEPTED</b> - Workbasket still contains completed Tasks. It has been "
                    + "marked for deletion and will be deleted automatically as soon as all "
                    + "completed Tasks are deleted.",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "423",
            description =
                "<b>423 LOCKED</b> - Workbasket contains non-completed Tasks and cannot be "
                    + "deleted.",
            content = @Content(schema = @Schema()))
      })
  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class, noRollbackFor = WorkbasketNotFoundException.class)
  public ResponseEntity<WorkbasketRepresentationModel> deleteWorkbasket(
      @PathVariable("workbasketId") String workbasketId)
      throws InvalidArgumentException,
          WorkbasketNotFoundException,
          WorkbasketInUseException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException {

    boolean workbasketDeleted = workbasketService.deleteWorkbasket(workbasketId);

    if (workbasketDeleted) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Workbasket successfully deleted.");
      }
      return ResponseEntity.noContent().build();
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Workbasket was only marked for deletion and will be physically deleted later on.");
      }
      return ResponseEntity.accepted().build();
    }
  }

  /**
   * This endpoint creates a persistent Workbasket.
   *
   * @title Create a new Workbasket
   * @param workbasketRepresentationModel the Workbasket which should be created.
   * @return the created Workbasket
   * @throws InvalidArgumentException if some required properties of the Workbasket are not set.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketAlreadyExistException if the Workbasket exists already
   * @throws DomainNotFoundException if the domain does not exist in the configuration.
   */
  @Operation(
      summary = "Create a new Workbasket",
      description = "This endpoint creates a persistent Workbasket.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "the Workbasket which should be created.",
              content =
                  @Content(
                      schema = @Schema(implementation = WorkbasketRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"key\" : \"asdasdasd\",\n"
                                      + "  \"name\" : \"this is a wonderful workbasket name\",\n"
                                      + "  \"domain\" : \"DOMAIN_A\",\n"
                                      + "  \"type\" : \"GROUP\",\n"
                                      + "  \"markedForDeletion\" : false\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "the created Workbasket",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = WorkbasketRepresentationModel.class))
            })
      })
  @PostMapping(path = RestEndpoints.URL_WORKBASKET)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> createWorkbasket(
      @RequestBody WorkbasketRepresentationModel workbasketRepresentationModel)
      throws InvalidArgumentException,
          NotAuthorizedException,
          WorkbasketAlreadyExistException,
          DomainNotFoundException {
    Workbasket workbasket =
        workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
    workbasket = workbasketService.createWorkbasket(workbasket);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(workbasketRepresentationModelAssembler.toModel(workbasket));
  }

  /**
   * This endpoint updates a given Workbasket.
   *
   * @title Update a Workbasket
   * @param workbasketId the Id of the Workbasket which should be updated.
   * @param workbasketRepresentationModel the new Workbasket for the requested id.
   * @return the updated Workbasket
   * @throws InvalidArgumentException if the requested Id and the Id within the new Workbasket do
   *     not match.
   * @throws WorkbasketNotFoundException if the requested workbasket does not
   * @throws NotAuthorizedException if the current user is not authorized to update the Workbasket
   * @throws ConcurrencyException if an attempt is made to update the Workbasket and another user
   *     updated it already
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   */
  @Operation(
      summary = "Update a Workbasket",
      description = "This endpoint creates a persistent Workbasket.",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the requested Workbasket",
            required = true,
            example = "WBI:100000000000000000000000000000000001")
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "the new Workbasket for the requested id",
              content =
                  @Content(
                      schema = @Schema(implementation = WorkbasketRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"workbasketId\" : "
                                      + "\"WBI:100000000000000000000000000000000001\",\n"
                                      + "  \"key\" : \"GPK_KSC\",\n"
                                      + "  \"name\" : \"new name\",\n"
                                      + "  \"domain\" : \"DOMAIN_A\",\n"
                                      + "  \"type\" : \"GROUP\",\n"
                                      + "  \"description\" : \"Gruppenpostkorb KSC\",\n"
                                      + "  \"owner\" : \"teamlead-1\",\n"
                                      + "  \"custom1\" : \"ABCQVW\",\n"
                                      + "  \"custom2\" : \"\",\n"
                                      + "  \"custom3\" : \"xyz4\",\n"
                                      + "  \"custom4\" : \"\",\n"
                                      + "  \"custom5\" : \"\",\n"
                                      + "  \"custom6\" : \"\",\n"
                                      + "  \"custom7\" : \"\",\n"
                                      + "  \"custom8\" : \"\",\n"
                                      + "  \"orgLevel1\" : \"\",\n"
                                      + "  \"orgLevel2\" : \"\",\n"
                                      + "  \"orgLevel3\" : \"\",\n"
                                      + "  \"orgLevel4\" : \"\",\n"
                                      + "  \"markedForDeletion\" : false,\n"
                                      + "  \"created\" : \"2018-02-01T12:00:00.000Z\",\n"
                                      + "  \"modified\" : \"2018-02-01T12:00:00.000Z\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the requested Workbasket",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = WorkbasketRepresentationModel.class))
            })
      })
  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketRepresentationModel> updateWorkbasket(
      @PathVariable("workbasketId") String workbasketId,
      @RequestBody WorkbasketRepresentationModel workbasketRepresentationModel)
      throws WorkbasketNotFoundException,
          NotAuthorizedException,
          ConcurrencyException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {
    if (!workbasketId.equals(workbasketRepresentationModel.getWorkbasketId())) {
      throw new InvalidArgumentException(
          "Target-WB-ID('"
              + workbasketId
              + "') is not identical with the WB-ID of to object which should be updated. ID=('"
              + workbasketRepresentationModel.getWorkbasketId()
              + "')");
    }
    Workbasket workbasket =
        workbasketRepresentationModelAssembler.toEntityModel(workbasketRepresentationModel);
    workbasket = workbasketService.updateWorkbasket(workbasket);

    return ResponseEntity.ok(workbasketRepresentationModelAssembler.toModel(workbasket));
  }

  /**
   * This endpoint retrieves all Workbasket Access Items for a given Workbasket.
   *
   * @title Get all Workbasket Access Items
   * @param workbasketId the Id of the requested Workbasket.
   * @return the access items for the requested Workbasket.
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist.
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   */
  @Operation(
      summary = "Get all Workbasket Access Items",
      description = "This endpoint retrieves all Workbasket Access Items for a given Workbasket.",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the requested Workbasket",
            required = true,
            example = "WBI:100000000000000000000000000000000001")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the access items for the requested Workbasket.",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema =
                      @Schema(
                          implementation = WorkbasketAccessItemCollectionRepresentationModel.class))
            })
      })
  @GetMapping(
      path = RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
      produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> getWorkbasketAccessItems(
      @PathVariable("workbasketId") String workbasketId)
      throws WorkbasketNotFoundException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException {
    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);

    return ResponseEntity.ok(
        workbasketAccessItemRepresentationModelAssembler.toKadaiCollectionModelForSingleWorkbasket(
            workbasketId, accessItems));
  }

  /**
   * This endpoint replaces all Workbasket Access Items for a given Workbasket with the provided
   * ones.
   *
   * @title Set all Workbasket Access Items
   * @param workbasketId the Id of the Workbasket whose Workbasket Access Items will be replaced
   * @param workbasketAccessItemRepModels the new Workbasket Access Items.
   * @return the new Workbasket Access Items for the requested Workbasket
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   * @throws InvalidArgumentException if the new Workbasket Access Items are not provided.
   * @throws WorkbasketNotFoundException TODO: this is never thrown.
   * @throws WorkbasketAccessItemAlreadyExistException if a duplicate Workbasket Access Item exists
   *     in the provided list.
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   */
  @Operation(
      summary = "Set all Workbasket Access Items",
      description =
          "This endpoint replaces all Workbasket Access Items for a given Workbasket with the "
              + "provided",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the Workbasket whose Workbasket Access Items will be replaced",
            required = true,
            example = "WBI:100000000000000000000000000000000001")
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "the new Workbasket Access Items.",
              content =
                  @Content(
                      schema =
                          @Schema(
                              implementation =
                                  WorkbasketAccessItemCollectionRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"accessItems\" : [ {\n"
                                      + "    \"workbasketId\" : "
                                      + "\"WBI:100000000000000000000000000000000001\",\n"
                                      + "    \"accessId\" : \"new-access-id\",\n"
                                      + "    \"accessName\" : \"new-access-name\",\n"
                                      + "    \"permRead\" : false,\n"
                                      + "    \"permReadTasks\" : false,\n"
                                      + "    \"permOpen\" : true,\n"
                                      + "    \"permAppend\" : false,\n"
                                      + "    \"permEditTasks\" : false,\n"
                                      + "    \"permTransfer\" : false,\n"
                                      + "    \"permDistribute\" : false,\n"
                                      + "    \"permCustom1\" : false,\n"
                                      + "    \"permCustom2\" : false,\n"
                                      + "    \"permCustom3\" : false,\n"
                                      + "    \"permCustom4\" : false,\n"
                                      + "    \"permCustom5\" : false,\n"
                                      + "    \"permCustom6\" : false,\n"
                                      + "    \"permCustom7\" : false,\n"
                                      + "    \"permCustom8\" : false,\n"
                                      + "    \"permCustom9\" : false,\n"
                                      + "    \"permCustom10\" : false,\n"
                                      + "    \"permCustom11\" : false,\n"
                                      + "    \"permCustom12\" : false\n"
                                      + "  } ]\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the new Workbasket Access Items for the requested Workbasket",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema =
                      @Schema(
                          implementation = WorkbasketAccessItemCollectionRepresentationModel.class))
            })
      })
  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> setWorkbasketAccessItems(
      @PathVariable("workbasketId") String workbasketId,
      @RequestBody WorkbasketAccessItemCollectionRepresentationModel workbasketAccessItemRepModels)
      throws InvalidArgumentException,
          WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException {
    if (workbasketAccessItemRepModels == null) {
      throw new InvalidArgumentException("Can't create something with NULL body-value.");
    }

    List<WorkbasketAccessItem> wbAccessItems =
        workbasketAccessItemRepModels.getContent().stream()
            .map(workbasketAccessItemRepresentationModelAssembler::toEntityModel)
            .toList();
    workbasketService.setWorkbasketAccessItems(workbasketId, wbAccessItems);
    List<WorkbasketAccessItem> updatedWbAccessItems =
        workbasketService.getWorkbasketAccessItems(workbasketId);

    return ResponseEntity.ok(
        workbasketAccessItemRepresentationModelAssembler.toKadaiCollectionModelForSingleWorkbasket(
            workbasketId, updatedWbAccessItems));
  }

  /**
   * This endpoint retrieves all Distribution Targets for a requested Workbasket.
   *
   * @title Get all Distribution Targets for a Workbasket
   * @param workbasketId the Id of the Workbasket whose Distribution Targets will be retrieved
   * @return the Distribution Targets for the requested Workbasket
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     specified Workbasket
   */
  @Operation(
      summary = "Get all Distribution Targets for a Workbasket",
      description = "This endpoint retrieves all Distribution Targets for a requested Workbasket.",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the Workbasket whose Distribution Targets will be retrieved",
            required = true,
            example = "WBI:100000000000000000000000000000000002")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the Distribution Targets for the requested Workbasket",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema =
                      @Schema(
                          implementation = DistributionTargetsCollectionRepresentationModel.class))
            })
      })
  @GetMapping(
      path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
      produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetsCollectionRepresentationModel> getDistributionTargets(
      @PathVariable("workbasketId") String workbasketId)
      throws WorkbasketNotFoundException, NotAuthorizedOnWorkbasketException {
    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasketId);
    DistributionTargetsCollectionRepresentationModel distributionTargetRepModels =
        workbasketSummaryRepresentationModelAssembler.toKadaiCollectionModel(distributionTargets);

    return ResponseEntity.ok(distributionTargetRepModels);
  }

  /**
   * This endpoint replaces all Distribution Targets for a given Workbasket with the provided ones.
   *
   * @title Set all Distribution Targets for a Workbasket
   * @param sourceWorkbasketId the source Workbasket
   * @param targetWorkbasketIds the destination Workbaskets.
   * @return the new Distribution Targets for the requested Workbasket.
   * @throws WorkbasketNotFoundException if any Workbasket was not found (either source or target)
   * @throws NotAuthorizedOnWorkbasketException if the current user doesn't have READ permission for
   *     the source Workbasket
   * @throws NotAuthorizedException if the current user has not correct permissions
   */
  @Operation(
      summary = "Set all Distribution Targets for a Workbasket",
      description =
          "This endpoint replaces all Distribution Targets for a given Workbasket with the "
              + "provided ones.",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the source Workbasket",
            required = true,
            example = "WBI:100000000000000000000000000000000001")
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "the destination Workbaskets.",
              content =
                  @Content(
                      array = @ArraySchema(schema = @Schema(implementation = String.class)),
                      examples =
                          @ExampleObject(
                              value =
                                  "[ \"WBI:100000000000000000000000000000000002\", "
                                      + "\"WBI:100000000000000000000000000000000003\" ]"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the new Distribution Targets for the requested Workbasket.",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema =
                      @Schema(
                          implementation = DistributionTargetsCollectionRepresentationModel.class))
            })
      })
  @PutMapping(path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetsCollectionRepresentationModel>
      setDistributionTargetsForWorkbasketId(
          @PathVariable("workbasketId") String sourceWorkbasketId,
          @RequestBody List<String> targetWorkbasketIds)
          throws WorkbasketNotFoundException,
              NotAuthorizedException,
              NotAuthorizedOnWorkbasketException {
    workbasketService.setDistributionTargets(sourceWorkbasketId, targetWorkbasketIds);

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasketId);

    return ResponseEntity.ok(
        workbasketSummaryRepresentationModelAssembler.toKadaiCollectionModel(distributionTargets));
  }

  /**
   * This endpoint removes all Distribution Target references for a provided Workbasket.
   *
   * @title Remove a Workbasket as Distribution Target
   * @param targetWorkbasketId the Id of the requested Workbasket.
   * @return no content
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist.
   * @throws NotAuthorizedException if the requested user ist not ADMIN or BUSINESS_ADMIN.
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   */
  @Operation(
      summary = "Remove a Workbasket as Distribution Target",
      description =
          "This endpoint removes all Distribution Target references for a provided Workbasket.",
      parameters = {
        @Parameter(
            name = "workbasketId",
            description = "the Id of the requested Workbasket.",
            required = true,
            example = "WBI:100000000000000000000000000000000007")
      })
  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> removeDistributionTargetForWorkbasketId(
      @PathVariable("workbasketId") String targetWorkbasketId)
      throws WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          NotAuthorizedException {
    List<WorkbasketSummary> sourceWorkbaskets =
        workbasketService.getDistributionSources(targetWorkbasketId);
    for (WorkbasketSummary source : sourceWorkbaskets) {
      workbasketService.removeDistributionTarget(source.getId(), targetWorkbasketId);
    }

    return ResponseEntity.noContent().build();
  }

  public enum WorkbasketQuerySortBy implements QuerySortBy<WorkbasketQuery> {
    NAME(WorkbasketQuery::orderByName),
    KEY(WorkbasketQuery::orderByKey),
    OWNER(WorkbasketQuery::orderByOwner),
    TYPE(WorkbasketQuery::orderByType),
    DESCRIPTION(WorkbasketQuery::orderByDescription),
    CUSTOM_1((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_1, sort)),
    CUSTOM_2((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_2, sort)),
    CUSTOM_3((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_3, sort)),
    CUSTOM_4((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_4, sort)),
    CUSTOM_5((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_5, sort)),
    CUSTOM_6((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_6, sort)),
    CUSTOM_7((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_7, sort)),
    CUSTOM_8((query, sort) -> query.orderByCustomAttribute(WorkbasketCustomField.CUSTOM_8, sort)),
    DOMAIN(WorkbasketQuery::orderByDomain),
    ORG_LEVEL_1(WorkbasketQuery::orderByOrgLevel1),
    ORG_LEVEL_2(WorkbasketQuery::orderByOrgLevel2),
    ORG_LEVEL_3(WorkbasketQuery::orderByOrgLevel3),
    ORG_LEVEL_4(WorkbasketQuery::orderByOrgLevel4);

    private final BiConsumer<WorkbasketQuery, SortDirection> consumer;

    WorkbasketQuerySortBy(BiConsumer<WorkbasketQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(WorkbasketQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the generic 'sort-by'
  // parameter from the super class.
  public static class WorkbasketQuerySortParameter
      extends QuerySortParameter<WorkbasketQuery, WorkbasketQuerySortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public WorkbasketQuerySortParameter(
        List<WorkbasketQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<WorkbasketQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
