package pro.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import pro.taskana.rest.resource.WorkbasketAccessItemResource;
import pro.taskana.rest.resource.WorkbasketDefinition;
import pro.taskana.rest.resource.WorkbasketResource;
import pro.taskana.rest.resource.assembler.WorkbasketAccessItemAssembler;
import pro.taskana.rest.resource.assembler.WorkbasketDefinitionAssembler;
import pro.taskana.rest.resource.assembler.WorkbasketResourceAssembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for all {@link WorkbasketDefinition} related endpoints.
 */
@RestController
@RequestMapping(path = "/v1/workbasketdefinitions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkbasketDefinitionController {

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketDefinitionAssembler workbasketDefinitionAssembler;

    @Autowired
    private WorkbasketResourceAssembler workbasketResourceAssembler;

    @Autowired
    private WorkbasketAccessItemAssembler workbasketAccessItemAssembler;

    @GetMapping
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<WorkbasketDefinition>> exportWorkbaskets(@RequestParam(required = false) String domain) {
        try {
            WorkbasketQuery workbasketQuery = workbasketService.createWorkbasketQuery();
            List<WorkbasketSummary> workbasketSummaryList = domain != null
                ? workbasketQuery.domainIn(domain).list()
                : workbasketQuery.list();
            List<WorkbasketDefinition> basketExports = new ArrayList<>();
            for (WorkbasketSummary summary : workbasketSummaryList) {
                Workbasket workbasket = workbasketService.getWorkbasket(summary.getId());
                basketExports.add(workbasketDefinitionAssembler.toResource(workbasket));
            }
            return new ResponseEntity<>(basketExports, HttpStatus.OK);
        } catch (WorkbasketNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * This method imports a <b>list of {@link WorkbasketDefinition}</b>. This does not exactly match the REST norm, but
     * we want to have an option to import all settings at once. When a logical equal (key and domain are equal)
     * workbasket already exists an update will be executed. Otherwise a new workbasket will be created.
     *
     * @param definitions the list of workbasket definitions which will be imported to the current system.
     * @return Return answer is determined by the status code: 200 - all good 400 - list state error (referring to non
     * existing id's) 401 - not authorized
     */
    @PostMapping(path = "/import")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> importWorkbaskets(@RequestBody List<WorkbasketDefinition> definitions) {
        try {
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
                WorkbasketResource res = definition.workbasketResource;
                Workbasket workbasket;
                String oldId = res.workbasketId;
                if (systemIds.containsKey(logicalId(res))) {
                    res.workbasketId = systemIds.get(logicalId(res));
                    workbasket = workbasketService.updateWorkbasket(
                        workbasketResourceAssembler.toModel(res));
                } else {
                    res.workbasketId = null;
                    workbasket = workbasketService.createWorkbasket(
                        workbasketResourceAssembler.toModel(res));
                }
                res.workbasketId = oldId;

                // Since we would have a n² runtime when doing a lookup and updating the access items we decided to
                // simply delete all existing accessItems and create new ones.
                for (WorkbasketAccessItem accessItem : workbasketService.getWorkbasketAccessItems(workbasket.getId())) {
                    workbasketService.deleteWorkbasketAccessItem(accessItem.getId());
                }
                for (WorkbasketAccessItemResource authorization : definition.authorizations) {
                    workbasketService.createWorkbasketAccessItem(
                        workbasketAccessItemAssembler.toModel(authorization));
                }
                idConversion.put(definition.workbasketResource.workbasketId, workbasket.getId());
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
                    idConversion.get(definition.workbasketResource.workbasketId), distributionTargets);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (WorkbasketNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidWorkbasketException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotAuthorizedException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (InvalidArgumentException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } catch (WorkbasketAlreadyExistException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (DomainNotFoundException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String logicalId(WorkbasketSummary workbasket) {
        return logicalId(workbasket.getKey(), workbasket.getDomain());
    }

    private String logicalId(WorkbasketResource resource) {
        return logicalId(resource.key, resource.domain);
    }

    private String logicalId(String key, String domain) {
        return key + "|" + domain;
    }
}
