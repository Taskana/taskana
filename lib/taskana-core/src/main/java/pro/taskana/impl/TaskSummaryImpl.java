package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;

/**
 * Entity which contains the most important informations about a Task.
 */
public class TaskSummaryImpl implements TaskSummary {

    private String taskId;
    private String externalId;
    private Instant created;
    private Instant claimed;
    private Instant completed;
    private Instant modified;
    private Instant planned;
    private Instant due;
    private String name;
    private String creator;
    private String note;
    private int priority;
    private TaskState state;
    private ClassificationSummary classificationSummary;
    private WorkbasketSummary workbasketSummary;
    private String businessProcessId;
    private String parentBusinessProcessId;
    private String owner;
    private ObjectReference primaryObjRef;
    private boolean isRead;
    private boolean isTransferred;
    // All objects have to be serializable
    private List<AttachmentSummary> attachmentSummaries = new ArrayList<>();
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;
    private String custom6;
    private String custom7;
    private String custom8;
    private String custom9;
    private String custom10;
    private String custom11;
    private String custom12;
    private String custom13;
    private String custom14;
    private String custom15;
    private String custom16;


    TaskSummaryImpl() {
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getTaskId()
     */
    @Override
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String id) {
        this.taskId = id;
    }

    /*
    * (non-Javadoc)
    * @see pro.taskana.TaskSummary#getExternalId()
    */
    @Override
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getCreated()
     */
    @Override
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getClaimed()
     */
    @Override
    public Instant getClaimed() {
        return claimed;
    }

