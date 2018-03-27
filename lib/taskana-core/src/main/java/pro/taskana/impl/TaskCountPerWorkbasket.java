package pro.taskana.impl;

/**
 * This class keeps a count of tasks that are contained in a workbasket.
 *
 * @author bbr
 */
public class TaskCountPerWorkbasket {

    Integer taskCount;
    String workbasketId;

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public String getWorkbasketId() {
        return workbasketId;
    }

    public void setWorkbasketId(String workbasketId) {
        this.workbasketId = workbasketId;
    }

}
