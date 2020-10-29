package pro.taskana.workbasket.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.AbstractPagingController;
import pro.taskana.common.rest.QueryHelper;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.workbasket.api.WorkbasketAccessItemQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.rest.assembler.WorkbasketAccessItemRepresentationModelAssembler;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;

/** Controller for Workbasket access. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class WorkbasketAccessItemController extends AbstractPagingController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WorkbasketAccessItemController.class);

  private static final String LIKE = "%";
  private static final String WORKBASKET_KEY = "workbasket-key";
  private static final String WORKBASKET_KEY_LIKE = "workbasket-key-like";
  private static final String ACCESS_ID = "access-id";
  private static final String ACCESS_ID_LIKE = "access-id-like";
  private static final String ACCESS_IDS = "access-ids";

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
   * This GET method return all workbasketAccessItems that correspond the given data.
   *
   * @param params filter, order and access ids.
   * @return all WorkbasketAccesItemResource.
   * @throws NotAuthorizedException if the user is not authorized.
   * @throws InvalidArgumentException if some argument is invalid.
   */
  @GetMapping(path = RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
  public ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>>
      getWorkbasketAccessItems(@RequestParam MultiValueMap<String, String> params)
          throws NotAuthorizedException, InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getWorkbasketAccessItems(params= {})", params);
    }

    WorkbasketAccessItemQuery query = workbasketService.createWorkbasketAccessItemQuery();
    applyAccessIdIn(query, params);
    applyFilterParams(query, params);
    applySortingParams(query, params);

    PageMetadata pageMetadata = getPageMetadata(params, query);
    List<WorkbasketAccessItem> workbasketAccessItems = getQueryList(query, pageMetadata);

    TaskanaPagedModel<WorkbasketAccessItemRepresentationModel> pagedResources =
        modelAssembler.toPageModel(workbasketAccessItems, pageMetadata);

    ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>> response =
        ResponseEntity.ok(pagedResources);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getWorkbasketAccessItems(), returning {}", response);
    }

    return response;
  }

  /**
   * This DELETE method delete all workbasketAccessItems that correspond the given accessId.
   *
   * @param accessId which need remove his workbasketAccessItems.
   * @return ResponseEntity if the user is not authorized.
   * @throws NotAuthorizedException if the user is not authorized.
   * @throws InvalidArgumentException if some argument is invalid.
   */
  @DeleteMapping(path = RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
  public ResponseEntity<Void> removeWorkbasketAccessItems(
      @RequestParam("access-id") String accessId)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to removeWorkbasketAccessItems(access-id= {})", accessId);
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

    ResponseEntity<Void> response = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from removeWorkbasketAccessItems(), returning {}", response);
    return response;
  }

  private void applyAccessIdIn(
      WorkbasketAccessItemQuery query, MultiValueMap<String, String> params) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getAccessIds(query= {}, params= {})", query, params);
    }

    if (params.containsKey(ACCESS_IDS)) {
      String[] accessIds = extractVerticalBarSeparatedFields(params.get(ACCESS_IDS));
      query.accessIdIn(accessIds);
      params.remove(ACCESS_IDS);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getAccessIds(), returning {}", query);
    }
  }

  private void applyFilterParams(
      WorkbasketAccessItemQuery query, MultiValueMap<String, String> params) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applyFilterParams(query= {}, params= {})", query, params);
    }

    if (params.containsKey(WORKBASKET_KEY)) {
      String[] keys = extractCommaSeparatedFields(params.get(WORKBASKET_KEY));
      query.workbasketKeyIn(keys);
      params.remove(WORKBASKET_KEY);
    }
    if (params.containsKey(WORKBASKET_KEY_LIKE)) {
      query.workbasketKeyLike(LIKE + params.get(WORKBASKET_KEY_LIKE).get(0) + LIKE);
      params.remove(WORKBASKET_KEY_LIKE);
    }
    if (params.containsKey(ACCESS_ID)) {
      String[] accessId = extractCommaSeparatedFields(params.get(ACCESS_ID));
      query.accessIdIn(accessId);
      params.remove(ACCESS_ID);
    }
    if (params.containsKey(ACCESS_ID_LIKE)) {
      query.accessIdLike(LIKE + params.get(ACCESS_ID_LIKE).get(0) + LIKE);
      params.remove(ACCESS_ID_LIKE);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyFilterParams(), returning {}", query);
    }
  }

  private void applySortingParams(
      WorkbasketAccessItemQuery query, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applySortingParams(query= {}, params= {})", query, params);
    }

    QueryHelper.applyAndRemoveSortingParams(
        params,
        (sortBy, sortDirection) -> {
          switch (sortBy) {
            case (WORKBASKET_KEY):
              query.orderByWorkbasketKey(sortDirection);
              break;
            case (ACCESS_ID):
              query.orderByAccessId(sortDirection);
              break;
            default:
              throw new InvalidArgumentException("Unknown order '" + sortBy + "'");
          }
        });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", query);
    }
  }

  private String[] extractVerticalBarSeparatedFields(List<String> searchFor) {
    List<String> values = new ArrayList<>();
    if (searchFor != null) {
      searchFor.forEach(item -> Collections.addAll(values, item.split("\\|")));
    }
    return values.toArray(new String[0]);
  }
}
