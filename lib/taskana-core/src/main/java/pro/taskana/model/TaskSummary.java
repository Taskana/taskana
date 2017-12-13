package pro.taskana.model;

/**
 * Entity which contains the most important
 * informations about a Task.
 */
public class TaskSummary {

    private String taskId;
    private String taskName;
    private String workbasketId;
    private String workbasketName;
    private String classificationKey;
    private String classificationName;

    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getWorkbasketId() {
        return workbasketId;
    }
    public void setWorkbasketId(String workbasketId) {
        this.workbasketId = workbasketId;
    }
    public String getWorkbasketName() {
        return workbasketName;
    }
    public void setWorkbasketName(String workbasketName) {
        this.workbasketName = workbasketName;
    }
    public String getClassificationKey() {
        return classificationKey;
    }
    public void setClassificationKey(String classificationKey) {
        this.classificationKey = classificationKey;
    }
    public String getClassificationName() {
        return classificationName;
    }
    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classificationKey == null) ? 0 : classificationKey.hashCode());
        result = prime * result + ((classificationName == null) ? 0 : classificationName.hashCode());
        result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
        result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
        result = prime * result + ((workbasketId == null) ? 0 : workbasketId.hashCode());
        result = prime * result + ((workbasketName == null) ? 0 : workbasketName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TaskSummary other = (TaskSummary) obj;
        if (classificationKey == null) {
            if (other.classificationKey != null) {
                return false;
            }
        } else if (!classificationKey.equals(other.classificationKey)) {
            return false;
        }
        if (classificationName == null) {
            if (other.classificationName != null) {
                return false;
            }
        } else if (!classificationName.equals(other.classificationName)) {
            return false;
        }
        if (taskId == null) {
            if (other.taskId != null) {
                return false;
            }
        } else if (!taskId.equals(other.taskId)) {
            return false;
        }
        if (taskName == null) {
            if (other.taskName != null) {
                return false;
            }
        } else if (!taskName.equals(other.taskName)) {
            return false;
        }
        if (workbasketId == null) {
            if (other.workbasketId != null) {
                return false;
            }
        } else if (!workbasketId.equals(other.workbasketId)) {
            return false;
        }
        if (workbasketName == null) {
            if (other.workbasketName != null) {
                return false;
            }
        } else if (!workbasketName.equals(other.workbasketName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TaskSummary [taskId=" + taskId + ", taskName=" + taskName + ", workbasketId=" + workbasketId
                + ", workbasketName=" + workbasketName + ", classificationKey=" + classificationKey
                + ", classificationName=" + classificationName + "]";
    }
}
