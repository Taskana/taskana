package pro.taskana.rest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pro.taskana.*;
import pro.taskana.exceptions.*;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketType;
import pro.taskana.rest.dto.WorkbasketSummaryDto;
import pro.taskana.rest.mapper.WorkbasketSummaryMapper;
import pro.taskana.security.CurrentUserContext;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/v1/workbaskets", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkbasketController {

    private static final String LIKE = "%";
    private static final String NAME = "name";
    private static final String KEY  = "key";
    private static final String DESCRIPTION  = "description";
    private static final String OWNER  = "owner";
    private static final String DESC = "DESC";

    @Autowired
    private WorkbasketService workbasketService;
    @Autowired
    private WorkbasketSummaryMapper workbasketSummaryMapper;


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<WorkbasketSummaryDto>> GetWorkbaskets(@RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy ,
                                                                     @RequestParam(value = "order", defaultValue = "asc", required = false) String order ,
                                                                     @RequestParam(value = "name", defaultValue = "", required = false) String name ,
                                                                     @RequestParam(value = "nameLike", defaultValue = "", required = false) String nameLike ,
                                                                     @RequestParam(value = "descLike", defaultValue = "", required = false) String descLike ,
                                                                     @RequestParam(value = "owner", defaultValue = "", required = false) String owner ,
                                                                     @RequestParam(value = "ownerLike", defaultValue = "", required = false) String ownerLike ,
                                                                     @RequestParam(value = "type", defaultValue = "", required = false) String type ,
                                                                     @RequestParam(value = "requiredPermission", defaultValue = "", required = false) String requiredPermission) {

        List<WorkbasketSummary> workbasketsSummary;
        WorkbasketQuery query = workbasketService.createWorkbasketQuery();

        try{

            AddSortByQuery(query, sortBy, order);
            AddFilterQuery(query, name, nameLike, descLike, owner, ownerLike, type);
            AddAuthorization(query, requiredPermission);
            workbasketsSummary = query.list();

        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        return new ResponseEntity<>(workbasketsSummary.stream().map(workbasket -> workbasketSummaryMapper.convertToDto(workbasket))
                                                               .map(WorkbasketController::WorkbasketSummaryLink).collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{workbasketid}")
    public ResponseEntity<Workbasket> getWorkbasketById(@PathVariable(value = "workbasketid") String workbasketId) {
        try {
            Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);
            return new ResponseEntity<>(workbasket, HttpStatus.OK);
        } catch (WorkbasketNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createWorkbasket(@RequestBody Workbasket workbasket) {
        Workbasket createdWorkbasket;
        try {
            createdWorkbasket = workbasketService.createWorkbasket(workbasket);
            return new ResponseEntity<>(createdWorkbasket, HttpStatus.CREATED);
        } catch (InvalidWorkbasketException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{workbasketkey}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateWorkbasket(@PathVariable(value = "workbasketkey") String workbasketKey,
        @RequestBody Workbasket workbasket) {
        try {
            Workbasket updatedWorkbasket = workbasketService.updateWorkbasket(workbasket);
            return new ResponseEntity<>(updatedWorkbasket, HttpStatus.OK);
        } catch (InvalidWorkbasketException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (WorkbasketNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{workbasketkey}/authorizations", method = RequestMethod.GET)
    public List<WorkbasketAccessItem> getWorkbasketAuthorizations(
        @PathVariable(value = "workbasketkey") String workbasketKey) {
        return workbasketService.getWorkbasketAuthorizations(workbasketKey);
    }

    @RequestMapping(value = "/authorizations", method = RequestMethod.POST)
    public WorkbasketAccessItem createWorkbasketAuthorization(@RequestBody WorkbasketAccessItem workbasketAccessItem) {
        return workbasketService.createWorkbasketAuthorization(workbasketAccessItem);
    }

    @RequestMapping(value = "/authorizations/{authid}", method = RequestMethod.PUT)
    public WorkbasketAccessItem updateWorkbasketAuthorization(@PathVariable(value = "authid") String authId,
        @RequestBody WorkbasketAccessItem workbasketAccessItem) throws InvalidArgumentException {
        return workbasketService.updateWorkbasketAuthorization(workbasketAccessItem);
    }

    @RequestMapping(value = "/authorizations/{authid}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWorkbasketAuthorization(@PathVariable(value = "authid") String authId) {
        workbasketService.deleteWorkbasketAuthorization(authId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private void AddAuthorization(WorkbasketQuery query, String requiredPermission) throws InvalidArgumentException {
       if (requiredPermission.isEmpty()) {return;}

       String[] accessIds = GetCurrentUserAccessIds();
        for (String authorization : Arrays.asList(requiredPermission.split(","))) {
            try {
                switch (authorization.trim()) {
                case "READ":
                    query.accessIdsHavePermission(WorkbasketAuthorization.READ, accessIds);
                    break;
                case "OPEN":
                    query.accessIdsHavePermission(WorkbasketAuthorization.OPEN, accessIds);
                    break;
                case "APPEND":
                    query.accessIdsHavePermission(WorkbasketAuthorization.APPEND, accessIds);
                    break;
                case "TRANSFER":
                    query.accessIdsHavePermission(WorkbasketAuthorization.TRANSFER, accessIds);
                    break;
                case "DISTRIBUTE":
                    query.accessIdsHavePermission(WorkbasketAuthorization.DISTRIBUTE, accessIds);
                    break;
                case "DELETE":
                    query.accessIdsHavePermission(WorkbasketAuthorization.DELETE, accessIds);
                    break;
                case "CUSTOM_1":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_1, accessIds);
                    break;
                case "CUSTOM_2":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_2, accessIds);
                    break;
                case "CUSTOM_3":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_3, accessIds);
                    break;
                case "CUSTOM_4":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_4, accessIds);
                    break;
                case "CUSTOM_5":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_5, accessIds);
                    break;
                case "CUSTOM_6":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_6, accessIds);
                    break;
                case "CUSTOM_7":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_7, accessIds);
                    break;
                case "CUSTOM_8":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_8, accessIds);
                    break;
                case "CUSTOM_9":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_9, accessIds);
                    break;
                case "CUSTOM_10":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_10, accessIds);
                    break;
                case "CUSTOM_11":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_11, accessIds);
                    break;
                case "CUSTOM_12":
                    query.accessIdsHavePermission(WorkbasketAuthorization.CUSTOM_12, accessIds);
                    break;
                }
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
    }


    private void AddSortByQuery(WorkbasketQuery query, String sortBy, String order) throws InvalidRequestException, InvalidArgumentException {
        BaseQuery.SortDirection sortDirection = GetSortDirecction(order);

        if (sortBy.equals(NAME)) {
            query.orderByKey(sortDirection);
        } else if (sortBy.equals(KEY)){
            query.orderByKey(sortDirection);
        }
        /*else if (sortBy.equals(DESCRIPTION)){
            query.orderByDescription(sortDirection);
        }
        else if (sortBy.equals(OWNER)){
            query.orderByOwner(sortDirection);
        }*/
    }

    private BaseQuery.SortDirection GetSortDirecction(String order) throws InvalidRequestException {
        if (order.equals(DESC)){
            return BaseQuery.SortDirection.DESCENDING;
        }
        return BaseQuery.SortDirection.ASCENDING;
    }


    private void AddFilterQuery(WorkbasketQuery query,
                                String name, String nameLike,
                                String descLike, String owner,
                                String ownerLike, String type) throws NotAuthorizedException, InvalidArgumentException {
        if(!name.isEmpty())query.nameIn(name);
        if(!nameLike.isEmpty())query.nameLike(LIKE + nameLike + LIKE);
        if(!owner.isEmpty())query.ownerIn(owner);
        //if(!ownerLike.isEmpty())query.ownerLike(LIKE + ownerLike + LIKE);
        if(!descLike.isEmpty())query.descriptionLike(LIKE + descLike + LIKE);
        switch (type) {
            case "PERSONAL":
                query.typeIn(WorkbasketType.PERSONAL);
            case "GROUP":
                query.typeIn(WorkbasketType.GROUP);
            case "CLEARANCE":
                query.typeIn(WorkbasketType.CLEARANCE);
            case "TOPIC":
                query.typeIn(WorkbasketType.TOPIC);
        }
    }

    private String[] GetCurrentUserAccessIds() throws InvalidArgumentException{
        String[] accessIds;
        List<String> ucAccessIds = CurrentUserContext.getAccessIds();
        if (ucAccessIds != null && !ucAccessIds.isEmpty()) {
            accessIds = new String[ucAccessIds.size()];
            accessIds = ucAccessIds.toArray(accessIds);
        } else {
            throw new InvalidArgumentException("CurrentUserContext need to have at least one accessId.");
        }
        return accessIds;
    }

    private static WorkbasketSummaryDto WorkbasketSummaryLink(WorkbasketSummaryDto workbasketSummaryDto){

        Link selfLink = linkTo(WorkbasketController.class).slash(workbasketSummaryDto.getWorkBasketId()).withSelfRel();
        workbasketSummaryDto.add(selfLink);
        return workbasketSummaryDto;
    }

}
