package pro.taskana.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.ReportResource;
import pro.taskana.rest.resource.assembler.ReportAssembler;

/**
 * Controller for all monitoring endpoints.
 */
@RestController
@RequestMapping(path = "/v1/monitor", produces = "application/hal+json")
public class MonitorController {

    @Autowired
    private TaskMonitorService taskMonitorService;

    @Autowired
    private ReportAssembler reportAssembler;

    @GetMapping(path = "/countByState")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<?> getTaskcountForState(
        @RequestParam(value = "states") List<TaskState> taskStates) {
        String taskCount = "[{\"state\": \"READY\", \"counter\": 7},{\"state\": \"CLAIMED\",\"counter\": 4},{\"state\": \"COMPLETED\",\"counter\": 4 }]";
        return ResponseEntity.status(HttpStatus.OK).body(taskCount);
    }

    @GetMapping(path = "/taskcountByWorkbasketDaysAndState")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<?> getTaskCountByWorkbasketAndDaysInPastAndState(
        @RequestParam(value = "daysInPast") Long daysInPast,
        @RequestParam(value = "states") List<TaskState> states) {

        StringBuilder builder = new StringBuilder();
        builder.append(
            "{ \"dates\": [\"02.02.2018\",\"03.02.2018\",\"04.02.2018\", \"05.02.2018\", \"06.02.2018\",  \"07.02.2018\",\"08.02.2018\",\"09.02.2018\",\"10.02.2018\",\"11.02.2018\", \"12.02.2018\"],");
        builder.append("\"data\": [");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Basket1\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Basket2\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Basket3\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Basket4\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Gruppenpostkorb KSC\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Gruppenpostkorb KSC 1\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Gruppenpostkorb KSC 2\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"PPK Teamlead KSC 1\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"PPK Teamlead KSC 2\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"PPK User 1 KSC 1\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"PPK User 2 KSC 1\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"PPK User 1 KSC 2\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"PPK User 2 KSC 2\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Gruppenpostkorb KSC B\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Gruppenpostkorb KSC B1\"},");
        builder.append("{\"data\": [0,0,0,0,0,0,0,0,0,0,0],\"label\": \"Gruppenpostkorb KSC B2\"}");
        builder.append("]}");
        return ResponseEntity.status(HttpStatus.OK).body(builder.toString());
    }

    @GetMapping(path = "/taskStatusReport")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ReportResource> getTaskStatusReport(@RequestParam(required = false) List<String> domains,
        @RequestParam(required = false) List<TaskState> states) throws NotAuthorizedException {
        // return ResponseEntity.status(HttpStatus.OK)
        // .body(reportAssembler.toResource(taskMonitorService.getTaskStatusReport(domains, states), domains, states));
        return ResponseEntity.status(HttpStatus.OK)
            .body(reportAssembler.toResource(
                taskMonitorService.createTaskStatusReportBuilder().stateIn(states).domainIn(domains).buildReport(),
                domains, states));
    }
}
