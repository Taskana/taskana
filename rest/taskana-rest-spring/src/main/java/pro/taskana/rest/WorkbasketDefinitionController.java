package pro.taskana.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.resource.WorkbasketDefinition;
import pro.taskana.rest.resource.WorkbasketDefinitionAssembler;

/**
 * Controller for all {@link WorkbasketDefinition} related endpoints.
 */
@RestController
@RequestMapping(path = "/v1/workbasket-definitions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkbasketDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketDefinitionController.class);

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketDefinitionAssembler workbasketDefinitionAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<WorkbasketDefinition>> exportWorkbaskets(@RequestParam(required = false) String domain)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("Entry to exportWorkbaskets(domain= {})", domain);
        WorkbasketQuery workbasketQuery = workbasketService.createWorkbasketQuery();
        List<WorkbasketSummary> workbasketSummaryList = domain != null
            ? workbasketQuery.domainIn(domain).list()
            : workbasketQuery.list();
        List<WorkbasketDefinition> basketExports = new ArrayList<>();
        for (WorkbasketSummary summary : workbasketSummaryList) {
            Workbasket workbasket = workbasketService.getWorkbasket(summary.getId());
            basketExports.add(workbasketDefinitionAssembler.toDefinition(workbasket));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from exportWorkbaskets(), returning {}",
                new ResponseEntity<>(basketExports, HttpStatus.OK));
        }

        return new ResponseEntity<>(basketExports, HttpStatus.OK);
    }

    /**
     * This method imports a <b>list of {@link WorkbasketDefinition}</b>. This does not exactly match the REST norm, but
     * we want to have an option to import all settings at once. When a logical equal (key and domain are equal)
     * workbasket already exists an update will be executed. Otherwise a new workbasket will be created.
     *
     * @param file the list of workbasket definitions which will be imported to the current system.
     * @return Return answer is determined by the status code: 200 - all good 400 - list state error (referring to non
     * existing id's) 401 - not authorized
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> importWorkbaskets(@RequestParam("file") MultipartFile file)
        throws IOException, NotAuthorizedException, DomainNotFoundException, InvalidWorkbasketException,
        WorkbasketAlreadyExistException, WorkbasketNotFoundException, InvalidArgumentException {
        LOGGER.debug("Entry to importWorkbaskets()");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        List<WorkbasketDefinition> definitions = mapper.readValue(file.getInputStream(),
            new TypeReference<List<WorkbasketDefinition>>() {

            });

        // key: logical ID
        // value: system ID (in database)
        Map<String, String> systemIds = workbasketService.createWorkbasketQuery()
            .list()
            .stream()
            .collect(Collectors.toMap(this::logicalId, WorkbasketSummary::getId));

        // key: old system ID
        // value: system ID
        Map<String, String> idConversion = new HashMap<>();

        // STEP 1: update or create workbaskets from the import
        for (WorkbasketDefinition definition : definitions) {
            Workbasket importedWb = workbasketDefinitionAssembler.toModel(definition.workbasket);
            Workbasket workbasket;
            if (systemIds.containsKey(logicalId(importedWb))) {
                workbasket = workbasketService.updateWorkbasket(importedWb);
            } else {
                workbasket = workbasketService.createWorkbasket(importedWb);
            }

            // Since we would have a n² runtime when doing a lookup and updating the access items we decided to
            // simply delete all existing accessItems and create new ones.
            for (WorkbasketAccessItem accessItem : workbasketService.getWorkbasketAccessItems(workbasket.getId())) {
                workbasketService.deleteWorkbasketAccessItem(accessItem.getId());
            }
            for (WorkbasketAccessItem authorization : definition.authorizations) {
                workbasketService.createWorkbasketAccessItem(authorization);
            }
            idConversion.put(importedWb.getId(), workbasket.getId());
        }

        // STEP 2: update distribution targets
        // This can not be done in step 1 because the system IDs are only known after step 1
        for (WorkbasketDefinition definition : definitions) {
            List<String> distributionTargets = new ArrayList<>();
            for (String oldId : definition.distributionTargets) {
                if (idConversion.containsKey(oldId)) {
                    distributionTargets.add(idConversion.get(oldId));
                } else {
                    throw new InvalidWorkbasketException(
                        String.format(
                            "invalid import state: Workbasket '%s' does not exist in the given import list",
                            oldId));
                }
            }

            workbasketService.setDistributionTargets(
                // no verification necessary since the workbasket was already imported in step 1.
                idConversion.get(definition.workbasket.getWorkbasketId()), distributionTargets);
        }
        LOGGER.debug("Exit from importWorkbaskets(), returning {}", new ResponseEntity<>(HttpStatus.OK));
        return new ResponseEntity<>(HttpStatus.OK);
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
