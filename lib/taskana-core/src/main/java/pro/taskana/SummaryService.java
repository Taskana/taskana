package pro.taskana;

import java.util.List;

import pro.taskana.model.TaskSummary;

/**
 * This interface manages the summaries of some models.
 */
public interface SummaryService {

    List<TaskSummary> getTaskSummariesByWorkbasketId(String workbasketId);
}
