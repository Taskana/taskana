package pro.taskana.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.Classification;
import pro.taskana.Task;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;

/**
 * Task entity.
 */
public class TaskImpl implements Task {

    private String id;
    private Timestamp created;
    private Timestamp claimed;
    private Timestamp completed;
    private Timestamp modified;
    private Timestamp planned;
    private Timestamp due;
    private String name;
    private String description;
    private String note;
    private int priority;
    private TaskState state;
    private String classificationKey;
    private Classification classification;
    private String workbasketKey;
    private String domain;
    private String businessProcessId;
    private String parentBusinessProcessId;
    private String owner;
    private ObjectReference primaryObjRef;
    private boolean isRead;
    private boolean isTransferred;
    // All objects have to be serializable
    private Map<String, Object> customAttributes = Collections.emptyMap();
    private String porCompany;          // auxiliary field needed to avoid 2nd query for primaryObjRef in TaskMapper
    private String porSystem;           // auxiliary field needed to avoid 2nd query for primaryObjRef in TaskMapper
    private String porSystemInstance;   // auxiliary field needed to avoid 2nd query for primaryObjRef in TaskMapper
    private String porType;             // auxiliary field needed to avoid 2nd query for primaryObjRef in TaskMapper
    private String porValue;            // auxiliary field needed to avoid 2nd query for primaryObjRef in TaskMapper
    private List<Attachment> attachments;
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
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public Timestamp getClaimed() {
        return claimed;
    }

    public void setClaimed(Timestamp claimed) {
        this.claimed = claimed;
    }

    @Override
    public Timestamp getCompleted() {
        return completed;
    }

    public void setCompleted(Timestamp completed) {
        this.completed = completed;
    }

    @Override
    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Override
    public Timestamp getPlanned() {
        return planned;
    }

    @Override
    public void setPlanned(Timestamp planned) {
        this.planned = planned;
    }

    @Override
    public Timestamp getDue() {
        return due;
    }

    public void setDue(Timestamp due) {
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
    public Classification getClassification() {
        return classification;
    }

    @Override
    public void setClassificationKey(String classificationKey) {
        this.classificationKey = classificationKey;
    }

    @Override
    public String getWorkbasketKey() {
        return workbasketKey;
    }

    @Override
    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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
    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public String getCustom1() {
        return custom1;
    }

    @Override
    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    @Override
    public String getCustom2() {
        return custom2;
    }

    @Override
    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    @Override
    public String getCustom3() {
        return custom3;
    }

    @Override
    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    @Override
    public String getCustom4() {
        return custom4;
    }

    @Override
    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    @Override
    public String getCustom5() {
        return custom5;
    }

    @Override
    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    @Override
    public String getCustom6() {
        return custom6;
    }

    @Override
    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    @Override
    public String getCustom7() {
        return custom7;
    }

    @Override
    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    @Override
    public String getCustom8() {
        return custom8;
    }

    @Override
    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    @Override
    public String getCustom9() {
        return custom9;
    }

    @Override
    public void setCustom9(String custom9) {
        this.custom9 = custom9;
    }

    @Override
    public String getCustom10() {
        return custom10;
    }

    @Override
    public void setCustom10(String custom10) {
        this.custom10 = custom10;
    }

    @Override
    public void addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<Attachment>();
        }
        attachments.add(attachment);
    }

    @Override
    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getClassificationKey() {
        return classificationKey;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
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

    public String getPorSystemInstance() {
        return porSystemInstance;
    }

    public void setPorSystemInstance(String porSystemInstance) {
        this.porSystemInstance = porSystemInstance;
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
        builder.append(", description=");
        builder.append(description);
        builder.append(", note=");
        builder.append(note);
        builder.append(", priority=");
        builder.append(priority);
        builder.append(", state=");
        builder.append(state);
        builder.append(", classificationKey=");
        builder.append(classificationKey);
        builder.append(", classification=");
        builder.append(classification);
        builder.append(", workbasketKey=");
        builder.append(workbasketKey);
        builder.append(", domain=");
        builder.append(domain);
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
        builder.append(", porCompany=");
        builder.append(porCompany);
        builder.append(", porSystem=");
        builder.append(porSystem);
        builder.append(", porSystemInstance=");
        builder.append(porSystemInstance);
        builder.append(", porType=");
        builder.append(porType);
        builder.append(", porValue=");
        builder.append(porValue);
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
