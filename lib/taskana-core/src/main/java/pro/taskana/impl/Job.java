package pro.taskana.impl;

import java.time.Instant;
import java.util.Map;

/**
 * This class holds all data that go into the Job table.
 *
 * @author bbr
 */
public class Job {

    private Integer jobId;
    private Instant created;
    private Instant started;
    private Instant completed;
    private State state;
    private Type type;
    private int retryCount;
    private String executor;
    private String errors;
    Map<String, String> arguments;

    public Job() {
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

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getStarted() {
        return started;
    }

    public void setStarted(Instant started) {
        this.started = started;
    }

    public Instant getCompleted() {
        return completed;
    }

    public void setCompleted(Instant completed) {
        this.completed = completed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
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

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
        if (this.errors != null && this.errors.length() > 4096) {
            this.errors = errors.substring(0, 4095);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Job [jobId=");
        builder.append(jobId);
        builder.append(", created=");
        builder.append(created);
        builder.append(", started=");
        builder.append(started);
        builder.append(", completed=");
        builder.append(completed);
        builder.append(", state=");
        builder.append(state);
        builder.append(", type=");
        builder.append(type);
        builder.append(", retryCount=");
        builder.append(retryCount);
        builder.append(", executor=");
        builder.append(executor);
        builder.append(", arguments=");
        builder.append(arguments);
        builder.append(", errors=");
        builder.append(errors);
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
        RUNNING,
        FAILED,
        COMPLETED
    }

    /**
     * This enum controls the type of a job.
     */
    public enum Type {
        CLASSIFICATIONCHANGEDJOB,
        UPDATETASKSJOB;
    }
}
