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
import pro.taskana.exceptions.SystemException;

/**
 * Task entity.
 */
public class TaskImpl implements Task {

    private String id;
    private String externalId;
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

    private static final String ARGUMENT_STR = "Argument '";
    private static final String NOT_A_VALID_NUMBER_GET = "' of getCustomAttribute() cannot be converted to a number between 1 and 16";
    private static final String NOT_A_VALID_NUMBER_SET = "' of setCustomAttribute() cannot be converted to a number between 1 and 16";

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
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
                ARGUMENT_STR + number + NOT_A_VALID_NUMBER_GET,
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
                    ARGUMENT_STR + number + NOT_A_VALID_NUMBER_GET);
        }

    }

    @Override
    public void setCustomAttribute(String number, String value) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                ARGUMENT_STR + number + NOT_A_VALID_NUMBER_SET,
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
                    ARGUMENT_STR + number + NOT_A_VALID_NUMBER_SET);
        }

    }

    @Override
    public void addAttachment(Attachment attachmentToAdd) {
        if (attachments == null) {
            attachments = new ArrayList<>();
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
        taskSummary.setExternalId(externalId);
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
        setClassificationSummary(classificationSummary);
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
            if (attachment.getId().equals(attachmentId)
                && attachments.remove(attachment)) {
                    result = attachment;
                    break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "TaskImpl [id=" + id + ", externalId=" + externalId + ", created=" + created + ", claimed=" + claimed
            + ", completed=" + completed + ", modified=" + modified + ", planned=" + planned + ", due=" + due
            + ", name=" + name + ", creator=" + creator + ", description=" + description + ", note=" + note
            + ", priority=" + priority + ", state=" + state + ", classificationSummary=" + classificationSummary
            + ", workbasketSummary=" + workbasketSummary + ", businessProcessId=" + businessProcessId
            + ", parentBusinessProcessId=" + parentBusinessProcessId + ", owner=" + owner + ", primaryObjRef="
            + primaryObjRef + ", isRead=" + isRead + ", isTransferred=" + isTransferred + ", customAttributes="
            + customAttributes + ", callbackInfo=" + callbackInfo + ", attachments=" + attachments + ", custom1="
            + custom1 + ", custom2=" + custom2 + ", custom3=" + custom3 + ", custom4=" + custom4 + ", custom5="
            + custom5 + ", custom6=" + custom6 + ", custom7=" + custom7 + ", custom8=" + custom8 + ", custom9="
            + custom9 + ", custom10=" + custom10 + ", custom11=" + custom11 + ", custom12=" + custom12 + ", custom13="
            + custom13 + ", custom14=" + custom14 + ", custom15=" + custom15 + ", custom16=" + custom16 + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        Object[] myFields = {externalId, attachments, businessProcessId, claimed, classificationSummary, completed, created,
            creator, custom1, custom10, custom11, custom12, custom13, custom14, custom15, custom16, custom2,
            custom3, custom4, custom5, custom6, custom7, custom8, custom9, customAttributes, callbackInfo,
            description, due, id, modified, name, note, owner, parentBusinessProcessId, planned, primaryObjRef, state,
            workbasketSummary};

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
        TaskImpl other = (TaskImpl) obj;

        Object[] myFields = {externalId, attachments, businessProcessId, claimed, classificationSummary, completed, created,
            creator, custom1, custom10, custom11, custom12, custom13, custom14, custom15, custom16, custom2,
            custom3, custom4, custom5, custom6, custom7, custom8, custom9, customAttributes, callbackInfo,
            description, due, id, modified, name, note, owner, parentBusinessProcessId, planned, primaryObjRef, state,
            workbasketSummary};

            Object[] otherFields = {other.externalId, other.attachments, other.businessProcessId, other.claimed, other.classificationSummary, other.completed,
            other.created, other.creator, other.custom1, other.custom10, other.custom11, other.custom12, other.custom13, other.custom14,
            other.custom15, other.custom16, other.custom2, other.custom3, other.custom4, other.custom5, other.custom6, other.custom7,
            other.custom8, other.custom9, other.customAttributes, other.callbackInfo, other.description, other.due, other.id, other.modified,
            other.name, other.note, other.owner, other.parentBusinessProcessId, other.planned, other.primaryObjRef, other.state, other.workbasketSummary};

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

}
