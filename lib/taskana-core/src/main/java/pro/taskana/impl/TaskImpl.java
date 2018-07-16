package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;
import pro.taskana.Task;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;

/**
 * Task entity.
 */
public class TaskImpl implements Task {

    private String id;
    private Instant created;
    private Instant claimed;
    private Instant completed;
    private Instant modified;
    private Instant planned;
    private Instant due;
    private String name;
    private String creator;
    private String description;
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
    private Map<String, String> customAttributes = Collections.emptyMap();
    private Map<String, String> callbackInfo = Collections.emptyMap();
    private List<Attachment> attachments = new ArrayList<>();
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

    TaskImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getClaimed() {
        return claimed;
    }

    public void setClaimed(Instant claimed) {
        this.claimed = claimed;
    }

    @Override
    public Instant getCompleted() {
        return completed;
    }

    public void setCompleted(Instant completed) {
        this.completed = completed;
    }

    @Override
    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    @Override
    public Instant getPlanned() {
        return planned;
    }

    @Override
    public void setPlanned(Instant planned) {
        this.planned = planned;
    }

    @Override
    public Instant getDue() {
        return due;
    }

    public void setDue(Instant due) {
        this.due = due;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    @Override
    public ClassificationSummary getClassificationSummary() {
        return classificationSummary;
    }

    @Override
    public void setClassificationKey(String classificationKey) {
        if (this.classificationSummary == null) {
            this.classificationSummary = new ClassificationSummaryImpl();
        }

        ((ClassificationSummaryImpl) this.classificationSummary).setKey(classificationKey);
    }

    public void setClassificationCategory(String classificationCategory) {
        if (this.classificationSummary == null) {
            this.classificationSummary = new ClassificationSummaryImpl();
        }
        ((ClassificationSummaryImpl) this.classificationSummary).setCategory(classificationCategory);
    }

    @Override
    public String getClassificationCategory() {
        return this.classificationSummary == null ? null : this.classificationSummary.getCategory();
    }

    @Override
    public String getWorkbasketKey() {
        return workbasketSummary == null ? null : workbasketSummary.getKey();
    }

    public void setWorkbasketKey(String workbasketKey) {
        if (workbasketSummary == null) {
            workbasketSummary = new WorkbasketSummaryImpl();
        }
        ((WorkbasketSummaryImpl) this.workbasketSummary).setKey(workbasketKey);
    }

    @Override
    public WorkbasketSummary getWorkbasketSummary() {
        return workbasketSummary;
    }

    public void setWorkbasketSummary(WorkbasketSummary workbasket) {
        this.workbasketSummary = workbasket;
    }

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

    @Override
    public String getBusinessProcessId() {
        return businessProcessId;
    }

    @Override
    public void setBusinessProcessId(String businessProcessId) {
        this.businessProcessId = businessProcessId;
    }

    @Override
    public String getParentBusinessProcessId() {
        return parentBusinessProcessId;
    }

    @Override
    public void setParentBusinessProcessId(String parentBusinessProcessId) {
        this.parentBusinessProcessId = parentBusinessProcessId;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public ObjectReference getPrimaryObjRef() {
        return primaryObjRef;
    }

    @Override
    public void setPrimaryObjRef(ObjectReference primaryObjRef) {
        this.primaryObjRef = primaryObjRef;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean isTransferred) {
        this.isTransferred = isTransferred;
    }

    @Override
    public Map<String, String> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new HashMap<>();
        }
        return customAttributes;
    }

    @Override
    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public Map<String, String> getCallbackInfo() {
        if (callbackInfo == null) {
            callbackInfo = new HashMap<>();
        }
        return callbackInfo;
    }

    @Override
    public void setCallbackInfo(Map<String, String> callbackInfo) {
        this.callbackInfo = callbackInfo;
    }

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

