package pro.taskana.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.BaseQuery;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidRequestException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketAuthorization;
import pro.taskana.impl.WorkbasketType;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;
import pro.taskana.rest.resource.WorkbasketResource;
import pro.taskana.rest.resource.WorkbasketSummaryResource;
import pro.taskana.rest.resource.mapper.WorkbasketAccessItemMapper;
import pro.taskana.rest.resource.mapper.WorkbasketMapper;
import pro.taskana.rest.resource.mapper.WorkbasketSummaryMapper;

@RestController
@RequestMapping(path = "/v1/workbaskets", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkbasketController {

    private static final String LIKE = "%";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String DESCRIPTION = "description";
    private static final String OWNER = "owner";
    private static final String TYPE = "type";
    private static final String DESC = "desc";

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketSummaryMapper workbasketSummaryMapper;

    @Autowired
    private WorkbasketMapper workbasketMapper;

    @Autowired
    private WorkbasketAccessItemMapper workbasketAccessItemMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<WorkbasketSummaryResource>> getWorkbaskets(
        @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
        @RequestParam(value = "order", defaultValue = "asc", required = false) String order,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "nameLike", required = false) String nameLike,
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "keyLike", required = false) String keyLike,
        @RequestParam(value = "descLike", required = false) String descLike,
        @RequestParam(value = "owner", required = false) String owner,
        @RequestParam(value = "ownerLike", required = false) String ownerLike,
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "requiredPermission", required = false) String requiredPermission) {

        List<WorkbasketSummary> workbasketsSummary;
        WorkbasketQuery query = workbasketService.createWorkbasketQuery();
        try {
            addSortingToQuery(query, sortBy, order);
            addAttributeFilter(query, name, nameLike, key, keyLike, descLike, owner, ownerLike, type);
            addAuthorizationFilter(query, requiredPermission);
            workbasketsSummary = query.list();
        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(workbasketsSummary.stream()
            .map(workbasket -> workbasketSummaryMapper.toResource(workbasket))
            .collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{workbasketId}")
    public ResponseEntity<WorkbasketResource> getWorkbasket(@PathVariable(value = "workbasketId") String workbasketId) {
        try {
            Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);
            return new ResponseEntity<>(workbasketMapper.toResource(workbasket), HttpStatus.OK);
        } catch (WorkbasketNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<WorkbasketResource> createWorkbasket(@RequestBody Workbasket workbasket) {
        Workbasket createdWorkbasket;
        try {
            createdWorkbasket = workbasketService.createWorkbasket(workbasket);
            return new ResponseEntity<>(workbasketMapper.toResource(createdWorkbasket), HttpStatus.CREATED);
        } catch (InvalidWorkbasketException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{workbasketKey}", method = RequestMethod.PUT)
    public ResponseEntity<WorkbasketResource> updateWorkbasket(
        @PathVariable(value = "workbasketKey") String workbasketKey,
        @RequestBody Workbasket workbasket) {
        try {
            Workbasket updatedWorkbasket = workbasketService.updateWorkbasket(workbasket);
            return new ResponseEntity<>(workbasketMapper.toResource(updatedWorkbasket), HttpStatus.OK);
        } catch (InvalidWorkbasketException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (WorkbasketNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{workbasketKey}/authorizations", method = RequestMethod.GET)
    public ResponseEntity<List<WorkbasketAccessItemResource>> getWorkbasketAuthorizations(
        @PathVariable(value = "workbasketKey") String workbasketKey) {
        List<WorkbasketAccessItem> wbAuthorizations = workbasketService.getWorkbasketAuthorizations(workbasketKey);
        return new ResponseEntity<>(wbAuthorizations.stream()
            .map(accItem -> workbasketAccessItemMapper.toResource(accItem))
            .collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/authorizations", method = RequestMethod.POST)
    public ResponseEntity<WorkbasketAccessItemResource> createWorkbasketAuthorization(
        @RequestBody WorkbasketAccessItem workbasketAccessItem) {
        workbasketAccessItem = workbasketService.createWorkbasketAuthorization(workbasketAccessItem);
        return new ResponseEntity<>(workbasketAccessItemMapper.toResource(workbasketAccessItem), HttpStatus.OK);
    }

    @RequestMapping(value = "/authorizations/{authId}", method = RequestMethod.PUT)
    public ResponseEntity<WorkbasketAccessItemResource> updateWorkbasketAuthorization(
        @PathVariable(value = "authId") String authId,
        @RequestBody WorkbasketAccessItem workbasketAccessItem) throws InvalidArgumentException {
        workbasketAccessItem = workbasketService.updateWorkbasketAuthorization(workbasketAccessItem);
        return new ResponseEntity<>(workbasketAccessItemMapper.toResource(workbasketAccessItem), HttpStatus.OK);
    }

    @RequestMapping(value = "/authorizations/{authId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWorkbasketAuthorization(@PathVariable(value = "authId") String authId) {
        workbasketService.deleteWorkbasketAuthorization(authId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/{workbasketId}/distributiontargets", method = RequestMethod.GET)
    public ResponseEntity<List<WorkbasketSummaryResource>> getDistributionTargetsForWorkbasketId(
        @PathVariable(value = "workbasketId") String workbasketId) {

        ResponseEntity<List<WorkbasketSummaryResource>> result = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        List<WorkbasketSummary> distributionTargets = null;
        try {
            distributionTargets = workbasketService.getDistributionTargets(workbasketId);
            result = new ResponseEntity<>(distributionTargets.stream()
                .map(workbasket -> workbasketSummaryMapper.toResource(workbasket))
                .collect(Collectors.toList()), HttpStatus.OK);
        } catch (WorkbasketNotFoundException e) {
            result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            result = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return result;
    }

    private void addAuthorizationFilter(WorkbasketQuery query, String requiredPermission)
        throws InvalidArgumentException {
        if (requiredPermission == null) {
            return;
        }

        for (String authorization : Arrays.asList(requiredPermission.split(","))) {
            try {
                switch (authorization.trim()) {
                    case "READ":
                        query.callerHasPermission(WorkbasketAuthorization.READ);
                        break;
                    case "OPEN":
                        query.callerHasPermission(WorkbasketAuthorization.OPEN);
                        break;
                    case "APPEND":
                        query.callerHasPermission(WorkbasketAuthorization.APPEND);
                        break;
                    case "TRANSFER":
                        query.callerHasPermission(WorkbasketAuthorization.TRANSFER);
                        break;
                    case "DISTRIBUTE":
                        query.callerHasPermission(WorkbasketAuthorization.DISTRIBUTE);
                        break;
                    case "CUSTOM_1":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_1);
                        break;
                    case "CUSTOM_2":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_2);
                        break;
                    case "CUSTOM_3":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_3);
                        break;
                    case "CUSTOM_4":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_4);
                        break;
                    case "CUSTOM_5":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_5);
                        break;
                    case "CUSTOM_6":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_6);
                        break;
                    case "CUSTOM_7":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_7);
                        break;
                    case "CUSTOM_8":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_8);
                        break;
                    case "CUSTOM_9":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_9);
                        break;
                    case "CUSTOM_10":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_10);
                        break;
                    case "CUSTOM_11":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_11);
                        break;
                    case "CUSTOM_12":
                        query.callerHasPermission(WorkbasketAuthorization.CUSTOM_12);
                        break;
                }
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void addSortingToQuery(WorkbasketQuery query, String sortBy, String order)
        throws InvalidRequestException, InvalidArgumentException {
        BaseQuery.SortDirection sortDirection = getSortDirection(order);

        if (sortBy.equals(NAME)) {
            query.orderByName(sortDirection);
        } else if (sortBy.equals(KEY)) {
            query.orderByKey(sortDirection);
        } else if (sortBy.equals(DESCRIPTION)) {
            query.orderByDescription(sortDirection);
        } else if (sortBy.equals(OWNER)) {
            query.orderByOwner(sortDirection);
        } else if (sortBy.equals(TYPE)) {
            query.orderByType(sortDirection);
        }
    }

    private BaseQuery.SortDirection getSortDirection(String order) throws InvalidRequestException {
        if (order.equals(DESC)) {
            return BaseQuery.SortDirection.DESCENDING;
        }
        return BaseQuery.SortDirection.ASCENDING;
    }

    private void addAttributeFilter(WorkbasketQuery query,
        String name, String nameLike,
        String key, String keyLike,
        String descLike, String owner,
        String ownerLike, String type) throws InvalidArgumentException {
        if (name != null)
            query.nameIn(name);
        if (nameLike != null)
            query.nameLike(LIKE + nameLike + LIKE);
        if (key != null)
            query.keyIn(key);
        if (keyLike != null)
            query.keyLike(LIKE + keyLike + LIKE);
        if (owner != null)
            query.ownerIn(owner);
        if (ownerLike != null)
            query.ownerLike(LIKE + ownerLike + LIKE);
        if (descLike != null)
            query.descriptionLike(LIKE + descLike + LIKE);
        if (type != null) {
            switch (type) {
                case "PERSONAL":
                    query.typeIn(WorkbasketType.PERSONAL);
                    break;
                case "GROUP":
                    query.typeIn(WorkbasketType.GROUP);
                    break;
                case "CLEARANCE":
                    query.typeIn(WorkbasketType.CLEARANCE);
                    break;
                case "TOPIC":
                    query.typeIn(WorkbasketType.TOPIC);
                    break;
            }
        }
    }
}
