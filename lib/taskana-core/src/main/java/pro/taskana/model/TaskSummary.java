package pro.taskana.model;

/**
 * Entity which contains the most important
 * informations about a Task.
 */
public class TaskSummary {

    private String taskId;
    private String taskName;
    private String workbasketKey;
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

    public String getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
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
        result = prime * result + ((workbasketKey == null) ? 0 : workbasketKey.hashCode());
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
        if (workbasketKey == null) {
            if (other.workbasketKey != null) {
                return false;
            }
        } else if (!workbasketKey.equals(other.workbasketKey)) {
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
        StringBuilder builder = new StringBuilder();
        builder.append("TaskSummary [taskId=");
        builder.append(taskId);
        builder.append(", taskName=");
        builder.append(taskName);
        builder.append(", workbasketKey=");
        builder.append(workbasketKey);
        builder.append(", workbasketName=");
        builder.append(workbasketName);
        builder.append(", classificationKey=");
        builder.append(classificationKey);
        builder.append(", classificationName=");
        builder.append(classificationName);
        builder.append("]");
        return builder.toString();
    }
}
