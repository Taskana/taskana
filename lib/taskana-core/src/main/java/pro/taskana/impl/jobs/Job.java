package pro.taskana.impl.jobs;

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
    private String executor;
    Map<String, String> arguments;

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
        builder.append(", executor=");
        builder.append(executor);
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
        RUNNING,
        FAILED,
        COMPLETED
    }

}
