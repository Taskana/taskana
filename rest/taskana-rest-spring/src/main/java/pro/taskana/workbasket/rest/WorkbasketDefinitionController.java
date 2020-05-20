package pro.taskana.workbasket.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.Mapping;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.assembler.WorkbasketDefinitionRepresentationModelAssembler;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;

/** Controller for all {@link WorkbasketDefinitionRepresentationModel} related endpoints. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class WorkbasketDefinitionController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WorkbasketDefinitionController.class);

  private final WorkbasketService workbasketService;

  private final WorkbasketDefinitionRepresentationModelAssembler workbasketDefinitionAssembler;

  WorkbasketDefinitionController(
      WorkbasketService workbasketService,
      WorkbasketDefinitionRepresentationModelAssembler workbasketDefinitionAssembler) {
    this.workbasketService = workbasketService;
    this.workbasketDefinitionAssembler = workbasketDefinitionAssembler;
  }

  @GetMapping(path = Mapping.URL_WORKBASKETDEFIITIONS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<WorkbasketDefinitionRepresentationModel>> exportWorkbaskets(
      @RequestParam(required = false) String domain)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    LOGGER.debug("Entry to exportWorkbaskets(domain= {})", domain);
    WorkbasketQuery workbasketQuery = workbasketService.createWorkbasketQuery();
    List<WorkbasketSummary> workbasketSummaryList =
        domain != null ? workbasketQuery.domainIn(domain).list() : workbasketQuery.list();
    List<WorkbasketDefinitionRepresentationModel> basketExports = new ArrayList<>();
    for (WorkbasketSummary summary : workbasketSummaryList) {
      Workbasket workbasket = workbasketService.getWorkbasket(summary.getId());
      basketExports.add(workbasketDefinitionAssembler.toModel(workbasket));
    }

    ResponseEntity<List<WorkbasketDefinitionRepresentationModel>> response =
        ResponseEntity.ok(basketExports);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from exportWorkbaskets(), returning {}", response);
    }

    return response;
  }

  /**
   * This method imports a <b>list of {@link WorkbasketDefinitionRepresentationModel}</b>. This does
   * not exactly match the REST norm, but we want to have an option to import all settings at once.
   * When a logical equal (key and domain are equal) workbasket already exists an update will be
   * executed. Otherwise a new workbasket will be created.
   *
   * @param file the list of workbasket definitions which will be imported to the current system.
   * @return Return answer is determined by the status code: 200 - all good 400 - list state error
   *     (referring to non existing id's) 401 - not authorized
   * @throws IOException if multipart file cannot be parsed.
   * @throws NotAuthorizedException if the user is not authorized.
   * @throws DomainNotFoundException if domain information is incorrect.
   * @throws InvalidWorkbasketException if workbasket has invalid information.
   * @throws WorkbasketAlreadyExistException if workbasket already exists when trying to create a
   *     new one.
   * @throws WorkbasketNotFoundException if do not exists a workbasket in the system with the used
   *     id.
   * @throws InvalidArgumentException if authorization information in workbaskets definitions is
   *     incorrect.
   * @throws WorkbasketAccessItemAlreadyExistException if a WorkbasketAccessItem for the same
   *     workbasket and access_id already exists.
   * @throws ConcurrencyException if workbasket was updated by an other user
   */
  @PostMapping(path = Mapping.URL_WORKBASKETDEFIITIONS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> importWorkbaskets(@RequestParam("file") MultipartFile file)
      throws IOException, NotAuthorizedException, DomainNotFoundException,
          InvalidWorkbasketException, WorkbasketAlreadyExistException, WorkbasketNotFoundException,
          InvalidArgumentException, WorkbasketAccessItemAlreadyExistException,
          ConcurrencyException {
    LOGGER.debug("Entry to importWorkbaskets()");
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    List<WorkbasketDefinitionRepresentationModel> definitions =
        mapper.readValue(
            file.getInputStream(),
            new TypeReference<List<WorkbasketDefinitionRepresentationModel>>() {});

    // key: logical ID
    // value: system ID (in database)
    Map<String, String> systemIds =
        workbasketService.createWorkbasketQuery().list().stream()
            .collect(Collectors.toMap(this::logicalId, WorkbasketSummary::getId));
    checkForDuplicates(definitions);

    // key: old system ID
    // value: system ID
    Map<String, String> idConversion = new HashMap<>();

    // STEP 1: update or create workbaskets from the import
    for (WorkbasketDefinitionRepresentationModel definition : definitions) {
      Workbasket importedWb = workbasketDefinitionAssembler
                                  .toEntityModel(definition.getWorkbasket());
      String newId;
      WorkbasketImpl wbWithoutId = (WorkbasketImpl) removeId(importedWb);
      if (systemIds.containsKey(logicalId(importedWb))) {
        Workbasket modifiedWb =
            workbasketService.getWorkbasket(importedWb.getKey(), importedWb.getDomain());
        wbWithoutId.setModified(modifiedWb.getModified());
        workbasketService.updateWorkbasket(wbWithoutId);

        newId = systemIds.get(logicalId(importedWb));
      } else {
        newId = workbasketService.createWorkbasket(wbWithoutId).getId();
      }

      // Since we would have a n² runtime when doing a lookup and updating the access items we
      // decided to
      // simply delete all existing accessItems and create new ones.
      boolean authenticated =
          definition.getAuthorizations().stream()
              .anyMatch(
                  access -> (access.getWorkbasketId().equals(importedWb.getId()))
                                && (access.getWorkbasketKey().equals(importedWb.getKey())));
      if (!authenticated && !definition.getAuthorizations().isEmpty()) {
        throw new InvalidWorkbasketException(
            "The given Authentications for Workbasket "
                + importedWb.getId()
                + " don't match in WorkbasketId and WorkbasketKey. "
                + "Please provide consistent WorkbasketDefinitions");
      }
      for (WorkbasketAccessItem accessItem : workbasketService.getWorkbasketAccessItems(newId)) {
        workbasketService.deleteWorkbasketAccessItem(accessItem.getId());
      }
      for (WorkbasketAccessItemImpl authorization : definition.getAuthorizations()) {
        authorization.setWorkbasketId(newId);
        workbasketService.createWorkbasketAccessItem(authorization);
      }
      idConversion.put(importedWb.getId(), newId);
    }

    // STEP 2: update distribution targets
    // This can not be done in step 1 because the system IDs are only known after step 1
    for (WorkbasketDefinitionRepresentationModel definition : definitions) {
      List<String> distributionTargets = new ArrayList<>();
      for (String oldId : definition.getDistributionTargets()) {
        if (idConversion.containsKey(oldId)) {
          distributionTargets.add(idConversion.get(oldId));
        } else if (systemIds.containsValue(oldId)) {
          distributionTargets.add(oldId);
        } else {
          throw new InvalidWorkbasketException(
              String.format(
                  "invalid import state: Workbasket '%s' does not exist in the given import list",
                  oldId));
        }
      }

      workbasketService.setDistributionTargets(
          // no verification necessary since the workbasket was already imported in step 1.
          idConversion.get(definition.getWorkbasket().getWorkbasketId()), distributionTargets);
    }
    ResponseEntity<Void> response = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from importWorkbaskets(), returning {}", response);
    return response;
  }

  private Workbasket removeId(Workbasket importedWb) {
    WorkbasketRepresentationModel wbRes = new WorkbasketRepresentationModel(importedWb);
    wbRes.setWorkbasketId(null);
    return workbasketDefinitionAssembler.toEntityModel(wbRes);
  }

  private void checkForDuplicates(List<WorkbasketDefinitionRepresentationModel> definitions) {
    List<String> identifiers = new ArrayList<>();
    Set<String> duplicates = new HashSet<>();
    for (WorkbasketDefinitionRepresentationModel definition : definitions) {
      String identifier =
          logicalId(workbasketDefinitionAssembler.toEntityModel(definition.getWorkbasket()));
      if (identifiers.contains(identifier)) {
        duplicates.add(identifier);
      } else {
        identifiers.add(identifier);
      }
    }
    if (!duplicates.isEmpty()) {
      throw new DuplicateKeyException(
          "The 'key|domain'-identifier is not unique for the value(s): " + duplicates.toString());
    }
  }

  private String logicalId(WorkbasketSummary workbasket) {
    return logicalId(workbasket.getKey(), workbasket.getDomain());
  }

  private String logicalId(Workbasket workbasket) {
    return logicalId(workbasket.getKey(), workbasket.getDomain());
  }

  private String logicalId(String key, String domain) {
    return key + "|" + domain;
  }
}
