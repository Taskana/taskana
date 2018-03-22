package pro.taskana.impl.report.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.TaskState;
import pro.taskana.impl.report.Report;

/**
 * The TaskStatusReport displays the amount of tasks, differentiated by their state and grouped by domain.
 */
public class TaskStatusReport extends Report<TaskQueryItem, TaskStatusColumnHeader> {

    public TaskStatusReport() {
        this(null);
    }

    public TaskStatusReport(List<TaskState> filter) {
        super((filter != null ? filter.stream() : Arrays.stream(TaskState.values()))
            .map(TaskStatusColumnHeader::new)
            .collect(Collectors.toList()), "DOMAINS");
    }

}
