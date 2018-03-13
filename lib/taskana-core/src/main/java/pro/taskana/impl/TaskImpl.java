package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
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

    public void setBusinessProcessId(String businessProcessId) {
        this.businessProcessId = businessProcessId;
    }

    @Override
    public String getParentBusinessProcessId() {
        return parentBusinessProcessId;
    }

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
        return customAttributes;
    }

    @Override
    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public String getCustomAttribute(String number) throws InvalidArgumentException {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16");
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
                return custom10;
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
                "Argument '" + number + "' to getCustomAttribute cannot be converted to a number between 1 and 16");
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
        builder.append("]");
        return builder.toString();
    }
}
