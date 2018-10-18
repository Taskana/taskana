package pro.taskana.history.api;

import java.time.Instant;

import pro.taskana.security.CurrentUserContext;

/**
 * Super class for all specific events from the TASKANA engine.
 */
public class TaskanaHistoryEvent {

    protected String id;
    protected String type;
    protected String userId;
    protected Instant created;
    protected String comment;

    public TaskanaHistoryEvent() {
        userId = CurrentUserContext.getUserid();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
