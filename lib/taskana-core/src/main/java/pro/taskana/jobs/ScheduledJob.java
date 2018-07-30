package pro.taskana.jobs;

import java.time.Instant;
import java.util.Map;

/**
 * This class holds all data that go into the Job table.
 *
 * @author bbr
 */
public class ScheduledJob {

    private Integer jobId;
    private Integer priority;
    private Instant created;
    private Instant due;
    private State state;
    private String lockedBy;
    private Instant lockExpires;
    private Type type;
    private int retryCount;
    Map<String, String> arguments;

    public ScheduledJob() {
        created = Instant.now();
        state = State.READY;
        retryCount = 0;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getDue() {
        return due;
    }

    public void setDue(Instant due) {
        this.due = due;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public Instant getLockExpires() {
        return lockExpires;
    }

    public void setLockExpires(Instant lockExpires) {
        this.lockExpires = lockExpires;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ScheduledJob [jobId=");
        builder.append(jobId);
        builder.append(", priority=");
        builder.append(priority);
        builder.append(", created=");
        builder.append(created);
        builder.append(", due=");
        builder.append(due);
        builder.append(", state=");
        builder.append(state);
        builder.append(", lockedBy=");
        builder.append(lockedBy);
        builder.append(", lockExpires=");
        builder.append(lockExpires);
        builder.append(", type=");
        builder.append(type);
        builder.append(", retryCount=");
        builder.append(retryCount);
        builder.append(", arguments=");
        builder.append(arguments);
        builder.append("]");
        return builder.toString();
    }

    /**
     * This enum tracks the state of a job.
     *
     * @author bbr
     */
    public enum State {
        READY,
        FAILED
    }

    /**
     * This enum controls the type of a job.
     */
    public enum Type {
        CLASSIFICATIONCHANGEDJOB,
        UPDATETASKSJOB,
        TASKCLEANUPJOB;
    }
}
