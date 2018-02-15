package pro.taskana.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.impl.TaskState;

@RestController
@RequestMapping(path = "/v1/monitor", produces = { MediaType.APPLICATION_JSON_VALUE })
public class MonitorController {

    @RequestMapping(value = "/countByState")
    public ResponseEntity<?> getTaskcountForState(
        @RequestParam(value = "states") List<TaskState> taskStates) {
        String taskCount = "[{\"state\": \"READY\", \"counter\": 7},{\"state\": \"CLAIMED\",\"counter\": 4},{\"state\": \"COMPLETED\",\"counter\": 4 }]";
        return ResponseEntity.status(HttpStatus.OK).body(taskCount);
    }

    @RequestMapping(value = "/taskcountByWorkbasketDaysAndState")
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
}