    @Override
    public void setCustomAttribute(String number, String value) throws InvalidArgumentException {
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
                custom1 = value;
                break;
            case 2:
                custom2 = value;
                break;
            case 3:
                custom3 = value;
                break;
            case 4:
                custom4 = value;
                break;
            case 5:
                custom5 = value;
                break;
            case 6:
                custom6 = value;
                break;
            case 7:
                custom7 = value;
                break;
            case 8:
                custom8 = value;
                break;
            case 9:
                custom9 = value;
                break;
            case 10:
                custom10 = value;
                break;
            case 11:
                custom11 = value;
                break;
            case 12:
                custom12 = value;
                break;
            case 13:
                custom13 = value;
                break;
            case 14:
                custom14 = value;
                break;
            case 15:
                custom15 = value;
                break;
            case 16:
                custom16 = value;
                break;
            default:
                throw new InvalidArgumentException(
                    "Argument '" + number + "' to getCustomAttribute does not represent a number between 1 and 16");
        }

    }

    @Override
    public void addAttachment(Attachment attachmentToAdd) {
        if (attachments == null) {
            attachments = new ArrayList<Attachment>();
        }
        if (attachmentToAdd != null) {
            if (attachmentToAdd.getId() != null) {
                Iterator<Attachment> i = attachments.iterator();
                while (i.hasNext()) {
                    Attachment attachment = i.next();
                    if (attachmentToAdd.getId().equals(attachment.getId())) {
                        i.remove();
                    }
                }
            }
            attachments.add(attachmentToAdd);
        }
    }

    @Override
    public List<Attachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    @Override
    public TaskSummary asSummary() {
        TaskSummaryImpl taskSummary = new TaskSummaryImpl();
        List<AttachmentSummary> attSummaries = new ArrayList<>();
        for (Attachment att : attachments) {
            attSummaries.add(att.asSummary());
        }
        taskSummary.setAttachmentSummaries(attSummaries);
        taskSummary.setBusinessProcessId(this.businessProcessId);
        taskSummary.setClaimed(claimed);
        if (classificationSummary != null) {
            taskSummary.setClassificationSummary(classificationSummary);
        }
        taskSummary.setCompleted(completed);
        taskSummary.setCreated(created);
        taskSummary.setCustom1(custom1);
        taskSummary.setCustom2(custom2);
        taskSummary.setCustom3(custom3);
        taskSummary.setCustom4(custom4);
        taskSummary.setCustom5(custom5);
        taskSummary.setCustom6(custom6);
        taskSummary.setCustom7(custom7);
        taskSummary.setCustom8(custom8);
        taskSummary.setCustom9(custom9);
        taskSummary.setCustom10(custom10);
        taskSummary.setCustom11(custom11);
        taskSummary.setCustom12(custom12);
        taskSummary.setCustom13(custom13);
        taskSummary.setCustom14(custom14);
        taskSummary.setCustom15(custom15);
        taskSummary.setCustom16(custom16);
        taskSummary.setDue(due);
        taskSummary.setTaskId(id);
        taskSummary.setModified(modified);
        taskSummary.setName(name);
        taskSummary.setCreator(creator);
        taskSummary.setNote(note);
        taskSummary.setOwner(owner);
        taskSummary.setParentBusinessProcessId(parentBusinessProcessId);
        taskSummary.setPlanned(planned);
        taskSummary.setPrimaryObjRef(primaryObjRef);
        taskSummary.setPriority(priority);
        taskSummary.setRead(isRead);
        taskSummary.setState(state);
        taskSummary.setTransferred(isTransferred);
        taskSummary.setWorkbasketSummary(workbasketSummary);
        return taskSummary;

    }

    public void setAttachments(List<Attachment> attachments) {
        if (attachments != null) {
            this.attachments = attachments;
        } else if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
    }

    public String getClassificationKey() {
        return classificationSummary == null ? null : classificationSummary.getKey();
    }

    public void setClassificationSummary(ClassificationSummary classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    public ClassificationSummaryImpl getClassificationSummaryImpl() {
        return (ClassificationSummaryImpl) classificationSummary;
    }

    public WorkbasketSummaryImpl getWorkbasketSummaryImpl() {
        return (WorkbasketSummaryImpl) workbasketSummary;
    }

    public void setWorkbasketSummaryImpl(WorkbasketSummaryImpl workbasketSummary) {
        this.workbasketSummary = workbasketSummary;
    }

    public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public String getCustom5() {
        return custom5;
    }

    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    public String getCustom6() {
        return custom6;
    }

    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    public String getCustom7() {
        return custom7;
    }

    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    public String getCustom8() {
        return custom8;
    }

    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    public String getCustom9() {
        return custom9;
    }

    public void setCustom9(String custom9) {
        this.custom9 = custom9;
    }

    public String getCustom10() {
        return custom10;
    }

    public void setCustom10(String custom10) {
        this.custom10 = custom10;
    }

    public String getCustom11() {
        return custom11;
    }

    public void setCustom11(String custom11) {
        this.custom11 = custom11;
    }

    public String getCustom12() {
        return custom12;
    }

    public void setCustom12(String custom12) {
        this.custom12 = custom12;
    }

    public String getCustom13() {
        return custom13;
    }

    public void setCustom13(String custom13) {
        this.custom13 = custom13;
    }

    public String getCustom14() {
        return custom14;
    }

    public void setCustom14(String custom14) {
        this.custom14 = custom14;
    }

    public String getCustom15() {
        return custom15;
    }

    public void setCustom15(String custom15) {
        this.custom15 = custom15;
    }

    public String getCustom16() {
        return custom16;
    }

    public void setCustom16(String custom16) {
        this.custom16 = custom16;
    }

    @Override
    public Attachment removeAttachment(String attachmentId) {
        Attachment result = null;
        Iterator<Attachment> i = attachments.iterator();
        while (i.hasNext()) {
            Attachment attachment = i.next();
            if (attachment.getId().equals(attachmentId)) {
                if (attachments.remove(attachment)) {
                    result = attachment;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskImpl [id=");
        builder.append(id);
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
        builder.append(", creator=");
        builder.append(creator);
        builder.append(", description=");
        builder.append(description);
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
        builder.append(", customAttributes=");
        builder.append(customAttributes);
        builder.append(", callbackInfo=");
        builder.append(callbackInfo);
        builder.append(", attachments=");
        builder.append(attachments);
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
        builder.append(", custom11=");
        builder.append(custom11);
        builder.append(", custom12=");
        builder.append(custom12);
        builder.append(", custom13=");
        builder.append(custom13);
        builder.append(", custom14=");
        builder.append(custom14);
        builder.append(", custom15=");
        builder.append(custom15);
        builder.append(", custom16=");
        builder.append(custom16);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attachments == null) ? 0 : attachments.hashCode());
        result = prime * result + ((businessProcessId == null) ? 0 : businessProcessId.hashCode());
        result = prime * result + ((claimed == null) ? 0 : claimed.hashCode());
        result = prime * result + ((classificationSummary == null) ? 0 : classificationSummary.hashCode());
        result = prime * result + ((completed == null) ? 0 : completed.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((custom1 == null) ? 0 : custom1.hashCode());
        result = prime * result + ((custom10 == null) ? 0 : custom10.hashCode());
        result = prime * result + ((custom11 == null) ? 0 : custom11.hashCode());
        result = prime * result + ((custom12 == null) ? 0 : custom12.hashCode());
        result = prime * result + ((custom13 == null) ? 0 : custom13.hashCode());
        result = prime * result + ((custom14 == null) ? 0 : custom14.hashCode());
        result = prime * result + ((custom15 == null) ? 0 : custom15.hashCode());
        result = prime * result + ((custom16 == null) ? 0 : custom16.hashCode());
        result = prime * result + ((custom2 == null) ? 0 : custom2.hashCode());
        result = prime * result + ((custom3 == null) ? 0 : custom3.hashCode());
        result = prime * result + ((custom4 == null) ? 0 : custom4.hashCode());
        result = prime * result + ((custom5 == null) ? 0 : custom5.hashCode());
        result = prime * result + ((custom6 == null) ? 0 : custom6.hashCode());
        result = prime * result + ((custom7 == null) ? 0 : custom7.hashCode());
        result = prime * result + ((custom8 == null) ? 0 : custom8.hashCode());
        result = prime * result + ((custom9 == null) ? 0 : custom9.hashCode());
        result = prime * result + ((customAttributes == null) ? 0 : customAttributes.hashCode());
        result = prime * result + ((callbackInfo == null) ? 0 : callbackInfo.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((due == null) ? 0 : due.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        TaskImpl other = (TaskImpl) obj;
        if (attachments == null) {
            if (other.attachments != null) {
                return false;
            }
        } else if (!attachments.equals(other.attachments)) {
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
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
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
        if (custom11 == null) {
            if (other.custom11 != null) {
                return false;
            }
        } else if (!custom11.equals(other.custom11)) {
            return false;
        }
        if (custom12 == null) {
            if (other.custom12 != null) {
                return false;
            }
        } else if (!custom12.equals(other.custom12)) {
            return false;
        }
        if (custom13 == null) {
            if (other.custom13 != null) {
                return false;
            }
        } else if (!custom13.equals(other.custom13)) {
            return false;
        }
        if (custom14 == null) {
            if (other.custom14 != null) {
                return false;
            }
        } else if (!custom14.equals(other.custom14)) {
            return false;
        }
        if (custom15 == null) {
            if (other.custom15 != null) {
                return false;
            }
        } else if (!custom15.equals(other.custom15)) {
            return false;
        }
        if (custom16 == null) {
            if (other.custom16 != null) {
                return false;
            }
        } else if (!custom16.equals(other.custom16)) {
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
        if (customAttributes == null) {
            if (other.customAttributes != null) {
                return false;
            }
        } else if (!customAttributes.equals(other.customAttributes)) {
            return false;
        }
        if (callbackInfo == null) {
            if (other.callbackInfo != null) {
                return false;
            }
        } else if (!callbackInfo.equals(other.callbackInfo)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (due == null) {
            if (other.due != null) {
                return false;
            }
        } else if (!due.equals(other.due)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
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

}
