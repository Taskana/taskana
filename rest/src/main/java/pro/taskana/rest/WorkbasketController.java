package pro.taskana.rest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pro.taskana.*;
import pro.taskana.exceptions.*;
import pro.taskana.model.WorkbasketType;

@RestController
@RequestMapping(path = "/v1/workbaskets", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkbasketController {

    private static final String LIKE = "%";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";

    //region Controllers
    @Autowired
    private WorkbasketService workbasketService;
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<WorkbasketSummary>> GetWorkbaskets(  @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy ,
                                                                    @RequestParam(value = "order", defaultValue = "asc", required = false) String order ,
                                                                    @RequestParam(value = "filterName", defaultValue = "", required = false) String filterName ,
                                                                    @RequestParam(value = "filterDesc", defaultValue = "", required = false) String filterDesc ,
                                                                    @RequestParam(value = "filterOwner", defaultValue = "", required = false) String filterOwner ,
                                                                    @RequestParam(value = "filterType", defaultValue = "", required = false) String filterType ,
                                                                    @RequestParam(value = "requiredPermission", defaultValue = "", required = false) String requiredPermission) {

        List<WorkbasketSummary> workbaskets;
        WorkbasketQuery query = workbasketService.createWorkbasketQuery();

        try{
            AddSortByQuery(query, sortBy);
            AddFilterQuery(query, filterName, filterDesc, filterOwner, filterType);
            AddOrderQuery(query, order);

            workbaskets = query.list();

        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(workbaskets, HttpStatus.OK);
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

    //endregion

    //region Private

   /*private WorkbasketAuthorization GetAuthorization(){
        if (!requiredPermission.isEmpty()) {
            List<WorkbasketAuthorization> authorizations = new ArrayList<>();
            requiredPermission.forEach(item -> {
                for (String authorization : Arrays.asList(item.split(","))) {
                    switch (authorization.trim()) {
                        case "READ":
                            authorizations.add(WorkbasketAuthorization.READ);
                            break;
                        case "OPEN":
                            authorizations.add(WorkbasketAuthorization.OPEN);
                            break;
                        case "APPEND":
                            authorizations.add(WorkbasketAuthorization.APPEND);
                            break;
                        case "TRANSFER":
                            authorizations.add(WorkbasketAuthorization.TRANSFER);
                            break;
                        case "DISTRIBUTE":
                            authorizations.add(WorkbasketAuthorization.DISTRIBUTE);
                            break;
                        case "DELETE":
                            authorizations.add(WorkbasketAuthorization.DELETE);
                            break;
                        case "CUSTOM_1":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_1);
                            break;
                        case "CUSTOM_2":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_2);
                            break;
                        case "CUSTOM_3":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_3);
                            break;
                        case "CUSTOM_4":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_4);
                            break;
                        case "CUSTOM_5":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_5);
                            break;
                        case "CUSTOM_6":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_6);
                            break;
                        case "CUSTOM_7":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_7);
                            break;
                        case "CUSTOM_8":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_8);
                            break;
                        case "CUSTOM_9":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_9);
                            break;
                        case "CUSTOM_10":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_10);
                            break;
                        case "CUSTOM_11":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_11);
                            break;
                        case "CUSTOM_12":
                            authorizations.add(WorkbasketAuthorization.CUSTOM_12);
                            break;
                    }
                }
            });
            workbaskets = workbasketService.getWorkbaskets(authorizations);
        }

        //workbaskets = workbasketService.getWorkbaskets();
        return WorkbasketAuthorization.READ;
    }*/

    private void AddOrderQuery(WorkbasketQuery query, String order) throws InvalidRequestException {
        if(order.equals(ASC)){
            query.ascending();
        } else if (order.equals(DESC)){
            query.descending();
        };
    }

    private void AddSortByQuery(WorkbasketQuery query, String sortBy) throws InvalidRequestException, InvalidArgumentException {
        if (sortBy.equals(NAME)) {
            query.orderByName();
        } else if (sortBy.equals(KEY)){
            query.orderByKey();
        }
    }

    private void AddFilterQuery(WorkbasketQuery query, String filterName,String filterDesc,String filterOwner,String filterType) throws NotAuthorizedException, InvalidArgumentException {
        if(!filterName.isEmpty())query.nameLike(LIKE + filterName + LIKE);
        if(!filterDesc.isEmpty())query.descriptionLike(LIKE + filterDesc + LIKE);
        if(!filterOwner.isEmpty())query.ownerIn(LIKE + filterOwner + LIKE);
        switch (filterType) {
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
    //endregion
}
