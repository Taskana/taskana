package pro.taskana.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.SummaryService;
import pro.taskana.model.TaskSummary;

@RestController
@RequestMapping(path = "/v1/summary", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SummaryController {

    @Autowired
    private SummaryService summaryService;
    
    @RequestMapping(value = "/{workbasketId}/tasks", method = RequestMethod.GET)
    public ResponseEntity<List<TaskSummary>> getTasksummariesByWorkbasketId(@PathVariable(value="workbasketId") String workbasketId) {
        List<TaskSummary> taskSummaries = null;
        try {
            taskSummaries = summaryService.getTaskSummariesByWorkbasketId(workbasketId);
            return ResponseEntity.status(HttpStatus.OK).body(taskSummaries);
        } catch(Exception ex) {
            if (taskSummaries == null) {
                taskSummaries = Collections.emptyList();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
