package org.taskana.model;

import java.sql.Timestamp;

/**
 * Task entity
 */
public class Task {

	private String id;
	private String tenantId;
	private Timestamp created;
	private Timestamp claimed;
	private Timestamp completed;
	private Timestamp modified;
	private Timestamp planned;
	private Timestamp due;
	private String name;
	private String description;
	private int priority;
	private TaskState state;
	private String type;
	private String workbasketId;
	private String owner;
	private boolean isRead;
	private boolean isTransferred;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getClaimed() {
		return claimed;
	}

	public void setClaimed(Timestamp claimed) {
		this.claimed = claimed;
	}

	public Timestamp getCompleted() {
		return completed;
	}

	public void setCompleted(Timestamp completed) {
		this.completed = completed;
	}

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

	public Timestamp getPlanned() {
		return planned;
	}

	public void setPlanned(Timestamp planned) {
		this.planned = planned;
	}

	public Timestamp getDue() {
		return due;
	}

	public void setDue(Timestamp due) {
		this.due = due;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWorkbasketId() {
		return workbasketId;
	}

	public void setWorkbasketId(String workbasketId) {
		this.workbasketId = workbasketId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isTransferred() {
		return isTransferred;
	}

	public void setTransferred(boolean isTransferred) {
		this.isTransferred = isTransferred;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TASK(");
		sb.append("id="+id);
		sb.append(", tenantId="+tenantId);
		sb.append(", created="+created);
		sb.append(", claimed="+claimed);
		sb.append(", completed="+completed);
		sb.append(", modified="+modified);
		sb.append(", planned="+planned);
		sb.append(", due="+due);
		sb.append(", name="+name);
		sb.append(", description="+description);
		sb.append(", priority="+priority);
		sb.append(", state="+state);
		sb.append(", type="+type);
		sb.append(", workbasketId="+workbasketId);
		sb.append(", owner="+owner);
		sb.append(", isRead="+isRead);
		sb.append(", isTransferred="+isTransferred);
		sb.append(")");
		return sb.toString();

	}
}
