package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskSummary;
import pro.taskana.WorkbasketSummary;

/**
 * Entity which contains the most important informations about a Task.
 */
public class TaskSummaryImpl implements TaskSummary {

    private String taskId;
    private Instant created;
    private Instant claimed;
    private Instant completed;
    private Instant modified;
    private Instant planned;
    private Instant due;
    private String name;
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
    private List<AttachmentSummary> attachmentSummaries;
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

    TaskSummaryImpl() {

    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getId()
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
     * @see pro.taskana.impl.TaskSummary#getCreated()
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
     * @see pro.taskana.impl.TaskSummary#getClaimed()
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
     * @see pro.taskana.impl.TaskSummary#getCompleted()
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
     * @see pro.taskana.impl.TaskSummary#getModified()
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
     * @see pro.taskana.impl.TaskSummary#getPlanned()
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
     * @see pro.taskana.impl.TaskSummary#getDue()
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
     * @see pro.taskana.impl.TaskSummary#getName()
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
     * @see pro.taskana.impl.TaskSummary#getNote()
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
     * @see pro.taskana.impl.TaskSummary#getPriority()
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
     * @see pro.taskana.impl.TaskSummary#getState()
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
     * @see pro.taskana.impl.TaskSummary#getClassificationSummary()
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
     * @see pro.taskana.impl.TaskSummary#getWorkbasketSummary()
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
        this.workbasketSummary = workbasketSummary;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getDomain()
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
     * @see pro.taskana.impl.TaskSummary#getBusinessProcessId()
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
     * @see pro.taskana.impl.TaskSummary#getParentBusinessProcessId()
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
     * @see pro.taskana.impl.TaskSummary#getOwner()
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
     * @see pro.taskana.impl.TaskSummary#getPrimaryObjRef()
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
     * @see pro.taskana.impl.TaskSummary#isRead()
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
     * @see pro.taskana.impl.TaskSummary#isTransferred()
     */
    @Override
    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean isTransferred) {
        this.isTransferred = isTransferred;
    }

    public List<AttachmentSummary> getAttachmentSummaries() {
        return attachmentSummaries;
    }

    public void setAttachmentSummaries(List<AttachmentSummary> attachmentSummaries) {
        this.attachmentSummaries = attachmentSummaries;
    }

