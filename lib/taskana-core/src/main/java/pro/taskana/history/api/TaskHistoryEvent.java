package pro.taskana.history.api;

import pro.taskana.Task;

/**
 * Super class for all task related events.
 */
public class TaskHistoryEvent extends TaskanaHistoryEvent {

    protected String taskId;
    protected String businessProcessId;
    protected String parentBusinessProcessId;
    protected String domain;
    protected String workbasketKey;
    protected String taskClassificationCategory;
    protected String taskClassificationKey;
    protected String attachmentClassificationKey;
    protected String porCompany;
    protected String porSystem;
    protected String porInstance;
    protected String porType;
    protected String porValue;

    public TaskHistoryEvent(Task task) {
        super();
        taskId = task.getId();
        businessProcessId = task.getBusinessProcessId();
        parentBusinessProcessId = task.getParentBusinessProcessId();
        domain = task.getDomain();
        workbasketKey = task.getWorkbasketKey();
        taskClassificationCategory = task.getClassificationCategory();
        taskClassificationKey = task.getClassificationSummary().getKey();
        if (!task.getAttachments().isEmpty()) {
            attachmentClassificationKey = task.getAttachments().get(0).getClassificationSummary().getKey();
        }
        porCompany = task.getPrimaryObjRef().getCompany();
        porSystem = task.getPrimaryObjRef().getSystem();
        porInstance = task.getPrimaryObjRef().getSystemInstance();
        porType = task.getPrimaryObjRef().getType();
        porValue = task.getPrimaryObjRef().getValue();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBusinessProcessId() {
        return businessProcessId;
    }

    public void setBusinessProcessId(String businessProcessId) {
        this.businessProcessId = businessProcessId;
    }

    public String getParentBusinessProcessId() {
        return parentBusinessProcessId;
    }

    public void setParentBusinessProcessId(String parentBusinessProcessId) {
        this.parentBusinessProcessId = parentBusinessProcessId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
    }

    public String getTaskClassificationCategory() {
        return taskClassificationCategory;
    }

    public void setTaskClassificationCategory(String taskClassificationCategory) {
        this.taskClassificationCategory = taskClassificationCategory;
    }

    public String getTaskClassificationKey() {
        return taskClassificationKey;
    }

    public void setTaskClassificationKey(String taskClassificationKey) {
        this.taskClassificationKey = taskClassificationKey;
    }

    public String getAttachmentClassificationKey() {
        return attachmentClassificationKey;
    }

    public void setAttachmentClassificationKey(String attachmentClassificationKey) {
        this.attachmentClassificationKey = attachmentClassificationKey;
    }

    public String getPorCompany() {
        return porCompany;
    }

    public void setPorCompany(String porCompany) {
        this.porCompany = porCompany;
    }

    public String getPorSystem() {
        return porSystem;
    }

    public void setPorSystem(String porSystem) {
        this.porSystem = porSystem;
    }

    public String getPorInstance() {
        return porInstance;
    }

    public void setPorInstance(String porInstance) {
        this.porInstance = porInstance;
    }

    public String getPorType() {
        return porType;
    }

    public void setPorType(String porType) {
        this.porType = porType;
    }

    public String getPorValue() {
        return porValue;
    }

    public void setPorValue(String porValue) {
        this.porValue = porValue;
    }

}
