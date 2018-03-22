package pro.taskana.impl.report.impl;

import pro.taskana.TaskState;
import pro.taskana.impl.report.ReportColumnHeader;

/**
 * The TaskStatusColumnHeader represents a column for each {@link TaskState}.
 */
public class TaskStatusColumnHeader implements ReportColumnHeader<TaskQueryItem> {

    private TaskState state;

    public TaskStatusColumnHeader(TaskState state) {
        this.state = state;
    }

    @Override
    public String getDisplayName() {
        return state.name();
    }

    @Override
    public boolean fits(TaskQueryItem item) {
        return item.getState() == state;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