    public void setClaimed(Instant claimed) {
        this.claimed = claimed;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getCompleted()
     */
    @Override
    public Instant getCompleted() {
        return completed;
    }

    public void setCompleted(Instant completed) {
        this.completed = completed;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getModified()
     */
    @Override
    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getPlanned()
     */
    @Override
    public Instant getPlanned() {
        return planned;
    }

    public void setPlanned(Instant planned) {
        this.planned = planned;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getDue()
     */
    @Override
    public Instant getDue() {
        return due;
    }

    public void setDue(Instant due) {
        this.due = due;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getCreator()
     */
    @Override
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getNote()
     */
    @Override
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getPriority()
     */
    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getState()
     */
    @Override
    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getClassificationSummary()
     */
    @Override
    public ClassificationSummary getClassificationSummary() {
        return classificationSummary;
    }

    public void setClassificationSummary(ClassificationSummary classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getWorkbasketSummary()
     */
    @Override
    public WorkbasketSummary getWorkbasketSummary() {
        return workbasketSummary;
    }

    public void setWorkbasketSummary(WorkbasketSummary workbasketSummary) {
        this.workbasketSummary = workbasketSummary;
    }

    // utility method to allow mybatis access to workbasketSummary
    public WorkbasketSummaryImpl getWorkbasketSummaryImpl() {
        return (WorkbasketSummaryImpl) workbasketSummary;
    }

    // utility method to allow mybatis access to workbasketSummary
    public void setWorkbasketSummaryImpl(WorkbasketSummaryImpl workbasketSummary) {
        setWorkbasketSummary(workbasketSummary);
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getDomain()
     */
    @Override
    public String getDomain() {
        return workbasketSummary == null ? null : workbasketSummary.getDomain();
    }

    public void setDomain(String domain) {
        if (workbasketSummary == null) {
            workbasketSummary = new WorkbasketSummaryImpl();
        }
        ((WorkbasketSummaryImpl) this.workbasketSummary).setDomain(domain);
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getBusinessProcessId()
     */
    @Override
    public String getBusinessProcessId() {
        return businessProcessId;
    }

    public void setBusinessProcessId(String businessProcessId) {
        this.businessProcessId = businessProcessId;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getParentBusinessProcessId()
     */
    @Override
    public String getParentBusinessProcessId() {
        return parentBusinessProcessId;
    }

    public void setParentBusinessProcessId(String parentBusinessProcessId) {
        this.parentBusinessProcessId = parentBusinessProcessId;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getOwner()
     */
    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getPrimaryObjRef()
     */
    @Override
    public ObjectReference getPrimaryObjRef() {
        return primaryObjRef;
    }

    public void setPrimaryObjRef(ObjectReference primaryObjRef) {
        this.primaryObjRef = primaryObjRef;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#isRead()
     */
    @Override
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#isTransferred()
     */
    @Override
    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean isTransferred) {
        this.isTransferred = isTransferred;
    }

    @Override
    public List<AttachmentSummary> getAttachmentSummaries() {
        if (attachmentSummaries == null) {
            attachmentSummaries = new ArrayList<>();
        }
        return attachmentSummaries;
    }

    public void setAttachmentSummaries(List<AttachmentSummary> attachmentSummaries) {
        this.attachmentSummaries = attachmentSummaries;
    }

    public void addAttachmentSummary(AttachmentSummary attachmentSummary) {
        if (this.attachmentSummaries == null) {
            this.attachmentSummaries = new ArrayList<>();
        }
        this.attachmentSummaries.add(attachmentSummary);
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.TaskSummary#getCustomAttribute(String number)
     */
    @Override
    public String getCustomAttribute(String number) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16",
                e.getCause());
        }

        switch (num) {
            case 1:
                return custom1;
            case 2:
                return custom2;
            case 3:
                return custom3;
            case 4:
                return custom4;
            case 5:
                return custom5;
            case 6:
                return custom6;
            case 7:
                return custom7;
            case 8:
                return custom8;
            case 9:
                return custom9;
            case 10:
                return custom10;
            case 11:
                return custom11;
            case 12:
                return custom12;
            case 13:
                return custom13;
            case 14:
                return custom14;
            case 15:
                return custom15;
            case 16:
                return custom16;
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
        }

    }

    // auxiliary Method to enable Mybatis to access classificationSummary
    public ClassificationSummaryImpl getClassificationSummaryImpl() {
        return (ClassificationSummaryImpl) classificationSummary;
    }

    // auxiliary Method to enable Mybatis to access classificationSummary
    public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
        setClassificationSummary(classificationSummary);
    }

    // auxiliary Method needed by Mybatis
    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom9(String custom9) {
        this.custom9 = custom9;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom10(String custom10) {
        this.custom10 = custom10;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom11(String custom11) {
        this.custom11 = custom11;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom12(String custom12) {
        this.custom12 = custom12;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom13(String custom13) {
        this.custom13 = custom13;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom14(String custom14) {
        this.custom14 = custom14;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom15(String custom15) {
        this.custom15 = custom15;
    }

    // auxiliary Method needed by Mybatis
    public void setCustom16(String custom16) {
        this.custom16 = custom16;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        Object[] myFields = {externalId, attachmentSummaries, businessProcessId, claimed, classificationSummary,
            completed, created, creator, custom1, custom10, custom11, custom12, custom13, custom14,
            custom15, custom16, custom2, custom3, custom4, custom5, custom6, custom7, custom8, custom9,
            due, modified, name, note, owner, parentBusinessProcessId, planned, primaryObjRef,
            state, taskId, workbasketSummary};

        for (Object property : myFields) {
            result = prime * result + (property == null ? 0 : property.hashCode());
        }
        result = prime * result + (isRead ? 1231 : 1237);
        result = prime * result + (isTransferred ? 1231 : 1237);
        result = prime * result + priority;
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
        TaskSummaryImpl other = (TaskSummaryImpl) obj;
        Object[] myFields = {externalId, attachmentSummaries, businessProcessId, claimed, classificationSummary,
            completed, created, creator, custom1, custom10, custom11, custom12, custom13, custom14,
            custom15, custom16, custom2, custom3, custom4, custom5, custom6, custom7, custom8, custom9,
            due, modified, name, note, owner, parentBusinessProcessId, planned, primaryObjRef,
            state, taskId, workbasketSummary};

        Object[] otherFields =  {other.externalId, other.attachmentSummaries, other.businessProcessId, other.claimed, other.classificationSummary,
            other.completed, other.created, other.creator, other.custom1, other.custom10, other.custom11, other.custom12,
            other.custom13, other.custom14, other.custom15, other.custom16, other.custom2, other.custom3, other.custom4,
            other.custom5, other.custom6, other.custom7, other.custom8, other.custom9, other.due, other.modified, other.name,
            other.note, other.owner, other.parentBusinessProcessId, other.planned, other.primaryObjRef, other.state,
            other.taskId, other.workbasketSummary};

        if (myFields.length != otherFields.length) {
            throw new SystemException("TaskSummaryImpl: length mismatch between internal arrays");
        }
        for (int i = 0; i < myFields.length; i++) {
            if ((myFields[i] == null && otherFields[i] != null)
                || (myFields[i] != null && !myFields[i].equals(otherFields[i]))) {
                return false;
            }
        }
        if (isRead != other.isRead) {
            return false;
        }
        if (isTransferred != other.isTransferred) {
            return false;
        }

        return (priority == other.priority);
    }

    @Override
    public String toString() {
        return "TaskSummaryImpl [taskId=" + taskId + ", externalId=" + externalId + ", created=" + created
            + ", claimed=" + claimed + ", completed=" + completed + ", modified=" + modified + ", planned=" + planned
            + ", due=" + due + ", name=" + name + ", creator=" + creator + ", note=" + note + ", priority=" + priority
            + ", state=" + state + ", classificationSummary=" + classificationSummary + ", workbasketSummary="
            + workbasketSummary + ", businessProcessId=" + businessProcessId + ", parentBusinessProcessId="
            + parentBusinessProcessId + ", owner=" + owner + ", primaryObjRef=" + primaryObjRef + ", isRead=" + isRead
            + ", isTransferred=" + isTransferred + ", attachmentSummaries=" + attachmentSummaries + ", custom1="
            + custom1 + ", custom2=" + custom2 + ", custom3=" + custom3 + ", custom4=" + custom4 + ", custom5="
            + custom5 + ", custom6=" + custom6 + ", custom7=" + custom7 + ", custom8=" + custom8 + ", custom9="
            + custom9 + ", custom10=" + custom10 + ", custom11=" + custom11 + ", custom12=" + custom12 + ", custom13="
            + custom13 + ", custom14=" + custom14 + ", custom15=" + custom15 + ", custom16=" + custom16 + "]";
    }

}