    public void addAttachmentSummary(AttachmentSummary attachmentSummary) {
        if (this.attachmentSummaries == null) {
            this.attachmentSummaries = new ArrayList<AttachmentSummary>();
        }
        this.attachmentSummaries.add(attachmentSummary);
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom1()
     */
    @Override
    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom2()
     */
    @Override
    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom3()
     */
    @Override
    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom4()
     */
    @Override
    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom5()
     */
    @Override
    public String getCustom5() {
        return custom5;
    }

    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom6()
     */
    @Override
    public String getCustom6() {
        return custom6;
    }

    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom7()
     */
    @Override
    public String getCustom7() {
        return custom7;
    }

    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom8()
     */
    @Override
    public String getCustom8() {
        return custom8;
    }

    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom9()
     */
    @Override
    public String getCustom9() {
        return custom9;
    }

    public void setCustom9(String custom9) {
        this.custom9 = custom9;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.TaskSummary#getCustom10()
     */
    @Override
    public String getCustom10() {
        return custom10;
    }

    public void setCustom10(String custom10) {
        this.custom10 = custom10;
    }

    // auxiliary Method to enable Mybatis to access classificationSummary
    public ClassificationSummaryImpl getClassificationSummaryImpl() {
        return (ClassificationSummaryImpl) classificationSummary;
    }

    // auxiliary Method to enable Mybatis to access classificationSummary
    public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attachmentSummaries == null) ? 0 : attachmentSummaries.hashCode());
        result = prime * result + ((businessProcessId == null) ? 0 : businessProcessId.hashCode());
        result = prime * result + ((claimed == null) ? 0 : claimed.hashCode());
        result = prime * result + ((classificationSummary == null) ? 0 : classificationSummary.hashCode());
        result = prime * result + ((completed == null) ? 0 : completed.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((custom1 == null) ? 0 : custom1.hashCode());
        result = prime * result + ((custom10 == null) ? 0 : custom10.hashCode());
        result = prime * result + ((custom2 == null) ? 0 : custom2.hashCode());
        result = prime * result + ((custom3 == null) ? 0 : custom3.hashCode());
        result = prime * result + ((custom4 == null) ? 0 : custom4.hashCode());
        result = prime * result + ((custom5 == null) ? 0 : custom5.hashCode());
        result = prime * result + ((custom6 == null) ? 0 : custom6.hashCode());
        result = prime * result + ((custom7 == null) ? 0 : custom7.hashCode());
        result = prime * result + ((custom8 == null) ? 0 : custom8.hashCode());
        result = prime * result + ((custom9 == null) ? 0 : custom9.hashCode());
        result = prime * result + ((due == null) ? 0 : due.hashCode());
        result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
        result = prime * result + (isRead ? 1231 : 1237);
        result = prime * result + (isTransferred ? 1231 : 1237);
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((note == null) ? 0 : note.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((parentBusinessProcessId == null) ? 0 : parentBusinessProcessId.hashCode());
        result = prime * result + ((planned == null) ? 0 : planned.hashCode());
        result = prime * result + ((primaryObjRef == null) ? 0 : primaryObjRef.hashCode());
        result = prime * result + priority;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((workbasketSummary == null) ? 0 : workbasketSummary.hashCode());
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
        if (!(obj instanceof TaskSummaryImpl)) {
            return false;
        }
        TaskSummaryImpl other = (TaskSummaryImpl) obj;
        if (attachmentSummaries == null) {
            if (other.attachmentSummaries != null) {
                return false;
            }
        } else if (!attachmentSummaries.equals(other.attachmentSummaries)) {
            return false;
        }
        if (businessProcessId == null) {
            if (other.businessProcessId != null) {
                return false;
            }
        } else if (!businessProcessId.equals(other.businessProcessId)) {
            return false;
        }
        if (claimed == null) {
            if (other.claimed != null) {
                return false;
            }
        } else if (!claimed.equals(other.claimed)) {
            return false;
        }
        if (classificationSummary == null) {
            if (other.classificationSummary != null) {
                return false;
            }
        } else if (!classificationSummary.equals(other.classificationSummary)) {
            return false;
        }
        if (completed == null) {
            if (other.completed != null) {
                return false;
            }
        } else if (!completed.equals(other.completed)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (custom1 == null) {
            if (other.custom1 != null) {
                return false;
            }
        } else if (!custom1.equals(other.custom1)) {
            return false;
        }
        if (custom10 == null) {
            if (other.custom10 != null) {
                return false;
            }
        } else if (!custom10.equals(other.custom10)) {
            return false;
        }
        if (custom2 == null) {
            if (other.custom2 != null) {
                return false;
            }
        } else if (!custom2.equals(other.custom2)) {
            return false;
        }
        if (custom3 == null) {
            if (other.custom3 != null) {
                return false;
            }
        } else if (!custom3.equals(other.custom3)) {
            return false;
        }
        if (custom4 == null) {
            if (other.custom4 != null) {
                return false;
            }
        } else if (!custom4.equals(other.custom4)) {
            return false;
        }
        if (custom5 == null) {
            if (other.custom5 != null) {
                return false;
            }
        } else if (!custom5.equals(other.custom5)) {
            return false;
        }
        if (custom6 == null) {
            if (other.custom6 != null) {
                return false;
            }
        } else if (!custom6.equals(other.custom6)) {
            return false;
        }
        if (custom7 == null) {
            if (other.custom7 != null) {
                return false;
            }
        } else if (!custom7.equals(other.custom7)) {
            return false;
        }
        if (custom8 == null) {
            if (other.custom8 != null) {
                return false;
            }
        } else if (!custom8.equals(other.custom8)) {
            return false;
        }
        if (custom9 == null) {
            if (other.custom9 != null) {
                return false;
            }
        } else if (!custom9.equals(other.custom9)) {
            return false;
        }
        if (due == null) {
            if (other.due != null) {
                return false;
            }
        } else if (!due.equals(other.due)) {
            return false;
        }
        if (taskId == null) {
            if (other.taskId != null) {
                return false;
            }
        } else if (!taskId.equals(other.taskId)) {
            return false;
        }
        if (isRead != other.isRead) {
            return false;
        }
        if (isTransferred != other.isTransferred) {
            return false;
        }
        if (modified == null) {
            if (other.modified != null) {
                return false;
            }
        } else if (!modified.equals(other.modified)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (note == null) {
            if (other.note != null) {
                return false;
            }
        } else if (!note.equals(other.note)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (parentBusinessProcessId == null) {
            if (other.parentBusinessProcessId != null) {
                return false;
            }
        } else if (!parentBusinessProcessId.equals(other.parentBusinessProcessId)) {
            return false;
        }
        if (planned == null) {
            if (other.planned != null) {
                return false;
            }
        } else if (!planned.equals(other.planned)) {
            return false;
        }
        if (primaryObjRef == null) {
            if (other.primaryObjRef != null) {
                return false;
            }
        } else if (!primaryObjRef.equals(other.primaryObjRef)) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        if (state != other.state) {
            return false;
        }
        if (workbasketSummary == null) {
            if (other.workbasketSummary != null) {
                return false;
            }
        } else if (!workbasketSummary.equals(other.workbasketSummary)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskSummaryImpl [taskId=");
        builder.append(taskId);
        builder.append(", created=");
        builder.append(created);
        builder.append(", claimed=");
        builder.append(claimed);
        builder.append(", completed=");
        builder.append(completed);
        builder.append(", modified=");
        builder.append(modified);
        builder.append(", planned=");
        builder.append(planned);
        builder.append(", due=");
        builder.append(due);
        builder.append(", name=");
        builder.append(name);
        builder.append(", note=");
        builder.append(note);
        builder.append(", priority=");
        builder.append(priority);
        builder.append(", state=");
        builder.append(state);
        builder.append(", classificationSummary=");
        builder.append(classificationSummary);
        builder.append(", workbasketSummary=");
        builder.append(workbasketSummary);
        builder.append(", businessProcessId=");
        builder.append(businessProcessId);
        builder.append(", parentBusinessProcessId=");
        builder.append(parentBusinessProcessId);
        builder.append(", owner=");
        builder.append(owner);
        builder.append(", primaryObjRef=");
        builder.append(primaryObjRef);
        builder.append(", isRead=");
        builder.append(isRead);
        builder.append(", isTransferred=");
        builder.append(isTransferred);
        builder.append(", attachmentSummaries=");
        builder.append(attachmentSummaries);
        builder.append(", custom1=");
        builder.append(custom1);
        builder.append(", custom2=");
        builder.append(custom2);
        builder.append(", custom3=");
        builder.append(custom3);
        builder.append(", custom4=");
        builder.append(custom4);
        builder.append(", custom5=");
        builder.append(custom5);
        builder.append(", custom6=");
        builder.append(custom6);
        builder.append(", custom7=");
        builder.append(custom7);
        builder.append(", custom8=");
        builder.append(custom8);
        builder.append(", custom9=");
        builder.append(custom9);
        builder.append(", custom10=");
        builder.append(custom10);
        builder.append("]");
        return builder.toString();
    }
}
