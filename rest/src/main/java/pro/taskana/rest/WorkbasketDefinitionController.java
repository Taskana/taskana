package pro.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.resource.WorkbasketDefinition;
import pro.taskana.rest.resource.mapper.WorkbasketDefinitionMapper;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/workbasketdefinitions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkbasketDefinitionController {

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketDefinitionMapper workbasketDefinitionMapper;

    @GetMapping
    public ResponseEntity<List<WorkbasketDefinition>> exportWorkbaskets(@RequestParam(required = false) String domain) {
        try {
            WorkbasketQuery workbasketQuery = workbasketService.createWorkbasketQuery();
            List<WorkbasketSummary> workbasketSummaryList = domain != null
                ? workbasketQuery.domainIn(domain).list()
                : workbasketQuery.list();
            List<WorkbasketDefinition> basketExports = new ArrayList<>();
            for (WorkbasketSummary summary : workbasketSummaryList) {
                Workbasket workbasket = workbasketService.getWorkbasket(summary.getId());
                basketExports.add(workbasketDefinitionMapper.toResource(workbasket));
            }
            return new ResponseEntity<>(basketExports, HttpStatus.OK);
        } catch (WorkbasketNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
